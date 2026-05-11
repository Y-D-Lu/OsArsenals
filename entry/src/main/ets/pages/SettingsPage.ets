import Logger from '../common/logger';

@Entry
@Component
struct SettingsPage {
  @State permissionsStatus: string = '待检查';
  @State pointMapCount: number = 0;

  aboutToAppear(): void {
    Logger.info('SettingsPage aboutToAppear');
    this.loadSettings();
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.settings'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // 权限状态
          Column() {
            Text('权限状态')
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .margin({ bottom: 8 })

            List() {
              ListItem() {
                Row() {
                  Text('悬浮窗权限')
                    .fontSize(14)
                  Text(this.permissionsStatus)
                    .fontSize(14)
                    .fontColor($r('app.color.text_secondary'))
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
                .padding(16)
              }

              ListItem() {
                Row() {
                  Text('输入注入权限')
                    .fontSize(14)
                  Text(this.permissionsStatus)
                    .fontSize(14)
                    .fontColor($r('app.color.text_secondary'))
                }
                .width('100%')
                .justifyContent(FlexAlign.SpaceBetween)
                .padding(16)
              }
            }
            .width('100%')
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
          }
          .width('100%')
          .padding(16)
          .margin({ bottom: 16 })

          // 坐标点映射配置
          Column() {
            Text('原神自动化配置')
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .margin({ bottom: 8 })

            Row() {
              Text('预设坐标点数量')
                .fontSize(14)
              Text(`${this.pointMapCount}`)
                .fontSize(14)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('100%')
            .justifyContent(FlexAlign.SpaceBetween)
            .padding(16)
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
          }
          .width('100%')
          .padding(16)
          .margin({ bottom: 16 })

          // 其他设置
          Column() {
            Text('应用信息')
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .margin({ bottom: 8 })

            Row() {
              Text('应用版本')
                .fontSize(14)
              Text('1.0.0')
                .fontSize(14)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('100%')
            .justifyContent(FlexAlign.SpaceBetween)
            .padding(16)
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
          }
          .width('100%')
          .padding(16)
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 16, bottom: 64 })
      }
      .width('100%')
      .scrollBar(BarState.Auto)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }

  private loadSettings(): void {
    Logger.info('SettingsPage loadSettings');

    // 检查权限状态
    // system_core等级应用在Profile中已配置权限，安装时自动授权
    this.permissionsStatus = '已配置（system_core）';

    // 坐标点数量
    // 原神自动化固定6个预设坐标点
    this.pointMapCount = 6;
  }
}export default SettingsPage;
