from kubernetes import config, client
from src.listallpods import listAllPods
import logging, sys

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# load the kubeconfig
try:
    logger.info("Loading kubeconfig..")
    config.load_kube_config()
    logger.info("kubeconfig loaded successfully.")
except config.ConfigException as e:
    logger.error("Failed to load kubeconfig: %s", e)
    sys.exit(1)
    
# create the api client
# pass this in to any functions
v1 = client.CoreV1Api()

def main():
    # list all pods in the cluster
    listAllPods(v1)


if __name__ == "__main__":
    main()
