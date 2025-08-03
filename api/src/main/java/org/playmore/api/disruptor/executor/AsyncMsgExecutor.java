package org.playmore.api.disruptor.executor;

import org.playmore.api.disruptor.TaskDisruptor;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.api.disruptor.task.impl.MessageTask;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public record AsyncMsgExecutor(ThreadPoolExecutor executor, AtomicInteger counter) {
    public void publish(TaskDisruptor disruptor, BaseTask task) {
        MessageTask messageTask = new MessageTask(disruptor, task);
        executor.execute(messageTask);
    }
}
