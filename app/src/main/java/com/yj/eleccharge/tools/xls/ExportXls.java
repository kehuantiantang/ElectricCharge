package com.yj.eleccharge.tools.xls;

import android.util.Log;

import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.util.TypeAnnotation;
import com.yj.eleccharge.util.TypeElement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 指挥生成xls数据 应用泛型，代表任意一个符合javabean风格的类
 * 注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 * byte[]表jpg格式的图片数据
 */
public class ExportXls {
    private Map<String, ?> settingMap;
    private XlsData xlsData;
    private CellStyleFactory cellStyleFactory;

    /**
     * 按照数据集合要求生成数据
     *
     * @param dataSet
     * @param settingMap
     */
    public ExportXls(List<XlsData.XlsLine> dataSet, Map<String, ?> settingMap) {
        this.settingMap = settingMap;
        initData(dataSet);
        cellStyleFactory = CellStyleFactory.getInstance();
    }

    /**
     * 初始化数据集，将他包装成一个类
     * @param dataSet
     */
    private void initData(List<XlsData.XlsLine> dataSet) {
        this.xlsData = new XlsData();
        xlsData.setListData(dataSet);

        //获得所有的属性
        Field[] fields = XlsData.XlsLine.class.getDeclaredFields();

        //按照key进行排序的Map排序树
        Map<String, String> subTitlesMap = new TreeMap<>();

        //遍历每一个field看看是否在setting中需要
        for (Field field : fields) {
            //从设置中获得是否是否需要这个属性
            String object = (String)this.settingMap.get(field.getName());
            if ("true".equals(object)) {
                //不是要忽略的元素
                if (field.isAnnotationPresent(TypeAnnotation.class) && field.getAnnotation(TypeAnnotation.class).type() != TypeElement.IGNORE) {
                    //field中文名/英文名的情况 --> 户号/code
                    subTitlesMap.put(field.getAnnotation(TypeAnnotation.class).name(), field.getName());
                }
            }
        }
        xlsData.setSubTitlesMap(subTitlesMap);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        xlsData.setTitle(title);
    }

    /**
     * 设置备注
     *
     * @param remark
     */
    public void setRemark(String remark) {
        xlsData.setRemark(remark);
    }

    /**
     * 设置签名
     *
     * @param sign
     */
    public void setSign(String sign) {
        xlsData.setSign(sign);
    }

    /**
     * 设置时间，就是日期
     *
     * @param time
     */
    public void setTime(String time) {
        xlsData.setTime(time);
    }


