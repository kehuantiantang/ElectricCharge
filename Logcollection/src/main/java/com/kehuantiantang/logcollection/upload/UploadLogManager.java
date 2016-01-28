package com.kehuantiantang.logcollection.upload;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.kehuantiantang.logcollection.capture.LogFileStorage;

import java.io.File;

/**
 * 启动线程操作上传文件
 */
public class UploadLogManager {

    private static final String TAG = UploadLogManager.class.getName();

    private static UploadLogManager sInstance;

    private Context mContext;

    private HandlerThread mHandlerThread;

    private static volatile MyHandler mHandler;

    private volatile Looper mLooper;

    private volatile boolean isRunning = false;

    private String url;

    private HttpParameters params;

    /**
     * 启动上传线程，开始上传文件
     *
     * @param context
     */
    private UploadLogManager(Context context) {
        mContext = context.getApplicationContext();
        //上传文件
        mHandlerThread = new HandlerThread(TAG + ":HandlerThread");
        mHandlerThread.start();
    }

    /**
     * 获得的上传实例化对象，单例
     *
     * @param context
     * @return
     */
    public static synchronized UploadLogManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UploadLogManager(context);
        }
        return sInstance;
    }

    /**
     * 按照url和HttpParameters文件清单进行上传
     *
     * @param url
     * @param params
     */
    public void uploadLogFile(String url, HttpParameters params) {
        this.url = url;
        this.params = params;

        mLooper = mHandlerThread.getLooper();
        //自己创建的handler实例，通过HandlerThread来运行Handler
        mHandler = new MyHandler(mLooper);
        if (mHandlerThread == null) {
            return;
        }
        //正在上传
        if (isRunning) {
            return;
        }
        mHandler.sendMessage(mHandler.obtainMessage());
        isRunning = true;
    }

    /**
     * 获得需要上传的文件
     */
    private final class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //获得的要上传的文件
            File logFile = LogFileStorage.getInstance(mContext).getUploadLogFile();
            if (logFile == null) {
                isRunning = false;
                return;
            }
            //上传文件
            String result = HttpManager.uploadFileStream(url, logFile, null);
            if (result != null) {
                //成功上传
                LogFileStorage.getInstance(mContext).deleteInternalUploadLogFile();
            }
            isRunning = false;
        }

    }

}
