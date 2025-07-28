package com.example.openfilelibrary.pdf

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.example.openfilelibrary.R
import com.example.openfilelibrary.base.BaseBottomSheetFrag
import com.example.openfilelibrary.databinding.PdfLayoutBinding
import com.example.openfilelibrary.utile.ScreenUtils
import com.example.openfilelibrary.utile.common.SingleClick
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnRenderListener

/**
 * @author zyju
 * @date 2024/8/27 10:23
 * @see 效果不如第三方的wps让用户下载个wps
 */
internal class PDFPreView(var FileLocalUri: Uri): BaseBottomSheetFrag() {

    private var TAG ="PDFPreView"
    private lateinit var bind: PdfLayoutBinding

    override fun getLayoutHeight(): Int {
        return ScreenUtils.getScreenHeight(mContext!!)
    }

    override fun getisDraggable(): Boolean {
        return false
    }

    override fun IsScrollable(): Boolean {
        return false
    }
    override fun getLayoutResId(): Int {
       return R.layout.pdf_layout
    }

    override fun initView() {
        bind = PdfLayoutBinding.bind(rootView!!)
        bind.tvTitle.text = FileUtils.getFileName(FileLocalUri.path)
        SingleClick(bind.imgCancel){
            dismiss()
        }
        LogUtils.i("打开PDF: $FileLocalUri")
        bind.pdfView.fromUri(FileLocalUri)
//            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
            .enableSwipe(true) // allows to block changing pages using swipe
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            // allows to draw something on the current page, usually visible in the middle of the screen
//            .onDraw(onDrawListener)
            // allows to draw something on all pages, separately for every page. Called only for visible pages
//            .onDrawAll(onDrawListener)
            .onLoad(object :OnLoadCompleteListener{
                override fun loadComplete(nbPages: Int) {
                    LogUtils.i(TAG, "loadComplete: "+nbPages)
                }

            }) // called after document is loaded and starts to be rendered
//            .onPageChange(onPageChangeListener)
//            .onPageScroll(onPageScrollListener)
            .onError(object :OnErrorListener{
                override fun onError(t: Throwable?) {
                    bind.loadProgress.visibility = android.view.View.GONE
                    LogUtils.i(TAG, "onError: ${t?.message}")
                }

            })
//            .onPageError(onPageErrorListener)
            .onRender(object :OnRenderListener{

                override fun onInitiallyRendered(nbPages: Int, pageWidth: Float, pageHeight: Float) {
                    bind.loadProgress.visibility = android.view.View.GONE
                    LogUtils.i(TAG, "onInitiallyRendered: $nbPages")
                }

            }) // called after document is rendered for the first time
            // called on single tap, return true if handled, false to toggle scroll handle visibility
//            .onTap(onTapListener)
//            .onLongPress(onLongPressListener)
            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
            .password(null)
            .scrollHandle(null)
            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
            // spacing between pages in dp. To define spacing color, set view background
            .spacing(0)
//            .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//            .linkHandler(DefaultLinkHandler)
//            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
//            .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
//            .pageSnap(false) // snap pages to screen boundaries
//            .pageFling(false) // make a fling change only a single page like ViewPager
//            .nightMode(false) // toggle night mode
            .load();
    }

    override fun show(manager: FragmentManager) {
        super.show(manager)
    }


}