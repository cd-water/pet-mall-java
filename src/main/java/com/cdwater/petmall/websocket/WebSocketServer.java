package com.cdwater.petmall.websocket;

import com.cdwater.petmall.common.enums.ReturnMsg;
import com.cdwater.petmall.common.exception.BusinessException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@ServerEndpoint("/ws/{shopId}")
public class WebSocketServer {

    //存放会话对象
    private static final Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 建立连接回调
     *
     * @param session 会话对象
     * @param shopId  宠物店id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("shopId") String shopId) {
        sessionMap.put(shopId, session);
        log.info("宠物店客户端：[{}]建立连接;目前在线连接数——{}", shopId, sessionMap.size());
    }

    /**
     * 接收客户端消息回调
     *
     * @param message 消息：客户端->服务器
     * @param shopId  宠物店id
     */
    @OnMessage
    public void onMessage(String message, @PathParam("shopId") String shopId) {
        log.info("接收客户端消息：宠物店[{}]——[{}]", shopId, message);
    }

    /**
     * 断开连接回调
     *
     * @param shopId 宠物店id
     */
    @OnClose
    public void onClose(@PathParam("shopId") String shopId) {
        sessionMap.remove(shopId);
        log.info("宠物店客户端：[{}]断开连接;目前在线连接数——{}", shopId, sessionMap.size());
    }

    /**
     * 服务器发送消息给指定客户端
     *
     * @param message 消息：服务器->客户端
     * @param shopId  宠物店id
     */
    public void sendToClient(String message, String shopId) {
        Session session = sessionMap.get(shopId);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                throw new BusinessException(ReturnMsg.SYSTEM_ERROR);
            }
        }
    }
}
