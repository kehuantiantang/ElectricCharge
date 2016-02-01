package yj.com.fileexplorer;

/**
 * Created by Sober on 2016/2/1.
 */
public enum ExplorerState {
    //只能进行选择一个文件夹操作
    SELECT,
    //可以打开文件这种
    READ_ONLY,
    //正常模式，可读，可写，可以进行文件的操作
    NORMAL

}
