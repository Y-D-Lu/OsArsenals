import { DeviceStatusManager } from '../managers/DeviceStatusManager';
import { DeviceStatusInfo } from '../models/DeviceStatusInfo';
import { PercentageCircle } from '../components/PercentageCircle';
import { PercentageRect } from '../components/PercentageRect';
import { FloatingWindowManager } from '../managers/FloatingWindowManager';
import Logger from '../common/logger';

@Entry
@Component
struct OverviewPage {
  @State deviceInfo: DeviceStatusInfo = new DeviceStatusInfo();
  @State isFloatingWindowShowing: boolean = false;
  private deviceStatusManager: DeviceStatusManager = new DeviceStatusManager();
  private floatingWindowManager: FloatingWindowManager | null = null;

  // 格式化数值显示，-1表示不可用，显示为"--"
  private formatValue(value: number, suffix: string = ''): string {
    if (value === -1 || value === undefined) {
      return '--';
    }
    return `${value}${suffix}`;
  }

  // 格式化带小数的数值显示
  private formatFloatValue(value: number, decimals: number = 1, suffix: string = ''): string {
    if (value === -1 || value === undefined) {
      return '--';
    }
    return `${value.toFixed(decimals)}${suffix}`;
  }

  // 格式化存储大小显示（KB转换为GB，小数点后一位）
  private formatStorageKB(kb: number): string {
    if (kb === -1 || kb === undefined || kb === 0) {
      return '--';
    }
    const gb = kb / (1024 * 1024);
    return `${gb.toFixed(1)} GB`;
  }

  // 格式化存储大小显示（MB转换为GB，小数点后一位）
  private formatStorageMB(mb: number): string {
    if (mb === -1 || mb === undefined || mb === 0) {
      return '--';
    }
    const gb = mb / 1024;
    return `${gb.toFixed(1)} GB`;
  }

  // 格式化RAM显示：已用/总
  private formatRamDisplay(totalKB: number, availableKB: number): string {
    if (totalKB === -1 || totalKB === 0 || availableKB === -1) {
      return '--';
    }
    const usedKB = totalKB - availableKB;
    const usedGB = usedKB / (1024 * 1024);
    const totalGB = totalKB / (1024 * 1024);
    return `${usedGB.toFixed(1)}/${totalGB.toFixed(1)} GB`;
  }

  // 格式化ROM显示：已用/总
  private formatRomDisplay(totalMB: number, freeMB: number): string {
    if (totalMB === -1 || totalMB === 0 || freeMB === -1) {
      return '--';
    }
    const usedMB = totalMB - freeMB;
    const usedGB = usedMB / 1024;
    const totalGB = totalMB / 1024;
    return `${usedGB.toFixed(1)}/${totalGB.toFixed(1)} GB`;
  }

  aboutToAppear(): void {
    Logger.info('OverviewPage aboutToAppear');
    this.deviceStatusManager.startMonitoring((info: DeviceStatusInfo) => {
      this.deviceInfo = info;
    });
  }

