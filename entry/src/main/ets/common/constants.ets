/**
 * 常量定义
 */
export class Constants {
  // 日志相关
  static readonly LOG_DOMAIN = 0x0000;
  static readonly LOG_TAG = 'OsArsenals';

  // 无效值标识
  static readonly INVALID_VALUE = -1;

  // 定时器间隔（毫秒）
  static readonly MONITOR_INTERVAL = 1000;  // 设备监控刷新间隔

  // 悬浮窗尺寸（px）- window.resize使用px单位
  static readonly FLOAT_WINDOW_WIDTH = 500;
  static readonly FLOAT_WINDOW_HEIGHT = 300;

  // 防烧屏位移间隔（毫秒）
  static readonly ANTI_BURN_IN_INTERVAL = 300000;  // 5分钟

  // 原神坐标点键名
  static readonly GENSHIN_POINT_MAP = 'GENSHIN_POINT_MAP';
  static readonly GENSHIN_POINT_PLAYER_E = 'GENSHIN_POINT_PLAYER_E';
  static readonly GENSHIN_POINT_PLAYER_Q = 'GENSHIN_POINT_PLAYER_Q';
  static readonly GENSHIN_POINT_ATTACK = 'GENSHIN_POINT_ATTACK';
  static readonly GENSHIN_POINT_JUMP = 'GENSHIN_POINT_JUMP';
  static readonly GENSHIN_POINT_SWITCH = 'GENSHIN_POINT_SWITCH';
}