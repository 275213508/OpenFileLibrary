package com.example.openfilelibrary.txt

import android.text.Html
import com.blankj.utilcode.util.FileIOUtils
import com.example.openfilelibrary.R
import com.example.openfilelibrary.databinding.TxtPlayerBinding
import com.example.openfilelibrary.base.BaseBottomSheetFrag
import com.example.openfilelibrary.base.ICell
import com.example.openfilelibrary.utile.ScreenUtils
import com.example.openfilelibrary.utile.common.DownLoadUtile
import java.io.File

/**
 * @author zyju
 * @date 2024/9/12 9:20
 * @param type 1:富文本
 */
internal class TxtPreView(var str: String, var type: Int?) : BaseBottomSheetFrag() {
    private lateinit var binding: TxtPlayerBinding

    override fun getLayoutHeight(): Int {
        return ScreenUtils.getScreenHeight(requireContext())
    }

    override fun getisDraggable(): Boolean {
        return false
    }

    override fun getLayoutResId(): Int {
        return R.layout.txt_player
    }

    override fun initView() {
        binding = TxtPlayerBinding.bind(rootView!!)
        when (type) {
            1 -> {//富文本
                binding.txt.text = Html.fromHtml(str)
            }

            else -> {
                binding.txt.text = str
            }
        }
    }
}