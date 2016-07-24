--RESET Serial number : SELECT SETVAL ((SELECT pg_get_serial_sequence('"Exam"', 'examid')),1,false);

--##################### User INSERTS ########################################

--SELECT CURRVAL (pg_get_serial_sequence('"ExamStep"','stepnum'));

SELECT SETVAL ((SELECT pg_get_serial_sequence('dbo."User"', 'id')),1,false);

INSERT INTO dbo."Patient"(
            patientid, username, userpassword, securityquestion, securityanswer, descriminator, mac_bitalino)
    VALUES ('2015-194','Xavier Pedro Linares', '92f5675e579a6689b601a9223a458325', 'Best childhood friends name?', '0c50e40970f0b6ca97f8b3546cbf090f', 'patient','01:23:45:67:89:AB'); --123456 / Albertina

INSERT INTO dbo."Patient"(
             patientid,username, userpassword, securityquestion, securityanswer, descriminator, mac_bitalino)
    VALUES ('2014-445','Valentina Neuza Gómez Villena Balladares', '4dec80f55efe91694ce51ea9352ff898','Marca do primeiro carro?','fbc1e1bc4ca744ac288f107ac26cc398', 'patient', '99:88:45:25:77:CC'); --abcdef / Opel

INSERT INTO dbo."Patient"(
             patientid,username, userpassword, securityquestion, securityanswer, descriminator)
    VALUES ( '2014-433','Tânia Gutiérrez Almeida', '1d5c909ace34a4d47fe7df9720c3bf52', 'Nome do primeiro animal de estimação?', '16d69976142b0e80a5f3e55ec6c83c90', 'patient'); -- 000000 / Estrela


SELECT * FROM dbo."Patient";

--####################Doctor INSERTS ########################################

INSERT INTO dbo."Doctor"(
		username, userpassword, securityquestion, securityanswer, descriminator)
	VALUES ('João Raposa', 'ba0f22cbd3da86009ac1dbe6a534d2d5','Best childhood friends name?', '46eb4d515b00df60a845f5417354bb45', 'doctor' ); -- admin0 / Ricardo


SELECT * FROM dbo."Doctor";

SELECT * FROM dbo."User";

--##################### Muscle INSERTS ########################################
SELECT SETVAL ((SELECT pg_get_serial_sequence('dbo."Muscle"', 'id')),1,false);

INSERT INTO dbo."Muscle"(musclename, abbreviation) VALUES ('Anterior Tibialis', 'AT');

INSERT INTO dbo."Muscle"(musclename, abbreviation) VALUES ('Flexor Carpi Radialis', 'FCR');

INSERT INTO dbo."Muscle"(musclename, abbreviation) VALUES ('Sternocleido Mastoideus', 'SCM');

--##################### Exam INSERTS ########################################
SELECT SETVAL ((SELECT pg_get_serial_sequence('dbo."Exam"', 'examid')),1,false);

INSERT INTO dbo."Exam"(
            examtype, examstate, examinitialdate, examenddate, userid, muscleid)
    VALUES ('ecg', 'pending', '2016-04-24T00:00:00Z' , '2016-04-25T00:00:00Z', 1, 1);
    
INSERT INTO dbo."Exam"(
            examtype, examstate, examinitialdate, examenddate, userid, muscleid)
    VALUES ('emg', 'completed', '2016-05-20T09:00:00Z', '2016-05-20T09:20:00Z', 2, 1);
    
INSERT INTO dbo."Exam"(
            examtype, examstate, examinitialdate, examenddate, userid, muscleid)
    VALUES ('ecg', 'pending','2016-08-16T00:00:00Z', '2016-08-17T00:00:00Z', 2, 2);
    
INSERT INTO dbo."Exam"(
            examtype, examstate, examinitialdate, examenddate, userid, muscleid)
    VALUES ('spo2', 'cancelled', '2016-02-12T10:00:00Z', '2016-02-12T12:00:00Z', 3, 3);

SELECT * FROM dbo."Exam";

--##################### ExamStep INSERTS ########################################

INSERT INTO dbo."ExamStep"(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (1,'morning', 'pending', 10, null, null, 1);

INSERT INTO dbo."ExamStep"(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (2, 'afternoon', 'pending', 25, null, null, 1);

INSERT INTO dbo."ExamStep"(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (1, 'morning', 'completed', 20, '2016-05-20 09:00:00', '2016-05-20 09:20:00', 2);

INSERT INTO dbo."ExamStep"(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (1, 'night', 'cancelled', 120, null, null, 3);
    
INSERT INTO dbo."ExamStep"(
            stepnum, description, state, "time", initialdate, enddate, examid)
    VALUES (2, 'night', 'pending', 10, null, null, 3);

SELECT * FROM dbo."ExamStep";

--##################### Point INSERTS ########################################

INSERT INTO dbo."Point"(
            x, y, examid, examstepnum)
    VALUES (1, 1.2, 2, 1);
    
INSERT INTO dbo."Point"(
            x, y, examid, examstepnum)
    VALUES (2, 0.8, 2, 1);

INSERT INTO dbo."Point"(
            x, y, examid, examstepnum)
    VALUES (3, 0.7, 2, 1);

INSERT INTO dbo."Point"(
            x, y, examid, examstepnum)
    VALUES (4, -1, 2, 1);

INSERT INTO dbo."Point"(
            x, y, examid, examstepnum)
    VALUES (5, -1.2, 2, 1);

INSERT INTO dbo."Point"(
            x, y, examid, examstepnum)
    VALUES (6, 0, 2, 1);

SELECT * FROM dbo."Point";

--DELETE FROM dbo."Point" WHERE examid = 1

-- UPDATE dbo."Exam"
-- SET examstate = 'pending'
-- WHERE examid = 1;


SELECT e.examtype, e.examstate, u.username, m.abbreviation FROM dbo."Exam" as e JOIN dbo."Muscle" as m ON e.muscleid = m.id JOIN dbo."User" as u ON e.userid = u.id


