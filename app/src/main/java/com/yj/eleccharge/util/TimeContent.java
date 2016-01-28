package com.yj.eleccharge.util;

/**
 * Created by 游捷 on 2015/8/29.
 */
public class TimeContent {
    private String code;
    private String name;
    private String elect;
    private String values;
    private String group;

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

    public String getElect() {
        return elect;
    }

    public void setElect(String elect) {
        this.elect = elect;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public TimeContent(String code, String name, String elect, String values) {
        this.code = code;
        this.name = name;
        this.elect = elect;
        this.values = values;

    }
}
