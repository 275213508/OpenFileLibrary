package com.example.openfilelibrary.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @author zyju
 * @date 2024/3/13 15:07
 */
internal abstract class BaseBottomSheetFrag : BottomSheetDialogFragment() {
    protected var mContext: Context? = null

    protected var rootView: View? = null
    protected var dialog: BottomSheetDialog? = null

    protected var mBehavior: BottomSheetBehavior<*>? = null
    private val mBottomSheetBehaviorCallback
            : BottomSheetBehavior.BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            //下拉关闭，
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
            //禁止拖拽，
            if (newState == BottomSheetBehavior.STATE_DRAGGING && !IsScrollable()) {
                // 设置为收缩状态
                mBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onStart() {
        super.onStart()
        mBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroy() {
        super.onDestroy()
        //解除缓存View和当前ViewGroup的关联
        val vi = (rootView?.parent as ViewGroup)
        vi.removeView(rootView)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //每次打开都调用该方法 类似于onCreateView 用于返回一个Dialog实例
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        //缓存下来的View 当为空时才需要初始化 并缓存
        rootView = View.inflate(mContext, getLayoutResId(), null)
        initView()
        resetView()
        //设置View重新关联
        dialog?.setContentView(rootView!!)
        mBehavior = BottomSheetBehavior.from(rootView?.parent as View)
        mBehavior?.setSkipCollapsed(true)
        mBehavior?.setHideable(true)
        mBehavior?.setDraggable(isDraggable)
        mBehavior?.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        //圆角边的关键(设置背景透明)
//        ((View) rootView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        //重置高度
        if (dialog != null) {
            val bottomSheet = dialog!!.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = getLayoutHeight()
            rootView?.post {
                mBehavior?.setPeekHeight(rootView?.height?:100)
                bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        return dialog!!
    }

    abstract fun getLayoutHeight(): Int

    abstract fun getLayoutResId(): Int
    open fun getisDraggable(): Boolean {
        return true
    };
    open val isDraggable: Boolean
        get() = true

    open fun IsScrollable(): Boolean {
        return true
    }

    /**
     * 初始化View和设置数据等操作的方法
     */
    abstract fun initView()

    /**
     * 重置的View和数据的空方法 子类可以选择实现
     * 为避免多次inflate 父类缓存rootView
     * 所以不会每次打开都调用[.initView]方法
     * 但是每次都会调用该方法 给子类能够重置View和数据
     */
    fun resetView() {
    }

    val isShowing: Boolean
        get() = dialog != null && dialog!!.isShowing

    /**
     * 使用关闭弹框 是否使用动画可选
     * 使用动画 同时切换界面Aty会卡顿 建议直接关闭
     *
     * @param isAnimation
     */
    fun close(isAnimation: Boolean) {
        if (isAnimation) {
            if (mBehavior != null) mBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            dismiss()
        }
    }


    open fun show(manager: FragmentManager) {
        super.show(manager, "")
    }
}
