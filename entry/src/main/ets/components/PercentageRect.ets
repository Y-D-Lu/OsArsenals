import Logger from '../common/logger';

/**
 * 百分比矩形进度组件（占位）
 */
@Component
export struct PercentageRect {
  @Prop value: number = 0;     // 百分比值（0-100）

  build() {
    Stack() {
      // 背景矩形
      Rect()
        .width('100%')
        .height($r('app.float.progress_rect_height'))
        .fill($r('app.color.progress_background'))

      // 进度矩形
      Rect()
        .width(`${this.value}%`)
        .height($r('app.float.progress_rect_height'))
        .fill($r('app.color.progress_foreground'))
    }
    .width('100%')
    .height($r('app.float.progress_rect_height'))
  }
}