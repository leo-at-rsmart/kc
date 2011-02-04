/*
 * Copyright 2005-2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.award.budget;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.award.budget.document.AwardBudgetDocument;
import org.kuali.kra.award.budget.document.AwardBudgetDocumentVersion;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardAmountInfo;
import org.kuali.kra.budget.BudgetDecimal;
import org.kuali.kra.budget.calculator.QueryList;
import org.kuali.kra.budget.calculator.query.And;
import org.kuali.kra.budget.calculator.query.Equals;
import org.kuali.kra.budget.core.Budget;
import org.kuali.kra.budget.core.BudgetParent;
import org.kuali.kra.budget.core.BudgetService;
import org.kuali.kra.budget.document.BudgetDocument;
import org.kuali.kra.budget.document.BudgetParentDocument;
import org.kuali.kra.budget.nonpersonnel.AbstractBudgetCalculatedAmount;
import org.kuali.kra.budget.nonpersonnel.BudgetLineItem;
import org.kuali.kra.budget.nonpersonnel.BudgetLineItemCalculatedAmount;
import org.kuali.kra.budget.parameters.BudgetPeriod;
import org.kuali.kra.budget.personnel.BudgetPerson;
import org.kuali.kra.budget.personnel.BudgetPersonnelCalculatedAmount;
import org.kuali.kra.budget.personnel.BudgetPersonnelDetails;
import org.kuali.kra.budget.summary.BudgetSummaryService;
import org.kuali.kra.budget.versions.AddBudgetVersionEvent;
import org.kuali.kra.budget.versions.BudgetDocumentVersion;
import org.kuali.kra.budget.versions.BudgetVersionOverview;
import org.kuali.kra.budget.versions.BudgetVersionRule;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.DeepCopyPostProcessor;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

/**
 * This class is to process all basic services required for AwardBudget
 */
public class AwardBudgetServiceImpl implements AwardBudgetService {

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private BudgetService<Award> budgetService;
    private BudgetSummaryService budgetSummaryService;

