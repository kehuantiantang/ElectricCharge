package yj.com.fileexplorer;

import java.io.File;
import java.io.Serializable;

/**
 * 因为想要通过Activity传递interface,
 * example:
 *
 * static IFileFilter fileFilter = new IFileFilter() {
 * public boolean accept(File dir, String name) {
 * File file = new File(dir.getAbsolutePath() + File.separator + name);
 *      if(file.isDirectory()){
 *          return true;
 *      }
 *      return false;
 *      }
 * };
 */
public interface IFileFilter extends Serializable {
    boolean accept(File dir, String name);
}
