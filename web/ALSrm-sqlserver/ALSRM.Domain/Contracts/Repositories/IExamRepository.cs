using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Repositories
{
    public interface IExamRepository : IDisposable
    {
        IQueryable<Exam> Get(int id);
        IQueryable<Exam> Get();
        Task<Exam> FindAsync(int examId);
        Task<int> CreateAsync(Exam exam);

        Task<int> UpdateAsync(Exam exam);

        Task<int> DeleteAsync(Exam exam);

        IQueryable<Muscle> GetMuscles();
    }
}
