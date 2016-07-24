Use ALSRM;

INSERT INTO Users(username,userpassword,securityQuestion,securityAnswer,descriminator) values('Xavier Pedro Linares','92f5675e579a6689b601a9223a458325','Best childhood friends name?', '0c50e40970f0b6ca97f8b3546cbf090f','patient')
INSERT INTO Patient(
            id, patientid, mac_bitalino)
VALUES (SCOPE_IDENTITY(),'2015-194','01:23:45:67:89:AB'); --123456 / Albertina

--##################### User INSERTS ########################################

INSERT INTO Users(
             username, userpassword, securityquestion, securityanswer, descriminator)
    VALUES ('Valentina Neuza Gómez Villena Balladares', '4dec80f55efe91694ce51ea9352ff898','First car brand?','fbc1e1bc4ca744ac288f107ac26cc398', 'patient'); --abcdef / Opel

INSERT INTO Patient(
             id,patientid, mac_bitalino)
    VALUES (SCOPE_IDENTITY(), '2014-445','99:88:45:25:77:CC'); --abcdef / Opel


INSERT INTO Users(
             username, userpassword, securityquestion, securityanswer, descriminator)
    VALUES ( 'Tânia Gutiérrez Almeida', '1d5c909ace34a4d47fe7df9720c3bf52', 'Name of first pet?', '16d69976142b0e80a5f3e55ec6c83c90', 'patient'); -- 000000 / Estrela


INSERT INTO Patient(
             id,patientid)
    VALUES ( SCOPE_IDENTITY(),'2014-433'); -- 000000 / Estrela


INSERT INTO Users(
		username, userpassword, securityquestion, securityanswer, descriminator)
	VALUES ('João Raposa', 'ba0f22cbd3da86009ac1dbe6a534d2d5','Best childhood friends name?', '46eb4d515b00df60a845f5417354bb45', 'doctor' ); -- admin0 / Ricardo

INSERT INTO Doctor(
		id)
	VALUES (SCOPE_IDENTITY()); -- admin0 / Ricardo

SELECT * FROM Doctor;
SELECT * FROM Patient;
SELECT * FROM Users;

--##################### Muscle INSERTS ########################################

INSERT INTO Muscle(musclename, abbreviation) VALUES ('Anterior Tibialis', 'AT');

INSERT INTO Muscle(musclename, abbreviation) VALUES ('Flexor Carpi Radialis', 'FCR');

INSERT INTO Muscle(musclename, abbreviation) VALUES ('Sternocleido Mastoideus', 'SCM');

SELECT * FROM Muscle;

--##################### Exam INSERTS ########################################

INSERT INTO Exam(
            examtype, examstate, examinitialdate, examenddate, userid)
    VALUES ('ecg', 'pending', '2016-04-24T00:00:00Z' , '2016-04-25T00:00:00Z', 1);
    
INSERT INTO Exam(
            examtype, examstate, examinitialdate, examenddate, userid, muscleid)
    VALUES ('emg', 'completed', '2016-05-20T09:00:00Z', '2016-05-20T09:20:00Z', 2, 1);
    
INSERT INTO Exam(
            examtype, examstate, examinitialdate, examenddate, userid)
    VALUES ('ecg', 'pending','2016-08-16T00:00:00Z', '2016-08-17T00:00:00Z', 2);
    
INSERT INTO Exam(
            examtype, examstate, examinitialdate, examenddate, userid, muscleid)
    VALUES ('spo2', 'cancelled', '2016-02-12T10:00:00Z', '2016-02-12T12:00:00Z', 3, 3);

SELECT * FROM Exam;

--##################### ExamStep INSERTS ########################################

INSERT INTO ExamStep(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (1,'morning', 'pending', 10, null, null, 1);

INSERT INTO ExamStep(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (2, 'afternoon', 'pending', 25, null, null, 1);

INSERT INTO ExamStep(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (1, 'morning', 'completed', 20, '2016-05-20 09:00:00', '2016-05-20 09:20:00', 2);

INSERT INTO ExamStep(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (1, 'night', 'cancelled', 120, null, null, 3);
    
INSERT INTO ExamStep(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (2, 'night', 'pending', 10, null, null, 3);

SELECT * FROM ExamStep;

--##################### Point INSERTS ########################################

INSERT INTO Point(
            x, y, examid, examstepnum)
    VALUES (1, 1.2, 2, 1);
    
INSERT INTO Point(
            x, y, examid, examstepnum)
    VALUES (2, 0.8, 2, 1);

INSERT INTO Point(
            x, y, examid, examstepnum)
    VALUES (3, 0.7, 2, 1);

INSERT INTO Point(
            x, y, examid, examstepnum)
    VALUES (4, -1, 2, 1);

INSERT INTO Point(
            x, y, examid, examstepnum)
    VALUES (5, -1.2, 2, 1);

INSERT INTO Point(
            x, y, examid, examstepnum)
    VALUES (6, 0, 2, 1);

SELECT * FROM Point;
