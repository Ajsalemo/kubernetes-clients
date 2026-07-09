using k8s;
using Microsoft.AspNetCore.Mvc;

namespace kubernetes_clients.Controllers
{
    // Route: /pod/get/{namespace}/{name}
    [Route("/[controller]")]
    [ApiController]
    public class GetSpecificPodController(Kubernetes client, ILogger<GetSpecificPodController> logger) : ControllerBase
    {
        private readonly Kubernetes _client = client;
        private readonly ILogger<GetSpecificPodController> _logger = logger;
        // GET: /pod/get?namespace={namespace}&name={name}
        [HttpGet("/pod/get/")]
        public ActionResult<string> GetSpecificPod(string @namespace, string name)
        {
            // If no namespace is provided, default to "default"
            if (string.IsNullOrEmpty(@namespace))
            {
                @namespace = "default";
            }

            var pod = _client.CoreV1.ReadNamespacedPod(name, @namespace);
            _logger.LogInformation($"Retrieved pod '{name}' in namespace '{@namespace}'.");
            return Ok(pod);
        }
    }
}