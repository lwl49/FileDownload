# FileDownload

#### 介绍
1.使用系统DownloadManager 下载文件 并保存到共享目录<br>
2.本项目集成了新版蒲公英内侧分发系统，无需SDK  直接访问蒲公英后台接口 ，查询是否有app更新<br>
#### 软件架构
软件架构说明


#### 安装教程

###1.对于新版 gradle 插件的 7.0+<br>

dependencyResolutionManagement {<br>
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)<br>
		repositories {<br>
			mavenCentral()<br>
			maven { url 'https://jitpack.io' }<br>
		}<br>
	}<br>
 
###旧版 gradle 4.+<br>

   repositories {<br>
        mavenCentral()<br>
        maven { url 'https://jitpack.io' }<br>
    }<br>

    allprojects {
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' } 
    }
}

###2.在使用的module 中<br>
  dependencies {<br>
	        implementation 'com.github.lwl49:file_down_git:xxx'  xxx 具体版本号<br>
	}<br>



#### 使用说明
1.文件下载 目前 支持图片（jpg），视频（MP4）,其他文档（office），其中图片，视频 保存在共享目录PICTURE 中  其他文件保存在共享目录DOWNLOAD 中
String imgPath // 文件下载路径
String fileName //包含后缀名
String dirName //文件保存的文件夹名称
SpepcDownloadUtil.getIns(context).setPackName(dirName).downloadComplete(new DownloadListener() {
            @Override
             public void post(ModelBean bean) {

                    }
                })
                .startDMDownLoad(imgPath, fileName);

2.使用新版的蒲公英内侧分发方式

 /**
     * 使用4.+以上版本
     * CommonLogUtils.e("xxx - checkSoftModel = "+checkSoftModel);
     * CommonLogUtils.e("xxx - checkSoftModel = onFail = "+s);
     * 蒲公英升级  需要蒲公英SDK
     * @param activity
     * @param apiKey  用户身份 api_key
     * @param appKey  应用 app_key
     * @param showToast 是否需要弹出新版提示
     */

 UpdateAppUtils.updateAPP(activity,apiKey,appKey,showToast);




