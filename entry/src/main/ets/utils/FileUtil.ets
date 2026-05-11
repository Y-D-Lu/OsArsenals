import fs from '@ohos.file.fs';
import util from '@ohos.util';
import Logger from '../common/logger';

/**
 * 文件读取工具
 */
export class FileUtil {
  /**
   * 同步读取文件内容为字符串
   */
  static readString(path: string): string {
    let file: fs.File | null = null;

    try {
      file = fs.openSync(path, fs.OpenMode.READ_ONLY);
      if (!file) {
        Logger.error(`Failed to open file: ${path}`);
        return '';  // 文件打开失败返回空字符串，不抛出异常
      }

      const stat = fs.statSync(file.fd);
      const buffer = new ArrayBuffer(stat.size);
      fs.readSync(file.fd, buffer);

      const decoder = new util.TextDecoder('utf-8');
      return decoder.decodeToString(new Uint8Array(buffer)).trim();
    } catch (error) {
      Logger.error(`Failed to read ${path}: ${error}`);
      return '';  // 读取失败返回空字符串，不抛出异常
    } finally {
      // 释放资源（重要！）
      if (file) {
        try {
          fs.closeSync(file);
        } catch (closeError) {
          Logger.error(`Failed to close file: ${closeError}`);
        }
      }
    }
  }

  /**
   * 尝试读取文件内容，失败返回空字符串
   */
  static tryReadString(path: string): string {
    try {
      return FileUtil.readString(path);
    } catch (error) {
      return '';
    }
  }
}