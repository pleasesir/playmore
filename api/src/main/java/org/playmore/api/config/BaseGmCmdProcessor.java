package org.playmore.api.config;


import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import org.playmore.api.annotation.GmCmd;
import org.playmore.api.domain.gm.GmRelation;
import org.playmore.api.exception.GameError;
import org.playmore.api.exception.MwException;
import org.playmore.api.verticle.BaseVerticle;
import org.playmore.api.verticle.eventbus.ExternalEventConsumer;
import org.playmore.api.verticle.eventbus.event.Address;
import org.playmore.api.verticle.manager.ExternalEventManager;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:59
 * @description TODO
 */
public abstract class BaseGmCmdProcessor {
    protected List<String> errors = null;
    @Getter
    protected final Map<String, GmRelation> relationMap = new HashMap<>();


    public GmRelation checkAndGetGmRelation(String cmd) {
        GmRelation relation = relationMap.get(cmd);
        if (relation == null) {
            throw new MwException(GameError.PARAM_ERROR, ", cmd: ", cmd, " not found");
        }
        return relation;
    }

    public void checkErrors() throws Exception {
        if (CheckNull.nonEmpty(errors)) {
            throw new Exception(String.format("存在重复的GM命令, %s", errors));
        }
    }

    protected void process(Object bean) {
        if (AopUtils.isAopProxy(bean)) {
            return;
        }

        Class<?> beanClass = bean.getClass();
        // 确保 beanClass 不是 Lambda 表达式生成的类
        if (beanClass.getName().contains("$$Lambda")) {
            return;
        }

        Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanClass);
        MethodAccess ma = MethodAccess.get(beanClass);
        for (Method method : methods) {
            GmCmd gmCmd = AnnotationUtils.findAnnotation(method, GmCmd.class);
            if (gmCmd == null) {
                continue;
            }

            Address[] addresses = gmCmd.external().subscribe().gmEvent();
            if (CheckNull.isEmpty(addresses)) {
                LogUtil.error(String.format("GmCmd=%s, GM事件地址为空", gmCmd.desc()));
                continue;
            }

            Arrays.stream(addresses).forEach(address -> {
                String ads = address.toString().toLowerCase(Locale.ROOT);
                if (relationMap.containsKey(ads)) {
                    Class<?> declaringClass = method.getDeclaringClass();
                    if (declaringClass.isAssignableFrom(beanClass) || beanClass.isAssignableFrom(declaringClass)) {
                        return;
                    }
                    LogUtil.error(String.format("Gm命令=%s已存在,%s", ads, relationMap.get(ads)));
                    if (errors == null) {
                        errors = new ArrayList<>();
                    }
                    errors.add(ads);
                } else {
                    GmRelation gmRelation = createGmRelation(gmCmd.external().verticle(),
                            ExternalEventManager.transferConsumer(gmCmd.external(), ma, bean, method));
                    relationMap.put(ads, gmRelation);
                }
            });
        }
    }

    /**
     * 创建gm-relation
     *
     * @param consumer 消费逻辑
     * @return gm命令关系
     */
    protected abstract GmRelation createGmRelation(Class<? extends BaseVerticle> clazz, ExternalEventConsumer consumer);
}
