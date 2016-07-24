using System.Collections.Generic;

namespace ALRSM.Resources.Shared
{
    public class SharedFunctions
    {
        public const int MAX_TIME_MINUTES_A_DAY = 24 * 60;
        public const int MIN_TIME_MINUTES_A_DAY = 1;

        public static List<string> stateOptions = new List<string> { "pending", "completed", "cancelled"};
        public static List<string> typeOptions = new List<string> { "ECG", "EMG", "Spo2" };
        public static List<string> descriptionOptions = new List<string> { "morning", "afternoon", "night" };
        public static List<string> securityQuestions = new List<string> { "Best childhood friends name ?", "Name of first pet ?", "First car brand ?" };
        public static string securityAnswerUndefined = "undefined";
    }
}
