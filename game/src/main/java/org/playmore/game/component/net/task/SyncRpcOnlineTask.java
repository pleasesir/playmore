package org.playmore.game.component.net.task;

import lombok.Getter;
import org.playmore.api.account.AccountRoleDto;
import org.playmore.api.disruptor.task.impl.AsyncTask;

/**
 * @ClassName SyncRpcOnlineTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:44
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:44
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Getter
public class SyncRpcOnlineTask extends AsyncTask {
    private final AccountRoleDto dto;

    public SyncRpcOnlineTask(AccountRoleDto dto) {
        super(System.currentTimeMillis());
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
//        AppContext.getBean(AccountRpcService.class).asyncAccountRole(Collections.singletonList(dto));
    }

}
