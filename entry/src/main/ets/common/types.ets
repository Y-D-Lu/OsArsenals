/**
 * 类型定义文件
 */

/**
 * 坐标点
 */
export interface Point {
  x: number;
  y: number;
}

/**
 * 触摸事件
 */
export interface TouchEvent {
  type: 'down' | 'move' | 'up';
  x: number;
  y: number;
  pressure: number;
}

/**
 * 按键事件
 */
export interface KeyEvent {
  keyCode: number;
  isDown: boolean;
}

/**
 * CPU核心信息
 */
export interface CpuCore {
  id: number;
  freq: number;
  utilization: number;
  online: boolean;
}

/**
 * 电池信息
 */
export interface BatteryInfo {
  capacity: number;
  temperature: number;
  current: number;
  voltage: number;
  power: number;
}

/**
 * 多点触摸手指信息
 */
export interface FingerInfo {
  fingerId: number;
  x: number;
  y: number;
  pressure: number;
}