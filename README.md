# FileDownload

#### 介绍
1.使用系统DownloadManager 下载文件 并保存到共享目录<br>  本项目不支持断点，没有service 只在activity 生命周期内有用，仅做图片，视频 和 office 文件的下载 ，不要离开 作用的 activity，<br>
2.本项目集成了新版蒲公英内侧分发系统，无需SDK  直接访问蒲公英后台接口 ，查询是否有app更新<br>

#### 软件架构
软件架构说明


#### 安装教程

###1.对于新版 gradle 插件的 7.0+<br>
	
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
 
###旧版 gradle 4.+<br>
	
	buildscript {
		repositories {
	   	 	mavenCentral()
	  	  	maven { url 'https://jitpack.io' }
	      }
      }
	allprojects {
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' } 
		}
	}

###2.在使用的module 中<br>

	dependencies {
		implementation 'com.github.lwl49:FileDownload:x.x.x'  x.x.x 具体版本号，请使用最新版本<br>
	}

#### 使用说明
1.文件下载 目前 支持图片（jpg），视频（MP4）,其他文档（office），其中图片，视频 保存在共享目录PICTURE 中  其他文件保存在共享目录DOWNLOAD 中<br>

	String imgPath // 文件下载路径
	String fileName //包含后缀名
	String dirName //文件保存的文件夹名称
	SpepcDownloadUtil.getIns(context).setPackName(dirName).downloadComplete(new DownloadListener() {
		@Override
		public void post(ModelBean bean) {
		}
	}).startDMDownLoad(imgPath, fileName);
	

2.使用新版的蒲公英内侧分发方式<br>
只需要在蒲公英官网上面注册好获取apikey 和 appkey 就行，https://www.pgyer.com/app/distribution<br>
可以自己封装一个方法 inspectUpdate() 组合参数  只需要  UpdateAppUtils.updateAPP(UpdateParamBuild paramBuild) 就可以检查更新了<br>

检查更新<br>
@param activity 用于弹窗使用，权限使用<br>
@param showToast 显示检查消息<br>
@param useCostDialog 是否自定义升级弹窗  如果为 true 必须传递 loadingInterface<br>
@param loadingInterface 结果返回<br>

	public static void inspectUpdate(Activity activity, boolean showToast,boolean useCostDialog, UpdateAppUtils.LoadingInterface loadingInterface){
       
	        UpdateParamBuild paramBuild = new UpdateParamBuild();
	        paramBuild.activity = activity;
	        paramBuild.apiKey = apiKey;
	        paramBuild.appKey = appKey;
	        paramBuild.showToast = showToast;
	        paramBuild.useCostDialog = useCostDialog;
	        paramBuild.loadingInterface = loadingInterface;
	        UpdateAppUtils.updateAPP(paramBuild);
	    }

3.直接使用glide 自带函数保存图片<br>
@param url 下载地址<br>
@param fileName 保存的文件名称 携带 后缀名<br>

	SpepcDownloadUtil.saveImgFromGlide(Activity activity, String url,String fileName) 
 


