using System.Linq;
using System.Security.Principal;
using System.Web;
using ALSRM.WebApp.Models;

namespace ALSRM.WebApp.Security
{
    public class CustomPrincipal : IPrincipal
    {
        private readonly Account Account;

        public CustomPrincipal(Account account)
        {
            // Session pessister time
            HttpContext.Current.Session.Timeout = 60; // 1hour
            Account = account;
            Identity = new GenericIdentity(account.UserName);
        }

        public bool IsInRole(string role)
        {
            var roles = role.Split(',');
            return roles.Any(r => r.Equals(Account.Role));
        }

        public IIdentity Identity { get; }
    }
}