package org.playmore.game.domain.entity;

import com.google.protobuf.GeneratedMessage;
import lombok.Getter;
import lombok.Setter;
import org.playmore.api.annotation.Subscribe;
import org.playmore.api.domain.PlayerEntity;
import org.playmore.api.exception.GameError;
import org.playmore.api.exception.MwException;
import org.playmore.api.handler.BaseRpcHandler;
import org.playmore.api.util.MethodUtil;
import org.playmore.api.verticle.BaseVerticle;
import org.playmore.api.verticle.eventbus.event.impl.GameEvent;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.msg.ChannelId;
import org.playmore.common.util.LogUtil;
import org.playmore.game.domain.entity.function.BaseFunctionEntity;
import org.playmore.game.domain.entity.function.FunctionType;
import org.playmore.game.domain.entity.role.Account;
import org.playmore.game.domain.entity.role.DbRole;

import java.util.*;

import static org.playmore.api.verticle.eventbus.event.impl.GameEvent.EXC_PLAYER_EVENT;
import static org.playmore.common.constant.VertxContextConst.ACTOR_KEY;

/**
 * @ClassName Player
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/4 22:39
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/4 22:39
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class PlayerActor extends BaseVerticle implements PlayerEntity {

    private final long roleId;
    /**
     * 功能信息hash表
     */
    @Getter
    private final Map<FunctionType, BaseFunctionEntity<? extends GeneratedMessage>> functionMap;
    @Setter
    @Getter
    private transient Account account;
    /**
     * 上一次存储数据时间戳 单位: 秒
     */
    @Setter
    private transient int lastSaveTime;
    /**
     * 是否无敌
     */
    @Setter
    @Getter
    private transient boolean strength;

    public PlayerActor(long roleId) {
        this.roleId = roleId;
        functionMap = new HashMap<>(FunctionType.values().length);
    }

    public PlayerActor(long roleId, Account account) {
        super();
        this.roleId = roleId;
        this.account = account;
        functionMap = new HashMap<>(FunctionType.values().length);
    }

    @Override
    public void start() throws Exception {
        try {
            super.start();
            // 注册玩家handler事件处理
            register(EXC_PLAYER_EVENT,
                    MethodUtil.consumerName(this.getClass(), "playerEvent"), this::playerEvent);
        } finally {
            context.put(ACTOR_KEY, this);
        }
    }

    @Override
    public long getRoleId() {
        return roleId;
    }

    @Override
    protected String uniqueAddress() {
        return String.valueOf(roleId);
    }

    /**
     * 服务器启动后处理逻辑
     *
     * @param empty 空置方法参数
     */
    @Subscribe(gameEvent = GameEvent.SERVER_START)
    public void serverStartLogic(Object empty) {
//        if (CheckNull.nonEmpty(functionMap)) {
//            functionMap.values().stream()
//                    .filter(tmp -> tmp instanceof ServerStartLogic)
//                    .sorted(Comparator.comparingInt(tmp -> tmp.getType().getInitOrder()))
//                    .forEach(tmp -> ((ServerStartLogic) tmp).dealAfterServerStart());
//        }
//        // 重新计算玩家英雄战斗力, 总战斗力
//        calculateAllFight();
    }

    /**
     * 获取玩家详情pb
     *
     * @param msg 获取玩家详情pb请求结构体
     * @return 玩家详情pb
     */
//    @Subscribe(gameEvent = GameEvent.GET_PLAYER_DETAIL_PB)
//    public CommonPb.RoleDetailPb getDetailPb(GetRoleDetailPbMsg msg) {
//        return PbHelper.toDetail(this, msg.fightAttr(), msg.needHero());
//    }

    /**
     * 获取玩家详情
     *
     * @param detail 是否包含更多详细信息
     * @return 玩家dto
     */
