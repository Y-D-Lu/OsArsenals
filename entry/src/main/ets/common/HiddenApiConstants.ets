/**
 * HarmonyOS Hidden System API Constants
 *
 * Hidden API常量定义
 */

// Hidden API: 水印类型接口
export interface WatermarkTypeInterface {
  DEFAULT: number;
  BRAND_COMMON: number;
  COMMON: number;
  BRAND: number;
}

// Hidden API: 水印类型值
export const WatermarkType: WatermarkTypeInterface = {
  DEFAULT: 0,
  BRAND_COMMON: 1,
  COMMON: 2,
  BRAND: 3
};

// Hidden API: SUPPORTED_WATERMARK_TYPE属性名
export const SUPPORTED_WATERMARK_TYPE: string = 'supported_watermark_type';