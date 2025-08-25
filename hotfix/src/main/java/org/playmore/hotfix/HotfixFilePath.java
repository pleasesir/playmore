package org.playmore.hotfix;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @ClassName HotfixFilePath
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/25 22:51
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/25 22:51
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Slf4j
public class HotfixFilePath {
    private static File AGENT_PACKAGE_PATH;

    public static File getPath() throws Exception {
        if (AGENT_PACKAGE_PATH == null) {
            AGENT_PACKAGE_PATH = findPath();
        }
        return AGENT_PACKAGE_PATH;
    }

    public static boolean isPathFound() {
        return AGENT_PACKAGE_PATH != null;
    }

    /**
     * 获取当前agent的目录
     *
     * @return agent的目录
     * @throws Exception 抛出异常
     */
    private static File findPath() throws Exception {
        String classResourcePath = HotfixFilePath.class.getName().replaceAll("\\.", "/") + ".class";

        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();

            log.info("The beacon class location is {}", urlString);

            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File agentJarFile = null;
                try {
                    agentJarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    printLog(e);
                }
                assert agentJarFile != null;
                if (agentJarFile.exists()) {
                    return agentJarFile.getParentFile();
                }
            } else {
                int prefixLength = "file:".length();
                String classLocation = urlString.substring(
                        prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }
        printLog(new NullPointerException("Can not locate agent jar file."));
        throw new Exception("Can not locate agent jar file.");
    }

    /**
     * 打印异常信息
     *
     * @param e 异常
     */
    public static void printLog(Exception e) {
        try (PrintWriter write = new PrintWriter(new StringWriter())) {
            e.printStackTrace(write);
            log.trace(write.toString());
        }
    }
}
