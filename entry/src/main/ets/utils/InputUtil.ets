import inputEventClient from '@ohos.multimodalInput.inputEventClient';
import { Action, Touch, TouchEvent, SourceType, ToolType } from '@ohos.multimodalInput.touchEvent';
import Logger from '../common/logger';

/**
 * 输入注入工具（真实实现）
 * 使用multimodalInput.inputEventClient Kit API实现输入注入
 * 需要 ohos.permission.INJECT_INPUT_EVENT 权限（已配置）
 */
export class InputUtil {
  /**
   * 注入点击事件
   * @param x X坐标
   * @param y Y坐标
   * @param duration 按压时长（毫秒），0表示短点击
   */
  static injectTap(x: number, y: number, duration: number = 0): void {
    try {
      Logger.info(`Inject tap: (${x}, ${y}), duration=${duration}ms`);

      const pressedTime = Date.now();

      // 构建触摸点
      const touch: Touch = {
        id: 0,
        pressedTime: pressedTime,
        screenX: x,
        screenY: y,
        windowX: 0,
        windowY: 0,
        pressure: 1.0,
        width: 0,
        height: 0,
        tiltX: 0,
        tiltY: 0,
        toolX: 0,
        toolY: 0,
        toolWidth: 0,
        toolHeight: 0,
        rawX: 0,
        rawY: 0,
        toolType: ToolType.FINGER
      };

      // DOWN事件
      const downEvent: TouchEvent = {
        action: Action.DOWN,
        touch: touch,
        touches: [touch],
        sourceType: SourceType.TOUCH_SCREEN,
        id: 0,
        deviceId: 0,
        actionTime: pressedTime,
        screenId: 0,
        windowId: 0
      };

      inputEventClient.injectTouchEvent({ touchEvent: downEvent });

      if (duration > 0) {
        // 长按：等待指定时长
        setTimeout(() => {
          const upEvent: TouchEvent = {
            action: Action.UP,
            touch: touch,
            touches: [touch],
            sourceType: SourceType.TOUCH_SCREEN,
            id: 0,
            deviceId: 0,
            actionTime: Date.now(),
            screenId: 0,
            windowId: 0
          };
          inputEventClient.injectTouchEvent({ touchEvent: upEvent });
          Logger.info('Tap event injected successfully (long press)');
        }, duration);
      } else {
        // 短点击：立即UP
        const upEvent: TouchEvent = {
          action: Action.UP,
          touch: touch,
          touches: [touch],
          sourceType: SourceType.TOUCH_SCREEN,
          id: 0,
          deviceId: 0,
          actionTime: pressedTime + 50,
          screenId: 0,
          windowId: 0
        };
        inputEventClient.injectTouchEvent({ touchEvent: upEvent });
        Logger.info('Tap event injected successfully (short tap)');
      }
    } catch (error) {
      Logger.error(`Failed to inject tap: ${error}`);
    }
  }

