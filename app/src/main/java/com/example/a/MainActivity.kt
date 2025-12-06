package com.example.com.example.a

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged

class MainActivity : AppCompatActivity() {
    private lateinit var tvMainDisplay: TextView
    private lateinit var etInputText: EditText
    private lateinit var sbFontSize: SeekBar
    private lateinit var tvFontSizeValue: TextView
    private lateinit var btnFontColor: Button
    private lateinit var btnBgColor: Button
    private lateinit var tbFlashToggle: ToggleButton
    private lateinit var btnSelectFlashColors: Button
    private lateinit var btnExplosionMode: Button
    private lateinit var sbFlashSpeed: SeekBar
    private lateinit var tvFlashSpeedValue: TextView
    private lateinit var btnMenu: Button
    private lateinit var llMenu: LinearLayout

    private var fontSize = 48f
    private var fontColor = Color.WHITE
    private var bgColor = Color.BLACK
    private var displayText = ""
    private var flashInterval = 300L
    private val customFlashColors = mutableListOf(Color.BLACK, Color.WHITE)
    private var flashAnimator: ValueAnimator? = null
    private var explosionHandler: Handler? = null
    private var explosionRunnable: Runnable? = null
    private var isExplosionMode = false
    private val menuHandler = Handler(Looper.getMainLooper())
    private val hideMenuRunnable = Runnable { llMenu.visibility = View.GONE }

    // 添加 MediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "FlashApp"

    private val explosionColors = listOf(
        Color.RED to Color.GREEN,
        Color.GREEN to Color.RED,
        Color.BLUE to Color.YELLOW,
        Color.YELLOW to Color.BLUE,
        Color.MAGENTA to Color.GREEN,
        Color.CYAN to Color.RED,
        Color.parseColor("#FFA500") to Color.parseColor("#005AFF"),
        Color.parseColor("#FFC0CB") to Color.parseColor("#003F34"),
        Color.parseColor("#800080") to Color.parseColor("#7FFF80"),
        Color.parseColor("#00FFFF") to Color.parseColor("#FF0000"),
        Color.parseColor("#FF00FF") to Color.parseColor("#00FF00"),
        Color.parseColor("#0000FF") to Color.parseColor("#FFFF00"),
        Color.parseColor("#008000") to Color.parseColor("#FF7FFF"),
        Color.parseColor("#FFFF00") to Color.parseColor("#0000FF"),
        Color.parseColor("#808080") to Color.parseColor("#7F7F7F"),
        Color.parseColor("#FF4500") to Color.parseColor("#00BAFF"),
        Color.parseColor("#9ACD32") to Color.parseColor("#6532CD"),
        Color.parseColor("#4169E1") to Color.parseColor("#BE961F"),
        Color.BLACK to Color.WHITE,
        Color.WHITE to Color.BLACK
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "应用启动")
        initViews()
        initDefaultValues()
        setupListeners()

