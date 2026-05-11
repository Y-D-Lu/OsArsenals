import Logger from '../common/logger';

/**
 * 百分比圆形进度组件
 */
@Component
export struct PercentageCircle {
  @Prop value: number = 0;     // 百分比值（0-100）
  @Prop circleSize: number = 80;     // 圆形大小
  @Prop label: string = '';    // 标签文本

  build() {
    Stack() {
      // 背景圆环
      Circle()
        .width(this.circleSize)
        .height(this.circleSize)
        .fill(Color.Transparent)
        .stroke($r('app.color.progress_background'))
        .strokeWidth(this.circleSize * 0.25)

      // 进度圆环（使用半透明青色）
      // -1值表示数据不可用，显示灰色圆环
      Circle()
        .width(this.circleSize)
        .height(this.circleSize)
        .fill(Color.Transparent)
        .stroke(this.value === -1 ? Color.Gray : $r('app.color.progress_foreground'))
        .strokeWidth(this.circleSize * 0.25)
        .opacity(this.value === -1 ? 0.3 : Math.min(this.value / 100, 1))

      // 标签文本
      Column() {
        if (this.label) {
          Text(this.label)
            .fontSize(12)
            .fontColor($r('app.color.text_primary'))
        }
        // -1值显示"--"，其他值显示百分比
        Text(this.value === -1 || this.value === undefined ? '--' : `${Math.round(this.value)}%`)
          .fontSize(14)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .justifyContent(FlexAlign.Center)
    }
    .width(this.circleSize)
    .height(this.circleSize)
  }
}