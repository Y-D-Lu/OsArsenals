# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个鸿蒙 (HarmonyOS) 应用项目，使用 ArkTS（基于 TypeScript）开发。项目采用 Stage 模型架构，目标 SDK 版本为 6.0.2(22)。

## 常用命令

### 构建前环境配置（重要）

命令行构建前必须配置三个环境变量：

```bash
# Windows PowerShell
$env:DEVECO_SDK_HOME = "C:\Program Files\Huawei\DevEco Studio\sdk"
$env:OHOS_SDK_HOME = "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony"
$env:JAVA_HOME = "C:\Program Files\Huawei\DevEco Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Linux/Mac
export DEVECO_SDK_HOME="/path/to/DevEco Studio/sdk"
export OHOS_SDK_HOME="/path/to/DevEco Studio/sdk/default/openharmony"
export JAVA_HOME="/path/to/DevEco Studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"
```

**环境变量说明**：
- `DEVECO_SDK_HOME`: SDK 根目录（包含 default/hms 等子目录）
- `OHOS_SDK_HOME`: OpenHarmony SDK 目录（提供 ArkTS、ets、native 等组件）
- `JAVA_HOME`: DevEco Studio 内置 JDK（JBR 21）

### Hvigor 构建命令
Hvigor 是鸿蒙的构建系统，类似 Gradle：

```bash
# 构建项目（debug 模式，自动签名）
hvigorw assembleHap --mode module -p module=entry@default -p product=default --no-daemon

# 构建项目（release 模式）
hvigorw assembleHap --mode module -p module=entry@default -p product=default -p buildMode=release --no-daemon

# 清理构建输出
hvigorw clean --no-daemon

# 运行代码检查
hvigorw lint --no-daemon

# 运行单元测试
hvigorw test@entry --mode module -p module=entry@ohosTest -p product=default --no-daemon
```

**构建产物**：
- `entry-default-unsigned.hap`: 未签名 HAP（123KB）
- `entry-default-signed.hap`: 已签名 HAP（163KB，自动系统签名）

**构建性能参考**：
- 首次构建：约 1 分钟（编译 ArkTS 39秒）
- 增量构建：约 14 秒
- 系统签名：约 2.7 秒

### Hvigor 任务查看
```bash
# 查看可用任务
hvigorw tasks
```

### 系统签名相关命令
```bash
# 手动执行签名（不通过构建流程）
cd hw_sign && node sign.js

# 删除旧 Profile 强制重新生成
rm hw_sign/openharmony_sx.p7b

# 安装签名后的 HAP 到设备
hdc install -r entry/build/default/outputs/default/entry-default-signed.hap

# 启动应用
hdc shell "aa start -a EntryAbility -b cn.arsenals.osarsenals"

# 查看应用进程
hdc shell "ps -ef | grep cn.arsenals.osarsenals | grep -v grep"
```

### hdc 设备管理命令
```bash
# 查看连接的设备
hdc list targets

# 查看设备信息
hdc shell "param get const.product.devicetype"

# 查看应用信息
hdc shell "bm dump -n cn.arsenals.osarsenals"

# 查看应用日志（实时）
hdc hilog -x | grep -E "OsArsenals|cn.arsenals"

# 清除应用数据
hdc shell "bm clean -n cn.arsenals.osarsenals -c"

# 卸载应用
hdc uninstall cn.arsenals.osarsenals
```

## 架构和结构

