namespace kubernetes_clients.Models
{
    public class Spec
    {
        public required int Replicas { get; set; }
        public required Selector Selector { get; set; }
        public required Template Template { get; set; }
    }
}