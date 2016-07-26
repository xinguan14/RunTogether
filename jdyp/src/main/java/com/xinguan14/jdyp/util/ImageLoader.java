package com.xinguan14.jdyp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by wm on 2016/7/23.
 * 图片加载类
 */
public class ImageLoader {
    private static ImageLoader mInstance;
    //图片缓存的核心对象
    private LruCache<String,Bitmap> mLruCache;
    //线程池,执行加载图片的任务
    private ExecutorService mThreadPool;
    //默认线程为1
    private static final int DEAFULT_THREAD_COUNT=1;
    //队列的调度方式
    private Type mType =Type.LIFO;
    //任务队列，从线程池里面取任务
    private LinkedList<Runnable> mTaskQueue;
    //后台轮训线程
    private  Thread mPoolThread;

    private Handler mPoolThreadHandler;
    //UI线程中的Handler用于给ImageView设置图片
    private Handler mUIHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

    //队列调度方式
    public enum Type{
        FIFO,LIFO;
    }



    private ImageLoader(int threadCount,Type type){
        init(threadCount,type);
    }

    //初始化操作
    private void init(int threadCount,Type type){

        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };

        mPoolThread.start();

        //获取应用的最大内存
        int maxMemory =(int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory/8;
        mLruCache = new LruCache<String ,Bitmap>(cacheMemory){
            //测量每个BitMap的值
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTaskQueue =new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;
    }


    //单例获得该实例对象
    public static ImageLoader getInstance(int threadCount, Type type){
        if (mInstance==null){
            synchronized (ImageLoader.class){
                if (mInstance==null) {
                    mInstance = new ImageLoader(DEAFULT_THREAD_COUNT,Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /*
    * 根据path为imageview设置图片
    * */
    public  void  loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);
        if (mUIHandler==null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //获取得到图片，为ImageView回调设置图片
                    ImageBeanHolder holder = (ImageBeanHolder)msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageView= holder.imageView;
                    String path = holder.path;
                    //将path与getTag存储路径进行比较
                    if(imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bm);
                    }

                }
            };
        }
        //根据path从LruCache获得图片
        Bitmap bm =getBitmapFromLruCache(path);
        if (bm!=null){
            Message message = Message.obtain();
            ImageBeanHolder holder = new ImageBeanHolder();
            holder.bitmap = bm;
            holder.imageView = imageView;
            holder.path =path;
            message.obj =holder;
            mUIHandler.sendMessage(message);
        }else {
            addTask(new Runnable(){

                @Override
                public void run() {
                    //加载图片，图片压缩
                    ImageSize imageSize = getImageSize(imageView);

                    int reqWidth = imageSize.width;
                    int reqHeight = imageSize.height;

                    //压缩图片
                    Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
                            reqHeight);
                    //将图片加入缓存
                    addBitmapToLruCache(path, bm);
                    ImageBeanHolder holder = new ImageBeanHolder();
                    holder.bitmap = getBitmapFromLruCache(path);
                    holder.imageView = imageView;
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    // Log.e("TAG", "mHandler.sendMessage(message);");
                    mUIHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }

    }


    /**
     * 添加一个任务
     */
    private synchronized void addTask(Runnable runnable)
    {
        try
        {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHandler == null)
                mSemaphore.acquire();
        } catch (InterruptedException e)
        {
        }
        mTaskQueue.add(runnable);

        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     */
    private synchronized Runnable getTask()
    {
        if (mType == Type.FIFO)
        {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO)
        {
            return mTaskQueue.removeLast();
        }
        return null;
    }


    /**
     * 根据ImageView获得适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageSize(ImageView imageView)
    {
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth(); // Get actual image width
        if (width <= 0)
            width = params.width; // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // 检查最大值
        if (width <= 0)
            width = displayMetrics.widthPixels;//宽度为屏幕宽度

        int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = params.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;

        imageSize.width = width;
        imageSize.height = height;
        return imageSize;

    }

    /*
    * 根据path从缓存中获取路径
    * */
    private Bitmap getBitmapFromLruCache(String path){
        return mLruCache.get(path);
    }


    /**
     * 往LruCache中添加一张图片
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromLruCache(key) == null)
        {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }

    /**
     * 计算inSampleSize，用于压缩图片
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight)
    {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight)
        {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight)
    {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        return bitmap;
    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     */
    private static int getImageViewFieldValue(Object object, String fieldName)
    {
        int value = 0;
        try
        {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
            {
                value = fieldValue;

                Log.e("TAG", value + "");
            }
        } catch (Exception e)
        {
        }
        return value;
    }


    private class ImageBeanHolder
    {
        Bitmap bitmap;
        ImageView imageView;
        String path;

    }

    private class ImageSize
    {
        int width;
        int height;
    }
}
