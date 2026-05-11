import Logger from '../common/logger';
import window from '@ohos.window';

@Entry
@Component
struct StandbyPage {
  @State currentTime: string = '';
  @State offsetX: number = 0;  // 防烧屏位移X
  @State offsetY: number = 0;  // 防烧屏位移Y
  private timer: number | null = null;
  private antiBurnInTimer: number | null = null;
  private mainWindow: window.Window | null = null;

  aboutToAppear(): void {
    Logger.info('StandbyPage aboutToAppear');
    this.startClock();
    this.startAntiBurnIn();
    this.initWindow();
  }

  aboutToDisappear(): void {
    Logger.info('StandbyPage aboutToDisappear');
    this.stopClock();
    this.stopAntiBurnIn();

    // 恢复非全屏显示和竖屏方向
    if (this.mainWindow) {
      this.mainWindow.setFullScreen(false)
        .then(() => {
          Logger.info('StandbyPage: Full screen disabled');
        })
        .catch((error: Error) => {
          Logger.error(`Failed to disable full screen: ${error.message}`);
        });

      this.mainWindow.setPreferredOrientation(window.Orientation.PORTRAIT)
        .then(() => {
          Logger.info('StandbyPage: Portrait orientation restored');
        })
        .catch((error: Error) => {
          Logger.error(`Failed to restore portrait orientation: ${error.message}`);
        });
    }
  }

  private async initWindow(): Promise<void> {
    try {
      // 获取主窗口实例
      this.mainWindow = await window.getLastWindow(getContext(this));
      Logger.info('StandbyPage: Got main window instance');

      // 设置全屏显示（隐藏状态栏和导航栏）
      await this.mainWindow.setFullScreen(true);
      Logger.info('StandbyPage: Full screen enabled');

      // 设置横屏显示
      await this.mainWindow.setPreferredOrientation(window.Orientation.LANDSCAPE);
      Logger.info('StandbyPage: Landscape orientation set');
    } catch (error) {
      Logger.error(`Failed to get window instance: ${error}`);
    }
  }

  build() {
    Column() {
      Column() {
        Text(this.currentTime)
          .fontSize(120)
          .fontColor(Color.White)
          .fontWeight(FontWeight.Bold)
      }
      .width('100%')
      .height('100%')
      .justifyContent(FlexAlign.Center)
      .translate({ x: this.offsetX, y: this.offsetY })  // 防烧屏位移
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.Black)
  }

  private startClock(): void {
    // 启动前检查并清理旧timer，防止重复启动
    if (this.timer) {
      Logger.warn('StandbyPage: Clock timer already running, stopping old timer first');
      this.stopClock();
    }

    this.updateTime();
    this.timer = setInterval(() => {
      this.updateTime();
    }, 1000);
  }

  private stopClock(): void {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  private updateTime(): void {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    this.currentTime = `${hours}:${minutes}`;
  }

  private startAntiBurnIn(): void {
    // 启动前检查并清理旧timer，防止重复启动
    if (this.antiBurnInTimer) {
      Logger.warn('StandbyPage: Anti burn-in timer already running, stopping old timer first');
      this.stopAntiBurnIn();
    }

    // 每5分钟位移一次，防止OLED烧屏
    this.antiBurnInTimer = setInterval(() => {
      this.offsetX += 10;
      if (this.offsetX >= 50) {
        this.offsetX = 0;
        this.offsetY += 10;
        if (this.offsetY >= 50) {
          this.offsetY = 0;
        }
      }
      Logger.info(`Anti burn-in offset: (${this.offsetX}, ${this.offsetY})`);
    }, 300000); // 5分钟间隔
  }

  private stopAntiBurnIn(): void {
    if (this.antiBurnInTimer) {
      clearInterval(this.antiBurnInTimer);
      this.antiBurnInTimer = null;
    }
  }
}export default StandbyPage;
