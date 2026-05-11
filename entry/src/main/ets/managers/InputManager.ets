import preferences from '@ohos.data.preferences';
import { Point } from '../common/types';
import Logger from '../common/logger';

/**
 * 输入管理器
 * 负责坐标点映射的持久化存储和管理（完整移植版本）
 */
export class InputManager {
  private static instance: InputManager | null = null;
  private preferences: preferences.Preferences | null = null;

  /**
   * 获取单例实例
   */
  static getInstance(): InputManager {
    if (!InputManager.instance) {
      InputManager.instance = new InputManager();
    }
    return InputManager.instance;
  }

  /**
   * 初始化Preferences
   * @param context 应用上下文
   */
  async init(context: Context): Promise<void> {
    try {
      this.preferences = await preferences.getPreferences(context, 'InputManager');
      Logger.info('InputManager initialized');
    } catch (error) {
      Logger.error(`Failed to init InputManager: ${error}`);
    }
  }

  /**
   * 更新坐标点映射
   * @param key 坐标点键名
   * @param x X坐标
   * @param y Y坐标
   */
  static async updatePointMap(key: string, x: number, y: number): Promise<void> {
    const instance = InputManager.getInstance();
    if (!instance.preferences) {
      Logger.warn('InputManager not initialized');
      return;
    }

    try {
      await instance.preferences.put(key, `${x},${y}`);
      await instance.preferences.flush();
      Logger.info(`Point map updated: ${key} = (${x}, ${y})`);
    } catch (error) {
      Logger.error(`Failed to update point map: ${error}`);
    }
  }

  /**
   * 获取坐标点
   * @param key 坐标点键名
   * @returns 坐标点对象
   */
  static getPointFromMap(key: string): Point {
    const instance = InputManager.getInstance();
    if (!instance.preferences) {
      Logger.warn('InputManager not initialized, returning default point');
      return InputManager.getDefaultPoint(key);
    }

    try {
      const value = instance.preferences.getSync(key, InputManager.getDefaultPointString(key)) as string;
      const coords = value.split(',');
      const x = parseInt(coords[0]) || 0;
      const y = parseInt(coords[1]) || 0;
      return { x, y };
    } catch (error) {
      Logger.error(`Failed to get point from map: ${error}`);
      return InputManager.getDefaultPoint(key);
    }
  }

  /**
   * 获取默认坐标点字符串（完整移植，38个坐标点）
   * @param key 坐标点键名
   * @returns 默认坐标字符串
   */
  private static getDefaultPointString(key: string): string {
    // 原神完整坐标定义（基于Android Java版本，2400x1080分辨率）
    const defaultPoints: Record<string, string> = {
      // 系统通用坐标
      'POINT_LAST_ICON_ON_RECENT': '330,540',
      'POINT_MORE_INFO_ON_RECENT': '650,540',
      'POINT_FORCE_STOP_IN_SETTINGS': '875,1080',
      'POINT_SCREEN_CENTER': '1200,540',
      'POINT_CONFORM_FORCE_STOP_IN_SETTINGS': '825,1380',
      'GENSHIN_ON_PRIME_SCREEN': '120,1050',

      // 地图相关
      'GENSHIN_POINT_MAP': '300,157',
      'GENSHIN_POINT_MAP_SCALE_LEFT': '300,540',
      'GENSHIN_POINT_MAP_SCALE_RIGHT': '2100,540',
      'GENSHIN_POINT_MAP_SELECTOR': '2213,1002',
      'GENSHIN_POINT_MAP_SELECTOR_MONDSTADT': '1730,220',
      'GENSHIN_POINT_MAP_SELECTOR_LIYUE': '2080,220',
      'GENSHIN_POINT_MAP_SELECTOR_INAZUMA': '1730,360',
      'GENSHIN_POINT_MAP_TELEPORT': '2023,987',

      // 玩家操作相关
      'GENSHIN_POINT_PLAYER_OPERATE': '1451,537',
      'GENSHIN_POINT_PLAYER_WALK_AHEAD': '440,625',
      'GENSHIN_POINT_PLAYER_WALK_BACK': '440,1025',
      'GENSHIN_POINT_PLAYER_WALK_LEFT': '240,825',
      'GENSHIN_POINT_PLAYER_WALK_RIGHT': '641,825',
      'GENSHIN_POINT_PLAYER_JUMP': '2100,700',
      'GENSHIN_POINT_PLAYER_SPRINT': '2100,930',
      'GENSHIN_POINT_PLAYER_ATTACK': '1920,825',
      'GENSHIN_POINT_PLAYER_E': '1740,930',

      // 副本相关
      'GENSHIN_POINT_DOMAIN_LIST_SWIPE_START': '540,805',
      'GENSHIN_POINT_DOMAIN_FIGHT': '2070,1000',
      'GENSHIN_POINT_DOMAIN_SELECT_TEAM': '185,1005',
      'GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM': '540,375',
      'GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE': '500,1000',
      'GENSHIN_POINT_DOMAIN_SKIP_TIPS': '1200,240',
      'GENSHIN_POINT_DOMAIN_EXIT': '160,60',
      'GENSHIN_POINT_DOMAIN_EXIT_CONFORM': '1450,810',
      'GENSHIN_POINT_DOMAIN_EXIT_FINAL': '1200,975',
      'GENSHIN_POINT_DOMAIN_GET_BONUS': '1451,537',
      'GENSHIN_POINT_DOMAIN_USE_CONDENSED_RESIN': '950,810',
      'GENSHIN_POINT_DOMAIN_USE_ORIGINAL_RESIN': '1450,810',
      'GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS': '950,980',

      // 角色切换相关
      'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1': '2100,240',
      'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2': '2100,360',
      'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3': '2100,480'
    };

    return defaultPoints[key] || '0,0';
  }

