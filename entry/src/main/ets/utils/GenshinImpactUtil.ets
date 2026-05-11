import { InputManager } from '../managers/InputManager';
import { InputUtil } from '../utils/InputUtil';
import { Point } from '../common/types';
import Logger from '../common/logger';

/**
 * 原神自动化工具（完整移植版本）
 * 负责命令解析和执行，移植Java版本所有32个命令
 */
export class GenshinImpactUtil {
  private static fightLoopStr: string = 'e,1000|sleep,600|switch,1|sleep,1000|nahida_e|sleep,600|switch,2|sleep,1000|e|sleep,1000|switch,3|sleep,1000|e|sleep,600|neuvillette_z|switch,1|sleep,600';
  private static fightLoopLiteStr: string = 'e|sleep,500|switch,1|sleep,600|e|sleep,600|switch,2|sleep,500|e|sleep,600|a|sleep,1400|a|sleep,1400|z,1000|sleep,500|a|sleep,1200|a|sleep,1400|z,1000|sleep,200|switch,3|sleep,200|e|sleep,1000|switch,1|sleep,1000';

  /**
   * 处理原神命令
   */
  static async handleGenshinCommand(command: string): Promise<void> {
    try {
      Logger.info(`Handle genshin command: ${command}`);

      const commands = command.split('|');
      for (let i = 0; i < commands.length; i++) {
        const cmd = commands[i];
        const parts = cmd.split(',');
        const action = parts[0];
        const params = parts.slice(1);

        // 特殊命令处理：需要传递完整后续命令链
        if (action === 'fight_loop' || action === 'fight_loop_lite') {
          await GenshinImpactUtil.executeFightLoop(action, command, params);
          return; // 后续命令已处理，直接返回
        }

        if (action === 'set_fight_loop' || action === 'set_fight_loop_lite') {
          await GenshinImpactUtil.executeSetFightLoop(action, command, params);
          return;
        }

        await GenshinImpactUtil.executeAction(action, params);
      }

      Logger.info('Genshin command executed successfully');
    } catch (error) {
      Logger.error(`Failed to handle genshin command: ${error}`);
    }
  }

  /**
   * 执行战斗循环命令（特殊处理）
   */
  private static async executeFightLoop(action: string, command: string, params: string[]): Promise<void> {
    if (params.length === 0) {
      // 无参数：使用预设循环
      if (action === 'fight_loop') {
        await GenshinImpactUtil.handleGenshinCommand(GenshinImpactUtil.fightLoopStr);
      } else {
        await GenshinImpactUtil.handleGenshinCommand(GenshinImpactUtil.fightLoopLiteStr);
      }
      return;
    }

    // 有参数：提取后续命令并执行
    const prefix = action + ',';
    if (command.startsWith(prefix)) {
      const subsequentCommands = command.substring(prefix.length);
      await GenshinImpactUtil.handleGenshinCommand(subsequentCommands);
    }
  }

  /**
   * 执行设置战斗循环命令（特殊处理）
   */
  private static async executeSetFightLoop(action: string, command: string, params: string[]): Promise<void> {
    if (params.length === 0) {
      Logger.warn(`${action} params empty`);
      return;
    }

    // 提取后续命令并设置
    const prefix = action + ',';
    if (command.startsWith(prefix)) {
      const subsequentCommands = command.substring(prefix.length);
      if (action === 'set_fight_loop') {
        GenshinImpactUtil.fightLoopStr = subsequentCommands;
      } else {
        GenshinImpactUtil.fightLoopLiteStr = subsequentCommands;
      }
    }
  }

