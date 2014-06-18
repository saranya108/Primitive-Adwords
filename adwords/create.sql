CREATE TABLE QUERIES(QID NUMBER PRIMARY KEY, QUERY VARCHAR2(400));
CREATE TABLE ADVERTISERS(ADVERTISERID INTEGER PRIMARY KEY, BUDGET NUMBER, CTC NUMBER,  G_1_BALANCE NUMBER, G_2_BALANCE NUMBER, B_1_BALANCE NUMBER, B_2_BALANCE NUMBER, GEN_1_BALANCE NUMBER, GEN_2_BALANCE NUMBER, G_1_COUNT NUMBER, G_2_COUNT NUMBER, B_1_COUNT NUMBER, B_2_COUNT NUMBER, GEN_1_COUNT NUMBER, GEN_2_COUNT NUMBER);
CREATE TABLE KEYWORDS (ADVERTISERID NUMBER,	KEYWORD VARCHAR2(100), BID NUMBER,  PRIMARY KEY (ADVERTISERID, KEYWORD), CONSTRAINT FK_ADVERTISERID_KEYWORDS FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID));
CREATE TABLE WORDS_DICTIONARY(WORDS VARCHAR2(200), INDEX_NUMBER NUMBER);
CREATE TABLE TEMP(WORDS VARCHAR2(200));
CREATE TABLE TEMP2(WORDS VARCHAR2(200));
CREATE TABLE GREEDY_FIRST  (QID NUMBER, RANK NUMBER, ADVERTISERID NUMBER,	BALANCE NUMBER, 	BUDGET NUMBER,  CONSTRAINT FK_ADVERTISERID_G1 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID), CONSTRAINT FK_QID_G1 FOREIGN KEY (QID) REFERENCES QUERIES(QID));
CREATE TABLE GREEDY_SECOND  (QID NUMBER, RANK NUMBER, ADVERTISERID NUMBER,	BALANCE NUMBER, 	BUDGET NUMBER,  CONSTRAINT FK_ADVERTISERID_G2 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID), CONSTRAINT FK_QID_G2 FOREIGN KEY (QID) REFERENCES QUERIES(QID));
CREATE TABLE BALANCE_FIRST  (QID NUMBER, RANK NUMBER, ADVERTISERID NUMBER,	BALANCE NUMBER, 	BUDGET NUMBER,  CONSTRAINT FK_ADVERTISERID_B1 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID), CONSTRAINT FK_QID_B1 FOREIGN KEY (QID) REFERENCES QUERIES(QID));
CREATE TABLE BALANCE_SECOND  (QID NUMBER, RANK NUMBER, ADVERTISERID NUMBER,	BALANCE NUMBER, 	BUDGET NUMBER,  CONSTRAINT FK_ADVERTISERID_B2 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID), CONSTRAINT FK_QID_B2 FOREIGN KEY (QID) REFERENCES QUERIES(QID));
CREATE TABLE GENERALIZED_FIRST  (QID NUMBER, RANK NUMBER, ADVERTISERID NUMBER,	BALANCE NUMBER, 	BUDGET NUMBER,  CONSTRAINT FK_ADVERTISERID_GEN1 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID), CONSTRAINT FK_QID_GEN1 FOREIGN KEY (QID) REFERENCES QUERIES(QID));
CREATE TABLE GENERALIZED_SECOND  (QID NUMBER, RANK NUMBER, ADVERTISERID NUMBER,	BALANCE NUMBER, 	BUDGET NUMBER,  CONSTRAINT FK_ADVERTISERID_GEN2 FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID), CONSTRAINT FK_QID_GEN2 FOREIGN KEY (QID) REFERENCES QUERIES(QID));
CREATE TABLE ADVERTISERS_INDEX  (ADVERTISERID NUMBER,  INDEX_NUMBER NUMBER,   CNT NUMBER,  PRIMARY KEY (ADVERTISERID, INDEX_NUMBER), CONSTRAINT FK_ADVERTISERID_AI FOREIGN KEY (ADVERTISERID) REFERENCES ADVERTISERS(ADVERTISERID));
CREATE SEQUENCE  INDEX_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
EXIT;