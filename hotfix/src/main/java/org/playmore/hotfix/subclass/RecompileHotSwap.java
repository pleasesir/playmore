package org.playmore.hotfix.subclass;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName RecompileHotSwap
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/9/16 14:01
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/9/16 14:01
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class RecompileHotSwap {
    public static final Logger logger = LoggerFactory.getLogger("HOTFIX");
    /**
     * 子类后缀名
     */
    public static final String SUBCLASS_SUFFIX = "$$$SUBCLASS";

    /**
     * 获取需要替换的新类的Class
     *
     * @param oldClass 原class名字
     * @return
     * @throws IOException
     */
    public static Class<?> recompileClass(Class<?> oldClass) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass oldCtClass;
        try {
            oldCtClass = pool.getCtClass(oldClass.getName());
        } catch (NotFoundException e) {
            logger.error("javassist.NotFoundException: {}", oldClass.getName());
            // https://www.javassist.org/tutorial/tutorial.html
            // 页面搜索"Class search path"
            // 运行在web容器(如tomcat)中的程序,可能存在多个ClassLoader,导致ClassPool.getDefault()找不到对应的class
            pool.insertClassPath(new ClassClassPath(oldClass));
            oldCtClass = pool.getCtClass(oldClass.getName());
        }

        // 从class文件中获取CtClass
        CtClass newCtClass;
        try (InputStream classInputStream = getClassInputStream(oldClass)) {
            newCtClass = pool.makeClass(classInputStream);
        }

        String newClassName = oldClass.getSimpleName() + SUBCLASS_SUFFIX;
        String newFullClassName = oldClass.getName() + SUBCLASS_SUFFIX;

        // 新类改名，设置父类为原来的类
        newCtClass.replaceClassName(oldClass.getName(), newFullClassName);
        newCtClass.setSuperclass(oldCtClass);

        // 如果有默认构造函数，则调用父类构造函数
        CtConstructor constructor = newCtClass.getDeclaredConstructor(new CtClass[0]);
        if (constructor == null) {
            throw new RuntimeException("has no default constructor:" + oldClass.getName());
        }

        if (java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())) {
            throw new RuntimeException("the constructor is private, cannot extend:" + oldClass.getName());
        }

        // 设置子类的构造函数为public的，方便后面newInstance
        constructor.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        // 设置默认构造函数为 super();
        constructor.setBody("super();");

        // final的方法忽略
        CtMethod[] declaredMethods = newCtClass.getDeclaredMethods();
        for (CtMethod declaredMethod : declaredMethods) {
            int modifiers = declaredMethod.getModifiers();
            boolean isPrivate = java.lang.reflect.Modifier.isPrivate(modifiers);
            boolean isFinal = Modifier.isFinal(modifiers);
            if (!isPrivate && isFinal) {
                // 从新类中移除
                logger.error("{}.{}(), isFinal:{}, method is removed in newClass", oldClass, declaredMethod.getName(),
                        true);
                newCtClass.removeMethod(declaredMethod);
            }
        }

        // 二进制内容
        byte[] targetBytes = newCtClass.toBytecode();

        // 移除临时类，让它从CtPool中移除，以便多次热更
        newCtClass.detach();

        dump(targetBytes, newClassName);

        // 重新获取新类
        RecompileClassLoader recompileClassLoader = new RecompileClassLoader(oldClass.getClassLoader(), newFullClassName,
                targetBytes);
        return recompileClassLoader.findClass(newFullClassName);
    }

    private static void dump(byte[] targetBytes, String className) throws IOException {
        // class dump到日志中
        String basePath = new File("").getAbsolutePath();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path finalPath = Paths.get(basePath, "recompile-output", className + "-" + timestamp + ".class");
        File to = finalPath.toFile();
        Files.createDirectories(finalPath);
        logger.info("class dump: {}", to.getAbsolutePath());
        Files.write(finalPath, targetBytes);
    }

    public static InputStream getClassInputStream(Class<?> clazz) throws Exception {
        String classLocation = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();

        if (classLocation.endsWith(".jar")) {
            throw new IOException("cannot recompile class from jar: " + clazz);
        } else {
            String clazzName = clazz.getName().replace('.', '/') + ".class";
            return new FileInputStream(new File(classLocation, clazzName));
        }
    }
}
