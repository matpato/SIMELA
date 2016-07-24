Use ALSRM;

if(OBJECT_ID('Point') is not null) drop table Point
if(OBJECT_ID('ExamStep') is not null) drop table ExamStep
if(OBJECT_ID('Exam') is not null) drop table Exam
if(OBJECT_ID('Muscle') is not null) drop table Muscle
if(OBJECT_ID('Doctor') is not null) drop table Doctor
if(OBJECT_ID('Patient') is not null) drop table Patient
if(OBJECT_ID('Users') is not null) drop table Users

CREATE TABLE Users(
id	int identity(1,1),
username varchar(500) NOT NULL,
userpassword varchar(32) NOT NULL,
securityQuestion varchar(250) NOT NULL,
securityAnswer varchar(50) NOT NULL,
descriminator varchar(25) NOT NULL DEFAULT 'patient',
CONSTRAINT "pk_User" PRIMARY KEY (id),
CONSTRAINT "ck_User_descriminator" CHECK (descriminator IN('patient', 'doctor'))
)

CREATE TABLE Patient
(
  id int NOT NULL PRIMARY KEY,
  patientid varchar(30) NOT NULL,
  mac_bitalino varchar(17),
  CONSTRAINT "pk_UserId" FOREIGN KEY (id) REFERENCES Users(id)
)

CREATE TABLE Doctor
(
  id int NOT NULL PRIMARY KEY,
  CONSTRAINT "pk_Doctor" FOREIGN KEY (id) REFERENCES Users(id)
)

CREATE TABLE Muscle
(
id int identity(1,1) NOT NULL,
musclename varchar(50) NOT NULL,
abbreviation varchar(10) NOT NULL,
CONSTRAINT "pk_Muscle" PRIMARY KEY (id)
)

CREATE TABLE Exam
(
  examid int identity(1,1) NOT NULL,
  examtype varchar(25) NOT NULL,
  examstate varchar(25) NOT NULL,
  examinitialdate datetimeoffset,
  examenddate datetimeoffset,
  userid int NOT NULL,
  muscleid int,
  CONSTRAINT "pk_Exam" PRIMARY KEY (examid),
  CONSTRAINT "fk_UserId" FOREIGN KEY (userid) REFERENCES Patient(id) ON DELETE CASCADE,
  CONSTRAINT "fk_Exam_muscle" FOREIGN KEY (muscleid) REFERENCES Muscle (id) ON DELETE CASCADE,
  CONSTRAINT "ck_Unique_Type_InitialDate" UNIQUE (examtype, examinitialdate, userid),
  CONSTRAINT "ck_Exam_state" CHECK (examstate IN('pending', 'completed', 'cancelled', 'running')),
  CONSTRAINT "ck_Exam_type" CHECK (examtype IN('ecg', 'emg', 'spo2'))
)

CREATE TABLE ExamStep(
  examid int NOT NULL,
  stepnum int NOT NULL,
  description varchar(25) NOT NULL,
  state varchar(25) NOT NULL,
  time  int NOT NULL,
  initialdate datetimeoffset,
  enddate datetimeoffset,
  CONSTRAINT "ExamStep_pkey" PRIMARY KEY (examid, stepnum),
  CONSTRAINT "fk_ExamId" FOREIGN KEY (examid) REFERENCES Exam (examid) ON DELETE CASCADE,
  CONSTRAINT "ck_ExamStep_state" CHECK (state IN ('pending','cancelled','completed')),
  CONSTRAINT "ck_ExamStep_desc" CHECK (description IN ('morning','afternoon','night'))
)

CREATE TABLE Point
(
  examid int NOT NULL,
  examstepnum int NOT NULL,
  x numeric NOT NULL,
  y numeric NOT NULL,
  CONSTRAINT "pk_Point" PRIMARY KEY (examid,examstepnum, x),
  CONSTRAINT "fk_Point_ExamId" FOREIGN KEY (examid, examstepnum) REFERENCES dbo."ExamStep" (examid, stepnum) ON DELETE CASCADE
)