using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Mvc;
using ALSRM.Domain.Models;
using ALSRM.WebApp.Models;
using ALSRM.WebApp.Security;
using Newtonsoft.Json;

namespace ALSRM.WebApp.Controllers
{
    public class HomeController : Controller
    {
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> Index()
        {
            var hpm = new HomeProfileModel();

            if (SessionPresister.Role.Equals("Doctor"))
            {
                return RedirectToAction("Index", "Exams");
            }

            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // HTTP GET
                var uri = $"odata/Users({SessionPresister.UserId})?$expand=Exams/ExamSteps,Exams/ThisMuscle";
                var response = await client.GetAsync(uri);

                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results = await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                    List<Patient> resultList = results.value.ToObject<List<Patient>>();
                    var u = resultList.FirstOrDefault();

                    hpm.Exams1 =
                        u?.Exams.Where(e => e.ExamEndDate.CompareTo(DateTime.Now) >= 0)
                            .OrderBy(e => e.ExamInitialDate)
                            .Take(10);
                    hpm.Exams2 =
                        u?.Exams.Where(e => e.ExamEndDate.CompareTo(DateTime.Now) < 0)
                            .OrderBy(e => e.ExamEndDate)
                            .Take(10);

                    return View(hpm);
                }
            }


            return View(hpm);
        }

        //GET: /Home/About
        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }
    }
}