  /**
   * 注入滑动事件
   * @param x1 起点X坐标
   * @param y1 起点Y坐标
   * @param x2 终点X坐标
   * @param y2 终点Y坐标
   * @param duration 滑动时长（毫秒）
   * @param frequency 频率（Hz，默认50）
   */
  static injectSwipe(x1: number, y1: number, x2: number, y2: number, duration: number = 300, frequency: number = 50): void {
    try {
      Logger.info(`Inject swipe: (${x1},${y1}) -> (${x2},${y2}), duration=${duration}ms, frequency=${frequency}Hz`);

      const startTime = Date.now();
      const interval = frequency > 1000 ? 1 : Math.floor(1000 / frequency);
      const steps = Math.floor(duration / interval);

      // DOWN事件
      const startTouch: Touch = {
        id: 0,
        pressedTime: startTime,
        screenX: x1,
        screenY: y1,
        windowX: 0,
        windowY: 0,
        pressure: 1.0,
        width: 0,
        height: 0,
        tiltX: 0,
        tiltY: 0,
        toolX: 0,
        toolY: 0,
        toolWidth: 0,
        toolHeight: 0,
        rawX: 0,
        rawY: 0,
        toolType: ToolType.FINGER
      };

      const downEvent: TouchEvent = {
        action: Action.DOWN,
        touch: startTouch,
        touches: [startTouch],
        sourceType: SourceType.TOUCH_SCREEN,
        id: 0,
        deviceId: 0,
        actionTime: startTime,
        screenId: 0,
        windowId: 0
      };

      inputEventClient.injectTouchEvent({ touchEvent: downEvent });

      // MOVE事件序列
      for (let i = 1; i <= steps; i++) {
        setTimeout(() => {
          const ratio = i / steps;
          const x = Math.round(x1 + (x2 - x1) * ratio);
          const y = Math.round(y1 + (y2 - y1) * ratio);

          const moveTouch: Touch = {
            id: 0,
            pressedTime: startTime,
            screenX: x,
            screenY: y,
            windowX: 0,
            windowY: 0,
            pressure: 1.0,
            width: 0,
            height: 0,
            tiltX: 0,
            tiltY: 0,
            toolX: 0,
            toolY: 0,
            toolWidth: 0,
            toolHeight: 0,
            rawX: 0,
            rawY: 0,
            toolType: ToolType.FINGER
          };

          const moveEvent: TouchEvent = {
            action: Action.MOVE,
            touch: moveTouch,
            touches: [moveTouch],
            sourceType: SourceType.TOUCH_SCREEN,
            id: 0,
            deviceId: 0,
            actionTime: startTime + i * interval,
            screenId: 0,
            windowId: 0
          };

          inputEventClient.injectTouchEvent({ touchEvent: moveEvent });
        }, i * interval);
      }

      // UP事件
      setTimeout(() => {
        const endTouch: Touch = {
          id: 0,
          pressedTime: startTime,
          screenX: x2,
          screenY: y2,
          windowX: x2,
          windowY: y2,
          pressure: 0.0,
          width: 1,
          height: 1,
          tiltX: 0,
          tiltY: 0,
          toolX: x2,
          toolY: y2,
          toolWidth: 1,
          toolHeight: 1,
          rawX: x2,
          rawY: y2,
          toolType: ToolType.FINGER
        };

        const upEvent: TouchEvent = {
          action: Action.UP,
          touch: endTouch,
          touches: [endTouch],
          sourceType: SourceType.TOUCH_SCREEN,
          id: 0,
          deviceId: 0,
          actionTime: startTime + duration,
          screenId: 0,
          windowId: 0
        };

        inputEventClient.injectTouchEvent({ touchEvent: upEvent });
        Logger.info('Swipe event injected successfully');
      }, duration);
    } catch (error) {
      Logger.error(`Failed to inject swipe: ${error}`);
    }
  }

  /**
   * 注入按键事件
   * @param keyCode 按键代码
   * @param duration 按压时长（毫秒），0表示短按
   */
  static injectKey(keyCode: number, duration: number = 0): void {
    try {
      Logger.info(`Inject key: keyCode=${keyCode}, duration=${duration}ms`);

      // KeyEvent结构（根据SDK定义）
      const keyEvent: inputEventClient.KeyEvent = {
        isPressed: true,
        keyCode: keyCode,
        keyDownDuration: duration * 1000, // 转换为微秒
        isIntercepted: false
      };

      inputEventClient.injectKeyEvent({ keyEvent: keyEvent });
      Logger.info('Key event injected successfully');
    } catch (error) {
      Logger.error(`Failed to inject key: ${error}`);
    }
  }

