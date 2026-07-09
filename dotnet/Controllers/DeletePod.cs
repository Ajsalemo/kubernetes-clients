using k8s;
using Microsoft.AspNetCore.Mvc;

namespace kubernetes_clients.Controllers
{
    [Route("/[controller]")]
    [ApiController]
    public class DeletePodController(Kubernetes client, ILogger<DeletePodController> logger) : ControllerBase
    {
        private readonly Kubernetes _client = client;
        private readonly ILogger<DeletePodController> _logger = logger;
        // DELETE: /pod/delete?namespace={namespace}&name={name}
        [HttpDelete("/pod/delete")]
        public ActionResult<string> DeleteSpecificPod(string @namespace, string name)
        {
            // If no namespace is provided, default to "default"
            if (string.IsNullOrEmpty(@namespace))
            {
                @namespace = "default";
            }

            _client.CoreV1.DeleteNamespacedPod(name, @namespace);
            _logger.LogInformation($"Pod '{name}' in namespace '{@namespace}' has been deleted.");
            return Ok($"Pod '{name}' in namespace '{@namespace}' has been deleted.");
        }
    }
}