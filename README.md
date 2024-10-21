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
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation("com.blankj:utilcodex:1.31.1")
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    implementation("com.github.getActivity:Toaster:12.6")
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
    
    # [https://github.com/DImuthuUpe/AndroidPdfViewer]
    -keep class com.shockwave.**
```

#### 参与贡献

#### 仓库地址

#### 历史记录
```
    # 2024/10/10 替换播放器
    # 2024/10/21 pdf打开本地文件兼容
```

#### 待做
```
    1.跳转前需要添加网络判断
    2.离线文件和在线地址添加判断
    3.尽可能的减少其他仓库的依赖
```
