using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Mvc;
using ALRSM.Resources.Shared;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using ALSRM.WebApp.Models;
using ALSRM.WebApp.Security;
using Newtonsoft.Json;

namespace ALSRM.WebApp.Controllers
{
    [RoutePrefix("Exams")]
    public class ExamsController : Controller
    {
        private readonly IExamService _serviceExam;

        public ExamsController(IExamService serviceExam)
        {
            _serviceExam = serviceExam;
        }

        // GET: Exam
        [Route]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> Index()
        {
            var exams = new List<Exam>();
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // HTTP GET
                HttpResponseMessage response;
                if (SessionPresister.Role.Equals("Doctor"))
                    response = await client.GetAsync("odata/Exams?$expand=ThisUser,ThisMuscle");
                else
                {
                    response =
                        await
                            client.GetAsync(
                                $"odata/Exams?$expand=ThisUser,ThisMuscle&$filter=UserId eq {SessionPresister.UserId}");
                }

                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results = await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                    exams = results.value.ToObject<List<Exam>>();
                }
            }
            return View(exams);
        }


        // GET: Exam/5/Details
        [Route("{id:int}/Details")]
        [CustomAuthorize(Roles = "Doctor,Patient")]
        public async Task<ActionResult> Details(int? id)
        {
            Exam exam = null;
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // HTTP GET
                var uri = $"odata/Exams({id})?$expand=ThisUser,ExamSteps,ThisMuscle";
                var response = await client.GetAsync(uri);

                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results = await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                    List<Exam> resultList = results.value.ToObject<List<Exam>>();
                    exam = resultList.FirstOrDefault();

                    // put correct languange to show on properties
                    if (exam != null)
                    {
                        exam.ExamType = exam.ExamType.ToUpper();
                        ExamViewModels.ChangeLanguage(exam);
                    }
                }
            }

            if (exam == null)
            {
                return HttpNotFound();
            }

            return View(exam);
        }


        // GET: Exam/Create
        [Route("Create")]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> Create()
        {
            ICollection<User> users = await GetPatientsFromApiAsync();

            ViewBag.ExamState = new SelectList(SharedElements.StateOptions);
            ViewBag.ExamType = new SelectList(SharedElements.TypeOptions);
            ViewBag.Description = new SelectList(SharedElements.DescriptionOptions);
            ViewBag.State = new SelectList(SharedElements.StateOptions);
            ViewBag.UserId = new SelectList(users, "Id", "Id");
            ViewBag.MuscleId = new SelectList(SharedElements.MuscleAbb);
            return View();
        }

        // POST: Exam/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        //[ValidateAntiForgeryToken]
        [Route("Create")]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<int> Create(ExamViewModels exam)
        {
            if (ModelState.IsValid)
            {
                exam.ExamInitialDate = exam.ExamInitialDate.ToLocalTime();
                exam.ExamEndDate = exam.ExamEndDate.ToLocalTime();
                if (exam.MuscleAbb != null)
                // define correct id for muscle
                    exam.MuscleId = SharedElements.MuscleAbb.FindIndex(s => s.Equals(exam.MuscleAbb)) + 1;

                // Translate if necessary for api accept data
                exam.ValidateLanguage();

                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                        SessionPresister.TokenApi);
                    client.DefaultRequestHeaders.Accept.Clear();

                    // HTTP POST 
                    //var response = await client.PostAsJsonAsync("odata/Exams", exam);
                    var examPost = new ExamPostModel(exam);
                    var response = await client.PostAsJsonAsync("odata/Exams", examPost);
                    if (response.IsSuccessStatusCode)
                    {
                        var jsonString = await response.Content.ReadAsStringAsync();
                        var results =
                            await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                        examPost = results.ToObject<ExamPostModel>();
                        return examPost.ExamId;
                    }
                }
            }
            ICollection<User> users = await GetPatientsFromApiAsync();

            ViewBag.ExamState = new SelectList(SharedFunctions.stateOptions);
            ViewBag.ExamType = new SelectList(SharedFunctions.typeOptions);
            ViewBag.UserId = new SelectList(users, "Id", "Id");
            ViewBag.MuscleId = new SelectList(SharedElements.MuscleAbb);
            ViewBag.Description = new SelectList(SharedFunctions.descriptionOptions);
            ViewBag.State = new SelectList(SharedFunctions.stateOptions);

            return -1;
        }

        // GET: Exam/Create

        [Route("{id:int}/ExamSteps/Create")]
        [CustomAuthorize(Roles = "Doctor")]
        public ActionResult CreateExamStep()
        {
            ViewBag.State = new SelectList(SharedElements.StateOptions);
            ViewBag.Description = new SelectList(SharedElements.DescriptionOptions);
            return View("~/Views/ExamSteps/Create.cshtml");
        }

        // POST: Exam/5/ExamSteps/Create 
        [HttpPost]
        [ValidateAntiForgeryToken]
        [Route("{id:int}/ExamSteps/Create")]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> CreateExamStep(int id,
            [Bind(Include = "Description,State,Time")] ExamStepModels examStep)
        {
            if (ModelState.IsValid)
            {
                examStep.ExamId = id;

                // Translate if necessary some properties for api accept data
                examStep.ValidateLanguage();


                using (var client = new HttpClient())
                {
                    client.BaseAddress = new Uri(SharedElements.BaseUri);
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                        SessionPresister.TokenApi);
                    client.DefaultRequestHeaders.Accept.Clear();
                    client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));


                    // HTTP POST 
                    string uri = $"odata/Exams({examStep.ExamId})/ExamSteps";
                    var response = await client.PostAsJsonAsync(uri, examStep);
                    if (response.IsSuccessStatusCode)
                        return RedirectToAction("Details");
                }
            }

            ViewBag.ExamId = new SelectList(_serviceExam.GetExams(), "ExamId", "ExamId", examStep.ExamId);
            return View("~/Views/ExamSteps/Create.cshtml", examStep);
        }

        // POST: Exam/5/ExamSteps/2/Cancel
        [HttpPost]
        [Route("{id:int}/Cancel")]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> CancelExam(int id)
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
                    string uri = $"odata/Exams({id})/Cancel";
                    var response = await client.PostAsync(uri, null); // no content é needed
                    if (response.IsSuccessStatusCode)
                        return RedirectToAction("Index");
                }
            }
            return RedirectToAction("Index");
        }

        // POST: Exam/5/ExamSteps/2/Cancel
        [HttpPost]
        [Route("{id:int}/ExamSteps/{stepnum:int}/Cancel")]
        [CustomAuthorize(Roles = "Doctor")]
        public async Task<ActionResult> CancelExamStep(int id, int stepnum)
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
                    string uri = $"odata/Exams({id})/ExamSteps({stepnum})/Cancel";
                    var response = await client.PostAsync(uri, null); // no content é needed
                    if (response.IsSuccessStatusCode)
                    {
                        return RedirectToAction("Details");
                    }
                }
            }
            return RedirectToAction("Details");
        }


        /* Othrer endpoints not used for now
        // POST: Exams/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> Edit([Bind(Include = "ExamId,ExamType,ExamState,ExamInitialDate,ExamEndDate,UserId")] Exam exam)
        {
            if (ModelState.IsValid)
            {
                db.Entry(exam).State = EntityState.Modified;
                await db.SaveChangesAsync();
                return RedirectToAction("Index");
            }
            ViewBag.UserId = new SelectList(db.Users, "Id", "Name", exam.UserId);
            return View(exam);
        }

        // GET: Exams/Delete/5
        public async Task<ActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Exam exam = await db.Exams.FindAsync(id);
            if (exam == null)
            {
                return HttpNotFound();
            }
            return View(exam);
        }

          // GET: Exam/5/Edit
        [Route("{id:int}/Edit")]
        public async Task<ActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Exam exam = await _serviceExam.FindAsync((int)id);
            if (exam == null)
            {
                return HttpNotFound();
            }
            ViewBag.UserId = new SelectList(db.Users, "Id", "Name", exam.UserId);
            return View(exam);
        }
        

        // POST: Exams/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<ActionResult> DeleteConfirmed(int id)
        {
            Exam exam = await db.Exams.FindAsync(id);
            db.Exams.Remove(exam);
            await db.SaveChangesAsync();
            return RedirectToAction("Index");
        }*/

        #region Auxiliar Methods

        public static async Task<List<User>> GetPatientsFromApiAsync()
        {
            using (var client = new HttpClient())
            {
                client.BaseAddress = new Uri(SharedElements.BaseUri);
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer",
                    SessionPresister.TokenApi);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // HTTP GET
                var response = await client.GetAsync("odata/Users");

                if (response.IsSuccessStatusCode)
                {
                    var jsonString = await response.Content.ReadAsStringAsync();
                    var results = await Task.Factory.StartNew(() => JsonConvert.DeserializeObject<dynamic>(jsonString));
                    return results.value.ToObject<List<User>>();
                }
                return null;
            }
        }

        #endregion
    }
}