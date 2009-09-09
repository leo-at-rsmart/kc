-- FK constraints for PROPOSAL_LOG 
ALTER TABLE PROPOSAL_LOG 
    ADD CONSTRAINT FK_LOG_STATUS FOREIGN KEY (LOG_STATUS) 
                REFERENCES PROPOSAL_LOG_STATUS (PROPOSAL_LOG_STATUS_CODE) ;

ALTER TABLE PROPOSAL_LOG 
    ADD CONSTRAINT FK2_PROPOSAL_TYPE_CODE FOREIGN KEY (PROPOSAL_TYPE_CODE) 
                REFERENCES PROPOSAL_TYPE (PROPOSAL_TYPE_CODE) ;

ALTER TABLE PROPOSAL_LOG 
    ADD CONSTRAINT FK_LEAD_UNIT FOREIGN KEY (LEAD_UNIT) 
                REFERENCES UNIT (UNIT_NUMBER) ;

ALTER TABLE PROPOSAL_LOG 
    ADD CONSTRAINT FK_PROPOSAL_LOG_TYPE_CODE FOREIGN KEY (PROPOSAL_LOG_TYPE_CODE) 
                REFERENCES PROPOSAL_LOG_TYPE (PROPOSAL_LOG_TYPE_CODE) ;