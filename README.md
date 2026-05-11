# OsArsenals

HarmonyOS 设备监控和自动化工具，支持设备状态监控、悬浮窗显示、输入注入、原神自动化等功能。

## 环境要求

- **DevEco Studio**: 最新版本（推荐 5.0+）
- **HarmonyOS SDK**: API Level 22 (SDK 6.0.2)
- **Java**: JBR 21（DevEco Studio 内置）
- **HarmonyOS 设备**: API Level 22+ 或模拟器

## 快速上手

### 1. 克隆项目

```bash
git clone <repository-url>
cd OsArsenals
```

### 2. 配置环境变量（Windows PowerShell）

**重要**：构建前必须配置环境变量，否则会报错"SDK component missing"

```powershell
$env:DEVECO_SDK_HOME = "C:\Program Files\Huawei\DevEco Studio\sdk"
$env:OHOS_SDK_HOME = "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony"
$env:JAVA_HOME = "C:\Program Files\Huawei\DevEco Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

**Linux/Mac 用户**：
```bash
export DEVECO_SDK_HOME="/path/to/DevEco Studio/sdk"
export OHOS_SDK_HOME="/path/to/DevEco Studio/sdk/default/openharmony"
export JAVA_HOME="/path/to/DevEco Studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"
```

### 3. 构建项目

**方法一：使用 DevEco Studio**
- 打开 DevEco Studio
- 导入项目
- 点击 Build > Build Hap(s)/APP(s)

**方法二：命令行构建**
```bash
# Windows
hvigorw assembleHap --mode module -p module=entry@default -p product=default --no-daemon

# Linux/Mac（需要DevEco Studio的node）
"/path/to/DevEco Studio/tools/node/node" hvigorw.js assembleHap --mode module -p module=entry@default -p product=default --no-daemon
```

**构建产物**：
- `entry/build/default/outputs/default/entry-default-signed.hap`（已签名）

### 4. 部署到设备

**连接设备**：
```bash
# 查看连接的设备
hdc list targets

