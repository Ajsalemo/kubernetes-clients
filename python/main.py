from kubernetes import config
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
    

def main():
    print("Hello from python!")


if __name__ == "__main__":
    main()
