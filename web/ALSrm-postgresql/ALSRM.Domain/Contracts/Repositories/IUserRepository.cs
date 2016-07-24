using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Repositories
{
    public interface IUserRepository : IDisposable
    { 
            IQueryable<BaseUser> Get(int id);
            IQueryable<Patient> GetByPatientId(string id);
            IQueryable<User> Get();
            IQueryable<Patient> GetPatients();
            User GetUser(int id);
            Doctor GetDoctor(int id);
            Task<BaseUser> CreateAsync(BaseUser user);
            Task<int> UpdateAsync(User user);
            Task<int> DeleteAsync(User user);
            Task<User> FindAsync(int id);
            IQueryable<Exam> GetExamsByUserId(int id);
            Task<int> SaveRepositoryAsync();

    }
}
