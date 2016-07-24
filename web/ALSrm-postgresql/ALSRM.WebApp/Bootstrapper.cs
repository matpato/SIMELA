using System.Web.Mvc;
using ALSRM.Buisness.Services;
using ALSRM.Domain.Contracts.Repositories;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using ALSRM.Infrastructure.Data;
using ALSRM.Infrastructure.Repositories;
using Microsoft.Practices.Unity;
using Unity.Mvc4;

namespace ALSRM.WebApp
{
    public static class Bootstrapper
    {
        public static IUnityContainer Initialise()
        {
            var container = BuildUnityContainer();

            DependencyResolver.SetResolver(new UnityDependencyResolver(container));

            return container;
        }

        private static IUnityContainer BuildUnityContainer()
        {
            var container = new UnityContainer();
            container.RegisterType<AppDataContext, AppDataContext>(new HierarchicalLifetimeManager());
            container.RegisterType<IExamRepository, ExamRepository>(new HierarchicalLifetimeManager());
            container.RegisterType<IExamService, ExamService>(new HierarchicalLifetimeManager());
            container.RegisterType<IUserRepository, UserRepository>(new HierarchicalLifetimeManager());
            container.RegisterType<IUserService, UserService>(new HierarchicalLifetimeManager());
            container.RegisterType<IExamStepRepository, ExamStepRepository>(new HierarchicalLifetimeManager());
            container.RegisterType<IExamStepService, ExamStepService>(new HierarchicalLifetimeManager());

            container.RegisterType<BaseUser, BaseUser>(new HierarchicalLifetimeManager());
            container.RegisterType<User, User>(new HierarchicalLifetimeManager());
            container.RegisterType<Doctor, Doctor>(new HierarchicalLifetimeManager());
            container.RegisterType<Patient, Patient>(new HierarchicalLifetimeManager());
            container.RegisterType<Exam, Exam>(new HierarchicalLifetimeManager());
            container.RegisterType<ExamStep, ExamStep>(new HierarchicalLifetimeManager());
            container.RegisterType<BaseExam, BaseExam>(new HierarchicalLifetimeManager());

            return container;
        }
    }
}