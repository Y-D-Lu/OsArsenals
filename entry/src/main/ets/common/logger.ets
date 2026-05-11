import { hilog } from '@kit.PerformanceAnalysisKit';
import { Constants } from './constants';

/**
 * 日志工具类
 */
export class Logger {
  static debug(message: string): void {
    hilog.debug(Constants.LOG_DOMAIN, Constants.LOG_TAG, message);
  }

  static info(message: string): void {
    hilog.info(Constants.LOG_DOMAIN, Constants.LOG_TAG, message);
  }

  static warn(message: string): void {
    hilog.warn(Constants.LOG_DOMAIN, Constants.LOG_TAG, message);
  }

  static error(message: string): void {
    hilog.error(Constants.LOG_DOMAIN, Constants.LOG_TAG, message);
  }

  static fatal(message: string): void {
    hilog.fatal(Constants.LOG_DOMAIN, Constants.LOG_TAG, message);
  }
}

export default Logger;