package com.customizedemo.mylibrary.hotfix;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.Log;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.AliHaConfig;
import com.alibaba.ha.adapter.Plugin;
import com.alibaba.ha.adapter.service.tlog.TLogLevel;
import com.alibaba.ha.adapter.service.tlog.TLogService;
import com.customizedemo.mylibrary.MyApplication;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

public class SopHotFixApplication extends SophixApplication {

    private final String TAG = "SophixStubApplication";
    private String appVersion = "0.0.0";

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取补丁
        SophixManager.getInstance().queryAndLoadNewPatch();
        //初始化远程日志
        initHa();
    }

    // 这里为原来定义的Application
    @Keep
    @SophixEntry(MyApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
//         MultiDex.install(this);
        initSophix();
    }


    private void initSophix() {
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            Log.i(TAG, "appVersion: " + appVersion);
        } catch (Exception e) {

        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                // 此处传入配置文件中HOTFIX_ID_SECRET，EMAS_APP_SECRET，HOTFIX_RSA_SECRET
                .setSecretMetaData(AliServicesConfig.HOTFIX_ID_SECRET, AliServicesConfig.EMAS_APP_SECRET, AliServicesConfig.HOTFIX_RSA_SECRET)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        Log.e(TAG, "修复模式：" + mode);
                        Log.e(TAG, "修复回调code：" + code);
                        Log.e(TAG, "修复信息：" + info);
                        Log.e(TAG, "修复版本：" + handlePatchVersion);

                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                            Log.e(TAG, "表明补丁加载成功");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                            Log.e(TAG, "表明新补丁生效需要重启. 开发者可提示用户或者强制重启");
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            // SophixManager.getInstance().cleanPatches();
                            Log.e(TAG, "内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载");
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                            Log.e(TAG, code + " " + info);
                        }

                    }
                }).initialize();
    }

    private void initHa() {
        AliHaConfig config = new AliHaConfig();
        config.appKey = AliServicesConfig.APP_KEY; //配置项：appkey
        config.appVersion = appVersion; //配置项：应用的版本号
        config.appSecret = AliServicesConfig.EMAS_APP_SECRET; //配置项：appsecret
        config.channel = "mqc_test"; //配置项：应用的渠道号标记，自定义
        config.userNick = "zz"; //配置项：用户的昵称
        config.application = this; //配置项：应用指针
        config.context = getApplicationContext(); //配置项：应用上下文
        config.isAliyunos = false; //配置项：是否为yunos
        config.rsaPublicKey = AliServicesConfig.APPMONITOR_TLOG_RSASECRET; //配置项：tlog公钥
        AliHaAdapter.getInstance().addPlugin(Plugin.tlog);
        AliHaAdapter.getInstance().openDebug(true);
        AliHaAdapter.getInstance().start(config);
        TLogService.updateLogLevel(TLogLevel.ERROR); //配置项：控制台可拉取的日志级别
    }
}

