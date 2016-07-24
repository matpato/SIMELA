
using ALSRM.Domain.Models;
using System.Data.Entity;
using System.Data.Entity.ModelConfiguration.Conventions;

namespace ALSRM.Infrastructure.Data
{
    public class AppDataContext : DbContext
    {

        public AppDataContext(): base("AppConnectionString")
        {
           // Configuration.LazyLoadingEnabled = false;
            // Because of Json Serialization
            Configuration.ProxyCreationEnabled = false;

        }

        public DbSet<Exam> Exams { get; set; }
        public DbSet<Point> Points { get; set; }
        public DbSet<User> Users { get; set; }
        public DbSet<Doctor> Doctors { get; set; }
        public DbSet<Patient> Patients { get; set; }
        public DbSet<ExamStep> ExamSteps { get; set; }
        public DbSet<Muscle> Muscles { get; set; }



        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // On PostgreSQL the tables stays on public."table"
            modelBuilder.Entity<Exam>().ToTable("Exam", "dbo");
            modelBuilder.Entity<ExamStep>().ToTable("ExamStep", "dbo");
            modelBuilder.Entity<Point>().ToTable("Point", "dbo");
            modelBuilder.Entity<User>().ToTable("User", "dbo");
            modelBuilder.Entity<Patient>().ToTable("Patient", "dbo");
            modelBuilder.Entity<Doctor>().ToTable("Doctor", "dbo");
            modelBuilder.Entity<Muscle>().ToTable("Muscle", "dbo");

            modelBuilder.Ignore<BaseUser>();
            modelBuilder.Ignore<BaseExam>();


            modelBuilder.Entity<ExamStep>()
                .HasKey(e => new { e.ExamId, e.StepNum });
                             
            modelBuilder.Entity<Point>()
                .HasKey(p => new { p.X, p.ExamId, p.ExamStepNum });

            modelBuilder.Conventions.Remove<ManyToManyCascadeDeleteConvention>();
            modelBuilder.Conventions.Remove<OneToManyCascadeDeleteConvention>();

            //modelBuilder.Entity<Exam>().HasKey(e => e.ExamId);

            // Database for PostgreSQL doesn't auto-increment Ids
            modelBuilder.Conventions
            .Remove<StoreGeneratedIdentityKeyConvention>();
        }
    }
}
