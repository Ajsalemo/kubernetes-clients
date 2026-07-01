using k8s;
using k8s.Models;
using Microsoft.AspNetCore.Mvc;

namespace kubernetes_clients.Controllers
{
    // Route: /pod/list/all/{namespace}
    [Route("/[controller]")]
    [ApiController]
    public class GetPodController(Kubernetes client) : ControllerBase
    {
        private readonly Kubernetes _client = client;

        [HttpGet("/pod/list/all/{namespace?}")]
        public ActionResult<string> GetPod(string @namespace)
        {
            // If no namespace is provided, default to "default"
            if (string.IsNullOrEmpty(@namespace))
            {
                @namespace = "default";
            }

            var pods = _client.CoreV1.ListNamespacedPod(@namespace);
            return Ok(pods.Items.Select(p => p.Metadata.Name).ToList());
        }
    }
}