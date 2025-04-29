package org.playmore.common.util;


import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:54
 * @description TODO
 */
public class ReflectUtils {
    /**
     * 从一个Class中获得具有指定注解的Method，只获取子类的Method，不获取父类的Method
     *
     * @param clazz      指定的Class
     * @param annotation 指定注解的Class
     * @return 数组，可能长度为0
     */
    public static List<Method> getMethodsByAnnotationInClass(Class<?> clazz, Class<? extends Annotation> annotation) {
        var list = new ArrayList<Method>();
        var methods = clazz.getDeclaredMethods();
        for (var method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                list.add(method);
            }
        }
        return list;
    }

    /**
     * <pre>
     * 从包package中获取所有的Class
     * </pre>
     *
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClasses(String packageName) {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar = null;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    // 去掉后面的".class" 获取真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        // 添加到classes
                                        classes.add(Class.forName(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        LogUtil.error(e);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        LogUtil.error(e);
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.error(e);
        }
        return classes;
    }

    /**
     * <pre>
     * 以文件的形式来获取包下的所有Class
     * </pre>
     *
     * @param packageName 包名
     * @param packagePath 路径
     * @param recursive   是否循环迭代
     * @param classes     class集合
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath,
                                                        boolean recursive,
                                                        Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] files = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));

        // 循环所有文件
        if (files != null) {
            for (File file : files) {
                // 如果是目录 则继续扫描
                if (file.isDirectory()) {
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
                } else {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    try {
                        // 添加到集合中去
                        classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        LogUtil.error(e);
                    }
                }
            }
        }
    }
}