  /**
   * 注入双指缩放事件（简化版本）
   * @param centerX 中心点X坐标
   * @param centerY 中心点Y坐标
   * @param startDistance 起始手指间距
   * @param endDistance 结束手指间距
   * @param duration 缩放时长（毫秒）
   */
  static injectPinch(centerX: number, centerY: number, startDistance: number, endDistance: number, duration: number = 300): void {
    try {
      Logger.info(`Inject pinch: center(${centerX},${centerY}), distance ${startDistance} -> ${endDistance}, duration=${duration}ms`);

      // 双指起始位置
      const finger1StartX = centerX - startDistance / 2;
      const finger1StartY = centerY;
      const finger2StartX = centerX + startDistance / 2;
      const finger2StartY = centerY;

      // 双指结束位置
      const finger1EndX = centerX - endDistance / 2;
      const finger1EndY = centerY;
      const finger2EndX = centerX + endDistance / 2;
      const finger2EndY = centerY;

      const startTime = Date.now();
      const steps = Math.floor(duration / 16);

      // DOWN事件 - 双指同时按下
      const touch1: Touch = {
        id: 0,
        pressedTime: startTime,
        screenX: finger1StartX,
        screenY: finger1StartY,
        windowX: 0,
        windowY: 0,
        pressure: 1.0,
        width: 0,
        height: 0,
        tiltX: 0,
        tiltY: 0,
        toolX: 0,
        toolY: 0,
        toolWidth: 0,
        toolHeight: 0,
        rawX: 0,
        rawY: 0,
        toolType: ToolType.FINGER
      };

      const touch2: Touch = {
        id: 1,
        pressedTime: startTime,
        screenX: finger2StartX,
        screenY: finger2StartY,
        windowX: 0,
        windowY: 0,
        pressure: 1.0,
        width: 0,
        height: 0,
        tiltX: 0,
        tiltY: 0,
        toolX: 0,
        toolY: 0,
        toolWidth: 0,
        toolHeight: 0,
        rawX: 0,
        rawY: 0,
        toolType: ToolType.FINGER
      };

      const downEvent: TouchEvent = {
        action: Action.DOWN,
        touch: touch1,
        touches: [touch1, touch2],
        sourceType: SourceType.TOUCH_SCREEN,
        id: 0,
        deviceId: 0,
        actionTime: startTime,
        screenId: 0,
        windowId: 0
      };

      inputEventClient.injectTouchEvent({ touchEvent: downEvent });

      // MOVE事件序列 - 双指同时移动
      for (let i = 1; i <= steps; i++) {
        setTimeout(() => {
          const ratio = i / steps;
          const finger1X = Math.round(finger1StartX + (finger1EndX - finger1StartX) * ratio);
          const finger1Y = Math.round(finger1StartY + (finger1EndY - finger1StartY) * ratio);
          const finger2X = Math.round(finger2StartX + (finger2EndX - finger2StartX) * ratio);
          const finger2Y = Math.round(finger2StartY + (finger2EndY - finger2StartY) * ratio);

          const moveTouch1: Touch = {
            id: 0,
            pressedTime: startTime,
            screenX: finger1X,
            screenY: finger1Y,
            windowX: 0,
            windowY: 0,
            pressure: 1.0,
            width: 0,
            height: 0,
            tiltX: 0,
            tiltY: 0,
            toolX: 0,
            toolY: 0,
            toolWidth: 0,
            toolHeight: 0,
            rawX: 0,
            rawY: 0,
            toolType: ToolType.FINGER
          };

          const moveTouch2: Touch = {
            id: 1,
            pressedTime: startTime,
            screenX: finger2X,
            screenY: finger2Y,
            windowX: 0,
            windowY: 0,
            pressure: 1.0,
            width: 0,
            height: 0,
            tiltX: 0,
            tiltY: 0,
            toolX: 0,
            toolY: 0,
            toolWidth: 0,
            toolHeight: 0,
            rawX: 0,
            rawY: 0,
            toolType: ToolType.FINGER
          };

          const moveEvent: TouchEvent = {
            action: Action.MOVE,
            touch: moveTouch1,
            touches: [moveTouch1, moveTouch2],
            sourceType: SourceType.TOUCH_SCREEN,
            id: 0,
            deviceId: 0,
            actionTime: startTime + i * 16,
            screenId: 0,
            windowId: 0
          };

          inputEventClient.injectTouchEvent({ touchEvent: moveEvent });
        }, i * 16);
      }

      // UP事件 - 双指同时抬起
      setTimeout(() => {
        const upTouch1: Touch = {
          id: 0,
          pressedTime: startTime,
          screenX: finger1EndX,
          screenY: finger1EndY,
          windowX: 0,
          windowY: 0,
          pressure: 0.0,
          width: 0,
          height: 0,
          tiltX: 0,
          tiltY: 0,
          toolX: 0,
          toolY: 0,
          toolWidth: 0,
          toolHeight: 0,
          rawX: 0,
          rawY: 0,
          toolType: ToolType.FINGER
        };

        const upTouch2: Touch = {
          id: 1,
          pressedTime: startTime,
          screenX: finger2EndX,
          screenY: finger2EndY,
          windowX: 0,
          windowY: 0,
          pressure: 0.0,
          width: 0,
          height: 0,
          tiltX: 0,
          tiltY: 0,
          toolX: 0,
          toolY: 0,
          toolWidth: 0,
          toolHeight: 0,
          rawX: 0,
          rawY: 0,
          toolType: ToolType.FINGER
        };

        const upEvent: TouchEvent = {
          action: Action.UP,
          touch: upTouch1,
          touches: [upTouch1, upTouch2],
          sourceType: SourceType.TOUCH_SCREEN,
          id: 0,
          deviceId: 0,
          actionTime: startTime + duration,
          screenId: 0,
          windowId: 0
        };

        inputEventClient.injectTouchEvent({ touchEvent: upEvent });
        Logger.info('Pinch event injected successfully');
      }, duration);
    } catch (error) {
      Logger.error(`Failed to inject pinch: ${error}`);
    }
  }

