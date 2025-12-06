# Flash Text Explosion
### 一款安卓平台的文字爆闪应用，支持自定义文字、颜色、速度和エクスプロージョン……
![展示](assets/images/main_ui.png)

- 这里展示惠惠（めぐみん）仅为对角色的喜爱，从而选其为“代言人”，而非商业用途，若侵权请联系删除！
- Here, Megumin (めぐみん) is featured as a "representative" solely out of love for the character, not for commercial purposes. Should any infringement occur, please contact us for removal immediately!
- ここでめぐみん（Megumin）を掲載しているのは、キャラクターへの愛情からのみ「イメージキャラクター」として選んだもので、営利目的ではありません。万一著作権侵害に該当する場合、速やかに連絡して削除をお願いします！


## 功能亮点
### 基础文字自定义：
- 输入任意文字，调整字体大小（12-120sp）、字体颜色、背景颜色

### 多模式爆闪特效：
- 普通爆闪：自定义爆闪颜色组合、调节爆闪速度（50-2000ms）
- エクスプロージョン模式：多色快速切换特效与一些音乐

### 人性化交互：
   - 悬浮菜单自动隐藏（5秒无操作）
   - 点击空白处关闭菜单
   - 完整的日志输出，便于调试
   - 资源优化：音频预加载、资源自动释放，避免内存泄漏


## 运行环境
- Android Studio Flamingo | 2022.2.1 及以上
- Min SDK：API 24 (Android 7.0)
- Target SDK：API 34 (Android 14)
- 语言：Kotlin (兼容Java)


## 使用说明
### 编译运行：
- 克隆仓库到本地：
- 用Android Studio打开项目，等待Gradle同步完成
- 连接模拟器或真机，运行程序

### 基础操作：
- 点击右下角「菜单」按钮展开功能面板
- 文字修改：在输入框输入自定义文字，实时同步到主显示区
- 字体大小：拖动滑块调节，右侧实时显示当前字号

### 颜色设置：
- 点击「字体颜色」/「背景颜色」选择预设颜色
- 点击「选择爆闪颜色」可多选颜色，作为普通爆闪的切换色组

### 爆闪控制：
- 「爆闪开关」：开启/关闭所有爆闪特效
- 「爆闪速度」：拖动滑块调节切换间隔（数值越小，闪得越快）

### エクスプロージョン：
- 点击エクスプロージョン


## 核心资源说明
- 音频文件：res/raw/explosion_sound.mp3（エクスプロージョン模式的BGM，需确保文件存在）
- 布局文件：res/layout/activity_main.xml（核心UI布局，基于RelativeLayout+LinearLayout实现）
- 样式资源：res/drawable/（按钮圆角样式，需补充对应xml文件）
- 字符串资源：res/values/strings.xml（需补充以下核心字符串）：

```xml
<resources>
    <string name="app_name">爆裂魔法</string>
    <string name="default_text">Ciallo～(∠・ω&lt; )⌒★</string>
    <string name="font_size_format">%dsp</string>
    <string name="flash_speed_form9at">%dms</string>
    <string name="choose_font_color">选择字体颜色</string>
    <string name="choose_bg_color">选择背景颜色</string>
    <string name="choose_flash_colors">选择爆闪颜色</string>
    <string name="color_black">黑色</string>
    <string name="color_white">白色</string>
    <string name="color_red">红色</string>
    <string name="color_green">绿色</string>
    <string name="color_blue">蓝色</string>
    <string name="color_yellow">黄色</string>
    <string name="color_purple">紫色</string>
    <string name="color_cyan">青色</string>
    <string name="color_orange">橙色</string>
    <string name="confirm">确定</string>
    <string name="menu">菜单</string>
    <string name="input_hint">输入自定义文字</string>
    <string name="font_size_label">字体大小</string>
    <string name="initial_font_size">48sp</string>
    <string name="font_color_button">字体颜色</string>
    <string name="bg_color_button">背景颜色</string>
    <string name="flash_off">爆闪关</string>
    <string name="flash_on">爆闪开</string>
    <string name="select_flash_colors">选爆闪色</string>
    <string name="explosion_mode">「エクスプロージョン」</string>
    <string name="flash_speed_label">爆闪速度</string>
    <string name="initial_flash_speed">300ms</string>
    <string name="flash_speed_format">%dms</string>
</resources>
```

## 注意事项
- 爆炸模式闪屏速度较快，光敏性癫痫患者请勿使用
- 确保res/raw/explosion_sound.mp3音频文件存在，否则音效功能无法使用
- 应用退出时会自动释放MediaPlayer和动画资源，避免内存泄漏
- 适配横屏模式，暂未支持竖屏
