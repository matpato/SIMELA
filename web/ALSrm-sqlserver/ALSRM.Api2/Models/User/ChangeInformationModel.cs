using System.Runtime.Serialization;

namespace ALSRM.Api2.Models.User
{
    [DataContract]
    public class ChangeInformationModel : ALSRM.Domain.Models.BaseUser
    {
        [DataMember]
        public string Name { get; set; }
    }
}