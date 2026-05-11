import window from '@ohos.window';
import Logger from '../common/logger';
import { Constants } from '../common/constants';

/**
 * 悬浮窗管理器
 * 负责悬浮窗口的创建、显示、隐藏、销毁和拖动
 */
export class FloatingWindowManager {
  private floatWindow: window.Window | null = null;
  private isShowing: boolean = false;
  private windowX: number = 100;
  private windowY: number = 100;

  /**
   * 创建悬浮窗口
   * @param context 应用上下文
   */
  async createFloatingWindow(context: Context): Promise<void> {
    try {
      Logger.info('FloatingWindowManager: Creating floating window');

      // 创建悬浮窗口
      this.floatWindow = await window.create(context, 'floatWindow', window.WindowType.TYPE_FLOAT);

      // 加载内容页面
      await this.floatWindow.loadContent('pages/MonitorPanel', (err) => {
        if (err && err.code) {
          Logger.error(`Failed to load MonitorPanel content: ${err.message}`);
          return;
        }
        Logger.info('MonitorPanel content loaded successfully');
      });

      // 设置窗口属性（在loadContent之后）
      await this.floatWindow.resize(Constants.FLOAT_WINDOW_WIDTH, Constants.FLOAT_WINDOW_HEIGHT);
      await this.floatWindow.moveWindowTo(this.windowX, this.windowY);

      // 显示窗口
      await this.floatWindow.showWindow();
      this.isShowing = true;

      Logger.info('Floating window created and shown');
    } catch (error) {
      Logger.error(`Failed to create floating window: ${error}`);
      // 不抛出异常，仅记录错误
    }
  }

  /**
   * 显示悬浮窗口
   */
  async showFloatingWindow(): Promise<void> {
    if (this.floatWindow && !this.isShowing) {
      try {
        await this.floatWindow.showWindow();
        this.isShowing = true;
        Logger.info('Floating window shown');
      } catch (error) {
        Logger.error(`Failed to show floating window: ${error}`);
      }
    }
  }

  /**
   * 隐藏悬浮窗口
   */
  async hideFloatingWindow(): Promise<void> {
    if (this.floatWindow && this.isShowing) {
      try {
        // 暂时使用销毁窗口的方式，后续可优化
        await this.floatWindow.destroyWindow();
        this.floatWindow = null;
        this.isShowing = false;
        Logger.info('Floating window hidden (destroyed)');
      } catch (error) {
        Logger.error(`Failed to hide floating window: ${error}`);
      }
    }
  }

  /**
   * 销毁悬浮窗口
   */
  async destroyFloatingWindow(): Promise<void> {
    if (this.floatWindow) {
      try {
        await this.floatWindow.destroyWindow();
        this.floatWindow = null;
        this.isShowing = false;
        Logger.info('Floating window destroyed');
      } catch (error) {
        Logger.error(`Failed to destroy floating window: ${error}`);
      }
    }
  }

  /**
   * 检查悬浮窗口是否正在显示
   */
  isFloatingWindowShowing(): boolean {
    return this.isShowing;
  }

  /**
   * 移动悬浮窗口到指定位置
   * @param x X坐标
   * @param y Y坐标
   */
  async moveFloatingWindow(x: number, y: number): Promise<void> {
    if (this.floatWindow) {
      try {
        await this.floatWindow.moveWindowTo(x, y);
        this.windowX = x;
        this.windowY = y;
        Logger.info(`Floating window moved to (${x}, ${y})`);
      } catch (error) {
        Logger.error(`Failed to move floating window: ${error}`);
      }
    }
  }
}