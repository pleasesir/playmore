package org.playmore.chat.cache.vo;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.playmore.chat.cache.component.PersistContext;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.config.AppContext;
import org.playmore.chat.constant.DBConstant;
import org.playmore.chat.db.entity.ChatMsgModel;
import org.playmore.chat.db.mapper.ChatMsgMapper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: zhangpeng
 * @Date: 2023/10/11/17:02
 * @Description:
 */
public class ChatMsgShardingModel extends SimpleCacheVO<ChatMsgModel> {
    public ChatMsgShardingModel(ChatMsgModel model) {
        super(model);
    }

    @Override
    protected synchronized void persist(Object key, PersistContext ctx) {
        long clearTimestamp = DateUtil.offsetMonth(new Date(), DBConstant.CHAT_MSG_CLEAR_MONTH_LIMIT).getTime();
        if (model.getChatTime() <= clearTimestamp) {
            return;
        }

        try {
            ChatMsgMapper chatMsgMapper = Objects.requireNonNull(AppContext.getBean(ChatMsgMapper.class));
            Serializable pkVal = model.pkVal();
            if (Objects.isNull(pkVal)) {
                chatMsgMapper.insert(model);
                ctx.getInsertCount().incrementAndGet();
            } else {
                QueryWrapper<ChatMsgModel> qw = Wrappers.query();
                qw.eq("room_id", model.getRoomId()).eq("unique_msg_id", model.getUniqueMsgId());
                chatMsgMapper.update(model, qw);
                ctx.getUpdateCount().incrementAndGet();
            }
            storedCount++;
        } catch (Exception e) {
            if (expiryTime - ctx.getStartTime() <= ctx.getTtl()) {
                expiryTime += ctx.getTtl();
            }
            List<Model<?>> saveList = ctx.getSaveMap().computeIfAbsent(model.getClass(),
                    t -> new CopyOnWriteArrayList<>());
            saveList.add(model);
        }
    }
}
