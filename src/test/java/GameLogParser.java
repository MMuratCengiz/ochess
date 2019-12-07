import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.game.Board;
import com.mcp.ochess.game.PieceKind;
import com.mcp.ochess.game.Position;
import com.mcp.ochess.game.Side;

/*
* TODO, left half many cases to handle
* */
public class GameLogParser {
    public static void parse(String toParse, Board board) throws OChessBaseException {
        State state = State.ExpectNum;

        int[] loc = new int[] { 0 };
        char[] toParseC = toParse.toCharArray();

        while (loc[0] < toParseC.length) {
            char c = toParseC[loc[0]++];
            while (isWhitespace(c)) {
                c = toParseC[loc[0]++];
            }

            switch (state) {
                case ExpectNum:
                    state = State.ExpectDot;
                    break;

                case ExpectDot:
                    state = State.ExpectMove1;
                    break;

                case ExpectMove1:
                    consumeMoveNMove(toParseC, loc, board, Side.White);
                    state = State.ExpectMove2;
                    break;

                case ExpectMove2:
                    consumeMoveNMove(toParseC, loc, board, Side.Black);
                    state = State.ExpectNum;
                    break;
            }


        }
    }

    private static void consumeMoveNMove(char[] toParseC, int[] loc, Board board, Side side) throws OChessBaseException {
        loc[0]--;
        Position[] move = consumeMove(toParseC, loc, board, side);
        board.move(move[0], move[1]);
    }

    private static Position[] consumeMove(char[] toParseC, int loc[], Board b, Side side) throws OChessBaseException {
        Position from = null;
        Position to = null;

        char c = toParseC[loc[0]++];

        String possibleFromStr = "";

        PieceKind kind = null;

        if (c == 'Q') {
            kind = PieceKind.Queen;
        }
        if (c == 'N') {
            kind = PieceKind.Knight;
        }
        if (c == 'K') {
            kind = PieceKind.King;
        }
        if (c == 'R') {
            kind = PieceKind.Rook;
        }
        if (c == 'B') {
            kind = PieceKind.Bishop;
        }

        if (!((c + "").equals((c + "").toLowerCase()))) {
            c = toParseC[loc[0]++];
        }

        if (kind != null && countTillWhiteSpace(toParseC, loc[0]) > 1) {
            try {
                from = b.queryPiece(side, kind, (String) null);
            } catch (Exception ignored) {
                try {
                    possibleFromStr = c + "";
                    from = b.queryPiece(side, kind, possibleFromStr);
                } catch (Exception ignoredAgain) {
                    possibleFromStr = toParseC[loc[0]++] + "";
                    from = Position.fromString(possibleFromStr);
                }
            }
        }

        to = Position.fromString(c + "" + toParseC[loc[0]++]);

        char lookahead = toParseC[loc[0]];

        if (! isWhitespace(lookahead) && !isInt(lookahead)) {
            from = to;
            to = Position.fromString(toParseC[loc[0]++] + "" + toParseC[loc[0]++]);
        } else if (from == null) {
            from = b.queryPiece(side, kind, to);
        }

        return new Position[] { from, to };
    }

    private static int countTillWhiteSpace(char[] toParse, int loc) {
        int count = 0;
        while (! isWhitespace(toParse[loc])) {
            if (toParse[loc] != 'x') {
                count++;
            }
            ++loc;
        }
        return count;
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\n';
    }

    private static boolean isInt(char c) {
        return c > '0' && c < '9';
    }
}

enum State {
    ExpectNum,
    ExpectDot,
    ExpectMove1,
    ExpectMove2,
}