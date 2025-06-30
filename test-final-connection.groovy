pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: test
                image: alpine:3.18
                command: ['sleep', '300']
                resources:
                  requests:
                    memory: "64Mi"
                    cpu: "25m"
            """
        }
    }
    stages {
        stage('Final Connection Test') {
            steps {
                container('test') {
                    sh """
                    echo "🎉 Jenkins Agent连接成功！"
                    echo "Pod名称: \$HOSTNAME"
                    echo "当前时间: \$(date)"
                    echo "IP地址: \$(hostname -i)"
                    echo ""
                    echo "✅ 如果您看到这条消息，说明Jenkins TCP端口问题已解决！"
                    echo "✅ Kubernetes Agent现在可以正常连接到Jenkins了！"
                    """
                }
            }
        }
    }
} 