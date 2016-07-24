using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Mvc;
using System.Web.Security;
using ALRSM.Resources.Shared;
using ALSRM.Domain.Models;
using ALSRM.WebApp.Models;
using ALSRM.WebApp.Resources;
using ALSRM.WebApp.Security;
using Newtonsoft.Json;

namespace ALSRM.WebApp.Controllers
{
    public class AccountController : Controller
    {
        // GET: Account
        public ActionResult Index()
        {
            return RedirectToAction("DashBoard");
        }

        [HttpGet]
        public ActionResult Login()
        {
            return View();
        }

        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Login(LoginViewModel model, string returnUrl)
        {
            if (ModelState.IsValid)
            {
                using (var client = new HttpClient())
                {
                    var account = new Account
                    {
                        UserId = model.UserId,
                        UserPassword = model.Password
                    };
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var request = new HttpRequestMessage(HttpMethod.Post, "odata/security/token");

                    var keyValues = new List<KeyValuePair<string, string>>();
                    keyValues.Add(new KeyValuePair<string, string>("grant_type", "password"));
                    keyValues.Add(new KeyValuePair<string, string>("username", model.UserId.ToString()));
                    keyValues.Add(new KeyValuePair<string, string>("password", model.Password));

                    request.Content = new FormUrlEncodedContent(keyValues);
                    var response = await client.SendAsync(request);

                    if (response.IsSuccessStatusCode)
                    {
                        var jsonString = await response.Content.ReadAsStringAsync();
                        var results =
                            await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                        account.TokenApi = results.access_token;

                        // Get info from User to set on Account

                        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                            account.TokenApi);
                        client.DefaultRequestHeaders.Accept.Clear();

                        // HTTP GET
                        response = await client.GetAsync("odata/Users(" + model.UserId + ")");
                        if (response.IsSuccessStatusCode)
                        {
                            jsonString = await response.Content.ReadAsStringAsync();
                            results =
                                await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                            account.Role = results.value[0]["odata.type"].ToString().Replace("ALSRM.Domain.Models.", "");
                            account.UserName = results.value[0]["Name"];

                            SessionPresister.UserId = account.UserId.ToString();
                            SessionPresister.UserName = account.UserName;
                            SessionPresister.TokenApi = account.TokenApi;
                            SessionPresister.Role = account.Role;

                            var newUser = new CustomPrincipal(account);
                            HttpContext.User = newUser;

                            return RedirectToLocal(returnUrl);
                        }
                    }
                }
            }
            ViewBag.Error = AccountResources.SecurityAnswerError;
            return View(model);
        }

        // POST: /Account/Logout
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult LogOut()
        {
            Session.Abandon();
            return RedirectToAction("Index", "Home");
        }

