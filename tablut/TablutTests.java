package tablut;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Junit tests for our Tablut Board class.
 *
 * @author Vivant Sakore & Junyi Cao
 */
public class TablutTests {

    /**
     * Tests legalMoves for white pieces to make sure it returns all
     * legal Moves. This method needs to be finished and may need
     * to be changed based on your implementation.
     */
    @Test
    public void testLegalWhiteMoves() {
        Board b = new Board();
        buildBoard(b, INITIAL_BOARD_STATE);

        List<Move> movesList = b.legalMoves(Piece.WHITE);

        assertEquals(56, movesList.size());

        assertFalse(movesList.contains(Move.mv("e7-8")));
        assertFalse(movesList.contains(Move.mv("e8-f")));

        assertTrue(movesList.contains(Move.mv("e6-f")));
        assertTrue(movesList.contains(Move.mv("f5-8")));

    }

    /**
     * Tests legalMoves for black pieces to make sure it returns
     * all legal Moves. This method needs to be finished and may
     * need to be changed based on your implementation.
     */
    @Test
    public void testLegalBlackMoves() {
        Board b = new Board();
        buildBoard(b, INITIAL_BOARD_STATE);

        List<Move> movesList = b.legalMoves(Piece.BLACK);

        assertEquals(80, movesList.size());

        assertFalse(movesList.contains(Move.mv("e8-7")));
        assertFalse(movesList.contains(Move.mv("e7-8")));

        assertTrue(movesList.contains(Move.mv("f9-i")));
        assertTrue(movesList.contains(Move.mv("h5-1")));

    }

    @Test
    public void testMoveLists() {
        Board board = new Board();
        buildBoard(board, MOVE_LIST_TEST);

        List<Move> movesList = board.legalMoves(Piece.BLACK);
        assertEquals(78, movesList.size());

        assertTrue(movesList.contains(Move.mv("e8-4")));
        assertFalse(movesList.contains(Move.mv("e8-5")));

        movesList = board.legalMoves(Piece.WHITE);
        assertEquals(66, movesList.size());

        assertFalse(movesList.contains(Move.mv("f4-7")));
        assertFalse(movesList.contains(Move.mv("f4-6")));
        assertFalse(movesList.contains(Move.mv("f4-i")));
        assertFalse(movesList.contains(Move.mv("d6-4")));
        assertFalse(movesList.contains(Move.mv("d5-7")));

        assertTrue(movesList.contains(Move.mv("d5-e")));
        assertTrue(movesList.contains(Move.mv("d5-1")));
    }

