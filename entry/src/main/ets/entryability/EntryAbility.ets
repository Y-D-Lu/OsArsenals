import { AbilityConstant, ConfigurationConstant, UIAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';
import { InputManager } from '../managers/InputManager';
import abilityAccessCtrl from '@ohos.abilityAccessCtrl';
import Logger from '../common/logger';

const DOMAIN = 0x0000;

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    try {
      this.context.getApplicationContext().setColorMode(ConfigurationConstant.ColorMode.COLOR_MODE_NOT_SET);
    } catch (err) {
      hilog.error(DOMAIN, 'testTag', 'Failed to set colorMode. Cause: %{public}s', JSON.stringify(err));
    }
    hilog.info(DOMAIN, 'testTag', '%{public}s', 'Ability onCreate');

    // 初始化InputManager
    this.initInputManager();

    // 申请媒体读写权限
    this.requestMediaPermissions();
  }

  onDestroy(): void {
    hilog.info(DOMAIN, 'testTag', '%{public}s', 'Ability onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    // Main window is created, set main page for this ability
    hilog.info(DOMAIN, 'testTag', '%{public}s', 'Ability onWindowStageCreate');

    // 启动后台服务
    this.startServiceAbility();

    windowStage.loadContent('pages/Index', (err) => {
      if (err.code) {
        hilog.error(DOMAIN, 'testTag', 'Failed to load the content. Cause: %{public}s', JSON.stringify(err));
        return;
      }
      hilog.info(DOMAIN, 'testTag', 'Succeeded in loading the content.');

      // 设置沉浸式体验：隐藏状态栏和导航栏
      this.setupImmersiveMode(windowStage);
    });
  }

  onWindowStageDestroy(): void {
    // Main window is destroyed, release UI related resources
    hilog.info(DOMAIN, 'testTag', '%{public}s', 'Ability onWindowStageDestroy');
  }

  onForeground(): void {
    // Ability has brought to foreground
    hilog.info(DOMAIN, 'testTag', '%{public}s', 'Ability onForeground');
  }

  onBackground(): void {
    // Ability has back to background
    hilog.info(DOMAIN, 'testTag', '%{public}s', 'Ability onBackground');
  }

  /**
   * 初始化InputManager
   */
  private async initInputManager(): Promise<void> {
    try {
      const instance = InputManager.getInstance();
      await instance.init(this.context);
      Logger.info('InputManager initialized successfully');
    } catch (error) {
      Logger.error(`Failed to initialize InputManager: ${error}`);
    }
  }

  /**
   * 启动后台服务Ability
   */
  private startServiceAbility(): void {
    try {
      const serviceWant: Want = {
        bundleName: 'cn.arsenals.osarsenals',
        abilityName: 'ServiceAbility'
      };

      this.context.startAbility(serviceWant, (err) => {
        if (err.code) {
          Logger.error(`Failed to start ServiceAbility: ${err.message}`);
        } else {
          Logger.info('ServiceAbility started successfully');
        }
      });
    } catch (error) {
      Logger.error(`Failed to start ServiceAbility: ${error}`);
    }
  }

  /**
   * 设置沉浸式体验（状态栏和导航栏与app融为一体）
   */
  private async setupImmersiveMode(windowStage: window.WindowStage): Promise<void> {
    try {
      // 获取主窗口
      const mainWindow = await windowStage.getMainWindow();

      // 设置系统栏属性：背景色与app一致，内容颜色适配
      const systemBarProperties: window.SystemBarProperties = {
        statusBarColor: '#F5F5F5',  // 状态栏背景色（浅灰，与app背景一致）
        statusBarContentColor: '#000000',  // 状态栏内容颜色（黑色文字/图标）

        navigationBarColor: '#F5F5F5',  // 导航栏背景色（浅灰，与app背景一致）
        navigationBarContentColor: '#000000'  // 导航栏内容颜色（黑色）
      };

      await mainWindow.setSystemBarProperties(systemBarProperties);

      Logger.info('Immersive mode enabled: system bars color matched with app background (#F5F5F5)');
    } catch (error) {
      Logger.error(`Failed to setup immersive mode: ${error}`);
    }
  }

  // 申请媒体读写权限
  private async requestMediaPermissions(): Promise<void> {
    try {
      const atManager = abilityAccessCtrl.createAtManager();

      // 申请权限（system_core等级应该自动授予，但仍需显式请求）
      const grantResult = await atManager.requestPermissionsFromUser(this.context, [
        'ohos.permission.READ_IMAGEVIDEO',
        'ohos.permission.WRITE_IMAGEVIDEO'
      ]);

      Logger.info(`Media permissions requested successfully`);

      // 检查授权结果
      if (grantResult && grantResult.authResults) {
        for (let i = 0; i < grantResult.authResults.length; i++) {
          if (grantResult.authResults[i] === 0) {
            Logger.info(`Permission granted`);
          } else {
            Logger.warn(`Permission denied: ${grantResult.authResults[i]}`);
          }
        }
      }
    } catch (error) {
      Logger.error(`Failed to request media permissions: ${error}`);
    }
  }
}