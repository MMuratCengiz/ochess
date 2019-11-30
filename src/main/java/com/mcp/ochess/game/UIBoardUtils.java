package com.mcp.ochess.game;

import java.util.HashMap;

public class UIBoardUtils {
    private static final HashMap<String, String> initialPieceLocations = new HashMap<>() {
        {
            put("0:0", "wh_rook");
            put("0:1", "wh_knight");
            put("0:2", "wh_bishop");
            put("0:3", "wh_queen");
            put("0:4", "wh_king");
            put("0:5", "wh_bishop");
            put("0:6", "wh_knight");
            put("0:7", "wh_rook");

            put("1:0", "wh_pawn");
            put("1:1", "wh_pawn");
            put("1:2", "wh_pawn");
            put("1:3", "wh_pawn");
            put("1:4", "wh_pawn");
            put("1:5", "wh_pawn");
            put("1:6", "wh_pawn");
            put("1:7", "wh_pawn");

            put("7:0", "bl_rook");
            put("7:1", "bl_knight");
            put("7:2", "bl_bishop");
            put("7:3", "bl_queen");
            put("7:4", "bl_king");
            put("7:5", "bl_bishop");
            put("7:6", "bl_knight");
            put("7:7", "bl_rook");

            put("6:0", "bl_pawn");
            put("6:1", "bl_pawn");
            put("6:2", "bl_pawn");
            put("6:3", "bl_pawn");
            put("6:4", "bl_pawn");
            put("6:5", "bl_pawn");
            put("6:6", "bl_pawn");
            put("6:7", "bl_pawn");
        }
    };

    private static String getColor(int row, int cell) {
        return row % 2 == 0 ? (cell % 2 == 0 ? "white" : "gray") : (cell % 2 == 0 ? "gray" : "white");
    }

    public static String createBoard() {
        StringBuilder board = new StringBuilder();

        // loop backwards as the table numbering is bottom up
        for (int row = 7; row >= 0; --row) {
            board.append("<tr>");
            for (int cell = 7; cell >= 0; --cell) {
                String content = "";
                String piece = initialPieceLocations.get(row + ":" + cell);
                if (piece != null) {
                    String type = piece.substring(3);
                    // Todo retrieve content dynamically
                    content = "/ochess/resources/images/" + piece + ".png";
                }

                String format = String.format("<td id='cell_%d_%d' class='cell, %s' onclick='selectCell(%d,%d)' background=\"%s\"></td>",
                        row, // For Cell_1_1, create unique id for javascript
                        cell, // same as above
                        getColor(row, cell),
                        row, // this time to create method parameters
                        cell, // same as above
                        content
                );

                board.append(format);
            }
            board.append("</tr>");
        }

        return board.toString();
    }
}
