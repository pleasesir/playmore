package org.playmore.gateway.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.playmore.api.config.AppContext;
import org.playmore.common.component.ComponentLifecycle;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.config.GatewayOrder;
import org.playmore.gateway.config.GatewayServerConfig;
import org.playmore.gateway.net.codec.factory.MessageCodecFactory;
import org.playmore.gateway.util.ChannelUtil;
import org.springframework.core.io.FileSystemResource;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.playmore.gateway.config.GatewayOrder.WEBSOCKET_SERVER;

/**
 * @ClassName WebsocketServer
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:04
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:04
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class BaseWebsocketServer implements ComponentLifecycle<GatewayOrder> {

    public static int MAX_CONNECT = 10000;
    private final int port;
    private final IpSubnetFilterRule aliHealthCheckerIps = new IpSubnetFilterRule("100.64.0.0", 10, IpFilterRuleType.ACCEPT);
    public GlobalTrafficShapingHandler trafficShapingHandler;
    public AtomicInteger maxMessage = new AtomicInteger(0);
    public AtomicInteger maxConnect = new AtomicInteger(0);
    ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private SslContext sslContext;

    public BaseWebsocketServer(int port, int corePoolSize) throws SSLException {
        this.port = port;
        GatewayServerConfig config = AppContext.getBean(GatewayServerConfig.class);

        if (config.isSslEnable()) {
            // 启用 SSL证书
            String certificateFilePath = config.getSslPath() + File.separator + config.getCertificateFile();
            String privateKeyFilePath = config.getSslPath() + File.separator + config.getPrivateKeyFile();
            File pem = new FileSystemResource(certificateFilePath).getFile();
            File key = new FileSystemResource(privateKeyFilePath).getFile();
            sslContext = SslContextBuilder.forServer(pem, key).build();
            LogUtil.COMMON_LOGGER.info("SSL certificate file: {}", certificateFilePath);
            LogUtil.COMMON_LOGGER.info("SSL private key file: {}", privateKeyFilePath);
        }
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public void start() {
        // 定义两个工作线程 bossGroup workerGroup 用于管理channel连接
        bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        trafficShapingHandler = new GlobalTrafficShapingHandler(workerGroup, 5000L);
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childHandler(new ConnectChannelHandler());
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);

        ChannelFuture f;
        try {
            // 绑定端口，同步等待成功
            LogUtil.common("+++++++++++++++++++++++++++ gateway band port: ", port);
            f = bootstrap.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LogUtil.error("服务器启动绑定端口异常", e);
        }
    }

    @Override
    public void afterStart() {

    }

    @Override
    public void beforeStop() {

    }

    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public GatewayOrder order() {
        return WEBSOCKET_SERVER;
    }

    /**
     * 初始化处理客户端协议交互的handler，在创建ConnectServer对象是，必须实现该方法
     *
     * @return
     */
    protected abstract SimpleChannelInboundHandler<GatewayMsg> initGameServerHandler();

    /**
     * 阿里云健康检测拦截
     *
     * @param ctx ctx
     * @return true - 阿里云的健康检查
     */
    public boolean isAliHealthCheckAddress(ChannelHandlerContext ctx) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return isAliHealthCheckAddress(inetSocketAddress);
    }

    public boolean isAliHealthCheckAddress(InetSocketAddress inetSocketAddress) {
        if (aliHealthCheckerIps.matches(inetSocketAddress)) {
            LogUtil.debug("aliyun health check ip: {}", inetSocketAddress.getAddress());
            return true;
        }
        return false;
    }

    private class ConnectChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeLine = ch.pipeline();
            pipeLine.addLast(trafficShapingHandler);
            // 心跳 360秒查看一次在线的客户端channel是否空闲
            pipeLine.addLast(new IdleStateHandler(360, 0, 0, TimeUnit.SECONDS));
            pipeLine.addLast(new HeartbeatHandler());
            if (Objects.nonNull(sslContext)) {
                pipeLine.addFirst(sslContext.newHandler(ch.alloc()));
            }
            pipeLine.addLast("http-code-c", new HttpServerCodec());
            pipeLine.addLast("aggregator", new HttpObjectAggregator(65535));
            pipeLine.addLast("ChunkedWrite", new ChunkedWriteHandler());
            pipeLine.addLast("ProtocolHandler", new WebSocketServerProtocolHandler("/", null, true));
            pipeLine.addLast("confirmDecoder", MessageCodecFactory.getConfirmDecoder());
            pipeLine.addLast("WsMsgDecoder", MessageCodecFactory.getWsDecoder());
            pipeLine.addLast("WsMsgEncoder", MessageCodecFactory.getWsEncoder());
            pipeLine.addLast("BatchWsEncoder", MessageCodecFactory.getBatchWsMsEncoder());
            pipeLine.addLast("protobufHandler", MessageCodecFactory.getGatewayMessageDispatcher());
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            LogUtil.common("ConnectChannelHandler channelUnregistered:" + Thread.currentThread().threadId());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            LogUtil.common("ConnectChannelHandler channelInactive:" + Thread.currentThread().threadId());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            Throwable throwable;
            Throwable ex = (throwable = cause.getCause()) == null ? cause : throwable;
            ChannelUtil.closeChannel(ctx, ex.getMessage());
        }
    }
}

class HeartbeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.READER_IDLE) {
                ChannelUtil.closeChannel(ctx, "HeartbeatHandler trigger READER_IDLE");
            }
        }
    }
}
