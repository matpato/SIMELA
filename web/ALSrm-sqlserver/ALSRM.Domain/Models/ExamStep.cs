using ALRSM.Resources.Resources;
using ALRSM.Resources.Shared;
using ALSRM.Resources.Validation;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ALSRM.Domain.Models
{
    public class ExamStep
    { 
        #region Ctor

        public ExamStep() { }
        public ExamStep(int examId, int stepnum, string description, string state, int time, DateTimeOffset? initialDate, DateTimeOffset? endData, ICollection<Point> points)
        {
            this.StepNum = stepnum;
            this.Description = description;
            this.State = state;
            this.Time = time;
            this.InitialDate = initialDate;
            this.EndDate = endData;
            this.ExamId = examId;
            this.ExamStepPoints = points;
        }

        public ExamStep(int examId, string description, string state, int time, DateTimeOffset? initialDate, DateTimeOffset? endDate)
        {
            this.ExamId = examId;
            this.Description = description;
            this.State = state;
            this.Time = time;
            this.InitialDate = initialDate;
            this.EndDate = endDate;
        }

        #endregion

        #region Properties
        [Column("examid", Order = 0), ForeignKey("ThisExam"), Key]
        [Display(Name = "Exam Id")]
        public int ExamId { get; set; }

        public virtual Exam ThisExam { get; set; }

        [Column("stepnum", Order = 1), Key]
        [Display(Name = "Step")]
        public int StepNum { get; set; }

        [Column("description")]
        [Display(Name = "Description")]
        public string Description { get; set; }

        [Column("state")]
        [Display(Name = "State")]
        public string State { get; set; }

        [Column("time")]
        [Display(Name = "Time")]
        public int Time { get; set; }

        [Column("initialdate")]
        [Display(Name = "Initial Date")]
        public DateTimeOffset? InitialDate { get; set; }

        [Column("enddate")]
        [Display(Name = "End Date")]
        public DateTimeOffset? EndDate { get; set; }

        public virtual ICollection<Point> ExamStepPoints { get; set; }

        #endregion

        #region Methods

        public void ChangeDescription(string description)
        {
            AssertionConcern.AssertArgumentNotNull(description, Errors.InvalidExamStepDescription);
            this.Description = description;
        }

        public void ChangeState(string state)
        {
            AssertionConcern.AssertArgumentNotNull(state, Errors.InvalidExamStepState);
            this.State = state;
        }

        public void ChangeIntialDate(DateTimeOffset? initialDate)
        {
            this.InitialDate = initialDate;
        }

        public void ChangeEndDate(DateTimeOffset? endDate)
        {
            this.EndDate = endDate;
        }

        public void ChangeTime(int time)
        {
            AssertionConcern.AssertArgumentNotNull(time, Errors.InvalidExamStepTime);
            AssertionConcern.AssertArgumentRange(time, SharedFunctions.MIN_TIME_MINUTES_A_DAY, SharedFunctions.MAX_TIME_MINUTES_A_DAY, Errors.InvalidExamStepTime);
            this.Time = time;
        }

        public void ChangeExamStepPoints(ICollection<Point> points)
        {
            this.ExamStepPoints = points;
        }

        public void Validate()
        {
        }
        #endregion
    }
}
