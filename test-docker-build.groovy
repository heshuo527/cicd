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
              volumes:
                - name: docker-sock
                  hostPath:
                    path: /var/run/docker.sock
              activeDeadlineSeconds: 1800
            """
        }
    }
    options {
        timeout(time: 15, unit: 'MINUTES')
    }
    stages {
        stage('Test Docker') {
            steps {
                container('docker') {
                    sh """
                    echo "=== Docker测试开始 ==="
                    echo "检查Docker版本..."
                    docker --version
                    
                    echo "检查Docker信息..."
                    docker info
                    
                    echo "测试Docker构建..."
                    echo 'FROM alpine:latest' > Dockerfile.test
                    echo 'RUN echo "Hello World"' >> Dockerfile.test
                    
                    docker build -t test-image:latest -f Dockerfile.test .
                    
                    echo "✅ Docker构建测试成功！"
                    echo "清理测试文件..."
                    rm -f Dockerfile.test
                    docker rmi test-image:latest || true
                    
                    echo "=== Docker测试完成 ==="
                    """
                }
            }
        }
    }
} 