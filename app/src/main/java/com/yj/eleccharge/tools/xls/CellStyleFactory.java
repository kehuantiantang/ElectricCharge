package com.yj.eleccharge.tools.xls;

import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.util.TypeAnnotation;
import com.yj.eleccharge.util.TypeElement;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Sober on 2015/12/31.
 * 自定义风格类工厂
 */
public class CellStyleFactory {
    //	传统类型的风格
    private static HSSFCellStyle customBooleanStyle;
    private static HSSFCellStyle customIntegerStyle;
    private static HSSFCellStyle customFloatStyle;
    private static HSSFCellStyle customDoubleStyle;
    private static HSSFCellStyle customDateStyle;
    private static HSSFCellStyle customStringStyle;
    private static HSSFCellStyle customLongStyle;

    private static CellStyleFactory cellStyleFactory;


    private CellStyleFactory() {
        super();
    }

    /**
     * 没有太多线程同步问题，就直接使用懒汉模式吧
     *
     * @return
     */
    public static synchronized CellStyleFactory getInstance() {
        if (cellStyleFactory == null) {
            cellStyleFactory = new CellStyleFactory();
        }
        return cellStyleFactory;
    }

    public HSSFCellStyle getCustomStyle(TypeElement type , HSSFWorkbook workbook){
        String typeString;
        if (!type.equals(TypeElement.IGNORE.toString())) {
            typeString = "getCustom" + type.toString() + "Style";
            try {
                Class mClass = Class.forName(CellStyleFactory.class.getName());
                Method method = mClass.getDeclaredMethod(typeString, HSSFWorkbook.class);
                return (HSSFCellStyle) method.invoke(cellStyleFactory, workbook);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 返回指定类型的对象
     *
     * @param field
     * @return
     */
    public HSSFCellStyle getCustomStyle(Field field, HSSFWorkbook workbook) {
        TypeAnnotation typeAnnotation = field.getAnnotation(TypeAnnotation.class);
        if (typeAnnotation == null) {
            throw new RuntimeException("Please add typeAnnotation in field");
        } else {
            TypeElement typeElement = typeAnnotation.type();
            String type = typeElement.toString();
            if (!type.equals(TypeElement.IGNORE.toString())) {
                type = "getCustom" + type + "Style";
                try {
                    Class mClass = Class.forName("com.yj.eleccharge.tools.xls.CellStyleFactory");
                    return (HSSFCellStyle) mClass.getDeclaredMethod(type, HSSFWorkbook.class).invoke(cellStyleFactory, workbook);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 资源消耗完完成后，将实例化的style对象摧毁
     */
    public void destroy() {
        customBooleanStyle = null;
        customDateStyle = null;
        customStringStyle = null;
        customIntegerStyle = null;
        customFloatStyle = null;
        customDoubleStyle = null;
        customLongStyle = null;
        System.gc();
    }


    /**
     * 建立标题 头的样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomTitleStyle(HSSFWorkbook workbook) {
        HSSFCellStyle customTitleStyle = this.setTypeStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = this.setTypeFont(workbook, HSSFColor.BLACK.index, "隶书",
                HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
        // 把字体应用到当前的样式
        customTitleStyle.setFont(font);
        return customTitleStyle;
    }

    /**
     * 传统的boolean类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomBooleanStyle(HSSFWorkbook workbook) {
        if (customBooleanStyle == null) {
            customBooleanStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_CENTER);
            HSSFFont font = setTypeFont(workbook, HSSFColor.BLACK.index,
                    "宋体", HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customBooleanStyle.setFont(font);
        }
        return customBooleanStyle;
    }

    /**
     * 传统的Integer类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomIntegerStyle(HSSFWorkbook workbook) {
        if (customIntegerStyle == null) {
            customIntegerStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_RIGHT);
            HSSFFont font = this.setTypeFont(workbook,
                    HSSFColor.BLUE.index, "宋体",
                    HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customIntegerStyle.setFont(font);
        }
        return customIntegerStyle;
    }

    /**
     * 设置单元格中的字体样式
     *
     * @param workbook
     * @param color
     * @param fontName
     * @param boldWeightBold
     * @param size
     * @return
     */
    private HSSFFont setTypeFont(HSSFWorkbook workbook, short color,
                                 String fontName, short boldWeightBold, short size) {
        HSSFFont textFont = workbook.createFont();
        textFont.setColor(color);
        textFont.setFontName(fontName);
        textFont.setBoldweight(boldWeightBold);
        textFont.setFontHeightInPoints(size);
        return textFont;
    }

    /**
     * 设置单元格的对齐样式
     *
     * @param workbook
     * @param align    对齐样式
     * @return
     */
    private HSSFCellStyle setTypeStyle(HSSFWorkbook workbook, short align) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(align);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }

    /**
     * 传统的Float类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomFloatStyle(HSSFWorkbook workbook) {
        if (customFloatStyle == null) {
            customFloatStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_RIGHT);
            HSSFFont font = this.setTypeFont(workbook,
                    HSSFColor.GREEN.index, "宋体",
                    HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customFloatStyle.setFont(font);
        }
        return customFloatStyle;
    }

    /**
     * 传统的Date类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomDateStyle(HSSFWorkbook workbook) {
        if (customDateStyle == null) {
            customDateStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_CENTER);
            HSSFFont font = this.setTypeFont(workbook, HSSFColor.BLACK.index,
                    "宋体", HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customDateStyle.setFont(font);
        }
        return customDateStyle;
    }

    /**
     * 传统的String类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomStringStyle(HSSFWorkbook workbook) {
        if (customStringStyle == null) {
            customStringStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_CENTER);
            HSSFFont font = this.setTypeFont(workbook, HSSFColor.BLACK.index,
                    "隶书", HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customStringStyle.setFont(font);
        }
        return customStringStyle;
    }

    /**
     * 传统的Long类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomLongStyle(HSSFWorkbook workbook) {
        if (customLongStyle == null) {
            customLongStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_CENTER);
            HSSFFont font = this.setTypeFont(workbook, HSSFColor.BLACK.index,
                    "宋体", HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customLongStyle.setFont(font);
        }
        return customLongStyle;
    }

    /**
     * 传统的Double类型样式
     *
     * @param workbook
     * @return
     */
    private HSSFCellStyle getCustomDoubleStyle(HSSFWorkbook workbook) {
        if (customDoubleStyle == null) {
            customDoubleStyle = this.setTypeStyle(workbook,
                    HSSFCellStyle.ALIGN_RIGHT);
            HSSFFont font = this.setTypeFont(workbook, HSSFColor.BLACK.index,
                    "宋体", HSSFFont.BOLDWEIGHT_BOLD, AppConfig.CUSTOM_CONTENT_TEXT_SIZE);
            customDoubleStyle.setFont(font);
        }
        return customDoubleStyle;
    }
}
