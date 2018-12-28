
# 简介

拍摄视频、图片，同时可选择两种多媒体形式


# Library 上传 jcenter 仓库

1.具体配置请查看此library下[build.gradle](build.gradle)

```

/** 以下开始是将Android Library上传到jcenter的相关配置**/

apply plugin: 'com.jfrog.bintray'
apply plugin: 'com.github.dcendents.android-maven'

// 项目的主页
def siteUrl = 'https://github.com/wlinking/MediaPickerPoject'
// Git仓库的url
def gitUrl = 'https://github.com/wlinking/MediaPickerPoject.git'

//https://dl.bintray.com/linking123w/MediaPicker
//发布到组织名称名字，必须填写
group = "com.linking123w" //最终引用形式，如compile 'com.**;
// 版本号，下次更新是只需要更改版本号即可
version = "1.0.1"
/**  上面配置后上传至jcenter后的编译路径是这样的： compile 'com.linking:MediaPicker:1.0'  **/

//生成源文件
task sourcesJar(type: Jar) {
    from android.sourceSets.main.java.srcDirs
    classifier = 'sources'
}

//生成文档
task javadoc(type: Javadoc) {
    failOnError false //必须添加以免出错
    source = android.sourceSets.main.java.srcDirs
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
}

javadoc {
    options{
        //如果你的项目里面有中文注释的话，必须将格式设置为UTF-8，不然会出现乱码
        encoding "UTF-8"
        charSet 'UTF-8'
        author true
        version true
        links "http://docs.oracle.com/javase/7/docs/api"
    }
}

//文档打包成jar
task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier = 'javadoc'
    from javadoc.destinationDir
}

//拷贝javadoc文件
task copyDoc(type: Copy) {
    from "${buildDir}/docs/"
    into "docs"
}

//上传到jcenter所需要的源码文件
artifacts {
    archives javadocJar
    archives sourcesJar
}

// 配置maven库，生成POM.xml文件
install {
    repositories.mavenInstaller {
        // This generates POM.xml with proper parameters
        pom {
            project {
                packaging 'aar'
                // Add your description here
                name 'mediaPicker for Android'
                description 'MediaPicker open library.'
                url siteUrl
                // Set your license
                licenses {
                    license {
                        name 'The Apache Software License, Version 2.1'
                        url 'http://www.apache.org/licenses/LICENSE-2.1.txt'
                    }
                }
                developers {
                    developer {
                        id 'linking123w'        //填写bintray或者github的用户名
                        name 'Linking'         //姓名
                        email 'linking123w@gmail.com'//邮箱
                    }
                }
                scm {
                    connection gitUrl
                    developerConnection gitUrl
                    url siteUrl
                }
            }
        }
    }
}

//上传到jcenter
Properties properties = new Properties()
properties.load(project.rootProject.file('local.properties').newDataInputStream())
bintray {
    //读取 local.properties 文件里面的 bintray.user
    user = properties.getProperty("bintray.user")
    //读取 local.properties 文件里面的 bintray.apikey
    key = properties.getProperty("bintray.apikey")
    pkg {
        userOrg = 'linking123w' //自己创建的organization名称
        repo = 'MediaPicker'//自己创建的仓库名字
        name = 'MediaPicker'//libname 发布到JCenter上的项目名字，必须填写.最终引用的名字
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        licenses = ['Apache-2.0']//不能随便写，只能是仓库创建时选择的license type
        publish = true // 是否是公开项目，公开别人可以引用

        version {
            name = '1.0.1'
            desc = 'One open library for mediaPicker, both image and video.'//描述，自己定义
            released  = new Date()
            vcsTag = 'v1.0'
            attributes = ['gradle-plugin': 'com.use.less:com.use.less.gradle:gradle-useless-plugin']
        }
    }
    configurations = ['archives']
}
```

2.上传命令

进入terminal，首先确保gradlew具有执行权限，没有的话执行

```
chmod +x ./gradlew
```

开始安装

```
gradlew install
```

开始上传

```
gradlew bintrayUpload
```