using System.Web;

namespace ALSRM.WebApp.Security
{
    public static class SessionPresister
    {
        private static readonly string usernameSessionvar = "username";
        private static readonly string useridSessionvar = "userid";
        private static readonly string roleSessionvar = "role";
        private static readonly string tokenapiSessionvar = "tokenapi";

        public static string UserName
        {
            get
            {
                if (HttpContext.Current == null)
                    return string.Empty;
                var sessionVar = HttpContext.Current.Session[usernameSessionvar];
                if (sessionVar != null)
                    return sessionVar as string;
                return null;
            }
            set { HttpContext.Current.Session[usernameSessionvar] = value; }
        }

        public static string UserId
        {
            get
            {
                if (HttpContext.Current == null)
                    return string.Empty;
                var sessionVar = HttpContext.Current.Session[useridSessionvar];
                if (sessionVar != null)
                    return sessionVar as string;
                return null;
            }
            set { HttpContext.Current.Session[useridSessionvar] = value; }
        }

        public static string Role
        {
            get
            {
                if (HttpContext.Current == null)
                    return string.Empty;
                var sessionVar = HttpContext.Current.Session[roleSessionvar];
                if (sessionVar != null)
                    return sessionVar as string;
                return null;
            }
            set { HttpContext.Current.Session[roleSessionvar] = value; }
        }

        public static string TokenApi
        {
            get
            {
                if (HttpContext.Current == null)
                    return string.Empty;
                var sessionVar = HttpContext.Current.Session[tokenapiSessionvar];
                if (sessionVar != null)
                    return sessionVar as string;
                return null;
            }
            set { HttpContext.Current.Session[tokenapiSessionvar] = value; }
        }
    }
}