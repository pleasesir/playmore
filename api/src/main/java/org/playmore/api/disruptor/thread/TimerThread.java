package org.playmore.api.disruptor.thread;

import org.playmore.api.disruptor.OneServerDisruptor;

import java.util.*;

/**
 * @ClassName TimerThread
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:31
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:31
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class TimerThread extends Timer {
    /**
     * 事件集合
     */
    private final List<ITimerEvent> events = new ArrayList<>(30);
    /**
     * 主线程
     */
    private final OneServerDisruptor main;
    /**
     * 定时任务
     */
    private TimerTask task;

    public TimerThread(OneServerDisruptor main) {
        super("main" + "-Timer");
        this.main = main;
    }

    public void start() {
        task = new TimerTask() {
            @Override
            public void run() {
                synchronized (events) {
                    // 事件迭代器
                    Iterator<ITimerEvent> it = events.iterator();
                    // 派发事件
                    while (it.hasNext()) {
                        ITimerEvent event = it.next();
                        // 未结束
                        if (event.remain() <= 0) {
                            if (event.getLoop() > 0) {
                                event.setLoop(event.getLoop() - 1);
                            } else {
                                event.setLoop(event.getLoop());
                            }
                            // 需要放入主线程
                            main.addCommand(event);
                        }
                        if (event.getLoop() == 0) {
                            it.remove();
                        }
                    }
                }
            }
        };
        schedule(task, 0, main.getHeart());
    }

    public void stop(boolean flag) {
        synchronized (events) {
            events.clear();
            if (task != null) {
                task.cancel();
            }
            cancel();
        }
    }

    /**
     * 添加定时事件
     *
     * @param event 定时事件
     */
    public void addTimerEvent(ITimerEvent event) {
        synchronized (events) {
            events.add(event);
        }
    }

    /**
     * 移除定时事件
     *
     * @param event 定时事件
     */
    public void removeTimerEvent(ITimerEvent event) {
        synchronized (events) {
            events.remove(event);
        }
    }
}