//    @Subscribe(gameEvent = GameEvent.GET_PLAYER_DETAIL)
//    public PlayerRoleDto getDetail(boolean detail) {
//        PlayerRoleDto dto;
//        ServerConfig serverConfig = AppContext.getBean(ServerConfig.class);
//        if (detail) {
//            calculateAllFight();
//            dto = DtoUtil.createRoleDto(this, serverConfig);
//        } else {
//            dto = DtoUtil.createBriefRoleDto(this, serverConfig);
//        }
//        return dto;
//    }

    /**
     * 接收玩家请求事件
     *
     * @param handler 玩家事件
     * @throws MwException 抛出异常
     */
    public void playerEvent(BaseRpcHandler<? extends BaseRpcMsg> handler) {
        handler.run();
    }

//    @Subscribe(gameEvent = GameEvent.SYNC_PLAYER_PB)
//    public void syncRolePb(SyncPbMsg msg) {
//        if (!isOnline()) {
//            return;
//        }
//        GameServer.getInstance().syncMsgToPlayer(msg.basePb(), msg.body());
//    }

    /**
     * 获取玩家网关相关信息
     *
     * @param rqMsg 请求消息体
     * @return 玩家网关相关内容消息体
     */
//    @Subscribe(gameEvent = GameEvent.GET_ROLE_GATEWAY_ID)
//    public SyncOnlinePbRsMsg getRoleChannelInfo(SyncOnlinePbRqMsg rqMsg) {
//        if (rqMsg.camp() > 0 && getCamp() != rqMsg.camp()) {
//            return null;
//        }
//
//        return new SyncOnlinePbRsMsg(getGatewayServerId(), getChannelId());
//    }
    public DbRole saveData() {
        DbRole dbRole = new DbRole(roleId);
        functionMap.values().forEach(functionEntity -> {
            try {
                saveData(dbRole, functionEntity);
            } catch (Throwable ex) {
                LogUtil.error("存储玩家数据: ", functionEntity.getClass(), ", 报错: ", ex);
            }
        });
        return dbRole;
    }

    private <Type extends GeneratedMessage> void saveData(DbRole dbRole, BaseFunctionEntity<Type> functionEntity) {
//        if (functionEntity instanceof LordFunction) {
//            dbRole.setLordFunction(((LordFunction) functionEntity).clone());
//        } else {
//            FunctionType ft = functionEntity.getType();
//            RoleFunctionData.setFieldData(dbRole, ft, functionEntity.serverBase().toByteArray());
//        }
    }

    public void loadData(DbRole dbRole) {
        Arrays.stream(FunctionType.values())
                .sorted(Comparator.comparingInt(FunctionType::getInitialPriority))
                .forEach(functionType -> {
//                    if (FunctionType.LORD_DATA_FUNC.equals(functionType)) {
//                        return;
//                    }
//                    BaseFunctionEntity<?> function = ReflectUtil.newInstance(
//                            functionType.getClazz());
//                    function.setPlayer(this);
//                    try {
//                        function.load(dbRole);
//                    } catch (InvalidProtocolBufferException | IllegalAccessException e) {
//                        throw new RuntimeException(e);
//                    }
//                    functionMap.put(functionType, function);
                });
    }

