using ALRSM.Resources.Resources;
using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using ALSRM.Resources.Validation;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Buisness.Services
{
    public class UserService : IUserService
    {
        private readonly IUserRepository _repository;

        public UserService(IUserRepository repository)
        {
            this._repository = repository;
        }

        public IQueryable<User> Get()
        {
            return _repository.Get();
        }

        public IQueryable<Patient> GetPatients()
        {
            return _repository.GetPatients();
        }

        public IQueryable<BaseUser> Get(int id)
        {
            return _repository.Get(id);
        }

        public User Authenticate(int id, string password)
        {
            User user = (User)_repository.Get(id).FirstOrDefault();

            if (user.Password != PasswordAssertionConcern.Encrypt(password))
                throw new Exception(Errors.InvalidCredentials);

            return user;
        }

        public async Task<int> ChangeInformationAsync(int id, string name)
        {
            User user = await _repository.FindAsync(id);
           
            if(user == null)            
                throw new Exception(Errors.InvalidPatientId);
            
            user.ChangeName(name);
            user.Validate();

            return await _repository.UpdateAsync(user);
        }

        public async Task<int> ChangePasswordAsync(int id, string password, string newPassword, string confirmNewPassword, string securityAnswer)
        {
            var user = Authenticate(id, password);
            user.ValidateSecurityAnswer(securityAnswer);
            user.SetPassword(newPassword, confirmNewPassword);
            user.Validate();

            return await _repository.UpdateAsync(user);
        }

        public IQueryable<User> GetByPacientId(string id)
        {
            return _repository.GetByPatientId(id);
        }
      
        public async Task<BaseUser> RegisterAsync(string name, string password, string confirmPassword, string securityQuestion, string securityAnswer, string patientId, string mac_bitalino)
        {
           if (_repository.GetByPatientId(patientId).FirstOrDefault() != null)
                throw new Exception(Errors.DuplicatePatientId);

            Patient user = new Patient(name, patientId, mac_bitalino);
            user.SetPassword(password, confirmPassword,securityQuestion, securityAnswer);
            user.Validate();

            return await _repository.CreateAsync(user);
        }

        public async Task<BaseUser> RegisterAsync(string name, string password, string confirmPassword, string securityQuestion, string securityAnswer)
        {
            
            Doctor user = new Doctor(name);
            user.SetPassword(password, confirmPassword, securityQuestion, securityAnswer);
            user.Validate();

             return await _repository.CreateAsync(user);
        }

        public async Task<string> ResetPasswordAsync(int id, string securityAnswer)
        {
            User user = (User)_repository.Get(id).FirstOrDefault();
            user.ValidateSecurityAnswer(securityAnswer);
            string password = user.ResetPassword();
            user.Validate();

            await _repository.UpdateAsync(user);

            return password;
        }

        public async Task<int> ChangeSecurityQuestionAsync(int id, string securityQuestion, string securityAnswer, string password)
        {
            User user = Authenticate(id, password);
            user.ChangeSecurityQuestion(securityQuestion, securityAnswer);
            user.Validate();

            return await _repository.UpdateAsync(user);
        }

        public async Task<int> ChangeMacBitalinoAsync(int id, string mac_bitalino)
        {
            Patient patient = (Patient) _repository.Get(id).FirstOrDefault(); 
            patient.Mac_Bitalino = mac_bitalino;
             
            return await _repository.UpdateAsync(patient);
        }

        public IQueryable<Exam> GetExamsByUserId(int id)
        {
           return _repository.GetExamsByUserId(id);
        }

        public async Task<User> FindAsync(int id) {
            return await _repository.FindAsync(id);
        }

        public async Task<int> SaveRepositoryAsync()
        {
            return await _repository.SaveRepositoryAsync();
        }

        public bool UserExists(int id)
        {
            return _repository.Get(id) == null ? false : true;
        }

        public bool VerifyIsDoctor(int id)
        {
            return _repository.GetDoctor(id) == null ? false : true;
        }

        public void Dispose()
        {
            _repository.Dispose();
        }

    }
}
