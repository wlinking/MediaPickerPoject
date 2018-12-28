# MediaPickerProject

仿微信视频图片选择器。


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



[Cordova版](https://github.com/DmcSDK/cordova-plugin-mediaPicker) : https://github.com/DmcSDK/cordova-plugin-mediaPicker 

[IOS版](https://github.com/DmcSDK/IOSMediaPicker) : https://github.com/DmcSDK/IOSMediaPicker

# Screenshots
![](https://github.com/dmcBig/MediaPickerPoject/blob/master/Screenshots/Screenshots1.png)

