package com.spepc.lib_download;

import java.io.Serializable;

/**
 * @Author lwl
 * 日期    2024/3/6
 * 目的    下载绑定 通知栏
 */
public class ModelBean implements Serializable {
    public long id ;//content://..../id
    public String name; //文件名称  包含后缀名
    public String filePath; //文件下载路径

    public ModelBean(){

    }
    public ModelBean(Long id,String name,String filePath){
        this.id = id;
        this.name = name;
        this.filePath = filePath;
    }
}
