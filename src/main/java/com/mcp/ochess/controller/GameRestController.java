package com.mcp.ochess.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.Game;
import com.mcp.ochess.game.MoveResultStatus;
import com.mcp.ochess.model.MoveRequest;
import com.mcp.ochess.model.MoveResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game/play")
public class GameRestController {
    @PostMapping(value = "/lobby/{lobbyid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MoveResult executeMove(@PathVariable Integer lobbyId, @RequestBody String postData) throws JsonProcessingException, OChessBaseException {
        ObjectMapper mapper = new ObjectMapper();
        MoveRequest request = mapper.readValue(postData, MoveRequest.class);

        Game game = Game.getGame(lobbyId);
        MoveResultStatus status = game.move(request.getFrom(), request.getTo(), null); // todo

        if (status == MoveResultStatus.INVALID_MOVE) {
            throw new OChessBaseException("Invalid move.");
        }

        if (status == MoveResultStatus.PIECE_DOES_NOT_EXIST) {
            throw new OChessBaseException("No piece exists in the selected cell.");
        }

        MoveResult result = createMoveResult(status);

        return result;
    }

    public static MoveResult createMoveResult(MoveResultStatus status) {
        MoveResult result = new MoveResult();

        result.setValidMove(
                status != MoveResultStatus.PIECE_DOES_NOT_EXIST &&
                status != MoveResultStatus.INVALID_MOVE_KING_THREATENED &&
                status != MoveResultStatus.OUT_OF_TURN &&
                status != MoveResultStatus.INVALID_MOVE);

        switch (status) {
            case OUT_OF_TURN:
                result.setActionResult("OutOfTurn");
                break;
            case INVALID_MOVE:
                result.setActionResult("InvalidMove");
                break;
            case INVALID_MOVE_KING_THREATENED:
                result.setActionResult("InvalidMoveKingThreatened");
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