### 项目结构
- **AppScope/**: 应用级资源（字符串、图片等）
- **entry/**: 主模块（entry 模块）
  - **src/main/**: 主要源代码
    - **ets/entryability/**: 应用能力入口
    - **ets/pages/**: UI 页面
  - **src/ohosTest/**: 系统测试（端到端测试）
  - **src/test/**: 本地单元测试
  - **src/mock/**: Mock 数据和模拟实现
- **hvigor/**: Hvigor 构建系统配置
- **oh_modules/**: 鸿蒙包依赖（类似 node_modules）

### Stage 模型核心组件

**EntryAbility**: 应用入口能力，继承自 UIAbility，负责应用生命周期管理：
- `onCreate`: 应用创建时调用
- `onWindowStageCreate`: 窗口阶段创建，加载主页面
- `onForeground/onBackground`: 前台/后台切换

**页面路由**: 通过 `main_pages.json` 配置页面列表，当前包含 `pages/Index` 页面。

### 关键配置文件

- **build-profile.json5**: 项目和模块的构建配置（SDK 版本、构建模式等）
- **module.json5**: 模块元数据（能力、权限、设备类型等）
- **hvigorfile.ts**: Hvigor 构建脚本（项目级和模块级）
- **hvigor/hvigor-config.json5**: Hvigor 执行配置（并行编译、增量编译等）
- **code-linter.json5**: 代码检查规则配置
- **oh-package.json5**: 包依赖配置

### UI 开发

使用 ArkUI 声明式语法：
- `@Entry`: 标记页面入口组件
- `@Component`: 标记自定义组件
- `@State`: 状态变量装饰器
- **RelativeContainer**: 相对布局容器
- **build()**: 组件构建函数

## 测试

项目使用 **Hypium** 测试框架（`@ohos/hypium`）和 **Hamock** Mock 框架（`@ohos/hamock`）。

测试文件位置：
- **entry/src/ohosTest/ets/test/**: 系统测试（运行在设备/模拟器上）
- **entry/src/test/**: 本地单元测试（不依赖设备）

测试 API：
- `describe`: 定义测试套件
- `it`: 定义测试用例
- `beforeAll/beforeEach/afterEach/afterAll`: 生命周期钩子
- `expect`: 断言函数

## 代码检查和安全规则

项目配置了性能和安全相关的代码检查规则（见 `code-linter.json5`）：

**性能规则**: `plugin:@performance/recommended`
**TypeScript 规则**: `plugin:@typescript-eslint/recommended`

**安全规则**: 禁止使用不安全的加密算法：
- AES、Hash、MAC、DH、DSA、ECDSA、RSA 等的安全检查
- 不允许使用弱加密（如 3DES）
- RSA 加密、签名、密钥的安全限制

## 开发注意事项

### 资源引用
使用 `$r()` 引用资源：
- `$r('app.float.page_text_font_size')`: 引用应用资源
- `$r('sys.*')`: 引用系统资源

### 日志
使用 `hilog` 进行日志记录：
```typescript
import { hilog } from '@kit.PerformanceAnalysisKit';
const DOMAIN = 0x0000;
hilog.info(DOMAIN, 'tag', 'message');
```

### Kit 模块导入
鸿蒙按功能域划分为不同的 Kit：
- **@kit.AbilityKit**: 能力相关（AbilityConstant、UIAbility、Want）
- **@kit.ArkUI**: UI 相关（window、组件）
- **@kit.PerformanceAnalysisKit**: 性能分析（hilog）

### 严格模式
项目启用了严格模式（`build-profile.json5` 中 `strictMode`）：
- `caseSensitiveCheck`: 大小写敏感检查
- `useNormalizedOHMUrl`: 使用标准化的 OHM URL

### 代码混淆
Release 构建时可启用代码混淆，配置在 `entry/obfuscation-rules.txt`。

## 隐私安全合规约束

**重要原则**：避免在项目代码、文档、Git 历史中主动引入敏感数据。

### 敏感数据分类

**禁止提交的内容**：
- ❌ 个人信息：真实姓名、邮箱、手机号、IP 地址
- ❌ 设备信息：真实设备 ID、MAC 地址、序列号
- ❌ 路径信息：包含真实用户名的本地路径（`C:/Users/<真实用户名>/...`)
- ❌ 密钥凭证：真实密码（非公开标准密码）、API Token、私钥
- ❌ 项目信息：其他项目名称、公司内部项目代号

**允许保留的内容**：
- ✅ 示例用户名：`demo`、`example`、`test`
- ✅ 示例邮箱：`demo@example.com`、`test@test.com`
- ✅ 示例 IP：`<device-ip>`、`192.168.1.100`（示例网段）
- ✅ 公开密码：OpenHarmony 标准证书密码 `123456`（官方公开）
- ✅ 标准路径：DevEco Studio 默认安装路径（`C:/Program Files/Huawei/...`)

### Git 提交前检查清单

**提交前必须检查**：

1. **文件内容扫描**：
   ```bash
   # 检查文件内容
   git diff | grep -iE "(真实用户名|真实邮箱|真实IP|password.*=.*[^123456])"
   
   # 检查新文件
   git ls-files --others | xargs grep -iE "(真实用户名|真实邮箱)"
   ```

2. **Commit Message 检查**：
   ```bash
   # 检查 commit message
   git log --format="%B" -1 | grep -iE "(真实信息|敏感内容)"
   ```

3. **Author 信息确认**：
   ```bash
   # 确认 author 信息
   git log --format="%an <%ae>" -1
   # 应为：demo developer <demo@example.com>
   ```

### 正确处理敏感数据

**发现敏感数据的处理流程**：

1. **立即停止提交**：不要新增 commit 修改文件
2. **定位引入 commit**：
   ```bash
   git log --all -p | grep "敏感内容"
   ```
3. **修改历史 commit**：
   ```bash
   # 修改文件
   git add <modified-file>
   # Amend 原 commit
   git commit --amend --no-edit
   ```
4. **验证历史清理**：
   ```bash
   git log --all -p | grep "敏感内容"
   # 应无结果
   ```

### 文件内容约束

**代码中的敏感数据处理**：
```typescript
// ❌ 错误示例：硬编码真实信息
const USER_NAME = "张三";
const DEVICE_IP = "192.168.43.2:33329";
const API_TOKEN = "sk-1234567890abcdef";

// ✅ 正确示例：使用配置文件或环境变量
const USER_NAME = process.env.USER_NAME || "demo";
const DEVICE_IP = "<device-ip>"; // 文档中使用占位符
const API_TOKEN = process.env.API_TOKEN; // 从环境变量读取
```

**文档中的敏感数据处理**：
```markdown
# ❌ 错误示例
设备连接：192.168.43.2:33329（我的手机）

# ✅ 正确示例
设备连接：<device-ip> 或 192.168.1.100（示例 IP）
```

**日志输出约束**：
```typescript
// ❌ 错误示例：输出真实信息
hilog.info(0, TAG, `User ${realUserName} logged in`);
hilog.info(0, TAG, `Device ${realDeviceId} connected`);

// ✅ 正确示例：脱敏或使用示例信息
hilog.info(0, TAG, `User logged in`);
hilog.info(0, TAG, `Device connected`);
```

### .gitignore 配置

**必须排除的文件**：
```gitignore
# 本地配置（可能包含真实路径）
local.properties

# IDE 配置（可能包含用户路径）
.idea/

# 构建产物（动态生成）
**/build/
.hvigor/cache/

