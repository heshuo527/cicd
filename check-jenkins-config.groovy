// Jenkins系统配置检查脚本
// 在Jenkins的Script Console中运行此脚本
// 路径：Manage Jenkins -> Script Console

import jenkins.model.Jenkins
import org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud

println "=== Jenkins系统配置检查 ==="

// 检查Jenkins URL
def jenkinsLocationConfig = Jenkins.instance.getExtensionList('jenkins.model.JenkinsLocationConfiguration')[0]
println "当前Jenkins URL: ${jenkinsLocationConfig.url}"

// 检查所有云配置
println "\n=== 云配置列表 ==="
def clouds = Jenkins.instance.clouds
if (clouds.isEmpty()) {
    println "❌ 没有配置任何云"
} else {
    clouds.each { cloud ->
        println "云名称: ${cloud.name}"
        println "云类型: ${cloud.class.simpleName}"
        
        if (cloud instanceof KubernetesCloud) {
            println "  Kubernetes URL: ${cloud.serverUrl}"
            println "  Jenkins URL: ${cloud.jenkinsUrl}"
            println "  Jenkins Tunnel: ${cloud.jenkinsTunnel}"
            println "  命名空间: ${cloud.namespace}"
            println "  跳过TLS验证: ${cloud.skipTlsVerify}"
        }
        println "---"
    }
}

println "\n=== 建议的修复配置 ==="
println "如果没有Kubernetes云或配置不正确，请："
println "1. 进入 Manage Jenkins -> Manage Nodes and Clouds -> Configure Clouds"
println "2. 添加或修改Kubernetes云配置："
println "   - Jenkins URL: http://host.docker.internal:8080"
println "   - Jenkins tunnel: host.docker.internal:50000"
println "   - Kubernetes URL: https://kubernetes.docker.internal:6443"
println "   - 勾选 'Disable https certificate check'" 