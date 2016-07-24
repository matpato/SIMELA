using System;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using System.Web.Http.OData;
using ALRSM.Resources.Resources;
using ALSRM.Api2.Models.User;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;

namespace ALSRM.Api2.Controllers
{
    public class UsersController : ODataController
    {
        private readonly IUserService _service;

        public UsersController(IUserService service)
        {
            _service = service;
        }

        #region EndPoints

       
        [Authorize]
        [ResponseType(typeof(IQueryable<User>))]
        [EnableQuery]
        // GET: odata/Users
        public IHttpActionResult GetUsers()
        {
            // If is the patient himself or a doctor to get access to profile id
            if (!_service.VerifyIsDoctor(int.Parse(User.Identity.Name)))
                return BadRequest(Errors.InvalidCredentials);

            IQueryable<User> users = _service.GetPatients();
            return Ok(users);
        }


        [Authorize]
        [ResponseType(typeof(IQueryable<BaseUser>))]
        [EnableQuery(MaxExpansionDepth = 2)]
        // The MaxExpansionDepth guarantees that there is no access to ExamStepPoints 
        // GET: odata/Users(5)
        public IHttpActionResult GetBaseUser([FromODataUri] int key)
        {
            // If is the patient himself or a doctor to get access to profile id
            if ((int.Parse(User.Identity.Name) != key) && !_service.VerifyIsDoctor(int.Parse(User.Identity.Name)))
                return BadRequest(Errors.InvalidCredentials);


            IQueryable<BaseUser> user;
            try
            {
                user = _service.Get(key);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(user);
        }

        // POST: odata/Users
        public async Task<IHttpActionResult> Post(RegisterUserModel user)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            try
            {
                BaseUser newUser;
                if (user.PatientId == null)
                {
                    newUser =
                        await
                            _service.RegisterAsync(user.Name, user.Password, user.ConfirmPassword, user.SecurityQuestion,
                                user.SecurityAnswer);
                    return Created((Doctor)newUser);
                }
                newUser =
                    await
                        _service.RegisterAsync(user.Name, user.Password, user.ConfirmPassword, user.SecurityQuestion,
                            user.SecurityAnswer, user.PatientId, user.Mac_Bitalino);
                return Created((Patient)newUser);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // PUT: odata/Users(5)
        [Authorize]
        public async Task<IHttpActionResult> Put([FromODataUri] int key, PatientModel model)
        {
            try
            {
                if (key != model.Id)
                    return BadRequest(Errors.InvalidUserId);

                await _service.FindAsync(key);
                await _service.ChangeInformationAsync(int.Parse(User.Identity.Name), model.Name);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Updated(model);
        }

        // PATCH: odata/Users(5)
        [Authorize]
        [AcceptVerbs("PATCH")]
        public async Task<IHttpActionResult> Patch([FromODataUri] int key, ChangeInformationModel model)
        {
            try
            {
                await _service.ChangeInformationAsync(int.Parse(User.Identity.Name), model.Name);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Updated(model);
        }

        // POST: odata/Users(5)/ChangePassword
        [HttpPost]
        [Authorize]
        //[ODataRoute("({key})/ChangePassword")]
        public async Task<IHttpActionResult> ChangePassword([FromODataUri] int key, ODataActionParameters parameters)
        {
            if (int.Parse(User.Identity.Name) != key)
            {
                return Unauthorized();
            }
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var password = (string)parameters["Password"];
            var newPassword = (string)parameters["NewPassword"];
            var confirmNewPassword = (string)parameters["ConfirmNewPassword"];
            var securityAnswer = (string)parameters["SecurityAnswer"];

            try
            {
                await
                    _service.ChangePasswordAsync(int.Parse(User.Identity.Name), password, newPassword,
                        confirmNewPassword, securityAnswer);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(Messages.PasswordChangedSuccessfully);
        }

        // POST: odata/Users(5)/ResetPassword
        [HttpPost]
        public async Task<IHttpActionResult> ResetPassword([FromODataUri] int key, ODataActionParameters parameters)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            var securityAnswer = (string)parameters["SecurityAnswer"];
            string password;
            try
            {
                password = await _service.ResetPasswordAsync(key, securityAnswer);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(Messages.PasswordReseted + " " + password);
        }

        [HttpPost]
        [Authorize]
        public async Task<IHttpActionResult> ChangeSecurityQuestion([FromODataUri] int key, ODataActionParameters parameters)
        {
            if (int.Parse(User.Identity.Name) != key)
            {
                return Unauthorized();
            }

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            string securityAnswer = (string)parameters["SecurityAnswer"];
            string securityQuestion = (string)parameters["SecurityQuestion"];
            string password = (string)parameters["Password"];
            try
            {
                await _service.ChangeSecurityQuestionAsync(key, securityQuestion, securityAnswer, password);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(Messages.SecurityQuestionChangesSuccessfully);
        }


        // POSt :odata/Users(5)/ChangeMacBitalino
        [HttpPost]
        [Authorize]
        public async Task<IHttpActionResult> ChangeMacBitalino([FromODataUri] int key, ODataActionParameters parameters)
        {
            if ((int.Parse(User.Identity.Name) != key) && !_service.VerifyIsDoctor(int.Parse(User.Identity.Name)))
            {
                return Unauthorized();
            }

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            try
            {

                await _service.ChangeMacBitalinoAsync(key, (string)parameters["Mac_Bitalino"]);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(Messages.ChangedMacBitalino);
        }

        // GET: odata/Users(5)/Exams
        [Authorize]
        [ResponseType(typeof(IQueryable<Exam>))]
        [EnableQuery(MaxExpansionDepth = 1)]
        // The MaxExpansionDepth guarantees that there is no access to ExamStepPoints
        public IHttpActionResult GetExams([FromODataUri] int key)
        {
            IQueryable<Exam> exams;
            if (int.Parse(User.Identity.Name) != key)
                return BadRequest(Errors.InvalidCredentials);
            try
            {
                exams = _service.GetExamsByUserId(key);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

            return Ok(exams);
        }

        #endregion

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                _service.Dispose();
            }
            base.Dispose(disposing);
        }

    
    }
}