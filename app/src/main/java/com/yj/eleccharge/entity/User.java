package com.yj.eleccharge.entity;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.db.sqlite.FinderLazyLoader;

/**
 * Created by kehuantiantang on 2015/9/2.
 */
@Table(name = "user")
public class User implements Parcelable {
    @Id
    private int id;

    @Transient
    public Drawable icon;

    @Transient
    public String group;

    @NotNull
    @Column(column = "code")
    private String code;

    @NotNull
    @Column(column = "name")
    private String name;

    @Column(column = "email")
    private String email;

    @Column(column = "phone")
    private String phone;

    @Column(column = "location")
    private String location;

    @Column(column = "remark")
    private String remark;

    @Column(column = "birthday")
    private String birthday;

    @Foreign(column = "groupId", foreign = "id")
    public Group groupForeign;

    @Finder(valueColumn = "id", targetColumn = "userId")
    private FinderLazyLoader<Charge> chargeForeign; // 关联对象多时建议使用这种方式，延迟加载效率较高。


    public User(){
        super();
    }

    protected User(Parcel in) {
        id = in.readInt();
        group = in.readString();
        code = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        location = in.readString();
        remark = in.readString();
        birthday = in.readString();
        groupForeign = in.readParcelable(Group.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }


    public FinderLazyLoader<Charge> getChargeForeign() {
        return chargeForeign;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(group);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(location);
        dest.writeString(remark);
        dest.writeString(birthday);
        dest.writeParcelable(groupForeign, flags);
    }
}
