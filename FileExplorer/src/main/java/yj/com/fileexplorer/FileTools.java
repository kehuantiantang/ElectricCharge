package yj.com.fileexplorer;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
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
     * 操作
     */
    public enum OperateType {
        COPY("copy", "复制"),
        CUT("cut", "粘贴"),
        DELETE("delete", "删除"),
        NEW_FOLDER("newFolder", "新建文件夹"),
        SHARE("share", "分享"),
        EMPTY("empty", "空"),
        SELECT("select", "选择");

        private String value;
        private String chineseValue;

        OperateType(String value, String chineseValue) {
            this.value = value;
            this.chineseValue = chineseValue;
        }


        public String getValue() {
            return value;
        }

        public String getChineseValue() {
            return chineseValue;
        }
    }


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

    /**
     * 按照文件名称进行排序
     *
     * @param isIncreased 升序？
     * @return
     */
    public static Comparator<FileItem> sortByName(final boolean isIncreased) {
        return new Comparator<FileItem>() {
            @Override
            public int compare(FileItem lhs, FileItem rhs) {
                if (isIncreased) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                } else {
                    return -(lhs.getName().compareToIgnoreCase(rhs.getName()));
                }

            }
        };
    }

    /**
     * 按照文件的时间进行排序
     *
     * @param isIncreased 新的在前？
     * @return
     */
    public static Comparator<FileItem> sortByTime(final boolean isIncreased) {
        return new Comparator<FileItem>() {
            @Override
            public int compare(FileItem lhs, FileItem rhs) {
                Long lhsTime = lhs.getFile().lastModified();
                Long rhsTime = rhs.getFile().lastModified();
                if (isIncreased) {
                    return lhsTime.compareTo(rhsTime);
                } else {
                    return -lhsTime.compareTo(rhsTime);
                }
            }
        };
    }


    /**
     * 按照文件大小进行排序
     *
     * @param isIncreased 从大到小？
     * @return
     */
    public static Comparator<FileItem> sortBySize(final boolean isIncreased) {
        return new Comparator<FileItem>() {
            @Override
            public int compare(FileItem lhs, FileItem rhs) {
                File lhsFile = lhs.getFile();
                File rhsFile = rhs.getFile();
                BigInteger lhsSize = BigInteger.valueOf(Long.MAX_VALUE);
                BigInteger rhsSize = BigInteger.valueOf(Long.MAX_VALUE);

                //如果是文件夹的话，按照文件夹内的文件数量排序
                if (lhsFile.isDirectory()) {
                    lhsSize = lhsSize.add(new BigInteger(lhsFile.list().length + ""));
                } else {
                    lhsSize = BigInteger.valueOf(lhsFile.length());
                }
                if (rhsFile.isDirectory()) {
                    rhsSize = rhsSize.add(new BigInteger(rhsFile.list().length + ""));
                } else {
                    rhsSize = BigInteger.valueOf(rhsFile.length());
                }
                if (isIncreased) {
                    return lhsSize.compareTo(rhsSize);
                } else {
                    return -lhsSize.compareTo(rhsSize);
                }
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
     * 在root目录中显示内存卡和内置内存卡有多少空间，还有多少空间剩余
     *
     * @param path 地址
     * @return 卷标容量
     */
    public String getRootVolume(String path) {
        StatFs stat = new StatFs(path);
        long total = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        long free = (long) stat.getAvailableBlocks()
                * (long) stat.getBlockSize();
        if (total == 0) {
            return "";
        }
        return "Free " + this.formatFileSize(free) + " of " + this.formatFileSize(total);
    }


    /**
     * 删除文件，目录或者是文件都可以
     *
     * @param filePath 删除的地址
     * @return
     */
    public boolean delete(String filePath) {
        File delFile = new File(filePath);
        if (!delFile.exists()) {
            return false;
        } else {
            if (delFile.isDirectory()) {
                String[] children = delFile.list();
                for (String child : children) {
                    delete(delFile.getAbsolutePath() + File.separator + child);
                }
            }
            return delFile.delete();
        }
    }


    /**
     * 新建目录
     * @param folderPath 新建目录的路径
     * @param folderName 文件夹名称
     * @return boolean 返回是否建立成功
     */
    public boolean newFolder(String folderPath, String folderName) {
        File file = new File(folderPath);
        if (!file.exists()) {
            return false;
        } else {
            File newFolder = new File(folderPath + File.separator + folderName);
            return newFolder.mkdir();
        }
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return String 返回拷贝文件的日志
     */
    public boolean copyFile(String oldPath, String newPath) {
        //相同目录的情况
        if(oldPath.equals(newPath)){
            return true;
        }
        try {
            int byteRead;
            File oldFile = new File(oldPath);

            BufferedInputStream inStream = null; // 读入原文件
            if (oldFile.exists()) { // 文件存在时
                inStream = new BufferedInputStream(new FileInputStream(oldPath));
            }

            BufferedOutputStream fs = new BufferedOutputStream(new FileOutputStream(newPath));
            while ((byteRead = inStream.read()) != -1) {
                fs.write(byteRead);
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
     * 复制文件
     *
     * @param oldPath 原文件，文件或者是文件夹
     * @param newPath 必须是文件夹
     */
    public boolean copy(String oldPath, String newPath) {
        //在相同目录内复制
        if(oldPath.equals(newPath)){
            return true;
        }
        File oldFile = new File(oldPath);
        File target = new File(newPath);
        if (!oldFile.exists() || !target.exists() || !target.isDirectory()) {
            return false;
        } else {
            boolean flag = true;
            if (oldFile.isDirectory()) {
                String oldFileName = oldFile.getName();
                newPath = newPath + File.separator + oldFileName;
                File newFile = new File(newPath);
                newFile.mkdirs();
                File[] children = oldFile.listFiles();
                for (File child : children) {
                    if (child.isDirectory()) {
                        flag = copy(child.getAbsolutePath(), newPath);
                        if (!flag) {
                            break;
                        }
                    } else {
                        flag = copyFile(child.getAbsolutePath(), newPath + File.separator + child.getName());
                    }
                }
                return flag;
            }
            return copyFile(oldPath, newPath + File.separator + oldFile.getName());
        }
    }

    /**
     * 剪切文件
     *
     * @param oldPath 原文件地址
     * @param newPath 目的文件地址
     * @return
     */
    public boolean cut(String oldPath, String newPath) {
        if (oldPath.equals(newPath)) {
            return true;
        }
        if (!copy(oldPath, newPath)) {
            return false;
        }
        //避免出现在一个文件夹中粘贴行为的情况
        if (!delete(oldPath)) {
            return false;
        }
        return true;
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
     *
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
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String getFileDetailInfo(String path) throws Exception {
        File file = new File(path);
        StringBuffer detailInfo = new StringBuffer();
        if (!file.exists()) {
            throw new Exception("文件不存在!");
        } else {
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
     *
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