  /**
   * 获取默认坐标点
   * @param key 坐标点键名
   * @returns 默认坐标点对象
   */
  private static getDefaultPoint(key: string): Point {
    const defaultString = InputManager.getDefaultPointString(key);
    const coords = defaultString.split(',');
    const x = parseInt(coords[0]) || 0;
    const y = parseInt(coords[1]) || 0;
    return { x, y };
  }

  /**
   * 获取所有坐标点映射
   * @returns 坐标点映射对象
   */
  static async getAllPointMaps(): Promise<Record<string, Point>> {
    const instance = InputManager.getInstance();
    if (!instance.preferences) {
      Logger.warn('InputManager not initialized, returning default points');
      return InputManager.getAllDefaultPoints();
    }

    try {
      // 原神所有预设坐标点键名（完整列表）
      const allKeys = [
        'POINT_LAST_ICON_ON_RECENT',
        'POINT_MORE_INFO_ON_RECENT',
        'POINT_FORCE_STOP_IN_SETTINGS',
        'POINT_SCREEN_CENTER',
        'POINT_CONFORM_FORCE_STOP_IN_SETTINGS',
        'GENSHIN_ON_PRIME_SCREEN',
        'GENSHIN_POINT_MAP',
        'GENSHIN_POINT_MAP_SCALE_LEFT',
        'GENSHIN_POINT_MAP_SCALE_RIGHT',
        'GENSHIN_POINT_MAP_SELECTOR',
        'GENSHIN_POINT_MAP_SELECTOR_MONDSTADT',
        'GENSHIN_POINT_MAP_SELECTOR_LIYUE',
        'GENSHIN_POINT_MAP_SELECTOR_INAZUMA',
        'GENSHIN_POINT_MAP_TELEPORT',
        'GENSHIN_POINT_PLAYER_OPERATE',
        'GENSHIN_POINT_PLAYER_WALK_AHEAD',
        'GENSHIN_POINT_PLAYER_WALK_BACK',
        'GENSHIN_POINT_PLAYER_WALK_LEFT',
        'GENSHIN_POINT_PLAYER_WALK_RIGHT',
        'GENSHIN_POINT_PLAYER_JUMP',
        'GENSHIN_POINT_PLAYER_SPRINT',
        'GENSHIN_POINT_PLAYER_ATTACK',
        'GENSHIN_POINT_PLAYER_E',
        'GENSHIN_POINT_DOMAIN_LIST_SWIPE_START',
        'GENSHIN_POINT_DOMAIN_FIGHT',
        'GENSHIN_POINT_DOMAIN_SELECT_TEAM',
        'GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM',
        'GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE',
        'GENSHIN_POINT_DOMAIN_SKIP_TIPS',
        'GENSHIN_POINT_DOMAIN_EXIT',
        'GENSHIN_POINT_DOMAIN_EXIT_CONFORM',
        'GENSHIN_POINT_DOMAIN_EXIT_FINAL',
        'GENSHIN_POINT_DOMAIN_GET_BONUS',
        'GENSHIN_POINT_DOMAIN_USE_CONDENSED_RESIN',
        'GENSHIN_POINT_DOMAIN_USE_ORIGINAL_RESIN',
        'GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS',
        'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1',
        'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2',
        'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3'
      ];

      const pointMap: Record<string, Point> = {};
      for (const key of allKeys) {
        pointMap[key] = InputManager.getPointFromMap(key);
      }

      Logger.info(`getAllPointMaps: ${allKeys.length} points`);
      return pointMap;
    } catch (error) {
      Logger.error(`Failed to get all point maps: ${error}`);
      return InputManager.getAllDefaultPoints();
    }
  }

