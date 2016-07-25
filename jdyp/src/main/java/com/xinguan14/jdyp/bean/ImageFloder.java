package com.xinguan14.jdyp.bean;

/**
 * Created by wm on 2016/7/23.
 * 用来存储当前文件夹的路径，当前文件夹包含多少张图片
 * 以及第一张图片路径用于做文件夹的图标
 */
public class ImageFloder
{
    /**
     * 图片的文件夹路径
     */
    private String dir;

    /**
     * 第一张图片的路径
     */
    private String firstImagePath;

    /**
     * 文件夹的名称
     */
    private String name;

    /**
     * 图片的数量
     */
    private int count;

    public String getDir()
    {
        return dir;
    }

    public void setDir(String dir)
    {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf);
    }

    public String getFirstImagePath()
    {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath)
    {
        this.firstImagePath = firstImagePath;
    }

    public String getName()
    {
        return name;
    }
    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }



}
