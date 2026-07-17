using k8s;
using Microsoft.AspNetCore.Mvc;

namespace kubernetes_clients.Controllers
{
    [ApiController]
    [Route("/[controller]")]
    public class CreateDeploymentController(Kubernetes client, ILogger<CreateDeploymentController> logger) : ControllerBase
    {
        private readonly Kubernetes _client = client;
        private readonly ILogger<CreateDeploymentController> _logger = logger;

        [HttpPost("/deployment/create")]
        public IActionResult CreateDeployment([FromBody] Models.Deployment deployment, string @namespace)
        {
            // If no namespace is provided, default to "default"
            if (string.IsNullOrEmpty(@namespace))
            {
                @namespace = "default";
            }
            // Cast deployment into k8s.Models.V1Deployment
            var v1Deployment = new k8s.Models.V1Deployment
            {
                ApiVersion = "apps/v1",
                Kind = "Deployment",
                Metadata = new k8s.Models.V1ObjectMeta
                {
                    Name = deployment.Metadata.Name,
                    NamespaceProperty = @namespace
                },
                Spec = new k8s.Models.V1DeploymentSpec
                {
                    Replicas = deployment.Spec.Replicas,
                    Selector = new k8s.Models.V1LabelSelector
                    {
                        MatchLabels = new Dictionary<string, string> { { "app", deployment.Spec.Selector.MatchLabels.App } }
                    },
                    Template = new k8s.Models.V1PodTemplateSpec
                    {
                        Metadata = new k8s.Models.V1ObjectMeta
                        {
                            Labels = new Dictionary<string, string> { { "app", deployment.Spec.Template.Metadata.Labels.App } }
                        },
                        Spec = new k8s.Models.V1PodSpec
                        {
                            Containers = deployment.Spec.Template.Spec.Containers.Select(c => new k8s.Models.V1Container
                            {
                                Name = c.Name,
                                Image = c.Image
                                // TODO - fix the usage of `Select` here - its returning an error due to it cant infer types
                                // Ports = c.Ports?.Select(p => new k8s.Models.V1ContainerPort { ContainerPort = p }).ToList()
                            }).ToList()
                        }
                    }
                }
            };

            _logger.LogInformation("Creating deployment with name: {Name}", deployment.Metadata.Name);

            _client.CreateNamespacedDeployment(v1Deployment, @namespace);

            // Implementation for creating a deployment goes here
            return Ok();
        }

    }
}