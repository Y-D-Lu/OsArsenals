import { DeviceStatusInfo } from '../models/DeviceStatusInfo';
import { DeviceStatusUtil } from '../utils/DeviceStatusUtil';
import Logger from '../common/logger';

/**
 * 设备状态管理器
 * 负责定时采集设备状态数据并分发
 */
export class DeviceStatusManager {
  private timer: number | null = null;
  private callback: ((info: DeviceStatusInfo) => void) | null = null;

  /**
   * 启动监控
   * @param callback 数据更新回调函数
   */
  startMonitoring(callback: (info: DeviceStatusInfo) => void): void {
    Logger.info('DeviceStatusManager startMonitoring');

    // 如果已经在监控，先停止旧的监控
    if (this.timer) {
      Logger.warn('DeviceStatusManager already monitoring, stopping old timer first');
      this.stopMonitoring();
    }

    if (!callback) {
      Logger.error('Callback is null, cannot start monitoring');
      return;
    }

    this.callback = callback;

    // 立即执行一次
    try {
      const info = this.collectDeviceInfo();
      callback(info);
    } catch (error) {
      Logger.error(`Failed to collect device info: ${error}`);
    }

    // 启动定时器（1秒间隔）
    this.timer = setInterval(() => {
      try {
        const info = this.collectDeviceInfo();
        if (this.callback) {
          this.callback(info);
        }
      } catch (error) {
        Logger.error(`Failed to collect device info in timer: ${error}`);
      }
    }, 1000);

    Logger.info('DeviceStatusManager timer started');
  }

  /**
   * 停止监控
   */
  stopMonitoring(): void {
    Logger.info('DeviceStatusManager stopMonitoring');

    if (this.timer) {
      try {
        clearInterval(this.timer);
        Logger.info('DeviceStatusManager timer cleared');
      } catch (error) {
        Logger.error(`Failed to clear timer: ${error}`);
      }
      this.timer = null;
    } else {
      Logger.info('DeviceStatusManager no timer to clear');
    }

    this.callback = null;
  }

  /**
   * 采集设备信息
   */
  private collectDeviceInfo(): DeviceStatusInfo {
    const info = new DeviceStatusInfo();

    try {
      info.cpuUtilizationList = DeviceStatusUtil.getCpuUtilizationList();
    } catch (error) {
      Logger.error(`Failed to get CPU utilization: ${error}`);
      info.cpuUtilizationList = [];
    }

    try {
      info.thermalLevel = DeviceStatusUtil.getThermalLevel();
    } catch (error) {
      Logger.error(`Failed to get thermal level: ${error}`);
      info.thermalLevel = '--';
    }

    try {
      info.batteryCapacity = DeviceStatusUtil.getBatteryCapacity();
    } catch (error) {
      Logger.error(`Failed to get battery capacity: ${error}`);
      info.batteryCapacity = 0;
    }

    try {
      info.batteryTemp = DeviceStatusUtil.getBatteryTemp();
    } catch (error) {
      Logger.error(`Failed to get battery temp: ${error}`);
      info.batteryTemp = 0;
    }

    try {
      info.batteryCurrent = DeviceStatusUtil.getBatteryCurrent();
    } catch (error) {
      Logger.error(`Failed to get battery current: ${error}`);
      info.batteryCurrent = 0;
    }

    try {
      info.batteryVoltage = DeviceStatusUtil.getBatteryVoltage();
    } catch (error) {
      Logger.error(`Failed to get battery voltage: ${error}`);
      info.batteryVoltage = 0;
    }

    try {
      info.batteryPower = DeviceStatusUtil.getBatteryPower();
    } catch (error) {
      Logger.error(`Failed to get battery power: ${error}`);
      info.batteryPower = 0;
    }

    try {
      info.totalRam = DeviceStatusUtil.getTotalRam();
    } catch (error) {
      Logger.error(`Failed to get total RAM: ${error}`);
      info.totalRam = 0;
    }

    try {
      info.availableRam = DeviceStatusUtil.getAvailableRam();
    } catch (error) {
      Logger.error(`Failed to get available RAM: ${error}`);
      info.availableRam = 0;
    }

    try {
      info.ramUtilization = DeviceStatusUtil.getRamUtilization();
    } catch (error) {
      Logger.error(`Failed to get RAM utilization: ${error}`);
      info.ramUtilization = 0;
    }

    try {
      info.totalRom = DeviceStatusUtil.getTotalRom();
    } catch (error) {
      Logger.error(`Failed to get total ROM: ${error}`);
      info.totalRom = -1;
    }

    try {
      info.freeRom = DeviceStatusUtil.getFreeRom();
    } catch (error) {
      Logger.error(`Failed to get free ROM: ${error}`);
      info.freeRom = -1;
    }

    return info;
  }
}