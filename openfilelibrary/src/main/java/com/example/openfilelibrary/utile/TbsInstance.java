package com.example.openfilelibrary.utile;

import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.example.openfilelibrary.utile.common.CommonFunKt;
import com.example.openfilelibrary.utile.common.config;
import com.hjq.toast.Toaster;
import com.tencent.tbs.reader.ITbsReader;
import com.tencent.tbs.reader.ITbsReaderCallback;
import com.tencent.tbs.reader.TbsFileInterfaceImpl;
import com.wx.android.common.util.LogUtils;
import com.wx.android.common.util.SharedPreferencesUtils;


/**
 * 集成腾讯文件浏览服务
 * https://cloud.tencent.com/document/product/1645/83900
 *
 * @author CodeK 2023/10/16
 */
public class TbsInstance {


    private static volatile TbsInstance instance;

    public static TbsInstance getInstance() {
        if (instance == null) {
            synchronized (CommonFunKt.class) {
                if (instance == null)
                    instance = new TbsInstance();
            }
        }
        return instance;
    }

    public int initEngine(Context applicationContext) {
        String key =SPUtils.getInstance().getString(config.INSTANCE.getTbsLicenseKey());// getOpenFilePrivate(applicationContext);
        //设置licenseKey
        TbsFileInterfaceImpl.setLicenseKey(key);

        int ret = -1;

        //初始化Engine
        if (!TbsFileInterfaceImpl.isEngineLoaded()) {
            ret = TbsFileInterfaceImpl.initEngine(applicationContext);
        }

        return ret;
    }

    public void initX5Environment(Context applicationContext) {
        try {
            //异步初始化Engine
            //设置licenseKey
            //102419,102420,102421,102422
            //sanhu 环境下的licenseKey
            String key = SPUtils.getInstance().getString(config.INSTANCE.getTbsLicenseKey());
            TbsFileInterfaceImpl.setLicenseKey(key);
            ITbsReaderCallback callback = new ITbsReaderCallback() {
                @Override
                public void onCallBackAction(Integer actionType, Object args, Object result) {
                   // ITbsReader.OPEN_FILEREADER_ASYNC_LOAD_READER_ENTRY_CALLBACK 的值为 7002，不是错误码
                    if (ITbsReader.OPEN_FILEREADER_ASYNC_LOAD_READER_ENTRY_CALLBACK == actionType) {
                        int ret = (int) args; // 错误码为actionType == 7002时 args的值
                        if (ret == 0) {
                            // 初始化成功
                            LogUtils.e("腾讯TBS初始化成功");
                            SharedPreferencesUtils.init(applicationContext);
                            SharedPreferencesUtils.put(TBS, 1);
                        } else {
                            // 初始化失败
                            LogUtils.e("腾讯TBS初始化失败");
                            SharedPreferencesUtils.init(applicationContext);
                            SharedPreferencesUtils.put(TBS, 0);
                        }

                    }
                }
            };
            if (!TbsFileInterfaceImpl.isEngineLoaded()) {
                TbsFileInterfaceImpl.initEngineAsync(applicationContext, callback);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String TBS = "TBS";
}
