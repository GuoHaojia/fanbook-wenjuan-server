package com.tduck.cloud.project.constant;

/**
 * @description: redis key常量
 * @author: smalljop
 * @create: 2020-02-12 22:34
 **/
public interface ProjectRedisKeyConstants {
    /**
     * 项目Item排序位置自增
     */
    String PROJECT_ITEM_POS_DELTA = "project:item:pos:{}";


    /**
     * 项目答卷数自增
     */
    String PROJECT_RESULT_NUMBER = "project:result:number:{}";

    /**
     * 项目发布数自增
     */
    String PROJECT_PUBLISH_NUMBER = "project:publish:number:{}";

    /**
     * 查看项目IP记录列表
     */
    String PROJECT_VIEW_IP_LIST = "project:view:ip:list:{}";

//    /**
//     * 答卷结果多选题数量自增
//     */
//    String PROJECT_RESULT_CHECKBOX = "project:result:checkbox:{}";
//
//    /**
//     * 答卷结果单选题数量自增
//     */
//    String PROJECT_RESULT_RADIO = "project:result:radio:{}";
//
//    /**
//     * 答卷结果下拉题数量自增
//     */
//    String PROJECT_RESULT_SELECT = "project:result:select:{}";
//    /**
//     * 答卷结果图片选择题数量自增
//     */
//    String PROJECT_RESULT_IMAGE_SELECT = "project:result:imageselect:{}";
//    /**
//     * 答卷结果单行文本数量自增
//     */
//    String PROJECT_RESULT_INPUT = "project:result:input:{}";
//    /**
//     * 答卷结果多行文本数量自增
//     */
//    String PROJECT_RESULT_TEXTAREA = "project:result:textarea:{}";
//    /**
//     * 答卷结果日期题数量自增
//     */
//    String PROJECT_RESULT_DATE = "project:result:date:{}";
//    /**
//     * 答卷结果时间题数量自增
//     */
//    String PROJECT_RESULT_TIME = "project:result:time:{}";
//    /**
//     * 答卷结果省市区数量自增
//     */
//    String PROJECT_RESULT_PROVINCE_CITY = "project:result:provincecity:{}";
//    /**
//     * 答卷结果上传图片题数量自增
//     */
//    String PROJECT_RESULT_UPLOAD = "project:result:upload:{}";
//    /**
//     * 答卷结果上传文件题数量自增
//     */
//    String PROJECT_RESULT_WUPLOAD = "project:result:radio:{}";
//    /**
//     * 答卷结果矩阵量表题数量自增
//     */
//    String PROJECT_RESULT_MATRIX_SCALE = "project:result:matrixscale:{}";
//    /**
//     * 答卷结果矩阵选择题数量自增
//     */
//    String PROJECT_RESULT_MATRIX_SELECT = "project:result:matrixselect:{}";
//    /**
//     * 答卷结果评分题数量自增
//     */
//    String PROJECT_RESULT_RATE = "project:result:rate:{}";

}
