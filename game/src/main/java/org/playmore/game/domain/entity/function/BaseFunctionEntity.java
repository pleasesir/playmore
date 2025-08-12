package org.playmore.game.domain.entity.function;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.playmore.common.pb.GamePb;
import org.playmore.game.domain.entity.PlayerActor;
import org.playmore.game.domain.entity.role.DbRole;
import org.playmore.game.util.DbUtil;
import org.playmore.pb.CommonPb;

import java.util.Objects;

/**
 * @ClassName BaseFunctionEntity
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/4 22:38
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/4 22:38
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class BaseFunctionEntity<Type extends GeneratedMessage> implements GamePb<Type> {

    protected transient PlayerActor player;

    public BaseFunctionEntity() {
    }

    public BaseFunctionEntity(PlayerActor player) {
        this.player = player;
    }

    public BaseFunctionEntity<Type> load(DbRole entity) throws InvalidProtocolBufferException, IllegalAccessException {
        loadData(entity);
        initData();
        start();
        return this;
    }

    /**
     * 加载具体功能信息
     *
     * @param entity
     */
    protected void loadData(DbRole entity) throws InvalidProtocolBufferException, IllegalAccessException {
        CommonPb.FunctionClientBase basePb = DbUtil.getFunctionBase(entity, getType());
        if (Objects.nonNull(basePb)) {
            deserialize(basePb.getExtension(ext()));
        }
    }

    /**
     * 创建新账号时, 初始化数据
     */
    public void init() {
        initData();
        start();
        player.getFunctionMap().put(getType(), this);
    }

    /**
     * 初始化功能具体数据
     */
    protected void initData() {
    }

    public CommonPb.FunctionClientBase clientBase() {
        CommonPb.FunctionClientBase.Builder baseBuilder = CommonPb.FunctionClientBase.newBuilder();
        baseBuilder.setType(getType().ordinal());
        Type type = serialize(false);
        baseBuilder.setExtension(ext(), type);
        return baseBuilder.build();
    }

    public CommonPb.FunctionClientBase serverBase() {
        CommonPb.FunctionClientBase.Builder baseBuilder = CommonPb.FunctionClientBase.newBuilder();
        Type type = serialize(true);
        baseBuilder.setExtension(ext(), type);
        return baseBuilder.build();
    }

    /**
     * 同步客户端功能数据
     */
    public void synFuncDataToClient() {
//        if (!player.isOnline()) {
//            return;
//        }
//        com.gryphpoem.game.zw.pb.GamePb.SynFunctionDataRs.Builder builder = com.gryphpoem.game.zw.pb.GamePb.SynFunctionDataRs.newBuilder();
//        builder.addFunctionBase(clientBase());
//        BasePb.Base msg = PbHelper.createSynBase(
//                com.gryphpoem.game.zw.pb.GamePb.SynFunctionDataRs.EXT_FIELD_NUMBER,
//                com.gryphpoem.game.zw.pb.GamePb.SynFunctionDataRs.ext,
//                builder.build()
//        ).build();
//        GameServer.getInstance().syncMsgToPlayer(msg);
    }

    /**
     * function启动
     */
    public void start() {
    }

    /**
     * 创角时处理的优先级
     *
     * @return 创角逻辑优先级
     */
    public int dealPriorityAfterCreateRole() {
        return 0;
    }

    /**
     * 玩家创角时需要处理的逻辑
     */
    public void dealAfterCreateRole() {
    }

    /**
     * 登录时处理的优先级
     *
     * @return 登录逻辑优先级
     */
    public int dealPriorityAfterLogin() {
        return 0;
    }

    /**
     * 玩家登录时需要处理的逻辑
     */
    public void dealAfterBeginGame(boolean reConnect) {
    }

    /**
     * 玩家退出游戏时需要处理的逻辑
     */
    public void dealAfterLogOut() {
    }

    public void dealAtFuncOpen() {
    }

    /**
     * 功能类型
     *
     * @return 功能枚举类型
     */
    public abstract FunctionType getType();

    /**
     * 功能扩展pb
     *
     * @return 功能扩展pb
     */
    public abstract GeneratedMessage.GeneratedExtension<CommonPb.FunctionClientBase, Type> ext();
}
