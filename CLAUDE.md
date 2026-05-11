# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个鸿蒙 (HarmonyOS) 应用项目，使用 ArkTS（基于 TypeScript）开发。项目采用 Stage 模型架构，目标 SDK 版本为 6.0.2(22)。

**项目状态**：
- Android → HarmonyOS 移植已完成（100%）
- 33个ETS源文件，约7000行代码（28个基础源文件 + 5个照片功能相关文件）
- 20个Git commit记录完整移植和功能开发过程
- 已安装到华为HarmonyOS设备并运行
- 所有核心功能已实现：设备监控、悬浮窗拖动、输入注入、原神自动化、照片处理等
- 照片处理功能完整实现：实况照片、本机水印、元数据编辑、批量光圈（含Hidden API补丁方案）

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
- `entry-default-unsigned.hap`: 未签名 HAP
- `entry-default-signed.hap`: 已签名 HAP（自动系统签名）

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
    - **ets/abilities/**: ServiceAbility（后台服务）
    - **ets/common/**: 通用工具（Logger、Constants、types）
    - **ets/components/**: 自定义组件（PercentageCircle、PercentageRect）
    - **ets/managers/**: 业务管理器（DeviceStatusManager、FloatingWindowManager等）
    - **ets/models/**: 数据模型（DeviceStatusInfo、PointMap等）
    - **ets/pages/**: UI 页面（13个页面）
    - **ets/types/**: Hidden API类型定义补丁（@ohos.file.photoAccessHelper.d.ts）
    - **ets/utils/**: 工具类（DeviceStatusUtil、InputUtil、GenshinImpactUtil等）
  - **src/ohosTest/**: 系统测试（端到端测试）
  - **src/test/**: 本地单元测试
  - **src/mock/**: Mock 数据和模拟实现
- **hvigor/**: Hvigor 构建系统配置
- **oh_modules/**: 鸿蒙包依赖（类似 node_modules）
- **hw_sign/**: 系统签名配置（Profile模板、证书、签名脚本）

### Stage 模型核心组件

**EntryAbility**: 应用入口能力，继承自 UIAbility，负责应用生命周期管理：
- `onCreate`: 应用创建时调用
- `onWindowStageCreate`: 窗口阶段创建，加载主页面
- `onForeground/onBackground`: 前台/后台切换

**页面路由**: 通过 `main_pages.json` 配置页面列表，当前包含13个页面：
- `pages/Index`: 主入口（Tabs导航）
- `pages/OverviewPage`: 设备监控页
- `pages/FunctionPage`: 功能导航页（8个功能入口）
- `pages/SettingsPage`: 设置页
- `pages/GenshinPage`: 原神自动化页
- `pages/HookPage`: 挂机模式页
- `pages/StandbyPage`: 待机显示页
- `pages/PerformanceTestPage`: 性能测试页
- `pages/MonitorPanel`: 悬浮窗监控面板
- `pages/MovingPhotoPage`: 实况照片生成页（选择图片+视频，创建实况照片）
- `pages/WatermarkPage`: 本机水印页（查看/设置水印属性，批量处理最多500张）
- `pages/MetadataEditPage`: 元数据编辑页（读取/编辑10个EXIF属性）
- `pages/AperturePage`: 批量光圈页（批量修改FNumber值）

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

## 已实现功能模块

### 核心功能（100%完成）

1. **主界面导航**
   - Tabs组件：Overview/Function/Settings三个Tab
   - 符合HarmonyOS UX最佳实践
   - FunctionPage包含8个功能入口（Grid布局，4行2列）：
     - 原神自动化 🎮、挂机模式 🌙、待机显示 ⏰、性能测试 ⚡
     - 实况照片 📸、本机水印 💧、元数据编辑 📝、批量光圈 ⚙️

2. **设备监控**
   - 真实数据采集：sysfs文件读取（/proc/meminfo、/sys/devices/system/cpu等）
   - 电池、内存、CPU、GPU、FPS监控
   - 百分比图表组件：PercentageCircle、PercentageRect

3. **悬浮窗显示与拖动**
   - window.create TYPE_FLOAT创建悬浮窗
   - onTouch事件实现拖动交互
   - MonitorPanel页面实时显示监控数据

4. **后台服务**
   - ServiceAbility + CommonEvent订阅
   - 支持3种命令：INJECT_INPUT、UPDATE_POINT_MAP、EXECUTE_COMMAND

5. **输入注入**
   - inputEventClient Kit API调用
   - injectTap、injectSwipe、injectKey、injectPinch完整实现
   - 需要INJECT_INPUT_EVENT权限

6. **原神自动化**
   - GenshinImpactUtil命令解析完整
   - 10+种命令：e、z、a、j、s、sleep、domain_enter等
   - 3个预设脚本：clear_pool、forsaken_rift、pale_forgotten_glory

7. **挂机模式**
   - setWindowKeepScreenOn（屏幕常亮）
   - setWindowBrightness（亮度控制，挂机模式0.01）
   - 黑色背景保护OLED屏幕

8. **待机显示**
   - 实时时钟显示
   - 防烧屏机制：5分钟定时位移

9. **性能测试**
   - 单核/多核CPU压力测试
   - Math.sqrt计算循环产生负载
   - 进度条实时显示

10. **坐标点管理**
    - InputManager + Preferences存储
    - 原神坐标点映射配置

11. **照片处理功能（完整实现）**

    **a. 实况照片生成（MovingPhotoPage）**
    - 功能：选择静态图片 + 动态视频 → 创建实况照片资产
    - 技术实现：
      - AVImageGenerator提取视频首帧作为缩略图
      - MediaAssetChangeRequest创建资产（subtype: MOVING_PHOTO）
      - 专用临时文件夹：moving_photo_temp，生命周期自动清理
    - UI特性：统一按钮样式、ImageFit.Contain完整显示、底部安全区域预留
    - 完成度：✅ 100%（已测试成功，支持连续生成）

    **b. 本机水印功能（WatermarkPage）**
    - 功能：查看水印属性 + 批量设置水印（最多500张照片）
    - Hidden API支持：
      - PhotoKeys.SUPPORTED_WATERMARK_TYPE（查询水印类型）
      - WatermarkType enum（DEFAULT=0, BRAND_COMMON=1, COMMON=2, BRAND=3）
      - MediaAssetChangeRequest.setSupportedWatermarkType（设置水印）
    - SDK补丁方案：
      - `entry/src/main/ets/types/@ohos.file.photoAccessHelper.d.ts`：类型定义补丁文件
      - `patch_sdk_hidden_api.ps1`：自动补丁脚本（需管理员权限）
      - `SDK_HIDDEN_API_PATCH.md`：补丁使用文档
    - EXIF修改：同时设置Make=deviceInfo.manufacture、Model=deviceInfo.marketName
    - 实况照片支持：提取静态图片和视频 → 修改EXIF → 创建新实况照片资产
    - 批处理优化：batchSize=10，Promise.all并行，批次间delay(100ms)
    - 完成度：✅ 100%（含完整Hidden API补丁方案）

    **c. 元数据编辑（MetadataEditPage）**
    - 功能：读取10个EXIF属性 + 编辑保存
    - EXIF读取（使用image.PropertyKey枚举）：
      - Make（制造商）、Model（型号）、FNumber（光圈）、ISO（感光度）
      - ExposureTime（曝光时间）、DateTimeOriginal（拍摄时间）
      - GPSLatitude（纬度）、GPSLongitude（经度）、GPSAltitude（高度）
      - Orientation（方向）
    - 批量读取方法：imageSource.getImageProperties（完整功能）
    - UI特性：中文标签、可编辑输入框、支持添加自定义EXIF属性
    - 实况照片支持：检测PhotoSubtype.MOVING_PHOTO，分别处理静态图片和视频
    - 保存策略：另存为 原文件名_exif
    - 完成度：✅ 100%（完整EXIF读取功能，非降级）

    **d. 批量光圈修改（AperturePage）**
    - 功能：批量修改FNumber光圈值（最多500张照片）
    - 输入格式：纯数字光圈值（如2.8、1.8）
    - 批处理优化：
      - batchSize=10，每批并行处理（Promise.all）
      - 批次间delay(100ms)，避免系统压力
      - 实时进度显示（processedCount/totalCount）
      - 错误隔离：单张失败不影响其他照片
    - 保存策略：另存为 原文件名_fn
    - 完成度：✅ 100%（含批量处理优化和错误处理）

    **照片功能技术要点**：
    - ✅ 完善的错误处理：所有API调用判空检查、try-catch包裹
    - ✅ 资源管理规范：文件/ImageSource/fetchResult在finally块释放
    - ✅ 临时文件清理：aboutToDisappear生命周期自动清理所有临时目录
    - ✅ 实况照片支持：3个页面支持实况照片处理（MovingPhotoPage、WatermarkPage、MetadataEditPage）
    - ✅ 批量处理优化：WatermarkPage和AperturePage实现高效的批处理逻辑
    - ✅ Hidden API补丁：完整的SDK类型定义补丁方案和自动化脚本

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

### Git 提交规则

**重要规则**：只有编译通过的代码才commit，绝不擅自提交未编译通过的代码。

**提交前必须验证**：
1. 执行构建：`/build` 或使用DevEco Studio构建
2. 确认编译成功（BUILD SUCCESSFUL）
3. 确认无编译错误（ERROR数为0）
4. 只有警告（WARN）可以接受，但不能有错误

**禁止操作**：
- ❌ 未执行构建就直接commit
- ❌ 构建失败后擅自commit（即使只修复了部分错误）
- ❌ 假设编译会通过而不验证

**正确流程**：
- ✅ 修改代码后立即构建验证
- ✅ 编译成功后再commit
- ✅ 如果编译失败，修复所有错误后重新构建验证

### Git 历史

**当前分支**: hmos
**主分支**: master

**Git commit历史**：
- HarmonyOS移植完整流程（初始移植 → 功能实现 → 照片处理 → 文档完善）
- 关键commit节点：
  - 完成OsArsenals Android到HarmonyOS移植（28个ETS文件）
  - 实现实况照片、本机水印、元数据编辑、批量光圈功能
  - 修复原神自动化关键逻辑错误（32个命令+38个坐标点）
  - 补充项目README和照片功能文档
- 详见完整历史：`git log --oneline origin/hmos..hmos`

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

### HarmonyOS开发经验教训

---

## ⚠️ 必须遵守的准则（反复偷工减料的严重教训 2026-04-28）

**这是最严重的职业操守问题，必须醒目标注并严格遵守！**

### 🚫 禁止做的事情（绝对不能违反）

1. **❌ 禁止跳过API验证步骤**
   - 计划中写了"需API验证"，就必须验证
   - 不能假设API不存在就直接降级
   - 不能偷懒跳过SDK文档查阅

2. **❌ 禁止擅自降级功能**
   - 必须实现完整功能，不能偷工减料
   - 不能因为"可能不存在"就简化实现
   - 用户明确要求的功能必须完整交付

3. **❌ 禁止不查阅文档就假设API不存在**
   - 必须先查阅SDK类型定义文件（`.d.ts`）
   - 必须使用Grep搜索API方法名
   - 必须确认API签名和参数
   - 只有确认不存在后才能讨论降级

4. **❌ 禁止违背计划中的验证步骤**
   - 计划明确写了步骤，必须严格执行
   - 不能因为"麻烦"就跳过关键步骤
   - 不能擅自修改计划流程

5. **❌ 禁止提交前不系统化检视代码**
   - 必须检查编码规范
   - 必须检查资源管理
   - 必须检查错误处理
   - 必须清理冗余代码

### ✅ 必须遵守的准则（严格执行）

1. **✅ 必须先查阅SDK文档验证API**
   ```bash
   # 查找SDK类型定义文件
   Glob path: SDK目录 pattern: **/*.d.ts
   
   # 搜索API方法
   Grep pattern: 方法名 path: SDK类型定义文件
   
   # 读取完整定义
   Read file_path: SDK类型定义文件
   ```

2. **✅ 必须实现完整功能**
   - 查阅文档确认API存在 → 实现完整功能
   - 确认API不存在 → 与用户讨论降级方案
   - 不能自行决定降级

3. **✅ 必须严格按计划执行**
   - 计划中的验证步骤必须执行
   - 不能跳过关键步骤
   - 每个Phase必须完整实现

4. **✅ 遇到不确定API立即查阅文档**
   - HarmonyOS官方API参考
   - SDK类型定义文件（权威来源）
   - WebSearch查找最新资料
   - 绝不能猜测或假设

5. **✅ 提交前系统化检视代码**
   - 编码规范检查
   - 资源管理检查（finally块）
   - 错误处理检查（判空、try-catch）
   - 布局逻辑检查
   - 清理冗余代码

### 📌 元数据编辑功能的深刻教训（2026-04-28）

**错误行为**：
- 计划Phase 2明确写了"EXIF读取API验证（需API验证）"
- 直接跳过验证，假设API不存在
- 没有查阅SDK文档就实现降级方案（手动输入）
- 用户反馈："你又偷偷降级功能了？" "怎么老是偷工减料"

**实际发现**：
- ✅ HarmonyOS完全支持EXIF读取
- ✅ `imageSource.getImageProperties()`批量读取方法存在
- ✅ PropertyKey枚举完整（Make、Model、FNumber、ISO等10+属性）
- ✅ 这是完整功能，根本不需要降级

**正确流程**（以后必须严格执行）：
1. 查阅SDK类型定义：`@ohos.multimedia.image.d.ts`
2. Grep搜索API：`getImageProperty`、`getImageProperties`、`PropertyKey`
3. Read确认完整定义（line 560+）
4. 实现完整功能（批量读取10个EXIF属性）
5. 系统化检视代码质量
6. 提交详细commit message

**深刻反思**：
偷工减料看似省时间，实际上浪费更多时间：
- 用户投诉 → 反复修改 → 重新实现 → 重新测试 → 重新提交
- 损害信任，用户不再相信你的交付质量
- 正确做法：一开始就完整实现，宁可多花30分钟查阅文档

**承诺**：以后绝对不会再偷工减料，严格遵守上述准则！

---

**实况照片水印功能开发的深刻教训（2026-04-28）**：

这是本项目中最严重的失误案例，浪费了数天时间反复尝试无效方案，最终通过人工审视才发现根本问题。

**失误过程回顾**：
- 问题现象：实况照片处理报错"previous asset creation/modification request has not been applied"
- 反复尝试的方案（全部无效）：
  - 延迟等待：500ms → 2s → 10s → 15s（全部失败）
  - cancelRequest清除pending request（失败）
  - 创建空资产apply（报错"Cannot create or edit asset without data to write"）
  - 创建临时资产apply消耗pending request（失败）
- 根本原因：创建MediaAssetChangeRequest时忘记指定`subtype: PhotoSubtype.MOVING_PHOTO`
- 最终解决：人工审视代码后发现缺失subtype参数，添加后立即解决

**为什么会犯这种低级错误**：

1. **注意力分散在复杂问题上**：
   - ❌ 过度关注"pending request"的复杂系统错误，忽略了最基本的参数配置
   - ❌ 盲目相信日志错误信息，误以为是系统机制问题，实际上是自己参数缺失
   - ✅ 正确做法：遇到错误时，先检查基本参数是否正确，再考虑复杂问题

2. **没有仔细审视代码逻辑**：
   - ❌ 一直在修改流程逻辑（delay/cancel/apply），却没有检查createAssetRequest的参数完整性
   - ❌ 修改代码方案时丢失了关键参数（切换方案时忘记保留subtype）
   - ✅ 正确做法：切换方案时，逐行对比关键代码，确保所有必要参数都保留

3. **盲目尝试相同错误模式**：
   - ❌ 反复尝试delay/cancel/apply的组合，每次只是改变参数值，没有从根本上检查代码
   - ❌ 没有验证假设是否正确（"是pending request问题吗？"）
   - ✅ 正确做法：尝试方案前先验证假设，尝试2-3次后应重新审视问题根源

4. **忽视用户的明确提示**：
   - ❌ 用户问"你就不能把实况照片的视频也放进缓存目录，然后生成吗？"时，只关注流程，没检查参数
   - ❌ 用户多次提醒"你是不是应该apply啊？"，只关注apply操作，没检查资产类型
   - ❌ 用户说"你抓log要加超时啊"时，只加超时，没重新审视代码
   - ✅ 正确做法：用户提出疑问时，应该全面审视相关代码，而不是只关注单一操作

**关键经验教训**：

1. **基本参数检查优先于复杂问题排查**：
   - 遇到错误时，第一件事是检查：参数完整性、类型正确性、路径有效性
   - 不要被复杂的错误信息误导，先排除低级错误

2. **切换方案时要对比关键代码**：
   - 从方案A切换到方案B时，必须逐行对比关键参数
   - 使用对比工具或逐行检查，确保没有遗漏必要配置

3. **日志错误信息可能是误导性的**：
   - "previous asset creation/modification request has not been applied"看似是系统pending机制问题
   - 实际原因可能是参数缺失导致创建失败，触发系统错误提示
   - 要从多个角度分析问题：参数完整性、API用法、系统机制

4. **尝试同一类方案最多2-3次**：
   - 如果delay失败，尝试更长delay最多2次，然后必须重新审视问题
   - 如果方案无效，立即切换思路，不要反复调整参数值

5. **用户质疑时要全面审视**：
   - 用户提问通常指向问题的盲点
   - 不要只回答"好的我试试"，要全面检查相关代码
   - 用户说"你是不是应该X？"，不仅要尝试X，还要检查是否有其他问题

**实况照片功能开发经验（2026-04-25）**：

1. **API使用规范**：
   - ✅ 使用正确的Kit API：`photoAccessHelper.MediaAssetChangeRequest.createAssetRequest`
   - ✅ AVImageGenerator的正确用法：`media.createAVImageGenerator()` + `avImageGenerator.fdSrc = {fd: file.fd}`
   - ❌ 不懂API时应及时查阅文档或询问，不要猜测或编造

2. **判空处理的重要性**：
   - ✅ HarmonyOS的回调函数参数可能为null，必须判空
   - ✅ 示例：`if (err && err.code)` 而不是直接 `if (err.code)`
   - ✅ 所有API调用返回值都要判空：context、photoPicker、result、createRequest等
   - ❌ 缺少判空会导致JS Crash，影响用户体验

3. **生命周期管理**：
   - ✅ DeviceStatusManager定时器：启动前检查是否已在运行，停止旧timer再启动新timer
   - ✅ aboutToAppear/aboutToDisappear正确管理资源
   - ❌ 从后台切换回来时，重复启动timer会导致crash

4. **临时文件管理**：
   - ✅ 使用专用临时文件夹：`${cacheDir}/moving_photo_temp`
   - ✅ aboutToAppear时清理旧文件，aboutToDisappear时清理所有文件
   - ❌ 不要直接使用cacheDir根目录，会污染缓存空间

5. **UI细节规范**：
   - ✅ 按钮样式统一：宽度、高度、字体大小、颜色必须一致
   - ✅ 显式设置backgroundColor，不要依赖默认样式
   - ✅ 图片/视频使用ImageFit.Contain完整显示，不要Cover裁剪
   - ✅ 安全区域预留：底部padding至少64dp，避免被导航栏遮挡
   - ❌ 不同按钮颜色不一致会显得不专业

6. **功能逻辑设计**：
   - ✅ 生成成功后清空状态，支持连续生成
   - ✅ 使用promptAction.showToast提示用户，而不是Text组件
   - ❌ 不要在生成成功后自动返回上一页（setTimeout + router.back()）
   - ❌ 从后台切换时setTimeout可能立即执行，导致页面退出

7. **权限配置完整流程**：
   - ✅ Profile ACL添加权限（`hw_sign/UnsgnedReleasedProfileTemplate.json`）
   - ✅ module.json5声明权限（`requestPermissions`）
   - ✅ EntryAbility启动时请求权限（`requestPermissionsFromUser`）
   - ❌ 缺少任何一个步骤都会导致Permission denied

8. **Git提交规范**：
   - ✅ 功能开发完成后合并相关commit，保持历史清晰
   - ✅ 使用`git reset --soft` + 重新commit合并多个小commit
   - ✅ Commit message详细描述功能、修复、测试验证
   - ❌ 不要保留大量零碎的修复commit

**通用开发原则**：
- 先验证编译成功再commit，绝不提交未编译通过的代码
- 遵循HarmonyOS UX最佳实践，使用标准组件和样式
- 完善错误处理，所有可能失败的API调用都要try-catch
- 测试从后台切换回来的场景，验证生命周期稳定性

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
- 自动配置环境变量、执行构建、系统签名、可选部署

**构建流程**（使用build skill）：
```bash
/build               # 自动构建并签名
```

执行步骤：
1. 配置环境变量（DEVECO_SDK_HOME、OHOS_SDK_HOME、JAVA_HOME）
2. 使用DevEco Studio的node和hvigorw.js执行构建
3. 自动系统签名（SystemSignPlugin）
4. 生成entry-default-signed.hap（约900KB）

## 常见构建错误排查

### ArkTS编译错误

**错误示例**：
- `Cannot find module '@ohos.statistical'`: Kit API不存在
- `Property 'getBatteryInfo' does not exist`: API方法不存在
- `Use explicit types instead of "any", "unknown"`: ArkTS严格类型检查
- `Module has no default export`: 缺少export default声明

**解决**：
- 使用sysfs文件读取代替不存在的Kit API
- 添加明确的类型声明，避免any/unknown
- 所有页面文件添加export default声明
- TouchEvent使用event.touches[0].x/y访问坐标

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
- 已配置ACL权限：
  - `ohos.permission.SYSTEM_FLOAT_WINDOW`（悬浮窗）
  - `ohos.permission.INJECT_INPUT_EVENT`（输入注入）
  - `ohos.permission.GET_RUNNING_INFO`（运行信息）
  - `ohos.permission.READ_IMAGEVIDEO`（读取媒体文件）
  - `ohos.permission.WRITE_IMAGEVIDEO`（写入媒体文件）
- app-privilege-capabilities: `AllowAppUsePrivilegeExtension`（ServiceAbility特权）

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

## 部署状态

**当前部署情况**：
- ✅ 已构建成功
- ✅ 已安装到华为HarmonyOS设备
- ✅ 应用已启动运行
- Bundle Name：cn.arsenals.osarsenals

**测试建议**：
1. 测试悬浮窗拖动交互
2. 验证真实设备监控数据（sysfs文件读取）
3. 测试输入注入功能（需要设备支持）
4. 测试原神自动化脚本执行
5. 测试挂机模式屏幕常亮和亮度控制