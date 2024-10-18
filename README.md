# OpenFileLibrary

#### 介绍
播放器部分ExoPlayer需要较高版本compileSdk, 故使用 https://github.com/CarGuo/GSYVideoPlayer

#### 软件架构

```
   
```

#### 使用说明

```
    //引用项目必须添加一下依赖
    implementation(name: 'openfilelibrary-release', ext: 'aar')
    implementation("com.blankj:utilcodex:1.31.1")
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    implementation("com.github.getActivity:Toaster:12.6")

    implementation 'com.wx.android.common:common:1.0.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation("com.squareup.okhttp3:okhttp:3.12.13")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.recyclerview:recyclerview:1.2.1'

    //[通用弹窗 https://github.com/li-xiaojun/XPopup]
    implementation 'com.github.li-xiaojun:XPopup:2.10.0'


    implementation('com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-java:v8.3.5-release-jitpack') {
        exclude group: 'androidx.appcompat'
        exclude group: 'androidx.core'
    }
    implementation('com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-arm64:v8.3.5-release-jitpack') {
        exclude group: 'androidx.appcompat'
        exclude group: 'androidx.core'
    }

```

#### 使用混淆

```
    # [播放器部分 https://github.com/CarGuo/GSYVideoPlayer/blob/master/doc/QUESTION.md]
    -keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.shuyu.gsyvideoplayer.** { *; }
    -dontwarn com.shuyu.gsyvideoplayer.**

    -keep class tv.danmaku.ijk.media.player.** { *; }
    -keep class tv.danmaku.ijk.media.player.IjkMediaPlayer{ *; }
    -keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi{ *; }
```

#### 参与贡献

#### 仓库地址

#### 历史记录
```
    # 2024/05/27 替换播放器
```


//跳转前需要添加网络判断