using ALSRM.Api2.Helpers;
using ALSRM.Api2.Security;
using ALSRM.Domain.Contracts.Services;
using ALSRM.Domain.Models;
using ALSRM.Startup;
using Microsoft.Data.Edm;
using Microsoft.Owin;
using Microsoft.Owin.Security.OAuth;
using Microsoft.Practices.Unity;
using Owin;
using System;
using System.Web.Http;
using System.Web.Http.OData.Batch;
using System.Web.Http.OData.Builder;
using System.Web.Http.OData.Extensions;
using System.Web.Http.OData.Routing;
using System.Web.Http.OData.Routing.Conventions;

namespace ALSRM.Api2
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            HttpConfiguration config = new HttpConfiguration();

            var container =  ConfigureDependencyResolver(config);

            ConfigureWebApi(config);
            ConfigureOAuth(app, container.Resolve<IUserService>());

            app.UseCors(Microsoft.Owin.Cors.CorsOptions.AllowAll);
            app.UseWebApi(config);

            
        }

        public static void ConfigureWebApi(HttpConfiguration config)
        {           

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            
            var conventions = ODataRoutingConventions.CreateDefault();
            // Insert the custom convention at the start of the collection.
            conventions.Insert(0, new CustomConvention());

            config.Routes.MapODataServiceRoute("odata", "odata",
                                        GetEdmModel(), new DefaultODataPathHandler(), conventions,
                                        new DefaultODataBatchHandler(GlobalConfiguration.DefaultServer));
            config.EnsureInitialized();
            



            //config.Routes.MapODataServiceRoute("odata", "odata", GetEdmModel());
    }

        private static IEdmModel GetEdmModel()
        {
            ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
            builder.EntitySet<BaseUser>("Users");
            builder.EntitySet<Exam>("Exams");
            builder.EntitySet<ExamStep>("ExamSteps");
            builder.EntitySet<Point>("Points");
            builder.EntitySet<Muscle>("Muscles");


            // Actions for User
            ActionConfiguration changePassword = builder.Entity<BaseUser>().Action("ChangePassword");
            changePassword.Parameter<string>("Password");
            changePassword.Parameter<string>("NewPassword");
            changePassword.Parameter<string>("ConfirmNewPassword");
            changePassword.Parameter<string>("SecurityAnswer");
            changePassword.Returns<string>();

            ActionConfiguration resetPassword = builder.Entity<BaseUser>().Action("ResetPassword");
            resetPassword.Parameter<string>("SecurityAnswer");
            resetPassword.Returns<string>();

            ActionConfiguration changeSecurityQuestion = builder.Entity<BaseUser>().Action("ChangeSecurityQuestion");
            changeSecurityQuestion.Parameter<string>("SecurityQuestion");
            changeSecurityQuestion.Parameter<string>("SecurityAnswer");
            changeSecurityQuestion.Parameter<string>("Password");

            ActionConfiguration changeMacBitalino = builder.Entity<BaseUser>().Action("ChangeMacBitalino");
            changeMacBitalino.Parameter<string>("Mac_Bitalino");

            ActionConfiguration postExamSteps = builder.Entity<Exam>().Action("ExamStep");
            postExamSteps.Parameter<int>("ExamId");
            postExamSteps.Parameter<string>("Description");
            postExamSteps.Parameter<string>("State");
            postExamSteps.Parameter<int>("Time");
            postExamSteps.Parameter<DateTime?>("InitialDate");
            postExamSteps.Parameter<DateTime?>("EndDate");
            postExamSteps.ReturnsFromEntitySet<ExamStep>("ExamSteps");

            builder.Entity<ExamStep>().Action("Cancel");

            builder.Entity<Exam>().Action("Cancel");






            return builder.GetEdmModel();
        }

        // Configure Dependencies
        private UnityContainer ConfigureDependencyResolver(HttpConfiguration config)
        {
            var container = new UnityContainer();
            DependencyResolver.Resolve(container);
            config.DependencyResolver = new UnityResolver(container);
            return container;
        }

        public void ConfigureOAuth(IAppBuilder app, IUserService service)
        {
            OAuthAuthorizationServerOptions OAuthServerOptions = new OAuthAuthorizationServerOptions()
            {
                AllowInsecureHttp = true,
                TokenEndpointPath = new PathString("/odata/security/token"),
                AccessTokenExpireTimeSpan = TimeSpan.FromHours(4),
                Provider = new AuthorizationServerProvider(service)
            };

            // Token Generation
            app.UseOAuthAuthorizationServer(OAuthServerOptions);
            app.UseOAuthBearerAuthentication(new OAuthBearerAuthenticationOptions());
        }
    }
}