        // 预加载 MediaPlayer
        initializeMediaPlayer()
    }

    private fun initViews() {
        tvMainDisplay = findViewById(R.id.tvMainDisplay)
        etInputText = findViewById(R.id.etInputText)
        sbFontSize = findViewById(R.id.sbFontSize)
        tvFontSizeValue = findViewById(R.id.tvFontSizeValue)
        btnFontColor = findViewById(R.id.btnFontColor)
        btnBgColor = findViewById(R.id.btnBgColor)
        tbFlashToggle = findViewById(R.id.tbFlashToggle)
        btnSelectFlashColors = findViewById(R.id.btnSelectFlashColors)
        btnExplosionMode = findViewById(R.id.btnExplosionMode)
        sbFlashSpeed = findViewById(R.id.sbFlashSpeed)
        tvFlashSpeedValue = findViewById(R.id.tvFlashSpeedValue)
        btnMenu = findViewById(R.id.btnMenu)
        llMenu = findViewById(R.id.llMenu)
        Log.d(TAG, "视图初始化完成")
    }

    private fun initDefaultValues() {
        displayText = getString(R.string.default_text)
        tvMainDisplay.text = displayText
        tvMainDisplay.textSize = fontSize
        tvMainDisplay.setTextColor(fontColor)
        tvMainDisplay.setBackgroundColor(bgColor)
        tvFontSizeValue.text = getString(R.string.font_size_format, fontSize.toInt())
        tvFlashSpeedValue.text = getString(R.string.flash_speed_format, flashInterval)
        Log.d(TAG, "默认值初始化完成")
    }

    private fun setupListeners() {
        btnMenu.setOnClickListener {
            llMenu.visibility = if (llMenu.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            resetMenuTimer()
            Log.d(TAG, "菜单按钮点击，菜单状态: ${llMenu.visibility}")
        }

        etInputText.doAfterTextChanged { text ->
            displayText = text?.toString()?.trim() ?: getString(R.string.default_text)
            tvMainDisplay.text = displayText
            Log.d(TAG, "文本更新: $displayText")
        }

        sbFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fontSize = progress.coerceAtLeast(12).toFloat()
                tvMainDisplay.textSize = fontSize
                tvFontSizeValue.text = getString(R.string.font_size_format, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnFontColor.setOnClickListener {
            showSingleColorPicker(getString(R.string.choose_font_color)) { color ->
                fontColor = color
                if (!tbFlashToggle.isChecked) tvMainDisplay.setTextColor(color)
                resetMenuTimer()
                Log.d(TAG, "字体颜色选择: $color")
            }
        }

        btnBgColor.setOnClickListener {
            showSingleColorPicker(getString(R.string.choose_bg_color)) { color ->
                bgColor = color
                if (!tbFlashToggle.isChecked) tvMainDisplay.setBackgroundColor(color)
                resetMenuTimer()
                Log.d(TAG, "背景颜色选择: $color")
            }
        }

        btnSelectFlashColors.setOnClickListener {
            showMultiColorPicker(getString(R.string.choose_flash_colors)) { colors ->
                if (colors.isNotEmpty()) {
                    customFlashColors.clear()
                    customFlashColors.addAll(colors)
                    if (tbFlashToggle.isChecked && !isExplosionMode) {
                        restartFlashAnimation()
                    }
                }
                resetMenuTimer()
                Log.d(TAG, "爆闪颜色选择: $colors")
            }
        }

        btnExplosionMode.setOnClickListener {
            isExplosionMode = true
            tbFlashToggle.isChecked = true
            startFlashAnimation()
            resetMenuTimer()
            Log.d(TAG, "エクスプロージョン模式启动")
        }

        sbFlashSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                flashInterval = progress.coerceAtLeast(50).toLong()
                tvFlashSpeedValue.text = getString(R.string.flash_speed_format, flashInterval)
                if (tbFlashToggle.isChecked && !isExplosionMode) restartFlashAnimation()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        tbFlashToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!isExplosionMode && customFlashColors.isEmpty()) {
                    customFlashColors.addAll(listOf(Color.BLACK, Color.WHITE))
                }
                startFlashAnimation()
                Log.d(TAG, "爆闪开启，模式: ${if (isExplosionMode) "エクスプロージョン" else "普通"}")
            } else {
                isExplosionMode = false
                stopFlashAnimation()
                restoreNormalUI()
                Log.d(TAG, "爆闪关闭")
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && llMenu.visibility == View.VISIBLE) {
            val menuRect = android.graphics.Rect()
            llMenu.getGlobalVisibleRect(menuRect)
            val btnRect = android.graphics.Rect()
            btnMenu.getGlobalVisibleRect(btnRect)
            if (!menuRect.contains(ev.rawX.toInt(), ev.rawY.toInt()) && !btnRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                llMenu.visibility = View.GONE
                Log.d(TAG, "点击外部，菜单关闭")
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun resetMenuTimer() {
        menuHandler.removeCallbacks(hideMenuRunnable)
        if (llMenu.visibility == View.VISIBLE) {
            menuHandler.postDelayed(hideMenuRunnable, 5000)
            Log.d(TAG, "菜单计时器重置")
        }
    }

    private fun initializeMediaPlayer() {
        try {
            Log.d(TAG, "开始初始化MediaPlayer")

            // 先检查资源是否存在
            val resId = resources.getIdentifier("explosion_sound", "raw", packageName)
            if (resId == 0) {
                Log.e(TAG, "音频文件未找到: explosion_sound.mp3")
                Toast.makeText(this, "音频文件未找到", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d(TAG, "音频资源ID: $resId")

            mediaPlayer = MediaPlayer.create(this, resId)
            if (mediaPlayer == null) {
                Log.e(TAG, "MediaPlayer创建失败")
                Toast.makeText(this, "音频播放器创建失败", Toast.LENGTH_SHORT).show()
                return
            }

            // 设置音量
            mediaPlayer?.setVolume(1.0f, 1.0f)
            Log.d(TAG, "音量设置为最大")

            // 设置循环播放
            mediaPlayer?.isLooping = true
            Log.d(TAG, "循环播放已设置")

            // 添加错误监听器
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                Log.e(TAG, "MediaPlayer错误 - what: $what, extra: $extra")
                Toast.makeText(this, "音频播放错误: $what", Toast.LENGTH_SHORT).show()
                false
            }

            // 添加准备完成监听器
            mediaPlayer?.setOnPreparedListener {
                Log.d(TAG, "MediaPlayer准备完成")
            }

            // 添加播放完成监听器
            mediaPlayer?.setOnCompletionListener {
                Log.d(TAG, "MediaPlayer播放完成")
            }

            Log.d(TAG, "MediaPlayer初始化成功")
            Toast.makeText(this, "音频加载成功", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "MediaPlayer初始化异常: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "音频初始化异常: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun startFlashAnimation() {
        stopFlashAnimation()

        if (isExplosionMode) {
            Log.d(TAG, "启动エクスプロージョン模式动画")

            // 开始播放音频
            startAudioPlayback()

            // 使用Handler而不是ValueAnimator来避免渐变效果
            explosionHandler = Handler(Looper.getMainLooper())
            var currentIndex = 0

            explosionRunnable = object : Runnable {
                override fun run() {
                    if (isExplosionMode && tbFlashToggle.isChecked) {
                        val (bgColor, textColor) = explosionColors[currentIndex]
                        tvMainDisplay.setBackgroundColor(bgColor)
                        tvMainDisplay.setTextColor(textColor)

                        currentIndex = (currentIndex + 1) % explosionColors.size
                        explosionHandler?.postDelayed(this, 50)
                    }
                }
            }
            explosionRunnable?.run()
            Log.d(TAG, "エクスプロージョン动画已启动")
        } else {
            Log.d(TAG, "启动普通爆闪模式动画")

            // 普通爆闪模式仍然使用ValueAnimator
            val colors = if (customFlashColors.isEmpty()) listOf(Color.BLACK, Color.WHITE) else customFlashColors
            flashAnimator = ValueAnimator.ofInt(0, colors.size).apply {
                duration = flashInterval * colors.size
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener { anim ->
                    val index = (anim.animatedValue as Int) % colors.size
                    val color = colors[index]
                    tvMainDisplay.setBackgroundColor(color)
                    tvMainDisplay.setTextColor(fontColor)
                }
                start()
            }
            Log.d(TAG, "普通爆闪动画已启动，颜色数量: ${colors.size}")
        }
    }

    private fun startAudioPlayback() {
        try {
            Log.d(TAG, "尝试播放音频")

            if (mediaPlayer == null) {
                Log.w(TAG, "MediaPlayer为null，重新初始化")
                initializeMediaPlayer()
            }

            if (mediaPlayer != null) {
                // 确保音量最大
                mediaPlayer?.setVolume(1.0f, 1.0f)

                // 检查是否正在播放
                if (mediaPlayer?.isPlaying == true) {
                    Log.d(TAG, "音频已在播放中")
                } else {
                    mediaPlayer?.start()
                    Log.d(TAG, "音频开始播放")
                }

                Toast.makeText(this, "エクスプロージョン！", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "MediaPlayer仍然为null，无法播放")
                Toast.makeText(this, "音频播放器未就绪", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "音频播放异常: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "音频播放异常: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopAudioPlayback() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                Log.d(TAG, "音频已暂停")
            } else {
                Log.d(TAG, "音频未在播放，无需暂停")
            }
        } catch (e: Exception) {
            Log.e(TAG, "停止音频播放异常: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun restartFlashAnimation() {
        if (tbFlashToggle.isChecked) {
            Log.d(TAG, "重新启动爆闪动画")
            stopFlashAnimation()
            startFlashAnimation()
        }
    }

    private fun stopFlashAnimation() {
        Log.d(TAG, "停止爆闪动画")

        flashAnimator?.cancel()
        flashAnimator = null

        explosionHandler?.removeCallbacksAndMessages(null)
        explosionHandler = null
        explosionRunnable = null

        // 停止音频播放
        stopAudioPlayback()
    }

    private fun restoreNormalUI() {
        tvMainDisplay.text = displayText
        tvMainDisplay.textSize = fontSize
        tvMainDisplay.setTextColor(fontColor)
        tvMainDisplay.setBackgroundColor(bgColor)
        Log.d(TAG, "UI恢复正常状态")
    }

    private fun showSingleColorPicker(title: String, onSelect: (Int) -> Unit) {
        val colorOptions = listOf(
            getString(R.string.color_black) to Color.BLACK,
            getString(R.string.color_white) to Color.WHITE,
            getString(R.string.color_red) to Color.RED,
            getString(R.string.color_green) to Color.GREEN,
            getString(R.string.color_blue) to Color.BLUE,
            getString(R.string.color_yellow) to Color.YELLOW,
            getString(R.string.color_purple) to Color.MAGENTA,
            getString(R.string.color_cyan) to Color.CYAN,
            getString(R.string.color_orange) to Color.parseColor("#FFA500")
        )
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(colorOptions.map { it.first }.toTypedArray()) { _, index ->
                onSelect(colorOptions[index].second)
            }
            .setOnDismissListener { resetMenuTimer() }
            .show()
    }

    private fun showMultiColorPicker(title: String, onSelect: (List<Int>) -> Unit) {
        val colorOptions = listOf(
            getString(R.string.color_black) to Color.BLACK,
            getString(R.string.color_white) to Color.WHITE,
            getString(R.string.color_red) to Color.RED,
            getString(R.string.color_green) to Color.GREEN,
            getString(R.string.color_blue) to Color.BLUE,
            getString(R.string.color_yellow) to Color.YELLOW,
            getString(R.string.color_purple) to Color.MAGENTA,
            getString(R.string.color_cyan) to Color.CYAN,
            getString(R.string.color_orange) to Color.parseColor("#FFA500")
        )
        val checkedStatus = BooleanArray(colorOptions.size) { i ->
            customFlashColors.contains(colorOptions[i].second)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMultiChoiceItems(colorOptions.map { it.first }.toTypedArray(), checkedStatus) { _, index, isChecked ->
                checkedStatus[index] = isChecked
            }
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                val selected = mutableListOf<Int>()
                checkedStatus.forEachIndexed { index, isChecked ->
                    if (isChecked) selected.add(colorOptions[index].second)
                }
                onSelect(selected)
            }
            .setOnDismissListener { resetMenuTimer() }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "应用销毁，释放资源")
        stopFlashAnimation()
        menuHandler.removeCallbacksAndMessages(null)

        // 释放 MediaPlayer 资源
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d(TAG, "资源释放完成")
    }
}