        [HttpGet]
        [AllowAnonymous]
        [CustomAuthorize(Roles = "Doctor")]
        public ActionResult Register()
        {
            ViewBag.SecurityQuestion = new SelectList(SharedFunctions.securityQuestions);
            return View();
        }

        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> Register(GetRegisterViewModel model)
        {
            if (ModelState.IsValid)
            {
                var patient = new PostRegisterViewModel(model);
                // password generated automatically
                var psw = Membership.GeneratePassword(10, 0);

                patient.Password = psw;
                patient.ConfirmPassword = psw;
                patient.SecurityQuestion = SharedElements.SecurityQuestionEn.FirstOrDefault();
                patient.SecurityAnswer = SharedElements.SecurityAnswerUndefined;
                patient.Mac_Bitalino = model.Mac_Bitalino;

                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var response = await client.PostAsJsonAsync("odata/Users", patient);
                    if (response.IsSuccessStatusCode)
                    {
                        var jsonString = await response.Content.ReadAsStringAsync();
                        var results =
                            await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                        patient.Id = results.Id;
                        ViewBag.QrCode =
                            Account.GenerateQrCode(Account.GenerateQrCodeString(patient.Id, model.Mac_Bitalino));

                        return View("SuccessfulyRegister", patient);
                    }
                }
            }
            ViewBag.Error = "An error occurred!";
            ViewBag.SecurityQuestion = new SelectList(SharedFunctions.securityQuestions);
            return View();
        }

        [HttpGet]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        //[ValidateAntiForgeryToken]
        public async Task<ActionResult> DashBoard()
        {
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();

                // HTTP GET
                var response = await client.GetAsync("odata/Users(" + SessionPresister.UserId + ")");
                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results =
                        await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                    var user = new UserDashboardModel();
                    user.Id = results.value[0]["Id"];
                    user.Name = results.value[0]["Name"];
                    string type = results.value[0]["odata.type"].ToString().Replace("ALSRM.Domain.Models.", "");

                    if (type.Equals("Patient"))
                    {
                        user.PatientId = results.value[0]["PatientId"];
                        user.Mac_Bitalino = results.value[0]["Mac_Bitalino"];
                        ViewBag.QrCode =
                            Account.GenerateQrCode(Account.GenerateQrCodeString(user.Id, user.Mac_Bitalino));
                    }

                    return View(user);
                }
            }

            return RedirectToAction("Index", "Home");
        }

        [HttpGet]
        [AllowAnonymous]
        public ActionResult ResetPassword()
        {
            return View();
        }


        [HttpPost]
        [AllowAnonymous]
        public async Task<ActionResult> ResetPassword(ResetPasswordModel model, string returnUrl)
        {
            if (ModelState.IsValid)
            {
                using (var client = new HttpClient())
                {
                    var user = new ResetPasswordModelApi();
                    user.SecurityAnswer = model.SecurityAnswer;

                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var response = await client.PostAsJsonAsync("odata/Users(" + model.Id + ")/ResetPassword", user);
                    if (response.IsSuccessStatusCode)
                    {
                        var jsonString = await response.Content.ReadAsStringAsync();
                        var results =
                            await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                        string s = results.value.ToString();                      
                        ViewBag.NewPassword = s.Replace("The password has been reset and the new is:",
                            RecoveryPasswordResources.SuccessMessage);

                        return View();
                    }
                }
            }
            ViewBag.Error = AccountResources.SecurityAnswerError;
            return View();
        }

        [HttpGet]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> ChangePassword()
        {
            var sq = await GetSecurityQuestion();

            if (!string.IsNullOrEmpty(sq))
            {
                var culture = CultureInfo.CurrentCulture;

                if (culture.Name.Equals("pt"))
                {
                    var idx = SharedElements.SecurityQuestionEn.FindIndex(s => s.Equals(sq));
                    ViewBag.SecurityQuestion = SharedElements.SecurityQuestionPt[idx];
                }
                else
                {
                    ViewBag.SecurityQuestion = sq;
                }
            }

            return View();
        }


        [HttpPost]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> ChangePassword(ChangePasswordModel model, string returnUrl)
        {
            var sq = await GetSecurityQuestion();

            if (!string.IsNullOrEmpty(sq))
            {
                var culture = CultureInfo.CurrentCulture;

                if (culture.Name.Equals("pt"))
                {
                    var idx = SharedElements.SecurityQuestionEn.FindIndex(s => s.Equals(sq));
                    ViewBag.SecurityQuestion = SharedElements.SecurityQuestionPt[idx];
                }
                else
                {
                    ViewBag.SecurityQuestion = sq;
                }
                if (ModelState.IsValid)
                {
                }
                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                        SessionPresister.TokenApi);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var response =
                        await
                            client.PostAsJsonAsync("odata/Users(" + SessionPresister.UserId + ")/ChangePassword", model);
                    if (response.IsSuccessStatusCode)
                    {
                        ViewBag.Message = AccountResources.ChangedPasswordSuccess;
                        return View();
                    }
                }
            }

            ViewBag.Error = "Account's Invalid";
            return View();
        }


        [HttpGet]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public ActionResult ChangeMacBitAlino()
        {
            return View();
        }

        [HttpPost]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> ChangeMacBitAlino(ChangeMacBitalinoModel model, string returnUrl)
        {
            if (ModelState.IsValid)
            {
                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                        SessionPresister.TokenApi);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var response =
                        await
                            client.PostAsJsonAsync("odata/Users(" + SessionPresister.UserId + ")/ChangeMacBitalino",
                                model);
                    if (response.IsSuccessStatusCode)
                    {
                        return RedirectToAction("DashBoard");
                    }
                }
            }
            ViewBag.Error = AccountResources.ErrorMacBitAlino;
            return View();
        }

        [HttpGet]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> ChangeMacBitAlinoToPatient()
        {
            ICollection<User> users = await ExamsController.GetPatientsFromApiAsync();

            ViewBag.UserId = new SelectList(users, "Id", "Id");
            return View();
        }

        [HttpPost]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> ChangeMacBitAlinoToPatient(ChangeMacBitalinoToPatientModel model,
            string returnUrl)
        {
            ICollection<User> users = await ExamsController.GetPatientsFromApiAsync();
            ViewBag.UserId = new SelectList(users, "Id", "Id");

            if (ModelState.IsValid)
            {
                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                        SessionPresister.TokenApi);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var m = new ChangeMacBitalinoModel();
                    m.Mac_Bitalino = model.Mac_Bitalino;
                    var response =
                        await client.PostAsJsonAsync("odata/Users(" + model.UserId + ")/ChangeMacBitalino", m);
                    if (response.IsSuccessStatusCode)
                    {
                        ViewBag.Message = AccountResources.ChagedMacSuccess;
                        return View();
                    }
                }
            }
            ViewBag.Error = AccountResources.ErrorMacBitAlino;
            return View();
        }


        [HttpGet]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public ActionResult ChangeSecurityAnswer()
        {
            var culture = CultureInfo.CurrentCulture;

            if (culture.Name.Equals("pt"))
                ViewBag.SecurityQuestion = new SelectList(SharedElements.SecurityQuestionPt);
            else
                ViewBag.SecurityQuestion = new SelectList(SharedElements.SecurityQuestionEn);

            return View();
        }

        [HttpPost]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> ChangeSecurityAnswer(ChangeSecuritAnswerModel model, string returnUrl)
        {
            var culture = CultureInfo.CurrentCulture;

            // Data base in english for convention
            if (culture.Name.Equals("pt"))
            {
                ViewBag.SecurityQuestion = new SelectList(SharedElements.SecurityQuestionPt);
                var idx = SharedElements.SecurityQuestionPt.FindIndex(s => s.Equals(model.SecurityQuestion));
                model.SecurityQuestion = SharedElements.SecurityQuestionEn[idx];
            }
            else
            {
                ViewBag.SecurityQuestion = new SelectList(SharedElements.SecurityQuestionEn);
            }

            if (ModelState.IsValid)
            {
                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                        SessionPresister.TokenApi);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                    // HTTP POST 
                    var response =
                        await
                            client.PostAsJsonAsync(
                                "odata/Users(" + SessionPresister.UserId + ")/ChangeSecurityQuestion", model);
                    if (response.IsSuccessStatusCode)
                    {
                        ViewBag.Message = AccountResources.ChangedSecurityAnswerSuccess;
                        return View();
                    }
                }
            }
            ViewBag.Error = AccountResources.SecurityAnswerError;

            return View();
        }

        private ActionResult RedirectToLocal(string returnUrl)
        {
            if (Url.IsLocalUrl(returnUrl))
            {
                return Redirect(returnUrl);
            }
            return RedirectToAction("Index", "Home");
        }

        #region Auxiliar Methods

        private async Task<string> GetSecurityQuestion()
        {
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // HTTP GET
                var response = await client.GetAsync("odata/Users(" + SessionPresister.UserId + ")");
                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results =
                        await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));

                    return results.value[0]["SecurityQuestion"];
                }
                return null;
            }
        }

        #endregion
    }
}