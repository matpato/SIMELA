
-- CREATE User(Role)

-- password encrypted is 12345

CREATE ROLE alsrmadmin LOGIN ENCRYPTED PASSWORD 'md5ca529ae069ad41dc7df7a96db657c7bb'
  SUPERUSER CREATEDB CREATEROLE
   VALID UNTIL 'infinity';

