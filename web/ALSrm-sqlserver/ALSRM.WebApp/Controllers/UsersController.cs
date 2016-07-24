using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Web.Mvc;
using ALSRM.WebApp.Models;
using ALSRM.WebApp.Security;
using Newtonsoft.Json;

namespace ALSRM.WebApp.Controllers
{
    [RoutePrefix("Users")]
    public class UsersController : Controller
    {

        [Route]
        [CustomAuthorize(Roles = "Doctor")]
        // GET: Users
        public async Task<ActionResult> Index()
        {
            var users = new List<UserViewModels>();
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // HTTP GET
                HttpResponseMessage response = await client.GetAsync("odata/Users");

                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results = await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                    users = results.value.ToObject<List<UserViewModels>>();
                }
            }
            return View(users);
        }
    }
}