//    public LordFunction createLordData() {
//        LordFunction function = new LordFunction(this);
//        functionMap.put(function.getType(), function);
//        return function;
//    }

    @SuppressWarnings("unchecked")
    public <T extends BaseFunctionEntity<? extends GeneratedMessage>> T getFunction(FunctionType functionType) {
        BaseFunctionEntity<? extends GeneratedMessage> function = functionMap.get(functionType);
        if (function == null) {
            throw new MwException(GameError.FUNCTION_FOUND_WRONG, ", functionType: ", functionType, ", return: null");
        }
        if (function.getClass() != functionType.getClazz()) {
            LogUtil.common("获取功能时, 不是对应功能的function, function.class: ",
                    function.getClass(), ", functionType.class: ", functionType.getClazz());
            throw new MwException(GameError.FUNCTION_FOUND_WRONG, ", functionType: ", functionType, ", return: ", function);
        }
        return (T) function;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseFunctionEntity<? extends GeneratedMessage>> T function(FunctionType functionType) {
        BaseFunctionEntity<? extends GeneratedMessage> function = functionMap.get(functionType);
        if (function == null) {
            return null;
        }
        if (function.getClass() != functionType.getClazz()) {
            LogUtil.common("获取功能时, 不是对应功能的function, function.class: ",
                    function.getClass(), ", functionType.class: ", functionType.getClazz());
            throw new MwException(GameError.FUNCTION_FOUND_WRONG, ", functionType: ", functionType, ", return: ", function);
        }
        return (T) function;
    }

    public ChannelId getChannelId() {
        if (account == null) {
            return null;
        }
        return account.getChannelId();
    }

    public boolean isOnline() {
        if (account == null) {
            return false;
        }
        return account.getChannelId() != null;
    }

    /**
     * 跨天消息处理
     *
     * @param syncClient 是否同步客户端
     */
    @Subscribe(gameEvent = GameEvent.ROLE_ACROSS_DAY)
    public void dealAcrossDay(boolean syncClient) {
        LogUtil.common("roleId: ", roleId, ", 跨天处理开始...");
//        functionMap.values().stream()
//                .filter(func -> func instanceof AcrossDayLogic)
//                .map(func -> (AcrossDayLogic) func)
//                .sorted(Comparator.comparing(AcrossDayLogic::acrossDayPriority))
//                .forEach((func) -> {
//                    try {
//                        func.dealAcrossDay(true);
//                    } catch (Exception ex) {
//                        LogUtil.error("roleId: ", roleId, ", 跨天处理: ", func.getClass(), ", 报错: ", ex);
//                    }
//                });
        LogUtil.common("roleId: ", roleId, ", 跨天处理结束");
    }

    /**
     * 配置重载
     *
     * @param empty 传入参数
     */
    @Subscribe(gameEvent = GameEvent.ROLE_CONFIG_RELOAD)
    public void configReload(Object empty) {
//        if (CheckNull.nonEmpty(functionMap)) {
//            functionMap.values().stream().filter(tmp -> tmp instanceof ConfigReloadLogic)
//                    .sorted(Comparator.comparingInt(tmp ->
//                            ((ConfigReloadLogic) tmp).dealAfterConfigReloadPriority()))
//                    .forEach(tmp -> ((ConfigReloadLogic) tmp).dealAfterConfigReload());
//        }
    }

    @Subscribe(gameEvent = GameEvent.GATEWAY_SERVER_OFFLINE)
//    public void checkDisconnectGateway(GatewayStatusMsg msg) {
//        // 获取当前角色所连接的网关ID
//        int roleGateId = getGatewayServerId();
//
//        // 如果不强制登出且请求的网关ID与当前角色的网关ID不一致，则不执行登出操作
//        if (msg.gatewayId() != roleGateId) {
//            return;
//        }
//
//        logoutLogic(msg.gatewayId(), msg.force());
//    }

    /**
     * 安全登出函数。
     * 该函数用于处理玩家的登出逻辑，确保登出操作的安全性。通过检查gatewayId和当前角色所连接的gatewayId是否一致，
     * 来决定是否执行登出操作。如果force参数为true，或者gatewayId与当前角色的gatewayId一致，则执行登出逻辑。
     * 登出逻辑包括前置登出逻辑和实际的登出操作，登出操作完成后，会执行登出后的处理逻辑。
     *
     * @param msg 登出相关消息
     */
//    @Subscribe(gameEvent = GameEvent.ROLE_LOG_OUT)
//    public void logOut(RoleLogoutMsg msg) {
//        logoutLogic(msg.gatewayId(), msg.force());
//    }

    /**
     * 登出逻辑
     *
     * @param gatewayId 网关id
     * @param force     是否强制 玩家账号下线
     */
//    public void logoutLogic(int gatewayId, boolean force) {
//        // 如果不强制登出且请求的网关ID与当前角色的网关ID不一致，则不执行登出操作
//        if (!force && gatewayId != getGatewayServerId()) {
//            return;
//        }
//
//        LogUtil.common("roleId: ", roleId, ", channelId: ", account.getClientId(), " 玩家下线!");
//        // 清除账号的客户端ID，表示账号已退出登录
//        account.setClientId(null);
//        VertxUtil.sendEvent(GameEvent.ROLE_LOG_OUT, roleId);
//
//        // 通知账号服下线
//        AccountRoleDto dto = DtoUtil.accountRoleDto(this);
//        SeasonMapData data = VertxUtil.requestEvent(GameEvent.CHECK_AND_GET_SEASON_DATA, false);
//        if (CheckNull.nonEmpty(data)) {
//            long uniqueId = data.getUniqueId();
//            int mapServerId = data.getMapServerId();
//            // 同步rpc玩家离线
//            NetComponent.getInstance().publishRpcOffline(roleId, uniqueId, mapServerId, dto);
//        }
//        LordFunction function = getFunction(FunctionType.LORD_DATA_FUNC);
//        function.setOffTime(TimeHelper.getCurrentSecond());
//        function.setOlTime(onLineTime(function));
//        // 执行登出后的处理逻辑, 处理玩家下线后的逻辑
//        dealAfterLogOut();
//    }

    /**
     * 停服时踢下线
     */
//    @Subscribe(gameEvent = TICK_ONLINE_BY_STOP)
//    public void tickOutByStopServer(Object empty) {
//        if (isOnline()) {
//            // 先通知网关断开与客户端的连接
//            LordFunction lord = getFunction(FunctionType.LORD_DATA_FUNC);
//            lord.setOffTime(TimeHelper.getCurrentSecond());
//            lord.setOlTime(onLineTime(lord));
//        }
//
//        logoutLogic(0, true);
//    }

    /**
     * 后台强制踢下线
     */
//    @Subscribe(gameEvent = GameEvent.TICK_ONLINE_PLAYER_ON_CONDITION)
//    public void tickOutByBackgroundUnSafe(long offlineTime) {
//        if (!isOnline()) {
//            return;
//        }
//        if (offlineTime > 0 && getLord().getOnTime() > offlineTime) {
//            return;
//        }
//
//        // 先通知网关断开与客户端的连接
//        ChannelId channelId = getChannelId();
//        AppContext.getBean(DubboReferConfig.class).clientCloseSync(channelId,
//                null, channelId.gatewayServerId(), null);
//        logoutLogic(0, true);
//    }

    /**
     * 重新计算玩家战力
     */
//    public boolean reCalculateFight() {
//        LordFunction lord = getFunction(FunctionType.LORD_DATA_FUNC);
//        if (CheckNull.isNull(lord)) {
//            return false;
//        }
//        HeroFunction heroFunction = getFunction(FunctionType.HERO_FUNC);
//        long newBattleFight = heroFunction.calBattleFight();
//        boolean fightChg = false;
//        if (newBattleFight != lord.getBattleFight()) {
//            lord.setBattleFight(newBattleFight);
//            fightChg = true;
//        }
//        if (fightChg) {
//            GameServer.getInstance().synRoleFightChange();
//            KafkaComponent kafkaComponent = AppContext.getBean(KafkaComponent.class);
//            // 更新manager玩家数据
//            PlayerRoleDto roleDto = DtoUtil.createRoleDto(this, AppContext.getBean(ServerConfig.class));
//            kafkaComponent.asyncUploadManagerRole(roleDto);
//            // 更新战力榜
//            RankUpdateDto fightRankDto = DtoUtil.createUpdateRankValueDto(this,
//                    RankType.FIGHT_RANK_TYPE, newBattleFight);
//            if (Objects.nonNull(fightRankDto)) {
//                kafkaComponent.asyncRankUpdate(fightRankDto);
//            }
//        }
//        return fightChg;
//    }

    /**
     * 判断时间间隔存储
     *
     * @return 是否可存储
     */
//    public boolean isCanSave(int now) {
//        int saveInterval = now - lastSaveTime;
//        if (isOnline()) {
//            // 在线玩家
//            return saveInterval >= Constant.ONLINE_PLAYER_SAVE_DELAY_PERIOD;
//        } else {
//            // 离线玩家
//            LordFunction function = getFunction(FunctionType.LORD_DATA_FUNC);
//            int offTime = now - function.getOffTime();
//            if (offTime < Constant.DAY_SECOND) {
//                return saveInterval >= Constant.DAY_OFFLINE_PLAYER_SAVE_PERIOD;
//            } else if (offTime < Constant.WEEK_SECOND) {
//                return saveInterval >= Constant.WEEK_OFFLINE_PLAYER_SAVE_PERIOD;
//            } else if (offTime < Constant.MONTH_SECOND) {
//                return saveInterval >= Constant.WEEK_OFFLINE_PLAYER_SAVE_PERIOD;
//            } else {
//                return saveInterval >= Constant.OVER_MONTH_OFFLINE_PLAYER_SAVE_PERIOD;
//            }
//        }
//    }

    /**
     * 在线时长
     *
     * @return 在线时长秒数
     */
//    private int onLineTime(LordFunction lord) {
//        int now = TimeHelper.getCurrentSecond();
//        int nowDay = TimeHelper.getCurrentDay();
//
//        int lastDay = TimeHelper.getDay(lord.getOnTime());
//        if (nowDay != lastDay) {
//            // 登录时间不为当天,则取0点到当前时间
//            int noTime = TimeHelper.getTodayZone(now);
//            return now - noTime;
//        } else {
//            // 登录时间为当天,则取累积时长
//            int onlineTime = lord.getOlTime() + now - lord.getOnTime();
//            onlineTime = Math.min(onlineTime, TimeHelper.DAY_S);
//            return onlineTime;
//        }
//    }

    /**
     * 获取玩家战力
     *
     * @return 战斗力
     */
//    public long getBattleFight() {
//        LordFunction lordFunction = getFunction(FunctionType.LORD_DATA_FUNC);
//        return lordFunction.getBattleFight();
//    }

    /**
     * 保存账号封禁状态
     */
//    public void saveAccountForbidStatus() {
//        if (CheckNull.isNull(account)) {
//            return;
//        }
//        Account clone = account.clone();
//        AppContext.getBean(AccountMapperService.class).updateAccountForbidStatus(clone);
//    }

//    public int nextKey() {
//        LordFunction lordFunction = getFunction(FunctionType.LORD_DATA_FUNC);
//        return lordFunction.nextKey();
//    }

    /**
     * 登录或创角之后需要处理的逻辑
     */
    public void dealAfterRoleLogin(boolean reConnect) {
        // 请求roleLogin协议之后
        functionMap.values().stream()
                .sorted(Comparator.comparingInt(BaseFunctionEntity::dealPriorityAfterLogin))
                .forEach(tmp -> tmp.dealAfterBeginGame(reConnect));
        // 登录时重新计算战力
//        calculateAllFight();
    }

    public void dealAfterCreateRole() {
        // 玩家创角后处理逻辑
        functionMap.values().stream()
                .sorted(Comparator.comparingInt(BaseFunctionEntity::dealPriorityAfterCreateRole))
                .forEach(BaseFunctionEntity::dealAfterCreateRole);
    }

    /**
     * 退出游戏时, 需要处理的逻辑
     */
    private void dealAfterLogOut() {
        functionMap.values().forEach(BaseFunctionEntity::dealAfterLogOut);
    }

    /**
     * 功能刚开启时, 需要处理的逻辑
     */
    public void dealAtFuncOpen() {
        functionMap.values().forEach(BaseFunctionEntity::dealAtFuncOpen);
    }

    public int getGatewayServerId() {
        if (account == null) {
            return -1;
        }
        ChannelId channelId = account.getChannelId();
        if (channelId == null) {
            return -1;
        }

        return channelId.gatewayServerId();
    }

//    public LordFunction getLord() {
//        return getFunction(FunctionType.LORD_DATA_FUNC);
//    }
//
//    public int getCamp() {
//        WorldFunction worldFunction = getFunction(FunctionType.WORLD_FUNC);
//        return worldFunction.getCamp();
//    }
//
//    public void calculateAllFight() {
//        HeroFunction heroFunction = getFunction(FunctionType.HERO_FUNC);
//        heroFunction.reAddAllHeroFuncAttr();
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerActor player = (PlayerActor) o;
        return roleId == player.roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}
