package com.example.openfilelibrary.tbs

import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.example.openfilelibrary.OpenFileUtils
import com.example.openfilelibrary.R
import com.example.openfilelibrary.base.BaseBottomSheetFrag
import com.example.openfilelibrary.databinding.TbsLayoutBinding
import com.example.openfilelibrary.utile.ScreenUtils
import com.example.openfilelibrary.utile.TbsInstance
import com.example.openfilelibrary.utile.common.FileType
import com.example.openfilelibrary.utile.common.SingleClick
import com.example.openfilelibrary.utile.common.getSuffixName1
import com.hjq.toast.Toaster
import com.tencent.tbs.reader.ITbsReader
import com.tencent.tbs.reader.ITbsReaderCallback
import com.tencent.tbs.reader.TbsFileInterfaceImpl
import java.io.File


/**
 * @author zyju
 * @date 2024/8/27 10:23
 */
internal class TBSPreView(var FileLocalUri: Uri,var APP_File_Provider:String): BaseBottomSheetFrag() {

    private var TAG ="TbsPreView"
    private lateinit var bind: TbsLayoutBinding
    private var filePath = ""

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
       return R.layout.tbs_layout
    }

    override fun initView() {
        bind = TbsLayoutBinding.bind(rootView!!)
        SingleClick(bind.imgCancel){
            dismiss()
        }
        LogUtils.i("打开PDF: $FileLocalUri")
        openTbsFile(requireActivity(), FileLocalUri.path!!, getSuffixName1(FileLocalUri.path!!))
    }

    override fun show(manager: FragmentManager) {
        super.show(manager)
    }

    private fun openTbsFile(context: FragmentActivity, filePath: String, fileExt: String) {
        //判断是否初始化成功
        if (SPUtils.getInstance().getInt(TbsInstance.TBS, 0) == 0) {
            if (TbsInstance.getInstance().initEngine(context) != 0) {
                //再次失败跳转第三方
                startIntent(context,fileExt, filePath)
                SPUtils.getInstance().put(TbsInstance.TBS, 0)
                return
            } else {
                SPUtils.getInstance().put(TbsInstance.TBS, 1)
            }
        }
        //3、设置回调
        val callback: ITbsReaderCallback = object : ITbsReaderCallback {
            override fun onCallBackAction(actionType: Int, args: Any?, result: Any?) {
                if (ITbsReader.OPEN_FILEREADER_STATUS_UI_CALLBACK == actionType) {
                    if (args is Bundle) {
                        val id = args.getInt("typeId")
                        if (ITbsReader.TBS_READER_TYPE_STATUS_UI_SHUTDOWN == id) {
                            context.finish() //关闭fileReader
                        }
                    }
                }
            }
        }
        if (TbsFileInterfaceImpl.canOpenFileExt(fileExt)) {
            //4、设置参数
            val param = Bundle()
            param.putString("filePath", filePath)
            param.putString("fileExt", fileExt)
            param.putBoolean("file_reader_is_ppt_page_mode_default", true)
            param.putBoolean("file_reader_goto_last_pos", true)
            param.putBoolean("file_reader_enable_long_press_menu", true)
            param.putString("tempPath", context.getExternalFilesDir("temp")?.getAbsolutePath())

            //        param.putString("file_reader_content_bg_color", "#FFC0CB");
            //        param.putString("file_reader_content_bg_color", "#FFC0CB");

            bind.tbsView.post(Runnable {
                val height: Int = bind.tbsView.getHeight()
                param.putInt("set_content_view_height", height) // 文档内容的显示区域高度，自定义layout模式必须传入这个值
                val ret = TbsFileInterfaceImpl.getInstance().openFileReader(
                   context, param, callback, bind.tbsView
                )
            })
        } else {
            //腾讯TBS不支持此格式
            Toaster.show("不支持的文档格式")
        }
    }
    private fun startIntent(context: FragmentActivity,fileType: String, filePath: String) {
        if (APP_File_Provider.isBlank()){
            APP_File_Provider = context.packageName + ".fileprovider"
        }
        if (fileType == FileType.EPUB.name.lowercase()&&File(filePath).exists()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val apkUri = FileProvider.getUriForFile(context, APP_File_Provider, File(filePath))
                    intent.setDataAndType(apkUri, "application/epub+zip")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    intent.setDataAndType(Uri.fromFile(File(filePath)), "application/epub+zip")
                }
                if (TBSUtile.isAppInstalled(context, "com.huawei.hwread.al")) {
                    intent.setPackage("com.huawei.hwread.al")
                } else if (TBSUtile.isAppInstalled(context, "com.huawei.hwread")) {
                    intent.setPackage("com.huawei.hwread")
                } else if (TBSUtile.isAppInstalled(context, "com.duokan.reader")) {
                    intent.setPackage("com.duokan.reader")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                com.wx.android.common.util.LogUtils.e("feifei=======打开: ${e.message}")
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            com.wx.android.common.util.LogUtils.e("ShowPdfActivity--buildTbsReaderView: fileType:$fileType")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(context, APP_File_Provider, File(filePath))
                intent.setDataAndType(apkUri, OpenFileUtils.getFileIntentType(fileType))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                intent.setDataAndType(Uri.fromFile(File(filePath)), OpenFileUtils.getFileIntentType(fileType))
            }
            try {
                this.startActivity(intent)
            } catch (e: Exception) {
                Toaster.show("未安装可打开此文件的应用")
            }
        }
        dismiss()
    }

}