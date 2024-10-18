package com.example.openfilelibrary.video

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.openfilelibrary.databinding.VideoPlayerBinding

/**
 * @author zyju
 * @date 2024/9/11 16:56
 */
internal class VideoPreView(var uri: Uri) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen)
    }

    private lateinit var binding: VideoPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VideoPlayerBinding.inflate(inflater, container, false)
        binding.playerControlView.initializePlayer(requireContext(), uri.toString()) {
            dismissAllowingStateLoss()
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.playerControlView.releasePlayer()
        super.onDismiss(dialog)
    }
}