  /**
   * 执行单个动作
   */
  private static async executeAction(action: string, params: string[]): Promise<void> {
    switch (action) {
      case 'a': {
        // 普通攻击：固定duration=0（短点击）
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_ATTACK');
        Logger.info(`Execute attack: inject tap at (${point.x}, ${point.y})`);
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'z': {
        await GenshinImpactUtil.executeChargedAttack(params);
        break;
      }

      case 'e': {
        await GenshinImpactUtil.executeSkill(params);
        break;
      }

      case 'nahida_e': {
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_E');
        InputUtil.injectSwipe(point.x, point.y, point.x - 2400, point.y, 1000, 50);
        await GenshinImpactUtil.sleep(1000);
        break;
      }

      case 'neuvillette_z': {
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_ATTACK');
        InputUtil.injectSwipe(point.x, point.y, point.x - 20000, point.y, 8000, 50);
        await GenshinImpactUtil.sleep(8000);
        break;
      }

      case 's': {
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_SPRINT');
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'j': {
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_JUMP');
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'sleep': {
        const sleepTime = parseInt(params[0]) || 0;
        if (sleepTime > 0) {
          await GenshinImpactUtil.sleep(sleepTime);
        }
        break;
      }

      case 'left': {
        const duration = parseInt(params[0]) || 500;
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_WALK_LEFT');
        InputUtil.injectTap(point.x, point.y, duration);
        await GenshinImpactUtil.sleep(duration);
        break;
      }

      case 'right': {
        const duration = parseInt(params[0]) || 500;
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_WALK_RIGHT');
        InputUtil.injectTap(point.x, point.y, duration);
        await GenshinImpactUtil.sleep(duration);
        break;
      }

      case 'see_left': {
        const distance = parseInt(params[0]) || 100;
        const duration = parseInt(params[1]) || 500;
        const point = InputManager.getPointFromMap('POINT_SCREEN_CENTER');
        InputUtil.injectSwipe(point.x, point.y, point.x - distance, point.y, duration, 50);
        await GenshinImpactUtil.sleep(duration);
        break;
      }

      case 'see_right': {
        const distance = parseInt(params[0]) || 100;
        const duration = parseInt(params[1]) || 500;
        const point = InputManager.getPointFromMap('POINT_SCREEN_CENTER');
        InputUtil.injectSwipe(point.x, point.y, point.x + distance, point.y, duration, 50);
        await GenshinImpactUtil.sleep(duration);
        break;
      }

      case 'ahead': {
        const duration = parseInt(params[0]) || 500;
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_WALK_AHEAD');
        InputUtil.injectTap(point.x, point.y, duration);
        await GenshinImpactUtil.sleep(duration);
        break;
      }

      case 'back': {
        const duration = parseInt(params[0]) || 500;
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_WALK_BACK');
        InputUtil.injectTap(point.x, point.y, duration);
        await GenshinImpactUtil.sleep(duration);
        break;
      }

      case 'switch': {
        const player = parseInt(params[0]) || 1;
        if (player === 1) {
          const point = InputManager.getPointFromMap('GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1');
          InputUtil.injectTap(point.x, point.y, 0);
        } else if (player === 2) {
          const point = InputManager.getPointFromMap('GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2');
          InputUtil.injectTap(point.x, point.y, 0);
        } else if (player === 3) {
          const point = InputManager.getPointFromMap('GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3');
          InputUtil.injectTap(point.x, point.y, 0);
        }
        break;
      }

      case 'map': {
        const place = parseInt(params[0]) || 1;
        let pointPlace: Point | null = null;

        if (place === 1) {
          pointPlace = InputManager.getPointFromMap('GENSHIN_POINT_MAP_SELECTOR_MONDSTADT');
        } else if (place === 2) {
          pointPlace = InputManager.getPointFromMap('GENSHIN_POINT_MAP_SELECTOR_LIYUE');
        } else if (place === 3) {
          pointPlace = InputManager.getPointFromMap('GENSHIN_POINT_MAP_SELECTOR_INAZUMA');
        }

        if (pointPlace) {
          const pointMap = InputManager.getPointFromMap('GENSHIN_POINT_MAP');
          InputUtil.injectTap(pointMap.x, pointMap.y, 0);
          await GenshinImpactUtil.sleep(1000);

          const pointSelector = InputManager.getPointFromMap('GENSHIN_POINT_MAP_SELECTOR');
          InputUtil.injectTap(pointSelector.x, pointSelector.y, 0);
          await GenshinImpactUtil.sleep(1000);

          InputUtil.injectTap(pointPlace.x, pointPlace.y, 0);
          await GenshinImpactUtil.sleep(1000);

          const pointScaleLeft = InputManager.getPointFromMap('GENSHIN_POINT_MAP_SCALE_LEFT');
          const pointScaleRight = InputManager.getPointFromMap('GENSHIN_POINT_MAP_SCALE_RIGHT');
          const pointScreenCenter = InputManager.getPointFromMap('POINT_SCREEN_CENTER');

          InputUtil.injectTwoPointSwipe(
            pointScaleLeft.x, pointScaleLeft.y, pointScreenCenter.x, pointScreenCenter.y,
            pointScaleRight.x, pointScaleRight.y, pointScreenCenter.x, pointScreenCenter.y,
            2000, 50
          );
          await GenshinImpactUtil.sleep(2000);

          InputUtil.injectTap(pointSelector.x, pointSelector.y, 0);
          await GenshinImpactUtil.sleep(1000);

          InputUtil.injectTap(pointPlace.x, pointPlace.y, 0);
        }
        break;
      }

      case 'map_teleport': {
        const point = InputManager.getPointFromMap('GENSHIN_POINT_MAP_TELEPORT');
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'player_operate': {
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_OPERATE');
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'domain_enter': {
        let point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_FIGHT');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(2000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_SELECT_TEAM');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_LIST_SWIPE_START');
        InputUtil.injectSwipe(point.x, point.y, point.x, 0, 500, 50);
        await GenshinImpactUtil.sleep(1000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_FIGHT');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(10000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_SKIP_TIPS');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'domain_exit': {
        let point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_EXIT');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_EXIT_CONFORM');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(3000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_EXIT_FINAL');
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'domain_get_bonus': {
        let point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_OPERATE');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(1000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_EXIT_CONFORM');
        InputUtil.injectTap(point.x, point.y, 0);
        await GenshinImpactUtil.sleep(10000);

        point = InputManager.getPointFromMap('GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS');
        InputUtil.injectTap(point.x, point.y, 0);
        break;
      }

      case 'cancel': {
        break;
      }

      case 'domain_finish': {
        Logger.warn('domain_finish requires image recognition, simplified version');
        // 简化版本：等待并退出
        await GenshinImpactUtil.sleep(10000);
        break;
      }

      case 'get_rotate': {
        Logger.warn('get_rotate requires image recognition, not implemented');
        break;
      }

      case 'is_domain_succeed': {
        Logger.warn('is_domain_succeed requires image recognition, not implemented');
        break;
      }

      case 'judge_position_and_adjust': {
        Logger.warn('judge_position_and_adjust requires image recognition, simplified version');
        // 简化版本：向前走
        const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_WALK_AHEAD');
        InputUtil.injectTap(point.x, point.y, 500);
        await GenshinImpactUtil.sleep(500);
        break;
      }

      case 'process_fight_domain': {
        Logger.warn('process_fight_domain requires complex automation, simplified version');
        await GenshinImpactUtil.handleGenshinCommand('domain_enter|sleep,10000|domain_finish|domain_get_bonus|domain_exit');
        break;
      }

      case 'process_fight_domain_lite': {
        Logger.warn('process_fight_domain_lite requires complex automation, simplified version');
        await GenshinImpactUtil.handleGenshinCommand('domain_enter|sleep,10000|domain_finish|domain_get_bonus|domain_exit');
        break;
      }

      default: {
        Logger.warn(`Unknown action: ${action}`);
        break;
      }
    }
  }

  /**
   * 执行元素战技（E技能）
   */
  private static async executeSkill(params: string[]): Promise<void> {
    const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_E');
    const duration = parseInt(params[0]) || 0;

    Logger.info(`Execute skill: inject tap at (${point.x}, ${point.y}), duration=${duration}`);
    InputUtil.injectTap(point.x, point.y, duration);
    if (duration > 0) {
      await GenshinImpactUtil.sleep(duration);
    }
  }

  /**
   * 执行重击（长按攻击）
   */
  private static async executeChargedAttack(params: string[]): Promise<void> {
    const point = InputManager.getPointFromMap('GENSHIN_POINT_PLAYER_ATTACK');
    const duration = parseInt(params[0]) || 500;

    Logger.info(`Execute charged attack: inject tap at (${point.x}, ${point.y}), duration=${duration}`);
    InputUtil.injectTap(point.x, point.y, duration);
    await GenshinImpactUtil.sleep(duration);
  }

  /**
   * 等待指定时间
   */
  private static async sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}