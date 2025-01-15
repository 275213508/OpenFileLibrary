package com.example.openfilelibrary.video

import android.graphics.Color
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.blankj.utilcode.util.LogUtils
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.databinding.ActivityOpenVideoPlayBinding
import com.example.openfilelibrary.utile.common.DownLoadUtile
import com.example.openfilelibrary.utile.common.SingleClick
import com.folioreader.mediaoverlay.MediaControllerCallbacks
import com.hjq.toast.Toaster
import java.io.File

class OpenVideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenVideoPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar)
        super.onCreate(savedInstanceState)
        binding = ActivityOpenVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SingleClick(binding.imgCancel){
            finish()
        }

        var urlpath = intent.getStringExtra("videoUrl")
        LogUtils.i("视频地址", urlpath)
        if (urlpath == null) {
            Toaster.show("视频地址为空")
            finish()
            return
        }
//        urlpath = "http://101.201.100.220:8080/gaefb/file/download?guid=3db6819f04164c749d7bb60af2aa1d39"
//        urlpath = "http://www.w3school.com.cn/i/movie.mp4"
        val guid = urlpath.toUri().getQueryParameter("guid")
        if (guid.isNullOrBlank()||urlpath.toUri().query?.startsWith("guid=")==false||urlpath.endsWith(".mp4")){
            openVideo(urlpath)
        }else {
            var filename = if(guid.isNullOrBlank()) urlpath.substring(urlpath.lastIndexOf("/")+1) else "$guid.mp4"
            DownLoadUtile.downloadFile(this, this.cacheDir.absolutePath, urlpath, filename, object : ICell<File> {
                override fun cell(cell: File) {
                    openVideo(cell.path)
                }
            })
        }

    }

    private fun openVideo(cell: String) {
        binding.apply {
            videoView.apply {
                val controller = MediaController(this@OpenVideoPlayActivity);
                setVideoPath(cell)
                setMediaController(controller)
                controller.setMediaPlayer(this)
            }
    //            videoView.setUrl("http://101.201.100.220:8080/gaefb/file/download?guid=3db6819f04164c749d7bb60af2aa1d39") //设置视频地址
    //            videoView.setPlayerFactory(IjkPlayerFactory.create());
    //
    //            controller.addDefaultControlComponent(File(urlpath).name, false)
    //            videoView.setVideoController(controller) //设置控制器
            videoView.start() //开始播放，不调用则不自动播放
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