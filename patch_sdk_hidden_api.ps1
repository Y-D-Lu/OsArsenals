# HarmonyOS SDK Hidden API 补丁脚本
# 需要以管理员身份运行

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "HarmonyOS SDK Hidden API 补丁" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# 检查管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "错误：需要管理员权限运行此脚本！" -ForegroundColor Red
    Write-Host "请右键点击此脚本，选择'以管理员身份运行'" -ForegroundColor Yellow
    pause
    exit 1
}

# SDK文件路径
$sdkPath = "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts"
$backupPath = "C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api\@ohos.file.photoAccessHelper.d.ts.backup"
$patchPath = "C:\Users\OseasyVM\Desktop\OsArsenals\OsArsenals\entry\src\main\ets\types\@ohos.file.photoAccessHelper.d.ts"

Write-Host "SDK文件路径: $sdkPath" -ForegroundColor Gray
Write-Host "备份文件路径: $backupPath" -ForegroundColor Gray
Write-Host "补丁文件路径: $patchPath" -ForegroundColor Gray
Write-Host ""

# 检查补丁文件是否存在
if (-not (Test-Path $patchPath)) {
    Write-Host "错误：补丁文件不存在！" -ForegroundColor Red
    Write-Host "路径: $patchPath" -ForegroundColor Red
    pause
    exit 1
}

# 备份原SDK文件
Write-Host "正在备份原SDK文件..." -ForegroundColor Yellow
try {
    Copy-Item $sdkPath $backupPath -Force -ErrorAction Stop
    Write-Host "✓ 备份成功: $backupPath" -ForegroundColor Green
} catch {
    Write-Host "错误：备份失败！" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    pause
    exit 1
}

# 应用补丁
Write-Host "正在应用Hidden API补丁..." -ForegroundColor Yellow
try {
    Copy-Item $patchPath $sdkPath -Force -ErrorAction Stop
    Write-Host "✓ 补丁应用成功！" -ForegroundColor Green
} catch {
    Write-Host "错误：补丁应用失败！" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    pause
    exit 1
}

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "补丁已成功应用！" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "已添加的Hidden API:" -ForegroundColor Cyan
Write-Host "  • PhotoKeys.SUPPORTED_WATERMARK_TYPE" -ForegroundColor White
Write-Host "  • WatermarkType enum (DEFAULT=0, BRAND_COMMON=1, COMMON=2, BRAND=3)" -ForegroundColor White
Write-Host "  • MediaAssetChangeRequest.setSupportedWatermarkType()" -ForegroundColor White
Write-Host ""
Write-Host "现在可以重新构建项目了！" -ForegroundColor Green
Write-Host ""

pause