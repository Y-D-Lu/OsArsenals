import Logger from '../common/logger';
import batteryInfo from '@ohos.batteryInfo';
import thermal from '@ohos.thermal';
import hidebug from '@ohos.hidebug';
import storageStatistics from '@ohos.file.storageStatistics';
import deviceInfo from '@ohos.deviceInfo';

/**
 * 设备状态采集工具（使用HarmonyOS Kit API）
 * 仅采集官方Kit API支持的数据
 */
export class DeviceStatusUtil {
  // ===== 电池信息（使用batteryInfo Kit API）=====

  static getBatteryCapacity(): number {
    try {
      return batteryInfo.batterySOC;
    } catch (error) {
      Logger.error(`Failed to get battery SOC: ${error}`);
      return -1;
    }
  }

  static getBatteryTemp(): number {
    try {
      // batteryInfo.batteryTemperature：电池温度，单位0.1°C，需要除以10
      return batteryInfo.batteryTemperature / 10.0;
    } catch (error) {
      Logger.error(`Failed to get battery temperature: ${error}`);
      return -1;
    }
  }

  static getBatteryCurrent(): number {
    try {
      return Math.abs(batteryInfo.nowCurrent || 0);
    } catch (error) {
      Logger.error(`Failed to get battery current: ${error}`);
      return -1;
    }
  }

  static getBatteryVoltage(): number {
    try {
      return batteryInfo.voltage / 1000;
    } catch (error) {
      Logger.error(`Failed to get battery voltage: ${error}`);
      return -1;
    }
  }

  static getBatteryPower(): number {
    const current = DeviceStatusUtil.getBatteryCurrent();
    const voltage = DeviceStatusUtil.getBatteryVoltage();
    return (current * voltage) / 1000000.0;
  }

  // ===== ROM存储信息（使用storageStatistics Kit API）=====

  static getTotalRom(): number {
    try {
      // storageStatistics.getTotalSizeSync() 返回总存储空间（bytes）
      const totalSize: number = storageStatistics.getTotalSizeSync();
      return totalSize / (1024 * 1024); // 转换为MB
    } catch (error) {
      Logger.error(`Failed to get total ROM: ${error}`);
      return -1;
    }
  }

  static getFreeRom(): number {
    try {
      // storageStatistics.getFreeSizeSync() 返回可用存储空间（bytes）
      const freeSize: number = storageStatistics.getFreeSizeSync();
      return freeSize / (1024 * 1024); // 转换为MB
    } catch (error) {
      Logger.error(`Failed to get free ROM: ${error}`);
      return -1;
    }
  }

  // ===== 内存信息（使用hidebug Kit API）=====

  static getTotalRam(): number {
    try {
      const memInfo = hidebug.getSystemMemInfo();
      return Number(memInfo.totalMem);
    } catch (error) {
      Logger.error(`Failed to get total RAM: ${error}`);
      return -1;
    }
  }

  static getAvailableRam(): number {
    try {
      const memInfo = hidebug.getSystemMemInfo();
      return Number(memInfo.availableMem);
    } catch (error) {
      Logger.error(`Failed to get available RAM: ${error}`);
      return -1;
    }
  }

  static getRamUtilization(): number {
    const total = DeviceStatusUtil.getTotalRam();
    const available = DeviceStatusUtil.getAvailableRam();
    if (total === -1 || available === -1 || total === 0) {
      return -1;
    }
    return Math.round((1 - available / total) * 100);
  }

  // ===== CPU信息（使用hidebug Kit API）=====

  static getCpuUtilizationList(): number[] {
    try {
      const systemUsage = hidebug.getSystemCpuUsage();
      return [Math.round(systemUsage * 100)];
    } catch (error) {
      Logger.error(`Failed to get system CPU usage: ${error}`);
      return [-1];
    }
  }

  // ===== 温度等级（使用thermal Kit API）=====

  static getThermalLevel(): string {
    try {
      const level = thermal.getLevel();
      // 返回热等级名称（COOL/NORMAL/WARM/HOT等）
      const levelNames: Record<number, string> = {
        0: 'COOL',
        1: 'NORMAL',
        2: 'WARM',
        3: 'HOT',
        4: 'OVERHEATED',
        5: 'WARNING',
        6: 'EMERGENCY'
      };
      return levelNames[level] || 'UNKNOWN';
    } catch (error) {
      Logger.error(`Failed to get thermal level: ${error}`);
      return '--';
    }
  }

  // ===== 设备信息（使用deviceInfo Kit API）=====

  /**
   * 获取设备制造商
   */
  static getDeviceManufacture(): string {
    try {
      return deviceInfo.manufacture || 'Unknown';
    } catch (error) {
      Logger.error(`Failed to get device manufacture: ${error}`);
      return 'Unknown';
    }
  }

  /**
   * 获取设备市场名称（型号）
   */
  static getDeviceMarketName(): string {
    try {
      return deviceInfo.marketName || 'Unknown';
    } catch (error) {
      Logger.error(`Failed to get device market name: ${error}`);
      return 'Unknown';
    }
  }
}