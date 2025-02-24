package com.revortix.model.util;

import java.io.File;

public class JarFileUtil {

  public static File getJarFile() {
    return new File(
        JarFileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
  }
}
