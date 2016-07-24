using ALRSM.Resources.Resources;
using ALSRM.Resources.Validation;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace ALSRM.Domain.Models
{
    [DataContract]
    public class BaseUser
    {
        [Column("id")]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [DataMember]
        [Key]
        public int Id { get; set; }
 
    }

    [DataContract]
    public class User : BaseUser{

        public User()
        {
        }

        public User(int id, string username, string password, string securityQuestion, string securityAnswer, string descriminator)
        {
            this.Id = id;
            this.Name = username;
            this.Password = password;
            this.SecurityQuestion = securityAnswer;
            this.SecurityAnswer = securityAnswer;
            this.Descriminator = descriminator;

        }

        #region Properties  

        [Column("username")]
        [DataMember]
        public string Name { get; set; }

        [Column("userpassword")]
        public string Password { get; set; }

        [Column("securityquestion")]
        [DataMember]
        public string SecurityQuestion { get; set; }

        [Column("securityanswer")]
        public string SecurityAnswer { get; set; }

        [Column("descriminator")]
        public string Descriminator { get; set; }

        #endregion

        #region Methods

        public void SetPassword(string password, string confirmPassword)
        {
            AssertionConcern.AssertArgumentNotNull(password, Errors.InvalidUserPassword);
            AssertionConcern.AssertArgumentNotNull(confirmPassword, Errors.InvalidUserConfirmPassword);
            AssertionConcern.AssertArgumentLength(password, 6, 20, Errors.InvalidUserPasswordLength);
            AssertionConcern.AssertArgumentEquals(password, confirmPassword, Errors.PasswordDoesNotMatch);

            this.Password = PasswordAssertionConcern.Encrypt(password);
        }

        public void SetPassword(string password, string confirmPassword, string securityQuestion, string securityAnswer)
        {
            AssertionConcern.AssertArgumentNotNull(password, Errors.InvalidUserPassword);
            AssertionConcern.AssertArgumentNotNull(confirmPassword, Errors.InvalidUserConfirmPassword);
            AssertionConcern.AssertArgumentNotNull(securityQuestion, Errors.InvalidSecurityQuestion);
            AssertionConcern.AssertArgumentNotNull(securityAnswer, Errors.InvalidSecurityAnswer);
            AssertionConcern.AssertArgumentLength(password, 6, 20, Errors.InvalidUserPasswordLength);
            AssertionConcern.AssertArgumentEquals(password, confirmPassword, Errors.PasswordDoesNotMatch);

            this.Password = PasswordAssertionConcern.Encrypt(password);
            this.SecurityQuestion = securityQuestion;
            this.SecurityAnswer = PasswordAssertionConcern.Encrypt(securityAnswer);
        }

        public void ChangeSecurityQuestion(string question, string answer)
        {
            this.SecurityQuestion = question;
            this.SecurityAnswer = PasswordAssertionConcern.Encrypt(answer);
        }

        public void ValidateSecurityAnswer(string answer)
        {
            AssertionConcern.AssertArgumentNotNull(answer, Errors.InvalidSecurityAnswer);
            AssertionConcern.AssertArgumentEquals(PasswordAssertionConcern.Encrypt(answer), this.SecurityAnswer, Errors.InvalidSecurityAnswer);
        }

        public void ChangeName(string name)
        {
            this.Name = name;
        }

        public string ResetPassword()
        {
            string pass = Guid.NewGuid().ToString().Substring(0, 8);
            this.Password = PasswordAssertionConcern.Encrypt(pass);

            return pass;
        }

        public void Validate()
        {
            AssertionConcern.AssertArgumentLength(this.Name, 3, 250, Errors.InvalidUserName);
            PasswordAssertionConcern.AssertIsValid(this.Password);
        }
        #endregion
    }
   
    [DataContract]
    public class Doctor : User
    {
        public Doctor()
        {

        }

        public Doctor(string name)
        {
            this.Name = name;
        }

        public Doctor(int id, string username, string password, string securityQuestion, string securityAnswer)
        {
            this.Id = id;
            this.Name = username;
            this.Password = password;
            this.SecurityQuestion = securityAnswer;
            this.SecurityAnswer = securityAnswer;
            this.Descriminator = "doctor";

        }

        #region Properties  

        #endregion

        #region Methods
        #endregion
    }

    [DataContract]
    public class Patient : User 
    {
        public Patient()
        {
              
        }

        public Patient(string name, string patientId, string mac_bitalino)
        {
            this.Name = name;
            this.PatientId = patientId;
            this.Descriminator = "patient";
            this.Mac_Bitalino = mac_bitalino;
        }

        public Patient(int id, string username, string password, string securityQuestion, string securityAnswer, string patientId, List<Exam> exams)
        {
            this.Id = id;
            this.Name = username;
            this.Password = password;
            this.SecurityQuestion = securityAnswer;
            this.SecurityAnswer = securityAnswer;
            this.PatientId = patientId;
            this.Descriminator = "patient";
            this.Exams = exams;
        }

        [DataMember]
        public ICollection<Exam> Exams { get; set; }

        [Column("patientid")]
        [DataMember]
        public string PatientId { get; set; }

        [Column("mac_bitalino")]
        [DataMember]
        public string Mac_Bitalino { get; set; }

        #region Properties  

        #endregion

        #region Methods
        #endregion

    }
}

