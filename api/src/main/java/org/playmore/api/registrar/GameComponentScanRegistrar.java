package org.playmore.api.registrar;


import jakarta.annotation.Nullable;
import org.playmore.api.config.AppContext;
import org.playmore.api.verticle.manager.ExternalEventManager;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @ClassName GameComponentScanRegistrar  //类名称
 * @Description: 类描述
 * @Author: zhangpeng    //作者
 * @CreateDate: 2025/5/25 15:55	//创建时间
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/5/25 15:55	//更新时间
 * @UpdateRemark: 更新的信息
 * @Version: 1.0    //版本号
 */
public class GameComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

    private static void registerCommonBeans(BeanDefinitionRegistry registry) {
        registerBeans(registry, AppContext.class);
//        registerBeans(registry, CmdMgr.class);
//        registerBeans(registry, RpcFightConsumer.class);
//        registerBeans(registry, RpcAccountConsumer.class);
//        registerBeans(registry, RpcManagerConsumer.class);
//        registerBeans(registry, RpcChatConsumer.class);
//        registerBeans(registry, RpcGameConsumer.class);
//        registerBeans(registry, RpcWorldConsumer.class);
//        registerBeans(registry, RpcDatacenterConsumer.class);
//        registerBeans(registry, ResourceService.class);
//        registerBeans(registry, RpcRankConsumer.class);
//        registerBeans(registry, RpcGatewayConsumer.class);
        registerBeans(registry, ExternalEventManager.class);
//        registerBeans(registry, ExportServiceListener.class);
//        registerBeans(registry, AppStartedListener.class);
    }

    private static void registerBeans(BeanDefinitionRegistry registry, Class<?> clazz) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        // 注册上下文bean
        registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinition);
    }

    @Override
    public void registerBeanDefinitions(@Nullable AnnotationMetadata metadata, @Nullable BeanDefinitionRegistry registry) {
        if (registry != null) {
            registerCommonBeans(registry);
        }
    }
}
