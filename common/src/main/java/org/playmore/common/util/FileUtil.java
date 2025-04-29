package org.playmore.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void writeCacheObjects2File(File cacheDir, Class<?> cls, Collection<?> objs) {
        String cachePath = cacheDir.getAbsolutePath();
        String clsName = cls.getName();
        if (CheckNull.isEmpty(objs)) {
            logger.warn("cache: {}, cls: {} objs list is empty", cachePath, clsName);
            return;
        }

        File clsFile = new File(cacheDir.getPath() + "/" + clsName);
        try {
            if (!clsFile.exists() && clsFile.createNewFile()) {
                logger.info("cache: {}, cls: {} objs, create save file success", cachePath, clsFile.getPath());
            }
        } catch (IOException ioe) {
            logger.error("cache: {}, cls: {} objs, create new file error!!!", cachePath, clsName, ioe);
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(clsFile)) {
            byte[] modelBytes = KryoUtils.serialize(objs);
            fileOutputStream.write(modelBytes);
            LogUtil.CACHE_LOGGER.warn("cache: {}, cls: {} objs, save model count: {}", cachePath, clsName, objs.size());
        } catch (IOException ioe) {
            logger.error("", ioe);
        }
    }

    /**
     * @param parent
     * @param curFile
     * @param fileTimeMap
     * @param bDelete
     */
    public static void readHotfixDir(String parent, File curFile, Map<String, Long> fileTimeMap, boolean bDelete) {
        if (curFile.isDirectory()) {
            File[] files = curFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String packageName = parent != null ? parent + "." + file.getName() : file.getName();
                        readHotfixDir(packageName, file, fileTimeMap, bDelete);
                    } else {
                        readHotfixDir(parent, file, fileTimeMap, bDelete);
                    }
                }
            }
        } else {
            try {
                int classNameIdx = curFile.getName().indexOf(".class");
                if (classNameIdx >= 0) {
                    String className = curFile.getName().substring(0, classNameIdx);
                    String clsFullName = parent != null ? parent + "." + className : className;
                    fileTimeMap.put(clsFullName, curFile.lastModified());
                }
                if (bDelete && curFile.delete()) {
                    logger.warn("delete class file :" + curFile.getName());
                }

            } catch (Exception e) {
                logger.error(String.format("parent :%s, file :%s", parent, curFile.getName()), e);
            }

        }
    }

    public static void writeFile(String path, String content) {
        Path fpath = Paths.get(path);
        // 创建文件
        if (!Files.exists(fpath)) {
            try {
                Files.createFile(fpath);
            } catch (IOException e) {
                LogUtil.error(e);
            }
        }
        // 创建BufferedWriter
        try (BufferedWriter bfw = Files.newBufferedWriter(fpath)) {
            bfw.write(content);
            bfw.flush();
        } catch (IOException e) {
            LogUtil.error(e);
        }
    }

    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }
}
