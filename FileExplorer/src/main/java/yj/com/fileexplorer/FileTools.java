package yj.com.fileexplorer;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Sober on 2016/1/5.
 * 文件工具类，提供对文件夹的选择和读取
 */
public class FileTools {
    private Context context;
    private static Map<String, List<String>> suffixMap;
    private String TAG = getClass().getSimpleName();
    private boolean showHideFile = false;

    /**
     * 初始化需要查询的后缀名的map数组
     */
    static {
        suffixMap = new HashMap<>();
        String[] app = {"apk", "gpk", "sis", "sga", "ipa", "prc"};
        String[] audio = {"wma", "mp3", "wav", "mid", "MP3", "mp2", "mp1", "midi", "AAC", "WAV", "WMA", "CDA", "FLAC", "M4A", "MID", "MKA", "MP2", "MPA", "MPC", "APE", "OFR", "OGG", "RA", "WV", "TTA", "AC3", "DTS"};
        String[] binary = {"bat", "exe", "com", "obj", "bin"};
        String[] calendar = {"cal"};
        String[] cdimage = {"ape", "chs", "iso", "img", "gho", "dat", "ima", "vmdk"};
        String[] compress = {"rar", "zip", "7-zip", "ace", "arj", "bz2", "cab", "gzip", "jar", "lzh", "tar", "uue", "xz", "z"};
        String[] contact = {"vcf", "vcard", "ttf"};
        String[] database = {"MDF", "mdb", "mdf", "db", "wdb", "dbf", "db3"};
        String[] document = {"doc", "docx", "docm", "dotx", "dotm", "xps", "odt",
                "ppt", "pptx", "pptm", "potx", "potm", "pot", "thmx", "ppsx", "ppsm", "pps", "ppam", "ppa",};
        String[] ebook = {"ceb", "xeb", "caj"};
        String[] email = {"pst", "eml", "eml", "msg", "msg", "oft", "dbx"};
        String[] executable = {"rc", "sh"};
        String[] feed = {"RSS", "opml"};
        String[] font = {"ttf", "FON"};
        String[] image = {"BMP", "JPG", "JPEG", "PNG", "BMP", "GIF", "PCX", "TIFF", "TGA", "EXIF", "FPX", "SVG", "PSD", "CDR", "PCD", "DXF", "UFO", "EPS", "HDRI", "AI", "RAW", "EMF", "LIC", "EPS", "HDRI"};
        String[] pdf = {"pdf", "rtf"};
        String[] security = {"rxf", "fse", "axx", "uea", "crt", ""};
        String[] shell = {"DOS", "cfg", "vbs", "reg", "js", "inf"};
        String[] source = {"rc"};
        String[] spreadsheet = {"XLA", "XLB", "XLC", "XLD", "XLK", "XLL", "XLM", "XLS", "XLSHTML", "XLT", "XLTHTML", "XLV", "xlsx", "xls", "xlsx", "xlsm", "xlsb", "cvs", "xlam", "xla", "xlt", "xltm", "prn", "xltx", "xlv"};
        String[] system = {"ACA", "acf ", "cpl"};
        String[] text = {"ass", "log", "xml", "chm", "java", "c", "dll", "lib", "dsw", "dsp", "cpp", "cs", "asp", "aspx", "php", "jsp", "txt"};
        String[] video = {"AVI", "ASF", "AVS", "OGM", "VOB", "TP", "NSV", "FLV", "aiff", "avi", "mov", "mpeg", "mpg", "qt", "ram", "viv", "AVI", "MPEG", "MPG", "DAT", "RA", "RM", "RMVB", "ASF", "WMV", "mpg", "asf", "divx", "mpg", "mpeg", "mpe", "wmv", "mp4", "vob"};

        suffixMap.put("app", Arrays.asList(app));
        suffixMap.put("audio", Arrays.asList(audio));
        suffixMap.put("binary", Arrays.asList(binary));
        suffixMap.put("calendar", Arrays.asList(calendar));
        suffixMap.put("cdimage", Arrays.asList(cdimage));
        suffixMap.put("compress", Arrays.asList(compress));
        suffixMap.put("contact", Arrays.asList(contact));
        suffixMap.put("database", Arrays.asList(database));
        suffixMap.put("document", Arrays.asList(document));
        suffixMap.put("ebook", Arrays.asList(ebook));
        suffixMap.put("email", Arrays.asList(email));
        suffixMap.put("executable", Arrays.asList(executable));
        suffixMap.put("feed", Arrays.asList(feed));
        suffixMap.put("font", Arrays.asList(font));
        suffixMap.put("image", Arrays.asList(image));
        suffixMap.put("pdf", Arrays.asList(pdf));
        suffixMap.put("security", Arrays.asList(security));
        suffixMap.put("shell", Arrays.asList(shell));
        suffixMap.put("source", Arrays.asList(source));
        suffixMap.put("spreadsheet", Arrays.asList(spreadsheet));
        suffixMap.put("system", Arrays.asList(system));
        suffixMap.put("text", Arrays.asList(text));
        suffixMap.put("video", Arrays.asList(video));
    }

