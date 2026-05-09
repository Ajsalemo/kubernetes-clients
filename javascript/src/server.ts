import * as k8s from "@kubernetes/client-node";
import Fastify, { FastifyInstance } from "fastify";

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
