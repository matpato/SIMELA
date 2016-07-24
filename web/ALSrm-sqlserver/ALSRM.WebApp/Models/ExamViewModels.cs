using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using ALSRM.Domain.Models;
using ALSRM.WebApp.Resources;

namespace ALSRM.WebApp.Models
{
    public class ExamViewModels
    {
        public int ExamId { get; set; }

        [Required]
        public string ExamType { get; set; }

        [Required]
        public string ExamState { get; set; }

        [Required]
        [DataType(DataType.Date)]
        public DateTime ExamInitialDate { get; set; }

        [Required]
        [DataType(DataType.Date)]
        public DateTime ExamEndDate { get; set; }

        public virtual ICollection<ExamStepModels> ExamSteps { get; set; }

        public int? MuscleId { get; set; }

        public string MuscleAbb { get; set; }


        [Required]
        [Range(0, int.MaxValue)]
        public int UserId { get; set; }

        // method for guarantee values are valid to api
        public void ValidateLanguage()
        {
            ExamState = ExamState.ToLower();
            switch (ExamState)
            {
                case "pendente":
                    ExamState = "pending";
                    break;
                case "cancelado":
                    ExamState = "cancelled";
                    break;
                case "completado":
                    ExamState = "completed";
                    break;
            }
            foreach (var es in ExamSteps)
            {
                es.ValidateLanguage();
            }
        }

        // method for changing language to show
        public static void ChangeLanguage(Exam exam)
        {
            exam.ExamState = exam.ExamState.ToLower();
            switch (exam.ExamState)
            {
                case "pending":
                    exam.ExamState = ExamResources.Pending;
                    break;
                case "completed":
                    exam.ExamState = ExamResources.Completed;
                    break;
                case "cancelled":
                    exam.ExamState = ExamResources.Cancelled;
                    break;
            }
        }
    }

    public class ExamPostModel
    {
        public ExamPostModel()
        {
        }

        public ExamPostModel(ExamViewModels model)
        {
            ExamType = model.ExamType;
            ExamState = model.ExamState;
            ExamInitialDate = model.ExamInitialDate;
            ExamEndDate = model.ExamEndDate;
            ExamSteps = model.ExamSteps;
            MuscleId = model.MuscleId;
            UserId = model.UserId;
        }

        public int ExamId { get; set; }

        [Required]
        public string ExamType { get; set; }

        [Required]
        public string ExamState { get; set; }

        [Required]
        [DataType(DataType.Date)]
        public DateTime ExamInitialDate { get; set; }

        [Required]
        [DataType(DataType.Date)]
        public DateTime ExamEndDate { get; set; }

        public virtual ICollection<ExamStepModels> ExamSteps { get; set; }

        public int? MuscleId { get; set; }

        [Required]
        [Range(0, int.MaxValue)]
        public int UserId { get; set; }

        // method for guarantee values are valid to api
        public void ValidateLanguage()
        {
            ExamState = ExamState.ToLower();
            switch (ExamState)
            {
                case "pendente":
                    ExamState = "pending";
                    break;
                case "cancelado":
                    ExamState = "cancelled";
                    break;
                case "completado":
                    ExamState = "completed";
                    break;
            }
            foreach (var es in ExamSteps)
            {
                es.ValidateLanguage();
            }
        }

        // method for changing language to show
        public static void ChangeLanguage(Exam exam)
        {
            exam.ExamState = exam.ExamState.ToLower();
            switch (exam.ExamState)
            {
                case "pending":
                    exam.ExamState = ExamResources.Pending;
                    break;
                case "completed":
                    exam.ExamState = ExamResources.Completed;
                    break;
                case "cancelled":
                    exam.ExamState = ExamResources.Cancelled;
                    break;
            }
        }
    }

    public class ExamStepModels
    {
        public int ExamId { get; set; }

        [Required]
        [Display(Name = "Description")]
        public string Description { get; set; }

        [Required]
        [Display(Name = "State")]
        public string State { get; set; }

        [Required]
        [Display(Name = "Time")]
        public int Time { get; set; }

        // method for guarantee values are valid to api
        public void ValidateLanguage()
        {
            Description = Description.ToLower();

            switch (Description)
            {
                case "manhã":
                    Description = "morning";
                    break;
                case "tarde":
                    Description = "afternoon";
                    break;
                case "noite":
                    Description = "night";
                    break;
            }

            State = State.ToLower();

            switch (State)
            {
                case "pendente":
                    State = "pending";
                    break;
                case "cancelado":
                    State = "cancelled";
                    break;
                case "completado":
                    State = "completed";
                    break;
            }
        }

        // method for changing language to show
        public void ChangeLanguage()
        {
            State = State.ToLower();
            switch (State)
            {
                case "pending":
                    State = ExamResources.Pending;
                    break;
                case "completed":
                    State = ExamResources.Completed;
                    break;
                case "cancelled":
                    State = ExamResources.Cancelled;
                    break;
            }

            Description = Description.ToLower();
            switch (Description)
            {
                case "morning":
                    Description = ExamResources.Morning;
                    break;
                case "afternoon":
                    State = ExamResources.Afternoon;
                    break;
                case "night":
                    State = ExamResources.Night;
                    break;
            }
        }
    }

    public class HomeProfileModel
    {
        public HomeProfileModel()
        {
            Exams1 = new List<Exam>();
            Exams2 = new List<Exam>();
        }

        public IEnumerable<Exam> Exams1 { get; set; }

        public IEnumerable<Exam> Exams2 { get; set; }
    }
}