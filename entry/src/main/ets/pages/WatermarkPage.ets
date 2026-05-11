import photoAccessHelper from '@ohos.file.photoAccessHelper';
import dataSharePredicates from '@ohos.data.dataSharePredicates';
import image from '@ohos.multimedia.image';
import fs from '@ohos.file.fs';
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import Logger from '../common/logger';
import { DeviceStatusUtil } from '../utils/DeviceStatusUtil';
import common from '@ohos.app.ability.common';

@Entry
@Component
struct WatermarkPage {
  @State currentWatermarkType: string = '';      // 查询到的水印类型
  @State processedCount: number = 0;              // 已处理数量
  @State totalCount: number = 0;                  // 总数量
  @State isProcessing: boolean = false;           // 处理状态
  @State errorMessages: string[] = [];            // 错误列表
  @State statusMessage: string = '';              // 状态消息

  aboutToAppear(): void {
    Logger.info('WatermarkPage aboutToAppear');
  }

  aboutToDisappear(): void {
    Logger.info('WatermarkPage aboutToDisappear');

    // 清理所有临时目录
    try {
      const context = getContext(this) as common.UIAbilityContext;

      // 清理实况照片临时目录
      const movingTempDir = `${context.cacheDir}/moving_photo_temp`;
      try {
        // 先删除目录下的所有文件
        const files = fs.listFileSync(movingTempDir);
        for (const file of files) {
          try {
            fs.unlinkSync(`${movingTempDir}/${file}`);
          } catch (unlinkError) {
            // 忽略单个文件删除失败
          }
        }
        // 再删除目录
        fs.rmdirSync(movingTempDir);
        Logger.info('Cleaned up moving_photo_temp directory');
      } catch (movingCleanupError) {
        // 目录可能不存在，忽略错误
      }

      // 清理水印临时目录
      const watermarkTempDir = `${context.cacheDir}/watermark_temp`;
      try {
        // 先删除目录下的所有文件
        const files = fs.listFileSync(watermarkTempDir);
        for (const file of files) {
          try {
            fs.unlinkSync(`${watermarkTempDir}/${file}`);
          } catch (unlinkError) {
            // 忽略单个文件删除失败
          }
        }
        // 再删除目录
        fs.rmdirSync(watermarkTempDir);
        Logger.info('Cleaned up watermark_temp directory');
      } catch (watermarkCleanupError) {
        // 目录可能不存在，忽略错误
      }
    } catch (error) {
      Logger.error(`Failed to cleanup temp directories: ${error}`);
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.watermark'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // 查看水印属性区域
          this.ViewWatermarkSection()

          // 设置水印属性区域
          this.SetWatermarkSection()

          // 状态显示区域
          this.StatusSection()
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

  @Builder ViewWatermarkSection() {
    Column() {
      Text($r('app.string.view_watermark'))
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      // 水印类型显示（如果有）
      if (this.currentWatermarkType) {
        Row() {
          Text($r('app.string.watermark_type_info'))
            .fontSize(14)
          Text(this.currentWatermarkType)
            .fontSize(14)
            .fontColor($r('app.color.text_secondary'))
        }
        .width('100%')
        .justifyContent(FlexAlign.SpaceBetween)
        .padding(16)
        .backgroundColor($r('app.color.card_background'))
        .borderRadius($r('app.float.card_corner_radius'))
        .margin({ bottom: 12 })
      }

      // 查看按钮
      Button($r('app.string.select_single_image'))
        .width('100%')
        .height(40)
        .fontSize(14)
        .backgroundColor($r('app.color.primary'))
        .enabled(!this.isProcessing)
        .onClick(() => {
          this.viewWatermarkProperty();
        })
    }
    .width('100%')
    .padding(16)
    .backgroundColor($r('app.color.card_background'))
    .borderRadius($r('app.float.card_corner_radius'))
    .margin({ bottom: 16 })
  }

  @Builder SetWatermarkSection() {
    Column() {
      Text($r('app.string.set_watermark'))
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      // 进度显示（批量处理时）
      if (this.isProcessing && this.totalCount > 0) {
        Row() {
          Text($r('app.string.processing_images'))
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
        .margin({ bottom: 12 })
      }

      // 设置按钮
      Button($r('app.string.select_multiple_images'))
        .width('100%')
        .height(40)
        .fontSize(14)
        .backgroundColor($r('app.color.primary'))
        .enabled(!this.isProcessing)
        .onClick(() => {
          this.setWatermarkProperty();
        })
    }
    .width('100%')
    .padding(16)
    .backgroundColor($r('app.color.card_background'))
    .borderRadius($r('app.float.card_corner_radius'))
  }

  @Builder StatusSection() {
    // 状态消息显示
    if (this.statusMessage) {
      Column() {
        Text(this.statusMessage)
          .fontSize(14)
          .fontColor($r('app.color.text_secondary'))
          .width('100%')
          .textAlign(TextAlign.Center)
      }
      .width('100%')
      .padding(16)
      .margin({ top: 16 })
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
  }

  // 查看水印属性
  private async viewWatermarkProperty(): Promise<void> {
    try {
      this.isProcessing = true;
      this.errorMessages = [];
      this.statusMessage = '';
      this.currentWatermarkType = '';

      // 创建图片选择器
      const photoPicker = new photoAccessHelper.PhotoViewPicker();
      if (!photoPicker) {
        Logger.error('PhotoPicker is null');
        this.statusMessage = '创建图片选择器失败';
        this.isProcessing = false;
        return;
      }

      // 设置选择选项（单张图片）
      const selectOption = new photoAccessHelper.PhotoSelectOptions();
      if (!selectOption) {
        Logger.error('PhotoSelectOptions is null');
        this.statusMessage = '创建选择选项失败';
        this.isProcessing = false;
        return;
      }

      selectOption.MIMEType = photoAccessHelper.PhotoViewMIMETypes.IMAGE_TYPE;
      selectOption.maxSelectNumber = 1;

      const result = await photoPicker.select(selectOption);
      if (!result || !result.photoUris || result.photoUris.length === 0) {
        Logger.info('No image selected');
        this.statusMessage = '未选择图片';
        this.isProcessing = false;
        return;
      }

      const uri = result.photoUris[0];
      Logger.info(`Selected image URI: ${uri}`);

      // 获取PhotoAccessHelper实例
      const context = getContext(this) as common.UIAbilityContext;
      const phAccessHelper = photoAccessHelper.getPhotoAccessHelper(context);
      if (!phAccessHelper) {
        Logger.error('PhotoAccessHelper is null');
        this.statusMessage = '获取PhotoAccessHelper失败';
        this.isProcessing = false;
        return;
      }

      // 创建查询条件（使用DataSharePredicates）
      const predicates = new dataSharePredicates.DataSharePredicates();
      predicates.equalTo(photoAccessHelper.PhotoKeys.PHOTO_TYPE, photoAccessHelper.PhotoType.IMAGE);
      predicates.equalTo(photoAccessHelper.PhotoKeys.URI, uri);

      // 设置FetchOptions，指定查询列包含SUPPORTED_WATERMARK_TYPE（Hidden API）
      const fetchOptions: photoAccessHelper.FetchOptions = {
        fetchColumns: [photoAccessHelper.PhotoKeys.SUPPORTED_WATERMARK_TYPE],
        predicates: predicates
      };

      Logger.info('Querying watermark property with FetchOptions');

      // 查询图片资产
      let fetchResult: photoAccessHelper.FetchResult<photoAccessHelper.PhotoAsset> | null = null;
      try {
        fetchResult = await phAccessHelper.getAssets(fetchOptions);
        if (!fetchResult) {
          Logger.error('FetchResult is null');
          this.statusMessage = '查询图片资产失败';
          this.isProcessing = false;
          return;
        }

        // 获取PhotoAsset
        const photoAsset = await fetchResult.getFirstObject();
        if (!photoAsset) {
          Logger.error('PhotoAsset is null');
          this.statusMessage = '获取PhotoAsset失败';
          this.isProcessing = false;
          // 关闭fetchResult
          fetchResult.close();
          return;
        }

        // 获取水印类型属性（Hidden API）
        const watermarkType = photoAsset.get(photoAccessHelper.PhotoKeys.SUPPORTED_WATERMARK_TYPE);
        Logger.info(`Watermark type query result: ${watermarkType}`);

        // 解析水印类型（Hidden API枚举值）
        if (watermarkType !== null && watermarkType !== undefined) {
          const typeValue = Number(watermarkType);
          let typeString = '';
          switch (typeValue) {
            case photoAccessHelper.WatermarkType.DEFAULT:
              typeString = 'DEFAULT（默认水印）';
              break;
            case photoAccessHelper.WatermarkType.BRAND_COMMON:
              typeString = 'BRAND_COMMON（品牌通用水印）';
              break;
            case photoAccessHelper.WatermarkType.COMMON:
              typeString = 'COMMON（通用水印）';
              break;
            case photoAccessHelper.WatermarkType.BRAND:
              typeString = 'BRAND（品牌水印）';
              break;
            default:
              typeString = `未知类型 (${typeValue})`;
              break;
          }
          this.currentWatermarkType = typeString;
          promptAction.showToast({
            message: '水印属性查询成功',
            duration: 2000
          });
          this.statusMessage = `水印类型: ${typeString}`;
        } else {
          this.currentWatermarkType = '无水印信息';
          this.statusMessage = '该图片未设置水印属性';
          promptAction.showToast({
            message: '该图片无水印信息',
            duration: 2000
          });
        }

        Logger.info('View watermark property completed');
        this.isProcessing = false;

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

    } catch (error) {
      Logger.error(`Failed to view watermark property: ${error}`);
      this.statusMessage = `查询失败: ${error}`;
      this.errorMessages.push(`查询水印属性失败: ${error}`);
      this.isProcessing = false;
    }
  }

  // 设置水印属性（批量设置水印类型 + 修改EXIF信息）
  private async setWatermarkProperty(): Promise<void> {
    try {
      this.isProcessing = true;
      this.errorMessages = [];
      this.statusMessage = '';

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

      Logger.info(`Selected ${this.totalCount} images for watermark processing`);

      // 获取设备信息
      const manufacture = DeviceStatusUtil.getDeviceManufacture();
      const marketName = DeviceStatusUtil.getDeviceMarketName();
      Logger.info(`Device manufacture: ${manufacture}, marketName: ${marketName}`);

      // 批量处理图片（分批优化）
      const batchSize = 10;
      for (let i = 0; i < uris.length; i += batchSize) {
        const batch = uris.slice(i, Math.min(i + batchSize, uris.length));

        // 批次内并行处理
        const promises = batch.map(async (uri) => {
          await this.processSingleImage(uri, manufacture, marketName);
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
          message: `成功处理 ${successCount} 张图片！水印类型:BRAND_COMMON，EXIF:已修改`,
          duration: 2000
        });
        this.statusMessage = `处理完成：成功设置 ${successCount} 张图片的水印属性和EXIF信息`;
      } else {
        promptAction.showToast({
          message: `处理完成：成功 ${successCount} 张，失败 ${failCount} 张`,
          duration: 3000
        });
        this.statusMessage = `处理完成：成功 ${successCount} 张，失败 ${failCount} 张`;
      }

      Logger.info(`Watermark processing completed: success=${successCount}, fail=${failCount}`);
      this.isProcessing = false;

    } catch (error) {
      Logger.error(`Failed to set watermark property: ${error}`);
      this.statusMessage = `处理失败: ${error}`;
      this.isProcessing = false;
    }
  }

  // 处理单张图片（设置水印属性 + 修改EXIF信息）
  private async processSingleImage(uri: string, manufacture: string, marketName: string): Promise<void> {
    let sandboxFile: fs.File | null = null;
    let imageSource: image.ImageSource | null = null;

    try {
      Logger.info(`Processing image: ${uri}`);

      const context = getContext(this) as common.UIAbilityContext;
      const phAccessHelper = photoAccessHelper.getPhotoAccessHelper(context);
      if (!phAccessHelper) {
        Logger.error('PhotoAccessHelper is null');
        this.errorMessages.push(`处理失败: ${uri} - PhotoAccessHelper is null`);
        return;
      }

      // 步骤0：获取PhotoAsset（通过文件名查询）并提取原始文件名
      const uriParts = uri.split('/');
      const fileName = uriParts[uriParts.length - 1];

      Logger.info(`Extracting file name from URI: ${fileName}`);

      // 提取原始文件名的基础名称（去除扩展名）并添加"_wm"后缀
      const dotIndex = fileName.lastIndexOf('.');
      const baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
      const newTitle = baseName + '_wm';

      Logger.info(`Original file: ${fileName}, New title: ${newTitle}`);

      const predicates = new dataSharePredicates.DataSharePredicates();
      predicates.equalTo(photoAccessHelper.PhotoKeys.DISPLAY_NAME, fileName);

      const fetchOptions: photoAccessHelper.FetchOptions = {
        fetchColumns: [
          photoAccessHelper.PhotoKeys.PHOTO_TYPE,
          photoAccessHelper.PhotoKeys.PHOTO_SUBTYPE
        ],
        predicates: predicates
      };

      const fetchResult = await phAccessHelper.getAssets(fetchOptions);
      if (!fetchResult || !fetchResult.getCount()) {
        Logger.error('FetchResult is null or empty');
        this.errorMessages.push(`处理失败: ${uri} - Failed to fetch asset info`);
        return;
      }

      const photoAsset = await fetchResult.getFirstObject();
      if (!photoAsset) {
        Logger.error('PhotoAsset is null');
        this.errorMessages.push(`处理失败: ${uri} - Failed to get PhotoAsset`);
        return;
      }

      Logger.info(`Successfully obtained PhotoAsset`);

      // 步骤1：使用PhotoAsset的PHOTO_SUBTYPE属性判断是否为实况照片（专业方式）
      const photoSubtype = photoAsset.get(photoAccessHelper.PhotoKeys.PHOTO_SUBTYPE);
      const isMovingPhoto = (photoSubtype === photoAccessHelper.PhotoSubtype.MOVING_PHOTO);

      Logger.info(`Photo subtype: ${photoSubtype}, Is moving photo: ${isMovingPhoto}`);

      fetchResult.close();

      if (isMovingPhoto) {
        // 实况照片：提取静态图片和动态视频，修改EXIF后创建新的实况照片
        Logger.info(`Processing moving photo - will extract video to cache directory`);

        // 步骤1：创建沙箱临时目录
        const movingTempDir = `${context.cacheDir}/moving_photo_temp`;
        try {
          fs.mkdirSync(movingTempDir);
        } catch (mkdirError) {
          // 目录可能已存在
        }

        // 步骤2：使用Promise包装获取MovingPhoto对象和requestId
        const staticImagePath = `${movingTempDir}/static_${Date.now()}.jpg`;
        const videoPath = `${movingTempDir}/video_${Date.now()}.mp4`;

        Logger.info('Requesting MovingPhoto data...');

        // 创建RequestOptions
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

        // 步骤3：提取静态图片和动态视频到沙箱缓存目录
        await movingPhoto.requestContent(staticImagePath, videoPath);
        Logger.info(`Extracted static image and video to cache directory: ${staticImagePath}, ${videoPath}`);

        // 步骤4：修改静态图片的EXIF
        let staticModifyFile: fs.File | null = null;
        let staticImageSource: image.ImageSource | null = null;

        try {
          staticModifyFile = fs.openSync(staticImagePath, fs.OpenMode.READ_WRITE);
          if (!staticModifyFile) {
            Logger.error('Failed to open static image file for EXIF modification');
            this.errorMessages.push(`处理失败: ${uri} - Failed to open static image file`);
            return;
          }

          staticImageSource = image.createImageSource(staticModifyFile.fd);
          if (!staticImageSource) {
            Logger.error('Failed to create ImageSource for static image');
            this.errorMessages.push(`处理失败: ${uri} - Failed to create ImageSource`);
            return;
          }

          await staticImageSource.modifyImageProperty('Make', manufacture);
          Logger.info(`Set MAKE for moving photo: ${manufacture}`);

          await staticImageSource.modifyImageProperty('Model', marketName);
          Logger.info(`Set MODEL for moving photo: ${marketName}`);

          Logger.info(`Modified EXIF for static image`);

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

          if (staticModifyFile) {
            try {
              fs.closeSync(staticModifyFile);
              Logger.info('Closed static modify file');
            } catch (closeError) {
              Logger.error(`Failed to close static modify file: ${closeError}`);
            }
          }
        }

        // 步骤5：创建新的实况照片资产
        const movingCreateRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
          context,
          photoAccessHelper.PhotoType.IMAGE,
          'jpg',
          {
            title: newTitle,
            subtype: photoAccessHelper.PhotoSubtype.MOVING_PHOTO  // 指定为实况照片类型
          }
        );

        // 添加静态图片资源（IMAGE_RESOURCE）
        movingCreateRequest.addResource(photoAccessHelper.ResourceType.IMAGE_RESOURCE, staticImagePath);

        // 添加动态视频资源（VIDEO_RESOURCE）
        movingCreateRequest.addResource(photoAccessHelper.ResourceType.VIDEO_RESOURCE, videoPath);

        // 设置水印类型（Hidden API）
        movingCreateRequest.setSupportedWatermarkType(photoAccessHelper.WatermarkType.BRAND_COMMON);
        Logger.info(`Set watermark type to BRAND_COMMON for moving photo`);

        // 应用变更（创建新的实况照片）
        await phAccessHelper.applyChanges(movingCreateRequest);
        Logger.info(`Created new moving photo with watermark and modified EXIF`);

        // 步骤6：清理缓存临时文件和目录
        try {
          fs.unlinkSync(staticImagePath);
          fs.unlinkSync(videoPath);
          Logger.info(`Cleaned up moving photo temp files`);

          // 清理临时目录（如果为空）
          try {
            fs.rmdirSync(movingTempDir);
            Logger.info(`Cleaned up moving photo temp directory`);
          } catch (rmdirError) {
            // 目录可能不为空或不存在，忽略错误
          }
        } catch (cleanupError) {
          Logger.error(`Failed to cleanup temp files: ${cleanupError}`);
        }

        Logger.info(`Successfully processed moving photo: ${uri}`);
        return; // 实况照片处理完成
      }

      // 非实况照片：创建新资产并添加水印

      // 步骤1：将相册图片复制到沙箱临时目录
      const tempDir = `${context.cacheDir}/watermark_temp`;
      try {
        fs.mkdirSync(tempDir);
      } catch (mkdirError) {
        // 目录可能已存在，忽略错误
      }

      const imageFileName = `watermark_${Date.now()}.jpg`;
      const sandboxPath = `${tempDir}/${imageFileName}`;

      Logger.info(`Copying image from ${uri} to sandbox: ${sandboxPath}`);

      // 打开相册图片
      let srcFile: fs.File | null = null;
      try {
        srcFile = fs.openSync(uri, fs.OpenMode.READ_ONLY);
        if (!srcFile) {
          Logger.error(`Failed to open source file: ${uri}`);
          this.errorMessages.push(`处理失败: ${uri} - Failed to open source file`);
          return;
        }

        // 创建沙箱目标文件
        sandboxFile = fs.openSync(sandboxPath, fs.OpenMode.CREATE | fs.OpenMode.WRITE_ONLY);
        if (!sandboxFile) {
          Logger.error(`Failed to create sandbox file: ${sandboxPath}`);
          this.errorMessages.push(`处理失败: ${uri} - Failed to create sandbox file`);
          return;
        }

        // 复制文件内容
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

      // 步骤2：先修改EXIF信息（在沙箱文件上）
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

      // 修改EXIF属性：MAKE（制造商）
      await imageSource.modifyImageProperty('Make', manufacture);
      Logger.info(`Set MAKE: ${manufacture}`);

      // 修改EXIF属性：MODEL（型号）
      await imageSource.modifyImageProperty('Model', marketName);
      Logger.info(`Set MODEL: ${marketName}`);

      Logger.info(`EXIF modification completed, now creating asset request`);

      // 释放ImageSource并关闭文件（在finally块中保障）
      try {
        await imageSource.release();
        imageSource = null;
        Logger.info('Released ImageSource after EXIF modification');
      } catch (releaseError) {
        Logger.error(`Failed to release ImageSource: ${releaseError}`);
      }

      try {
        fs.closeSync(sandboxFile);
        sandboxFile = null;
        Logger.info('Closed sandbox file after EXIF modification');
      } catch (closeError) {
        Logger.error(`Failed to close sandbox file: ${closeError}`);
      }

      // 步骤3：使用已修改EXIF的沙箱文件创建MediaAssetChangeRequest（带标题）
      const changeRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
        context,
        photoAccessHelper.PhotoType.IMAGE,
        'jpg',
        {
          title: newTitle
        }
      );
      if (!changeRequest) {
        Logger.error('MediaAssetChangeRequest is null');
        this.errorMessages.push(`处理失败: ${uri} - Failed to create MediaAssetChangeRequest`);
        return;
      }

      // 添加图片资源
      changeRequest.addResource(photoAccessHelper.ResourceType.IMAGE_RESOURCE, sandboxPath);

      // 步骤4：设置水印类型为BRAND_COMMON（Hidden API）
      changeRequest.setSupportedWatermarkType(photoAccessHelper.WatermarkType.BRAND_COMMON);
      Logger.info(`Set watermark type to BRAND_COMMON`);

      // 步骤5：应用变更（将带正确EXIF和水印的图片添加到相册）
      await phAccessHelper.applyChanges(changeRequest);
      Logger.info(`Applied watermark and EXIF changes, new asset added to gallery`);

      Logger.info(`Successfully processed image: ${uri}`);

      // 步骤6：清理沙箱临时文件和目录
      try {
        fs.unlinkSync(sandboxPath);
        Logger.info(`Cleaned up temp file: ${sandboxPath}`);

        // 清理临时目录（如果为空）
        try {
          fs.rmdirSync(tempDir);
          Logger.info(`Cleaned up watermark temp directory`);
        } catch (rmdirError) {
          // 目录可能不为空或不存在，忽略错误
        }
      } catch (cleanupError) {
        Logger.error(`Failed to cleanup temp file: ${cleanupError}`);
      }

    } catch (error) {
      Logger.error(`Failed to process image ${uri}: ${error}`);
      this.errorMessages.push(`处理失败: ${uri} - ${error}`);
    } finally {
      // 资源释放（重要！）
      if (imageSource) {
        try {
          await imageSource.release();
          Logger.info('Released ImageSource');
        } catch (releaseError) {
          Logger.error(`Failed to release ImageSource: ${releaseError}`);
        }
      }

      if (sandboxFile) {
        try {
          fs.closeSync(sandboxFile);
          Logger.info('Closed sandbox file');
        } catch (closeError) {
          Logger.error(`Failed to close sandbox file: ${closeError}`);
        }
      }
    }
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

export default WatermarkPage;