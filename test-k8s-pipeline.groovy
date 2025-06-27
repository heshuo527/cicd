pipeline {
    agent {
        kubernetes {
            label "k8s-test-agent"
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
                            echo "Testing Kubernetes connection..."
                            kubectl get nodes
                            kubectl get namespaces
                            echo "Kubernetes connection test completed successfully!"
                            """
                        }
                    }
                }
            }
        }
    }
} 