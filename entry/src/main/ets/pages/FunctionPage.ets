import router from '@ohos.router';
import GenshinPage from './GenshinPage';
import HookPage from './HookPage';
import StandbyPage from './StandbyPage';
import PerformanceTestPage from './PerformanceTestPage';
import Logger from '../common/logger';

@Entry
@Component
struct FunctionPage {
  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.function'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // 功能入口网格
          Grid() {
            // 原神自动化
            GridItem() {
              Column() {
                Text('🎮')
                  .fontSize(40)
                Text($r('app.string.genshin_automation'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/GenshinPage' });
                Logger.info('Navigate to GenshinPage');
              })
            }

            // 挂机模式
            GridItem() {
              Column() {
                Text('🌙')
                  .fontSize(40)
                Text($r('app.string.hook_mode'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/HookPage' });
                Logger.info('Navigate to HookPage');
              })
            }

            // 待机显示
            GridItem() {
              Column() {
                Text('⏰')
                  .fontSize(40)
                Text($r('app.string.standby_mode'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/StandbyPage' });
                Logger.info('Navigate to StandbyPage');
              })
            }

            // 性能测试
            GridItem() {
              Column() {
                Text('⚡')
                  .fontSize(40)
                Text($r('app.string.performance_test'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/PerformanceTestPage' });
                Logger.info('Navigate to PerformanceTestPage');
              })
            }

            // 实况照片
            GridItem() {
              Column() {
                Text('📸')
                  .fontSize(40)
                Text($r('app.string.moving_photo'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/MovingPhotoPage' });
                Logger.info('Navigate to MovingPhotoPage');
              })
            }

            // 本机水印
            GridItem() {
              Column() {
                Text('💧')
                  .fontSize(40)
                Text($r('app.string.watermark'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/WatermarkPage' });
                Logger.info('Navigate to WatermarkPage');
              })
            }

            // 元数据编辑
            GridItem() {
              Column() {
                Text('📝')
                  .fontSize(40)
                Text($r('app.string.metadata_edit'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/MetadataEditPage' });
                Logger.info('Navigate to MetadataEditPage');
              })
            }

            // 批量光圈
            GridItem() {
              Column() {
                Text('⚙️')
                  .fontSize(40)
                Text($r('app.string.aperture_batch'))
                  .fontSize(16)
                  .fontWeight(FontWeight.Medium)
                  .margin({ top: 8 })
              }
              .width('100%')
              .height(150)
              .backgroundColor($r('app.color.card_background'))
              .borderRadius($r('app.float.card_corner_radius'))
              .justifyContent(FlexAlign.Center)
              .onClick(() => {
                router.pushUrl({ url: 'pages/AperturePage' });
                Logger.info('Navigate to AperturePage');
              })
            }
          }
          .columnsTemplate('1fr 1fr')
          .rowsTemplate('1fr 1fr 1fr 1fr')
          .columnsGap(16)
          .rowsGap(16)
          .width('100%')
          .height(680)
          .padding({ left: 16, right: 16, top: 16, bottom: 16 })
        }
        .width('100%')
        .padding({ bottom: 64 })
      }
      .width('100%')
      .scrollBar(BarState.Off)
      .edgeEffect(EdgeEffect.Spring)
      .align(Alignment.Top)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }
}

export default FunctionPage;
