BEGIN;

DROP TABLE IF EXISTS CAAB_MEMBER CASCADE;

CREATE TABLE CAAB_MEMBER (
  CUSTOMER_NO VARCHAR(10) NOT NULL,
  KANJI_FAMILY_NAME VARCHAR(10) NOT NULL,
  KANJI_GIVEN_NAME VARCHAR(10) NOT NULL,
  KANA_FAMILY_NAME VARCHAR(10) NOT NULL,
  KANA_GIVEN_NAME VARCHAR(10) NOT NULL,
  BIRTHDAY DATE NOT NULL,
  TEL VARCHAR(13) NOT NULL,
  ZIP_CODE VARCHAR(7) NOT NULL,
  ADDRESS VARCHAR(60) NOT NULL
);

ALTER TABLE CAAB_MEMBER ADD CONSTRAINT PK_CAAB_MEMBER PRIMARY KEY (CUSTOMER_NO);

COMMIT;