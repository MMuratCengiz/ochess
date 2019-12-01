package com.mcp.ochess.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.Game;
import com.mcp.ochess.game.MoveResultStatus;
import com.mcp.ochess.model.ActionResult;
import com.mcp.ochess.model.MoveRequest;
import com.mcp.ochess.model.MoveResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/play")
public class GameRestController {
    @PostMapping(value = "/lobby/{lobbyid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MoveResult executeMove(@PathVariable String lobbyid, @RequestBody String postData) throws JsonProcessingException, OChessBaseException {
        ObjectMapper mapper = new ObjectMapper();
        MoveRequest request = mapper.readValue(postData, MoveRequest.class);

        Game game = Game.getGame(lobbyid);
        MoveResultStatus status = game.move(request.getFrom(), request.getTo());

        if (status == MoveResultStatus.INVALID_MOVE) {
            throw new OChessBaseException("Invalid move.");
        }

        if (status == MoveResultStatus.PIECE_DOES_NOT_EXIST) {
            throw new OChessBaseException("No piece exists in the selected cell.");
        }

        MoveResult result = new MoveResult();

        result.setValidMove(status != MoveResultStatus.PIECE_DOES_NOT_EXIST || status != MoveResultStatus.INVALID_MOVE);

        switch (status) {
            case INVALID_MOVE:
                result.setActionResult("InvalidMove");
                break;
            case PIECE_DOES_NOT_EXIST:
                result.setActionResult("PieceDoesNotExist");
                break;
            case MOVED_TO_EMPTY:
                result.setActionResult("EmptyCell");
                break;
            case KILL:
                result.setActionResult("Kill");
                break;
            case CHECK:
                result.setActionResult("Check");
                break;
            case CHECKMATE:
                result.setActionResult("Checkmate");
                break;
        }

        return result;
    }


}
