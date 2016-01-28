package com.yj.eleccharge.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

/**
 * price table
 */
@Table(name = "price")
public class Price{
    @Id
    private int id;

    @NotNull
	@Column(column = "price")
    private double price;
	
	@Column(column = "remark")
	private String remark;

    @Foreign(column = "groupId", foreign = "id")
    public Group groupForeign;

    public Price() {
        super();
    }

    public Price(double price) {
        this.price = price;
    }
	
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	public String getRemark(){
		return remark;
	}

    public int getId() {
        return id;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
