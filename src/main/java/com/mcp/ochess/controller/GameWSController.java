package com.mcp.ochess.controller;

import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.Game;
import com.mcp.ochess.game.MoveResultStatus;
import com.mcp.ochess.model.MoveAction;
import com.mcp.ochess.model.MoveResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWSController {

    @MessageMapping("/lobby.move")
    @SendTo("/ingame")
    public MoveResult move(@Payload MoveAction action) throws OChessBaseException {
        // Need to make this autoload state.
        Game.ensureNewGame(action.getLobbyId());
        Game game = Game.getGame(action.getLobbyId());
        MoveResultStatus status = game.move(action.getFrom(), action.getTo());
        MoveResult result = GameRestController.createMoveResult(status);
        result.setMoveId(action.getMoveId());
        return result;
    }
}
