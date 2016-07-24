using Newtonsoft.Json.Linq;
using System.Linq;
using System.Net.Http;
using System.Web.Http.Controllers;
using System.Web.Http.OData;
using System.Web.Http.OData.Routing;
using System.Web.Http.OData.Routing.Conventions;

namespace ALSRM.Api2
{

    public class CustomConvention : EntitySetRoutingConvention
    {
        public override string SelectAction(ODataPath odataPath, HttpControllerContext context, ILookup<string, HttpActionDescriptor> actionMap)
        {
            if (context.Request.Method == HttpMethod.Post && odataPath.PathTemplate == "~/entityset/key/navigation/key/navigation")
            {
               // ActionPathSegment actionSegment = odataPath.Segments[4] as ActionPathSegment;
                NavigationPathSegment navigationSegment = odataPath.Segments[4] as NavigationPathSegment;
                var navigationProperty = navigationSegment?.NavigationProperty;

                //Create Action name
                string actionName = navigationProperty?.Name;                

                //string actionName = actionSegment.ActionName.Replace("Default.Container.", "");
                if (actionMap.Contains(actionName))
                {
                    // Add keys to route data, so they will bind to action parameters.
                    KeyValuePathSegment keyValueSegment = odataPath.Segments[1] as KeyValuePathSegment;
                    context.RouteData.Values[ODataRouteConstants.Key] = keyValueSegment?.Value;

                    KeyValuePathSegment relatedKeySegment = odataPath.Segments[3] as KeyValuePathSegment;
                    context.RouteData.Values[ODataRouteConstants.RelatedKey] = relatedKeySegment?.Value;

                    context.RouteData.Values["jsonObject"] = context.Request.Content.ReadAsAsync<JObject>().Result;

                    return actionName;
                }
            }

            if (context.Request.Method == HttpMethod.Post && odataPath.PathTemplate == "~/entityset/key/navigation/key/action")
               {
                // ActionPathSegment actionSegment = odataPath.Segments[4] as ActionPathSegment;
                ActionPathSegment navigationSegment = odataPath.Segments[4] as ActionPathSegment;
               
                //Create Action name
                //string actionName = navigationSegment?.ActionName;

                string actionName = navigationSegment.ActionName.Replace("Default.Container.", "");
                if (actionMap.Contains(actionName))
                {
                    // Add keys to route data, so they will bind to action parameters.
                    KeyValuePathSegment keyValueSegment = odataPath.Segments[1] as KeyValuePathSegment;
                    context.RouteData.Values[ODataRouteConstants.Key] = keyValueSegment?.Value;

                    KeyValuePathSegment relatedKeySegment = odataPath.Segments[3] as KeyValuePathSegment;
                    context.RouteData.Values[ODataRouteConstants.RelatedKey] = relatedKeySegment?.Value;

                    context.RouteData.Values["jsonObject"] = context.Request.Content.ReadAsAsync<JObject>().Result;

                    return actionName;
                }
            }


            // Not a match.
            return null;
        }
    }
}