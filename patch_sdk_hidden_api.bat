@echo off
chcp 65001 >nul
echo ======================================
echo HarmonyOS SDK Hidden API Patch
echo ======================================
echo.

set "SDK_DIR=C:\Program Files\Huawei\DevEco Studio\sdk\default\openharmony\ets\api"
set "PROJECT_DIR=%~dp0"
echo Project: %PROJECT_DIR%
echo SDK Dir: %SDK_DIR%
echo.

echo --- Patch 1: photoAccessHelper ---
if not exist "%SDK_DIR%\@ohos.file.photoAccessHelper.d.ts.backup" (
    if exist "%SDK_DIR%\@ohos.file.photoAccessHelper.d.ts" (
        copy "%SDK_DIR%\@ohos.file.photoAccessHelper.d.ts" "%SDK_DIR%\@ohos.file.photoAccessHelper.d.ts.backup" /Y
        echo   Backup OK
    )
)
copy /Y "%PROJECT_DIR%entry\src\main\ets\types\@ohos.file.photoAccessHelper.d.ts" "%SDK_DIR%\@ohos.file.photoAccessHelper.d.ts"
if errorlevel 1 (
    echo   Patch 1 FAILED
) else (
    echo   Patch 1 OK
)
echo.

echo --- Patch 2: inputEventClient ---
copy /Y "%PROJECT_DIR%entry\src\main\ets\types\@ohos.multimodalInput.inputEventClient.d.ts" "%SDK_DIR%\@ohos.multimodalInput.inputEventClient.d.ts"
if errorlevel 1 (
    echo   Patch 2 FAILED
) else (
    echo   Patch 2 OK
)
echo.

echo ======================================
echo Patch Done!
echo ======================================
echo.
echo Hidden APIs added:
echo   photoAccessHelper: watermark API
echo   inputEventClient: input inject API
echo.
echo Ready to rebuild project!
echo.
pause