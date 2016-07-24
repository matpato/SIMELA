
-- DROP TABLES

DROP TABLE IF EXISTS dbo."Point";
DROP TABLE IF EXISTS dbo."ExamStep";
DROP TABLE IF EXISTS dbo."Exam";
DROP TABLE IF EXISTS dbo."Muscle";
DROP TABLE IF EXISTS dbo."Patient";
DROP TABLE IF EXISTS dbo."Doctor";
DROP TABLE IF EXISTS dbo."User";


-- "User" Table

-- DROP TABLE dbo."User";

CREATE TABLE dbo."User"
(
  id bigserial NOT NULL,
  username varchar(500) NOT NULL,
  userpassword varchar(32) NOT NULL,
  securityQuestion varchar(250) NOT NULL,
  securityAnswer varchar(50) NOT NULL,
  descriminator varchar(25) NOT NULL DEFAULT 'patient',
  CONSTRAINT "pk_User" PRIMARY KEY (id),
  CONSTRAINT "ck_User_descriminator" CHECK (descriminator IN('patient', 'doctor'))
);

-- "Pacient" Table

--DROP TABLE dbo."Patient";

CREATE TABLE dbo."Patient"
(
  patientid varchar(30) NOT NULL,
  mac_bitalino varchar(17),
  CONSTRAINT "pk_UserId" PRIMARY KEY (id)
) INHERITS(dbo."User");

-- "Doctor" Table

--DROP TABLE dbo."Doctor";

CREATE TABLE dbo."Doctor"
(
  CONSTRAINT "pk_Doctor" PRIMARY KEY (id)
) INHERITS(dbo."User");

--"Muscle" Table

-- DROP TABLE dbo."Muscle";

CREATE TABLE dbo."Muscle"
(
id bigserial NOT NULL,
musclename varchar(50) NOT NULL,
abbreviation varchar(10) NOT NULL,
CONSTRAINT "pk_Muscle" PRIMARY KEY (id)
);

--"Exam" Table

-- DROP TABLE dbo."Exam";

CREATE TABLE dbo."Exam"
(
  examid bigserial NOT NULL,
  examtype varchar(25) NOT NULL,
  examstate varchar(25) NOT NULL,
  examinitialdate timestamp WITH TIME ZONE,
  examenddate timestamp WITH TIME ZONE,
  userid bigint NOT NULL,
  muscleid int NOT NULL,
  CONSTRAINT "pk_Exam" PRIMARY KEY (examid),
  CONSTRAINT "fk_UserId" FOREIGN KEY (userid) REFERENCES dbo."Patient" (id) ON DELETE CASCADE,
  CONSTRAINT "fk_Exam_muscle" FOREIGN KEY (muscleid) REFERENCES dbo."Patient" (id) ON DELETE CASCADE,
  CONSTRAINT "ck_Unique_Type_InitialDate" UNIQUE (examtype, examinitialdate, userid),
  CONSTRAINT "ck_Exam_state" CHECK (examstate IN('pending', 'completed', 'cancelled', 'running')),
  CONSTRAINT "ck_Exam_type" CHECK (examtype IN('ecg', 'emg', 'spo2'))
);

-- DROP TABLE dbo."ExamStep";

CREATE TABLE dbo."ExamStep"(
  examid bigint NOT NULL,
  stepnum int NOT NULL,
  description varchar(25) NOT NULL,
  state varchar(25) NOT NULL,
  time  numeric NOT NULL,
  initialdate timestamp WITH TIME ZONE,
  enddate timestamp WITH TIME ZONE,
  CONSTRAINT "ExamStep_pkey" PRIMARY KEY (examid, stepnum),
  CONSTRAINT "fk_ExamId" FOREIGN KEY (examid) REFERENCES dbo."Exam" (examid) ON DELETE CASCADE,
  CONSTRAINT "ck_ExamStep_state" CHECK (state IN ('pending','cancelled','completed')),
  CONSTRAINT "ck_ExamStep_desc" CHECK (description IN ('morning','afternoon','night'))
);

-- DROP TABLE dbo."Point";

CREATE TABLE dbo."Point"
(
  examid bigint NOT NULL,
  examstepnum bigint NOT NULL,
  x numeric NOT NULL,
  y numeric NOT NULL,
  CONSTRAINT "pk_Point" PRIMARY KEY (examid,examstepnum, x),
  CONSTRAINT "fk_ExamId" FOREIGN KEY (examid, examstepnum) REFERENCES dbo."ExamStep" (examid, stepnum) ON DELETE CASCADE
);



--SELECT * FROM dbo."Exam" as e JOIN dbo."ExamStep"as s ON e.examid = s.examid WHERE e.examid = 1
