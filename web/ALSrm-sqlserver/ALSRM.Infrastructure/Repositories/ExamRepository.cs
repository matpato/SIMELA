using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Models;
using ALSRM.Infrastructure.Data;
using System.Linq;
using System.Threading.Tasks;
using System;

namespace ALSRM.Infrastructure.Repositories
{
    public class ExamRepository : IExamRepository
    {
        private AppDataContext _context;

        public ExamRepository(AppDataContext context)
        {
            this._context = context;
        }

        public  async Task<int> CreateAsync(Exam exam)
        {
            SetPropertiesToLowerCase(exam);
            if(exam.ExamSteps != null)
                SetIdExamSteps(exam);   
                      
           _context.Exams.Add(exam);

            return await _context.SaveChangesAsync();
        }

        public async Task<int> UpdateAsync(Exam exam)
        {
            _context.Entry(exam).State = System.Data.Entity.EntityState.Modified;
            return await _context.SaveChangesAsync();
        }

        public Task<int> DeleteAsync(Exam exam)
        {
            throw new NotImplementedException();
        }

        public async Task<Exam> FindAsync(int examId)
        {
            return await _context.Exams.FindAsync(examId);
        }

        public IQueryable<Exam> Get(int id)
        {
            return _context.Exams.Where(x => x.ExamId == id);
        }

        public IQueryable<Exam> Get()
        {
            return _context.Exams;

           // return _context.Exams.OrderBy(x => x.ExamId).Skip(skip).Take(take).ToList();
        }

        public IQueryable<Muscle> GetMuscles()
        {
            return _context.Muscles;
        }

        public void Dispose()
        {
            _context.Dispose();
        }

        /* TODO Para dar reset ao serial do ExamStep (mudar para o repositório de ExamStep)
        private void ResetAutoIncrement() {
            _context.Exams.SqlQuery("SELECT SETVAL ((SELECT pg_get_serial_sequence('\"ExamStep\"', 'stepnum')),1,false)");
        }
        */

        private static void SetPropertiesToLowerCase(Exam exam)
        {
            exam.ExamState = exam.ExamState.ToLower();
            exam.ExamType = exam.ExamType.ToLower();

            if(exam.ExamSteps != null)
                foreach (ExamStep es in exam.ExamSteps)
                {
                    es.Description = es.Description.ToLower();
                    es.State = es.State.ToLower();
                }
        }

        private void SetIdExamSteps(Exam exam)
        {
            int counter = 1;

            foreach(ExamStep es in exam.ExamSteps)
            {
                es.StepNum = counter++;
            }
        }
    }
}
