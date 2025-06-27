pipeline {
    agent {
        kubernetes {
            label "cicd-agent"
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: docker
                image: docker:24.0
                command: ['cat']
                tty: true
                volumeMounts:
                  - mountPath: /var/run/docker.sock
                    name: docker-sock
              - name: kubectl
                image: bitnami/kubectl:1.28
                command: ['cat']
                tty: true
              volumes:
                - name: docker-sock
                  hostPath:
                    path: /var/run/docker.sock
            """
        }
    }
    environment {
        DOCKER_IMAGE = "heshuo527/cicd-app:${env.BUILD_NUMBER}"
        KUBE_NAMESPACE = "cicd-demo"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Docker Image') {
            steps {
                container('docker') {
                    script {
                        sh """
                        docker build -t ${DOCKER_IMAGE} .
                        """
                    }
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                container('docker') {
                    script {
                        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                            sh """
                            echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                            docker push ${DOCKER_IMAGE}
                            """
                        }
                    }
                }
            }
        }
        stage('Deploy to K8s') {
            steps {
                container('kubectl') {
                    script {
                        withCredentials([file(credentialsId: 'k8s-config', variable: 'KUBECONFIG')]) {
                            sh """
                            kubectl create namespace ${KUBE_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                            sed -i 's|heshuo527/cicd-app:latest|${DOCKER_IMAGE}|g' k8s/deployment.yaml
                            kubectl -n ${KUBE_NAMESPACE} apply -f k8s/
                            kubectl -n ${KUBE_NAMESPACE} rollout status deployment/cicd-app
                            """
                        }
                    }
                }
            }
        }
    }
}