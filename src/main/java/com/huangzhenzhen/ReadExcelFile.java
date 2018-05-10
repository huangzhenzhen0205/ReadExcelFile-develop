package com.huangzhenzhen;

import com.alibaba.fastjson.JSONObject;
import com.huangzhenzhen.utils.HttpUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzhenzhen on 2018/1/29.
 */
public class ReadExcelFile {
    /**
     * 获取 excel的原始数据
     *
     * @param path
     * @return
     * @throws Exception
     */
    private List<String[]> getExcelRowsInfo(String path) throws Exception {
        List<String[]> rows = new ArrayList<>();
        InputStream inputStream = new FileInputStream(new File(path));
//            fs = new POIFSFileSystem(inputStream);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(0);
        int rowNum = sheet.getPhysicalNumberOfRows();   //所有行数
        int colNum = row.getPhysicalNumberOfCells();   //所有列数
//        Cell cell = sheet.getRow(rowNum).getCell(colNum);
        for (int i = 1; i < rowNum; i++) {
            XSSFRow currentRow = sheet.getRow(i);
            String[] eachRow = new String[currentRow.getPhysicalNumberOfCells()];
            for (int j = 0; j < colNum; j++) {
//                cell.setCellType(Cell.CELL_TYPE_STRING);
                currentRow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                eachRow[j] = currentRow.getCell(j).getStringCellValue();
            }
            rows.add(eachRow);
        }
        return rows;
    }

    /**
     * 获取 解码以后的数据
     *
     * @param excelOriginRowsInfo
     * @return
     */
    private List<String[]> transformMd5Info(List<String[]> excelOriginRowsInfo) throws Exception {
        List<String[]> rows = new ArrayList<>();
        for (String[] row : excelOriginRowsInfo) {
            String encodePh = row[2];
            String encodeId = row[3];
            String[] decodeData = getDecodeData(encodePh, encodeId);
            row[2] = decodeData[0];
            row[3] = decodeData[1];
            //添加到新的list
            rows.add(row);
        }
        return rows;
    }

    /**
     * 写文件
     *
     * @param resultList
     */
    private void writeResult(List<String[]> resultList) throws Exception {
        //解密以后的结果
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        for (int i = 0; i < resultList.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            String[] rowInfo = resultList.get(i);
            for (int j = 0; j < rowInfo.length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(rowInfo[j]);
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream("D:\\userSystem\\out.xlsx");
            wb.write(fos);
            System.out.println("恭喜您！写入成功！！！！！！");
            fos.close();
        } catch (IOException e) {
            System.out.println("写入文件出错啦！");
            e.printStackTrace();
        }


    }


    /**
     * 解码具体的信息（手机号码，身份证号码)
     *
     * @param phoneMd5
     * @param idCardMd5
     * @return
     */
    private String[] getDecodeData(String phoneMd5, String idCardMd5) throws Exception {
        String[] result = new String[2];
        String url = "http://172.16.5.160:8084/md5/query";
        String postJson = "{\"phone_md5\":\"%s\",\"id_card_md5\":\"%s\"}";
        postJson = String.format(postJson, phoneMd5, idCardMd5);
        // OkHttpUtils 调用
        JSONObject respParam = HttpUtils.postJsonResponse(url, postJson);
        String phone = respParam.getString("phone");
        String idCard = respParam.getString("id_card");
        Integer code = respParam.getInteger("code");
        if (code == 200) {
            result[0] = phone;
            result[1] = idCard;
        }
        if (code == 404) {
            result[0] = "";
            result[1] = "";
            System.out.println("系统异常");
        }
        // 解析返回结果
        return result;
    }

    @Test
    public void executeTask() {
        String path = "d:\\18.xlsx";
        List<String[]> excelRowsInfo = null;
        try {
            excelRowsInfo = getExcelRowsInfo(path);
            //System.out.println(JSON.toJSON(excelRowsInfo));
        } catch (Exception e) {
            System.out.println("读取文件失败");
            e.printStackTrace();
        }
        List<String[]> resultList = null;
        try {
            resultList = transformMd5Info(excelRowsInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writeResult(resultList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
