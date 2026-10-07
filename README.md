# 碰撞桌（Collider）

点子垃圾箱的 Android 版：随机抽取两个领域，用领域概念词库与句式模板碰撞出火花，本地存储、可标记、可查看。

## 功能
- 撞一个：随机双领域碰撞，火花入池
- 每日一撞：当天幂等
- 点子池：列表查看 + 标记（垃圾 / 有点意思 / 心动）
- 生命周期：7 天未标记自动变垃圾，标记后按 30 / 90 天倒计时，超期转死亡

## 数据
- 全部本地存储：`/data/data/io.github.aixtin.collider/files/sparks.json`
- 无后端、无网络请求、无数据收集

## 构建
```bash
gradle assembleDebug   # 输出 app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

引擎词库与模板移植自云端 `collider.py`，数据结构与 `sparks.json` 同构。
