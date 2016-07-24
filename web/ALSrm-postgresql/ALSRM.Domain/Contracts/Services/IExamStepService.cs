using ALSRM.Domain.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Services
{
    public interface IExamStepService : IDisposable
    {
        IQueryable<ExamStep> Get();
        IQueryable<ExamStep> Get(int examId);
        IQueryable<ExamStep> Get(int examId, int stepNum);
        Task<ExamStep> RegisterAsync (int examId, string description, string state, int time, DateTimeOffset? initialDate, DateTimeOffset? endDate);
        Task<int> ChangeInformationAsync(int examId, int stepNum, string description, string state, int time, DateTimeOffset? initialDate, DateTimeOffset? endDate);
        Task<int> ChangeInformationAsync(int examId, int stepNum, DateTimeOffset? initialDate, DateTimeOffset? endDate, ICollection<Point> points);
        Task<int> CancelExamStepAsync(int examId, int stepNum);
        Task<bool> VerifyAllExamStepsAreCompleted(int examId);
        Task<ExamStep> FindAsync(int examId, int stepNum);
        bool ExamStepExists(int examId, int stepNum);
        Task<int> SaveRepositoryAsync();

    }
}
