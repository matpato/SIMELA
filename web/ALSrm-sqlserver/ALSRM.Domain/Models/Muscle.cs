using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ALSRM.Domain.Models
{
    public class Muscle
    {

        #region Properties

        [Column("id")]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Key]
        public int Id { get; set; }

        [Column("musclename")]
        public string Name { get; set; }

        [Column("abbreviation")]
        public string Abbreviation { get; set; }

        public ICollection<Exam> Exams { get; set; }

        #endregion
    }

}
