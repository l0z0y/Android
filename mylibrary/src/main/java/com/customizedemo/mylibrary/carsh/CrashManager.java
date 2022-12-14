package com.customizedemo.mylibrary.carsh;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.alibaba.ha.adapter.service.tlog.TLogService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;


public class CrashManager {

    private static CrashManager instance;
    private final HandlerThread handlerThread;
    private final Handler workHandler;

    /**
     * 保存并发送错误信息
     */
    public static final int MSG_SAVE_AND_SENT_CRASH_INFO = 1;
    /**
     * 仅发送错误信息
     */
    public static final int MSG_JUST_SEND_CRASH_INFO = 2;
    private Context context;


    private CrashManager() {
        handlerThread = new HandlerThread("Thread-0");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_SAVE_AND_SENT_CRASH_INFO) {
                    //todo 保存并发送错误信息

                } else if (msg.what == MSG_JUST_SEND_CRASH_INFO) {
                    Log.i("Crash", "handleMessage ");
                    //上报日志

                }
            }
        };
    }


    public static CrashManager getInstance() {
        if (instance == null) {
            synchronized (CrashManager.class) {
                if (instance == null) {
                    instance = new CrashManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        CrashHandler.init(context);
        workHandler.obtainMessage(MSG_JUST_SEND_CRASH_INFO, context).sendToTarget();
    }


    /**
     * //todo: 上传服务器操作
     * 检测遍历错误报告，把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    @WorkerThread
    public void checkSendToServer(@NonNull Context context) {
        File[] files = getReports(context);
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath())) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取异常文件名
     */
    private File[] getReports(@NonNull Context context) {
        File file = new File(getGlobalPath(context));
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("crash") && name.endsWith("txt");
            }
        });

        return files;
    }

    // 异常log保存路径
    private String getGlobalPath(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath() + "/Crash/";
    }

}