# 系统签名产物（动态生成）
*.p7b
```

### 定期安全扫描

**每周执行的安全检查**：
```bash
# 扫描 Git 历史
git log --all -p | grep -iE "(真实用户名|真实邮箱|真实IP|敏感项目名)"

# 扫描当前文件
git ls-tree -r HEAD --name-only | xargs grep -iE "(真实信息)"

# 扫描 commit message
git log --all --format="%B" | grep -iE "(敏感内容)"
```

### 违规处理

**发现违规内容的紧急处理**：
1. 立即停止推送代码
2. 定位违规 commit（使用 `git log --all -p`）
3. 使用 `git commit --amend` 或 `git rebase -i` 修改历史
4. 如已推送，考虑使用 `git filter-branch` 清理远程历史
5. 更新团队其他成员的本地仓库

### 安全意识提醒

**开发过程中时刻注意**：
- ✅ 使用示例数据代替真实数据
- ✅ 文档中的 IP/路径使用占位符
- ✅ Commit message 只描述技术内容，不包含个人信息
- ✅ Author 信息使用标准示例（demo developer）
- ❌ 不在代码中硬编码真实配置
- ❌ 不在文档中记录真实设备信息
- ❌ 不在 commit message 中提到其他项目或真实用户

### 调试和日志

**查看应用日志**：
```bash
# 实时查看日志
hdc hilog -x

