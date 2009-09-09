ALTER TABLE AWARD_ATTACHMENT 
    ADD CONSTRAINT FK_ATTACHMENT_FILE 
            FOREIGN KEY (FILE_ID) 
                REFERENCES ATTACHMENT_FILE (FILE_ID) ON DELETE CASCADE;

ALTER TABLE AWARD_ATTACHMENT 
    ADD CONSTRAINT FK_AWARD_ATTACHMENT_TYPE FOREIGN KEY (TYPE_CODE) 
                REFERENCES AWARD_ATTACHMENT_TYPE (TYPE_CODE) ;

ALTER TABLE AWARD_ATTACHMENT 
    ADD CONSTRAINT FK_AW_ATT_AWARD FOREIGN KEY (AWARD_ID) 
                REFERENCES AWARD (AWARD_ID) ;