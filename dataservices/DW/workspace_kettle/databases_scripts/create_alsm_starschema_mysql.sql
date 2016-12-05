#
# Create Star Schema
#

CREATE DATABASE IF NOT EXISTS `alsm_starschema` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

GRANT ALL ON alsm_starschema.* TO 'admin'@'localhost' IDENTIFIED BY 'alsmon';

USE `alsm_starschema`;

START TRANSACTION;

CREATE TABLE IF NOT EXISTS dim_date
  (
    keycol INT NOT NULL AUTO_INCREMENT,
    date_iso DATE NOT NULL,
    fulldate_en VARCHAR(50) NOT NULL,
	fulldate_pt VARCHAR(50) NOT NULL,
    dayofweek SMALLINT NOT NULL,
    dayofmonth SMALLINT NOT NULL,
    dayname_en VARCHAR(15) NOT NULL,
	dayname_pt VARCHAR(15) NOT NULL,
    weekofyear SMALLINT NOT NULL,
    monthnumber SMALLINT NOT NULL,
    monthname_en VARCHAR(25) NOT NULL,
	monthname_pt VARCHAR(25) NOT NULL,
    quarter SMALLINT NOT NULL,
    year SMALLINT NOT NULL,
	year_weekofyear CHAR(9) NOT NULL,
    year_monthnumber CHAR(9) NOT NULL,
	year_monthname_en VARCHAR(32) NOT NULL,
	year_monthname_pt VARCHAR(32) NOT NULL,
    year_quarter CHAR(9) NOT NULL,
	UNIQUE (date_iso),
	PRIMARY KEY (keycol)
);

CREATE TABLE IF NOT EXISTS dim_time
  (
    keycol INT NOT NULL AUTO_INCREMENT,
    fulltime24 TIME NOT NULL,
    fulltime12 CHAR(11) NOT NULL,
    hour24 SMALLINT NOT NULL,
    hour12 CHAR(5) NOT NULL,
    minute SMALLINT NOT NULL,
    second SMALLINT NOT NULL,
    period_en VARCHAR(25) NOT NULL,
	period_pt VARCHAR(25) NOT NULL,
	UNIQUE (fulltime24),
    PRIMARY KEY (keycol)
);


CREATE TABLE IF NOT EXISTS dim_patient
  (
    keycol INT NOT NULL AUTO_INCREMENT,
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
	UNIQUE (patient_id),
    PRIMARY KEY (keycol)
);

CREATE TABLE IF NOT EXISTS dim_muscle
  (
    keycol INT NOT NULL AUTO_INCREMENT,
    muscle_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    acronym VARCHAR(10) NOT NULL,
    side VARCHAR(25),
	UNIQUE (muscle_id),
    PRIMARY KEY (keycol)
);

CREATE TABLE IF NOT EXISTS fact_emg
  (
    emg_id BIGINT NOT NULL,
    date_keycol INT NOT NULL,
    time_keycol INT NOT NULL,
    patient_keycol INT NOT NULL,
    muscle_keycol INT NOT NULL,
    nr_peaks INT NOT NULL,
    area DOUBLE NOT NULL,
    high_pass VARCHAR(10) NOT NULL,
    low_pass VARCHAR(10) NOT NULL,
    electrode VARCHAR(50) NOT NULL,
    protocol VARCHAR(50) NOT NULL,
	UNIQUE (emg_id),
    PRIMARY KEY (date_keycol, time_keycol, patient_keycol, muscle_keycol),
    FOREIGN KEY (date_keycol) REFERENCES dim_date(keycol),
    FOREIGN KEY (time_keycol) REFERENCES dim_time(keycol),
    FOREIGN KEY (patient_keycol) REFERENCES dim_patient(keycol),
    FOREIGN KEY (muscle_keycol) REFERENCES dim_muscle(keycol)
);

CREATE TABLE IF NOT EXISTS fact_emg_data
  (
    emg_id BIGINT NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    PRIMARY KEY (emg_id, x, y),
    FOREIGN KEY (emg_id) REFERENCES fact_emg(emg_id)
);

COMMIT;