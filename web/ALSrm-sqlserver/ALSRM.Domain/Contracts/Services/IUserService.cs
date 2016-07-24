using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Services
{
    public interface IUserService : IDisposable
    {
        IQueryable<User> Get();
        IQueryable<Patient> GetPatients();
        IQueryable<BaseUser> Get(int id);
        User Authenticate(int id, string password);
        IQueryable<User> GetByPacientId(string id);
        Task<BaseUser> RegisterAsync(string name, string password, string confirmPassword, string securityQuestion, string securityAnswer, string patientId, string mac_bitalino);
        Task<BaseUser> RegisterAsync(string name, string password, string confirmPassword, string securityQuestion, string securityAnswer);
        Task<int> ChangeInformationAsync(int id, string name);
        Task<int> ChangePasswordAsync(int id, string password, string newPassword, string confirmNewPassword, string securityAnswer);
        Task<string> ResetPasswordAsync(int id, string securityAnswer);
        Task<int> ChangeSecurityQuestionAsync(int id, string securityQuestion, string securityAnswer, string password);
        Task<int> ChangeMacBitalinoAsync(int id, string mac_bitalino);
        Task<User> FindAsync(int id);
        bool UserExists(int id);
        bool VerifyIsDoctor(int id);
        Task<int> SaveRepositoryAsync();
        IQueryable<Exam> GetExamsByUserId(int id);
    }
}
