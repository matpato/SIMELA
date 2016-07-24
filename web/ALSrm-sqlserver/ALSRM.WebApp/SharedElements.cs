using System.Collections.Generic;
using ALSRM.WebApp.Resources;

namespace ALSRM.WebApp
{
    public class SharedElements
    {
        public static readonly string BaseUri = "http://alsrm.azurewebsites.net/"; //"http://localhost:15113/"; 

        public static List<string> StateOptions = new List<string> {ExamResources.Pending};
        public static List<string> TypeOptions = new List<string> {"ECG", "EMG", "Spo2"};

        public static List<string> DescriptionOptions = new List<string>
        {
            ExamResources.Morning,
            ExamResources.Afternoon,
            ExamResources.Night
        };

        // just because of translation
        public static List<string> SecurityQuestionEn = new List<string>
        {
            "Best childhood friends name?",
            "Name of first pet?",
            "First car brand?"
        };

        public static List<string> SecurityQuestionPt = new List<string>
        {
            "Melhor amigo de infância?",
            "Nome do primeiro animal de estimação?",
            "Marca do primeiro carro?"
        };

        public static string SecurityAnswerUndefined = "undefined";
        public static List<string> MuscleAbb = new List<string> {"AT", "FCR", "SCM"};
    }
}