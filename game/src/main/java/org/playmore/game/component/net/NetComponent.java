package org.playmore.game.component.net;

import jakarta.annotation.Resource;
import org.playmore.api.account.AccountRoleDto;
import org.playmore.api.disruptor.OrderedQueueDisruptor;
import org.playmore.api.disruptor.TaskDisruptor;
import org.playmore.common.component.AbsComponent;
import org.playmore.common.component.IComponent;
import org.playmore.common.msg.impl.BatchGatewayMsg;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.LogUtil;
import org.playmore.game.component.net.task.BatchGatewayRsTask;
import org.playmore.game.component.net.task.GatewayRsTask;
import org.playmore.game.component.net.task.SyncRpcOfflineTask;
import org.playmore.game.component.net.task.SyncRpcOnlineTask;
import org.playmore.game.component.order.GameOrder;
import org.playmore.game.domain.db.dao.ServerConfig;
import org.playmore.pb.BasePb;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName NetComponent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:34
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:34
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class NetComponent implements AbsComponent<TaskDisruptor>, IComponent<GameOrder> {

    private OrderedQueueDisruptor queueDisruptor;
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    private static NetComponent component;

    @Resource
    private ServerConfig serverConfig;

    @Override
    public String name() {
        return "net-component";
    }

    @Override
    public void start() {
        int consumerSize = Runtime.getRuntime().availableProcessors();
        TaskDisruptor taskDisruptor = new TaskDisruptor(name(), 32768, TaskDisruptor.newWaitStrategy(),
                stopping, consumerSize, serverConfig.getAsyncMessageEventThreadNum());
        taskDisruptor.start();
        queueDisruptor = new OrderedQueueDisruptor(taskDisruptor);
        component = this;
    }

    public static NetComponent getInstance() {
        return component;
    }

    @Override
    public void afterStart() {
    }

    @Override
    public void beforeStop() {

    }

    @Override
    public void stop() {
        try {
            if (Objects.nonNull(queueDisruptor)) {
                queueDisruptor.shutdownGracefully();
                LogUtil.stop(queueDisruptor.getTaskDisruptor().getDisruptorName() + "停止完成");
            }
            LogUtil.stop("====================" + name() + "任务组件停止完成====================");
        } catch (Exception ex) {
            LogUtil.stop(name() + "任务组件停止异常, ex: " + ex);
        }
    }

    @Override
    public GameOrder order() {
        return GameOrder.NET_COMPONENT;
    }

    @Override
    public TaskDisruptor next() {
        return null;
    }

    /**
     * 发布网关消息任务
     *
     * @param hashCode  {@link org.playmore.common.msg.ChannelId} hashCode
     * @param gatewayId 网关服务器id
     * @param msg       网关消息
     * @param pb        网关pb消息
     */
    public void publish(long hashCode, int gatewayId, GatewayMsg msg, BasePb.Base pb) {
        queueDisruptor.publishTask(hashCode, new GatewayRsTask(gatewayId, msg, pb));
    }

    /**
     * 发布网关批量消息任务
     *
     * @param msg       批量消息包体
     * @param gatewayId 网关服务器id
     */
    public void publish(BatchGatewayMsg msg, int gatewayId) {
        queueDisruptor.publishTask(msg.getCmdId(), new BatchGatewayRsTask(gatewayId, msg));
    }

    /**
     * 发布rpc离线通知任务
     *
     * @param roleId      玩家id
     * @param uniqueId    地图id
     * @param mapServerId 地图服务器id
     * @param dto         玩家传输数据
     */
    public void publishRpcOffline(long roleId, long uniqueId, int mapServerId, AccountRoleDto dto) {
        queueDisruptor.publishTask(roleId, new SyncRpcOfflineTask(uniqueId, roleId, mapServerId, dto));
    }

    public void publishRpcOnline(long roleId, AccountRoleDto dto) {
        queueDisruptor.publishTask(roleId, new SyncRpcOnlineTask(dto));
    }
}
