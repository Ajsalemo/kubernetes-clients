using Microsoft.AspNetCore.Mvc;

namespace kubernetes_clients.Controllers
{
    // Route: /api/getpod
    [Route("[controller]")]
    [ApiController]
    public class GetPodController : ControllerBase
    {
        [HttpGet("/pod/list/all/{namespace}")]
        public ActionResult<string> GetPod(string @namespace)
        {
            Console.WriteLine($"Namespace: {@namespace}");
            return Ok("t");
        }
    }
}