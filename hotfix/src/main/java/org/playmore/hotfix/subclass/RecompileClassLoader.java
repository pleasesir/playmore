package org.playmore.hotfix.subclass;

/**
 * @ClassName RecompileClassLoader
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/9/16 14:00
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/9/16 14:00
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class RecompileClassLoader extends ClassLoader {
    private final String className;
    private byte[] byteCodes;

    private Class<?> defineClass;

    public RecompileClassLoader(ClassLoader parent, String className, byte[] byteCodes) {
        super(parent);
        this.className = className;
        this.byteCodes = byteCodes;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (!name.equals(className)) {
            return null;
        }

        if (this.defineClass != null) {
            return this.defineClass;
        }

        this.defineClass = super.defineClass(name, byteCodes, 0, byteCodes.length);
        // 清空字节数组的内存，释放内存
        this.byteCodes = null;
        return defineClass;
    }
}
