package com.tduck.cloud.storage.cloud;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author : qiumingming
 * @class desc :
 * @class name : CosImageService
 * @date : 4/13/21 8:50 PM
 **/
@Service
@Validated
public class CosImageService {

    private static Logger LOGGER = LoggerFactory.getLogger(CosImageService.class);

    private COSClient cosClient;

    @PostConstruct
    public void init() {
        try {
            initClient();
//            checkAndInitBucket();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    private static String secretId = "secretId";
    private static String secretKey = "secretKey";
    private static String bucketName = "bucketName";
    private static String regionStr = "ap-shanghai";


    private void initClient() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(regionStr);
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        cosClient = new COSClient(cred, clientConfig);
    }

    public String upload(String fileName, Long id, File file, String md5) {
        String fileFormat = fileName.substring(fileName.lastIndexOf("."));
        String key = String.valueOf(id).concat("-").concat(md5.substring(0, 8)).concat(fileFormat);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        //examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/images/picture.jpg
        return "https://".concat(bucketName).concat(".cos.").concat(regionStr).concat(".myqcloud.com/").concat(key);
    }

}
