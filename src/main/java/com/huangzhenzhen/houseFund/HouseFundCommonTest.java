package com.huangzhenzhen.houseFund;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.huangzhenzhen.houseFund.vo.AuthorizeTask;
import com.huangzhenzhen.houseFund.vo.LoginMethodEnum;
import com.huangzhenzhen.houseFund.vo.RequiredParam;
import com.huangzhenzhen.utils.OkHttpUtils;
import okhttp3.ResponseBody;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseFundCommonTest {

    private static String commandUrl = "";
    //todo
    private static String houseFundfilePath = "d://houseFund.xlsx";
    private static Map<String, String> AUTHORIZE_HEADER = new HashMap<>();

    @Test
    public void testCase() throws Exception {
        SerializeConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        AUTHORIZE_HEADER.put("Authorization", "b2adf64605654d458e71deb45bd35e06");
        List<String[]> excelRowsInfo = getExcelRowsInfo(houseFundfilePath);
        List<AuthorizeTask> reqParamsList = ReqParams(excelRowsInfo, LoginMethodEnum.MULTIPLE_PARMAS);

        for (AuthorizeTask authorizeTask : reqParamsList) {
            ResponseBody taskResponseBody = OkHttpUtils.postJson(commandUrl, JSON.toJSONString(authorizeTask), AUTHORIZE_HEADER);
            System.out.println(JSON.toJSONString(authorizeTask));
            HttpJsonResponse taskResponse = JSONObject.parseObject(taskResponseBody.string(), HttpJsonResponse.class);
            System.out.println(JSON.toJSONString(taskResponse));
            if (taskResponse.getCode().equals("API_AUTHORIZE_INVOKE_SUCCESS")) {
                HttpJsonResponse resultResponse;
                String action;
                do {
                    ResponseBody resultResponseBody = OkHttpUtils.get(commandUrl + taskResponse.getData().toString(), null, AUTHORIZE_HEADER);
                    resultResponse = JSONObject.parseObject(resultResponseBody.string(), HttpJsonResponse.class);
                    System.out.println(JSON.toJSONString(resultResponse));
                    JSONObject returnInfo = JSONObject.parseObject(JSON.toJSONString(resultResponse));
                    writeResult(JSON.toJSONString(resultResponse));
                    JSONObject data = (JSONObject) returnInfo.get("data");
                    action = data.get("action").toString();
                    Thread.sleep(100);
                } while (action.equals("RUNNING"));
            }
        }


    }

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
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(0);
        int rowNum = sheet.getPhysicalNumberOfRows();   //所有行数
        int colNum = row.getPhysicalNumberOfCells();   //所有列数
        for (int i = 1; i < rowNum; i++) {
            XSSFRow currentRow = sheet.getRow(i);
            String[] eachRow = new String[currentRow.getPhysicalNumberOfCells()];
            for (int j = 0; j < colNum; j++) {
                currentRow.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                eachRow[j] = currentRow.getCell(j).getStringCellValue();
            }
            rows.add(eachRow);
        }
        return rows;
    }


    /**
     * requireParams
     * 处理task参数
     *
     * @param excelRowsInfo
     * @return
     */
    private List<AuthorizeTask> ReqParams(List<String[]> excelRowsInfo, LoginMethodEnum loginMethod) {
//        List<String[]> rows = new ArrayList<>();
        List<AuthorizeTask> authorizeTaskList = new ArrayList<>();
        for (String[] currentRow : excelRowsInfo) {
            AuthorizeTask authorizeTask = new AuthorizeTask();
            if (!currentRow[4].equals("该登录方式在近一个月的认证记录中未匹配到正确账号")) {
                List<RequiredParam> requiredParamList = new ArrayList<>();
                String[] strArgs = currentRow[4].split(",");
                for (String strArg : strArgs) {
                    RequiredParam requireParam = new RequiredParam();
                    String[] kv = strArg.split(":");
                    requireParam.setKey(kv[0]);
                    requireParam.setValue(kv[1]);
                    requiredParamList.add(requireParam);
                }
                authorizeTask.setRequiredParams(requiredParamList);
                authorizeTask.setLogin_method(loginMethod);
                String website = currentRow[2];
                String login_method_code = currentRow[3];
                authorizeTask.setLogin_method_code(login_method_code);
                authorizeTask.setWebsite(website);
                authorizeTask.setUser_id("");
                authorizeTaskList.add(authorizeTask);
            }

        }
        return authorizeTaskList;
    }


    /**
     * 返回结果写入文件
     *
     * @param result
     */
    private void writeResult(String result) {
        File file = new File("d://outHouseFund.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(result + "\r\n");
            bw.close();
        } catch (IOException e) {
            System.out.println("写入文件失败");
            e.printStackTrace();
        }

    }


}
