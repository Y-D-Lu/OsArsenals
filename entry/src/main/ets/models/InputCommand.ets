/**
 * 输入命令数据模型
 */
export class InputCommand {
  action: string = '';       // 动作类型：tap, swipe, key
  params: string[] = [];     // 参数列表

  constructor(action: string, params: string[] = []) {
    this.action = action;
    this.params = params;
  }

  /**
   * 从字符串解析命令
   * 格式："action,param1,param2,..."
   */
  static fromString(input: string): InputCommand {
    const parts = input.split(',');
    const action = parts[0];
    const params = parts.slice(1);
    return new InputCommand(action, params);
  }
}