import * as k8s from "@kubernetes/client-node";
import Fastify, { FastifyInstance } from "fastify";

type V1ResourceRequirements = {
    requests?: { [key: string]: string };
    limits?: { [key: string]: string };
}

type V1Container = {
    name: string;
    image: string;
    resources: V1ResourceRequirements;
}

type V1TemplateSpec = {
    metadata: {
        labels: { [key: string]: string };
    };
    spec: {
        containers: V1Container[];
    };
}

type V1Spec = {
    replicas: number;
    selector: V1Selector;
    template: V1TemplateSpec;
}

type V1Selector = {
    matchLabels: { [key: string]: string };
}

type V1Metadata = {
    name: string;
    namespace: string | "default";
}

type DeploymentSpec = {
    metadata: V1Metadata;
    spec: V1Spec;
}

const kc = new k8s.KubeConfig();
kc.loadFromDefault();

const k8sApi = kc.makeApiClient(k8s.CoreV1Api);
// Create Fastify instance
const server: FastifyInstance = Fastify({
    logger: true
});

// HTTP GET - list all pods in a namespace (default namespace is "default")
// Request URL shape: /pods/list/all?namespace=<namespace>
server.get<{ Querystring: { namespace: string } }>("/pods/list/all", async (request, _) => {
    let { namespace } = request.query;
    // If namespace is empty, default to "default"
    if (!namespace) {
        namespace = "default";
    }
    // Line 26 { namespace } as .. - is syntax to fit the shape of the below interface that CoreV1ApiListNamespacedPodRequest is
    // and which contains `namespace` 
    // export interface CoreV1ApiListNamespacedPodRequest {
    //     namespace: string;
    //     ..other props
    // }
    const n = { namespace } as k8s.CoreV1ApiListNamespacedPodRequest;
    const res = await k8sApi.listNamespacedPod(n);
    return { pods: res.items };
});

// HTTP GET - find a specific pod in a namespace
// Request URL shape: /pod/get?pod=<podname>&namespace=<namespace>
server.get<{ Querystring: { pod: string, namespace?: string } }>("/pod/get", async (request, response) => {
    const { pod } = request.query;
    let { namespace } = request.query;
    // If no pod is provided then return an HTTP 400
    if (!pod) {
        return response.status(400).send({ error: "Pod name is required" });
    }
    // If namespace is empty, default to "default"
    if (!namespace) {
        namespace = "default";
    }
    // Same as line 20, fit the shape of the below interface that CoreV1ApiReadNamespacedPodRequest is
    const p = { name: pod, namespace } as k8s.CoreV1ApiReadNamespacedPodRequest;
    const getPod = await k8sApi.readNamespacedPod(p);
    return { pod: getPod.spec };
});

// HTTP POST - create a pod in a namespace
// Request URL shape: /pod/create?namespace=<namespace>
server.post<{ Querystring: { namespace?: string }, Body: { metadata: V1Metadata, spec: V1Spec } }>("/pod/create", async (request) => {
    const { metadata, spec } = request.body;
    console.log("Received deployment spec:", { metadata, spec });
    let { namespace } = request.query;
    // If namespace is empty, default to "default"
    if (!namespace) {
        namespace = "default";
    }
    // Create a pod manifest based on the deployment spec
    const podManifest: k8s.V1Deployment = {
        metadata: {
            name: metadata.name,
            namespace: metadata.namespace
        },
        spec: {
            replicas: parseInt(spec.replicas.toString(), 10),
            selector: {
                matchLabels: {
                    app: metadata.name
                }
            },
            template: {
                metadata: {
                    labels: {
                        app: metadata.name
                    }
                },
                spec: {
                    containers: spec.template.spec.containers.map(container => ({
                        name: container.name,
                        image: container.image,
                        resources: container.resources
                    }))
                }
            }
        }
    };
    // Same as line 20, fit the shape of the below interface that CoreV1ApiCreateNamespacedPodRequest is
    const p = { namespace, body: podManifest } as k8s.CoreV1ApiCreateNamespacedPodRequest;
    const createPod = await k8sApi.createNamespacedPod(p);
    return { pod: createPod.spec };
});

// Start server
const start = async () => {
    try {
        await server.listen({ port: 3000, host: "0.0.0.0" });
        server.log.info(`Server running at http://0.0.0.0:3000`);
    } catch (err) {
        server.log.error(err);
        process.exit(1);
    }
};

try {
    start();
} catch (err) {
    server.log.error(err);
    process.exit(1);
}
