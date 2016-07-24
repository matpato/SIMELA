using System.Runtime.Serialization;

namespace ALSRM.Api2.Models.User
{
    [DataContract]
    public class PatientModel : Domain.Models.User
    {
        public PatientModel()
        {   }

        public PatientModel(int id, string name, string patientId)
        {
            this.Id = id;
            this.Name = name;
            this.PatientId = patientId;
        }

        [DataMember]
        public string PatientId { get; set; }

    }
}