# 应显示设备地址，如：192.168.43.2:33329
```

**安装应用**：
```bash
hdc install -r entry/build/default/outputs/default/entry-default-signed.hap
```

**启动应用**：
```bash
hdc shell "aa start -a EntryAbility -b cn.arsenals.osarsenals"
```

## 主要功能

### 1. 设备监控（Overview Tab）
- 电池状态（电量、温度、充电状态）
- 内存使用（可用内存、总内存）
- CPU 监控（核心数、频率、温度）
- GPU 监控（频率、温度）
- FPS 监控（实时帧率）

### 2. 悬浮窗显示
- 可拖动的监控面板
- 实时显示设备状态
- 支持最小化和展开

### 3. 原神自动化（GenshinPage）
- 10+ 种自动化命令（e、z、a、j、s、sleep 等）
- 3 个预设脚本（clear_pool、forsaken_rift、pale_forgotten_glory）
- 坐标点管理（38 个预设坐标）
- 支持自定义脚本

### 4. 挂机模式（HookPage）
- 屏幕常亮（防止息屏）
- 亮度控制（挂机模式亮度 0.01）
- OLED 屏幕保护（黑色背景）

### 5. 待机显示（StandbyPage）
- 实时时钟显示
- 防烧屏机制（5 分钟定时位移）

### 6. 性能测试（PerformanceTestPage）
- 单核/多核 CPU 压力测试
- 实时进度显示

### 7. 后台服务（ServiceAbility）
- 支持远程命令执行（INJECT_INPUT、UPDATE_POINT_MAP、EXECUTE_COMMAND）
- CommonEvent 事件订阅

### 8. 照片处理功能（完整实现）

#### a. 实况照片生成（MovingPhotoPage）
- 选择静态图片 + 动态视频创建实况照片
- AVImageGenerator提取视频首帧作为缩略图
- 专用临时文件夹，生命周期自动清理
- 支持连续生成

#### b. 本机水印功能（WatermarkPage）
- 查看和设置水印属性（最多500张照片）
- Hidden API支持：PhotoKeys.SUPPORTED_WATERMARK_TYPE
- SDK补丁方案：完整的类型定义补丁和自动化脚本
- EXIF修改：同时设置Make/Model为设备信息
- 实况照片支持：保持动态特性并添加水印

#### c. 元数据编辑（MetadataEditPage）
- 读取10个EXIF属性（Make、Model、FNumber、ISO、曝光时间、GPS等）
- 批量读取方法：image.getImageProperties（完整功能）
- 中文标签显示，可编辑输入框
- 实况照片支持：检测MOVING_PHOTO subtype
- 另存为：原文件名_exif

#### d. 批量光圈修改（AperturePage）
- 批量修改FNumber光圈值（最多500张）
- 纯数字输入（如2.8、1.8）
- 批处理优化：每批10张并行处理，实时进度显示
- 错误隔离：单张失败不影响其他照片
- 另存为：原文件名_fn

## 常见问题

### Q1: 构建报错 "SDK component missing"
**原因**：缺少环境变量配置

**解决**：
```bash
# 配置三个环境变量（见"快速上手"章节）
export DEVECO_SDK_HOME="..."
export OHOS_SDK_HOME="..."
export JAVA_HOME="..."
```

### Q2: 安装报错 "signature verification failed"
**原因**：Profile 模板的 bundle-name 与 app.json5 不一致

**解决**：
- 确认 `hw_sign/UnsgnedReleasedProfileTemplate.json` 的 `bundle-name` 为 `cn.arsenals.osarsenals`
- 确认 `AppScope/app.json5` 的 `bundleName` 为 `cn.arsenals.osarsenals`

### Q3: 运行报错 "201 - Permission verification failed"
**原因**：系统权限未正确配置

**解决**：
1. 检查 `hw_sign/UnsgnedReleasedProfileTemplate.json` 的 `allowed-acls` 是否包含所需权限
2. 确认 `apl` 设置为 `system_core`（最高权限）
3. 删除 `hw_sign/openharmony_sx.p7b` 重新构建

### Q4: hdc 命令找不到设备
**解决**：
- 确认设备开启开发者模式和 USB 调试
- 确认 DevEco Studio 已安装 hdc 工具
- 尝试重启 hdc 服务：`hdc kill` 然后 `hdc start`

### Q5: 原神自动化执行失败
**原因**：需要 INJECT_INPUT_EVENT 权限

**解决**：
- 确认系统签名正确（`apl: system_core`）
- 确认 `allowed-acls` 包含 `ohos.permission.INJECT_INPUT_EVENT`

### Q6: 本机水印功能编译报错
**原因**：Hidden API（PhotoKeys.SUPPORTED_WATERMARK_TYPE）未在SDK类型定义中声明

**解决**：
1. **自动补丁（推荐）**：右键点击 `patch_sdk_hidden_api.ps1` → 以管理员身份运行
2. **手动补丁**：参考 `SDK_HIDDEN_API_PATCH.md` 文档手动复制类型定义文件
3. 补丁后会添加：
   - PhotoKeys.SUPPORTED_WATERMARK_TYPE
   - WatermarkType枚举（DEFAULT=0, BRAND_COMMON=1, COMMON=2, BRAND=3）
   - MediaAssetChangeRequest.setSupportedWatermarkType方法

**注意**：补丁会修改SDK文件，建议先备份。SDK更新后可能需要重新应用补丁。

## 系统权限配置

项目使用 OpenHarmony 系统签名，获取系统级权限：

- **权限等级**：`system_core`（最高权限）
- **应用类型**：`hos_system_app`（系统应用）
- **已配置权限**：
  - `ohos.permission.SYSTEM_FLOAT_WINDOW`（悬浮窗）
  - `ohos.permission.INJECT_INPUT_EVENT`（输入注入）
  - `ohos.permission.GET_RUNNING_INFO`（运行信息）
  - `ohos.permission.READ_IMAGEVIDEO`（读取媒体文件）
  - `ohos.permission.WRITE_IMAGEVIDEO`（写入媒体文件）

**添加新权限**：
1. 编辑 `hw_sign/UnsgnedReleasedProfileTemplate.json`：
   ```json
   "acls": {
       "allowed-acls": ["新权限名称"]
   }
   ```

2. 编辑 `entry/src/main/module.json5`：
   ```json5
   "requestPermissions": [
       {
           "name": "新权限名称",
           "reason": "$string:permission_reason",
           "usedScene": {
               "abilities": ["EntryAbility"],
               "when": "inuse"
           }
       }
   ]
   ```

3. 删除旧 Profile 并重新构建：
   ```bash
   rm hw_sign/openharmony_sx.p7b
   hvigorw assembleHap --mode module -p module=entry@default
   ```

## 开发文档

- **CLAUDE.md**: 详细的项目开发指南（架构、构建、调试、安全规范）
- **SDK_HIDDEN_API_PATCH.md**: SDK Hidden API 补丁说明

## 安全注意事项

**重要**：本项目使用示例信息，请勿提交真实敏感数据：

- ❌ 禁止提交真实个人信息（姓名、邮箱、手机号）
- ❌ 禁止提交真实设备信息（设备 ID、MAC 地址）
- ❌ 禁止提交真实路径信息（包含真实用户名的路径）
- ✅ 使用示例用户名：`demo`、`example`、`test`
- ✅ 使用示例邮箱：`demo@example.com`
- ✅ 使用示例 IP：`<device-ip>` 或 `192.168.1.100`

**Git 提交前检查**：
```bash
# 扫描敏感信息
git diff | grep -iE "(真实用户名|真实邮箱|真实IP)"

# 确认 author 信息
git log --format="%an <%ae>" -1
# 应为：demo developer <demo@example.com>
```

## License

本项目仅供学习和研究使用。

## 联系方式

如有问题，请提交 Issue 或联系项目维护者。