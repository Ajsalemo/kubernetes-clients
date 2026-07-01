using k8s;

var builder = WebApplication.CreateBuilder(args);
// Load from the default kubeconfig on the machine.
var config = KubernetesClientConfiguration.BuildConfigFromConfigFile();
// Use the config object to create a client.
var client = new Kubernetes(config);
// Dependency injection for the Kubernetes client
builder.Services.AddSingleton(client);
// Add services for controllers
builder.Services.AddControllers();
// Add services to the container.
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

app.MapControllers();


app.Run();
