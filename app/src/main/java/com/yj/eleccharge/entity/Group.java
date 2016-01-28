package com.yj.eleccharge.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.db.sqlite.FinderLazyLoader;

/**
 * Created by kehuantiantang on 2015/9/2.
 */
@Table(name = "groups")
public class Group implements Parcelable{
    @Id
    private int id;

    @NotNull
    @Column(column = "name")
    private String name;

    @Column(column = "remark")
    private String remark;

    @Column(column = "decrip")
    private String decrip;

    @Column(column = "totalNum")
    private int totalNum;

    @Transient
    public double price;

    /**
     * 一个群组里面对应的一个设置
     */
    @Column(column = "setting")
    private String groupSetting;

    @Finder(valueColumn = "id", targetColumn = "groupId")
    private FinderLazyLoader<User> userForeign; // 关联对象多时建议使用这种方式，延迟加载效率较高。

    @Finder(valueColumn = "id", targetColumn = "groupId")
    private FinderLazyLoader<Price> priceForeign; // 关联对象多时建议使用这种方式，延迟加载效率较高。

    public Group(){
        super();
    }

    public Group(String name) {
        this.name = name;
    }

    protected Group(Parcel in) {
        id = in.readInt();
        name = in.readString();
        remark = in.readString();
        decrip = in.readString();
        totalNum = in.readInt();
        price = in.readDouble();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public int getId() {
        return id;
    }

    public FinderLazyLoader<User> getUserForeign() {
        return userForeign;
    }

    public FinderLazyLoader<Price> getPriceForeign() {
        return priceForeign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDecrip() {
        return decrip;
    }

    public void setDecrip(String decrip) {
        this.decrip = decrip;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    //Using in arrayAdapter's get text
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getGroupSetting() {
        return groupSetting;
    }

    public void setGroupSetting(String groupSetting) {
        this.groupSetting = groupSetting;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(remark);
        dest.writeString(decrip);
        dest.writeInt(totalNum);
        dest.writeDouble(price);
    }
}
