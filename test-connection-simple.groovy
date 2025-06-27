pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: tools
                image: alpine:3.18
                command: ['sleep', '3600']
                resources:
                  requests:
                    memory: "64Mi"
                    cpu: "25m"
                  limits:
                    memory: "128Mi"
                    cpu: "100m"
            """
            // 明确指定Jenkins连接配置
            jenkinsUrl 'http://host.docker.internal:8080'
            jenkinsTunnel 'host.docker.internal:50000'
            // 增加等待时间
            activeDeadlineSeconds: 300
        }
    }
    stages {
        stage('Connection Test') {
            steps {
                container('tools') {
                    sh """
                    echo "=== 基本信息 ==="
                    whoami
                    pwd
                    
                    echo "=== 安装网络工具 ==="
                    apk add --no-cache curl netcat-openbsd
                    
                    echo "=== 网络连接测试 ==="
                    echo "测试Jenkins连接..."
                    nc -zv host.docker.internal 8080 || echo "无法连接到Jenkins 8080端口"
                    nc -zv host.docker.internal 50000 || echo "无法连接到Jenkins 50000端口"
                    
                    echo "=== 测试完成 ==="
                    """
                }
            }
        }
    }
} 