    public FileTools(Context context) {
        this.context = context;
    }


    public void setShowHideFile(boolean enabled) {
        this.showHideFile = enabled;
    }

    /**
     * 获得外置存储器的位置
     *
     * @return
     */
    public String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    /**
     * sd卡是否存在
     *
     * @return
     */
    public boolean sdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public boolean isParentPath(String path) {
        return Environment.getExternalStorageDirectory().getAbsolutePath().equalsIgnoreCase(path);
    }


    /**
     * 格式化大小
     *
     * @param size
     * @return
     */
    public String formatFileSize(double size) {
        if (size < 1024f) {
            return String.format("%.1f B", size);
        } else if (size < 1024 * 1024f) {
            return String.format("%.1f KB", size / 1024.0f);
        } else if (size < 1024 * 1024 * 1024f) {
            return String.format("%.1f MB", size / 1024.0f / 1024.0f);
        } else if (size < 1024 * 1024 * 1024 * 1024f) {
            return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
        } else {
            return String.format("%.1f TB", size / 1024.0f / 1024.0f / 1024.0f / 1024.0f);
        }
    }


    /**
     * 获得所有的存储卡的地址
     *
     * @return
     */
    public Set<String> getExternalStoragePaths() {
        Set<String> paths = new TreeSet<>();
        //主要存储卡，默认存储卡
        paths.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        //读外置存储卡

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "/proc/mounts"));
            String line;
            while ((line = reader.readLine()) != null) {
                //不包含mnt,storage,sdcard，包含asec,tmpfs,none跳过
                if ((!line.contains("/mnt") && !line.contains("/storage") && !line
                        .contains("/sdcard"))
                        || line.contains("asec")
                        || line.contains("tmpfs") || line.contains("none")) {
                    continue;
                }
                String[] info = line.split(" ");
                paths.add(info[1]);
            }
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


        return paths;
    }


    /**
     * 返回根路径
     *
     * @param path
     * @return
     */
    public String getParentPath(String path) {
        if (isParentPath(path)) {
            return null;
        } else {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            } else {
                return file.getParent();
            }
        }
    }



    public Comparator<FileItem> increaseNameSort() {
        return new Comparator<FileItem>() {
            @Override
            public int compare(FileItem lhs, FileItem rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        };
    }

    public Comparator<FileItem> decreaseNameSort() {
        return new Comparator<FileItem>() {
            @Override
            public int compare(FileItem lhs, FileItem rhs) {
                return -(lhs.getName().compareToIgnoreCase(rhs.getName()));
            }
        };
    }

    /**
     * 按照文件大小从大到小排序
     *
     * @return
     */
    public Comparator<FileItem> sizeSort() {
        return new Comparator<FileItem>() {
            @Override
            public int compare(FileItem lhs, FileItem rhs) {
                long lhsize = lhs.getSize() == "" ? 0 : Integer.valueOf(lhs.getSize());
                long rhsize = rhs.getSize() == "" ? 0 : Integer.valueOf(rhs.getSize());
                return lhsize > rhsize ? 1 : 0;
            }
        };
    }


    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        return sdf.format(new Date((time)));
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public void openFile(File file) {
        //TODO 打开文件
    }


    /**
     * 删除文件
     *
     * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
     * @return boolean
     */
    public boolean delFile(String filePathAndName) {
        File delFile;
        try {
            delFile = new File(filePathAndName);
            if (delFile.isDirectory()) {
                delFolder(filePathAndName);
            } else {
                delFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + " " + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + File.separator + " " + tempList[i]);// 再删除空文件夹
            }
        }
    }


    /**
     * 删除文件夹
     *
     * @param folderPath String 文件夹路径及名称 如c:/fqf
     * @return String
     */
    public boolean delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 新建目录
     *
     * @param folderPath
     * @return boolean
     */
    public boolean newFolder(String folderPath) throws Exception {
        String filePath = folderPath;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
        if (!myFilePath.exists()) {
            if (!myFilePath.mkdir()) {
                return false;
            }
        }
        return true;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return String 返回拷贝文件的日志
     */
    public boolean copyFile(String oldPath, String newPath) {
        try {
            int byteSum = 0;
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.isDirectory()) {
                return copyFolder(oldPath, newPath);
            }
            InputStream inStream = null; // 读入原文件
            if (oldFile.exists()) { // 文件存在时
                inStream = new FileInputStream(oldPath);
            }
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead; // 字节数 文件大小
                System.out.println(byteSum);
                fs.write(buffer, 0, byteRead);
            }
            inStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return false;
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean 拷贝文件是否成功
     */
    public boolean copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File oldFile = new File(oldPath);
            String[] childrenFilePaths = oldFile.list();
            File temp;
            for (String path : childrenFilePaths) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + path);
                } else {
                    temp = new File(oldPath + File.separator + path);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/ " + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + File.separator + " " + path, newPath + File.separator + " " + path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 移动文件到指定目录
     *
     * @param oldPath String 如：c:/fqf.txt
     * @param newPath String 如：d:/fqf.txt
     */
    public void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }


    /**
     * 移动文件夹到指定目录
     *
     * @param oldPath String 如：c:/fqf
     * @param newPath String 如：d:/fqf
     */
    public void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * 分享文件
     *
     * @param file
     */
    public void shareFile(File file) {

    }


    /**
     * 获得一个文件夹的中的所有孩子的内容
     * @param path
     * @return
     */
    public List<FileItem> getChildrenFilesInfo(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("对不起，该文件不存在");
        } else if (!file.canRead()) {
            throw new Exception("非常抱歉，无法读取该文件");
        } else if (!file.isDirectory()) {
            throw new Exception("该文件不是文件夹");
        } else {
            List<FileItem> details = new ArrayList<>();
            File[] children = file.listFiles();
            for (File child : children) {
                FileItem fileItem = getFileInfo(child.getAbsolutePath());
                if (fileItem != null) {
                    details.add(fileItem);
                }
            }
            return details;
        }
    }


    /**
     * 获得一个文件的详细信息
     * @param path
     * @return
     * @throws Exception
     */
    public String getFileDetailInfo(String path) throws Exception{
        File file = new File(path);
        StringBuffer detailInfo = new StringBuffer();
        if(!file.exists()){
            throw  new Exception("文件不存在!");
        }else{
            detailInfo.append("文件名称: \t" + file.getName() + "\n");
            detailInfo.append("文件种类: \t" + fileType(file.getName()) + "\n");
            detailInfo.append("隐藏文件: \t" + (file.isHidden() ? "是" : "否") + "\n");
            detailInfo.append("文件大小: \t" + this.formatFileSize(file.length()) + "\n");
            detailInfo.append("修改时间: \t" + this.formatTime(file.lastModified()) + "\n");
            detailInfo.append("权限: \t\t\t" + (file.canRead() ? "可读" : "不可读") + " | " +
                    (file.canWrite() ? "可写" : "不可写") + " | " +
                    (file.canExecute() ? "可执行" : "不可执行") + "\n");
            detailInfo.append("路径: \t\t\t" + file.getAbsolutePath() + "\n");
        }
        return detailInfo.toString();
    }


    /**
     * 获得显示List的需要的一些文件信息
     * @param path
     * @return
     */
    public FileItem getFileInfo(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("对不起，该文件不存在");
        } else {
            //开头为"."的隐藏文件，不显示
            if (file.getName().startsWith(".") && !showHideFile) {
                return null;
            }


            FileItem fileItem = new FileItem();

            //文件
            fileItem.setFile(file);

            //名字
            String name = file.getName();
            fileItem.setName(name);

            //修改的时间
            long time = file.lastModified();
            fileItem.setModifyTime(this.formatTime(time));

            //图片文件的文件名前缀
            String resource = "ic_fso_type_";

            //是文件夹，没有大小信息
            if (file.isDirectory()) {
                fileItem.setCount(file.list().length + "");
                int imageId = context.getResources().getIdentifier(resource + "folder", "drawable", context.getPackageName());
                fileItem.setImageId(imageId);
            } else {

                //获得文件的长度
                double size = file.length();
                fileItem.setSize(this.formatFileSize(size));

                //获得文件类型
                String type = fileType(name);
                int imageId = context.getResources().getIdentifier(resource + type, "drawable", context.getPackageName());
                fileItem.setImageId(imageId);
            }
            return fileItem;
        }
    }

    /**
     * 根据后缀名查找对应的类型
     *
     * @param suffix
     * @return
     * @see FileTools#suffixMap
     */
    public String fileType(String suffix) {
        if (suffix.contains(".")) {
            suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
            for (String key : suffixMap.keySet()) {
                List<String> lists = suffixMap.get(key);
                ListIterator<String> iterator = lists.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().trim().equalsIgnoreCase(suffix.trim())) {
                        return key;
                    }
                }
            }
        }
        return "default";
    }

}
