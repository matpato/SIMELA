using System.ComponentModel.DataAnnotations;

namespace ALSRM.WebApp.Models
{
    public class AccountViewModels
    {
    }

    public class LoginViewModel
    {
        [Required]
        public int UserId { get; set; }

        [Required]
        [DataType(DataType.Password)]
        public string Password { get; set; }
    }

    public class PostRegisterViewModel
    {
        public PostRegisterViewModel()
        {
        }

        public PostRegisterViewModel(GetRegisterViewModel model)
        {
            Name = model.Name;
            PatientId = model.PatientId;
        }

        public int Id { get; set; }

        [Required]
        public string Name { get; set; }

        //[Required]
        [DataType(DataType.Password)]
        [StringLength(20, ErrorMessage = "The {0} must be at least {2} characters long.", MinimumLength = 6)]
        public string Password { get; set; }

        //[Required]
        [DataType(DataType.Password)]
        [Compare("Password", ErrorMessage = "The password and confirmation password do not match.")]
        public string ConfirmPassword { get; set; }

        //[Required]
        public string SecurityQuestion { get; set; }

        //[Required]
        public string SecurityAnswer { get; set; }

        [Required]
        public string PatientId { get; set; }

        public string Mac_Bitalino { get; set; }
    }

    public class GetRegisterViewModel
    {
        [Required]
        public string Name { get; set; }

        [Required]
        public string PatientId { get; set; }

        public string Mac_Bitalino { get; set; }
    }


    public class UserDashboardModel
    {
        public int Id { get; set; }

        [Required]
        public string Name { get; set; }

        [Required]
        public string PatientId { get; set; }

        public string Mac_Bitalino { get; set; }
    }

    public class ResetPasswordModelApi
    {
        [Required]
        public string SecurityAnswer { get; set; }
    }

    public class ResetPasswordModel
    {
        [Required]
        public int Id { get; set; }

        [Required]
        public string SecurityAnswer { get; set; }
    }

    public class ChangePasswordModel
    {
        [Required]
        public string Password { get; set; }

        [Required]
        public string NewPassword { get; set; }

        [Required]
        public string ConfirmNewPassword { get; set; }

        [Required]
        public string SecurityAnswer { get; set; }
    }

    public class ChangeMacBitalinoModel
    {
        [Required]
        public string Mac_Bitalino { get; set; }
    }

    public class ChangeMacBitalinoToPatientModel
    {
        [Required]
        public string UserId { get; set; }

        [Required]
        public string Mac_Bitalino { get; set; }
    }

    public class ChangeSecuritAnswerModel
    {
        [Required]
        public string Password { get; set; }

        [Required]
        public string SecurityQuestion { get; set; }

        [Required]
        public string SecurityAnswer { get; set; }
    }
}