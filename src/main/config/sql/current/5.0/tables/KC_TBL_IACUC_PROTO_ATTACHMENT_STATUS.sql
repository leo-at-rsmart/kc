CREATE TABLE IACUC_PROTO_ATTACHMENT_STATUS ( 
    STATUS_CD VARCHAR2(3) NOT NULL, 
    DESCRIPTION VARCHAR2(300) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) NOT NULL)
/
ALTER TABLE IACUC_PROTO_ATTACHMENT_STATUS 
ADD CONSTRAINT PK_IACUC_PROTO_ATTACH_STATUS 
PRIMARY KEY (STATUS_CD)
/