    /**
     * @see org.kuali.kra.award.budget.AwardBudgetService#post(org.kuali.kra.award.budget.document.AwardBudgetDocument)
     */
    public void post(AwardBudgetDocument awardBudgetDocument) {
        processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_POSTED);
        saveDocument(awardBudgetDocument);
    }

    /**
     * @see org.kuali.kra.award.budget.AwardBudgetService#post(org.kuali.kra.award.budget.document.AwardBudgetDocument)
     */
    public void toggleStatus(AwardBudgetDocument awardBudgetDocument) {
        String currentStatusCode = awardBudgetDocument.getAwardBudget().getAwardBudgetStatusCode();
        if (currentStatusCode.equals(getParameterValue(KeyConstants.AWARD_BUDGET_STATUS_TO_BE_POSTED))) {
            processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_DO_NOT_POST);
        }
        else if (currentStatusCode.equals(getParameterValue(KeyConstants.AWARD_BUDGET_STATUS_DO_NOT_POST))) {
            processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_TO_BE_POSTED);
        }
        saveDocument(awardBudgetDocument);
    }

    /**
     * This method...
     * @param awardBudgetDocument
     */
    protected void saveDocument(AwardBudgetDocument awardBudgetDocument) {
        try {
            getDocumentService().saveDocument(awardBudgetDocument);
        }catch (WorkflowException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see org.kuali.kra.award.budget.AwardBudgetService#processApproval(org.kuali.kra.award.budget.document.AwardBudgetDocument)
     */
    public void processApproval(AwardBudgetDocument awardBudgetDocument) {
        KualiWorkflowDocument workFlowDocument = getWorkflowDocument(awardBudgetDocument);
        if(workFlowDocument.stateIsFinal()){
            processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_TO_BE_POSTED);
        }
        saveDocument(awardBudgetDocument);
    }

    /**
     * @see org.kuali.kra.award.budget.AwardBudgetService#processDisapproval(org.kuali.kra.award.budget.document.AwardBudgetDocument)
     */
    public void processDisapproval(AwardBudgetDocument awardBudgetDocument) {
        processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_REJECTED);
    }

    /**
     * @see org.kuali.kra.award.budget.AwardBudgetService#processSubmision(org.kuali.kra.award.budget.document.AwardBudgetDocument)
     */
    public void processSubmision(AwardBudgetDocument awardBudgetDocument) {
        processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_SUBMITTED);
    }
    
    protected void processStatusChange(AwardBudgetDocument awardBudgetDocument,String routingStatus){
        KualiWorkflowDocument workflowDocument = getWorkflowDocument(awardBudgetDocument);
        String submittedStatusCode = getParameterValue(routingStatus);
        String submittedStatus = findStatusDescription(submittedStatusCode);
        awardBudgetDocument.getAwardBudget().setAwardBudgetStatusCode(submittedStatusCode);
        workflowDocument.getRouteHeader().setAppDocStatus(submittedStatus);
    }

    protected String getParameterValue(String awardBudgetParameter) {
        return  getParameterService().getParameterValue(AwardBudgetDocument.class, awardBudgetParameter);
    }


    protected String findStatusDescription(String statusCode) {
        AwardBudgetStatus budgetStatus = getBusinessObjectService().findBySinglePrimaryKey(AwardBudgetStatus.class, statusCode);
        return budgetStatus.getDescription();
    }

    /**
     * @see org.kuali.kra.award.budget.AwardBudgetService#rebudget(org.kuali.kra.award.budget.document.AwardBudgetDocument)
     */
    public AwardBudgetDocument rebudget(AwardDocument awardDocument,String documentDescription) throws WorkflowException{
        AwardBudgetDocument rebudgetDocument = createNewBudgetDocument(documentDescription, awardDocument, true);
        return rebudgetDocument;
    }

    /**
     * Get the corresponding workflow document.  
     * @param doc the document
     * @return the workflow document or null if there is none
     */
    protected KualiWorkflowDocument getWorkflowDocument(Document doc) {
        KualiWorkflowDocument workflowDocument = null;
        if (doc != null) {
            DocumentHeader header = doc.getDocumentHeader();
            if (header != null) {
                try {
                    workflowDocument = header.getWorkflowDocument();
                } 
                catch (RuntimeException ex) {
                    // do nothing; there is no workflow document
                }
            }
        }
        return workflowDocument;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentservice) {
        this.documentService = documentservice;
    }


    /**
     * 
     * @see org.kuali.kra.budget.core.BudgetCommonService#getNewBudgetVersion(org.kuali.kra.budget.document.BudgetParentDocument, java.lang.String)
     */
    public BudgetDocument<Award> getNewBudgetVersion(BudgetParentDocument parentBudgetDocument, String documentDescription)
        throws WorkflowException {
        
        AwardDocument parentDocument = (AwardDocument)parentBudgetDocument;
        AwardBudgetDocument awardBudgetDocument;
        awardBudgetDocument = createNewBudgetDocument(documentDescription, parentDocument,false);

        return awardBudgetDocument;
    }

    protected AwardBudgetDocument copyPostedBudgetVersion(AwardDocument parentDocument) {
        AwardBudgetExt previousPostedBudget = getLatestPostedBudget(parentDocument);
        return (AwardBudgetDocument)previousPostedBudget.getBudgetDocument();
    }

    /**
     * This method...
     * @param documentDescription
     * @param parentDocument
     * @return
     * @throws WorkflowException
     */
    protected AwardBudgetDocument createNewBudgetDocument(String documentDescription, AwardDocument parentDocument,boolean rebudget)
            throws WorkflowException {
        Integer budgetVersionNumber = parentDocument.getNextBudgetVersionNumber();
        AwardBudgetDocument awardBudgetDocument;
        if(isPostedBudgetExist(parentDocument)){
            BudgetDecimal obligatedChangeAmount = getObligatedChangeAmount(parentDocument);
            AwardBudgetExt previousPostedBudget = getLatestPostedBudget(parentDocument);
            BudgetDocument<Award> postedBudgetDocument = (AwardBudgetDocument)previousPostedBudget.getBudgetDocument();
            awardBudgetDocument =  (AwardBudgetDocument)copyBudgetVersion(postedBudgetDocument);
            copyObligatedAmountToLineItems(awardBudgetDocument,obligatedChangeAmount);
//            saveBudgetDocument(awardBudgetDocument,false);
//            awardBudgetDocument = (AwardBudgetDocument) documentService.getByDocumentHeaderId(awardBudgetDocument.getDocumentNumber());
//            parentDocument.refreshReferenceObject("budgetDocumentVersions");

        }else{
            awardBudgetDocument = (AwardBudgetDocument) documentService.getNewDocument(AwardBudgetDocument.class);
        }
        
        awardBudgetDocument.setParentDocument(parentDocument);
        awardBudgetDocument.setParentDocumentKey(parentDocument.getDocumentNumber());
        awardBudgetDocument.setParentDocumentTypeCode(parentDocument.getDocumentTypeCode());
        awardBudgetDocument.getDocumentHeader().setDocumentDescription(documentDescription);
        
        AwardBudgetExt awardBudget = awardBudgetDocument.getAwardBudget();
        awardBudget.setBudgetVersionNumber(budgetVersionNumber);
        awardBudget.setBudgetDocument(awardBudgetDocument);
        BudgetVersionOverview lastBudgetVersion = getLastBudgetVersion(parentDocument);
        awardBudget.setOnOffCampusFlag(lastBudgetVersion==null?Constants.DEFALUT_CAMUS_FLAG:lastBudgetVersion.getOnOffCampusFlag());
        if(awardBudgetDocument.getDocumentHeader() != null && awardBudgetDocument.getDocumentHeader().hasWorkflowDocument()){
            awardBudget.setBudgetInitiator(awardBudgetDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        }
        
        BudgetParent budgetParent = parentDocument.getBudgetParent();
        awardBudget.setStartDate(budgetParent.getRequestedStartDateInitial());
        awardBudget.setEndDate(budgetParent.getRequestedEndDateInitial());
        
        awardBudget.setOhRateTypeCode(this.parameterService.getParameterValue(BudgetDocument.class, Constants.BUDGET_DEFAULT_OVERHEAD_RATE_TYPE_CODE));
        awardBudget.setOhRateClassCode(this.parameterService.getParameterValue(BudgetDocument.class, Constants.BUDGET_DEFAULT_OVERHEAD_RATE_CODE));
        awardBudget.setUrRateClassCode(this.parameterService.getParameterValue(BudgetDocument.class, Constants.BUDGET_DEFAULT_UNDERRECOVERY_RATE_CODE));
        awardBudget.setModularBudgetFlag(this.parameterService.getIndicatorParameter(BudgetDocument.class, Constants.BUDGET_DEFAULT_MODULAR_FLAG));
        awardBudget.setBudgetStatus(this.parameterService.getParameterValue(AwardBudgetDocument.class, KeyConstants.AWARD_BUDGET_STATUS_IN_PROGRESS));

        boolean success = new AwardBudgetVersionRule().processAddBudgetVersion(
                    new AddBudgetVersionEvent("document.parentDocument.budgetDocumentVersion",
                            awardBudgetDocument.getParentDocument(),awardBudget));
        if(!success)
            return null;

        awardBudget.setRateClassTypesReloaded(true);
        //awardBudget.setTotalCostLimit(rebudget?BudgetDecimal.ZERO:getObligatedChangeAmount(parentDocument));
        awardBudget.setTotalCostLimit(getObligatedChangeAmount(parentDocument));
        if (awardBudget.getTotalCostLimit().equals(BudgetDecimal.ZERO) && isPostedBudgetExist(parentDocument)) {
            rebudget = true;
        }
        saveBudgetDocument(awardBudgetDocument,rebudget);
        awardBudgetDocument = (AwardBudgetDocument) documentService.getByDocumentHeaderId(awardBudgetDocument.getDocumentNumber());
        parentDocument.refreshReferenceObject("budgetDocumentVersions");

        return awardBudgetDocument;
    }
    protected void copyObligatedAmountToLineItems(AwardBudgetDocument awardBudgetDocument,BudgetDecimal obligatedChangeAmount) {
        AwardBudgetExt newAwardBudgetFromPosted = awardBudgetDocument.getAwardBudget();
        List<BudgetPeriod> awardBudgetPeriods = newAwardBudgetFromPosted.getBudgetPeriods();
        for (BudgetPeriod budgetPeriod : awardBudgetPeriods) {
            AwardBudgetPeriodExt awardBudgetPeriod = (AwardBudgetPeriodExt)budgetPeriod;
            List<BudgetLineItem> lineItems = awardBudgetPeriod.getBudgetLineItems();
            for (BudgetLineItem budgetLineItem : lineItems) {
                AwardBudgetLineItemExt awardBudgetLineItem = (AwardBudgetLineItemExt)budgetLineItem;
                List<BudgetPersonnelDetails> personnelDetailsList = awardBudgetLineItem.getBudgetPersonnelDetailsList();
                for (BudgetPersonnelDetails budgetPersonnelDetails : personnelDetailsList) {
                    AwardBudgetPersonnelDetailsExt awardBudgetPersonnelDetails = (AwardBudgetPersonnelDetailsExt)budgetPersonnelDetails;
                    List<AwardBudgetPersonnelCalculatedAmountExt> personnelCalcAmounts = awardBudgetPersonnelDetails.getBudgetCalculatedAmounts();
                    for (AwardBudgetPersonnelCalculatedAmountExt awardBudgetPersonnelCalculatedAmountExt : personnelCalcAmounts) {
                        awardBudgetPersonnelCalculatedAmountExt.setObligatedAmount(
                                awardBudgetPersonnelCalculatedAmountExt.getObligatedAmount().add(
                                        awardBudgetPersonnelCalculatedAmountExt.getCalculatedCost().add(
                                                awardBudgetPersonnelCalculatedAmountExt.getCalculatedCostSharing())));
                        awardBudgetPersonnelCalculatedAmountExt.setCalculatedCost(BudgetDecimal.ZERO);
                        awardBudgetPersonnelCalculatedAmountExt.setCalculatedCostSharing(BudgetDecimal.ZERO);
                    }
                    awardBudgetPersonnelDetails.setObligatedAmount(
                         awardBudgetPersonnelDetails.getObligatedAmount().add(
                            awardBudgetPersonnelDetails.getSalaryRequested().add(
                                    awardBudgetPersonnelDetails.getCostSharingAmount())));
                    awardBudgetPersonnelDetails.setSalaryRequested(BudgetDecimal.ZERO);
                    awardBudgetPersonnelDetails.setCostSharingAmount(BudgetDecimal.ZERO);
                }
                List<AwardBudgetLineItemCalculatedAmountExt> calcAmounts = budgetLineItem.getBudgetCalculatedAmounts();
                for (AwardBudgetLineItemCalculatedAmountExt budgetLineItemCalculatedAmount : calcAmounts) {
                    budgetLineItemCalculatedAmount = (AwardBudgetLineItemCalculatedAmountExt)budgetLineItemCalculatedAmount;
                    budgetLineItemCalculatedAmount.setObligatedAmount(
                            budgetLineItemCalculatedAmount.getObligatedAmount().add(
                            budgetLineItemCalculatedAmount.getCalculatedCost().add(
                                    budgetLineItemCalculatedAmount.getCalculatedCostSharing())));
                    budgetLineItemCalculatedAmount.setCalculatedCost(BudgetDecimal.ZERO);
                    budgetLineItemCalculatedAmount.setCalculatedCostSharing(BudgetDecimal.ZERO);
                }
                awardBudgetLineItem.setObligatedAmount(
                        awardBudgetLineItem.getObligatedAmount().add(
                        awardBudgetLineItem.getLineItemCost().add(
                                awardBudgetLineItem.getCostSharingAmount())));
                awardBudgetLineItem.setLineItemCost(BudgetDecimal.ZERO);
                awardBudgetLineItem.setCostSharingAmount(BudgetDecimal.ZERO);
            }
            awardBudgetPeriod.setObligatedAmount(awardBudgetPeriod.getObligatedAmount().add(awardBudgetPeriod.getTotalCost()));
            awardBudgetPeriod.setTotalCost(BudgetDecimal.ZERO);
            awardBudgetPeriod.setTotalDirectCost(BudgetDecimal.ZERO);
            awardBudgetPeriod.setTotalIndirectCost(BudgetDecimal.ZERO);
            awardBudgetPeriod.setTotalCostLimit(obligatedChangeAmount);
        }
//        getBudgetSummaryService().calculateBudget(newAwardBudgetFromPosted);
    }

    protected AwardBudgetExt getLatestPostedBudget(AwardDocument awardDocument) {
        List<BudgetDocumentVersion> documentVersions = awardDocument.getBudgetDocumentVersions();
        QueryList<AwardBudgetVersionOverviewExt> awardBudgetVersionOverviews = new QueryList<AwardBudgetVersionOverviewExt>();
        for (BudgetDocumentVersion budgetDocumentVersion : documentVersions) {
            awardBudgetVersionOverviews.add((AwardBudgetVersionOverviewExt)budgetDocumentVersion.getBudgetVersionOverview());
        }
        
        Equals eqPostedStatus = new Equals("awardBudgetStatusCode",getAwardPostedStatusCode()); 
        QueryList<AwardBudgetVersionOverviewExt> postedVersions = awardBudgetVersionOverviews.filter(eqPostedStatus);
        AwardBudgetExt postedBudget = null;
        if(!postedVersions.isEmpty()){
            postedVersions.sort("budgetVersionNumber",false);
            AwardBudgetVersionOverviewExt postedVersion = postedVersions.get(0);
            try {
                AwardBudgetDocument awardBudgetDocument = (AwardBudgetDocument)documentService.getByDocumentHeaderId(postedVersion.getDocumentNumber());
                postedBudget = awardBudgetDocument.getAwardBudget();
            }
            catch (WorkflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return postedBudget;
    }

    protected BudgetDecimal getObligatedChangeAmount(AwardDocument awardDocument) {
        List<BudgetDocumentVersion> documentVersions = awardDocument.getBudgetDocumentVersions();
        String postedStatusCode = getAwardPostedStatusCode();
        BudgetDecimal postedTotalAmount = BudgetDecimal.ZERO;
        for (BudgetDocumentVersion budgetDocumentVersion : documentVersions) {
            AwardBudgetVersionOverviewExt budget = (AwardBudgetVersionOverviewExt)budgetDocumentVersion.getBudgetVersionOverview();
            if(budget.getAwardBudgetStatusCode().equals(postedStatusCode)){
                postedTotalAmount = postedTotalAmount.add(budget.getTotalCost());
            }
        }
        BudgetDecimal awardObligatedAmount = new BudgetDecimal(awardDocument.getAward().getObligatedTotal().bigDecimalValue());
        return awardObligatedAmount.subtract(postedTotalAmount);
    }

    /*
     * A utility method to check whther a budget has been posted for this award, then it can be 
     * used as one of the condition to set rebudget type.
     */
    protected boolean isPostedBudgetExist(AwardDocument awardDocument) {
        boolean exist = false;
        List<BudgetDocumentVersion> documentVersions = awardDocument.getBudgetDocumentVersions();
        String postedStatusCode = getAwardPostedStatusCode();
        for (BudgetDocumentVersion budgetDocumentVersion : documentVersions) {
            AwardBudgetVersionOverviewExt budget = (AwardBudgetVersionOverviewExt)budgetDocumentVersion.getBudgetVersionOverview();
            if(budget.getAwardBudgetStatusCode().equals(postedStatusCode)){
                exist = true;
                break;
            }
        }
        return exist;
    }

    protected String getAwardPostedStatusCode() {
        return this.parameterService.getParameterValue(AwardBudgetDocument.class, KeyConstants.AWARD_BUDGET_STATUS_POSTED);
    }

    protected BudgetVersionOverview getLastBudgetVersion(AwardDocument award) {
        List<BudgetDocumentVersion> awardBudgetDocumentVersions = award.getBudgetDocumentVersions();
        BudgetVersionOverview budgetVersionOverview = null;
        int versionSize = awardBudgetDocumentVersions.size();
        if(versionSize>0){
            budgetVersionOverview = awardBudgetDocumentVersions.get(versionSize-1).getBudgetVersionOverview();
        }
        return budgetVersionOverview;
    }

    /**
     * This method...
     * @param budgetDocument
     * @param isProposalBudget
     * @param budget
     * @param budgetParent
     * @throws WorkflowException
     */
    protected void saveBudgetDocument(BudgetDocument<Award> budgetDocument,boolean rebudget) throws WorkflowException {
        AwardBudgetDocument awardBudgetDocument = (AwardBudgetDocument)budgetDocument;
        Budget budget = budgetDocument.getBudget();
        AwardBudgetExt budgetExt = (AwardBudgetExt)budget;
        budgetExt.setAwardBudgetTypeCode(getParameterValue(rebudget?KeyConstants.AWARD_BUDGET_TYPE_REBUDGET:
            KeyConstants.AWARD_BUDGET_TYPE_NEW));
        if(rebudget){
            AwardBudgetType awardBudgetType=getBusinessObjectService().findBySinglePrimaryKey(AwardBudgetType.class, 
                    budgetExt.getAwardBudgetTypeCode()); 
            budgetExt.setDescription(awardBudgetType.getDescription());
        }
        processStatusChange(awardBudgetDocument, KeyConstants.AWARD_BUDGET_STATUS_IN_PROGRESS);
        saveDocument(awardBudgetDocument);
    }

    protected void copyProposalBudgetLineItemsToAwardBudget(BudgetPeriod awardBudgetPeriod, BudgetPeriod proposalBudgetPeriod) {
        List awardBudgetLineItems = awardBudgetPeriod.getBudgetLineItems();
        List<BudgetLineItem> lineItems = proposalBudgetPeriod.getBudgetLineItems();
        for (BudgetLineItem budgetLineItem : lineItems) {
            String[] ignoreProperties = {"budgetId","budgetLineItemId","budgetPeriodId",
                        "budgetLineItemCalculatedAmounts","budgetPersonnelDetailsList","budgetRateAndBaseList"};
            AwardBudgetLineItemExt awardBudgetLineItem = new AwardBudgetLineItemExt(); 
            BeanUtils.copyProperties(budgetLineItem, awardBudgetLineItem, ignoreProperties);
            awardBudgetLineItem.setLineItemNumber(awardBudgetPeriod.getBudget().getHackedDocumentNextValue(Constants.BUDGET_LINEITEM_NUMBER));
            awardBudgetLineItem.setBudgetId(awardBudgetPeriod.getBudgetId());
            awardBudgetLineItem.setStartDate(awardBudgetPeriod.getStartDate());
            awardBudgetLineItem.setEndDate(awardBudgetPeriod.getEndDate());
            List<BudgetPersonnelDetails> awardBudgetPersonnelLineItems = awardBudgetLineItem.getBudgetPersonnelDetailsList();
            List<BudgetPersonnelDetails> budgetPersonnelLineItems = budgetLineItem.getBudgetPersonnelDetailsList();
            for (BudgetPersonnelDetails budgetPersonnelDetails : budgetPersonnelLineItems) {
                budgetPersonnelDetails.setBudgetLineItemId(budgetLineItem.getBudgetLineItemId());
                AwardBudgetPersonnelDetailsExt awardBudgetPerDetails = new AwardBudgetPersonnelDetailsExt();
                BeanUtils.copyProperties(budgetPersonnelDetails, awardBudgetPerDetails, 
                        new String[]{"budgetPersonnelLineItemId","budgetLineItemId","budgetId",
                        "budgetPersonnelCalculatedAmounts","budgetPersonnelRateAndBaseList", "validToApplyInRate"});
                awardBudgetPerDetails.setPersonNumber(awardBudgetPeriod.getBudget().getHackedDocumentNextValue(Constants.BUDGET_PERSON_LINE_NUMBER));
                BudgetPerson oldBudgetPerson = budgetPersonnelDetails.getBudgetPerson();
                BudgetPerson currentBudgetPerson = findMatchingPersonInBudget(awardBudgetPeriod.getBudget(), 
                		oldBudgetPerson, budgetPersonnelDetails.getJobCode());
                if (currentBudgetPerson == null) {
                	currentBudgetPerson = new BudgetPerson();
                	BeanUtils.copyProperties(oldBudgetPerson, currentBudgetPerson, new String[]{"budgetId", "personSequenceNumber"});
                	currentBudgetPerson.setBudgetId(awardBudgetPeriod.getBudgetId());
                	currentBudgetPerson.setPersonSequenceNumber(awardBudgetPeriod.getBudget().getBudgetDocument().getHackedDocumentNextValue(Constants.PERSON_SEQUENCE_NUMBER));
                	awardBudgetPeriod.getBudget().getBudgetPersons().add(currentBudgetPerson);
                }
                awardBudgetPerDetails.setBudgetPerson(currentBudgetPerson);
                awardBudgetPerDetails.setPersonSequenceNumber(currentBudgetPerson.getPersonSequenceNumber());
                awardBudgetPerDetails.setBudgetId(awardBudgetPeriod.getBudgetId());
                awardBudgetPerDetails.setCostElement(awardBudgetLineItem.getCostElement());
                awardBudgetPerDetails.setStartDate(awardBudgetLineItem.getStartDate());
                awardBudgetPerDetails.setEndDate(awardBudgetLineItem.getEndDate());
                awardBudgetPersonnelLineItems.add(awardBudgetPerDetails);
            }
            awardBudgetLineItems.add(awardBudgetLineItem);
        }
    }
    
    protected BudgetPerson findMatchingPersonInBudget(Budget budget, BudgetPerson oldBudgetPerson, String jobCode) {
        for (BudgetPerson budgetPerson : budget.getBudgetPersons()) {
        	if (budgetPerson.isSamePerson(oldBudgetPerson) && StringUtils.equals(budgetPerson.getJobCode(), jobCode)) {
        		return budgetPerson;
        	}
        }
        return null;
    }

    protected DeepCopyPostProcessor getDeepCopyPostProcessor() {
        return KraServiceLocator.getService(DeepCopyPostProcessor.class);
    }

    public BudgetDocument<Award> copyBudgetVersion(BudgetDocument<Award> budgetDocument) throws WorkflowException {
        return getBudgetService().copyBudgetVersion(budgetDocument);
    }

    /**
     * Sets the budgetService attribute value.
     * @param budgetService The budgetService to set.
     */
    public void setBudgetService(BudgetService<Award> budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Gets the budgetService attribute. 
     * @return Returns the budgetService.
     */
    public BudgetService<Award> getBudgetService() {
        return budgetService;
    }

    /**
     * Gets the budgetSummaryService attribute. 
     * @return Returns the budgetSummaryService.
     */
    public BudgetSummaryService getBudgetSummaryService() {
        return budgetSummaryService;
    }

    /**
     * Sets the budgetSummaryService attribute value.
     * @param budgetSummaryService The budgetSummaryService to set.
     */
    public void setBudgetSummaryService(BudgetSummaryService budgetSummaryService) {
        this.budgetSummaryService = budgetSummaryService;
    }

    public void copyLineItemsFromProposalPeriods(Collection rawValues, BudgetPeriod awardBudgetPeriod) throws WorkflowException {
        awardBudgetPeriod.getBudgetLineItems().clear();
        Iterator iter = rawValues.iterator();
        while (iter.hasNext()) {
            BudgetPeriod proposalPeriod = (BudgetPeriod)iter.next();
            copyProposalBudgetLineItemsToAwardBudget(awardBudgetPeriod, proposalPeriod);
        }
        getDocumentService().saveDocument(awardBudgetPeriod.getBudget().getBudgetDocument());
        getBudgetSummaryService().calculateBudget(awardBudgetPeriod.getBudget());
        
    }
}
