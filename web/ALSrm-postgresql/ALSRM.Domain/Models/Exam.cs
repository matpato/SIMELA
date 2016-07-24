using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ALSRM.Domain.Models
{
    public class BaseExam
    {
        [Column("examid")]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Display(Name = "Id")]
        [Key]
        public int ExamId { get; set; }
    }
    [Table("Exam")]
    public class Exam : BaseExam
    {
        #region Ctor

        public Exam() { }
        public Exam(int id, string type, string state, DateTimeOffset initialDate, DateTimeOffset endData, ICollection<ExamStep> steps, Patient user)
        {
            this.ExamId = id;
            this.ExamType = type;
            this.ExamState = state;
            this.ExamInitialDate = initialDate;
            this.ExamEndDate = endData;
            this.ExamSteps = steps;
            this.ThisUser = user;
            this.UserId = user.Id;
        }
        #endregion

        #region Properties
 

        [Column("examtype")]
        [Display(Name = "Type")]
        public string ExamType { get; set; }

        [Column("examstate")]
        [Display(Name = "State")]
        public string ExamState { get; set; }

        [Column("examinitialdate")]
        [Display(Name = "Initial Date")]
        [DataType(DataType.Date)]
        public DateTimeOffset ExamInitialDate { get; set; }

        [Column("examenddate")]
        [Display(Name = "End Date")]
        [DataType(DataType.Date)]
        public DateTimeOffset ExamEndDate { get; set; }
        
        public virtual ICollection<ExamStep> ExamSteps { get; set; }

        [Column("muscleid"), ForeignKey("ThisMuscle")]
        public  int? MuscleId { get; set; }

        public virtual Muscle ThisMuscle { get; set; }

        [Column("userid"), ForeignKey("ThisUser")]
        public int UserId { get; set; }

        public virtual Patient ThisUser { get; set; }
        #endregion

        #region Methods
        #endregion

    }
}
