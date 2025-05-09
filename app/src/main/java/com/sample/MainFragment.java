package com.sample;


import static com.sample.BuildConfig.APP_File_Provider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.openfilelibrary.OpenFileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment {

    public MainFragment() {
        this(R.layout.main_frag);
    }

    public MainFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    public static String[] titles = new String[]{
            "打开文本", "打开图片", "打开视频", "打开'民航航路图.pdf'", "打开'测试.doc'", "打开'VPN.txt'", "打开'测试.jpg'", "打开epub文件"
    };
    public static String[] fileUrls = new String[]{
            "https://dj-aers-gaefb.oss-cn-beijing.aliyuncs.com/gaefb_annex/20240923/1946ef14-be68-4c88-a4b7-591d74e1cfd8.txt",
            "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png",
            "https://dj-aers-gaefb.oss-cn-beijing.aliyuncs.com/gaefb_annex/20240925/a0166ee6-8f65-4643-ba33-3cd7a9597c0c.mp4",
//            Environment.getExternalStorageDirectory()+ "/efb/flight_data/953352877-1-30011.mp4",
            Environment.getExternalStorageDirectory() + "/storage/emulated/0/efb/flight_data/电子飞行图夹使用说明.pdf",
            Environment.getExternalStorageDirectory() + "/efb/flight_data/模块开发说明文档.docx",
            Environment.getExternalStorageDirectory() + "/efb/flight_data/text1.txt",
            Environment.getExternalStorageDirectory() + "/efb/测试.jpg",
            Environment.getExternalStorageDirectory() + "/efb/flight_data/三体3：死神永生.epub",
    };



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout linearLayout = view.findViewById(R.id.samples);
        linearLayout.addView(createLabel(""));
        for (String title : titles) {
            linearLayout.addView(createButton(title));
        }
        linearLayout.addView(createLabel(""));

//        OpenFileUtils.INSTANCE.setFilePrivate("com.efb.views.sanhu.fileprovider");
        OpenFileUtils.INSTANCE.setFilePrivate("com.sample.fileprovider");
        OpenFileUtils.INSTANCE.setIsSanHuApp(true);
        OpenFileUtils.INSTANCE.setTFBLicenseKey(requireContext(),"nGQic8OPFLleGnz7gW7Rpesab5bmjb2mzxeb9mqwC0t2W6YMn9tiOeTQq2dX83i7",-1);

    }

    private Button createButton(String text) {
        return this.createButton(text, null);
    }

    private Button createButton(String text, View.OnClickListener customListener) {
        Button button = new Button(requireContext());
        button.setAllCaps(false);
        button.setText(text);
        if (customListener == null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    /**打开毛玻璃弹窗*/
//                    BlurBehind.getInstance().execute(requireActivity(), () -> {
//                        Intent intent = new Intent(requireActivity(),MainActivity2. class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//                    });

                    List<String> titleList = Arrays.asList(titles);
                    int index = titleList.indexOf(text);
                    if (index == -1) return;
                    String path = fileUrls[index];
                    File file = new File(path);
                    boolean isex = file.exists();
                    OpenFileUtils.INSTANCE.openFile(requireActivity(), getContext().getFilesDir() + "/efb/flight_data/", path, null, APP_File_Provider);
                }
            });
        } else {
            button.setOnClickListener(customListener);
        }
        return button;
    }

    private TextView createLabel(String text) {
        TextView textView = new TextView(requireContext());
        textView.setGravity(Gravity.CENTER);
        if (text == null) {
            textView.setText("----------");
        } else {
            textView.setText(text);
        }
        return textView;
    }


}