  /**
   * 获取所有默认坐标点
   * @returns 默认坐标点映射
   */
  private static getAllDefaultPoints(): Record<string, Point> {
    const defaultPoints: Record<string, Point> = {
      'POINT_LAST_ICON_ON_RECENT': { x: 330, y: 540 },
      'POINT_MORE_INFO_ON_RECENT': { x: 650, y: 540 },
      'POINT_FORCE_STOP_IN_SETTINGS': { x: 875, y: 1080 },
      'POINT_SCREEN_CENTER': { x: 1200, y: 540 },
      'POINT_CONFORM_FORCE_STOP_IN_SETTINGS': { x: 825, y: 1380 },
      'GENSHIN_ON_PRIME_SCREEN': { x: 120, y: 1050 },
      'GENSHIN_POINT_MAP': { x: 300, y: 157 },
      'GENSHIN_POINT_MAP_SCALE_LEFT': { x: 300, y: 540 },
      'GENSHIN_POINT_MAP_SCALE_RIGHT': { x: 2100, y: 540 },
      'GENSHIN_POINT_MAP_SELECTOR': { x: 2213, y: 1002 },
      'GENSHIN_POINT_MAP_SELECTOR_MONDSTADT': { x: 1730, y: 220 },
      'GENSHIN_POINT_MAP_SELECTOR_LIYUE': { x: 2080, y: 220 },
      'GENSHIN_POINT_MAP_SELECTOR_INAZUMA': { x: 1730, y: 360 },
      'GENSHIN_POINT_MAP_TELEPORT': { x: 2023, y: 987 },
      'GENSHIN_POINT_PLAYER_OPERATE': { x: 1451, y: 537 },
      'GENSHIN_POINT_PLAYER_WALK_AHEAD': { x: 440, y: 625 },
      'GENSHIN_POINT_PLAYER_WALK_BACK': { x: 440, y: 1025 },
      'GENSHIN_POINT_PLAYER_WALK_LEFT': { x: 240, y: 825 },
      'GENSHIN_POINT_PLAYER_WALK_RIGHT': { x: 641, y: 825 },
      'GENSHIN_POINT_PLAYER_JUMP': { x: 2100, y: 700 },
      'GENSHIN_POINT_PLAYER_SPRINT': { x: 2100, y: 930 },
      'GENSHIN_POINT_PLAYER_ATTACK': { x: 1920, y: 825 },
      'GENSHIN_POINT_PLAYER_E': { x: 1740, y: 930 },
      'GENSHIN_POINT_DOMAIN_LIST_SWIPE_START': { x: 540, y: 805 },
      'GENSHIN_POINT_DOMAIN_FIGHT': { x: 2070, y: 1000 },
      'GENSHIN_POINT_DOMAIN_SELECT_TEAM': { x: 185, y: 1005 },
      'GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM': { x: 540, y: 375 },
      'GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE': { x: 500, y: 1000 },
      'GENSHIN_POINT_DOMAIN_SKIP_TIPS': { x: 1200, y: 240 },
      'GENSHIN_POINT_DOMAIN_EXIT': { x: 160, y: 60 },
      'GENSHIN_POINT_DOMAIN_EXIT_CONFORM': { x: 1450, y: 810 },
      'GENSHIN_POINT_DOMAIN_EXIT_FINAL': { x: 1200, y: 975 },
      'GENSHIN_POINT_DOMAIN_GET_BONUS': { x: 1451, y: 537 },
      'GENSHIN_POINT_DOMAIN_USE_CONDENSED_RESIN': { x: 950, y: 810 },
      'GENSHIN_POINT_DOMAIN_USE_ORIGINAL_RESIN': { x: 1450, y: 810 },
      'GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS': { x: 950, y: 980 },
      'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1': { x: 2100, y: 240 },
      'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2': { x: 2100, y: 360 },
      'GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3': { x: 2100, y: 480 }
    };
    return defaultPoints;
  }
}