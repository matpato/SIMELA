#
# Create Star Schema
#

CREATE DATABASE IF NOT EXISTS `alsm_stagingarea` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

GRANT ALL ON alsm_stagingarea.* TO 'admin'@'localhost' IDENTIFIED BY 'alsmon';

USE `alsm_stagingarea`;

START TRANSACTION;

CREATE TABLE IF NOT EXISTS patient
  (
    patient_id VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    gender CHAR(1) NOT NULL,
    birthdate DATE NOT NULL,
    diagnosedon DATE NOT NULL,
    createdon DATE NOT NULL,
    updatedon DATE NOT NULL,
    rowversion VARCHAR(18) NOT NULL,
    at_high DECIMAL(5,3) NOT NULL,
    at_low DECIMAL(5,3) NOT NULL,
    fcr_high DECIMAL(5,3) NOT NULL,
    fcr_low DECIMAL(5,3) NOT NULL,
    scm_high DECIMAL(5,3) NOT NULL,
    scm_low DECIMAL(5,3) NOT NULL,
    PRIMARY KEY (patient_id)
);

CREATE TABLE IF NOT EXISTS muscle
  (
    muscle_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    acronym VARCHAR(10) NOT NULL,
    side VARCHAR(25),
    PRIMARY KEY (muscle_id)
);

CREATE TABLE IF NOT EXISTS emg
  (
    emg_id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id VARCHAR(10) NOT NULL,
    muscle_id INT NOT NULL,
    fulldate DATE NOT NULL,
    fulltime TIME NOT NULL,
	nr_peaks INT,
    area DOUBLE,
    high_pass VARCHAR(10) NOT NULL,
    low_pass VARCHAR(10) NOT NULL,
    electrode VARCHAR(50) NOT NULL,
    protocol VARCHAR(50) NOT NULL,
    PRIMARY KEY (emg_id),
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    FOREIGN KEY (muscle_id) REFERENCES muscle(muscle_id)
);

CREATE TABLE IF NOT EXISTS emg_data
  (
    emg_id BIGINT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    PRIMARY KEY (emg_id, x, y),
    FOREIGN KEY (emg_id) REFERENCES emg(emg_id)
);

set @exists = (SELECT count(1) FROM information_schema.tables WHERE table_schema = 'alsm_stagingarea' AND table_name = 'metadata_execution');

CREATE TABLE IF NOT EXISTS metadata_execution
  (
    execution_number INT NOT NULL AUTO_INCREMENT,
    stagingarea_execution_date DATE NOT NULL,
	starschema_execution_date DATE,
    stagingarea_result BOOLEAN NOT NULL,
    starschema_result BOOLEAN NULL,
    PRIMARY KEY (execution_number)
);

SET @s = IF(!@exists, "INSERT INTO alsm_stagingarea.metadata_execution (`stagingarea_execution_date`, `starschema_execution_date`, `stagingarea_result`, `starschema_result`) VALUES ('2015-04-01', '2015-04-01', true, true)", 'DO SLEEP(0)');
PREPARE stmt FROM @s;
EXECUTE stmt;


COMMIT;