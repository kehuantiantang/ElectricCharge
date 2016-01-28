package com.yj.eleccharge.tools;

import android.content.Context;
import android.util.Log;

import com.kehuantiantang.logcollection.utils.LogHelper;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.table.Table;
import com.lidroid.xutils.exception.DbException;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.entity.Price;
import com.yj.eleccharge.entity.User;
import com.yj.eleccharge.tools.xls.XlsData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kehuantiantang on 2015/9/2.
 * 数据操作，模拟添加或者删除信息
 */
public class DbTools {

    private static DbUtils db = null;

    synchronized public static DbUtils getInstance(Context context) {
        if (db == null) {
            db = DbUtils.create(context, "eleccharge.db", 1, null);
        }
        return db;
    }


    /**
     * 清除表中数据
     *
     * @param context
     */
    public static void clearData(Context context) {
        if (DbTools.db == null) {
            getInstance(context);
        }
        try {
            db.deleteAll(Charge.class);
            db.deleteAll(Group.class);
            db.deleteAll(Price.class);
            db.deleteAll(User.class);
            Log.e("db", "clear all the data !");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为所有的表创建一个空表
     * @param context
     */
    public static void initAllTable(Context context){
        if (DbTools.db == null) {
            getInstance(context);
        }
        Map<String, String> maps = Scanner.scan(context, "com.yj.eleccharge.entity");
        for(String key : maps.keySet()){
            try {
                db.createTableIfNotExist(Class.forName(maps.get(key)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除表及其数据
     *
     * @param context
     */
    public static void clearTable(Context context) {
        if (db == null) {
            getInstance(context);
        }
        Table.remove(db, Charge.class);
        Table.remove(db, Group.class);
        Table.remove(db, Price.class);
        Table.remove(db, User.class);
        Log.e("db", "clear all the table !");
    }

    /**
     * 按照时间和群组要求，生成列数据
     * @param context
     * @param time
     * @param groupId
     * @return
     */
    public static List<XlsData.XlsLine> generateData(Context context, String time, int groupId){
        if(db == null){
            getInstance(context);
        }

        List<XlsData.XlsLine> xlsLines = null;
        try {
            Group group = db.findFirst(Selector.from(Group.class).where("id", "=", groupId));
            if(group == null){
                throw new RuntimeException("Group can't find in database");
            }
            //找到每个群组里面的用户
            List<User> users = group.getUserForeign().getAllFromDb();

            //TODO 找到每个用户下面的时间下的电费
            List<Integer> userIds = new LinkedList<>();
            for(User user : users){
                //将要查询的UserId加入List
                userIds.add(user.getId());
            }
            Integer[] userIDS = userIds.toArray(new Integer[1]);
            //找到所有的电费记录
            List<Charge> charges = db.findAll(Selector.from(Charge.class)
                    .where("userId", "in", userIDS).and("time", "=", time));
            //得到的XlsLine数据集
            xlsLines = new LinkedList<>();
            for(Charge charge : charges){
                //TODO 这里可以动态添加的
                XlsData.XlsLine xlsLine = new XlsData().new XlsLine();
                User user = charge.userForeign;
                xlsLine.setCode(user.getCode());
                xlsLine.setName(user.getName());
                xlsLine.seteMail(user.getEmail());
                xlsLine.setPhone(user.getPhone());
                xlsLine.setLocation(user.getLocation());
                xlsLine.setPrice(String.valueOf(user.groupForeign.price));
                xlsLine.setLastElec(charge.getLastElec());
                xlsLine.setNowElec(charge.getNowElec());
                xlsLine.setMargin(charge.getMargin());
                xlsLine.setAggregate(charge.getAggregate());
                xlsLines.add(xlsLine);
            }
            //对xlsLines进行排序
            Collections.sort(xlsLines);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return xlsLines;
    }

    /**
     * 添加信息
     * @param context
     */
    public static void addData(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (db == null) {
                    getInstance(context);
                }
                List<Price> prices = new ArrayList<>();
                Random random = new Random();
                try {
                    for (int i = 0; i < 3; i++) {
                        Group group = new Group("name group " + i);
                        group.setRemark("this is test group " + i);
                        group.setTotalNum(i);
                        db.saveBindingId(group);

                        for (int j = 0; j < 2; j++) {
                            User user = new User();
                            user.setName(j + " group " + i);
                            user.setRemark("this is test user in group " + i + "in user " + j);
                            user.groupForeign = group;
                            user.setPhone("1300000000");
                            user.setCode(random.nextInt(999) + "");

                            db.saveBindingId(user);

                            List<Charge> charges = new ArrayList<>();
                            for (int z = 9; z <= 11; z++) {
                                Charge charge = new Charge("time " + z, "elec " + z);
                                charge.setRemark("this is test charge@ in user " + j + " in charge " + z);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.MONTH, z);
                                charge.setTime(charge.getFormatDate(calendar));
                                double nowElec = random.nextInt(9999);
                                double lastElec = random.nextInt(9999);
                                double margin = nowElec - lastElec >= 0 ? nowElec - lastElec : 9999.0 - nowElec + lastElec;
                                charge.setNowElec(nowElec+"");
                                charge.setLastElec(lastElec+"");
                                charge.setMargin(margin+"");
                                charge.setAggregate(random.nextInt(9999)+"");

                                charge.userForeign = user;
                                charges.add(charge);
                            }
                            db.saveBindingIdAll(charges);
                            LogHelper.e("db", "charge in user " + j + " set !");
                        }


                        Price price = new Price(Double.valueOf(i));
                        price.setRemark("this is test price " + i);
                        price.groupForeign = group;

                        prices.add(price);
                    }


                    db.saveBindingIdAll(prices);
                    LogHelper.e("db", "price set !");
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
