// Jenkins启用TCP端口脚本
// 在Jenkins的Script Console中运行此脚本
// 路径：Manage Jenkins -> Script Console

import jenkins.model.Jenkins
import hudson.model.Node.Mode
import jenkins.security.s2m.AdminWhitelistRule

println "=== 检查当前TCP端口配置 ==="

// 获取Jenkins实例
def jenkins = Jenkins.instance

// 检查TCP端口设置
def slaveAgentPort = jenkins.slaveAgentPort
println "当前TCP端口设置: ${slaveAgentPort}"

if (slaveAgentPort == -1) {
    println "❌ TCP端口被禁用"
    println "正在启用TCP端口50000..."
    
    // 启用TCP端口50000
    jenkins.setSlaveAgentPort(50000)
    jenkins.save()
    
    println "✅ TCP端口50000已启用"
    println "⚠️  需要重启Jenkins以使配置生效"
} else if (slaveAgentPort == 0) {
    println "⚠️  TCP端口设置为随机端口"
    println "建议设置为固定端口50000"
    
    jenkins.setSlaveAgentPort(50000)
    jenkins.save()
    
    println "✅ TCP端口已设置为50000"
    println "⚠️  需要重启Jenkins以使配置生效"
} else {
    println "✅ TCP端口已启用: ${slaveAgentPort}"
}

// 检查安全配置
println "\n=== 安全配置检查 ==="
def authStrategy = jenkins.getAuthorizationStrategy()
println "授权策略: ${authStrategy.class.simpleName}"

println "\n=== 建议操作 ==="
println "1. 如果TCP端口刚刚启用，请重启Jenkins"
println "2. 确保Docker容器映射了50000端口"
println "3. 重新运行Pipeline测试" 