    static void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - 1 - row][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;
    static final Piece W = Piece.WHITE;
    static final Piece B = Piece.BLACK;
    static final Piece K = Piece.KING;

    static final Piece[][] INITIAL_BOARD_STATE = {
            {E, E, E, B, B, B, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {B, E, E, E, W, E, E, E, B},
            {B, B, W, W, K, W, W, B, B},
            {B, E, E, E, W, E, E, E, B},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, B, B, B, E, E, E},
    };

    static final Piece[][] MOVE_LIST_TEST = {
            {E, E, E, B, B, B, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, W, E, E, E, W, E, E},
            {E, E, E, W, E, W, E, E, B},
            {B, B, E, K, E, E, E, B, B},
            {B, E, E, E, E, W, E, E, B},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, B, B, E, E, E},
    };

    static final Piece[][] NON_CAPTURE_1 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, B, K, W, B, E, E},
            {B, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
    };

    static final Piece[][] NON_CAPTURE_2 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, B, E, E, E, E, E},
            {E, E, E, K, E, B, E, E, E},
            {E, B, E, E, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] NON_CAPTURE_3 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {B, E, E, B, E, E, E, E, E},
            {W, E, B, K, E, B, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {B, E, E, E, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] NON_CAPTURE_4 = {
            {E, E, E, B, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {B, E, E, E, W, E, E, E, E},
            {B, B, W, K, E, W, W, E, E},
            {E, E, B, E, W, E, E, E, E},
            {E, E, E, W, E, W, E, E, E},
            {B, E, E, E, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_2 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, B, K, W, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, B, E, E},
            {E, E, E, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_3 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
            {E, E, E, B, K, B, E, E, E},
            {E, E, E, E, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_4 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
            {E, E, B, K, E, B, E, E, E},
            {E, E, E, B, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_5 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, B, E, E, E, E, E},
            {E, E, E, K, E, B, E, E, E},
            {E, E, E, B, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_6 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, W, B, E, E, E, E, E},
            {E, E, B, E, E, B, E, E, E},
            {W, B, E, E, W, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, K, B, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_7 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, B, E, E, E, E, E},
            {E, E, W, E, K, B, E, E, E},
            {B, W, E, W, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, B, E, E, E, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] CAPTURE_8 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, B, E, E, E, K, B},
            {E, E, W, E, E, B, E, E, E},
            {B, W, E, W, B, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, B, B, E, E, B, E, E},
            {E, E, B, E, E, E, E, E, E},
    };

    static final Piece[][] STATIC_VALUE_1 = {
            {E, E, E, E, E, E, E, B, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, B, E, E, K, B},
            {E, E, E, E, E, E, E, B, B},
            {E, E, E, E, E, E, E, E, B},
            {E, E, E, E, E, E, E, E, B},
            {E, E, E, E, E, E, E, B, B},
            {E, E, E, E, E, E, E, E, B},
    };

    static final Piece[][] BLACK_ONE_MOVE_WIN1 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, E, B, W, E, E, E},
            {E, E, E, B, K, E, E, B, E},
            {E, E, E, E, B, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, E, E, B, E},
            {E, E, E, E, B, B, E, E, E},
    };

    static final Piece[][] BLACK_ONE_MOVE_WIN2 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, W, E, E, E},
            {E, E, B, K, E, E, E, B, E},
            {E, E, E, E, W, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, E, E, B, E},
            {E, E, E, E, B, B, E, E, E},
    };

    static final Piece[][] BLACK_ONE_MOVE_WIN3 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, W, E, E, E},
            {B, E, E, K, E, E, E, B, E},
            {E, E, W, B, W, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, E, E, B, E},
            {E, E, E, E, B, B, E, E, E},
    };

    static final Piece[][] BLACK_ONE_MOVE_WIN4 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, B, E, W, E, E, E, E},
            {B, E, K, B, B, W, E, E, E},
            {B, W, E, W, E, E, E, B, E},
            {E, E, W, B, W, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, E, E, B, E},
            {E, E, E, E, B, B, E, E, E},
    };

    static final Piece[][] BLACK_ONE_MOVE_WIN5 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, B, E, W, E, E, E, E},
            {E, E, K, B, B, W, E, E, E},
            {B, W, E, E, E, E, E, E, E},
            {E, E, W, B, W, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, B, B, B, E, E, B, E},
            {E, E, E, E, B, B, E, E, E},
    };

    static final Piece[][] WHITE_ONE_MOVE_WIN1 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, W, E, E, E},
            {B, W, E, E, E, E, K, E, B},
            {E, E, E, B, W, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, E, E, B, E},
            {E, E, E, E, B, B, B, E, E},
    };

    static final Piece[][] WHITE_ONE_MOVE_WIN2 = {
            {E, E, E, E, E, E, E, E, E},
            {E, E, E, E, E, E, E, B, E},
            {E, E, E, E, W, E, E, E, E},
            {E, E, E, B, B, W, E, E, E},
            {B, W, E, E, E, E, E, E, B},
            {E, E, E, B, W, B, E, E, E},
            {E, E, E, E, W, E, E, E, E},
            {B, K, E, E, E, E, E, B, E},
            {E, B, E, E, B, B, B, E, E},
    };

}
