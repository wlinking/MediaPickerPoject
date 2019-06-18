
# 简介

拍摄视频、图片，同时可选择两种多媒体形式


# Library 上传 jcenter 仓库

1.具体配置请查看此library下[build.gradle](build.gradle)


2.上传


2.1 版本号

```
# 69行
version = "1.0.x"
# 167行
name = '1.0.x'
# 可能 170行
vcsTag = 'v1.0'
```

2.2 命令

进入terminal，首先确保gradlew具有执行权限，没有的话执行

```
chmod +x ./gradlew
```

开始安装

```
./gradlew install
```

开始上传

```
./gradlew bintrayUpload
```

# 错误记录

## 版本冲突，已上传

- 描述

```
Execution failed for task ':mediapicker:bintrayUpload'.
> Could not upload to 'https://api.bintray.com/content/linking123w/MediaPicker/
MediaPicker/1.0/com/linking123w/mediapicker/1.0/mediapicker-1.0-javadoc.jar': 
HTTP/1.1 409 Conflict [message:Unable to upload files: An artifact with the path
 'com/linking123w/mediapicker/1.0/mediapicker-1.0-javadoc.jar' already exists]
```

- 解决

```
代表文件已经存在了，把版本号改一下 再上传

# 69行
version = "1.0.x"
# 167行
name = '1.0.x'
# 可能 170行
vcsTag = 'v1.0'
```

## 上传后延时下载

执行命令，成功但没有上传；

上 [bintray.com](https://bintray.com)登陆账号查看是否已更新

稍等一会，延时比较严重，过了一会去看，已上传