  aboutToDisappear(): void {
    Logger.info('OverviewPage aboutToDisappear');
    this.deviceStatusManager.stopMonitoring();
    if (this.floatingWindowManager) {
      this.floatingWindowManager.destroyFloatingWindow();
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.device_status_overview'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))

        Button(this.isFloatingWindowShowing ? $r('app.string.stop_floating_window') : $r('app.string.start_floating_window'))
          .fontSize(14)
          .height(40)
          .backgroundColor($r('app.color.primary'))
          .onClick(() => {
            this.toggleFloatingWindow();
          })
      }
      .width('100%')
      .justifyContent(FlexAlign.SpaceBetween)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // CPU/RAM/ROM/电池监控区域
          Row() {
            // CPU
            Column() {
              PercentageCircle({
                value: this.deviceInfo.cpuUtilizationList[0] || -1,
                circleSize: 60,
                label: 'CPU'
              })
              .margin({ bottom: 8 })
              Text(this.deviceInfo.thermalLevel)
                .fontSize(11)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('25%')
            .justifyContent(FlexAlign.Start)
            .alignItems(HorizontalAlign.Center)
            .padding({ top: 8, bottom: 8, left: 6, right: 6 })

            // RAM
            Column() {
              PercentageCircle({
                value: this.deviceInfo.ramUtilization,
                circleSize: 60,
                label: 'RAM'
              })
              .margin({ bottom: 8 })
              Text(this.formatRamDisplay(this.deviceInfo.totalRam, this.deviceInfo.availableRam))
                .fontSize(10)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('25%')
            .justifyContent(FlexAlign.Start)
            .alignItems(HorizontalAlign.Center)
            .padding({ top: 8, bottom: 8, left: 6, right: 6 })

            // ROM存储
            Column() {
              PercentageCircle({
                value: this.deviceInfo.totalRom > 0 && this.deviceInfo.freeRom > 0
                  ? Math.round((1 - this.deviceInfo.freeRom / this.deviceInfo.totalRom) * 100)
                  : -1,
                circleSize: 60,
                label: 'ROM'
              })
              .margin({ bottom: 8 })
              Text(this.formatRomDisplay(this.deviceInfo.totalRom, this.deviceInfo.freeRom))
                .fontSize(10)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('25%')
            .justifyContent(FlexAlign.Start)
            .alignItems(HorizontalAlign.Center)
            .padding({ top: 8, bottom: 8, left: 6, right: 6 })

            // 电池
            Column() {
              PercentageCircle({
                value: this.deviceInfo.batteryCapacity,
                circleSize: 60,
                label: '电池'
              })
              .margin({ bottom: 8 })
              Text(this.formatFloatValue(this.deviceInfo.batteryTemp, 1, '°C'))
                .fontSize(11)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('25%')
            .justifyContent(FlexAlign.Start)
            .alignItems(HorizontalAlign.Center)
            .padding({ top: 8, bottom: 8, left: 6, right: 6 })
          }
          .width('100%')
          .justifyContent(FlexAlign.SpaceEvenly)
          .padding({ left: 12, right: 12, top: 16, bottom: 16 })
          .backgroundColor($r('app.color.card_background'))
          .borderRadius($r('app.float.card_corner_radius'))
          .margin({ bottom: 16 })

          // 详细信息列表
          Column() {
            List() {
              ListItem() {
                Row() {
                  Text($r('app.string.current'))
                    .fontSize(14)
                  Text(this.formatValue(this.deviceInfo.batteryCurrent, ' mA'))
                    .fontSize(14)
                    .fontColor($r('app.color.text_secondary'))
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
                .padding(16)
              }

              ListItem() {
                Row() {
                  Text($r('app.string.power'))
                    .fontSize(14)
                  Text(this.formatFloatValue(this.deviceInfo.batteryPower, 2, ' W'))
                    .fontSize(14)
                    .fontColor($r('app.color.text_secondary'))
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
                .padding(16)
              }
            }
            .width('100%')
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
          }
          .width('100%')
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 16, bottom: 64 })
      }
      .width('100%')
      .scrollBar(BarState.Auto)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }

  private async toggleFloatingWindow(): Promise<void> {
    try {
      if (!this.floatingWindowManager) {
        this.floatingWindowManager = new FloatingWindowManager();
      }

      if (this.isFloatingWindowShowing) {
        await this.floatingWindowManager.hideFloatingWindow();
      } else {
        await this.floatingWindowManager.createFloatingWindow(getContext(this));
      }
      this.isFloatingWindowShowing = this.floatingWindowManager.isFloatingWindowShowing();
      Logger.info(`Floating window toggled: showing=${this.isFloatingWindowShowing}`);
    } catch (error) {
      Logger.error(`Failed to toggle floating window: ${error}`);
    }
  }
}

export default OverviewPage;
