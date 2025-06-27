pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: debug
                image: alpine:3.18
                command: ['sleep', '3600']
                resources:
                  requests:
                    memory: "64Mi"
                    cpu: "25m"
            """
        }
    }
    stages {
        stage('Debug Network') {
            steps {
                container('debug') {
                    sh """
                    echo "=== 网络调试信息 ==="
                    
                    # 安装网络工具
                    apk add --no-cache curl netcat-openbsd nmap
                    
                    echo "=== 测试DNS解析 ==="
                    nslookup host.docker.internal
                    
                    echo "=== 测试网络连通性 ==="
                    ping -c 3 host.docker.internal
                    
                    echo "=== 测试端口连接 ==="
                    nc -zv host.docker.internal 8080
                    nc -zv host.docker.internal 50000
                    
                    echo "=== 尝试HTTP连接 ==="
                    curl -I http://host.docker.internal:8080/ --connect-timeout 10 || echo "HTTP连接失败"
                    
                    echo "=== 网络接口信息 ==="
                    ip addr show
                    
                    echo "=== 路由信息 ==="
                    ip route show
                    """
                }
            }
        }
    }
} 