package cn.eyz2021.japanesekana

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class TTSHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * 初始化：现在使用本地 Asset 资源，无需特殊初始化
     */
    fun initTTS(onResult: (Boolean) -> Unit) {
        onResult(true)
    }

    /**
     * 播放本地假名发音
     * 注意：请确保 dist 文件夹位于 app/src/main/assets/dist/
     * @param romaji 假名的罗马音，对应 dist 目录下的文件名
     */
    fun speakKana(romaji: String) {
        if (romaji.isBlank()) return

        // 映射逻辑：处理文件名不一致的情况
        val fileName = when (val r = romaji.lowercase()) {
            "n" -> "nn" // 'ん' 的文件名是 nn.mp3
            else -> r
        }

        val assetPath = "dist/${fileName}.mp3"

        try {
            // 停止并释放之前的播放器
            mediaPlayer?.stop()
            mediaPlayer?.release()

            // 从 Assets 中打开文件描述符
            val afd = context.assets.openFd(assetPath)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                
                setOnPreparedListener { it.start() }
                setOnCompletionListener { 
                    it.release()
                    if (mediaPlayer == it) mediaPlayer = null
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("TTSHelper", "MediaPlayer Error: $what, $extra for $assetPath")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("TTSHelper", "Error playing local asset: $assetPath", e)
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * 引导用户设置（本地模式下不再需要）
     */
    fun goToTtsSettings() {}
}
