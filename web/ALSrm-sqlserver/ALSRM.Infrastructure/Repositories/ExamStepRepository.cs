using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Models;
using ALSRM.Infrastructure.Data;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ALSRM.Infrastructure.Repositories
{
    public class ExamStepRepository : IExamStepRepository
    {
        private AppDataContext _context;

        public ExamStepRepository(AppDataContext context)
        {
            this._context = context;
        }

        public async Task<ExamStep> CreateAsync(ExamStep examStep)
        {
             ExamStep exam = _context.ExamSteps.Add(examStep);
             await SaveRepositoryAsync();

            return exam;
        }

        public async Task<int> UpdateAsync(ExamStep examStep)
        {
            _context.Entry(examStep).State = System.Data.Entity.EntityState.Modified;
            return await SaveRepositoryAsync();
        }

        public async Task<int> DeleteAsync(ExamStep examStep)
        {
            _context.ExamSteps.Remove(examStep);
            return await SaveRepositoryAsync();
        }


        public async Task<ExamStep> FindAsync(int examId, int stepNum)
        {
            return await _context.ExamSteps.FindAsync(examId, stepNum);
        }

        public IQueryable<ExamStep> Get()
        {
            return _context.ExamSteps;
        }

        public IQueryable<ExamStep> Get(int examId)
        {
            return _context.ExamSteps.Where(e => e.ExamId == examId);
        }

        public IQueryable<ExamStep> Get(int examId, int stepNum)
        {
           return _context.ExamSteps.Where(e => e.ExamId == examId && e.StepNum == stepNum);
        }

        public int GetNextStepNum(int examId)
        {
            ExamStep step = _context.ExamSteps.Where(e => e.ExamId == examId).OrderByDescending(es => es.StepNum).FirstOrDefault();

            if (step == null)
                return 1;

            return step.StepNum + 1;
        }

        public async Task<int> SaveRepositoryAsync()
        {
            return await _context.SaveChangesAsync();
        }

        public void Dispose()
        {
            _context.Dispose();
        }

    }
}
