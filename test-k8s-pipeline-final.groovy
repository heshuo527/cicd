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
            """
        }
    }
    stages {
        stage('Test K8s Connection') {
            steps {
                container('kubectl') {
                    script {
                        withCredentials([file(credentialsId: 'k8s-config', variable: 'KUBECONFIG')]) {
                            sh """
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