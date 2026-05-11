import photoAccessHelper from '@ohos.file.photoAccessHelper';
import media from '@ohos.multimedia.media';
import image from '@ohos.multimedia.image';
import fs from '@ohos.file.fs';
import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import Logger from '../common/logger';

@Entry
@Component
struct MovingPhotoPage {
  @State imageUri: string = '';
  @State videoUri: string = '';
  @State videoThumbnail: image.PixelMap | null = null;
  @State isGenerating: boolean = false;
  @State statusMessage: string = '';

  private videoController: VideoController = new VideoController();
  private tempFolder: string = '';  // 临时文件夹路径

  aboutToAppear(): void {
    Logger.info('MovingPhotoPage aboutToAppear');
    // 初始化并清理临时文件夹
    this.initTempFolder();
  }

  aboutToDisappear(): void {
    Logger.info('MovingPhotoPage aboutToDisappear');
    // 释放PixelMap资源
    if (this.videoThumbnail) {
      this.videoThumbnail = null;
    }
    // 清理整个临时文件夹
    this.cleanupTempFolder();
  }

  /**
   * 初始化临时文件夹并清理旧文件
   */
  private initTempFolder(): void {
    try {
      const context = getContext(this);
      if (!context) {
        Logger.error('Context is null, cannot init temp folder');
        return;
      }

      const cacheDir = context.cacheDir;
      if (!cacheDir) {
        Logger.error('CacheDir is null, cannot init temp folder');
        return;
      }

      // 创建专用临时文件夹
      this.tempFolder = `${cacheDir}/moving_photo_temp`;

      // 检查文件夹是否存在
      try {
        const stat = fs.statSync(this.tempFolder);
        Logger.info(`Temp folder exists: ${this.tempFolder}`);
        // 清理文件夹内所有内容
        this.cleanupTempFolder();
      } catch (error) {
        // 文件夹不存在，创建它
        Logger.info(`Temp folder not exists, creating: ${this.tempFolder}`);
        try {
          fs.mkdirSync(this.tempFolder);
          Logger.info(`Created temp folder: ${this.tempFolder}`);
        } catch (mkdirError) {
          Logger.error(`Failed to create temp folder: ${mkdirError}`);
        }
      }
    } catch (error) {
      Logger.error(`Failed to init temp folder: ${error}`);
    }
  }

  /**
   * 清理临时文件夹内所有内容
   */
  private cleanupTempFolder(): void {
    if (!this.tempFolder) {
      Logger.warn('Temp folder path is empty, cannot cleanup');
      return;
    }

    try {
      // 检查文件夹是否存在
      try {
        fs.statSync(this.tempFolder);
      } catch (error) {
        Logger.info(`Temp folder not exists, no need to cleanup`);
        return;
      }

      // 列出文件夹内所有文件
      const files = fs.listFileSync(this.tempFolder);
      Logger.info(`Found ${files.length} temp files to cleanup`);

      // 删除所有文件
      for (const fileName of files) {
        const filePath = `${this.tempFolder}/${fileName}`;
        try {
          fs.unlinkSync(filePath);
          Logger.info(`Deleted temp file: ${filePath}`);
        } catch (unlinkError) {
          Logger.error(`Failed to delete temp file ${filePath}: ${unlinkError}`);
        }
      }

      Logger.info(`Temp folder cleanup completed`);
    } catch (error) {
      Logger.error(`Failed to cleanup temp folder: ${error}`);
    }
  }

