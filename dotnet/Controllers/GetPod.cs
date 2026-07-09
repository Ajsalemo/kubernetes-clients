using k8s;
using Microsoft.AspNetCore.Mvc;

namespace kubernetes_clients.Controllers
{
    // Route: /pod/list/all/{namespace}
    [Route("/[controller]")]
    [ApiController]
    public class GetPodController(Kubernetes client, ILogger<GetPodController> logger) : ControllerBase
    {
        private readonly Kubernetes _client = client;
        private readonly ILogger<GetPodController> _logger = logger;

        [HttpGet("/pod/list/all/{namespace?}")]
        public ActionResult<string> GetPod(string @namespace)
        {
            // If no namespace is provided, default to "default"
            if (string.IsNullOrEmpty(@namespace))
            {
                @namespace = "default";
            }

            var pods = _client.CoreV1.ListNamespacedPod(@namespace);
            _logger.LogInformation($"Retrieved {pods.Items.Count} pods in namespace '{@namespace}'.");
            return Ok(pods.Items.Select(p => p.Metadata.Name).ToList());
        }
    }
}