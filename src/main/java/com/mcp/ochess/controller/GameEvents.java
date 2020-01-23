package com.mcp.ochess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class GameEvents {
    private final SimpMessageSendingOperations messageOps;

    @Autowired
    public GameEvents(SimpMessageSendingOperations messageOps) {
        this.messageOps = messageOps;
    }

    @EventListener
    public void handleClientConnect(SessionConnectedEvent event) {
        int x = 1;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        int x = 1;
    }
}
