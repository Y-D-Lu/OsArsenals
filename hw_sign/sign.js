/**
 * HarmonyOS HAP 系统签名脚本
 * 用于自动化签名流程：生成 profile -> 签名 HAP
 */
const path = require('path');
const fs = require('fs');
const { execSync } = require('child_process');

// 获取项目根目录和 hw_sign 目录
const projectRoot = path.resolve(__dirname, '..');
const signDir = __dirname;
const entryDir = path.join(projectRoot, 'entry');
const buildOutputDir = path.join(entryDir, 'build', 'default', 'outputs', 'default');

// 配置文件路径
const configPath = path.join(signDir, 'sign-config.json5');

// 读取配置（JSON5 格式，需要简单解析）
function loadConfig() {
  const configContent = fs.readFileSync(configPath, 'utf-8');
  // 移除注释和尾随逗号以兼容 JSON.parse
  const jsonContent = configContent
    .replace(/\/\/.*$/gm, '')
    .replace(/,\s*}/g, '}')
    .replace(/,\s*]/g, ']');
  return JSON.parse(jsonContent);
}

// 检查 Java 环境
function checkJava() {
  try {
    execSync('java -version', { stdio: 'pipe' });
    return true;
  } catch (e) {
    console.error('[hw_sign] Java 未安装或不在 PATH 中');
    return false;
  }
}

// 生成 p7b profile
function generateProfile(config) {
  const profileFile = path.join(signDir, 'openharmony_sx.p7b');
  const templateFile = path.join(signDir, config.profileTemplate);

  // 检查模板文件是否存在
  if (!fs.existsSync(templateFile)) {
    console.error(`[hw_sign] Profile 模板文件不存在: ${templateFile}`);
    return false;
  }

  // 检查是否需要重新生成（模板或证书更新时）
  const profileCertFile = path.join(signDir, config.profileCertFile);
  const needRegenerate = !fs.existsSync(profileFile) ||
    fs.statSync(templateFile).mtimeMs > fs.statSync(profileFile).mtimeMs ||
    fs.statSync(profileCertFile).mtimeMs > fs.statSync(profileFile).mtimeMs;

  if (!needRegenerate) {
    console.log('[hw_sign] Profile 已存在且未过期，跳过生成');
    return true;
  }

  const hapSignTool = path.join(signDir, config.hapSignToolJar);
  const keystoreFile = path.join(signDir, config.keystoreFile);

  const cmd = `java -jar "${hapSignTool}" sign-profile ` +
    `-mode "localSign" ` +
    `-keyAlias "${config.profileKeyAlias}" ` +
    `-keyPwd "${config.keyPwd}" ` +
    `-inFile "${templateFile}" ` +
    `-outFile "${profileFile}" ` +
    `-keystoreFile "${keystoreFile}" ` +
    `-keystorePwd "${config.keystorePwd}" ` +
    `-signAlg "${config.signAlg}" ` +
    `-profileCertFile "${path.join(signDir, config.profileCertFile)}"`;

  console.log('[hw_sign] 生成 profile...');
  try {
    execSync(cmd, { stdio: 'inherit' });
    console.log('[hw_sign] Profile 生成成功');
    return true;
  } catch (e) {
    console.error('[hw_sign] Profile 生成失败');
    return false;
  }
}

// 签名 HAP
function signHap(config) {
  const unsignedHap = path.join(buildOutputDir, 'entry-default-unsigned.hap');
  const signedHap = path.join(buildOutputDir, 'entry-default-signed.hap');

  if (!fs.existsSync(unsignedHap)) {
    console.error(`[hw_sign] 未签名 HAP 不存在: ${unsignedHap}`);
    console.error('[hw_sign] 请先运行 hvigorw assembleHap 构建项目');
    return false;
  }

  const hapSignTool = path.join(signDir, config.hapSignToolJar);
  const keystoreFile = path.join(signDir, config.keystoreFile);
  const appCertFile = path.join(signDir, config.appCertFile);
  const profileFile = path.join(signDir, 'openharmony_sx.p7b');

  const cmd = `java -jar "${hapSignTool}" sign-app ` +
    `-keyAlias "${config.keyAlias}" ` +
    `-signAlg "${config.signAlg}" ` +
    `-mode "localSign" ` +
    `-appCertFile "${appCertFile}" ` +
    `-profileFile "${profileFile}" ` +
    `-inFile "${unsignedHap}" ` +
    `-keystoreFile "${keystoreFile}" ` +
    `-outFile "${signedHap}" ` +
    `-keyPwd "${config.keyPwd}" ` +
    `-keystorePwd "${config.keystorePwd}"`;

  console.log('[hw_sign] 签名 HAP...');
  try {
    execSync(cmd, { stdio: 'inherit' });
    console.log('[hw_sign] HAP 签名成功: ' + signedHap);
    return true;
  } catch (e) {
    console.error('[hw_sign] HAP 签名失败');
    return false;
  }
}

// 主流程
function main() {
  console.log('[hw_sign] 开始系统签名流程...');

  // 检查 Java
  if (!checkJava()) {
    process.exit(1);
  }

  // 加载配置
  const config = loadConfig();
  console.log('[hw_sign] 配置加载成功');

  // 生成 profile
  if (!generateProfile(config)) {
    process.exit(1);
  }

  // 签名 HAP
  if (!signHap(config)) {
    process.exit(1);
  }

  console.log('[hw_sign] 系统签名完成');
}

main();