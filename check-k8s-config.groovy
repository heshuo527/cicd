// 检查Jenkins Kubernetes云配置脚本
// 在Jenkins的Script Console中运行此脚本
// 路径：Manage Jenkins -> Script Console

import jenkins.model.Jenkins
import org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud

println "=== Jenkins Kubernetes云配置检查 ==="

def jenkins = Jenkins.instance
def clouds = jenkins.clouds

if (clouds.isEmpty()) {
    println "❌ 没有配置任何云"
    println ""
    println "需要配置Kubernetes云："
    println "1. 进入 Manage Jenkins -> Manage Nodes and Clouds -> Configure Clouds"
    println "2. 添加 Kubernetes 云"
    println "3. 配置以下参数："
    println "   - Name: kubernetes"
    println "   - Kubernetes URL: https://kubernetes.docker.internal:6443"
    println "   - Jenkins URL: http://host.docker.internal:8080"
    println "   - Jenkins tunnel: host.docker.internal:50000"
    println "   - 勾选 'Disable https certificate check'"
    println "   - Credentials: k8s-config"
} else {
    clouds.each { cloud ->
        println "云名称: ${cloud.name}"
        println "云类型: ${cloud.class.simpleName}"
        
        if (cloud instanceof KubernetesCloud) {
            println "  ✅ Kubernetes云配置："
            println "  - Kubernetes URL: ${cloud.serverUrl}"
            println "  - Jenkins URL: ${cloud.jenkinsUrl ?: '❌ 未设置'}"
            println "  - Jenkins Tunnel: ${cloud.jenkinsTunnel ?: '❌ 未设置'}"
            println "  - 命名空间: ${cloud.namespace}"
            println "  - 跳过TLS验证: ${cloud.skipTlsVerify}"
            println "  - 凭据ID: ${cloud.credentialsId ?: '❌ 未设置'}"
            
            // 检查关键配置
            def issues = []
            if (!cloud.jenkinsUrl || !cloud.jenkinsUrl.contains("host.docker.internal")) {
                issues.add("Jenkins URL应设置为: http://host.docker.internal:8080")
            }
            if (!cloud.jenkinsTunnel || !cloud.jenkinsTunnel.contains("host.docker.internal")) {
                issues.add("Jenkins tunnel应设置为: host.docker.internal:50000")
            }
            if (!cloud.credentialsId) {
                issues.add("需要设置kubeconfig凭据")
            }
            
            if (issues.isEmpty()) {
                println "  ✅ 配置看起来正确！"
            } else {
                println "  ⚠️  需要修复的问题："
                issues.each { issue ->
                    println "    - ${issue}"
                }
            }
        }
        println "---"
    }
}

println "\n=== 测试建议 ==="
println "配置正确后，可以运行以下Pipeline进行测试："
println "1. test-docker-build.groovy (测试Docker构建)"
println "2. cicd-demo Pipeline (完整CI/CD流程)" 