package com.yj.eleccharge.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.yj.eleccharge.AppConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 对数据库进行还原和备份的AsyncTask
 */
public class BackupTask extends AsyncTask<String, Void, Integer> {
    
    private Context mContext;
    private static final String COMMAND_BACKUP = "backupDatabase";
    private static final String COMMAND_RESTORE = "restroeDatabase";

    private	static final Integer COMMAND_BACKUP_SUCCESS = 0;
    private	static final Integer COMMAND_BACKUP_FAILED = 1;
    private	static final Integer COMMAND_RESTORE_SUCCESS = 2;
    private	static final Integer COMMAND_RESTORE_FAILED = 3;


    public BackupTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub

        // 获得正在使用的数据库路径，我的是 sdcard 目录下的 /dlion/db_dlion.db
    	// 默认路径是 /data/data/(包名)/databases/*.db
        File dbFile = mContext.getDatabasePath(AppConfig.DATABASE_NAME).getAbsoluteFile();
        
		File exportDir = new File(AppConfig.DEFAULT_BACKUP_DIR);
		
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        
//        备份的文件
        File backup = new File(exportDir, dbFile.getName());
        String command  = params[0];
        if (command.equals(COMMAND_BACKUP)) {
            try {
                backup.createNewFile();
                fileCopy(dbFile, backup);
                Log.d("backup", "ok");
                return COMMAND_BACKUP_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("backup", "fail");
                return COMMAND_BACKUP_FAILED;
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                fileCopy(backup, dbFile);
                Log.d("restore", "success");
                return COMMAND_RESTORE_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("restore", "fail");
                return COMMAND_RESTORE_FAILED;
            }
        } else {
            return null;
        }
    }

    /**
     * 拷贝文件
     * @param dbFile 数据库文件
     * @param backup 备份文件
     * @throws IOException
      */
    public static void fileCopy(File dbFile, File backup) throws IOException {
    	FileInputStream input = new FileInputStream(dbFile);
    	FileOutputStream output = new FileOutputStream(backup);
    	
        FileChannel inChannel = input.getChannel();
        FileChannel outChannel = output.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
            output.close();
            input.close();
        }
    }

    /**
     * 调用结束后UI的处理
     */
	@Override
	protected void onPostExecute(Integer result) {
		Dialog dialog;
		if(result == COMMAND_RESTORE_FAILED){
			dialog = new AlertDialog.Builder(mContext)
					.setTitle("警告")
					.setMessage(
							"未找到备份文件，请将备份文件放在\n/mnt/sdcard/ElectricCharge/backup文件夹中,\n并命名为ElectricCharge.db")
					.setPositiveButton("确认", null).create();
			dialog.show();
		}else if(result == COMMAND_RESTORE_SUCCESS){
			dialog = new AlertDialog.Builder(mContext).setTitle("警告")
					.setMessage("恭喜您，还原成功").setPositiveButton("确认", null)
					.create();
			dialog.show();
		}else if(result == COMMAND_BACKUP_FAILED){
			dialog = new AlertDialog.Builder(mContext).setTitle("警告")
					.setMessage("对不起，备份失败，请检测sd卡是否插入").setPositiveButton("确认", null)
					.create();
			dialog.show();
		}else if(result == COMMAND_BACKUP_SUCCESS){
			dialog = new AlertDialog.Builder(mContext).setTitle("警告")
					.setMessage("恭喜您，备份成功").setPositiveButton("确认", null)
					.create();
			dialog.show();
		}
	}
    
    
}