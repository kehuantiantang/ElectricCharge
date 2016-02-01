package yj.com.fileexplorer.listener;

import java.io.File;

import yj.com.fileexplorer.FileTools;

/**
 * Created by Sober on 2016/2/1.
 */
public interface IDataUpdate {
    public void updateCurrentDir(File currentDir);
    public void updateOperType(FileTools.OperateType operateType);
}
