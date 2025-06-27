pipeline {
    agent {
        kubernetes {
            yaml """
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: tools
                image: alpine:latest
                command: ['cat']
                tty: true
            """
        }
    }
    stages {
        stage('Test Connection') {
            steps {
                container('tools') {
                    sh '''
                        echo "=== Pod creation successful! ==="
                        echo "Current time: $(date)"
                        echo "Pod info:"
                        whoami
                        pwd
                        ls -la
                        echo "=== Test completed successfully! ==="
                    '''
                }
            }
        }
    }
} 