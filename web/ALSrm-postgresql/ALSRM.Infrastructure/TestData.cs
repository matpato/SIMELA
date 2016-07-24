using System;
using System.Collections.Generic;
using ALSRM.Domain.Models;

namespace ALSRM.Infrastructure
{
    // TODO APAGAR Para dados ficticios
    public class TestData
    {
        public static List<User> UsersList;
        public static List<Patient> PatientsList;
        public static List<Doctor> DoctorsList;
        public static List<Exam> ExamsList;
        public static List<List<ExamStep>> ExamStepList;

        public TestData()
        {
            var examStepPoint11 = new List<Point>();
            var examStepPoint12 = new List<Point>();
            var examStepPoint21 = new List<Point>();
            var examStepPoint22 = new List<Point>();
            var examStepPoint13 = new List<Point>();

            var patient1 = new Patient(1, "Xavier Pedro Linares", "92f5675e579a6689b601a9223a458325",
                "Qual o nome do seu melhor amigo de infância?", "0c50e40970f0b6ca97f8b3546cbf090f", "2015-194",
                ExamsList);
            var doctor1 = new Doctor(4, "João Raposa", "d1ff178253e3f8d94e6ce5b56be8fcc6",
                "Qual o nome do seu melhor amigo de infância?", "46eb4d515b00df60a845f5417354bb45");

            var examStep1 = new List<ExamStep>
            {
                new ExamStep(1, 1, "morning", "pending", 1, null, null, examStepPoint11),
                new ExamStep(1, 2, "afternoon", "pending", 1, null, null, examStepPoint12),
                new ExamStep(1, 3, "night", "pending", 1, null, null, examStepPoint13)
            };

            var examStep2 = new List<ExamStep>
            {
                new ExamStep(2, 1, "afternoon", "completed", 30, DateTime.Parse("2016-06-22T09:00:00Z"), DateTime.Parse("2016-06-22T09:30:00Z"), examStepPoint21),
                new ExamStep(2, 2, "night", "completed", 45, DateTime.Parse("2016-06-22T22:00:00Z"), DateTime.Parse("2016-06-22T22:45:00Z"), examStepPoint22)
            };

            ExamStepList = new List<List<ExamStep>> {examStep1, examStep2};

            ExamsList = new List<Exam>
            {
                new Exam(1, "ecg", "pending", DateTime.Parse("2016-06-18T00:00:00Z"),
                    DateTime.Parse("2016-06-30T00:00:00Z"), examStep1, patient1),
                new Exam(2, "emg", "completed", DateTime.Parse("2016-06-22T00:00:00Z"),
                    DateTime.Parse("2016-06-23T00:00:00Z"), examStep2, patient1)
            };

            UsersList = new List<User>
            {
                new User(1, "Xavier Pedro Linares", "92f5675e579a6689b601a9223a458325",
                    "Qual o nome do seu melhor amigo de infância?", "0c50e40970f0b6ca97f8b3546cbf090f", "patient"),
                new User(4, "João Raposa", "ba0f22cbd3da86009ac1dbe6a534d2d5",
                    "Qual o nome do seu melhor amigo de infância?", "46eb4d515b00df60a845f5417354bb45", "doctor")
            };

            PatientsList = new List<Patient> {patient1};

            DoctorsList = new List<Doctor> {doctor1};
        }
    }
}