/*
 * HarmonyOS Hidden API Type Definition Patch
 * @ohos.multimodalInput.inputEventClient
 *
 * 此模块为系统Hidden API，运行时存在但SDK未公开声明。
 * 需要将此文件复制到SDK目录才能通过编译：
 * SDK路径: <SDK>/default/openharmony/ets/api/@ohos.multimodalInput.inputEventClient.d.ts
 */
import type { TouchEvent } from './@ohos.multimodalInput.touchEvent';

declare namespace inputEventClient {
  /**
   * KeyEvent for inputEventClient (不同于 @ohos.multimodalInput.keyEvent 的 KeyEvent)
   */
  interface KeyEvent {
    isPressed: boolean;
    keyCode: number;
    keyDownDuration: number;
    isIntercepted: boolean;
  }

  /**
   * 注入触摸事件
   * @param { object } event - 包含 touchEvent 属性的对象
   * @syscap SystemCapability.MultimodalInput.Input.Core
   * @permission ohos.permission.INJECT_INPUT_EVENT
   */
  function injectTouchEvent(event: { touchEvent: TouchEvent }): void;

  /**
   * 注入按键事件
   * @param { object } event - 包含 keyEvent 属性的对象
   * @syscap SystemCapability.MultimodalInput.Input.Core
   * @permission ohos.permission.INJECT_INPUT_EVENT
   */
  function injectKeyEvent(event: { keyEvent: KeyEvent }): void;
}

export default inputEventClient;