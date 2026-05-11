# HarmonyOS Hidden API 补丁说明

## 问题背景

HarmonyOS的hidden系统API存在于运行时，但SDK的类型定义文件中没有公开声明，导致ArkTS编译器无法识别。

## Hidden API详情

### 1. PhotoKeys.SUPPORTED_WATERMARK_TYPE
- 类型：string枚举值
- 值：'supported_watermark_type'
- 用途：查询图片资产的水印类型属性

### 2. WatermarkType枚举
- DEFAULT = 0（默认水印）
- BRAND_COMMON = 1（品牌通用水印）
- COMMON = 2（通用水印）
- BRAND = 3（品牌水印）

### 3. MediaAssetChangeRequest.setSupportedWatermarkType()
- 方法签名：`setSupportedWatermarkType(watermarkType: WatermarkType): void`
- 用途：设置图片的水印类型

## 补丁文件位置

项目已准备好完整的SDK补丁文件：
```
entry/src/main/ets/types/@ohos.file.photoAccessHelper.d.ts
```

此文件是基于官方SDK文件，添加了hidden API的类型定义。

## 应用补丁方法

### 方法1：使用PowerShell脚本（推荐）

1. 右键点击 `patch_sdk_hidden_api.ps1`
2. 选择"以管理员身份运行"
3. 脚本会自动备份原SDK文件并应用补丁

### 方法2：手动复制

1. 以管理员身份打开PowerShell
2. 执行以下命令：

```powershell
# 备份原SDK文件
Copy-Item "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts" "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts.backup" -Force

# 应用补丁
Copy-Item "C:\Users\OseasyVM\Desktop\OsArsenals\OsArsenals\entry\src\main\ets\types\@ohos.file.photoAccessHelper.d.ts" "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts" -Force
```

## 恢复原SDK

如果需要恢复原SDK文件：

```powershell
Copy-Item "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts.backup" "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts" -Force
```

## 补丁后的使用

补丁应用后，可以正常使用hidden API：

```typescript
// 查询水印类型
const watermarkType = photoAsset.get(photoAccessHelper.PhotoKeys.SUPPORTED_WATERMARK_TYPE);

// 设置水印类型
changeRequest.setSupportedWatermarkType(photoAccessHelper.WatermarkType.BRAND_COMMON);
```

## 注意事项

1. 补丁会修改SDK文件，建议先备份
2. SDK更新后可能需要重新应用补丁
3. 此补丁仅用于开发，不影响运行时行为（hidden API实际已存在）