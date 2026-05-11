import { CpuCore } from '../common/types';

/**
 * 设备状态信息数据模型
 */
export class DeviceStatusInfo {
  // CPU信息
  cpuUtilizationList: number[] = [];   // CPU利用率（百分比，总体值）
  thermalLevel: string = '--';         // 热等级（COOL/NORMAL/WARM/HOT等）

  // 电池信息
  batteryCapacity: number = 0;         // 电池电量（百分比）
  batteryTemp: number = 0;             // 电池温度（摄氏度）
  batteryCurrent: number = 0;          // 电池电流（mA）
  batteryVoltage: number = 0;          // 电池电压（mV）
  batteryPower: number = 0;            // 电池功率（W）

  // RAM内存信息
  totalRam: number = 0;                // 总内存（KB）
  availableRam: number = 0;            // 可用内存（KB）
  ramUtilization: number = 0;          // 内存利用率（百分比）

  // ROM存储信息
  totalRom: number = -1;               // 总存储空间（MB），-1表示不可用
  freeRom: number = -1;                // 可用存储空间（MB）

  /**
   * 获取总体CPU利用率（平均值）
   */
  getTotalCpuUtilization(): number {
    if (this.cpuUtilizationList.length === 0) {
      return -1;
    }
    const validValues = this.cpuUtilizationList.filter(v => v !== -1);
    if (validValues.length === 0) {
      return -1;
    }
    const sum = validValues.reduce((a, b) => a + b, 0);
    return Math.round(sum / validValues.length);
  }
}