  /**
   * 注入双指滑动事件（地图缩放等场景）
   * @param firstStartX 第一个手指起点X
   * @param firstStartY 第一个手指起点Y
   * @param firstEndX 第一个手指终点X
   * @param firstEndY 第一个手指终点Y
   * @param secondStartX 第二个手指起点X
   * @param secondStartY 第二个手指起点Y
   * @param secondEndX 第二个手指终点X
   * @param secondEndY 第二个手指终点Y
   * @param duration 滑动时长（毫秒）
   * @param frequency 频率（Hz）
   */
  static injectTwoPointSwipe(
    firstStartX: number, firstStartY: number, firstEndX: number, firstEndY: number,
    secondStartX: number, secondStartY: number, secondEndX: number, secondEndY: number,
    duration: number = 1000, frequency: number = 50
  ): void {
    try {
      Logger.info(`Inject two point swipe: first(${firstStartX},${firstStartY})->(${firstEndX},${firstEndY}), second(${secondStartX},${secondStartY})->(${secondEndX},${secondEndY}), duration=${duration}ms`);

      const startTime = Date.now();
      const interval = frequency > 1000 ? 1 : Math.floor(1000 / frequency);
      const steps = Math.floor(duration / interval);

      // DOWN事件 - 第一个手指按下
      const touch1Down: Touch = {
        id: 0,
        pressedTime: startTime,
        screenX: firstStartX,
        screenY: firstStartY,
        windowX: 0,
        windowY: 0,
        pressure: 1.0,
        width: 0,
        height: 0,
        tiltX: 0,
        tiltY: 0,
        toolX: 0,
        toolY: 0,
        toolWidth: 0,
        toolHeight: 0,
        rawX: 0,
        rawY: 0,
        toolType: ToolType.FINGER
      };

      const downEvent1: TouchEvent = {
        action: Action.DOWN,
        touch: touch1Down,
        touches: [touch1Down],
        sourceType: SourceType.TOUCH_SCREEN,
        id: 0,
        deviceId: 0,
        actionTime: startTime,
        screenId: 0,
        windowId: 0
      };

      inputEventClient.injectTouchEvent({ touchEvent: downEvent1 });

      // POINTER_DOWN事件 - 第二个手指按下
      const touch2Down: Touch = {
        id: 1,
        pressedTime: startTime,
        screenX: secondStartX,
        screenY: secondStartY,
        windowX: 0,
        windowY: 0,
        pressure: 1.0,
        width: 0,
        height: 0,
        tiltX: 0,
        tiltY: 0,
        toolX: 0,
        toolY: 0,
        toolWidth: 0,
        toolHeight: 0,
        rawX: 0,
        rawY: 0,
        toolType: ToolType.FINGER
      };

      const pointerDownEvent: TouchEvent = {
        action: Action.DOWN,
        touch: touch1Down,
        touches: [touch1Down, touch2Down],
        sourceType: SourceType.TOUCH_SCREEN,
        id: 0,
        deviceId: 0,
        actionTime: startTime,
        screenId: 0,
        windowId: 0
      };

      inputEventClient.injectTouchEvent({ touchEvent: pointerDownEvent });

      // MOVE事件序列 - 双指同时移动
      for (let i = 1; i <= steps; i++) {
        setTimeout(() => {
          const elapsedTime = i * interval;
          if (elapsedTime >= duration) {
            // POINTER_UP事件 - 第二个手指抬起
            const touch1End: Touch = {
              id: 0,
              pressedTime: startTime,
              screenX: firstEndX,
              screenY: firstEndY,
              windowX: 0,
              windowY: 0,
              pressure: 0.0,
              width: 0,
              height: 0,
              tiltX: 0,
              tiltY: 0,
              toolX: 0,
              toolY: 0,
              toolWidth: 0,
              toolHeight: 0,
              rawX: 0,
              rawY: 0,
              toolType: ToolType.FINGER
            };

            const touch2End: Touch = {
              id: 1,
              pressedTime: startTime,
              screenX: secondEndX,
              screenY: secondEndY,
              windowX: 0,
              windowY: 0,
              pressure: 0.0,
              width: 0,
              height: 0,
              tiltX: 0,
              tiltY: 0,
              toolX: 0,
              toolY: 0,
              toolWidth: 0,
              toolHeight: 0,
              rawX: 0,
              rawY: 0,
              toolType: ToolType.FINGER
            };

            const pointerUpEvent: TouchEvent = {
              action: Action.UP,
              touch: touch1End,
              touches: [touch1End, touch2End],
              sourceType: SourceType.TOUCH_SCREEN,
              id: 0,
              deviceId: 0,
              actionTime: startTime + duration,
              screenId: 0,
              windowId: 0
            };

            inputEventClient.injectTouchEvent({ touchEvent: pointerUpEvent });

            // UP事件 - 第一个手指抬起
            const upEvent: TouchEvent = {
              action: Action.UP,
              touch: touch1End,
              touches: [touch1End],
              sourceType: SourceType.TOUCH_SCREEN,
              id: 0,
              deviceId: 0,
              actionTime: startTime + duration,
              screenId: 0,
              windowId: 0
            };

            inputEventClient.injectTouchEvent({ touchEvent: upEvent });
            Logger.info('Two point swipe event injected successfully');
            return;
          }

          const progress = elapsedTime / duration;
          const firstCurrentX = Math.round(firstStartX + progress * (firstEndX - firstStartX));
          const firstCurrentY = Math.round(firstStartY + progress * (firstEndY - firstStartY));
          const secondCurrentX = Math.round(secondStartX + progress * (secondEndX - secondStartX));
          const secondCurrentY = Math.round(secondStartY + progress * (secondEndY - secondStartY));

          const moveTouch1: Touch = {
            id: 0,
            pressedTime: startTime,
            screenX: firstCurrentX,
            screenY: firstCurrentY,
            windowX: 0,
            windowY: 0,
            pressure: 1.0,
            width: 0,
            height: 0,
            tiltX: 0,
            tiltY: 0,
            toolX: 0,
            toolY: 0,
            toolWidth: 0,
            toolHeight: 0,
            rawX: 0,
            rawY: 0,
            toolType: ToolType.FINGER
          };

          const moveTouch2: Touch = {
            id: 1,
            pressedTime: startTime,
            screenX: secondCurrentX,
            screenY: secondCurrentY,
            windowX: 0,
            windowY: 0,
            pressure: 1.0,
            width: 0,
            height: 0,
            tiltX: 0,
            tiltY: 0,
            toolX: 0,
            toolY: 0,
            toolWidth: 0,
            toolHeight: 0,
            rawX: 0,
            rawY: 0,
            toolType: ToolType.FINGER
          };

          const moveEvent: TouchEvent = {
            action: Action.MOVE,
            touch: moveTouch1,
            touches: [moveTouch1, moveTouch2],
            sourceType: SourceType.TOUCH_SCREEN,
            id: 0,
            deviceId: 0,
            actionTime: startTime + elapsedTime,
            screenId: 0,
            windowId: 0
          };

          inputEventClient.injectTouchEvent({ touchEvent: moveEvent });
        }, i * interval);
      }
    } catch (error) {
      Logger.error(`Failed to inject two point swipe: ${error}`);
    }
  }
}