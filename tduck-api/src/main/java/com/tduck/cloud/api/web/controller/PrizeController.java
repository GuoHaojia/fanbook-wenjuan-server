package com.tduck.cloud.api.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.project.entity.ProjectPrizeEntity;
import com.tduck.cloud.project.entity.ProjectPrizeItemEntity;
import com.tduck.cloud.project.entity.ProjectPrizeSettingEntity;
import com.tduck.cloud.project.service.ProjectPrizeItemService;
import com.tduck.cloud.project.service.ProjectPrizeService;
import com.tduck.cloud.project.service.ProjectPrizeSettingService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/18 10:27
 * <p>
 * mark
 */


@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class PrizeController {
    private final ProjectPrizeSettingService projectPrizeSettingService;
    private final ProjectPrizeService projectPrizeService;
    private final ProjectPrizeItemService projectPrizeItemService;

    @Login
    @PostMapping("/prize/setting/query")
    @ApiOperation("????????????????????????")
    public Result settingQuery(@RequestParam(value = "projectKey") String projectKey){
        List<ProjectPrizeSettingEntity> settingList = projectPrizeSettingService.lambdaQuery().eq(ProjectPrizeSettingEntity::getProjectKey,projectKey).list();

        if(settingList.size() > 0){
            ProjectPrizeSettingEntity setting = settingList.get(0);
            setting.setPrizes(projectPrizeService.lambdaQuery().eq(ProjectPrizeEntity::getProjectKey,projectKey).eq(ProjectPrizeEntity::getStatus,true).list());
            return Result.success(setting);
        }

        return Result.success(settingList);
    }

    @Login
    @PostMapping("/prize/setting/save")
    @ApiOperation("????????????????????????")
    public Result settingSave(@RequestBody ProjectPrizeSettingEntity projectPrizeSettingEntity){
        if(projectPrizeSettingEntity.getId() == null){
            projectPrizeSettingService.getBaseMapper().insert(projectPrizeSettingEntity);
        }else{
            projectPrizeSettingService.getBaseMapper().updateById(projectPrizeSettingEntity);
        }
        return Result.success(projectPrizeSettingEntity);
    }

    @Login
    @PostMapping("/prize/score/save")
    @ApiOperation("??????????????????")
    public Result scoreSave(@RequestBody ProjectPrizeEntity projectPrizeEntity){
        projectPrizeEntity.setType(1);
        projectPrizeService.getBaseMapper().insert(projectPrizeEntity);
        ProjectPrizeItemEntity prizeItem = ProjectPrizeItemEntity.builder()
                .id(null)
                .prizeid(projectPrizeEntity.getId())
                .prize(projectPrizeEntity.getDesc())
                .projectKey(projectPrizeEntity.getProjectKey())
                .fanbookid("")
                .nickname("")
                .phoneNumber("")
                .status(true)
                .type(1)
                .build();

        if(projectPrizeEntity.getCount() > 0){
            List<ProjectPrizeItemEntity> list = new ArrayList<ProjectPrizeItemEntity>();
            while (list.size() < projectPrizeEntity.getCount()){
                list.add(prizeItem);
            }

            return Result.success(projectPrizeItemService.saveBatch(list));
        }else{
            return Result.success(projectPrizeEntity);
        }
    }

    @Login
    @PostMapping("/prize/cdk/import")
    @ApiOperation("??????cdk??????")
    public Result cdkSave(@RequestParam(name = "projectKey") String projectKey, @RequestParam(name = "desc") String desc, @RequestParam("file") MultipartFile file){
        ProjectPrizeEntity projectPrizeEntity = ProjectPrizeEntity.builder().projectKey(projectKey).build();
        projectPrizeEntity.setDesc(desc);
        projectPrizeEntity.setStatus(true);
        projectPrizeEntity.setType(0);

        ///??????cdk
        try {
            XSSFWorkbook book = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = book.getSheetAt(0);
            int cols;
            List<ProjectPrizeItemEntity> list = new ArrayList<ProjectPrizeItemEntity>();

            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i + 1); // ????????????
                cols = 0;

                projectPrizeEntity.setCount(i);

                ProjectPrizeItemEntity prizeItem = ProjectPrizeItemEntity.builder()
                        .id(null)
                        .prize(getCellStringValue(row.getCell(cols++)))
                        .projectKey(projectPrizeEntity.getProjectKey())
                        .fanbookid("")
                        .nickname("")
                        .phoneNumber("")
                        .status(true)
                        .type(0)
                        .build();
                list.add(prizeItem);
            }
            if (list.size() == 0){
                return Result.failed("excel?????????cdk");
            }

            projectPrizeService.getBaseMapper().insert(projectPrizeEntity);
            list.forEach(item->item.setPrizeid(projectPrizeEntity.getId()));
            book.close();
            return Result.success(projectPrizeItemService.saveBatch(list));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed("excel??????????????????");
        }
    }


    @Login
    @PostMapping("/prize/delete")
    @ApiOperation("????????????")
    public Result prizeDelete(@RequestBody ProjectPrizeEntity projectPrizeEntity){
        projectPrizeEntity.setStatus(false);
        projectPrizeService.lambdaUpdate().eq(ProjectPrizeEntity::getId,projectPrizeEntity.getId()).update(projectPrizeEntity);
        projectPrizeItemService.lambdaUpdate().eq(ProjectPrizeItemEntity::getPrizeid,projectPrizeEntity.getId()).update(ProjectPrizeItemEntity.builder().status(false).build());
        return Result.success(projectPrizeEntity);
    }

    @Login
    @PostMapping("/prize/win")
    @ApiOperation("??????????????????")
    public Result win(@RequestParam(name = "projectKey") String projectKey , @RequestParam(name = "page") Integer page,@RequestParam(name = "limit") Integer limit){
        Page<ProjectPrizeItemEntity> ipage = projectPrizeItemService.lambdaQuery()
                .eq(ProjectPrizeItemEntity::getProjectKey,projectKey)
                .eq(ProjectPrizeItemEntity::getStatus,true)
                .page(new Page<ProjectPrizeItemEntity>(page,limit).setSearchCount(true));

        Map<String,Object > result = new HashMap<>();
        result.put("count",ipage.getTotal());
        result.put("data",ipage.getRecords());
        return Result.success(result);
    }



    @Login
    @PostMapping("/prize/info")
    @ApiOperation("??????????????????")
    public Result info(@RequestParam(name = "fanbookid") String fanbookid  , @RequestParam(name = "page") Integer page,@RequestParam(name = "limit") Integer limit){
        Page<ProjectPrizeItemEntity> ipage = projectPrizeItemService.lambdaQuery()
                .eq(ProjectPrizeItemEntity::getFanbookid,fanbookid)
                .eq(ProjectPrizeItemEntity::getStatus,true)
                .page(new Page<ProjectPrizeItemEntity>(page,limit).setSearchCount(true));

        Map<String,Object > result = new HashMap<>();
        result.put("count",ipage.getTotal());
        result.put("data",ipage.getRecords());
        return Result.success(result);
    }


    @Login
    @PostMapping("/prize/model")
    @ApiOperation("????????????")
    public void excelModel(HttpServletResponse httpServletResponse){

        try {
            httpServletResponse.reset();
            httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
            httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("????????????.xlsx", "utf-8"));
            httpServletResponse.setContentType("application/vnd.ms-excel;charset=utf-8");

            ClassPathResource resource = new ClassPathResource("template/cdkmodel.xlsx");

            XSSFWorkbook book = new XSSFWorkbook(resource.getInputStream());
            book.write(httpServletResponse.getOutputStream());
            httpServletResponse.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Login
    @PostMapping("/prize/export")
    @ApiOperation("??????????????????excel")
    public void excelExport(@RequestParam String projectKey,HttpServletResponse httpServletResponse){
        List<ProjectPrizeItemEntity> list = projectPrizeItemService.lambdaQuery().eq(ProjectPrizeItemEntity::getProjectKey,projectKey).eq(ProjectPrizeItemEntity::getStatus,true).list();

        try {
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.addHeader("Access-Control-Allow-Origin","*");
            httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("????????????.xls", "utf-8"));

            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            HSSFWorkbook workbook = new HSSFWorkbook();

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);

            Font font = workbook.createFont();
            font.setFontName("??????");
            font.setFontHeightInPoints((short) 12);
            cellStyle.setFont(font);

            HSSFSheet sheet = workbook.createSheet("Sheet1");
            Row row0 = sheet.createRow(0);
            Cell cell0 = row0.createCell(0);
            cell0.setCellValue("????????????");
            cell0.setCellStyle(cellStyle);

            Cell cell1 = row0.createCell(1);
            cell1.setCellValue("????????????");
            cell1.setCellStyle(cellStyle);

            Cell cell2 = row0.createCell(2);
            cell2.setCellValue("??????");
            cell2.setCellStyle(cellStyle);

            Cell cell3 = row0.createCell(3);
            cell3.setCellValue("????????????");
            cell3.setCellStyle(cellStyle);

            Cell cell4 = row0.createCell(4);
            cell4.setCellValue("fanbookid");
            cell4.setCellStyle(cellStyle);

            Cell cell5 = row0.createCell(5);
            cell5.setCellValue("??????");
            cell5.setCellStyle(cellStyle);

            Cell cell6 = row0.createCell(6);
            cell6.setCellValue("?????????");
            cell6.setCellStyle(cellStyle);

            int rowid = 1;
            for (ProjectPrizeItemEntity item : list){

                Row current_row = sheet.createRow(rowid);
                Cell cella = current_row.createCell(0);
                cella.setCellValue(item.getProjectKey());
                cella.setCellStyle(cellStyle);

                Cell cellb = current_row.createCell(1);
                if(item.getStatus()){
                    cellb.setCellValue("??????");
                }else{
                    cellb.setCellValue("CDK");
                }
                cellb.setCellStyle(cellStyle);

                Cell cellc = current_row.createCell(2);
                cellc.setCellValue(item.getPrize());
                cellc.setCellStyle(cellStyle);

                Cell celld = current_row.createCell(3);
                celld.setCellValue(item.getGetTime());
                celld.setCellStyle(cellStyle);

                Cell celle = current_row.createCell(4);
                celle.setCellValue(item.getFanbookid());
                celle.setCellStyle(cellStyle);

                Cell cellf = current_row.createCell(5);
                cellf.setCellValue(item.getNickname());
                cellf.setCellStyle(cellStyle);

                Cell cellg = current_row.createCell(6);
                cellg.setCellValue(item.getPhoneNumber());
                cellg.setCellStyle(cellStyle);
                rowid++;
            }

            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String getCellStringValue(XSSFCell cell) {
        try {
            if (null!=cell) {
                return String.valueOf(cell.getStringCellValue());
            }
        } catch (Exception e) {
            return String.valueOf(getCellIntValue(cell));
        }
        return "";
    }

    private int getCellIntValue(XSSFCell cell) {
        try {
            if (null!=cell) {
                return Integer.parseInt("" + (int) cell.getNumericCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
