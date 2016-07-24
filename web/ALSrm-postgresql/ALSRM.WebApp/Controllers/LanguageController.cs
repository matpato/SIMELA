using System.Globalization;
using System.Threading;
using System.Web;
using System.Web.Mvc;

namespace ALSRM.WebApp.Controllers
{
    public class LanguageController : Controller
    {
        public ActionResult Change(string languageAbbreviation, string returnuri)
        {
            if (languageAbbreviation != null)
            {
                Thread.CurrentThread.CurrentCulture = CultureInfo.CreateSpecificCulture(languageAbbreviation);
                Thread.CurrentThread.CurrentUICulture = new CultureInfo(languageAbbreviation);
            }

            var cookie = new HttpCookie("Language");
            cookie.Value = languageAbbreviation;
            Response.Cookies.Add(cookie);

            return Redirect(returnuri);
        }
    }
}