package org.playmore.common.component;

public interface ComponentLifecycle<T extends Enum<T>> {
    /**
     * 组件名称
     *
     * @return 组件名称
     */
    String name();

    /**
     * 组件启动
     */
    void start();

    /**
     * 组件启动后逻辑
     */
    void afterStart();

    /**
     * 组件停止前逻辑
     */
    void beforeStop();

    /**
     * 组件停止逻辑
     */
    void stop();

    /**
     * 组件启动停止先后排序
     *
     * @return 启动停止的排序
     */
    T order();
}
