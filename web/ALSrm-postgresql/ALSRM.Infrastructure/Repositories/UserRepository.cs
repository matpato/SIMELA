using System;
using System.Linq;
using System.Threading.Tasks;
using ALRSM.Resources.Resources;
using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Models;
using ALSRM.Infrastructure.Data;

namespace ALSRM.Infrastructure.Repositories
{
    public class UserRepository : IUserRepository
    {
        private readonly AppDataContext _context;

        public UserRepository(AppDataContext context)
        {
            this._context = context;
        }
        public async Task<BaseUser> CreateAsync(BaseUser user)
        {
            if (user is Patient)
              return await InsertPatientAsync((Patient)user);

              return await InsertDoctorAsync((Doctor)user);      
        }

        public async Task<int> UpdateAsync(User user)
        {
            _context.Entry(user).State = System.Data.Entity.EntityState.Modified;
            return await SaveRepositoryAsync();
        }

        public  async Task<int> DeleteAsync(User user)
        {
            _context.Users.Remove(user);
            return await SaveRepositoryAsync();
        }

        public async Task<User> FindAsync(int id)
        {
            User user = await _context.Users.FindAsync(id);

            // if user is patient or doctor
          /*  if (user.Descriminator == "patient")
                return await _context.Patients.FindAsync(id);
            else*/
                return user;
        }
        public IQueryable<User> Get()
        {
            return _context.Users;
        }

        public IQueryable<Patient> GetPatients()
        {
            return _context.Patients;
        }

        public IQueryable<BaseUser> Get(int id)
        {
            // this request is necessary to ensure that there are obtained the db data instead of data present in the context
            BaseUser reload = _context.Users.FirstOrDefault(u => u.Id == id);
            _context.Entry(reload).Reload();
            
            IQueryable<User> user = _context.Users.Where(u => u.Id == id);

            // if user is patient or doctor
            if (user.FirstOrDefault()?.Descriminator == "patient")
                 return _context.Patients.Where(p => p.Id == id);
            
            return user;
        }

        public User GetUser(int id)
        {
           return _context.Users.Where(u => u.Id == id).FirstOrDefault();
        }

        public Doctor GetDoctor(int id)
        {
            return _context.Doctors.FirstOrDefault(d => d.Id == id);
        }

        public IQueryable<Patient> GetByPatientId(string id)
        {
            return _context.Patients.Where(p => p.PatientId == id);
        }

        public IQueryable<Exam> GetExamsByUserId(int id)
        {
            IQueryable<User> users = _context.Users.Where(u => u.Id == id);

            if (users.FirstOrDefault().Descriminator == "patient")
                return _context.Exams.Where(e => e.UserId == id);
            else
                throw new System.Exception(Errors.InvalidUserIdForExams);
        }

        private async Task<Patient> InsertPatientAsync(Patient patient)
        {
            return await Task<User>.Run(() =>
            {
                string query = string.Format("INSERT INTO dbo.\"Patient\"(" +
                                             "\"username\", \"userpassword\", \"securityquestion\", \"securityanswer\", \"descriminator\", \"patientid\", \"mac_bitalino\")" +
                                             "VALUES('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}') RETURNING id", patient.Name, patient.Password, patient.SecurityQuestion, patient.SecurityAnswer, patient.Descriminator, patient.PatientId, patient.Mac_Bitalino);

                try
                {
                    patient.Id = _context.Database.SqlQuery<int>(query).FirstOrDefault();
                    query = string.Format("SELECT patientid FROM dbo.\"Patient\" WHERE id = {0}", patient.Id);
                    patient.PatientId = _context.Database.SqlQuery<string>(query).FirstOrDefault();
                    return patient;
                }
                catch (Exception)
                {
                    return null;
                }
            });
        }

        private async Task<Doctor> InsertDoctorAsync(Doctor doctor)
        {
            return await Task<User>.Run(() =>
            {
                string query = string.Format("INSERT INTO dbo.\"Doctor\"(" +
                                             "\"username\", \"userpassword\", \"securityquestion\", \"securityanswer\", \"descriminator\")" +
                                             "VALUES('{0}', '{1}', '{2}', '{3}', '{4}') RETURNING id", doctor.Name, doctor.Password, doctor.SecurityQuestion, doctor.SecurityAnswer, doctor.Descriminator);
                try
                {
                    doctor.Id = _context.Database.SqlQuery<int>(query).FirstOrDefault();
                }
                catch (Exception)
                {
                    return null;
                }

                return doctor;
            });
        }

        public async Task<int> SaveRepositoryAsync()
        {
            return await _context.SaveChangesAsync();
        }

        public void Dispose()
        {
            _context.Dispose();
        }

    }
}
