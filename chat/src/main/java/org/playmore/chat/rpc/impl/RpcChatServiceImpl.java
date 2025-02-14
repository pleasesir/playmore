package org.playmore.chat.rpc.impl;

import com.gryphpoem.cross.chat.RpcChatService;
import com.gryphpoem.cross.chat.msg.ChatRqMsg;
import com.gryphpoem.cross.chat.msg.ChatRsMsg;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.concurrent.CompletableFuture;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: zhangpeng
 * @Date: 2025/02/14/13:35
 * @Description:
 */
@DubboService
public class RpcChatServiceImpl implements RpcChatService {
    @Override
    public void receiveMsg(ChatRqMsg chatRqMsg) {

    }

    @Override
    public CompletableFuture<ChatRsMsg> receiveFutureMsg(ChatRqMsg chatRqMsg) {
        return null;
    }
}
