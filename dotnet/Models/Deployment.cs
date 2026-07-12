namespace kubernetes_clients.Models
{
    public class Deployment
    {
        public required Metadata Metadata { get; set; }
        public required Spec Spec { get; set; }
    }
}