pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: docker
                image: docker:24.0-dind
                command: ['cat']
                tty: true
                securityContext:
                  privileged: true
                volumeMounts:
                  - mountPath: /var/run/docker.sock
                    name: docker-sock
                resources:
                  requests:
                    memory: "512Mi"
                    cpu: "200m"
                  limits:
                    memory: "1Gi"
                    cpu: "500m"
              - name: kubectl
                image: bitnami/kubectl:1.28
                command: ['cat']
                tty: true
                resources:
                  requests:
                    memory: "128Mi"
                    cpu: "50m"
                  limits:
                    memory: "256Mi"
                    cpu: "100m"
              volumes:
                - name: docker-sock
                  hostPath:
                    path: /var/run/docker.sock
              activeDeadlineSeconds: 1800
            """
        }
    }
    environment {
        DOCKER_IMAGE = "heshuo527/cicd-app:${env.BUILD_NUMBER}"
        KUBE_NAMESPACE = "default"
    }
    options {
        timeout(time: 30, unit: 'MINUTES')
        retry(2)
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
                        timeout(time: 15, unit: 'MINUTES') {
                            sh """
                            echo "开始构建Docker镜像..."
                            docker info
                            docker build -t ${DOCKER_IMAGE} .
                            echo "Docker镜像构建完成: ${DOCKER_IMAGE}"
                            """
                        }
                    }
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                container('docker') {
                    script {
                        timeout(time: 10, unit: 'MINUTES') {
                            withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                sh """
                                echo "登录Docker Hub..."
                                echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
                                echo "推送镜像到Docker Hub..."
                                docker push ${DOCKER_IMAGE}
                                echo "镜像推送完成"
                                """
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy to K8s') {
            steps {
                container('kubectl') {
                    script {
                        timeout(time: 10, unit: 'MINUTES') {
                            withCredentials([file(credentialsId: 'k8s-config', variable: 'KUBECONFIG')]) {
                                sh """
                                echo "开始部署到Kubernetes..."
                                kubectl version --client
                                
                                kubectl create namespace ${KUBE_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f - || true
                                
                                sed -i.bak 's|heshuo527/cicd-app:.*|${DOCKER_IMAGE}|g' k8s/deployment.yaml
                                
                                kubectl -n ${KUBE_NAMESPACE} apply -f k8s/
                                
                                kubectl -n ${KUBE_NAMESPACE} rollout status deployment/cicd-app --timeout=300s
                                
                                echo "部署完成！"
                                """
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            echo "Pipeline执行完成"
        }
        success {
            echo "✅ CI/CD Pipeline执行成功！"
        }
        failure {
            echo "❌ Pipeline执行失败"
        }
        cleanup {
            echo "清理工作空间"
        }
    }
}