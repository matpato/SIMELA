using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Services
{
    public interface IExamService : IDisposable
    {
        IQueryable<Exam> Get(int id);
        IQueryable<Exam> GetExams();
        Task<int> RegisterAsync(Exam exam);
        bool VerifyiIsOwn(int userId, int ExamId);
        Task<Exam> FindAsync(int examId);
        Task<int> CancelExam(int examId);
        IQueryable<Muscle> GetMuscles();
    }
}
