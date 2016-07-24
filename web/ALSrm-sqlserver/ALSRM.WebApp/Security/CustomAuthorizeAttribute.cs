using System.Web.Mvc;
using System.Web.Routing;
using ALSRM.WebApp.Models;

namespace ALSRM.WebApp.Security
{
    public class CustomAuthorizeAttribute : AuthorizeAttribute
    {
        public override void OnAuthorization(AuthorizationContext filterContext)
        {
            if (string.IsNullOrEmpty(SessionPresister.UserName))
                filterContext.Result =
                    new RedirectToRouteResult(new RouteValueDictionary(new {controller = "Account", action = "Login"}));
            else
            {
                var mp =
                    new CustomPrincipal(new Account(SessionPresister.UserName, SessionPresister.TokenApi,
                        SessionPresister.Role));
                if (!mp.IsInRole(Roles))
                    filterContext.Result =
                        new RedirectToRouteResult(
                            new RouteValueDictionary(new {controller = "AccessDenied", action = "Index"}));
            }
        }
    }
}