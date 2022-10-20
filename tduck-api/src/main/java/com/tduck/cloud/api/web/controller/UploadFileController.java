package com.tduck.cloud.api.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.exception.BaseException;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.project.entity.UserProjectEntity;
import com.tduck.cloud.project.service.UserProjectService;
import com.tduck.cloud.storage.cloud.CosImageService;
import com.tduck.cloud.storage.cloud.OssStorageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserProjectService projectService;

    private final UserService userService;

    /**
     * 上传用户文件
     * <p>
     * 用户Id MD5加密 同一个用户的文件放在一个目录下
     *
     * @param mfile
     * @param fbuser
     * @return
     * @throws IOException
     */
    @Login
    @PostMapping("/user/file/upload")
    public Result<String> uploadUserFile(@RequestParam("file") MultipartFile mfile, @RequestParam("fbuser") String fbuser) throws IOException {
        UserEntity userEntity = userService.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getFbUser, fbuser));
//        String path = new StringBuffer(SecureUtil.md5(String.valueOf(userEntity.getId())))
//                .append(CharUtil.SLASH)
//                .append(IdUtil.simpleUUID())
//                .append(CharUtil.DOT)
//                .append(FileUtil.extName(mfile.getOriginalFilename())).toString();
        String folder = SecureUtil.md5(String.valueOf(userEntity.getId()));
        System.out.println(folder);
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
            throw new BaseException("上传失败");
        }


        String url = cosImageService.upload(mfile.getOriginalFilename(), System.currentTimeMillis(), file, fileMd5, folder);
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
        UserProjectEntity entity = projectService.getByKey(projectKey);
        UserEntity userEntity = userService.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getFbUser, entity.getFbUser()));
        String folder = SecureUtil.md5(String.valueOf(userEntity.getId()));
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


        String url = cosImageService.upload(mfile.getOriginalFilename(), System.currentTimeMillis(), file, fileMd5, folder);
//        String url = OssStorageFactory.build().upload(mfile.getInputStream(), path);
        return Result.success(url);
    }

}
