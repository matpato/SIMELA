using System;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using System.Web.Http.OData;
using ALRSM.Resources.Resources;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using Newtonsoft.Json.Linq;

namespace ALSRM.Api2.Controllers
{
    public class ExamsController : ODataController
    {
        private readonly IExamService _serviceExam;
        private readonly IExamStepService _serviceExamStep;
        private readonly IUserService _serviceUser;

        public ExamsController(IExamService serviceExam, IUserService serviceUser, IExamStepService serviceExamStep)
        {
            _serviceExam = serviceExam;
            _serviceUser = serviceUser;
            _serviceExamStep = serviceExamStep;
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                _serviceExam.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ExamExists(int key)
        {
            return _serviceExam.GetExams().Count(e => e.ExamId == key) > 0;
        }


        // GET: odata/Exams
        [Authorize]
        [EnableQuery]
        public IQueryable<Exam> GetExams()
        {
            return _serviceExam.GetExams();
        }

        // POST: odata/Exams
        [Authorize]
        public async Task<IHttpActionResult> Post([FromBody] Exam exam)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            if (!_serviceUser.VerifyIsDoctor(int.Parse(User.Identity.Name)))
                return BadRequest(Errors.InvalidCredentials);

            try
            {
                await _serviceExam.RegisterAsync(exam);
            }
            catch (DbUpdateException)
            {
                if (ExamExists(exam.ExamId))
                {
                    return Conflict();
                }
                throw;
            }

            return Created(exam);
        }


        // GET: odata/Exams(5)
        [Authorize]
        [ResponseType(typeof(IQueryable<BaseExam>))]
        [EnableQuery(MaxExpansionDepth = 2)]
        // The MaxExpansionDepth guarantees that there is no access to ExamStepPoints 
        public IHttpActionResult GetExam([FromODataUri] int key)
        {
            IQueryable<Exam> exam;
            try
            {
                exam = _serviceExam.Get(key);
                Exam ex = exam.FirstOrDefault();
                //     If is the patient himself or a doctor to get access to profile id
                if (ex != null && (!_serviceUser.VerifyIsDoctor(int.Parse(User.Identity.Name)) && ex.UserId != int.Parse(User.Identity.Name)))
                    return BadRequest(Errors.InvalidCredentials);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(exam);
        }


        // POST: odata/Exams(5)/ExamSteps
        [Authorize]
        [HttpPost]
        public async Task<IHttpActionResult> PostToExamSteps([FromODataUri] int key, ExamStep examStep)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // Only doctors have permissions to create ExamSteps
            if (!_serviceUser.VerifyIsDoctor(int.Parse(User.Identity.Name)))
                return BadRequest(Errors.InvalidCredentials);

            try
            {
                examStep = await _serviceExamStep.RegisterAsync(
                    examStep.ExamId,
                    examStep.Description,
                    examStep.State,
                    examStep.Time,
                    examStep.InitialDate,
                    examStep.EndDate);
            }
            catch (DbUpdateException ex)
            {
                return BadRequest(ex.Message);
            }

            return Created(examStep);
        }

        // POST: odata/Exams(5)/ExamSteps
        [Authorize]
        [HttpPost]
        public async Task<IHttpActionResult> Cancel([FromODataUri] int key)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // Only doctors have permissions to create ExamSteps
            if (!_serviceUser.VerifyIsDoctor(int.Parse(User.Identity.Name)))
                return BadRequest(Errors.InvalidCredentials);

            try
            {
                await _serviceExam.CancelExam(key);

                Exam exam = await _serviceExam.Get(key).Include(e => e.ExamSteps).FirstOrDefaultAsync();
                if (exam.ExamState.Equals("cancelled"))
                {
                    foreach (ExamStep step in exam.ExamSteps)
                    {
                        await _serviceExamStep.CancelExamStepAsync(step.ExamId, step.StepNum);
                    }
                }

            }
            catch (DbUpdateException ex)
            {
                return BadRequest(ex.Message);
            }

            return Updated("Exam cancelled");
        }

        //POST: odata/Exams(5)/ExamSteps(2)/ExamStepPoints
        [Authorize]
        [HttpPost]
        public async Task<IHttpActionResult> ExamStepPoints(int key, int relatedKey)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            //Only the patient has permissions
            if (!(_serviceExam.VerifyiIsOwn(int.Parse(User.Identity.Name), key)))
                return BadRequest(Errors.InvalidCredentials);

            try
            {
                JObject jobj = (JObject)ControllerContext.RouteData.Values["jsonObject"];
                ExamStep model = jobj.ToObject<ExamStep>();
                await _serviceExamStep.ChangeInformationAsync(model.ExamId, model.StepNum, model.InitialDate, model.EndDate, model.ExamStepPoints);
            }
            catch (DbUpdateException ex)
            {
                return BadRequest(ex.Message);
            }

            return Updated("Points Inserted");
        }

        //POST: odata/Exams(5)/ExamSteps(2)/Cancel
        [Authorize]
        [HttpPost]
        public async Task<IHttpActionResult> Cancel(int key, int relatedKey)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // Only doctors have permissions to create ExamSteps
            if (!_serviceUser.VerifyIsDoctor(int.Parse(User.Identity.Name)))
                return BadRequest(Errors.InvalidCredentials);

            try
            {
                await _serviceExamStep.CancelExamStepAsync(key, relatedKey);
            }
            catch (DbUpdateException ex)
            {
                return BadRequest(ex.Message);
            }

            return Updated("ExamStep cancelled");
        }


    }

}