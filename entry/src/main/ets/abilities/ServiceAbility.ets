import commonEventManager from '@ohos.commonEventManager';
import { CommandManager } from '../managers/CommandManager';
import Logger from '../common/logger';

/**
 * 后台服务Ability
 * 接收广播命令并执行相应操作
 */
export default class ServiceAbility {
  private subscriber: commonEventManager.CommonEventSubscriber | null = null;

  onCreate(): void {
    Logger.info('ServiceAbility onCreate');
    this.subscribeCommonEvents();
  }

  onDestroy(): void {
    Logger.info('ServiceAbility onDestroy');
    this.unsubscribeCommonEvents();
  }

  /**
   * 订阅广播事件
   */
  private async subscribeCommonEvents(): Promise<void> {
    const subscribeInfo: commonEventManager.CommonEventSubscribeInfo = {
      events: [
        'cn.arsenals.osarsenals.INJECT_INPUT',
        'cn.arsenals.osarsenals.UPDATE_POINT_MAP',
        'cn.arsenals.osarsenals.EXECUTE_COMMAND'
      ]
    };

    try {
      this.subscriber = await commonEventManager.createSubscriber(subscribeInfo);

      if (!this.subscriber) {
        Logger.error('Failed to create subscriber: subscriber is null');
        return;
      }

      commonEventManager.subscribe(this.subscriber, (err, data) => {
        if (err && err.code) {
          Logger.error(`Failed to receive common event: ${err.message}`);
          return;
        }

        if (!data) {
          Logger.error('Received event data is null');
          return;
        }

        this.handleCommonEvent(data);
      });

      Logger.info('Common events subscribed successfully');
    } catch (error) {
      Logger.error(`Failed to create subscriber: ${error}`);
    }
  }

  /**
   * 取消订阅广播事件
   */
  private unsubscribeCommonEvents(): void {
    if (this.subscriber) {
      try {
        commonEventManager.unsubscribe(this.subscriber, (err) => {
          if (err && err.code) {
            Logger.error(`Failed to unsubscribe: ${err.message}`);
          } else {
            Logger.info('Unsubscribed successfully');
          }
        });
        this.subscriber = null;
      } catch (error) {
        Logger.error(`Failed to unsubscribe: ${error}`);
      }
    }
  }

  /**
   * 处理广播事件
   */
  private handleCommonEvent(event: commonEventManager.CommonEventData): void {
    if (!event) {
      Logger.error('Event is null');
      return;
    }

    if (!event.event) {
      Logger.error('Event name is null');
      return;
    }

    Logger.info(`Received event: ${event.event}`);

    try {
      switch (event.event) {
        case 'cn.arsenals.osarsenals.INJECT_INPUT':
          CommandManager.handleInjectInput(event.parameters || {});
          break;

        case 'cn.arsenals.osarsenals.UPDATE_POINT_MAP':
          CommandManager.handleUpdatePointMap(event.parameters || {});
          break;

        case 'cn.arsenals.osarsenals.EXECUTE_COMMAND':
          CommandManager.handleExecuteCommand(event.parameters || {});
          break;

        default:
          Logger.warn(`Unknown event: ${event.event}`);
      }
    } catch (error) {
      Logger.error(`Failed to handle event ${event.event}: ${error}`);
    }
  }
}