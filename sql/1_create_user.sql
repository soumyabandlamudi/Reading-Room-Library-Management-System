-- STEP 1. Run ONCE as SYSTEM. Creates the schema owner used by the application.
--   SQL> connect system
--   SQL> @D:\path\to\lib-oracle\sql\1_create_user.sql
CREATE USER library IDENTIFIED BY library123
  DEFAULT TABLESPACE users QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE TO library;
