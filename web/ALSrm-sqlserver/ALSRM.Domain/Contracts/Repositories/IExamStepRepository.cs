using ALSRM.Domain.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ALSRM.Domain.Contracts.Repositories
{
    public interface IExamStepRepository : IDisposable
    {
        IQueryable<ExamStep> Get();
        IQueryable<ExamStep> Get(int examId, int stepNum);
        IQueryable<ExamStep> Get(int examId);
        Task<ExamStep> CreateAsync(ExamStep examStep);
        Task<int> UpdateAsync(ExamStep examStep);
        Task<int> DeleteAsync(ExamStep examStep);
        Task<ExamStep> FindAsync(int examId, int stepNum);
        Task<int> SaveRepositoryAsync();
        int GetNextStepNum(int examId);
    }
}
