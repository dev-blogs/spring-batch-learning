pipeline {
	agent {
		kubernetes {
			label 'custom-k8s-agent'
			yamlFile 'mvnBuildPod.yaml'
			defaultContainer 'jnlp'
		}
	}

	environment {
		CONFIG="/tmp/config"
		DOCKER_HUB_LOGIN="devblogs1"
		SERVICE_NAME="spring-batch-app"
		DOCKER_IMAGE="${DOCKER_HUB_LOGIN}/${SERVICE_NAME}"
		NAMESPACE="image-uploader"
		DOCKER_HUB_PASSWORD_SECRET="docker-hub-password"
		OS_HOST="https://ocp1.192.168.1.20.nip.io:8443"
		OS_USER="dev"
		OS_PASSWORD="dev"
	}

	stages {
		stage('Checkout') {
			steps {
				container('java-container') {
					sh 'echo "Hello from custom container!"'
					sh 'printenv CUSTOM_ENV_VAR'
					sh 'git clone https://github.com/dev-blogs/test.git'
				}
			}
		}

		stage('Build') {
			steps {
				container('java-container') {
					dir ('test') {
						sh "mvn -Dmaven.repo.local=/usr/.m2/repository clean install"
						build_image()
					}
				}
			}
		}

		stage('Deploy') {
			steps {
				container('java-container') {
					dir ('test') {
						deploy_image()
					}
				}
			}
		}

		stage('Check') {
		    steps {
                container('java-container') {
                    dir ('test/target') {
                        sh "ls -la"
                    }
                }
            }
        }
    }
}

def build_image() {
	sh "docker build -t ${SERVICE_NAME} ."
}

def deploy_image() {
	sh '''
		export DOCKER_CONFIG=/tmp/docker-config

		/usr/bin/oc login --insecure-skip-tls-verify --config=${CONFIG} -u ${OS_USER} -p ${OS_PASSWORD} ${OS_HOST}
		/usr/bin/oc get secret ${DOCKER_HUB_PASSWORD_SECRET} --config=${CONFIG} -n ${NAMESPACE} -o go-template --template="{{.data.password}}" | base64 -d | docker login -u ${DOCKER_HUB_LOGIN} --password-stdin

		docker tag ${SERVICE_NAME} ${DOCKER_HUB_LOGIN}/${SERVICE_NAME}
		docker push ${DOCKER_HUB_LOGIN}/${SERVICE_NAME}

		# Check if the service exists
		if /usr/bin/oc get service --config=${CONFIG} ${SERVICE_NAME} -n ${NAMESPACE} > /dev/null 2>&1; then
			echo "Service ${SERVICE_NAME} exists. Replacing it with the new deployment..."

			/usr/bin/oc delete service ${SERVICE_NAME} -n ${NAMESPACE} --config=${CONFIG}
			/usr/bin/oc delete dc ${SERVICE_NAME} -n ${NAMESPACE} --config=${CONFIG}
			/usr/bin/oc delete route ${SERVICE_NAME} -n ${NAMESPACE} --config=${CONFIG}
			/usr/bin/oc delete imagestream test-app -n ${NAMESPACE} --config=${CONFIG}
		else
			echo "Service ${SERVICE_NAME} does not exist. Creating a new deployment..."
		fi

		# Deploy the application
		/usr/bin/oc new-app --docker-image=${DOCKER_IMAGE} --name=${SERVICE_NAME} -n ${NAMESPACE} --config=${CONFIG}

		# Expose the service (if needed)
		if ! /usr/bin/oc get route ${SERVICE_NAME} -n ${NAMESPACE} --config=${CONFIG} > /dev/null 2>&1; then
			/usr/bin/oc expose service ${SERVICE_NAME} -n ${NAMESPACE} --config=${CONFIG}
		fi

		echo "Deployment complete"
	'''
}