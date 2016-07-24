using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using System.Linq;
using System.Threading.Tasks;
using System;

namespace ALSRM.Buisness.Services
{
    public class ExamService : IExamService
    {
        private readonly IExamRepository _repository; 
        public ExamService(IExamRepository repository)
        {
            _repository = repository;
        }     

        public IQueryable<Exam> GetExams() {
            return _repository.Get();
        }

        public IQueryable<Exam> Get(int id)
        {
            return _repository.Get(id);
        }

        public async Task<int> RegisterAsync(Exam exam)
        {
             return await _repository.CreateAsync(exam);
        }

        public bool VerifyiIsOwn(int userId, int examId)
        {
            Exam exam = _repository.Get(examId).FirstOrDefault();

            return (exam.UserId == userId);

        }

        public async Task<int> CancelExam(int examId)
        {
            Exam exam = await _repository.FindAsync(examId);

            exam.ExamState = "cancelled";

            return await _repository.UpdateAsync(exam);
        }

        public IQueryable<Muscle> GetMuscles()
        {
            return _repository.GetMuscles();
        }

        public void Dispose()
        {
            _repository.Dispose();
        }

        public Task<Exam> FindAsync(int examId)
        {
            return _repository.FindAsync(examId);
        }

    }
}
