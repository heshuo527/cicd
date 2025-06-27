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
        stage('Test Connection') {
            steps {
                container('test') {
                    sh """
                    echo "✅ Jenkins Agent连接成功！"
                    echo "Pod名称: \$HOSTNAME"
                    echo "当前时间: \$(date)"
                    echo "网络测试完成"
                    """
                }
            }
        }
    }
} 