    /**
     * 根据规则生成xls文件
     * @param out
     */
    public void exportExcel(OutputStream out) {

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        String title = this.xlsData.getTitle() == null ? (String)this.settingMap.get("title") : this.xlsData.getTitle();
        if("".equals(title.trim())){
            title = (String)this.settingMap.get("title");
        }
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为12个字节
        sheet.setDefaultColumnWidth(AppConfig.DEFAULT_COLUMN_WIDTH);
        // 设置表格行高为20个字节
        sheet.setDefaultRowHeightInPoints(AppConfig.DEFAULT_COLUMN_HEIGHT);


        Set<String> subTitles = this.xlsData.getSubTitlesMap().keySet();
        int headerLength = subTitles.size();

        // 合并单元格,创建大标题
        this.setMergedRegion(AppConfig.START_TITLE_ROW, 0, headerLength - 1, "宋体",
                HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_TITLE_TEXT_SIZE,
                HSSFCellStyle.ALIGN_CENTER, title, workbook);

        //		合并单元格，创建时间行
        String date = this.xlsData.getTime() == null ? Charge.getFormatDate(Calendar.getInstance()) : this.xlsData.getTime();
        this.setMergedRegion(AppConfig.START_TIME_ROW, 0, headerLength - 1, "隶书",
                HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE,
                HSSFCellStyle.ALIGN_RIGHT, date, workbook);


        // 产生表格的子标题行
        HSSFCellStyle titleStyle = cellStyleFactory.getCustomStyle(TypeElement.SUBTITLE, workbook);
        Iterator<String> subTitleIterator = subTitles.iterator();
        int index = 0;
        HSSFRow subTitleRow = sheet.createRow(AppConfig.START_CONTENT_ROW - 1);
        while (subTitleIterator.hasNext()){
            String subTitleString = subTitleIterator.next().split("\\|")[1];
            HSSFCell cell = subTitleRow.createCell(index++);
            HSSFRichTextString text = new HSSFRichTextString(subTitleString);

            cell.setCellStyle(titleStyle);
            cell.setCellValue(text);

            Log.e(this.getClass().getName(), subTitleString);
        }

        // 遍历集合数据，产生数据行
        Iterator<XlsData.XlsLine> dataIterator = this.xlsData.getListData().iterator();
        int i = AppConfig.START_CONTENT_ROW;
        while (dataIterator.hasNext()) {
            HSSFRow row = sheet.createRow(i++);
            XlsData.XlsLine xlsLine = dataIterator.next();
            int j = 0;
            for(String key : subTitles){
                //要的到的属性
                String property = this.xlsData.getSubTitlesMap().get(key);
                try {
                    Field field = xlsLine.getClass().getDeclaredField(property);
                    //可以读取值
                    field.setAccessible(true);
                    HSSFCell cell = row.createCell(j++);
                    HSSFRichTextString textValue = new HSSFRichTextString((String) field.get(xlsLine));
                    cell.setCellStyle(this.cellStyleFactory.getCustomStyle(field, workbook));
                    cell.setCellValue(textValue);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        //签名的行
        String sign = this.xlsData.getSign() == null ? (String)this.settingMap.get("sign") : this.xlsData.getSign();
        if(!" ".equals(sign.trim())){
            i += 2;
            HSSFRow signRow = sheet.createRow(i);
            HSSFCell signCell = signRow.createCell(headerLength - 1);
            signCell.setCellStyle(this.cellStyleFactory.getCustomStyle(TypeElement.STRING, workbook));
            signCell.setCellValue(sign);
        }

        //最后备注信息的行
        String remark = this.xlsData.getRemark() == null ? (String)this.settingMap.get("sign") : this.xlsData.getRemark();
        if(!" ".equals(remark)){
            this.setMergedRegion(i + 2, 0, headerLength - 1, "隶书",
                    HSSFFont.BOLDWEIGHT_BOLD, (short) 18, HSSFCellStyle.ALIGN_LEFT, remark,
                    workbook);
        }

        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//			资源清理
            this.cellStyleFactory.destroy();
        }
    }


    /**
     * 默认表单为第一个，设定合并显示的单元格
     *
     * @param row        合并的行单元格
     * @param firstCel   合并的开始列单元格，并作为一开始建立列的列号
     * @param secondCel  合并的结束列单元格
     * @param font       字体类型
     * @param boldWeight HSSFFont
     * @param textSize   字体大小
     * @param align      对其方式 HSSFCellStyle
     * @param string     要显示的字体
     * @param workbook   工作book
     */
    private void setMergedRegion(int row, int firstCel, int secondCel,
                                 String font, short boldWeight, short textSize, short align,
                                 String string, HSSFWorkbook workbook) {
        this.setMergedRegion(row, firstCel, secondCel, font, boldWeight,
                textSize, align, string, workbook, workbook.getSheetAt(0));
    }

    /**
     * 设定合并显示的单元格
     *
     * @param row        合并的行单元格
     * @param firstCel   合并的开始列单元格，并作为一开始建立列的列号
     * @param secondCel  合并的结束列单元格
     * @param font       字体类型
     * @param boldWeight HSSFFont
     * @param textSize   字体大小
     * @param align      对其方式 HSSFCellStyle
     * @param string     要显示的字体
     * @param workbook
     */
    private void setMergedRegion(int row, int firstCel, int secondCel,
                                 String font, short boldWeight, short textSize, short align,
                                 String string, HSSFWorkbook workbook, HSSFSheet sheet) {
        sheet.addMergedRegion(new CellRangeAddress(row, row, firstCel,
                secondCel));

        // 默认这里为第0个工作表
        HSSFRow rowHssfRow = sheet.createRow(row);
        HSSFCell cel = rowHssfRow.createCell(firstCel);

        HSSFRichTextString textString = new HSSFRichTextString(string);
        HSSFFont fontHssfFont = workbook.createFont();
        fontHssfFont.setFontHeightInPoints(textSize);
        fontHssfFont.setFontName(font);
        fontHssfFont.setBoldweight(boldWeight);
        textString.applyFont(fontHssfFont);
        cel.setCellValue(textString);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(align);
        cel.setCellStyle(style);
    }


}