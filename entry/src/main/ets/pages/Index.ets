import FunctionPage from './FunctionPage';
import OverviewPage from './OverviewPage';
import SettingsPage from './SettingsPage';

@Entry
@Component
struct Index {
  @State currentIndex: number = 1; // 默认显示概述页（中间）

  build() {
    Column() {
      Tabs({ index: $$this.currentIndex }) {
        TabContent() {
          FunctionPage()
        }
        .tabBar($r('app.string.function'))

        TabContent() {
          OverviewPage()
        }
        .tabBar($r('app.string.overview'))

        TabContent() {
          SettingsPage()
        }
        .tabBar($r('app.string.settings'))
      }
      .barPosition(BarPosition.End)
      .vertical(false)
      .scrollable(true)
      .barMode(BarMode.Fixed)
      .barBackgroundColor('#F5F5F5')  // Tabs导航栏背景色与app一致
      .onChange((index: number) => {
        this.currentIndex = index;
      })
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }
}

export default Index;