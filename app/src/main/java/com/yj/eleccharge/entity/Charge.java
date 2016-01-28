package com.yj.eleccharge.entity;

/**
 * Created by kehuantiantang on 2015/9/2.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Table(name = "charge")
public class Charge implements Parcelable {
    @Id
    private int id;

    @NotNull
    @Column(column = "time")
    private String time;

    /**
     * 上月电量
     */
    @Column(column = "lastElec", defaultValue = "0.0")
    private String lastElec;

    @NotNull
    @Column(column = "nowElec")
    private String nowElec;

    @Column(column = "remark")
    private String remark;

    /**
     * 本月的电量差值
     */
    @Column(column = "margin")
    private String margin;

    /**
     * 电量差*单价
     */
    @Column(column = "aggregate")
    private String aggregate;

    /**
     * 电费计算的时候所对应的名字
     */
    @Transient
    public String name;

    /**
     * 用户所对应的编码
     */
    @Transient
    public String code;

    /**
     * judge this item whether fill in
     */
    @Transient
    public boolean isComplete = false;


    public String getLastElec() {
        return lastElec;
    }

    public void setLastElec(String lastElec) {
        this.lastElec = lastElec;
    }


    @Foreign(column = "userId", foreign = "id")
    public User userForeign;

    public Charge() {
        super();
    }

    public Charge(String time, String elec) {
        this.time = time;
        this.nowElec = elec;
    }

    protected Charge(Parcel in) {
        id = in.readInt();
        time = in.readString();
        nowElec = in.readString();
        remark = in.readString();
        name = in.readString();
        code = in.readString();
        lastElec = in.readString();
    }

    public static final Creator<Charge> CREATOR = new Creator<Charge>() {
        @Override
        public Charge createFromParcel(Parcel in) {
            return new Charge(in);
        }

        @Override
        public Charge[] newArray(int size) {
            return new Charge[size];
        }
    };

    public String getMargin() {
        return margin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAggregate() {
        return aggregate;
    }

    public void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    /**
     * 获得指定的时间的格式
     *
     * @param calender
     * @return
     */
    public static String getFormatDate(Calendar calender) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(calender.getTime());
    }

    /**
     * 获得要记录的上个月的时间的格式
     *
     * @return
     */
    public static String getNowFormatDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return getFormatDate(calendar);
    }

    /**
     * 获得要记录的上上个月的时间格式
     *
     * @return
     */
    public static String getLastFormatDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        return getFormatDate(calendar);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getNowElec() {
        return nowElec;
    }

    public void setNowElec(String nowElec) {
        this.nowElec = nowElec;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    /**
     * 序列化
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(time);
        dest.writeString(nowElec);
        dest.writeString(remark);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(lastElec);
    }
}
