package com.yj.eleccharge.tools.xls;

import com.yj.eleccharge.util.TypeAnnotation;
import com.yj.eleccharge.util.TypeElement;

import java.util.List;
import java.util.Map;

/**
 * 用来承载xls数据的数据集
 * Using to carry the xls data
 */
public class XlsData {
    /**
     * 大标题
     */
    @TypeAnnotation(type = TypeElement.SUBTITLE)
    private String title;

    /**
     * 小标题，每行的标题
     */
    private Map<String, String> subTitlesMap;


    /**
     * 时间戳
     */
    private String time;

    /**
     * 列表数据。一行一行的
     */
    private List<XlsLine> listData;

    /**
     * 备注
     */
    private String remark;

    /**
     * 一些签名，是谁抄写的
     */
    private String sign;

    public Map<String, String> getSubTitlesMap() {
        return subTitlesMap;
    }

    public void setSubTitlesMap(Map<String, String> subTitlesMap) {
        this.subTitlesMap = subTitlesMap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<XlsLine> getListData() {
        return listData;
    }

    public void setListData(List<XlsLine> listData) {
        this.listData = listData;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * xls表格里面每一行的数据
     */
    public class XlsLine implements Comparable<XlsLine> {
        /**
         * 用户编号
         */
        @TypeAnnotation(type = TypeElement.STRING, name = "a|户号")
        private String code;

        /**
         * 用户姓名
         */
        @TypeAnnotation(type = TypeElement.STRING, name = "b|姓名")
        private String name;

        /**
         * 上月电量
         */
        @TypeAnnotation(type = TypeElement.INTEGER, name = "c|上月电费")
        private String lastElec;

        @TypeAnnotation(type = TypeElement.INTEGER, name = "d|本月电费")
        private String nowElec;

        /**
         * 电量差
         */
        @TypeAnnotation(type = TypeElement.DOUBLE, name = "e|计费量")
        private String margin;


        /**
         * 单价
         */
        @TypeAnnotation(type = TypeElement.DOUBLE, name = "f|单价")
        private String price;


        /**
         * 总价格
         */
        @TypeAnnotation(type = TypeElement.DOUBLE, name = "g|金额(元)")
        private String aggregate;


        @TypeAnnotation(type = TypeElement.STRING, name = "h|电话号码")
        private String phone;

        @TypeAnnotation(type = TypeElement.STRING, name = "i|家庭住址")
        private String location;

        @TypeAnnotation(type = TypeElement.STRING, name = "j|电子邮箱")
        private String eMail;


        public String getAggregate() {
            return aggregate;
        }

        public void setAggregate(String total) {
            this.aggregate = total;
        }

        public XlsLine() {
            super();
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastElec() {
            return lastElec;
        }

        public void setLastElec(String lastElec) {
            this.lastElec = lastElec;
        }

        public String getNowElec() {
            return nowElec;
        }

        public void setNowElec(String nowElec) {
            this.nowElec = nowElec;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getMargin() {
            return margin;
        }

        public void setMargin(String margin) {
            this.margin = margin;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String geteMail() {
            return eMail;
        }

        public void seteMail(String eMail) {
            this.eMail = eMail;
        }

        @Override
        public int compareTo(XlsLine another) {
            return Integer.valueOf(this.code).compareTo(Integer.valueOf(another.code));
        }
    }
}
