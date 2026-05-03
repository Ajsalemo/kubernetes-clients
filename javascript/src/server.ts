import * as k8s from '@kubernetes/client-node';
import Fastify, { FastifyInstance } from 'fastify';

const kc = new k8s.KubeConfig();
kc.loadFromDefault();

const k8sApi = kc.makeApiClient(k8s.CoreV1Api);
// Create Fastify instance
const server: FastifyInstance = Fastify({
    logger: true
});

// GET route with query parameter validation
server.get<{ Querystring: { namespace: string } }>('/pods/list/all', async (request, reply) => {
    let { namespace } = request.query;
    // If namespace is empty, default to "default"
    if (!namespace) {
        namespace = "default";
    }
    const n = { namespace } as k8s.CoreV1ApiListNamespacedPodRequest;
    const res = await k8sApi.listNamespacedPod(n);
    return { pods: res.items };
});

// Start server
const start = async () => {
    try {
        await server.listen({ port: 3000, host: '0.0.0.0' });
        server.log.info(`Server running at http://0.0.0.0:3000`);
    } catch (err) {
        server.log.error(err);
        process.exit(1);
    }
};

start();
