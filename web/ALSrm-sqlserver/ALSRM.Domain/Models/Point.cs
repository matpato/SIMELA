using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ALSRM.Domain.Models
{
    [Table("Point")]
    public class Point
    {
        #region ctor

        public Point() { }
        public Point(int examId, int examStepNum, decimal x, decimal y)
        {
            this.ExamId = examId;
            this.ExamStepNum = examStepNum;
            this.X = x;
            this.Y = y;
        }
        #endregion

        #region Properties

        [Column("examid", Order = 0), ForeignKey("ThisExamStep")]
        public int ExamId { get; set; }

        public virtual Exam ThisExam { get; set; }

        [Column("examstepnum", Order = 1), ForeignKey("ThisExamStep")]
        public int ExamStepNum { get; set; }

        public virtual ExamStep ThisExamStep { get; set; }

        [Column("x"), Key]
        public decimal X { get; set; }

        [Column("y")]
        public decimal Y { get; set; }

        public void ChangeY(decimal y)
        {
            this.Y = y;
        }

        public void Validate()
        { 
        }

        #endregion


        #region Methods
        #endregion

    }
}
