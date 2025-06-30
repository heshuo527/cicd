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
                    cpu: "100m"
            """
        }
    }
    stages {
        stage('Test K8s Connection') {
            steps {
                container('kubectl') {
                    script {
                        withCredentials([file(credentialsId: 'k8s-config-fixed', variable: 'KUBECONFIG')]) {
                            sh """
                            echo "测试Kubernetes连接..."
                            echo "KUBECONFIG文件路径: \$KUBECONFIG"
                            
                            echo "显示kubectl版本:"
                            kubectl version --client --output=yaml
                            
                            echo "显示集群信息:"
                            kubectl cluster-info
                            
                            echo "列出命名空间:"
                            kubectl get namespaces
                            
                            echo "检查节点状态:"
                            kubectl get nodes
                            
                            echo "✅ Kubernetes连接测试成功！"
                            """
                        }
                    }
                }
            }
        }
    }
} 