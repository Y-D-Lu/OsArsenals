import { GenshinImpactUtil } from '../utils/GenshinImpactUtil';
import Logger from '../common/logger';

@Entry
@Component
struct GenshinPage {
  @State scriptContent: string = '';
  @State isRunning: boolean = false;
  @State executionLog: string = '';

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.genshin_automation'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // 脚本编辑区
          Column() {
            Text($r('app.string.script_editor'))
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .margin({ bottom: 8 })

            TextArea({ placeholder: '输入脚本命令（如：e,1000|sleep,600|z,1000）', text: this.scriptContent })
              .height(150)
              .width('100%')
              .backgroundColor($r('app.color.card_background'))
              .onChange((value: string) => {
                this.scriptContent = value;
              })
          }
          .width('100%')
          .padding(16)
          .margin({ bottom: 16 })

          // 预设脚本选择
          Column() {
            Text($r('app.string.preset_scripts'))
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .margin({ bottom: 8 })

            List() {
              ListItem() {
                Button('Clear Pool 预设')
                  .width('100%')
                  .height(40)
                  .fontSize(14)
                  .backgroundColor($r('app.color.primary'))
                  .onClick(() => {
                    this.loadPresetScript('clear_pool');
                  })
              }
              .margin({ bottom: 8 })

              ListItem() {
                Button('Forsaken Rift 预设')
                  .width('100%')
                  .height(40)
                  .fontSize(14)
                  .backgroundColor($r('app.color.primary'))
                  .onClick(() => {
                    this.loadPresetScript('forsaken_rift');
                  })
              }
              .margin({ bottom: 8 })

              ListItem() {
                Button('Pale Forgotten Glory 预设')
                  .width('100%')
                  .height(40)
                  .fontSize(14)
                  .backgroundColor($r('app.color.primary'))
                  .onClick(() => {
                    this.loadPresetScript('pale_forgotten_glory');
                  })
              }
              .margin({ bottom: 8 })
            }
            .width('100%')
          }
          .width('100%')
          .padding(16)
          .margin({ bottom: 16 })

          // 执行日志显示
          Column() {
            Text('执行日志')
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .margin({ bottom: 8 })

            Text(this.executionLog || '无执行记录')
              .fontSize(12)
              .fontColor($r('app.color.text_secondary'))
              .width('100%')
              .height(70)
              .backgroundColor($r('app.color.card_background'))
              .padding(8)
          }
          .width('100%')
          .padding(16)
          .margin({ bottom: 16 })

          // 控制按钮
          Row() {
            Button(this.isRunning ? $r('app.string.stop_script') : $r('app.string.execute_script'))
              .width('100%')
              .height(40)
              .fontSize(14)
              .backgroundColor($r('app.color.primary'))
              .onClick(() => {
                if (this.isRunning) {
                  this.executionLog = '脚本已停止';
                  this.isRunning = false;
                  Logger.info('Script execution stopped by user');
                } else {
                  this.executeScript();
                }
              })
          }
          .width('100%')
          .padding({ left: 16, right: 16, top: 16, bottom: 80 })
          .justifyContent(FlexAlign.Center)
        }
        .width('100%')
      }
      .width('100%')
      .height('100%')
      .scrollBar(BarState.Auto)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }

  private loadPresetScript(name: string): void {
    Logger.info(`Load preset script: ${name}`);

    // 预设脚本内容（基于Android版本）
    const presets: Record<string, string> = {
      'clear_pool': 'domain_enter|sleep,2000|e,1000|sleep,600|z,1000|sleep,500|domain_exit',
      'forsaken_rift': 'domain_enter|sleep,3000|e,500|sleep,400|a,5|sleep,200|domain_exit',
      'pale_forgotten_glory': 'domain_enter|sleep,2500|s,1|sleep,300|e,800|s,2|sleep,300|e,800|domain_exit'
    };

    this.scriptContent = presets[name] || '';
    this.executionLog = `已加载预设: ${name}`;
  }

  private async executeScript(): Promise<void> {
    if (!this.scriptContent) {
      this.executionLog = '错误: 脚本内容为空';
      return;
    }

    Logger.info(`Execute script: ${this.scriptContent}`);
    this.isRunning = true;
    this.executionLog = '开始执行...';

    try {
      await GenshinImpactUtil.handleGenshinCommand(this.scriptContent);
      this.executionLog = '执行完成';
    } catch (error) {
      this.executionLog = `执行失败: ${error}`;
      Logger.error(`Script execution failed: ${error}`);
    }

    this.isRunning = false;
  }
}export default GenshinPage;
