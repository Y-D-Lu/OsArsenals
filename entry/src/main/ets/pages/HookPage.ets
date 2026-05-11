import Logger from '../common/logger';
import window from '@ohos.window';

@Entry
@Component
struct HookPage {
  @State isHooking: boolean = false;
  private mainWindow: window.Window | null = null;

  aboutToAppear(): void {
    Logger.info('HookPage aboutToAppear');
    this.initWindow();
  }

  aboutToDisappear(): void {
    Logger.info('HookPage aboutToDisappear');
    // 如果正在挂机，退出时恢复亮度
    if (this.isHooking) {
      this.exitHookMode();
    }

    // 恢复非全屏显示
    if (this.mainWindow) {
      this.mainWindow.setFullScreen(false)
        .then(() => {
          Logger.info('HookPage: Full screen disabled');
        })
        .catch((error: Error) => {
          Logger.error(`Failed to disable full screen: ${error.message}`);
        });
    }
  }

  private async initWindow(): Promise<void> {
    try {
      // 获取主窗口实例
      this.mainWindow = await window.getLastWindow(getContext(this));
      Logger.info('HookPage: Got main window instance');

      // 设置全屏显示（隐藏状态栏和导航栏）
      await this.mainWindow.setFullScreen(true);
      Logger.info('HookPage: Full screen enabled');
    } catch (error) {
      Logger.error(`Failed to get window instance: ${error}`);
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.hook_mode'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Column() {
        Text('挂机模式说明')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)
          .margin({ bottom: 16 })

        Text('保持屏幕常亮，亮度最低，适合长时间挂机场景')
          .fontSize(14)
          .fontColor($r('app.color.text_secondary'))
          .margin({ bottom: 24 })

        Button(this.isHooking ? '退出挂机' : '开始挂机')
          .width('100%')
          .height(40)
          .fontSize(14)
          .backgroundColor($r('app.color.primary'))
          .onClick(() => {
            if (this.isHooking) {
              this.exitHookMode();
            } else {
              this.enterHookMode();
            }
          })
      }
      .width('100%')
      .padding(16)
      .justifyContent(FlexAlign.Center)
    }
    .width('100%')
    .height('100%')
    .backgroundColor(this.isHooking ? Color.Black : $r('app.color.background'))
  }

  private async enterHookMode(): Promise<void> {
    Logger.info('Enter hook mode');
    this.isHooking = true;

    if (this.mainWindow) {
      try {
        // 设置屏幕常亮
        await this.mainWindow.setWindowKeepScreenOn(true);
        Logger.info('Screen keep awake enabled');

        // 设置最低亮度（0.01为最低，1.0为最高）
        await this.mainWindow.setWindowBrightness(0.01);
        Logger.info('Brightness set to minimum (0.01)');
      } catch (error) {
        Logger.error(`Failed to set hook mode: ${error}`);
      }
    } else {
      Logger.warn('Window instance not available, hook mode settings skipped');
    }
  }

  private async exitHookMode(): Promise<void> {
    Logger.info('Exit hook mode');
    this.isHooking = false;

    if (this.mainWindow) {
      try {
        // 恢复屏幕常亮状态
        await this.mainWindow.setWindowKeepScreenOn(false);
        Logger.info('Screen keep awake disabled');

        // 恢复正常亮度（使用系统默认亮度）
        await this.mainWindow.setWindowBrightness(-1.0); // -1表示使用系统亮度
        Logger.info('Brightness restored to system default');
      } catch (error) {
        Logger.error(`Failed to restore normal mode: ${error}`);
      }
    }
  }
}

export default HookPage;
