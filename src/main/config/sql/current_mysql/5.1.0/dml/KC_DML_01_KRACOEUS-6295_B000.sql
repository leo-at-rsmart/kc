DELIMITER /

UPDATE NOTIFICATION_TYPE SET SUBJECT='Human Subjects Usage Special Review Inserted - {AWARD_NUMBER}' WHERE MODULE_CODE='1' AND ACTION_CODE='552'
/
UPDATE NOTIFICATION_TYPE SET MESSAGE='Human Subjects Special Review Inserted - {AWARD_NUMBER}' WHERE MODULE_CODE='1' AND ACTION_CODE='552'
/
UPDATE NOTIFICATION_TYPE SET SUBJECT='Human Subjects Usage Special Review Removed - {AWARD_NUMBER}' WHERE MODULE_CODE='1' AND ACTION_CODE='553'
/
UPDATE NOTIFICATION_TYPE SET MESSAGE='Human Subjects Special Review Removed - {AWARD_NUMBER}' WHERE MODULE_CODE='1' AND ACTION_CODE='553'
/
INSERT INTO SEQ_NOTIFICATION_TYPE_ID VALUES(NULL)
/
INSERT INTO NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, MODULE_CODE, ACTION_CODE, DESCRIPTION, SUBJECT, MESSAGE, PROMPT_USER, SEND_NOTIFICATION, UPDATE_USER, UPDATE_TIMESTAMP, VER_NBR, OBJ_ID)
       VALUES((SELECT (MAX(ID)) FROM SEQ_NOTIFICATION_TYPE_ID),1,554,'Sent when a protocol is added as a special review in an award, that will result in IACUC linking.','Animal Usage Special Review Inserted - {AWARD_NUMBER}','Animal Usage Special Review Inserted - {AWARD_NUMBER}','Y','Y','admin',NOW(),1,UUID())
/
INSERT INTO SEQ_NOTIFICATION_TYPE_ID VALUES(NULL)
/
INSERT INTO NOTIFICATION_TYPE (NOTIFICATION_TYPE_ID, MODULE_CODE, ACTION_CODE, DESCRIPTION, SUBJECT, MESSAGE, PROMPT_USER, SEND_NOTIFICATION, UPDATE_USER, UPDATE_TIMESTAMP, VER_NBR, OBJ_ID)
       VALUES((SELECT (MAX(ID)) FROM SEQ_NOTIFICATION_TYPE_ID),1,555,'Send when a linked IACUC protocol is removed from the award.','Animal Usage Special Review Removed - {AWARD_NUMBER}','Animal Usage Special Review Removed - {AWARD_NUMBER}','Y','Y','admin',NOW(),1,UUID())
/
INSERT INTO SEQ_NOTIFICATION_TYPE_ID VALUES(NULL)
/
INSERT INTO NOTIFICATION_TYPE_RECIPIENT (NOTIFICATION_TYPE_RECIPIENT_ID, NOTIFICATION_TYPE_ID, ROLE_NAME, UPDATE_USER, UPDATE_TIMESTAMP, VER_NBR, OBJ_ID, ROLE_SUB_QUALIFIER)
       VALUES((SELECT (MAX(ID)) FROM SEQ_NOTIFICATION_TYPE_ID), (SELECT NOTIFICATION_TYPE_ID FROM NOTIFICATION_TYPE WHERE MODULE_CODE='1' AND ACTION_CODE='554'),'KC-WKFLW:Unit Administrator','admin',NOW(),1,UUID(),2)
/
INSERT INTO SEQ_NOTIFICATION_TYPE_ID VALUES(NULL)
/
INSERT INTO NOTIFICATION_TYPE_RECIPIENT (NOTIFICATION_TYPE_RECIPIENT_ID, NOTIFICATION_TYPE_ID, ROLE_NAME, UPDATE_USER, UPDATE_TIMESTAMP, VER_NBR, OBJ_ID, ROLE_SUB_QUALIFIER)
       VALUES((SELECT (MAX(ID)) FROM SEQ_NOTIFICATION_TYPE_ID), (SELECT NOTIFICATION_TYPE_ID FROM NOTIFICATION_TYPE WHERE MODULE_CODE='1' AND ACTION_CODE='555'),'KC-WKFLW:Unit Administrator','admin',NOW(),1,UUID(),2)
/
DELIMITER ;