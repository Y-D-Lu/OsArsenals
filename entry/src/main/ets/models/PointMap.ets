import { Point } from '../common/types';

/**
 * 坐标点映射数据模型
 */
export class PointMap {
  key: string = '';
  point: Point = { x: 0, y: 0 };

  constructor(key: string, x: number, y: number) {
    this.key = key;
    this.point = { x, y };
  }

  /**
   * 从字符串解析坐标点
   * 格式："x,y"
   */
  static fromString(key: string, value: string): PointMap {
    const parts = value.split(',');
    const x = parseInt(parts[0]) || 0;
    const y = parseInt(parts[1]) || 0;
    return new PointMap(key, x, y);
  }

  /**
   * 转换为字符串格式
   */
  toString(): string {
    return `${this.point.x},${this.point.y}`;
  }
}