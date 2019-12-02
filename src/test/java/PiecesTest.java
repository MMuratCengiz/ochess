import com.mcp.ochess.exceptions.OChessBaseException;
import com.mcp.ochess.exceptions.OChessNetException;
import com.mcp.ochess.game.*;
import com.mcp.ochess.model.MoveResult;
import junit.framework.TestCase;
import org.junit.Test;

public class PiecesTest extends TestCase {
    /* for visualization purposes
      _______________________
   8 |__|__|__|__|__|__|__|__|
   7 |__|__|__|__|__|__|__|__|
   6 |__|__|__|__|__|__|__|__|
   5 |__|__|__|__|__|__|__|__|
   4 |__|__|__|__|__|__|__|__|
   3 |__|__|__|__|__|__|__|__|
   2 |__|__|__|__|__|__|__|__|
   1 |__|__|__|__|__|__|__|__|
      A  B  C  D  E  F  G  H   */

    /*
    Pawn tests
     */
    @Test
    public void testPawn() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D2"), Side.White));
        board.move(Position.fromString("D2"), Position.fromString("D3"));
        assertTrue(board.isOccupied(Position.fromString("D3")));
        assertTrue(!board.isOccupied(Position.fromString("D2")));

        board.move(Position.fromString("D3"), Position.fromString("D4"));
        assertTrue(board.isOccupied(Position.fromString("D4")));
        assertTrue(!board.isOccupied(Position.fromString("D3")));
    }

    @Test
    public void testBlackPawn() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D5"), Side.Black));
        board.move(Position.fromString("D5"), Position.fromString("D4"));
        assertTrue(board.isOccupied(Position.fromString("D4")));
        assertTrue(!board.isOccupied(Position.fromString("D5")));

        board.move(Position.fromString("D4"), Position.fromString("D3"));
        assertTrue(board.isOccupied(Position.fromString("D3")));
        assertTrue(!board.isOccupied(Position.fromString("D4")));
    }

    @Test
    public void testPawnMove2() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D2"), Side.White));
        board.move(Position.fromString("D2"), Position.fromString("D4"));
        assertTrue(board.isOccupied(Position.fromString("D4")));
        assertTrue(!board.isOccupied(Position.fromString("D3")));
        assertTrue(!board.isOccupied(Position.fromString("D2")));
    }

    @Test
    public void testPawnBlocked() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D2"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D3"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("D2"), Position.fromString("D3"));

        assertEquals(status, MoveResultStatus.INVALID_MOVE);
        assertTrue(board.isOccupied(Position.fromString("D3")));
        assertTrue(board.isOccupied(Position.fromString("D2")));
    }

    @Test
    public void testPawnMoveInvalid() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D2"), Side.White));
        MoveResultStatus status = board.move(Position.fromString("D2"), Position.fromString("D5"));
        assertEquals(status, MoveResultStatus.INVALID_MOVE);
    }

    @Test
    public void testEnPassant() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D5"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("E7"), Side.Black));

        board.move(Position.fromString("E7"), Position.fromString("E5"));
        MoveResultStatus status = board.move(Position.fromString("D5"), Position.fromString("E6"));

        assertTrue(!board.isOccupied(Position.fromString("E5")));
        assertTrue(board.isOccupied(Position.fromString("E6")));
        assertEquals(status, MoveResultStatus.EN_PASSANT_MOVE);
    }

    @Test
    public void testKnightMove() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createKnight(board, Position.fromString("B1"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("B1"), Position.fromString("C3"));

        assertTrue(board.isOccupied(Position.fromString("C3")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);
    }

    @Test
    public void testKnightCapture() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createKnight(board, Position.fromString("C3"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("E2"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("C3"), Position.fromString("E2"));

        assertTrue(board.isOccupied(Position.fromString("E2")));
        assertEquals(status, MoveResultStatus.KILL);
    }

    @Test
    public void testBishopMove() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createBishop(board, Position.fromString("A1"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("H8"));

        assertTrue(board.isOccupied(Position.fromString("H8")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);
    }

    @Test
    public void testBishopCapture() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createBishop(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("C3"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("C3"));

        assertTrue(board.isOccupied(Position.fromString("C3")));
        assertEquals(status, MoveResultStatus.KILL);
    }

    @Test
    public void testBishopBlocked() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createBishop(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("C3"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("D4"));

        assertTrue(board.isOccupied(Position.fromString("A1")));
        assertTrue(board.isOccupied(Position.fromString("C3")));
        assertEquals(status, MoveResultStatus.INVALID_MOVE);
    }

    @Test
    public void testRookMove() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createRook(board, Position.fromString("A1"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("A7"));

        assertTrue(board.isOccupied(Position.fromString("A7")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);

        status = board.move(Position.fromString("A7"), Position.fromString("H7"));

        assertTrue(board.isOccupied(Position.fromString("H7")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);
    }

    @Test
    public void testRookCapture() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createRook(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createRook(board, Position.fromString("A7"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("A7"));

        assertTrue(board.isOccupied(Position.fromString("A7")));
        assertEquals(status, MoveResultStatus.KILL);
    }

    @Test
    public void testRookBlocked() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createBishop(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("A7"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("A8"));

        assertEquals(status, MoveResultStatus.INVALID_MOVE);

        status = board.move(Position.fromString("A1"), Position.fromString("A7"));
        assertEquals(status, MoveResultStatus.INVALID_MOVE);
    }

    @Test
    public void testQueenMove() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createQueen(board, Position.fromString("A1"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("H8"));

        assertTrue(board.isOccupied(Position.fromString("H8")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);

        status = board.move(Position.fromString("H8"), Position.fromString("H1"));

        assertTrue(board.isOccupied(Position.fromString("H1")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);
    }

    @Test
    public void testQueenCapture() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createQueen(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("C3"), Side.Black));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("C8"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("C3"));

        assertTrue(board.isOccupied(Position.fromString("C3")));
        assertEquals(status, MoveResultStatus.KILL);

        status = board.move(Position.fromString("C3"), Position.fromString("C8"));

        assertTrue(board.isOccupied(Position.fromString("C8")));
        assertEquals(status, MoveResultStatus.KILL);
    }

    @Test
    public void testQueenBlocked() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createBishop(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("C3"), Side.Black));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("D4"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("D4"));

        assertEquals(status, MoveResultStatus.INVALID_MOVE);
    }
    @Test
    public void testKingMove() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createKing(board, Position.fromString("A1"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("B2"));

        assertTrue(board.isOccupied(Position.fromString("B2")));
        assertEquals(status, MoveResultStatus.MOVED_TO_EMPTY);
    }

    @Test
    public void testKingCapture() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createQueen(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("A2"), Side.Black));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("A2"));

        assertTrue(board.isOccupied(Position.fromString("A2")));
        assertEquals(status, MoveResultStatus.KILL);
    }

    @Test
    public void testKingBlocked() throws OChessBaseException {
        Board board = new Board(true);
        board.addTestingPiece(Piece.createBishop(board, Position.fromString("A1"), Side.White));
        board.addTestingPiece(Piece.createPawn(board, Position.fromString("B2"), Side.White));

        MoveResultStatus status = board.move(Position.fromString("A1"), Position.fromString("B2"));

        assertEquals(status, MoveResultStatus.INVALID_MOVE);
    }

    @Test
    public void testCheck() throws OChessBaseException {
        Board board = new Board(true);

        board.addTestingPiece(Piece.createBishop(board, Position.fromString("E6"), Side.White));
        MoveResultStatus status = board.move(Position.fromString("E6"), Position.fromString("D7"));

        assertEquals(status, MoveResultStatus.CHECK);
    }
}
