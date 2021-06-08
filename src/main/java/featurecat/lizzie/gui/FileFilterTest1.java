package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;

public class FileFilterTest1 extends javax.swing.filechooser.FileFilter {
  public boolean accept(java.io.File f) {
    return true;
  }

  public String getDescription() {
    return Lizzie.resourceBundle.getString("FileFilterTest1.getDescription");
  }
}
