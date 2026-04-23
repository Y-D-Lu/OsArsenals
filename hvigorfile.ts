import { appTasks } from '@ohos/hvigor-ohos-plugin';
import { HvigorPlugin, HvigorNode, Task } from 'hvigor';
import * as path from 'path';
import * as fs from 'fs';
import { execSync } from 'child_process';

/**
 * 执行系统签名
 */
function runSystemSign(): void {
  const projectRoot = process.cwd();
  const signDir = path.join(projectRoot, 'hw_sign');
  const signScript = path.join(signDir, 'sign.js');

  if (!fs.existsSync(signScript)) {
    console.log('[SystemSign] hw_sign/sign.js 不存在，跳过系统签名');
    return;
  }

  console.log('[SystemSign] 开始执行系统签名...');
  try {
    execSync(`node "${signScript}"`, {
      cwd: projectRoot,
      stdio: 'inherit'
    });
    console.log('[SystemSign] 系统签名完成');
  } catch (error) {
    console.error('[SystemSign] 系统签名失败');
    throw error;
  }
}

/**
 * 系统签名插件
 */
const systemSignPlugin: HvigorPlugin = {
  pluginId: 'SystemSignPlugin',
  apply: (node: HvigorNode) => {
    // 在 entry 模块的 assembleHap 任务后添加签名 hook
    const entryNode = node.getSubNodeByName('entry');
    if (entryNode) {
      entryNode.afterNodeEvaluate((n: HvigorNode) => {
        const assembleHapTask = n.getTaskByName('assembleHap') as Task;
        if (assembleHapTask) {
          assembleHapTask.afterRun(() => {
            runSystemSign();
          });
          console.log('[SystemSignPlugin] 已在 assembleHap 后注册签名 hook');
        }
      });
    }
  }
};

export default {
  system: appTasks,
  plugins: [systemSignPlugin]
}