package com.example.openfilelibrary.image;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;


import com.blankj.utilcode.util.ScreenUtils;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import com.example.openfilelibrary.R;

/**
 * 预览照片dialog
 */
public class PreImageDialog extends Dialog {


    private int currentIndex;
    private List<String> path;
    private Context context;

    private PreImageDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public PreImageDialog(Context context, List<String> imagePath, int currentIndex) {
        this(context, R.style.Dialog_NoTitleAndBackground);
        this.context = context;
        this.path = imagePath;
        this.currentIndex = currentIndex;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_preimg);
        setCancelable(true);
        this.setCanceledOnTouchOutside(true);
        //设置其大小
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = ScreenUtils.getScreenHeight();
        window.setAttributes(lp);
        initView();
    }


    private ViewPager vp_change_page;
    private TextView pts_page_title;//每一页标题
    private List<View> layoutList;//每一页的布局
    private List<String> titleList;//标题

    private void initView() {
        vp_change_page = findViewById(R.id.viewpage_preimage);
        pts_page_title = findViewById(R.id.tv_pre_count);
        pts_page_title.setText((currentIndex + 1) + "/" + path.size());
        if (path.size() == 1) {
            pts_page_title.setVisibility(View.INVISIBLE);
        }
        titleList = new ArrayList<String>();
        layoutList = new ArrayList<View>();
        //动态加载页面布局
        for (int i = 0; i < path.size(); i++) {
            View page1 = LayoutInflater.from(context).inflate(R.layout.dialog_img, null);
            PhotoView pre = page1.findViewById(R.id.phot);
            pre.enable();
            pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            Glide.with(context).load(path.get(i)).apply(new RequestOptions().error(R.drawable.errorimg)).into(pre);
            layoutList.add(page1);
            titleList.add(i + "/" + path.size());
        }
        PrePhotoViewPageAdapter adapter = new PrePhotoViewPageAdapter(layoutList);
        //设置适配器
        vp_change_page.setAdapter(adapter);
        vp_change_page.setCurrentItem(currentIndex);
        vp_change_page.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pts_page_title.setText((position + 1) + "/" + path.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
