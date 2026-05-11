import { DeviceStatusManager } from '../managers/DeviceStatusManager';
import { DeviceStatusInfo } from '../models/DeviceStatusInfo';
import { PercentageCircle } from '../components/PercentageCircle';
import { PercentageRect } from '../components/PercentageRect';
import Logger from '../common/logger';
import window from '@ohos.window';

/**
 * 悬浮窗监控面板页面
 */
@Entry
@Component
struct MonitorPanel {
  @State deviceInfo: DeviceStatusInfo = new DeviceStatusInfo();
  private deviceStatusManager: DeviceStatusManager = new DeviceStatusManager();
  private windowInstance: window.Window | null = null;
  private windowX: number = 100;
  private windowY: number = 100;
  private startX: number = 0;  // 手势开始时的窗口X位置
  private startY: number = 0;  // 手势开始时的窗口Y位置

  aboutToAppear(): void {
    Logger.info('MonitorPanel aboutToAppear');
    // 获取当前窗口实例
    this.initWindow();
    // 启动设备状态监控
    this.deviceStatusManager.startMonitoring((info: DeviceStatusInfo) => {
      this.deviceInfo = info;
    });
  }

  private initWindow(): void {
    try {
      // 通过窗口名称查找悬浮窗实例（而不是getLastWindow）
      this.windowInstance = window.findWindow('floatWindow');
      Logger.info('MonitorPanel: Got floating window instance for dragging');
    } catch (error) {
      Logger.error(`Failed to find floating window: ${error}`);
    }
  }

  aboutToDisappear(): void {
    Logger.info('MonitorPanel aboutToDisappear');
    // 停止监控
    this.deviceStatusManager.stopMonitoring();
  }

  build() {
    Row() {
      // 左侧：图表区域（36dp宽度）
      Column() {
        // CPU利用率矩形条
        PercentageRect({ value: this.deviceInfo.getTotalCpuUtilization() })
          .margin({ bottom: 4 })

        // RAM利用率圆形
        PercentageCircle({
          value: this.deviceInfo.ramUtilization,
          circleSize: 30,
          label: ''
        })

        // 电池容量圆形
        PercentageCircle({
          value: this.deviceInfo.batteryCapacity,
          circleSize: 30,
          label: ''
        })
      }
      .width(36)
      .height('100%')
      .justifyContent(FlexAlign.SpaceAround)
      .padding(4)

      // 右侧：详细信息列表
      Column() {
        Text(`CPU: ${this.deviceInfo.getTotalCpuUtilization()}%`)
          .fontSize(12)
          .fontColor(Color.White)
          .margin({ bottom: 2 })

        Text(`${this.deviceInfo.thermalLevel}`)
          .fontSize(12)
          .fontColor(Color.White)
          .margin({ bottom: 2 })

        Text(`RAM: ${this.deviceInfo.ramUtilization}%`)
          .fontSize(12)
          .fontColor(Color.White)
          .margin({ bottom: 2 })

        Text(`电流: ${this.deviceInfo.batteryCurrent}mA`)
          .fontSize(12)
          .fontColor(Color.White)
          .margin({ bottom: 2 })

        Text(`功率: ${this.deviceInfo.batteryPower.toFixed(2)}W`)
          .fontSize(12)
          .fontColor(Color.White)
      }
      .justifyContent(FlexAlign.SpaceAround)
      .padding(4)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#3F000000') // 半透明黑色背景
    .borderRadius(8)
    .gesture(
      PanGesture()
        .onActionStart((event: GestureEvent) => {
          this.startX = this.windowX;
          this.startY = this.windowY;
          Logger.info(`PanGesture start at (${this.startX}, ${this.startY})`);
        })
        .onActionUpdate((event: GestureEvent) => {
          if (!this.windowInstance) {
            return;
          }

          const offsetX = Math.round(event.offsetX);
          const offsetY = Math.round(event.offsetY);

          this.windowX += offsetX;
          this.windowY += offsetY;

          this.windowInstance.moveWindowTo(this.windowX, this.windowY)
            .catch((error: Error) => {
              Logger.error(`Failed to move window: ${error.message}`);
            });
        })
        .onActionEnd((event: GestureEvent) => {
          Logger.info(`PanGesture end at (${this.windowX}, ${this.windowY})`);
        })
    )
  }
}

export default MonitorPanel;