INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL )
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'View Protocol Online Review Comments'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Section'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KR-WKFLW' AND NM = 'documentTypeName'), 'ProtocolDocument' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL )
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'View Protocol Online Review Comments'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Section'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KC-SYS' AND NM = 'sectionName'), 'reviewComments' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL )
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'Maintain Protocol Online Review Comments'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Section'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KR-WKFLW' AND NM = 'documentTypeName'), 'ProtocolOnlineReviewDocument' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL )
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'Maintain Protocol Online Review Comments'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Section'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KC-SYS' AND NM = 'sectionName'), 'reviewComments' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL )
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'Maintain Protocol Online Reviews'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Section'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KR-WKFLW' AND NM = 'documentTypeName'), 'ProtocolDocument' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL )
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'Maintain Protocol Online Reviews'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Section'),(SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KC-SYS' AND NM = 'sectionName'),'onlineReviews' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
SELECT MAX(ID), UUID(), 1, (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KC-PROTOCOL' AND NM = 'Perform Committee Actions'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD = ' KC-SYS' AND NM = 'Document Type (Permission)'),(SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD = 'KC-SYS' AND NM = 'documentTypeName'), 'CommitteeDocument' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
SELECT MAX(ID), UUID () , '1', (SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'Modify Notification Template' AND NMSPC_CD = 'KC-PROTOCOL'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NM = 'Document Type (Permission)' AND NMSPC_CD = 'KR-SYS'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NM = 'documentTypeName' AND NMSPC_CD = 'KR-WKFLW'), 'ProtocolNotificationTemplateMaintenanceDocument' FROM KRIM_ATTR_DATA_ID_S;

INSERT INTO KRIM_ATTR_DATA_ID_S VALUES (NULL);

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
SELECT MAX(ID), UUID () , '1', (SELECT PERM_ID FROM KRIM_PERM_T WHERE NM = 'View Notification Template' AND NMSPC_CD = 'KC-PROTOCOL'), (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NM = 'Document Type (Permission)' AND NMSPC_CD = 'KR-SYS'), (SELECT KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NM = 'documentTypeName' AND NMSPC_CD = 'KR-WKFLW'), 'ProtocolNotificationTemplateMaintenanceDocument' FROM KRIM_ATTR_DATA_ID_S;

COMMIT;