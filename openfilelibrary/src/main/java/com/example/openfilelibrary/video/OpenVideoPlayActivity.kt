package com.example.openfilelibrary.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.blankj.utilcode.util.LogUtils
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.databinding.ActivityOpenVideoPlayBinding
import com.example.openfilelibrary.utile.common.DownLoadUtile
import com.example.openfilelibrary.utile.common.SingleClick
import com.example.openfilelibrary.video.view.PlayViews
import com.hjq.toast.Toaster
import java.io.File

class OpenVideoPlayActivity : AppCompatActivity() {
    private var playViews: PlayViews?=null
    private lateinit var binding: ActivityOpenVideoPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar)
        super.onCreate(savedInstanceState)
        binding = ActivityOpenVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SingleClick(binding.imgCancel) {
            finish()
        }

        var urlpath = intent.getStringExtra("videoUrl")
        var fileName = intent.getStringExtra("fileName")
        LogUtils.i("地址", urlpath)
        if (urlpath == null) {
            Toaster.show("访问地址为空")
            finish()
            return
        }
//        urlpath = "http://101.201.100.220:8080/gaefb/file/download?guid=3db6819f04164c749d7bb60af2aa1d39"
//        urlpath = "http://www.w3school.com.cn/i/movie.mp4"

        fileName?.let {
            if (it.endsWith(".mp3")) {
                binding.tvMusiceName.text = it
                binding.tvMusiceName.visibility = View.VISIBLE
            }
        }
        val guid = urlpath.toUri().getQueryParameter("guid")
        if (guid.isNullOrBlank() || urlpath.toUri().query?.startsWith("guid=") == false || urlpath.endsWith(".mp4")) {
            openVideo(urlpath)
        } else {
            var filename = if (guid.isBlank()) urlpath.substring(urlpath.lastIndexOf("/") + 1) else "$guid.mp4"
            DownLoadUtile.downloadFile(this, this.cacheDir.absolutePath, urlpath, filename, object : ICell<File> {
                override fun cell(cell: File) {
                    openVideo(cell.path)
                }
            })
        }
        SingleClick(binding.imgCancel) {
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                finish()
            }
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        playViews?.refreshFullScreenBt()
    }

    private fun openVideo(cell: String) {
        binding.apply {
            playViews = PlayViews(this@OpenVideoPlayActivity, llMediaController, videoView)
            playViews?.setIsShowChengeListener(object : ICell<Boolean> {
                override fun cell(cell: Boolean) {
                   binding.imgCancel.visibility = if (cell) View.VISIBLE else View.GONE
                }
            })
            videoView.apply {
//                val controller = MediaController(this@OpenVideoPlayActivity);
                setVideoPath(cell)
//                setMediaController(controller)
//                controller.setMediaPlayer(this)
                setOnPreparedListener {
                    playViews?.play()
                }
                start() //开始播放，不调用则不自动播放
            }

            //            videoView.setUrl("http://101.201.100.220:8080/gaefb/file/download?guid=3db6819f04164c749d7bb60af2aa1d39") //设置视频地址
            //            videoView.setPlayerFactory(IjkPlayerFactory.create());
            //
            //            controller.addDefaultControlComponent(File(urlpath).name, false)
            //            videoView.setVideoController(controller) //设置控制器
            //            videoView.start()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.videoView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}