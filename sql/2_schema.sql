-- STEP 2. Run as the LIBRARY user.
--   SQL> connect library/library123
--   SQL> @D:\path\to\lib-oracle\sql\2_schema.sql
-- (Works on Oracle 10g and newer: sequences instead of identity columns.)
--
-- To start over, run these first:
--   DROP TABLE issues; DROP TABLE books; DROP TABLE users;
--   DROP SEQUENCE users_seq; DROP SEQUENCE books_seq; DROP SEQUENCE issues_seq;

CREATE SEQUENCE users_seq  START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE books_seq  START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE issues_seq START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE users (
  id            NUMBER(10)          NOT NULL,
  name          VARCHAR2(100 CHAR)  NOT NULL,
  email         VARCHAR2(120 CHAR)  NOT NULL,
  password_hash VARCHAR2(255)       NOT NULL,
  role          VARCHAR2(10)        DEFAULT 'MEMBER' NOT NULL,
  created_at    TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'MEMBER'))
);

CREATE TABLE books (
  id               NUMBER(10)          NOT NULL,
  title            VARCHAR2(200 CHAR)  NOT NULL,
  author           VARCHAR2(150 CHAR)  NOT NULL,
  category         VARCHAR2(80 CHAR)   DEFAULT 'General' NOT NULL,
  isbn             VARCHAR2(20),
  total_copies     NUMBER(5)           DEFAULT 1 NOT NULL,
  available_copies NUMBER(5)           DEFAULT 1 NOT NULL,
  CONSTRAINT pk_books PRIMARY KEY (id),
  CONSTRAINT chk_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);

CREATE TABLE issues (
  id          NUMBER(10) NOT NULL,
  user_id     NUMBER(10) NOT NULL,
  book_id     NUMBER(10) NOT NULL,
  issue_date  DATE       NOT NULL,
  due_date    DATE       NOT NULL,
  return_date DATE,
  fine        NUMBER(8)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_issues PRIMARY KEY (id),
  CONSTRAINT fk_issue_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_issue_book FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE INDEX idx_books_title  ON books (title);
CREATE INDEX idx_issues_active ON issues (return_date, due_date);

INSERT INTO books VALUES (books_seq.NEXTVAL, 'Clean Code', 'Robert C. Martin', 'Programming', '9780132350884', 3, 3);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'Effective Java', 'Joshua Bloch', 'Programming', '9780134685991', 2, 2);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'Head First Servlets and JSP', 'Bryan Basham', 'Programming', '9780596516680', 2, 2);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'Introduction to Algorithms', 'Thomas H. Cormen', 'Computer Science', '9780262033848', 4, 4);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'Database System Concepts', 'Abraham Silberschatz', 'Computer Science', '9780078022159', 3, 3);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'Wings of Fire', 'A. P. J. Abdul Kalam', 'Biography', '9788173711466', 5, 5);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'The Discovery of India', 'Jawaharlal Nehru', 'History', '9780143031031', 2, 2);
INSERT INTO books VALUES (books_seq.NEXTVAL, 'Malgudi Days', 'R. K. Narayan', 'Fiction', '9780143039655', 3, 3);

COMMIT;
