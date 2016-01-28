package yj.com.fileexplorer;

import java.io.File;

/**
 * Created by Sober on 2016/1/27.
 */
public class FileItem {
    private int imageId;
    private String name = "";
    private String modifyTime = "";
    //文件夹中子文件的数量
    private String count = "";
    //文件的大小
    private String size = "";
    //一些其他的信息
    private String other = "";
    //该文件
    private File file;
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }



    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}


