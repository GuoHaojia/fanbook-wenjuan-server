package com.tduck.cloud.api.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.storage.cloud.CosImageService;
import com.tduck.cloud.storage.cloud.OssStorageFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author : smalljop
 * @description : 上传文件
 * @create : 2020-11-27 14:00
 **/
@RestController
@RequiredArgsConstructor
public class UploadFileController {

    @Autowired
    CosImageService cosImageService;

    /**
     * 上传用户文件
     * <p>
     * 用户Id MD5加密 同一个用户的文件放在一个目录下
     *
     * @param mfile
     * @param userId
     * @return
     * @throws IOException
     */
    @Login
    @PostMapping("/user/file/upload")
    public Result<String> uploadUserFile(@RequestParam("file") MultipartFile mfile, @RequestAttribute Long userId) throws IOException {
        String path = new StringBuffer(SecureUtil.md5(String.valueOf(userId)))
                .append(CharUtil.SLASH)
                .append(IdUtil.simpleUUID())
                .append(CharUtil.DOT)
                .append(FileUtil.extName(mfile.getOriginalFilename())).toString();
        File file = null;
        String fileMd5 = "";
        try {
            String originalFilename = mfile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            file = File.createTempFile(filename[0], filename[1]);
            mfile.transferTo(file);
            fileMd5 = DigestUtils.md5Hex(new FileInputStream(file.getPath()));
            file.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
        }


        String url = cosImageService.upload(mfile.getOriginalFilename(), System.currentTimeMillis(), file, fileMd5);
//        String url = OssStorageFactory.build().upload(mfile.getInputStream(), path);
        return Result.success(url);
    }


    /**
     * 项目文件上传
     *
     * @param projectKey
     * @return
     * @throws IOException
     */
    @PostMapping("/project/file/upload/{projectKey}")
    public Result<String> uploadProjectFile(@RequestParam("file") MultipartFile mfile, @PathVariable("projectKey") String projectKey) throws IOException {
//        String path = new StringBuffer(SecureUtil.md5(projectKey))
//                .append(CharUtil.SLASH)
//                .append(IdUtil.simpleUUID())
//                .append(CharUtil.DOT)
//                .append(FileUtil.extName(file.getOriginalFilename())).toString();
//        String url = OssStorageFactory.build().upload(file.getInputStream(), path);
//        return Result.success(url);
        File file = null;
        String fileMd5 = "";
        try {
            String originalFilename = mfile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            file = File.createTempFile(filename[0], filename[1]);
            mfile.transferTo(file);
            fileMd5 = DigestUtils.md5Hex(new FileInputStream(file.getPath()));
            file.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
        }


        String url = cosImageService.upload(mfile.getOriginalFilename(), System.currentTimeMillis(), file, fileMd5);
//        String url = OssStorageFactory.build().upload(mfile.getInputStream(), path);
        return Result.success(url);
    }

}
