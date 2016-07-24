using System.Runtime.Serialization;

namespace ALSRM.Api2.Models.User
{
    [DataContract]
    public class DoctorModel : Domain.Models.BaseUser
    {
        public DoctorModel(int id, string name)
        {
            this.Id = id;
            this.Name = name;
        }


        [DataMember]
        public string Name { get; set; }
    }
}