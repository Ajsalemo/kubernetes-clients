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
        public IActionResult CreateDeployment([FromBody] Models.Deployment deployment)
        {
            _logger.LogInformation("Creating deployment with name: {Name}", deployment.Metadata.Name);

            // Implementation for creating a deployment goes here
            return Ok();
        }

    }
}