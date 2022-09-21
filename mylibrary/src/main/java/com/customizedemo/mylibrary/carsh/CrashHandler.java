package com.customizedemo.mylibrary.carsh;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // 系统默认处理程序
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private WeakReference<Context> contextWeakReference;

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);


    private CrashHandler(Context context, Thread.UncaughtExceptionHandler mDefaultHandler) {
        this.mDefaultHandler = mDefaultHandler;
        if (context != null) {
            this.contextWeakReference = new WeakReference<>(context.getApplicationContext());
        }
    }


    public Context getContext() {
        if (contextWeakReference != null) {
            return contextWeakReference.get();
        }
        return null;
    }

    /**
     * 初始化 处理程序
     */
    public static void init(Context context) {
        // 获取未捕获默认处理程序
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (defaultUncaughtExceptionHandler == null || !CrashHandler.class.getName().equals(defaultUncaughtExceptionHandler.getClass().getName())) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context, defaultUncaughtExceptionHandler));
        }

    }


    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d(TAG, "发生异常：----------------------\n" + getStackTraceString(ex));
        handleException(ex);
        if (mDefaultHandler != null) {
            SystemClock.sleep(1000);
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }

    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return
     */
    private void handleException(Throwable ex) {
        if (ex == null) {
            return;
        }
        try {
            // TODO:自定义错误处理,收集错误信息 发送错误报告等操作均在此完成
            saveCrashInfoFile(ex);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(getContext(), "很抱歉,程序出现异常,即将退出.",
                            Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取详细错误信息   tr.printStackTrace()
     *
     * @param tr
     * @return
     */
    public String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * 保存错误信息到文件中
     */
    private void saveCrashInfoFile(Throwable ex) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String date = sDateFormat.format(new Date());
            sb.append("\r\n").append(date).append("\n");

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();

            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }

            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            writeFile(sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "an error occurred while writing file...", e);
            sb.append("an error occurred while writing file...\r\n");
            writeFile(sb.toString());
        }
    }

    private void writeFile(String sb) throws Exception {
        String time = formatter.format(new Date());
        String fileName = "crash-" + time + ".txt";
        if (hasSdcard()) {
            String path = getGlobalPath();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(sb.getBytes());
            fos.flush();
            fos.close();
        }
    }

    // 异常log保存路径
    private String getGlobalPath() {
        return getContext().getExternalFilesDir("").getAbsolutePath() + "/Crash/";
    }

    // 判断是否存在sd卡
    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}



