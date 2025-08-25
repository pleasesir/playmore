package org.playmore.hotfix;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

/**
 * @ClassName HotfixAgent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/25 22:57
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/25 22:57
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class HotfixAgent {
    public static Instrumentation inst;

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        init(agentArgs, inst);
    }

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        init(agentArgs, inst);
        // 添加transformer在类加载前处理class字节码
//        inst.addTransformer(new ClassFileTransformer() {
//            @Override
//            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
//                                    ProtectionDomain protectionDomain, byte[] classFileBuffer)
//                    throws IllegalClassFormatException {
//                byte[] classBytes = HotfixStaticClass.remainHotfixClass(className);
//                if (classBytes != null) {
//                    HotfixStaticClass.logger.info("find remain class: " + className);
//                    return classBytes;
//                }
//                return classFileBuffer;
//            }
//        });
    }

    public static void init(String agentArgs, Instrumentation inst) throws Exception {
        HotfixAgent.inst = inst;
        Logger logger = HotfixClassWrapper.logger;
        logger.info("\n=============================HotfixAgent start==============================");
        Class<?>[] classes = HotfixAgent.inst.getAllLoadedClasses();
        logger.info("HotfixAgent clazz.length: " + classes.length);
        logger.info("HotfixAgent cur.classloader: " + HotfixAgent.class.getClassLoader());
        HotfixClassWrapper hotfixStaticClass = new HotfixClassWrapper();
        hotfixStaticClass.launch();
    }
}
