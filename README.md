# MediaPickerProject

仿微信视频图片、视频选择器，同时可直接拍摄照片、视频。


项目fork自[DmcSDK/MediaPickerPoject](https://github.com/DmcSDK/MediaPickerPoject)，感谢作者。

并结合了[TakePhotoVideoLib](https://github.com/HyfSunshine/TakePhotoVideoLib)，同样感谢作者。


## 使用


use Gradle:

```gradle
allprojects {
    repositories {
        jcenter()
        maven {
            url "https://jitpack.io"
        }
    }
}

dependencies {
    //多媒体选择器
    implementation('com.linking123w:mediapicker:1.0') {
        // 附赠：去除 glide 重复引用报错
        exclude group: 'com.github.bumptech.glide'
    }
}
```

具体使用方式请查看 [MainActivity.java](./app/src/main/java/com/linking/mediapickerpoject/MainActivity.java)




# Screenshots
![](https://github.com/dmcBig/MediaPickerPoject/blob/master/Screenshots/Screenshots1.png)