  build() {
    Column() {
      // 标题栏
      Row() {
        Text($r('app.string.moving_photo'))
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor($r('app.color.text_primary'))
      }
      .width('100%')
      .justifyContent(FlexAlign.Start)
      .padding({ left: 16, right: 16, top: 16, bottom: 16 })

      Scroll() {
        Column() {
          // 图片区域（上方）
          this.ImageSection()

          // 视频区域（下方）
          this.VideoSection()

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

          // 生成按钮区域
          Column() {
            Button($r('app.string.generate_moving_photo'))
              .width('100%')
              .height(40)
              .fontSize(14)
              .backgroundColor($r('app.color.primary'))
              .enabled(!this.isGenerating && this.videoUri !== '')
              .onClick(() => {
                this.generateMovingPhoto();
              })
          }
          .width('100%')
          .padding(16)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius($r('app.float.card_corner_radius'))
          .margin({ bottom: 16 })
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

  @Builder ImageSection() {
    Column() {
      Text($r('app.string.static_image_optional'))
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      // 图片显示
      Column() {
        if (this.imageUri || this.videoThumbnail) {
          Image(this.videoThumbnail || this.imageUri)
            .width('100%')
            .height(180)
            .objectFit(ImageFit.Contain)
            .borderRadius(8)
        } else {
          // 占位符
          Column() {
            Text('📷')
              .fontSize(40)
            Text('点击下方按钮添加图片')
              .fontSize(14)
              .fontColor($r('app.color.text_secondary'))
              .margin({ top: 8 })
          }
          .width('100%')
          .height(180)
          .justifyContent(FlexAlign.Center)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius(8)
        }
      }
      .width('100%')
      .margin({ bottom: 12 })

      // 添加图片按钮
      Button($r('app.string.select_image'))
        .width('100%')
        .height(40)
        .fontSize(14)
        .backgroundColor($r('app.color.primary'))
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

  @Builder VideoSection() {
    Column() {
      Text($r('app.string.dynamic_video_required'))
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .margin({ bottom: 12 })

      // 视频显示
      Column() {
        if (this.videoUri) {
          Video({ src: this.videoUri, controller: this.videoController })
            .width('100%')
            .height(180)
            .autoPlay(false)
            .controls(true)
            .objectFit(ImageFit.Contain)
            .borderRadius(8)
        } else {
          // 占位符
          Column() {
            Text('🎬')
              .fontSize(40)
            Text('点击下方按钮添加视频')
              .fontSize(14)
              .fontColor($r('app.color.text_secondary'))
              .margin({ top: 8 })
          }
          .width('100%')
          .height(180)
          .justifyContent(FlexAlign.Center)
          .backgroundColor($r('app.color.card_background'))
          .borderRadius(8)
        }
      }
      .width('100%')
      .margin({ bottom: 12 })

      // 添加视频按钮
      Button($r('app.string.select_video'))
        .width('100%')
        .height(40)
        .fontSize(14)
        .backgroundColor($r('app.color.primary'))
        .onClick(() => {
          this.selectVideo();
        })
    }
    .width('100%')
    .padding(16)
    .backgroundColor($r('app.color.card_background'))
    .borderRadius($r('app.float.card_corner_radius'))
  }

  // 选择图片
  private async selectImage(): Promise<void> {
    try {
      const photoPicker = new photoAccessHelper.PhotoViewPicker();
      if (!photoPicker) {
        Logger.error('PhotoPicker is null');
        this.statusMessage = '创建图片选择器失败';
        return;
      }

      const selectOption = new photoAccessHelper.PhotoSelectOptions();
      if (!selectOption) {
        Logger.error('PhotoSelectOptions is null');
        this.statusMessage = '创建选择选项失败';
        return;
      }

      selectOption.MIMEType = photoAccessHelper.PhotoViewMIMETypes.IMAGE_TYPE;
      selectOption.maxSelectNumber = 1;

      const result = await photoPicker.select(selectOption);

      if (!result) {
        Logger.error('PhotoPicker result is null');
        this.statusMessage = '选择结果为空';
        return;
      }

      if (!result.photoUris || result.photoUris.length === 0) {
        Logger.info('No image selected');
        return;
      }

      const uri = result.photoUris[0];
      if (!uri || uri === '') {
        Logger.error('Image URI is empty');
        this.statusMessage = '图片URI为空';
        return;
      }

      this.imageUri = uri;
      Logger.info(`Selected image: ${this.imageUri}`);
    } catch (error) {
      Logger.error(`Failed to select image: ${error}`);
      this.statusMessage = `选择图片失败: ${error}`;
    }
  }

  // 选择视频
  private async selectVideo(): Promise<void> {
    try {
      const photoPicker = new photoAccessHelper.PhotoViewPicker();
      if (!photoPicker) {
        Logger.error('PhotoPicker is null');
        this.statusMessage = '创建视频选择器失败';
        return;
      }

      const selectOption = new photoAccessHelper.PhotoSelectOptions();
      if (!selectOption) {
        Logger.error('PhotoSelectOptions is null');
        this.statusMessage = '创建选择选项失败';
        return;
      }

      selectOption.MIMEType = photoAccessHelper.PhotoViewMIMETypes.VIDEO_TYPE;
      selectOption.maxSelectNumber = 1;

      const result = await photoPicker.select(selectOption);

      if (!result) {
        Logger.error('PhotoPicker result is null');
        this.statusMessage = '选择结果为空';
        return;
      }

      if (!result.photoUris || result.photoUris.length === 0) {
        Logger.info('No video selected');
        return;
      }

      const uri = result.photoUris[0];
      if (!uri || uri === '') {
        Logger.error('Video URI is empty');
        this.statusMessage = '视频URI为空';
        return;
      }

      this.videoUri = uri;
      Logger.info(`Selected video: ${this.videoUri}`);

      // 如果没有选择图片，提取视频首帧
      if (!this.imageUri) {
        await this.extractFirstFrame();
      }
    } catch (error) {
      Logger.error(`Failed to select video: ${error}`);
      this.statusMessage = `选择视频失败: ${error}`;
    }
  }

  // 提取视频首帧
  private async extractFirstFrame(): Promise<void> {
    try {
      this.statusMessage = '正在提取视频首帧...';

      // 打开视频文件
      let file: fs.File | null = null;
      try {
        file = fs.openSync(this.videoUri, fs.OpenMode.READ_ONLY);
      } catch (openError) {
        Logger.error(`Failed to open video file: ${openError}`);
        this.statusMessage = '打开视频文件失败';
        return;
      }

      if (!file) {
        Logger.error('File object is null');
        this.statusMessage = '文件对象为空';
        return;
      }

      // 创建AVImageGenerator并设置源
      const avImageGenerator = await media.createAVImageGenerator();
      if (!avImageGenerator) {
        Logger.error('AVImageGenerator is null');
        fs.closeSync(file);
        this.statusMessage = '创建图像生成器失败';
        return;
      }

      avImageGenerator.fdSrc = { fd: file.fd };

      // 提取第一帧
      const pixelMap = await avImageGenerator.fetchFrameByTime(
        0,
        media.AVImageQueryOptions.AV_IMAGE_QUERY_NEXT_SYNC,
        {}
      );

      if (!pixelMap) {
        Logger.error('PixelMap from first frame is null');
        await avImageGenerator.release();
        fs.closeSync(file);
        this.statusMessage = '提取首帧失败：图像为空';
        return;
      }

      this.videoThumbnail = pixelMap;

      // 释放资源
      await avImageGenerator.release();
      fs.closeSync(file);

      Logger.info('Successfully extracted first frame');
      this.statusMessage = '已提取视频首帧作为静态图片';
    } catch (error) {
      Logger.error(`Failed to extract first frame: ${error}`);
      this.statusMessage = '提取首帧失败';
    }
  }

  // 将PixelMap保存为临时文件
  private async savePixelMapToTempFile(pixelMap: image.PixelMap): Promise<string> {
    try {
      if (!pixelMap) {
        Logger.error('PixelMap is null, cannot save to file');
        return '';
      }

      // 确保临时文件夹存在
      if (!this.tempFolder) {
        Logger.error('Temp folder is not initialized');
        return '';
      }

      // 生成临时文件名（使用专用临时文件夹）
      const tempFileName = `thumb_${Date.now()}.jpg`;
      const tempFilePath = `${this.tempFolder}/${tempFileName}`;

      Logger.info(`Saving PixelMap to: ${tempFilePath}`);

      // 将PixelMap写入文件
      const file = fs.openSync(tempFilePath, fs.OpenMode.CREATE | fs.OpenMode.WRITE_ONLY);
      if (!file) {
        Logger.error('Failed to open temp file for writing');
        return '';
      }

      const imagePackerApi = image.createImagePacker();
      if (!imagePackerApi) {
        Logger.error('ImagePacker is null');
        fs.closeSync(file);
        return '';
      }

      // 定义打包选项
      interface PackOptions {
        format: string;
        quality: number;
      }
      const packOpts: PackOptions = { format: 'image/jpeg', quality: 95 };

      const data = await imagePackerApi.packing(pixelMap, packOpts);
      if (!data) {
        Logger.error('Packed data is null');
        await imagePackerApi.release();
        fs.closeSync(file);
        return '';
      }

      fs.writeSync(file.fd, data);
      fs.closeSync(file);
      await imagePackerApi.release();

      Logger.info(`Saved PixelMap to temp file: ${tempFilePath}`);
      return tempFilePath;
    } catch (error) {
      Logger.error(`Failed to save PixelMap to temp file: ${error}`);
      return '';
    }
  }

  // 生成MOVING_PHOTO
  private async generateMovingPhoto(): Promise<void> {
    if (!this.videoUri) {
      this.statusMessage = '请先选择视频';
      return;
    }

    this.isGenerating = true;
    this.statusMessage = '正在生成实况照片...';

    try {
      const context = getContext(this);
      if (!context) {
        Logger.error('Context is null');
        this.statusMessage = '获取上下文失败';
        this.isGenerating = false;
        return;
      }

      const phAccessHelper = photoAccessHelper.getPhotoAccessHelper(context);
      if (!phAccessHelper) {
        Logger.error('PhotoAccessHelper is null');
        this.statusMessage = '获取相册管理器失败';
        this.isGenerating = false;
        return;
      }

      // 提取原始文件名并添加"_mp"后缀（实况照片）
      const sourceUri = this.imageUri || this.videoUri;
      const uriParts = sourceUri.split('/');
      const originalFileName = uriParts[uriParts.length - 1];
      const dotIndex = originalFileName.lastIndexOf('.');
      const baseName = dotIndex > 0 ? originalFileName.substring(0, dotIndex) : originalFileName;
      const newTitle = baseName + '_mp';

      Logger.info(`Original file: ${originalFileName}, New title: ${newTitle}`);

      // 创建MOVING_PHOTO资产请求（使用新文件名）
      const createRequest = photoAccessHelper.MediaAssetChangeRequest.createAssetRequest(
        context,
        photoAccessHelper.PhotoType.IMAGE,
        'jpg',
        {
          subtype: photoAccessHelper.PhotoSubtype.MOVING_PHOTO,
          title: newTitle
        }
      );

      if (!createRequest) {
        Logger.error('MediaAssetChangeRequest is null');
        this.statusMessage = '创建资产请求失败';
        this.isGenerating = false;
        return;
      }

      Logger.info('Created MOVING_PHOTO request');

      // 添加图片资源
      let imageResourceUri: string | null = null;
      let usedTempThumbnail = false;  // 标记是否使用了临时缩略图
      if (this.imageUri) {
        // 使用用户选择的图片
        imageResourceUri = this.imageUri;
      } else if (this.videoThumbnail) {
        // 将PixelMap保存为临时文件
        try {
          imageResourceUri = await this.savePixelMapToTempFile(this.videoThumbnail);
          usedTempThumbnail = true;  // 标记使用了临时文件
          Logger.info(`Using video thumbnail temp file: ${imageResourceUri}`);
        } catch (saveError) {
          Logger.error(`Failed to save thumbnail: ${saveError}`);
          this.statusMessage = '保存缩略图失败';
          this.isGenerating = false;
          return;
        }
      }

      if (imageResourceUri) {
        createRequest.addResource(
          photoAccessHelper.ResourceType.IMAGE_RESOURCE,
          imageResourceUri
        );
        Logger.info('Added image resource');
      }

      // 添加视频资源
      createRequest.addResource(
        photoAccessHelper.ResourceType.VIDEO_RESOURCE,
        this.videoUri
      );
      Logger.info('Added video resource');

      // 应用更改
      await phAccessHelper.applyChanges(createRequest);
      Logger.info('Successfully created MOVING_PHOTO');

      // 显示成功toast
      promptAction.showToast({
        message: '实况照片生成成功！',
        duration: 2000
      });

      // 清空状态，让用户可以选择新素材
      this.imageUri = '';
      this.videoUri = '';
      if (this.videoThumbnail) {
        this.videoThumbnail = null;
      }
      this.statusMessage = '';
      this.isGenerating = false;

      // 如果使用了临时缩略图，立即删除它
      if (usedTempThumbnail && imageResourceUri) {
        try {
          fs.unlinkSync(imageResourceUri);
          Logger.info(`Deleted temp thumbnail: ${imageResourceUri}`);
        } catch (unlinkError) {
          Logger.error(`Failed to delete temp thumbnail: ${unlinkError}`);
        }
      }

      Logger.info('MOVING_PHOTO created successfully, ready for new selection');
    } catch (error) {
      Logger.error(`Failed to generate MOVING_PHOTO: ${error}`);
      this.statusMessage = `生成失败: ${error}`;
      this.isGenerating = false;
    }
  }
}

export default MovingPhotoPage;