-- Table Script  
CREATE TABLE PROTO_NOTIFICATION_TEMPL (
    NOTIFICATION_TEMPL_ID NUMBER (12, 0) NOT NULL, 
    ACTION_TYPE_CODE VARCHAR (3) NOT NULL, 
    FILE_NAME VARCHAR2 (150) NOT NULL, 
    NOTIFICATION_TEMPLATE BLOB NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2 (60) NOT NULL, 
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2 (36) NOT NULL) ;

-- Primary Key Constraint   
ALTER TABLE PROTO_NOTIFICATION_TEMPL 
    ADD CONSTRAINT PK_PROTO_NOTIFICATION_TEMPL 
            PRIMARY KEY (NOTIFICATION_TEMPL_ID) ;