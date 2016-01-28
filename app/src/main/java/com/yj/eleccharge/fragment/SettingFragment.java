package com.yj.eleccharge.fragment;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.LayoutRipple;
import com.gc.materialdesign.widgets.Dialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.r0adkll.postoffice.styles.ListStyle;
import com.r0adkll.postoffice.styles.ProgressStyle;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.tools.SettingTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public class SettingFragment extends Fragment implements View.OnClickListener {


    private TextView maxCountTextView;
    private TextView saveDirTextView;
    private TextView backupTextView;

    /**
     * 保存setting到SharePreference的工具
     */
    private SharedPreferences sharedPreferencese;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这里设置了才会出现自定义的menu和ActionBar
        setHasOptionsMenu(true);

       //获得设置
        if(!SettingTools.isExist(this.getActivity().getBaseContext(), AppConfig.SETTING_SHAREDPREFERENCE)){
            this.sharedPreferencese = SettingTools.getSharedPreferencesFromStrings(getActivity().getBaseContext(), AppConfig.SETTING_SHAREDPREFERENCE, R.array.setting_system_default);
        }else{
            this.sharedPreferencese = this.getActivity().getSharedPreferences(AppConfig.SETTING_SHAREDPREFERENCE, 0);
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settting, null);
        this.initUI(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Delivery delivery = null;
        switch (v.getId()) {
            //设置电表最大值
            case R.id.setting_maxCountGroup:
               delivery =  PostOffice.newMail(getActivity())
                        .setTitle("电表最大额度")
                        .setThemeColorFromResource(R.color.colorPrimary)
                        .setDesign(Design.MATERIAL_LIGHT)
                        .showKeyboardOnDisplay(true)
                        .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.colorPrimary)
                        .setCanceledOnTouchOutside(true)
                        .setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setStyle(new EditTextStyle.Builder(getActivity())
                                .setHint("最大额度")
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                                .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                    @Override
                                    public void onAccepted(String text) {
                                        sharedPreferencese.edit().putString("maxCount", text).apply();
                                        maxCountTextView.setText(text);
                                    }
                                }).build())
                        .build();
                break;

            //file save path
            case R.id.setting_saveDirGroup:
                //TODO 选择文件目录
                Toast.makeText(getActivity(), "R.id.setting_saveDirGroup", Toast.LENGTH_SHORT).show();
                break;

            //database backup
            case R.id.setting_backupGroup:
                delivery = PostOffice.newMail(getActivity())
                        .setThemeColorFromResource(R.color.colorPrimary)
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setStyle(new ProgressStyle.Builder(getActivity())
                                .setProgressStyle(ProgressStyle.NORMAL)
                                .setProgressMessage("正在备份数据文件")
                                .build())
                        .setCancelable(true)
//                        .setCanceledOnTouchOutside(true)
                        .build();
                //TODO 进行备份处理
                break;

            //clean the xls file
            case R.id.setting_clearXls:
                delivery = PostOffice.newMail(getActivity())
                        .setThemeColorFromResource(R.color.colorPrimary)
                        .setTitle("清除数据")
                        .setMessage("确定清除数据？将会清除生成的xls表格数据！！！！！")
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCanceledOnTouchOutside(true)
                        .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.colorPrimary)
                        .setCancelable(true)
                        .setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO 清除xls有关的数据
                                Toast.makeText(getActivity(), "清楚数据成功", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .build();
                break;

            //清除xls的设置
            case R.id.setting_clearXlsSetting:
                //从数据库中筛选出需要的List
                final DbUtils db = DbTools.getInstance(getActivity());
                List<Group> groups = null;
                try {
                    groups = db.findAll(Group.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if(groups == null){
                    groups = new ArrayList<>();
                }

                //显示的ArrayList
                ArrayAdapter<Group> adapter = new ArrayAdapter<>(getActivity(), com.r0adkll.postoffice.R.layout.simple_listitem_mtrl_light, groups);

                //下拉菜单
                delivery = PostOffice.newMail(getActivity())
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCanceledOnTouchOutside(true)
                        .setTitle("删除")
                        .setStyle(new ListStyle.Builder(getActivity())
                                .setDrawSelectorOnTop(true)
                                .setDividerHeight(2)
                                .setOnItemAcceptedListener(new ListStyle.OnItemAcceptedListener<Group>() {
                                    @Override
                                    public void onItemAccepted(Group item, int position) {
                                        SettingFragment.this.getActivity().getSharedPreferences(item.getGroupSetting(), 0)
                                                .edit().clear().apply();
                                        item.setGroupSetting("");
                                        try {
                                            db.update(item);
                                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getActivity(), "删除设置失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .build(adapter)).build();
                break;

            //恢复默认的设置
            case R.id.setting_defaultGroup:
                delivery = PostOffice.newMail(getActivity())
                        .setThemeColorFromResource(R.color.colorPrimary)
                        .setTitle("恢复默认设置")
                        .setMessage("确定恢复默认设置？")
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCanceledOnTouchOutside(true)
                        .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.colorPrimary)
                        .setCancelable(true)
                        .setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingTools.resetDefault(SettingFragment.this.getActivity());
                                dialog.dismiss();
                            }
                        })
                        .build();
                break;
            default:
                Toast.makeText(getActivity(), "default", Toast.LENGTH_SHORT).show();

        }
        if (delivery != null) {
            delivery.show(getFragmentManager());
        }

    }


    private void initUI(View rootView) {

        LayoutRipple maxCountGroupLayoutRipple = (LayoutRipple) rootView.findViewById(R.id.setting_maxCountGroup);
        setOriginRiple(maxCountGroupLayoutRipple);

        LayoutRipple saveDirGroupLayoutRipple = (LayoutRipple) rootView.findViewById(R.id.setting_saveDirGroup);
        setOriginRiple(saveDirGroupLayoutRipple);

        LayoutRipple backupGroupLayoutRipple = (LayoutRipple) rootView.findViewById(R.id.setting_backupGroup);
        setOriginRiple(backupGroupLayoutRipple);

        LayoutRipple clearXlsLayoutRipple = (LayoutRipple) rootView.findViewById(R.id.setting_clearXls);
        setOriginRiple(clearXlsLayoutRipple);


        LayoutRipple clearXlsSettingLayoutRipple = (LayoutRipple) rootView.findViewById(R.id.setting_clearXlsSetting);
        setOriginRiple(clearXlsSettingLayoutRipple);

        LayoutRipple defaultLayoutRipple = (LayoutRipple) rootView.findViewById(R.id.setting_defaultGroup);
        setOriginRiple(defaultLayoutRipple);


        maxCountTextView = (TextView) rootView.findViewById(R.id.setting_maxCount);
        maxCountTextView.setText(sharedPreferencese.getString("maxCount", "9999.0"));
        saveDirTextView = (TextView) rootView.findViewById(R.id.setting_saveDir);
        saveDirTextView.setText(sharedPreferencese.getString("setting_saveDir", ""));
        backupTextView = (TextView) rootView.findViewById(R.id.setting_backup);
        backupTextView.setText(sharedPreferencese.getString("setting_backup", ""));
    }

    //设置Ripple初始参数
    private void setOriginRiple(final LayoutRipple layoutRipple) {
        layoutRipple.setOnClickListener(this);
        layoutRipple.post(new Runnable() {

            @Override
            public void run() {
                layoutRipple.setRippleColor(Color.parseColor("#BBDEFB"));

                layoutRipple.setRippleSpeed(40);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.global, menu);
    }

}
