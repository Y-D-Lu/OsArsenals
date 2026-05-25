# HarmonyOS Hidden API 补丁说明

## 问题背景

HarmonyOS的hidden系统API存在于运行时，但SDK的类型定义文件中没有公开声明，导致ArkTS编译器无法识别。

## Hidden API详情

### photoAccessHelper 补丁（水印相关API）

#### 1. PhotoKeys.SUPPORTED_WATERMARK_TYPE
- 类型：string枚举值
- 值：'supported_watermark_type'
- 用途：查询图片资产的水印类型属性

#### 2. WatermarkType枚举
- DEFAULT = 0（默认水印）
- BRAND_COMMON = 1（品牌通用水印）
- COMMON = 2（通用水印）
- BRAND = 3（品牌水印）

#### 3. MediaAssetChangeRequest.setSupportedWatermarkType()
- 方法签名：`setSupportedWatermarkType(watermarkType: WatermarkType): void`
- 用途：设置图片的水印类型

### inputEventClient 补丁（输入注入API）

#### 1. injectTouchEvent()
- 方法签名：`injectTouchEvent(event: { touchEvent: TouchEvent }): void`
- 用途：注入触摸事件（点击、滑动、缩放等）
- 权限：ohos.permission.INJECT_INPUT_EVENT

#### 2. injectKeyEvent()
- 方法签名：`injectKeyEvent(event: { keyEvent: KeyEvent }): void`
- 用途：注入按键事件
- 权限：ohos.permission.INJECT_INPUT_EVENT

#### 3. KeyEvent 接口（inputEventClient专用）
- `isPressed: boolean` - 是否按下
- `keyCode: number` - 按键代码
- `keyDownDuration: number` - 按下时长（微秒）
- `isIntercepted: boolean` - 是否被拦截

注意：此 `KeyEvent` 与 `@ohos.multimodalInput.keyEvent` 的 `KeyEvent` 是不同的接口。

## 补丁文件位置

项目已准备好完整的SDK补丁文件：
```
entry/src/main/ets/types/@ohos.file.photoAccessHelper.d.ts
entry/src/main/ets/types/@ohos.multimodalInput.inputEventClient.d.ts
```

- photoAccessHelper：基于官方SDK文件，添加了水印Hidden API的类型定义（Replace模式）
- inputEventClient：全新创建的类型定义文件，SDK中原本不存在（Create模式）

## 应用补丁方法

### 方法1：使用PowerShell脚本（推荐）

1. 右键点击 `patch_sdk_hidden_api.ps1`
2. 选择"以管理员身份运行"
3. 脚本会自动备份原SDK文件并应用两个补丁
4. 如自动检测路径失败，会提示输入项目根目录

### 方法2：手动复制

1. 以管理员身份打开PowerShell
2. 执行以下命令：

```powershell
$sdkDir = "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api"
$projectDir = "<你的项目根目录>"

# 补丁1: photoAccessHelper（备份原文件后替换）
Copy-Item "$sdkDir\@ohos.file.photoAccessHelper.d.ts" "$sdkDir\@ohos.file.photoAccessHelper.d.ts.backup" -Force
Copy-Item "$projectDir\entry\src\main\ets\types\@ohos.file.photoAccessHelper.d.ts" "$sdkDir\@ohos.file.photoAccessHelper.d.ts" -Force

# 补丁2: inputEventClient（SDK中不存在，直接创建）
Copy-Item "$projectDir\entry\src\main\ets\types\@ohos.multimodalInput.inputEventClient.d.ts" "$sdkDir\@ohos.multimodalInput.inputEventClient.d.ts" -Force
```

## 恢复原SDK

```powershell
# 恢复 photoAccessHelper
Copy-Item "$sdkDir\@ohos.file.photoAccessHelper.d.ts.backup" "$sdkDir\@ohos.file.photoAccessHelper.d.ts" -Force

# 删除 inputEventClient（SDK中原本不存在）
Remove-Item "$sdkDir\@ohos.multimodalInput.inputEventClient.d.ts" -Force
```

## 补丁后的使用

```typescript
// 水印相关API
const watermarkType = photoAsset.get(photoAccessHelper.PhotoKeys.SUPPORTED_WATERMARK_TYPE);
changeRequest.setSupportedWatermarkType(photoAccessHelper.WatermarkType.BRAND_COMMON);

// 输入注入API
import inputEventClient from '@ohos.multimodalInput.inputEventClient';
inputEventClient.injectTouchEvent({ touchEvent: downEvent });
inputEventClient.injectKeyEvent({ keyEvent: keyEvent });
```

## 注意事项

1. 补丁会修改/新增SDK文件，建议先备份
2. SDK更新后可能需要重新应用补丁
3. 此补丁仅用于开发编译，不影响运行时行为（hidden API实际已存在于系统）
4. inputEventClient 需要系统签名 + ohos.permission.INJECT_INPUT_EVENT 权限