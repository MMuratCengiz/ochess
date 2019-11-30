package com.mcp.ochess.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.ochess.model.ActionResult;
import com.mcp.ochess.model.MoveRequest;
import com.mcp.ochess.model.MoveResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/play")
public class GameRestController {
    @PostMapping(value = "/lobby/{lobbyid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MoveResult executeMove(@PathVariable String lobbyid, @RequestBody String postData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MoveRequest request = mapper.readValue(postData, MoveRequest.class);



        MoveResult result = new MoveResult();

        result.setValidMove(true);
        result.setMove(postData);
        result.setActionResult("EmptyCell");

        return result;
    }


}
