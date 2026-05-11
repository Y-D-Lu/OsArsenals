import { InputManager } from './InputManager';
import Logger from '../common/logger';

/**
 * 命令管理器
 * 解析和处理广播命令
 */
export class CommandManager {
  /**
   * 处理输入注入命令
   * @param params 命令参数
   */
  static handleInjectInput(params: Record<string, string>): void {
    const input = params['input'];
    if (!input) {
      Logger.warn('Missing input parameter');
      return;
    }

    try {
      Logger.info(`Handle inject input: ${input}`);

      // 解析输入命令格式："action,x1,y1,x2,y2,duration"
      const parts = input.split(',');
      const action = parts[0];

      switch (action) {
        case 'tap':
          // 点击："tap,x,y,duration"
          const x = parseInt(parts[1]) || 0;
          const y = parseInt(parts[2]) || 0;
          const duration = parseInt(parts[3]) || 0;
          // InputUtil.injectTap(x, y, duration); // 待实现
          Logger.info(`Inject tap: (${x}, ${y}), duration=${duration}`);
          break;

        case 'swipe':
          // 滑动："swipe,x1,y1,x2,y2,duration"
          const x1 = parseInt(parts[1]) || 0;
          const y1 = parseInt(parts[2]) || 0;
          const x2 = parseInt(parts[3]) || 0;
          const y2 = parseInt(parts[4]) || 0;
          const swipeDuration = parseInt(parts[5]) || 300;
          // InputUtil.injectSwipe(x1, y1, x2, y2, swipeDuration); // 待实现
          Logger.info(`Inject swipe: (${x1},${y1}) -> (${x2},${y2}), duration=${swipeDuration}`);
          break;

        case 'key':
          // 按键："key,keyCode,duration"
          const keyCode = parseInt(parts[1]) || 0;
          const keyDuration = parseInt(parts[2]) || 0;
          // InputUtil.injectKey(keyCode, keyDuration); // 待实现
          Logger.info(`Inject key: keyCode=${keyCode}, duration=${keyDuration}`);
          break;

        default:
          Logger.warn(`Unknown input action: ${action}`);
      }
    } catch (error) {
      Logger.error(`Failed to handle inject input: ${error}`);
    }
  }

  /**
   * 处理坐标点更新命令
   * @param params 命令参数
   */
  static async handleUpdatePointMap(params: Record<string, string>): Promise<void> {
    const key = params['key'];
    const value = params['value'];

    if (!key || !value) {
      Logger.warn('Missing key or value parameter');
      return;
    }

    try {
      Logger.info(`Handle update point map: key=${key}, value=${value}`);

      const coords = value.split(',');
      const x = parseInt(coords[0]) || 0;
      const y = parseInt(coords[1]) || 0;

      await InputManager.updatePointMap(key, x, y);
      Logger.info(`Point map updated: ${key} = (${x}, ${y})`);
    } catch (error) {
      Logger.error(`Failed to handle update point map: ${error}`);
    }
  }

  /**
   * 处理执行命令
   * @param params 命令参数
   */
  static async handleExecuteCommand(params: Record<string, string>): Promise<void> {
    const type = params['type'];
    const command = params['command'];

    if (!type || !command) {
      Logger.warn('Missing type or command parameter');
      return;
    }

    try {
      Logger.info(`Handle execute command: type=${type}, command=${command}`);

      switch (type) {
        case 'genshin':
          // 原神自动化命令
          // await GenshinImpactUtil.handleGenshinCommand(command); // 待实现
          Logger.info(`Execute genshin command: ${command}`);
          break;

        default:
          Logger.warn(`Unknown command type: ${type}`);
      }
    } catch (error) {
      Logger.error(`Failed to handle execute command: ${error}`);
    }
  }
}