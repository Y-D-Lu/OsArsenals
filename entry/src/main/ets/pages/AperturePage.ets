import photoAccessHelper from '@ohos.file.photoAccessHelper';
import dataSharePredicates from '@ohos.data.dataSharePredicates';
import image from '@ohos.multimedia.image';
import fs from '@ohos.file.fs';
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import Logger from '../common/logger';
import common from '@ohos.app.ability.common';

@Entry
@Component
struct AperturePage {
  @State fnumberInput: string = '';
  @State processedCount: number = 0;
  @State totalCount: number = 0;
  @State isProcessing: boolean = false;
  @State statusMessage: string = '';
  @State errorMessages: string[] = [];

  aboutToAppear(): void {
    Logger.info('AperturePage aboutToAppear');
  }

  aboutToDisappear(): void {
    Logger.info('AperturePage aboutToDisappear');

    // 清理aperture_temp目录
    try {
      const context = getContext(this) as common.UIAbilityContext;
      const apertureTempDir = `${context.cacheDir}/aperture_temp`;

      try {
        const files = fs.listFileSync(apertureTempDir);
        for (const file of files) {
          try {
            fs.unlinkSync(`${apertureTempDir}/${file}`);
          } catch (error) {
            Logger.error(`Failed to cleanup temp file ${file}: ${error}`);
          }
        }
        fs.rmdirSync(apertureTempDir);
        Logger.info(`Cleaned up aperture_temp directory`);
      } catch (error) {
        Logger.error(`Failed to cleanup aperture_temp directory: ${error}`);
      }
    } catch (error) {
      Logger.error(`Failed to cleanup temp files: ${error}`);
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.aperture_batch'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // FNumber输入区域
          this.FNumberInputSection()

          // 进度显示
          if (this.isProcessing && this.totalCount > 0) {
            Row() {
              Text('处理进度:')
                .fontSize(14)
              Text(`${this.processedCount}/${this.totalCount}`)
                .fontSize(14)
                .fontColor($r('app.color.text_secondary'))
            }
            .width('100%')
            .justifyContent(FlexAlign.SpaceBetween)
            .padding(16)
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
            .margin({ bottom: 16 })
          }

          // 状态消息
          if (this.statusMessage) {
            Text(this.statusMessage)
              .fontSize(14)
              .fontColor($r('app.color.text_secondary'))
              .margin({ top: 16, bottom: 16 })
              .padding(16)
              .width('100%')
              .textAlign(TextAlign.Center)
          }

          // 错误信息显示
          if (this.errorMessages.length > 0) {
            Column() {
              Text('错误信息:')
                .fontSize(14)
                .fontWeight(FontWeight.Medium)
                .margin({ bottom: 8 })

              ForEach(this.errorMessages, (msg: string, index: number) => {
                Text(msg)
                  .fontSize(12)
                  .fontColor(Color.Red)
                  .margin({ top: 4 })
              })
            }
            .width('100%')
            .padding(16)
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
            .margin({ top: 16 })
          }

          // 批量处理按钮
          Button('选择照片并批量修改光圈')
            .width('100%')
            .height(40)
            .fontSize(14)
            .backgroundColor($r('app.color.primary'))
            .enabled(!this.isProcessing && this.fnumberInput !== '')
            .margin({ bottom: 64 })
            .onClick(() => {
              this.batchSetAperture();
            })
        }
        .width('100%')
        .padding({ left: 16, right: 16, top: 16 })
      }
      .width('100%')
      .scrollBar(BarState.Auto)
    }
    .width('100%')
    .height('100%')
    .backgroundColor($r('app.color.background'))
  }

  @Builder FNumberInputSection() {
    Column() {
      Text('光圈值设置')
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      TextInput({ placeholder: '请输入光圈值，如2.8、1.8' })
        .width('100%')
        .height(40)
        .fontSize(14)
        .onChange((value: string) => {
          this.fnumberInput = value;
          Logger.info(`FNumber input updated: ${value}`);
        })

      Text('提示：输入纯数字值（如2.8），不带f/前缀')
        .fontSize(12)
        .fontColor($r('app.color.text_secondary'))
        .margin({ top: 8 })
    }
    .width('100%')
    .padding(16)
    .backgroundColor($r('app.color.card_background'))
    .borderRadius($r('app.float.card_corner_radius'))
    .margin({ bottom: 16 })
  }

  // 批量设置光圈
  private async batchSetAperture(): Promise<void> {
    try {
      this.isProcessing = true;
      this.errorMessages = [];
      this.statusMessage = '';

      // 验证FNumber输入值
      const fnumberValue = this.fnumberInput.trim();
      if (!this.validateFNumber(fnumberValue)) {
        promptAction.showToast({
          message: '请输入有效的光圈值，如2.8、1.8',
          duration: 2000
        });
        this.statusMessage = '请输入有效的光圈值';
        this.isProcessing = false;
        return;
      }

      Logger.info(`FNumber value validated: ${fnumberValue}`);

      // 创建图片选择器
      const photoPicker = new photoAccessHelper.PhotoViewPicker();
      if (!photoPicker) {
        Logger.error('PhotoPicker is null');
        this.statusMessage = '创建图片选择器失败';
        this.isProcessing = false;
        return;
      }

      // 设置选择选项（最多500张图片）
      const selectOption = new photoAccessHelper.PhotoSelectOptions();
      if (!selectOption) {
        Logger.error('PhotoSelectOptions is null');
        this.statusMessage = '创建选择选项失败';
        this.isProcessing = false;
        return;
      }

      selectOption.MIMEType = photoAccessHelper.PhotoViewMIMETypes.IMAGE_TYPE;
      selectOption.maxSelectNumber = 500;

      const result = await photoPicker.select(selectOption);
      if (!result || !result.photoUris || result.photoUris.length === 0) {
        Logger.info('No images selected');
        this.statusMessage = '未选择图片';
        this.isProcessing = false;
        return;
      }

      const uris = result.photoUris;
      this.totalCount = uris.length;
      this.processedCount = 0;
      this.statusMessage = `已选择${this.totalCount}张图片，开始处理...`;

      Logger.info(`Selected ${this.totalCount} images for aperture modification`);

      // 批量处理图片（分批优化）
      const batchSize = 10;
      for (let i = 0; i < uris.length; i += batchSize) {
        const batch = uris.slice(i, Math.min(i + batchSize, uris.length));

        // 批次内并行处理
        const promises = batch.map(async (uri) => {
          await this.processSingleImageAperture(uri, fnumberValue);
        });

        await Promise.all(promises);

        // 更新进度
        this.processedCount = Math.min(i + batchSize, uris.length);
        const percentage = Math.round((this.processedCount / this.totalCount) * 100);
        this.statusMessage = `处理进度: ${this.processedCount}/${this.totalCount} (${percentage}%)`;

        // 批次间短暂延迟（避免系统过载）
        if (i + batchSize < uris.length) {
          await this.delay(100);
        }
      }

      // 显示完成摘要
      const successCount = this.totalCount - this.errorMessages.length;
      const failCount = this.errorMessages.length;

      if (failCount === 0) {
        promptAction.showToast({
          message: `成功处理 ${successCount} 张照片！FNumber已设置为${fnumberValue}`,
          duration: 2000
        });
        this.statusMessage = `处理完成：成功修改 ${successCount} 张照片的光圈值`;
      } else {
        promptAction.showToast({
          message: `处理完成：成功 ${successCount} 张，失败 ${failCount} 张`,
          duration: 3000
        });
        this.statusMessage = `处理完成：成功 ${successCount} 张，失败 ${failCount} 张`;
      }

      Logger.info(`Aperture processing completed: success=${successCount}, fail=${failCount}`);
      this.isProcessing = false;

    } catch (error) {
      Logger.error(`Failed to batch set aperture: ${error}`);
      this.statusMessage = `处理失败: ${error}`;
      this.isProcessing = false;
    }
  }

  // 处理单张图片光圈值
  private async processSingleImageAperture(uri: string, fnumber: string): Promise<void> {
    try {
      Logger.info(`Processing image aperture: ${uri}`);

      const context = getContext(this) as common.UIAbilityContext;
      const phAccessHelper = photoAccessHelper.getPhotoAccessHelper(context);
      if (!phAccessHelper) {
        Logger.error('PhotoAccessHelper is null');
        this.errorMessages.push(`处理失败: ${uri} - PhotoAccessHelper is null`);
        return;
      }

      // 提取文件名并判断图片类型
      const uriParts = uri.split('/');
      const fileName = uriParts[uriParts.length - 1];
      const dotIndex = fileName.lastIndexOf('.');
      const baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
      const newTitle = baseName + '_fn';

      Logger.info(`Original file: ${fileName}, New title: ${newTitle}`);

      // 判断图片类型
      const predicates = new dataSharePredicates.DataSharePredicates();
      predicates.equalTo(photoAccessHelper.PhotoKeys.DISPLAY_NAME, fileName);

      const fetchOptions: photoAccessHelper.FetchOptions = {
        fetchColumns: [
          photoAccessHelper.PhotoKeys.PHOTO_TYPE,
          photoAccessHelper.PhotoKeys.PHOTO_SUBTYPE
        ],
        predicates: predicates
      };

      let fetchResult: photoAccessHelper.FetchResult<photoAccessHelper.PhotoAsset> | null = null;
      let photoAsset: photoAccessHelper.PhotoAsset | null = null;
      let isMovingPhoto: boolean = false;

      try {
        fetchResult = await phAccessHelper.getAssets(fetchOptions);
        if (!fetchResult || !fetchResult.getCount()) {
          Logger.error('FetchResult is null or empty');
          this.errorMessages.push(`处理失败: ${uri} - Failed to fetch asset info`);
          return;
        }

        photoAsset = await fetchResult.getFirstObject();
        if (!photoAsset) {
          Logger.error('PhotoAsset is null');
          this.errorMessages.push(`处理失败: ${uri} - Failed to get PhotoAsset`);
          return;
        }

        const photoSubtype = photoAsset.get(photoAccessHelper.PhotoKeys.PHOTO_SUBTYPE);
        isMovingPhoto = (photoSubtype === photoAccessHelper.PhotoSubtype.MOVING_PHOTO);

        Logger.info(`Photo subtype: ${photoSubtype}, Is moving photo: ${isMovingPhoto}`);

      } finally {
        // 释放资源（重要！）
        if (fetchResult) {
          try {
            fetchResult.close();
            Logger.info('Closed fetchResult');
          } catch (closeError) {
            Logger.error(`Failed to close fetchResult: ${closeError}`);
          }
        }
      }

      if (isMovingPhoto && photoAsset) {
        // 实况照片处理
        await this.processMovingPhotoAperture(photoAsset, fnumber, newTitle, phAccessHelper, context);
      } else {
        // 普通照片处理
        await this.processNormalPhotoAperture(uri, fnumber, newTitle, phAccessHelper, context);
      }

      Logger.info(`Successfully processed image: ${uri}`);

    } catch (error) {
      Logger.error(`Failed to process image ${uri}: ${error}`);
      this.errorMessages.push(`处理失败: ${uri} - ${error}`);
    }
  }

  // 处理普通照片光圈值
  private async processNormalPhotoAperture(
    uri: string,
    fnumber: string,
    newTitle: string,
    phAccessHelper: photoAccessHelper.PhotoAccessHelper,
    context: common.UIAbilityContext
  ): Promise<void> {
    let sandboxFile: fs.File | null = null;
    let imageSource: image.ImageSource | null = null;

    try {
      // 复制到沙箱
      const tempDir = `${context.cacheDir}/aperture_temp`;
      try {
        fs.mkdirSync(tempDir);
      } catch (mkdirError) {
        // 目录可能已存在
      }

      const sandboxPath = `${tempDir}/aperture_${Date.now()}.jpg`;

      Logger.info(`Copying image from ${uri} to sandbox: ${sandboxPath}`);

      let srcFile: fs.File | null = null;
      try {
        srcFile = fs.openSync(uri, fs.OpenMode.READ_ONLY);
        if (!srcFile) {
          Logger.error(`Failed to open source file: ${uri}`);
          this.errorMessages.push(`处理失败: ${uri} - Failed to open source file`);
          return;
        }

        sandboxFile = fs.openSync(sandboxPath, fs.OpenMode.CREATE | fs.OpenMode.WRITE_ONLY);
        if (!sandboxFile) {
          Logger.error(`Failed to create sandbox file: ${sandboxPath}`);
          this.errorMessages.push(`处理失败: ${uri} - Failed to create sandbox file`);
          return;
        }

        const stat = fs.statSync(srcFile.fd);
        const bufferSize = 4096;
        let offset = 0;
        const totalSize = stat.size;

        while (offset < totalSize) {
          const readSize = Math.min(bufferSize, totalSize - offset);
          const buffer = new ArrayBuffer(readSize);
          fs.readSync(srcFile.fd, buffer, { offset: offset, length: readSize });
          fs.writeSync(sandboxFile.fd, buffer, { offset: offset, length: readSize });
          offset += readSize;
        }

        Logger.info(`Successfully copied image to sandbox`);

      } finally {
        // 关闭源文件（重要！）
        if (srcFile) {
          try {
            fs.closeSync(srcFile);
            Logger.info('Closed source file');
          } catch (closeError) {
            Logger.error(`Failed to close source file: ${closeError}`);
          }
        }

        // 关闭沙箱文件（重要！）
        if (sandboxFile) {
          try {
            fs.closeSync(sandboxFile);
            sandboxFile = null;
            Logger.info('Closed sandbox file after copy');
          } catch (closeError) {
            Logger.error(`Failed to close sandbox file: ${closeError}`);
          }
        }
      }

      // 修改FNumber EXIF
      sandboxFile = fs.openSync(sandboxPath, fs.OpenMode.READ_WRITE);
      if (!sandboxFile) {
        Logger.error(`Failed to open sandbox file for EXIF modification`);
        this.errorMessages.push(`处理失败: ${uri} - Failed to open sandbox file`);
        return;
      }

      imageSource = image.createImageSource(sandboxFile.fd);
      if (!imageSource) {
        Logger.error('Failed to create ImageSource');
        this.errorMessages.push(`处理失败: ${uri} - Failed to create ImageSource`);
        return;
      }

      await imageSource.modifyImageProperty('FNumber', fnumber);
      Logger.info(`Set FNumber: ${fnumber}`);

      await imageSource.release();
      imageSource = null;
      fs.closeSync(sandboxFile);
      sandboxFile = null;

      // 创建新资产（文件名：原文件名_fn）
      const changeRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
        context,
        photoAccessHelper.PhotoType.IMAGE,
        'jpg',
        { title: newTitle }
      );

      changeRequest.addResource(photoAccessHelper.ResourceType.IMAGE_RESOURCE, sandboxPath);
      await phAccessHelper.applyChanges(changeRequest);

      Logger.info(`Applied aperture changes, new asset added`);

      // 清理临时文件
      fs.unlinkSync(sandboxPath);

    } catch (error) {
      Logger.error(`Failed to process normal photo aperture: ${error}`);
      this.errorMessages.push(`处理失败: ${uri} - ${error}`);
    } finally {
      if (imageSource) {
        try {
          await imageSource.release();
        } catch (releaseError) {
          Logger.error(`Failed to release ImageSource: ${releaseError}`);
        }
      }

      if (sandboxFile) {
        try {
          fs.closeSync(sandboxFile);
        } catch (closeError) {
          Logger.error(`Failed to close sandbox file: ${closeError}`);
        }
      }
    }
  }

  // 处理实况照片光圈值
  private async processMovingPhotoAperture(
    photoAsset: photoAccessHelper.PhotoAsset,
    fnumber: string,
    newTitle: string,
    phAccessHelper: photoAccessHelper.PhotoAccessHelper,
    context: common.UIAbilityContext
  ): Promise<void> {
    try {
      // 创建沙箱临时目录
      const movingTempDir = `${context.cacheDir}/aperture_temp`;
      try {
        fs.mkdirSync(movingTempDir);
      } catch (mkdirError) {
        // 目录可能已存在
      }

      const requestOptions: photoAccessHelper.RequestOptions = {
        deliveryMode: photoAccessHelper.DeliveryMode.FAST_MODE
      };

      // 使用Promise获取MovingPhoto对象
      const movingPhoto = await new Promise<photoAccessHelper.MovingPhoto>((resolve, reject) => {
        const dataHandler: photoAccessHelper.MediaAssetDataHandler<photoAccessHelper.MovingPhoto> = {
          onDataPrepared: (data: photoAccessHelper.MovingPhoto): void => {
            if (!data) {
              Logger.error('MovingPhoto data is null');
              reject(new Error('MovingPhoto data is null'));
              return;
            }
            Logger.info('MovingPhoto data prepared successfully');
            resolve(data);
          }
        };

        photoAccessHelper.MediaAssetManager.requestMovingPhoto(context, photoAsset, requestOptions, dataHandler)
          .catch((err: Error) => {
            Logger.error(`requestMovingPhoto failed: ${err.message}`);
            reject(err);
          });
      });

      Logger.info('Successfully obtained MovingPhoto object');

      const staticPath = `${movingTempDir}/static_${Date.now()}.jpg`;
      const videoPath = `${movingTempDir}/video_${Date.now()}.mp4`;

      await movingPhoto.requestContent(staticPath, videoPath);
      Logger.info(`Extracted static image and video: ${staticPath}, ${videoPath}`);

      // 修改静态图片FNumber EXIF
      let staticFile: fs.File | null = null;
      let staticImageSource: image.ImageSource | null = null;

      try {
        staticFile = fs.openSync(staticPath, fs.OpenMode.READ_WRITE);
        if (!staticFile) {
          Logger.error('Failed to open static image file for FNumber modification');
          this.errorMessages.push(`处理失败: 实况照片 - Failed to open static image file`);
          return;
        }

        staticImageSource = image.createImageSource(staticFile.fd);
        if (!staticImageSource) {
          Logger.error('Failed to create ImageSource for static image');
          this.errorMessages.push(`处理失败: 实况照片 - Failed to create ImageSource`);
          return;
        }

        await staticImageSource.modifyImageProperty('FNumber', fnumber);
        Logger.info(`Set FNumber for static image: ${fnumber}`);

        Logger.info(`Modified FNumber EXIF for static image`);

      } finally {
        // 释放资源（重要！）
        if (staticImageSource) {
          try {
            await staticImageSource.release();
            Logger.info('Released static ImageSource');
          } catch (releaseError) {
            Logger.error(`Failed to release static ImageSource: ${releaseError}`);
          }
        }

        if (staticFile) {
          try {
            fs.closeSync(staticFile);
            Logger.info('Closed static file');
          } catch (closeError) {
            Logger.error(`Failed to close static file: ${closeError}`);
          }
        }
      }

      // 创建新的实况照片资产（文件名：原文件名_fn）
      const movingCreateRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
        context,
        photoAccessHelper.PhotoType.IMAGE,
        'jpg',
        {
          title: newTitle,
          subtype: photoAccessHelper.PhotoSubtype.MOVING_PHOTO
        }
      );

      movingCreateRequest.addResource(photoAccessHelper.ResourceType.IMAGE_RESOURCE, staticPath);
      movingCreateRequest.addResource(photoAccessHelper.ResourceType.VIDEO_RESOURCE, videoPath);

      await phAccessHelper.applyChanges(movingCreateRequest);
      Logger.info(`Created new moving photo with modified FNumber`);

      // 清理临时文件
      fs.unlinkSync(staticPath);
      fs.unlinkSync(videoPath);

    } catch (error) {
      Logger.error(`Failed to process moving photo aperture: ${error}`);
      this.errorMessages.push(`处理失败: 实况照片 - ${error}`);
    }
  }

  // 验证FNumber输入值
  private validateFNumber(value: string): boolean {
    // 验证是否为有效的数字（可以是整数或小数）
    const numberRegex = /^[0-9]+(\.[0-9]+)?$/;
    return numberRegex.test(value);
  }

  // 延迟函数
  private async delay(ms: number): Promise<void> {
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        resolve();
      }, ms);
    });
  }
}

export default AperturePage;