namespace kubernetes_clients.Models
{
    public class Containers
    {
        public required string Name { get; set; }
        public required string Image { get; set; }
        public required Resources Resources { get; set; }
        public required Ports[] Ports { get; set; }
    }
}