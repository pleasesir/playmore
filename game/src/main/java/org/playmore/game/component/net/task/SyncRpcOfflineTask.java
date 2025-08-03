package org.playmore.game.component.net.task;

import lombok.Getter;
import org.apache.dubbo.rpc.RpcContext;
import org.playmore.api.account.AccountRoleDto;
import org.playmore.api.disruptor.task.impl.AsyncTask;
import org.playmore.common.constant.DubboConst;


/**
 * @ClassName SyncRpcOfflineTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:41
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:41
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class SyncRpcOfflineTask extends AsyncTask {

    @Getter
    private final long uniqueId;
    @Getter
    private final long roleId;
    private final int mapServerId;
    @Getter
    private final AccountRoleDto dto;

    public SyncRpcOfflineTask(long uniqueId, long roleId, int mapServerId, AccountRoleDto dto) {
        super(System.currentTimeMillis());
        this.uniqueId = uniqueId;
        this.roleId = roleId;
        this.mapServerId = mapServerId;
        this.dto = dto;
    }

    @Override
    protected void actionBefore(Object... args) {
    }

    @Override
    protected void onCompletion() {
    }

    @Override
    protected void action() throws Exception {
        RpcContext.getClientAttachment().setAttachment(DubboConst.PROVIDER_ID, mapServerId);
//        AppContext.getBean(RpcWorldService.class).playerOffLine(uniqueId, Collections.singletonList(roleId));
//        AppContext.getBean(AccountRpcService.class).asyncAccountRole(Collections.singletonList(dto));
    }

}
