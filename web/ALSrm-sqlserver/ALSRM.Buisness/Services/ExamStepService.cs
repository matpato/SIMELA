using ALRSM.Resources.Resources;
using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace ALSRM.Buisness.Services
{
    public class ExamStepService : IExamStepService
    {
        IExamStepRepository _repositoryExamStep;
        IExamRepository _repositoryExam;
        public ExamStepService(IExamStepRepository repositoryExamStep, IExamRepository repositoryExam)
        {
            this._repositoryExamStep = repositoryExamStep;
            this._repositoryExam = repositoryExam;
        }

        public IQueryable<ExamStep> Get()
        {
            return _repositoryExamStep.Get();
        }

        public IQueryable<ExamStep> Get(int examId)
        {
            return _repositoryExamStep.Get(examId);
        }
        public IQueryable<ExamStep> Get(int examId, int stepNum)
        {
            return _repositoryExamStep.Get(examId, stepNum);
        }

        public Task<ExamStep> FindAsync(int examId, int stepNum)
        {
            return _repositoryExamStep.FindAsync(examId, stepNum);
        }

        public async Task<ExamStep> RegisterAsync(int examId, string description, string state, int time, DateTimeOffset? initialDate, DateTimeOffset? endDate)
        {
            ExamStep examStep = new ExamStep(examId, description, state, time, initialDate, endDate);
            SetStepNum(examStep);

            if (state.Equals("pending"))
            {
                await ChangeExamState(examId, "pending");
            }else if (state.Equals("completed"))
            {
                await VerifyAllExamStepsAreCompleted(examId);
            }
            else
            {
                await VerifyAllExamStepsAreCancelled(examId);
            }
            
            return await _repositoryExamStep.CreateAsync(examStep);
        }

        public async Task<int> ChangeInformationAsync(int examId, int stepNum, string description, string state, int time, DateTimeOffset? initialDate, DateTimeOffset? endDate)
        {
            ExamStep examStep = await FindAsync(examId, stepNum);

            if (examStep == null)
                throw new Exception(Errors.InvalidExamStep);

            // TODO FALTA RESTRIÇÕES 
            examStep.ChangeState(state);

            examStep.ChangeIntialDate(initialDate);
            examStep.ChangeEndDate(endDate);
            examStep.ChangeDescription(description);
            examStep.ChangeTime(time);
            examStep.Validate();

            return await _repositoryExamStep.UpdateAsync(examStep);
        }
        public async Task<int> ChangeInformationAsync(int examId, int stepNum, DateTimeOffset? initialDate, DateTimeOffset? endDate, ICollection<Point> points)
        {
            ExamStep examStep = await FindAsync(examId, stepNum);

            examStep.ChangeState("completed");
            examStep.ChangeIntialDate(initialDate);
            examStep.ChangeEndDate(endDate);
            examStep.ChangeExamStepPoints(points);
            examStep.Validate();

            int ret = await _repositoryExamStep.UpdateAsync(examStep);

            await VerifyAllExamStepsAreCompleted(examId);

            return ret;
        }

        public async Task<int> CancelExamStepAsync(int examId, int stepNum)
        {
            ExamStep examStep = await FindAsync(examId, stepNum);

            examStep.ChangeState("cancelled");
            examStep.Validate();

            int ret = await _repositoryExamStep.UpdateAsync(examStep);

            await VerifyAllExamStepsAreCompleted(examId);
            await VerifyAllExamStepsAreCancelled(examId);

            return ret;
        }

        // Method to verify that all examSteps are completed, if true are changes Exam state to also completed.
        public async Task<bool> VerifyAllExamStepsAreCompleted(int examId)
        {
            Exam exam = await _repositoryExam.FindAsync(examId);
            List<ExamStep> exams = _repositoryExamStep.Get(examId).ToList();

            foreach (ExamStep examStep in exams)
            {
                if (examStep.State.Equals("pending"))
                    return false;                    
            }

            exam.ExamState = "completed";
            await _repositoryExam.UpdateAsync(exam);
            return true;
        }

        public async Task<bool> VerifyAllExamStepsAreCancelled(int examId)
        {
            Exam exam = await _repositoryExam.FindAsync(examId);
            List<ExamStep> exams = _repositoryExamStep.Get(examId).ToList();

            foreach (ExamStep examStep in exams)
            {
                if (!examStep.State.Equals("cancelled"))
                    return false;
            }
            exam.ExamState = "cancelled";
            await _repositoryExam.UpdateAsync(exam);
            return true;
        }

        public async Task<bool> ChangeExamState(int examId, string state)
        {
            Exam exam = await _repositoryExam.FindAsync(examId);
            exam.ExamState = "pending";
            await _repositoryExam.UpdateAsync(exam);
            return true;
        }


        private void SetStepNum(ExamStep examStep)
        {
            examStep.StepNum = _repositoryExamStep.GetNextStepNum(examStep.ExamId);
        }

        public bool ExamStepExists(int examId, int stepNum)
        {
            return _repositoryExamStep.Get(examId, stepNum) == null ? false : true;
        }

        public async Task<int> SaveRepositoryAsync()
        {
            return await _repositoryExamStep.SaveRepositoryAsync();
        }

        public void Dispose()
        {
            _repositoryExamStep.Dispose();
        }
    }
}