# 过滤应用日志
hdc hilog -x | grep "cn.arsenals.osarsenals"

# 清除日志缓冲区
hdc hilog -r
```

**应用内日志**：
```typescript
import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;
const TAG = 'MyApp';

// 日志级别
hilog.debug(DOMAIN, TAG, '调试信息');
hilog.info(DOMAIN, TAG, '一般信息');
hilog.warn(DOMAIN, TAG, '警告信息');
hilog.error(DOMAIN, TAG, '错误信息');
```

## 当前项目配置摘要

**基本信息**：
- Bundle Name: `cn.arsenals.osarsenals`
- SDK 版本: `6.0.2(22)` (API Level 22)
- 权限等级: `system_core`（最高权限等级）
- 应用类型: `hos_system_app`（系统应用）
- 分发类型: `os_integration`（系统预装）

**构建配置**：
- 构建系统: Hvigor
- 自动签名: SystemSignPlugin（集成在 `hvigorfile.ts`）
- 签名等级: OpenHarmony system_core

**Skill 系统**：
- `.claude/skills/build.md`: 构建 Skill（通过 `/build` 调用）

## 常见构建错误排查

### SDK component missing

**错误信息**：
```
Error: SDK component missing.
```

**原因**：缺少环境变量配置

**解决**：配置三个环境变量（见"构建前环境配置"章节）

### Java 未找到

**错误信息**：
```
java: command not found
```

**解决**：
```bash
export JAVA_HOME="C:\Program Files\Huawei\DevEco Studio\jbr"
export PATH="$JAVA_HOME/bin:$PATH"
java -version  # 验证
```

### 签名失败

**错误信息**：
```
Illegal base64 character
```

**原因**：Profile 模板证书格式错误

**解决**：使用标准 OpenHarmony Application Release 证书链

### 构建产物不存在

**检查**：
```bash
ls entry/build/default/outputs/default/*.hap
```

**解决**：
- 确认构建命令正确执行
- 检查 `.hvigor/` 和 `entry/build/` 目录权限

## 系统签名配置

项目配置了自动系统签名，用于获取系统级 API 权限（如截屏、虚拟屏等）。DevEco Studio 默认签名只能获取普通权限，系统签名可实现更高的权限等级。

### 签名流程

**自动化签名流程**：
```
构建 HAP (unsigned) → 生成 Profile (p7b) → 签名 HAP (signed) → 安装到设备
```

构建时自动执行签名（通过 `hvigorfile.ts` 的 SystemSignPlugin），输出 `entry-default-signed.hap`。

### hw_sign 目录结构

```
hw_sign/
├── hap-sign-tool.jar           # 签名工具（从 OpenHarmony SDK 获取）
├── OpenHarmony.p12             # 密钥库文件（密码：123456）
├── OpenHarmonyApplication.pem  # 应用签名证书
├── OpenHarmonyProfileRelease.pem  # Profile 签名证书
├── UnsgnedReleasedProfileTemplate.json  # Profile 模板（核心配置）
├── sign-config.json5           # 签名参数配置
├── sign.js                     # 签名脚本
└── openharmony_sx.p7b          # 生成的 Profile（自动生成，不提交）
```

### Profile 模板核心配置

`UnsgnedReleasedProfileTemplate.json` 决定应用的权限等级：

**关键字段**：
- `bundle-info.bundle-name`: 必须与 `AppScope/app.json5` 的 `bundleName` 一致
- `bundle-info.apl`: 权限等级
  - `normal`: 普通应用（仅 normal 权限）
  - `system_basic`: 系统基础应用（normal + system_basic 权限）
  - `system_core`: 系统核心应用（所有权限）
- `bundle-info.app-feature`: `hos_system_app`（系统应用）
- `app-distribution-type`: `os_integration`（系统预装）
- `acls.allowed-acls`: ACL 权限白名单（声明可用系统权限）
- `distribution-certificate`: `OpenHarmonyApplication.pem` 内容（换行符替换为 `\n`）

当前项目配置：
- Bundle Name: `cn.arsenals.osarsenals`
- 权限等级: `system_core`（最高权限）
- 应用类型: `hos_system_app`

### 添加系统权限

**三步操作**：

1. **更新 Profile 模板** (`hw_sign/UnsgnedReleasedProfileTemplate.json`)：
   ```json
   "acls": {
       "allowed-acls": [
           "ohos.permission.CAPTURE_SCREEN",
           "ohos.permission.ACCESS_VIRTUAL_SCREEN"
       ]
   },
   "permissions": {
       "restricted-permissions": [
           "ohos.permission.CAPTURE_SCREEN",
           "ohos.permission.ACCESS_VIRTUAL_SCREEN"
       ]
   }
   ```

2. **声明权限** (`entry/src/main/module.json5`)：
   ```json5
   "requestPermissions": [
       {
           "name": "ohos.permission.CAPTURE_SCREEN",
           "reason": "$string:permission_screen_capture_reason",
           "usedScene": {
               "abilities": ["EntryAbility"],
               "when": "inuse"
           }
       }
   ]
   ```

3. **删除旧 Profile 并重新构建**：
   ```bash
   rm hw_sign/openharmony_sx.p7b
   hvigorw assembleHap --mode module -p module=entry@default
   ```

**重要**：Profile 模板修改后必须删除 `openharmony_sx.p7b` 强制重新生成。

### 常用系统权限

| 权限 | 说明 | 最低 APL |
|------|------|---------|
| `ohos.permission.CAPTURE_SCREEN` | 截屏 | system_core |
| `ohos.permission.CUSTOM_SCREEN_CAPTURE` | 自定义截屏 | system_basic |
| `ohos.permission.ACCESS_VIRTUAL_SCREEN` | 虚拟屏访问 | system_basic |
| `ohos.permission.GET_RUNNING_INFO` | 获取运行信息 | system_basic |
| `ohos.permission.MANAGE_MISSIONS` | 管理任务 | system_core |

### 签名配置文件

`sign-config.json5` 使用 OpenHarmony 默认证书参数：
- 密钥库密码和应用密钥密码均为 `123456`
- 签名算法：`SHA256withECDSA`
- 一般无需修改

### 签名产物

构建输出：
- `entry-default-unsigned.hap`: 未签名 HAP（构建产物）
- `entry-default-signed.hap`: 已签名 HAP（最终产物）

### 常见问题

**Q1: 安装报 "signature verification failed"**
- 原因：Profile 模板的 `bundle-name` 与 `app.json5` 不一致
- 解决：确保两者完全一致

**Q2: 运行报 "201 - Permission verification failed"**
- 原因：
  1. 权限未在 `allowed-acls` 中配置
  2. APL 等级不够（如权限需要 `system_core` 但设置为 `system_basic`）
  3. Profile 未重新生成
- 解决：
  1. 确保 `allowed-acls` 包含所有系统权限
  2. 将 `apl` 设置为 `system_core`
  3. 删除 `openharmony_sx.p7b` 重新构建

**Q3: 签名报 "Illegal base64 character"**
- 原因：Profile 模板的证书格式错误
- 解决：使用标准 OpenHarmony Application Release 证书链

**Q4: Profile 修改后不生效**
- 原因：签名脚本检查时间戳，Profile 未过期时跳过生成
- 解决：删除 `hw_sign/openharmony_sx.p7b` 强制重新生成

### 手动签名

如需手动执行签名（不通过构建流程）：
```bash
cd hw_sign && node sign.js
```

### 安装签名后的 HAP

使用 hdc 工具安装：
```bash
hdc install -r entry/build/default/outputs/default/entry-default-signed.hap
```

`-r` 参数表示覆盖安装。