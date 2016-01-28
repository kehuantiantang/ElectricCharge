package com.yj.eleccharge.util;

/**
 * Created by 游捷 on 2015/8/29.
 */
public class TimeTitle {
    private String time;
    private int userNum;
    private String totalCount;

    private String totalValues;

    public TimeTitle() {
        super();
    }

    public TimeTitle(String name) {
        this(name, 0, 0.0+"", 0.0+"");
    }

    public TimeTitle(String time, int userNum, String totalCount, String totalValues) {
        this.time = time;
        this.userNum = userNum;
        this.totalCount = totalCount;
        this.totalValues = totalValues;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTotalValues() {
        return totalValues;
    }

    public void setTotalValues(String totalValues) {
        this.totalValues = totalValues;
    }
}
