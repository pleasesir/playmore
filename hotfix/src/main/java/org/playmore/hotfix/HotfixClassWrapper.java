package org.playmore.hotfix;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @ClassName HotfixWrapper
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/25 22:58
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/25 22:58
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class HotfixClassWrapper {
    public static final Logger logger = Logger.getLogger("HOTFIX");
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        final Thread t = new Thread(null, r, "single-hotfix-thread");
        t.setDaemon(true);
        //优先级
        if (Thread.NORM_PRIORITY != t.getPriority()) {
            // 标准优先级
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    });
    private String hotfixPath;
    private static final Map<String, Long> fileTimeMap = new HashMap<>();
    private File hotfixClassDir;

    public void launch() throws Exception {
        initProperties();
        File agentDir = HotfixFilePath.getPath();
        hotfixPath = agentDir.getParentFile() + "/hotfix/";
        logger.info("hotfixPath :" + hotfixPath);
        hotfixClassDir = new File(hotfixPath);
        //清空热更class文件目录
        readHotfixDir(null, hotfixClassDir, null, true);
        fileTimeMap.clear();
        executor.scheduleWithFixedDelay(new RefineWork(), 3, 10, TimeUnit.SECONDS);
    }

    private void initProperties() {
        Properties props = new Properties();
        String externalConfigPath = "config.properties";

        InputStream in = null;
        try {
            // 尝试从外部读取配置文件
            File externalConfigFile = new File(externalConfigPath);
            if (externalConfigFile.exists()) {
                in = new FileInputStream(externalConfigFile);
            } else {
                // 如果外部文件不存在，尝试从 JAR 内部读取配置文件
                in = HotfixClassWrapper.class.getResourceAsStream("/config.properties");
            }

            if (in != null) {
                props.load(in);
                // 读取配置文件中的属性
                int checkClassInterval = Integer.parseInt(props.getProperty("check.class.interval", "30000"));
                logger.info("hotfix checkClassInterval: " + checkClassInterval);
                long firstPrintTime = System.currentTimeMillis() + checkClassInterval;
                logger.info("hotfix firstPrintTime: " + new Date(firstPrintTime));
            } else {
                System.err.println("Configuration file not found.");
            }
        } catch (IOException e) {
            logger.info("Error loading configuration file: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.severe("Error closing input stream: " + e.getMessage());
                }
            }
        }
    }

    public class RefineWork implements Runnable {
        @Override
        public void run() {
            try {
                Map<String, Long> findHotfixMap = readHotfixDir(null, hotfixClassDir, null,
                        false);
                Map<String, File> hotfixMap = null;
                if (findHotfixMap != null && !findHotfixMap.isEmpty()) {
                    for (Map.Entry<String, Long> entry : findHotfixMap.entrySet()) {
                        Long modifyTime = fileTimeMap.get(entry.getKey());
                        if (modifyTime == null || modifyTime.longValue() != entry.getValue()) {
                            String fullClassName = entry.getKey();
                            String fullPath = hotfixPath + fullClassName.replace(".", "/") + ".class";
                            logger.info("full class path : " + fullPath);
                            File classFile = new File(fullPath);
                            if (hotfixMap == null) {
                                hotfixMap = new HashMap<>(16);
                            }
                            if (!classFile.setLastModified(entry.getValue())) {
                                logger.warning("setLastModified fail : " + fullPath);
                            }
                            hotfixMap.put(fullClassName, classFile);
                        }
                    }
                }
                // 若存在待更新的或正需要被更新的class, 则进行更新
                if (hotfixMap != null && !hotfixMap.isEmpty()) {
                    redefineClass(hotfixMap);
                }
            } catch (Throwable e) {
                printLog(e);
            }
        }
    }

    /**
     * @param hotfixMap 字节码数组
     * @throws Exception e
     */
    public static void redefineClass(Map<String, File> hotfixMap) throws Exception {
        long startTime = System.currentTimeMillis();
        List<ClassDefinition> list = new ArrayList<>(hotfixMap.size());
        for (Class<?> clazz : HotfixAgent.inst.getAllLoadedClasses()) {
            String className = clazz.getName();
            String cl = clazz.getClassLoader() == null ? "null" : clazz.getClassLoader().getClass().getName();
            if (hotfixMap.containsKey(className)) {
                File classFile = hotfixMap.remove(className);
                byte[] classBytes = fileToBytes(classFile);
                if (classBytes == null) {
                    logger.severe("class file not found : " + className);
                    continue;
                }
                // 热更成功的记录文件信息
                fileTimeMap.put(className, classFile.lastModified());
                ClassDefinition classDefinition = new ClassDefinition(clazz, classBytes);
                list.add(classDefinition);
                logger.info(String.format("prepare hotfix class %s, classloader: %s", clazz.getName(), cl));
            }
        }
        if (!hotfixMap.isEmpty()) {
            logger.warning("hotfix class is empty, remain need hotfix classMap: " + hotfixMap + ", fileTimeMap: "
                    + fileTimeMap);
        }
        if (list.isEmpty()) {
            return;
        }
        HotfixAgent.inst.redefineClasses(list.toArray(new ClassDefinition[0]));
        logger.info(String.format("hotfix success, class count : %d, costTime : %d",
                list.size(), System.currentTimeMillis() - startTime));
    }

    /**
     * 读取指定目录下的所有class文件
     *
     * @param parent  父文件目录
     * @param curFile 当前文件
     * @param bDelete 是否删除
     */
    public static Map<String, Long> readHotfixDir(String parent, File curFile, Map<String, Long> fileTimeMap, boolean bDelete) {
        if (curFile.isDirectory()) {
            File[] files = curFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String packageName = parent != null ? parent + "." + file.getName() : file.getName();
                        fileTimeMap = readHotfixDir(packageName, file, fileTimeMap, bDelete);
                    } else {
                        fileTimeMap = readHotfixDir(parent, file, fileTimeMap, bDelete);
                    }
                }
            }
        } else {
            try {
                int classNameIdx = curFile.getName().indexOf(".class");
                if (classNameIdx >= 0) {
                    String className = curFile.getName().substring(0, classNameIdx);
                    String clsFullName = parent != null ? parent + "." + className : className;
                    if (fileTimeMap == null) {
                        fileTimeMap = new HashMap<>(16);
                    }
                    fileTimeMap.put(clsFullName, curFile.lastModified());
                }
                if (bDelete && curFile.delete()) {
                    logger.warning("delete class file :" + curFile.getName());
                }
            } catch (Exception e) {
                printLog(e);
            }
        }

        return fileTimeMap;
    }


    public static void printLog(Throwable e) {
        logger.severe(String.format("错误信息 :%s", e));
        StackTraceElement[] st = e.getStackTrace();
        for (StackTraceElement stackTraceElement : st) {
            logger.severe(String.format("错误信息 :%s", stackTraceElement.toString()));
        }
    }

    public static byte[] fileToBytes(File file) {
        try (FileInputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (IOException e) {
            logger.severe("将文件转换为字节数组时发生错误: " + e);
            return null;
        }
    }
}
