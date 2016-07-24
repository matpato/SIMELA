using System.ComponentModel.DataAnnotations;

namespace ALSRM.WebApp.Models
{
    public class UserViewModels
    {

        [Required]
        public int Id { get; set; }

        [Required]
        public string Name { get; set; }

        [Required]
        public string SecurityQuestion { get; set; }

        [Required]
        public string PatientId { get; set; }


        public string Mac_Bitalino { get; set; }

    }
}