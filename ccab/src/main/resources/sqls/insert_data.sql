BEGIN;

INSERT INTO ACCOUNT (USER_ID, PASSWORD, FIRST_NAME, LAST_NAME) VALUES ('0000000001', '{pbkdf2}9dfbc2af972fc8e7ed0bf64366f3f9e4362d38fbddb9b1cd8ef968aeee71c68d1c33fb2c76b305d6', 'Hanako', 'Denden');
INSERT INTO ACCOUNT (USER_ID, PASSWORD, FIRST_NAME, LAST_NAME) VALUES ('0000000002', '{pbkdf2}9dfbc2af972fc8e7ed0bf64366f3f9e4362d38fbddb9b1cd8ef968aeee71c68d1c33fb2c76b305d6', 'Taro', 'Denden');

INSERT INTO MEMBER (CUSTOMER_NO, NAME, FURI_NAME) VALUES ('9999999991', '電電花子', 'Hanako Denden');
INSERT INTO MEMBER (CUSTOMER_NO, NAME, FURI_NAME) VALUES ('9999999992', '電電太郎', 'Taro Denden');

COMMIT;