pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
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
                    cpu: "200m"
            """
            jenkinsUrl 'http://host.docker.internal:8080'
            jenkinsTunnel 'host.docker.internal:50000'
        }
    }
    stages {
        stage('Test K8s Connection') {
            steps {
                container('kubectl') {
                    script {
                        withCredentials([file(credentialsId: 'k8s-config', variable: 'KUBECONFIG')]) {
                            sh """
                            echo "=== 检查网络连接 ==="
                            ping -c 3 host.docker.internal || echo "无法ping通host.docker.internal"
                            nslookup host.docker.internal || echo "DNS解析失败"
                            
                            echo "=== 测试Kubernetes连接 ==="
                            kubectl version --client
                            
                            echo "=== 获取集群信息 ==="
                            kubectl cluster-info
                            
                            echo "=== 获取节点信息 ==="
                            kubectl get nodes
                            
                            echo "=== 获取命名空间 ==="
                            kubectl get namespaces
                            
                            echo "=== 测试成功！==="
                            """
                        }
                    }
                }
            }
        }
    }
} 