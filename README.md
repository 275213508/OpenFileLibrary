# OpenFileLibrary

#### 介绍

播放器部分ExoPlayer需要较高版本compileSdk, 故使用 https://github.com/CarGuo/GSYVideoPlayer

#### 软件架构

```
   
```

#### 使用说明

1. 在项目根目录的 `build.gradle` 文件中添加 JitPack 仓库：

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. 在应用模块的 `build.gradle` 文件中添加依赖：

```gradle
dependencies {
    implementation 'com.github.275213508:OpenFileLibrary:1.0.0'
}
```

或者，如果您使用的是新版本的 Gradle (在 `settings.gradle` 中配置仓库)：

1. 在 `settings.gradle` 文件中添加 JitPack 仓库：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

2. 在应用模块的 `build.gradle` 文件中添加依赖：

```gradle
dependencies {
    implementation 'com.github.275213508:OpenFileLibrary:1.0.0'
}
```

#### 手动集成方式

```
    //引用项目必须添加一下依赖
    复制 versions.gradle 到 project根目录
    model.build.gradle:根级添加 
    apply from: '../versions.gradle'
    dependencies {
      implementation deps.common//'com.wx.android.common:common:1.0.1'
      implementation deps.retrofit2//'com.squareup.retrofit2:retrofit:2.9.0'
      implementation(deps.okhttp3)
      implementation(deps.gson)//"com.google.code.gson:gson:2.10.1"
      //[通用弹窗 https://github.com/li-xiaojun/XPopup]
      implementation(deps.photoview)//"com.bm.photoview:library:1.4.1")
      implementation(deps.XPopup)//'com.github.li-xiaojun:XPopup:2.10.0')
      implementation files('libs/TbsFileSdk_base_armeabi__release_1.0.5.6000022.20230906113337.aar')
      implementation(openfilelibs.openfile)
      implementation files('libs/openfilelibrary-release.aar')
    }
 ```   

 ```   
#### 初始化
    //初始化:
    setTFBLicenseKey(key) //使用腾讯tfb播放需要,
    setFilePrivate(private) //使用第三方播放需要

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