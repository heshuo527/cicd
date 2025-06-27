pipeline {
    agent any
    stages {
        stage('Test K8s Connection') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'k8s-config', variable: 'KUBECONFIG')]) {
                        // 在Jenkins主节点安装kubectl的情况下运行
                        sh '''
                            echo "=== 测试Kubernetes连接 ==="
                            echo "Kubeconfig文件路径: $KUBECONFIG"
                            
                            # 检查kubectl是否可用
                            if command -v kubectl >/dev/null 2>&1; then
                                echo "kubectl已安装"
                                kubectl version --client
                                echo "=== 获取集群信息 ==="
                                kubectl cluster-info
                                echo "=== 获取节点信息 ==="
                                kubectl get nodes
                                echo "=== 测试成功！==="
                            else
                                echo "kubectl未安装在Jenkins主节点上"
                                echo "需要使用Kubernetes agent方式运行"
                            fi
                        '''
                    }
                }
            }
        }
    }
} 