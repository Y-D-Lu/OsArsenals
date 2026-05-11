import Logger from '../common/logger';

@Entry
@Component
struct PerformanceTestPage {
  @State testMode: string = 'single';
  @State testDuration: number = 60;
  @State isRunning: boolean = false;
  @State testProgress: number = 0;
  private testTimer: number | null = null;
  private startTime: number = 0;

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.performance_test'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          Text('CPU性能测试')
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .margin({ bottom: 16 })

          // 测试模式选择
          Column() {
            Text('测试模式')
              .fontSize(14)
              .margin({ bottom: 8 })

            Row() {
              Radio({ value: 'single', group: 'testMode' })
                .checked(this.testMode === 'single')
                .onChange((isChecked: boolean) => {
                  if (isChecked) {
                    this.testMode = 'single';
                  }
                })
              Text('单核测试')
                .fontSize(14)
                .margin({ left: 8 })

              Radio({ value: 'multi', group: 'testMode' })
                .checked(this.testMode === 'multi')
                .onChange((isChecked: boolean) => {
                  if (isChecked) {
                    this.testMode = 'multi';
                  }
                })
              Text('多核测试')
                .fontSize(14)
                .margin({ left: 8 })
            }
            .width('100%')
            .justifyContent(FlexAlign.SpaceAround)
          }
          .width('100%')
          .padding(16)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius($r('app.float.card_corner_radius'))
          .margin({ bottom: 16 })

          // 测试时长输入
          Column() {
            Text('测试时长（秒）')
              .fontSize(14)
              .margin({ bottom: 8 })

            TextInput({ placeholder: '60' })
              .type(InputType.Number)
              .width('100%')
              .onChange((value: string) => {
                this.testDuration = parseInt(value) || 60;
              })
          }
          .width('100%')
          .padding(16)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius($r('app.float.card_corner_radius'))
          .margin({ bottom: 16 })

          // 测试进度显示
          if (this.isRunning) {
            Column() {
              Text(`测试进度: ${this.testProgress}%`)
                .fontSize(14)
                .margin({ bottom: 8 })

              Progress({ value: this.testProgress, total: 100, type: ProgressType.Linear })
                .width('100%')
            }
            .width('100%')
            .padding(16)
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
            .margin({ bottom: 16 })
          }

          // 控制按钮
          Button(this.isRunning ? '停止测试' : '开始测试')
            .width('100%')
            .height(40)
            .fontSize(14)
            .backgroundColor($r('app.color.primary'))
            .onClick(() => {
              if (this.isRunning) {
                this.stopTest();
              } else {
                this.startTest();
              }
            })
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 16, bottom: 80 })
      }
      .width('100%')
      .height('100%')
      .scrollBar(BarState.Auto)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }

  private startTest(): void {
    Logger.info(`Start performance test: mode=${this.testMode}, duration=${this.testDuration}`);
    this.isRunning = true;
    this.testProgress = 0;
    this.startTime = Date.now();

    // 启动CPU压力测试
    this.runCpuStressTest();
  }

  private stopTest(): void {
    Logger.info('Stop performance test');
    this.isRunning = false;
    this.testProgress = 0;

    // 清除定时器
    if (this.testTimer) {
      clearInterval(this.testTimer);
      this.testTimer = null;
    }
  }

  private runCpuStressTest(): void {
    // 启动定时器更新进度
    this.testTimer = setInterval(() => {
      const elapsed = (Date.now() - this.startTime) / 1000;
      this.testProgress = Math.min(Math.round((elapsed / this.testDuration) * 100), 100);

      // 测试完成
      if (elapsed >= this.testDuration) {
        this.stopTest();
        Logger.info('Performance test completed');
      }
    }, 1000);

    // 执行CPU压力测试
    if (this.testMode === 'single') {
      // 单核测试：单线程计算循环
      this.singleCoreTest();
    } else {
      // 多核测试：多线程计算循环
      this.multiCoreTest();
    }
  }

  private singleCoreTest(): void {
    Logger.info('Running single-core CPU stress test');

    // 使用setTimeout异步执行，避免阻塞UI线程
    setTimeout(() => {
      this.executeStressLoop(1);
    }, 100);
  }

  private multiCoreTest(): void {
    Logger.info('Running multi-core CPU stress test');

    // 模拟多核测试：使用多个setTimeout并行执行
    const coreCount = 4; // 模拟4核并行
    for (let i = 0; i < coreCount; i++) {
      setTimeout(() => {
        this.executeStressLoop(i);
      }, 100 * i);
    }
  }

  private executeStressLoop(threadId: number): void {
    Logger.info(`Stress loop thread ${threadId} started`);

    // 计算循环：计算平方根，产生CPU负载
    const iterations = 1000000; // 每次循环计算次数
    let counter = 0;

    // 使用while循环持续计算
    const startTime = Date.now();
    while (this.isRunning && (Date.now() - startTime) < this.testDuration * 1000) {
      // 执行密集计算
      for (let i = 0; i < iterations; i++) {
        // 计算平方根（CPU密集型操作）
        Math.sqrt(Math.random() * 10000);
        counter++;
      }

      // 短暂休息，避免完全阻塞
      // 注意：在真实应用中应使用Worker线程，这里简化实现
    }

    Logger.info(`Stress loop thread ${threadId} completed, iterations: ${counter}`);
  }
}

export default PerformanceTestPage;
