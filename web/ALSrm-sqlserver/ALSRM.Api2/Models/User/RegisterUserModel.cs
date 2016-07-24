using System.Runtime.Serialization;

namespace ALSRM.Api2.Models.User
{
    [DataContract]
    public class RegisterUserModel : Domain.Models.BaseUser
    {
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public string Password { get; set; }

        [DataMember]
        public string ConfirmPassword { get; set; }

        [DataMember]
        public string SecurityQuestion { get; set; }

        [DataMember]
        public string SecurityAnswer { get; set; }

        [DataMember]
        public string Descriminator { get; set; }

        [DataMember]
        public string PatientId { get; set; }

        [DataMember]
        public string Mac_Bitalino { get; set; }
    }
}