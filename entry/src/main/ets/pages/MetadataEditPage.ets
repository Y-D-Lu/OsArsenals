import photoAccessHelper from '@ohos.file.photoAccessHelper';
import dataSharePredicates from '@ohos.data.dataSharePredicates';
import image from '@ohos.multimedia.image';
import fs from '@ohos.file.fs';
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import Logger from '../common/logger';
import common from '@ohos.app.ability.common';

interface ExifEntry {
  key: string;
  value: string;
}

@Entry
@Component
struct MetadataEditPage {
  @State imageUri: string = '';
  @State isMovingPhoto: boolean = false;
  @State exifEntries: ExifEntry[] = [];
  @State isProcessing: boolean = false;
  @State statusMessage: string = '';
  @State errorMessages: string[] = [];

  private sandboxPath: string = '';
  private staticPath: string = '';
  private videoPath: string = '';
  private originalFileName: string = '';

  aboutToAppear(): void {
    Logger.info('MetadataEditPage aboutToAppear');
  }

  aboutToDisappear(): void {
    Logger.info('MetadataEditPage aboutToDisappear');

    // 清理临时文件
    try {
      const context = getContext(this) as common.UIAbilityContext;

      if (this.sandboxPath) {
        try {
          fs.unlinkSync(this.sandboxPath);
          Logger.info(`Cleaned up sandbox path: ${this.sandboxPath}`);
        } catch (error) {
          Logger.error(`Failed to cleanup sandbox path: ${error}`);
        }
      }

      if (this.staticPath) {
        try {
          fs.unlinkSync(this.staticPath);
          Logger.info(`Cleaned up static path: ${this.staticPath}`);
        } catch (error) {
          Logger.error(`Failed to cleanup static path: ${error}`);
        }
      }

      if (this.videoPath) {
        try {
          fs.unlinkSync(this.videoPath);
          Logger.info(`Cleaned up video path: ${this.videoPath}`);
        } catch (error) {
          Logger.error(`Failed to cleanup video path: ${error}`);
        }
      }

      // 清理metadata_temp目录
      const metadataTempDir = `${context.cacheDir}/metadata_temp`;
      try {
        const files = fs.listFileSync(metadataTempDir);
        for (const file of files) {
          try {
            fs.unlinkSync(`${metadataTempDir}/${file}`);
          } catch (error) {
            Logger.error(`Failed to cleanup temp file ${file}: ${error}`);
          }
        }
        fs.rmdirSync(metadataTempDir);
        Logger.info(`Cleaned up metadata_temp directory`);
      } catch (error) {
        Logger.error(`Failed to cleanup metadata_temp directory: ${error}`);
      }
    } catch (error) {
      Logger.error(`Failed to cleanup temp files: ${error}`);
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.metadata_edit'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // 图片预览区域
          this.ImagePreviewSection()

          // EXIF属性编辑区域
          this.ExifEditSection()

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

          // 保存按钮
          Button('保存修改')
            .width('100%')
            .height(40)
            .fontSize(14)
            .backgroundColor($r('app.color.primary'))
            .enabled(!this.isProcessing && this.exifEntries.length > 0)
            .margin({ top: 16, bottom: 64 })
            .onClick(() => {
              this.saveMetadata();
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

  @Builder ImagePreviewSection() {
    Column() {
      Text('已选择图片')
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      Column() {
        if (this.imageUri) {
          if (this.isMovingPhoto) {
            Column() {
              Text('🎬')
                .fontSize(40)
              Text('实况照片')
                .fontSize(14)
                .fontColor($r('app.color.text_secondary'))
                .margin({ top: 8 })
            }
            .width('100%')
            .height(180)
            .justifyContent(FlexAlign.Center)
            .backgroundColor($r('app.color.card_background'))
            .borderRadius($r('app.float.card_corner_radius'))
          } else {
            Image(this.imageUri)
              .width('100%')
              .height(180)
              .objectFit(ImageFit.Contain)
              .borderRadius($r('app.float.card_corner_radius'))
          }
        } else {
          Column() {
            Text('📷')
              .fontSize(40)
            Text('点击下方按钮选择图片')
              .fontSize(14)
              .fontColor($r('app.color.text_secondary'))
              .margin({ top: 8 })
          }
          .width('100%')
          .height(180)
          .justifyContent(FlexAlign.Center)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius($r('app.float.card_corner_radius'))
        }
      }
      .width('100%')
      .margin({ bottom: 12 })

      Button($r('app.string.select_single_image'))
        .width('100%')
        .height(40)
        .fontSize(14)
        .backgroundColor($r('app.color.primary'))
        .enabled(!this.isProcessing)
        .onClick(() => {
          this.selectImage();
        })
    }
    .width('100%')
    .padding(16)
    .backgroundColor($r('app.color.card_background'))
    .borderRadius($r('app.float.card_corner_radius'))
    .margin({ bottom: 16 })
  }

  @Builder ExifEditSection() {
    Column() {
      Text('EXIF属性编辑')
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      // EXIF属性列表（可编辑）
      if (this.exifEntries.length > 0) {
        ForEach(this.exifEntries, (entry: ExifEntry, index: number) => {
          Column() {
            if (entry.key === '') {
              // 自定义属性：显示可编辑的属性名和值输入框
              Row() {
                TextInput({ placeholder: '属性名（如Make）' })
                  .fontSize(14)
                  .width('40%')
                  .height(36)
                  .backgroundColor('#F5F5F5')
                  .borderRadius(4)
                  .margin({ right: 8 })
                  .onChange((value: string) => {
                    this.updateExifKey(index, value);
                  })

                TextInput({ placeholder: '属性值' })
                  .fontSize(14)
                  .width('50%')
                  .height(36)
                  .backgroundColor('#F5F5F5')
                  .borderRadius(4)
                  .onChange((value: string) => {
                    this.updateExifValue(index, value);
                  })

                Button('删除')
                  .fontSize(12)
                  .height(36)
                  .backgroundColor(Color.Red)
                  .onClick(() => {
                    this.removeExifEntry(index);
                  })
              }
              .width('100%')
            } else {
              // 已读取的属性：显示属性名标签和可编辑的值输入框
              Text(this.getPropertyDisplayName(entry.key))
                .fontSize(14)
                .fontWeight(FontWeight.Medium)
                .margin({ bottom: 4 })

              TextInput({ text: entry.value })
                .fontSize(14)
                .width('100%')
                .height(36)
                .backgroundColor('#F5F5F5')
                .borderRadius(4)
                .onChange((value) => {
                  this.updateExifValue(index, value);
                })
            }
          }
          .width('100%')
          .padding(12)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius($r('app.float.card_corner_radius'))
          .margin({ bottom: 8 })
        })

        // 添加自定义属性按钮
        Button('添加自定义EXIF属性')
          .width('100%')
          .height(40)
          .fontSize(14)
          .backgroundColor($r('app.color.primary'))
          .enabled(!this.isProcessing)
          .onClick(() => {
            this.addExifEntry();
          })
          .margin({ top: 12 })
      } else {
        // 未读取到EXIF属性，提供手动添加功能
        Text('该图片未包含EXIF信息，可手动添加属性')
          .fontSize(14)
          .fontColor($r('app.color.text_secondary'))
          .margin({ bottom: 12 })

        Button('添加EXIF属性')
          .width('100%')
          .height(40)
          .fontSize(14)
          .backgroundColor($r('app.color.primary'))
          .enabled(!this.isProcessing && this.imageUri !== '')
          .onClick(() => {
            this.addExifEntry();
          })
      }
    }
    .width('100%')
    .padding(16)
    .backgroundColor($r('app.color.card_background'))
    .borderRadius($r('app.float.card_corner_radius'))
    .margin({ bottom: 16 })
  }

  // 选择图片
  private async selectImage(): Promise<void> {
    try {
      this.isProcessing = true;
      this.errorMessages = [];
      this.statusMessage = '';
      this.exifEntries = [];

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

      // 提取文件名
      const uriParts = uri.split('/');
      this.originalFileName = uriParts[uriParts.length - 1];
      const dotIndex = this.originalFileName.lastIndexOf('.');
      const baseName = dotIndex > 0 ? this.originalFileName.substring(0, dotIndex) : this.originalFileName;

      Logger.info(`Original file: ${this.originalFileName}, Base name: ${baseName}`);

      // 判断图片类型（普通照片 vs 实况照片）
      const predicates = new dataSharePredicates.DataSharePredicates();
      predicates.equalTo(photoAccessHelper.PhotoKeys.DISPLAY_NAME, this.originalFileName);

      const fetchOptions: photoAccessHelper.FetchOptions = {
        fetchColumns: [
          photoAccessHelper.PhotoKeys.PHOTO_TYPE,
          photoAccessHelper.PhotoKeys.PHOTO_SUBTYPE
        ],
        predicates: predicates
      };

      let fetchResult: photoAccessHelper.FetchResult<photoAccessHelper.PhotoAsset> | null = null;
      let photoAsset: photoAccessHelper.PhotoAsset | null = null;

      try {
        fetchResult = await phAccessHelper.getAssets(fetchOptions);
        if (!fetchResult || !fetchResult.getCount()) {
          Logger.error('FetchResult is null or empty');
          this.statusMessage = '获取图片资产失败';
          this.isProcessing = false;
          return;
        }

        photoAsset = await fetchResult.getFirstObject();
        if (!photoAsset) {
          Logger.error('PhotoAsset is null');
          this.statusMessage = '获取PhotoAsset失败';
          this.isProcessing = false;
          return;
        }

        const photoSubtype = photoAsset.get(photoAccessHelper.PhotoKeys.PHOTO_SUBTYPE);
        this.isMovingPhoto = (photoSubtype === photoAccessHelper.PhotoSubtype.MOVING_PHOTO);

        Logger.info(`Photo subtype: ${photoSubtype}, Is moving photo: ${this.isMovingPhoto}`);

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

      // 根据图片类型准备文件路径并读取EXIF
      if (this.isMovingPhoto) {
        // 实况照片：提取静态图片和视频
        const movingTempDir = `${context.cacheDir}/metadata_temp`;
        try {
          fs.mkdirSync(movingTempDir);
        } catch (mkdirError) {
          // 目录可能已存在
        }

        const requestOptions: photoAccessHelper.RequestOptions = {
          deliveryMode: photoAccessHelper.DeliveryMode.FAST_MODE
        };

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

        this.staticPath = `${movingTempDir}/static_${Date.now()}.jpg`;
        this.videoPath = `${movingTempDir}/video_${Date.now()}.mp4`;

        await movingPhoto.requestContent(this.staticPath, this.videoPath);
        Logger.info(`Extracted static image and video: ${this.staticPath}, ${this.videoPath}`);

        // 读取静态图片EXIF
        await this.readExifProperties(this.staticPath);

      } else {
        // 普通照片：复制到沙箱
        const tempDir = `${context.cacheDir}/metadata_temp`;
        try {
          fs.mkdirSync(tempDir);
        } catch (mkdirError) {
          // 目录可能已存在
        }

        this.sandboxPath = `${tempDir}/metadata_${Date.now()}.jpg`;

        Logger.info(`Copying image from ${uri} to sandbox: ${this.sandboxPath}`);

        let srcFile: fs.File | null = null;
        let sandboxFile: fs.File | null = null;

        try {
          srcFile = fs.openSync(uri, fs.OpenMode.READ_ONLY);
          if (!srcFile) {
            Logger.error(`Failed to open source file: ${uri}`);
            this.statusMessage = '打开源文件失败';
            this.isProcessing = false;
            return;
          }

          sandboxFile = fs.openSync(this.sandboxPath, fs.OpenMode.CREATE | fs.OpenMode.WRITE_ONLY);
          if (!sandboxFile) {
            Logger.error(`Failed to create sandbox file: ${this.sandboxPath}`);
            this.statusMessage = '创建沙箱文件失败';
            this.isProcessing = false;
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
              Logger.info('Closed sandbox file after copy');
            } catch (closeError) {
              Logger.error(`Failed to close sandbox file: ${closeError}`);
            }
          }
        }

        // 读取EXIF属性
        await this.readExifProperties(this.sandboxPath);
      }

      this.imageUri = uri;
      this.statusMessage = this.isMovingPhoto ? '已读取实况照片EXIF属性' : '已读取普通照片EXIF属性';
      this.isProcessing = false;

    } catch (error) {
      Logger.error(`Failed to select image: ${error}`);
      this.statusMessage = `选择图片失败: ${error}`;
      this.errorMessages.push(`选择图片失败: ${error}`);
      this.isProcessing = false;
    }
  }

  // 读取EXIF属性并显示在UI上
  private async readExifProperties(filePath: string): Promise<void> {
    let file: fs.File | null = null;
    let imageSource: image.ImageSource | null = null;

    try {
      Logger.info(`Reading EXIF properties from: ${filePath}`);

      // 打开文件
      file = fs.openSync(filePath, fs.OpenMode.READ_ONLY);
      if (!file) {
        Logger.error('Failed to open file for EXIF reading');
        this.statusMessage = '打开文件失败';
        return;
      }

      // 创建ImageSource
      imageSource = image.createImageSource(file.fd);
      if (!imageSource) {
        Logger.error('Failed to create ImageSource for EXIF reading');
        this.statusMessage = '创建ImageSource失败';
        return;
      }

      // 定义要读取的EXIF属性列表（常见属性）
      const propertyKeys: image.PropertyKey[] = [
        image.PropertyKey.MAKE,
        image.PropertyKey.MODEL,
        image.PropertyKey.F_NUMBER,
        image.PropertyKey.ISO_SPEED_RATINGS,
        image.PropertyKey.EXPOSURE_TIME,
        image.PropertyKey.DATE_TIME_ORIGINAL,
        image.PropertyKey.ORIENTATION,
        image.PropertyKey.IMAGE_DESCRIPTION,
        image.PropertyKey.GPS_LATITUDE,
        image.PropertyKey.GPS_LONGITUDE
      ];

      Logger.info(`Reading ${propertyKeys.length} EXIF properties`);

      // 批量读取EXIF属性
      const properties = await imageSource.getImageProperties(propertyKeys);

      Logger.info('Successfully read EXIF properties');

      // 清空现有条目
      this.exifEntries = [];

      // 遍历读取结果并填充到UI数组
      for (const key of propertyKeys) {
        const value = properties[key];
        if (value !== null && value !== undefined && value !== '') {
          this.exifEntries.push({
            key: key as string,
            value: value
          });
          Logger.info(`EXIF property: ${key} = ${value}`);
        }
      }

      Logger.info(`Loaded ${this.exifEntries.length} EXIF properties to UI`);

      if (this.exifEntries.length === 0) {
        this.statusMessage = '该图片未包含EXIF信息，可手动添加';
      } else {
        this.statusMessage = `已读取 ${this.exifEntries.length} 个EXIF属性`;
      }

    } catch (error) {
      Logger.error(`Failed to read EXIF properties: ${error}`);
      this.statusMessage = `读取EXIF失败: ${error}`;
      this.errorMessages.push(`读取EXIF失败: ${error}`);
    } finally {
      // 释放资源（重要！）
      if (imageSource) {
        try {
          await imageSource.release();
          Logger.info('Released ImageSource');
        } catch (releaseError) {
          Logger.error(`Failed to release ImageSource: ${releaseError}`);
        }
      }

      if (file) {
        try {
          fs.closeSync(file);
          Logger.info('Closed file');
        } catch (closeError) {
          Logger.error(`Failed to close file: ${closeError}`);
        }
      }
    }
  }

  // 获取属性显示名称
  private getPropertyDisplayName(key: string): string {
    switch (key) {
      case image.PropertyKey.MAKE:
        return '制造商';
      case image.PropertyKey.MODEL:
        return '型号';
      case image.PropertyKey.F_NUMBER:
        return '光圈值';
      case image.PropertyKey.ISO_SPEED_RATINGS:
        return 'ISO感光度';
      case image.PropertyKey.EXPOSURE_TIME:
        return '曝光时间';
      case image.PropertyKey.DATE_TIME_ORIGINAL:
        return '拍摄时间';
      case image.PropertyKey.ORIENTATION:
        return '图片方向';
      case image.PropertyKey.IMAGE_DESCRIPTION:
        return '图片描述';
      case image.PropertyKey.GPS_LATITUDE:
        return 'GPS纬度';
      case image.PropertyKey.GPS_LONGITUDE:
        return 'GPS经度';
      default:
        return key; // 对于自定义属性，直接显示键名
    }
  }

  // 添加自定义EXIF属性条目
  private addExifEntry(): void {
    this.exifEntries.push({ key: '', value: '' });
    Logger.info(`Added new custom EXIF entry, total entries: ${this.exifEntries.length}`);
  }

  // 更新EXIF属性名（仅用于自定义属性）
  private updateExifKey(index: number, key: string): void {
    if (index >= 0 && index < this.exifEntries.length) {
      this.exifEntries[index].key = key;
      Logger.info(`Updated EXIF key at index ${index}: ${key}`);
    }
  }

  // 更新EXIF属性值
  private updateExifValue(index: number, value: string): void {
    if (index >= 0 && index < this.exifEntries.length) {
      this.exifEntries[index].value = value;
      Logger.info(`Updated EXIF value at index ${index}: ${value}`);
    }
  }

  // 删除EXIF属性条目
  private removeExifEntry(index: number): void {
    if (index >= 0 && index < this.exifEntries.length) {
      this.exifEntries.splice(index, 1);
      Logger.info(`Removed EXIF entry at index ${index}, remaining entries: ${this.exifEntries.length}`);
    }
  }

  // 保存修改后的元数据
  private async saveMetadata(): Promise<void> {
    try {
      this.isProcessing = true;
      this.statusMessage = '正在保存修改...';

      // 验证EXIF属性
      const validEntries = this.exifEntries.filter(entry => entry.key.trim() !== '' && entry.value.trim() !== '');
      if (validEntries.length === 0) {
        promptAction.showToast({
          message: '请至少添加一个有效的EXIF属性',
          duration: 2000
        });
        this.statusMessage = '请至少添加一个有效的EXIF属性';
        this.isProcessing = false;
        return;
      }

      Logger.info(`Valid EXIF entries: ${validEntries.length}`);

      const context = getContext(this) as common.UIAbilityContext;
      const phAccessHelper = photoAccessHelper.getPhotoAccessHelper(context);

      // 提取原始文件名并添加"_exif"后缀
      const dotIndex = this.originalFileName.lastIndexOf('.');
      const baseName = dotIndex > 0 ? this.originalFileName.substring(0, dotIndex) : this.originalFileName;
      const newTitle = baseName + '_exif';

      Logger.info(`New title: ${newTitle}`);

      if (this.isMovingPhoto) {
        // 实况照片：修改静态图片EXIF并创建新的实况照片
        await this.saveMovingPhotoMetadata(validEntries, newTitle, phAccessHelper, context);
      } else {
        // 普通照片：修改EXIF并创建新照片
        await this.saveNormalPhotoMetadata(validEntries, newTitle, phAccessHelper, context);
      }

      promptAction.showToast({
        message: 'EXIF修改已保存！',
        duration: 2000
      });

      // 清空状态
      this.imageUri = '';
      this.exifEntries = [];
      this.statusMessage = '';
      this.isMovingPhoto = false;
      this.isProcessing = false;

      Logger.info('Metadata saved successfully');

    } catch (error) {
      Logger.error(`Failed to save metadata: ${error}`);
      this.statusMessage = `保存失败: ${error}`;
      this.errorMessages.push(`保存失败: ${error}`);
      this.isProcessing = false;
    }
  }

  // 保存普通照片元数据
  private async saveNormalPhotoMetadata(
    entries: ExifEntry[],
    newTitle: string,
    phAccessHelper: photoAccessHelper.PhotoAccessHelper,
    context: common.UIAbilityContext
  ): Promise<void> {
    let file: fs.File | null = null;
    let imageSource: image.ImageSource | null = null;

    try {
      // 打开沙箱文件（READ_WRITE模式）
      file = fs.openSync(this.sandboxPath, fs.OpenMode.READ_WRITE);
      if (!file) {
        Logger.error('Failed to open sandbox file for EXIF modification');
        this.statusMessage = '打开沙箱文件失败';
        this.errorMessages.push('打开沙箱文件失败');
        return;
      }

      imageSource = image.createImageSource(file.fd);
      if (!imageSource) {
        Logger.error('Failed to create ImageSource');
        this.statusMessage = '创建ImageSource失败';
        this.errorMessages.push('创建ImageSource失败');
        return;
      }

      // 修改所有EXIF属性
      for (const entry of entries) {
        await imageSource.modifyImageProperty(entry.key.trim(), entry.value.trim());
        Logger.info(`Modified EXIF: ${entry.key} = ${entry.value}`);
      }

      Logger.info('EXIF modification completed, now creating asset request');

      // 创建新资产（文件名：原文件名_exif）
      const changeRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
        context,
        photoAccessHelper.PhotoType.IMAGE,
        'jpg',
        { title: newTitle }
      );

      changeRequest.addResource(photoAccessHelper.ResourceType.IMAGE_RESOURCE, this.sandboxPath);
      await phAccessHelper.applyChanges(changeRequest);

      Logger.info('Created new image asset with modified EXIF');

      // 清理临时文件
      fs.unlinkSync(this.sandboxPath);
      this.sandboxPath = '';

    } catch (error) {
      Logger.error(`Failed to save normal photo metadata: ${error}`);
      this.statusMessage = `保存失败: ${error}`;
      this.errorMessages.push(`保存普通照片元数据失败: ${error}`);
    } finally {
      // 释放资源（重要！）
      if (imageSource) {
        try {
          await imageSource.release();
          Logger.info('Released ImageSource');
        } catch (releaseError) {
          Logger.error(`Failed to release ImageSource: ${releaseError}`);
        }
      }

      if (file) {
        try {
          fs.closeSync(file);
          Logger.info('Closed file');
        } catch (closeError) {
          Logger.error(`Failed to close file: ${closeError}`);
        }
      }
    }
  }

  // 保存实况照片元数据
  private async saveMovingPhotoMetadata(
    entries: ExifEntry[],
    newTitle: string,
    phAccessHelper: photoAccessHelper.PhotoAccessHelper,
    context: common.UIAbilityContext
  ): Promise<void> {
    let staticFile: fs.File | null = null;
    let staticImageSource: image.ImageSource | null = null;

    try {
      // 修改静态图片EXIF
      staticFile = fs.openSync(this.staticPath, fs.OpenMode.READ_WRITE);
      if (!staticFile) {
        Logger.error('Failed to open static image file for EXIF modification');
        this.statusMessage = '打开静态图片文件失败';
        this.errorMessages.push('打开静态图片文件失败');
        return;
      }

      staticImageSource = image.createImageSource(staticFile.fd);
      if (!staticImageSource) {
        Logger.error('Failed to create ImageSource for static image');
        this.statusMessage = '创建ImageSource失败';
        this.errorMessages.push('创建ImageSource失败');
        return;
      }

      for (const entry of entries) {
        await staticImageSource.modifyImageProperty(entry.key.trim(), entry.value.trim());
        Logger.info(`Modified EXIF for static image: ${entry.key} = ${entry.value}`);
      }

      Logger.info('EXIF modification completed, now creating asset request');

      // 创建新的实况照片资产（文件名：原文件名_exif）
      const movingRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
        context,
        photoAccessHelper.PhotoType.IMAGE,
        'jpg',
        {
          title: newTitle,
          subtype: photoAccessHelper.PhotoSubtype.MOVING_PHOTO
        }
      );

      movingRequest.addResource(photoAccessHelper.ResourceType.IMAGE_RESOURCE, this.staticPath);
      movingRequest.addResource(photoAccessHelper.ResourceType.VIDEO_RESOURCE, this.videoPath);
      await phAccessHelper.applyChanges(movingRequest);

      Logger.info('Created new moving photo asset with modified EXIF');

      // 清理临时文件
      fs.unlinkSync(this.staticPath);
      fs.unlinkSync(this.videoPath);
      this.staticPath = '';
      this.videoPath = '';

    } catch (error) {
      Logger.error(`Failed to save moving photo metadata: ${error}`);
      this.statusMessage = `保存失败: ${error}`;
      this.errorMessages.push(`保存实况照片元数据失败: ${error}`);
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
  }
}

export default MetadataEditPage;