package com.customizedemo.mylibrary.picupload;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.customizedemo.mylibrary.util.DecodeUtil;

import java.io.File;
import java.util.Date;

public class ALiUploadManager {

    public static String ACCESS_ID;                                  //阿里云ID
    public static String ACCESS_KEY;                           //阿里云KEY
    public static String ACCESS_BUCKET_NAME;
    public static final String ACCESS_ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    public static final String ACCESS_DOMAINNAME = "http:xxxxx";

    private OSSClient ossClient = null;
    private static ALiUploadManager instance = null;

    public static ALiUploadManager getInstance() {
        if (instance == null) {
            synchronized (ALiUploadManager.class) {
                if (instance == null) {
                    instance = new ALiUploadManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化阿里云SDK
     *
     * @param context
     */
    public void init(Context context) {
        ALiOSSUser aLiOSSUser = ALiOSSUser.get(context);
        ACCESS_ID = DecodeUtil.decodeByBase64(aLiOSSUser.getAppId());
        ACCESS_KEY = DecodeUtil.decodeByBase64(aLiOSSUser.getAppkey());
        ACCESS_BUCKET_NAME = DecodeUtil.decodeByBase64(aLiOSSUser.getBucketName());
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ACCESS_ID, ACCESS_KEY);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000);               // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000);                   // socket超时，默认15秒
        conf.setMaxConcurrentRequest(8);                    // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(3);                           // 失败后最大重试次数，默认2次
        // oss为全局变量，OSS_ENDPOINT是一个OSS区域地址
        ossClient = new OSSClient(context, ACCESS_ENDPOINT, credentialProvider, conf);
    }

    /**
     * 上传图片到阿里云
     *
     * @param filePath 本地图片地址
     */
    public OSSAsyncTask uploadFile(String filePath, final ALiCallBack callBack) {
        // 构造上传请求
        final String key = getObjectPortraitKey(filePath);
        Log.i("ALiUploadManger", "key:" + key);
        // meta设置请求头
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/jpg");
        PutObjectRequest put = new PutObjectRequest(ACCESS_BUCKET_NAME, key, filePath, meta);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                if (callBack != null) {
                    callBack.process(currentSize, totalSize);
                }
            }
        });

        OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (callBack != null) {
                    //获取可访问的url
                    String url = ossClient.presignPublicObjectURL(ACCESS_BUCKET_NAME, key);
                    callBack.onSuccess(request, result, url);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.i("ALiUploadManger", "ErrorCode" + serviceException.getErrorCode());
                    Log.i("ALiUploadManger", "RequestId" + serviceException.getRequestId());
                    Log.i("ALiUploadManger", "HostId" + serviceException.getHostId());
                    Log.i("ALiUploadManger", "RawMessage" + serviceException.getRawMessage());
                }
                if (callBack != null) {
                    callBack.onError(request, clientExcepion, serviceException);
                }
            }
        });
        return task;
    }

    //格式: portrait/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectPortraitKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    /**
     * 获取时间
     *
     * @return 时间戳 例如:201805
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    public interface ALiCallBack {

        /**
         * 上传成功
         *
         * @param request
         * @param result
         */
        void onSuccess(PutObjectRequest request, PutObjectResult result, String url);

        /**
         * 上传失败
         *
         * @param request
         * @param clientExcepion
         * @param serviceException
         */
        void onError(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException);

        /**
         * 上传进度
         *
         * @param currentSize 当前进度
         * @param totalSize   总进度
         */
        void process(long currentSize, long totalSize);

    }
}
