---
name: build
description: HarmonyOS 项目自动构建和部署。自动配置环境变量（DEVECO_SDK_HOME、OHOS_SDK_HOME、JAVA_HOME）、执行构建、系统签名、可选部署到设备。
trigger:
  - 用户说 "build"、"构建"、"编译"、"部署"
  - 用户说 "/build"
  - 用户说 "构建并部署"
---

# HarmonyOS 构建 Skill

自动配置环境变量并执行完整的构建流程。

## 使用方法

```bash
/build               # 构建 debug 版本
/build --release     # 构建 release 版本
/build --deploy      # 构建并部署到设备
```

## 构建流程（Claude Code 执行步骤）

当用户调用 `/build` 时，按以下步骤执行：

### 1. 配置环境变量（关键）

**Windows 环境**：
```bash
export DEVECO_SDK_HOME="C:/Program Files/Huawei/DevEco Studio/sdk"
export OHOS_SDK_HOME="C:/Program Files/Huawei/DevEco Studio/sdk/default/openharmony"
export JAVA_HOME="C:/Program Files/Huawei/DevEco Studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"
```

**Linux/Mac 环境**（根据实际安装路径调整）：
```bash
export DEVECO_SDK_HOME="/path/to/DevEco Studio/sdk"
export OHOS_SDK_HOME="/path/to/DevEco Studio/sdk/default/openharmony"
export JAVA_HOME="/path/to/DevEco Studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"
```

### 2. 验证 Java 环境

```bash
java -version
```

预期输出：`OpenJDK 21.0.8 (JBR-21.0.8)`

### 3. 执行构建命令

**Debug 构建**：
```bash
"C:/Program Files/Huawei/DevEco Studio/tools/node/node.exe" \
  "C:/Program Files/Huawei/DevEco Studio/tools/hvigor/bin/hvigorw.js" \
  --mode module -p module=entry@default -p product=default \
  assembleHap --no-daemon
```

**Release 构建**（添加 `--release` 参数）：
```bash
... -p buildMode=release
```

### 4. 检查构建产物

构建完成后检查：
```bash
ls -lh entry/build/default/outputs/default/*.hap
```

预期输出：
- `entry-default-unsigned.hap`: 未签名 HAP（123KB）
- `entry-default-signed.hap`: 已签名 HAP（163KB，自动系统签名）

### 5. 可选部署（--deploy 参数）

检查设备连接：
```bash
hdc list targets
```

安装应用：
```bash
hdc install -r entry/build/default/outputs/default/entry-default-signed.hap
```

启动应用：
```bash
hdc shell "aa start -a EntryAbility -b cn.arsenals.osarsenals"
```

## 环境变量说明

**为什么需要这三个环境变量？**

- `DEVECO_SDK_HOME`: SDK 根目录，解决 "SDK component missing" 错误
- `OHOS_SDK_HOME`: OpenHarmony SDK 目录，提供 ArkTS、ets、native 等构建组件
- `JAVA_HOME`: DevEco Studio 内置 JDK（JBR 21），构建所需 Java 环境

缺少环境变量会导致构建失败，错误信息：`Error: SDK component missing`

## 构建时间

- 首次构建：约 1 分钟
- 增量构建：约 14 秒
- 系统签名：约 2.7 秒

## 常见问题

**构建失败 "SDK component missing"**：
- 解决：确保三个环境变量已配置（步骤1）

**构建失败 "Java 未找到"**：
- 解决：检查 JAVA_HOME 路径，确保 Java 在 PATH 中

**签名失败**：
- 检查 `hw_sign/openharmony_sx.p7b` 是否存在
- 检查 `hw_sign/` 目录中的证书文件完整性

**设备未连接**：
- 使用 `hdc list targets` 检查连接
- 确保设备已开启开发者模式和 USB 调试

## 示例执行过程

```bash
# 用户输入
/build --deploy

# Claude Code 执行
[INFO] 配置环境变量...
  DEVECO_SDK_HOME=C:/Program Files/Huawei/DevEco Studio/sdk
  OHOS_SDK_HOME=C:/Program Files/Huawei/DevEco Studio/sdk/default/openharmony
  JAVA_HOME=C:/Program Files/Huawei/DevEco Studio/jbr

[INFO] 验证 Java 环境...
openjdk version "21.0.8" 2025-07-15

[INFO] 开始构建（模式: debug）...
> hvigor BUILD SUCCESSFUL in 14 s 144 ms

[SystemSign] 系统签名完成

[INFO] 构建产物: entry-default-signed.hap (162 KB)

[INFO] 检查设备连接...
  <device-ip>

[INFO] 安装应用...
  install bundle successfully

[INFO] 启动应用...
  start ability successfully

[INFO] 构建流程完成！
```