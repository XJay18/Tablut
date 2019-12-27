package tablut;

import org.junit.Test;

import static org.junit.Assert.*;
import static tablut.TablutTests.*;
import static tablut.Move.*;

public class AITest {

    @Test
    public void testKingPosValue1() {
        Board board = new Board();
        buildBoard(board, STATIC_VALUE_1);
        assertTrue(AI.kingPosValue(board) < -100);
    }

    @Test
    public void testBlackOneMoveWin() {
        Board board = new Board();
        buildBoard(board, BLACK_ONE_MOVE_WIN1);
        assertTrue(AI.blackOneMoveWin(board));
        buildBoard(board, BLACK_ONE_MOVE_WIN2);
        assertTrue(AI.blackOneMoveWin(board));
        buildBoard(board, BLACK_ONE_MOVE_WIN3);
        assertTrue(AI.blackOneMoveWin(board));
        buildBoard(board, BLACK_ONE_MOVE_WIN4);
        assertTrue(AI.blackOneMoveWin(board));
        buildBoard(board, BLACK_ONE_MOVE_WIN5);
        assertFalse(AI.blackOneMoveWin(board));
    }

    @Test
    public void testWhiteOneMoveWin() {
        Board board = new Board();
        buildBoard(board, WHITE_ONE_MOVE_WIN1);
        board.makeMove(mv("i5-h"));
        assertTrue(AI.whiteOneMoveWin(board));
        board = new Board();
        buildBoard(board, WHITE_ONE_MOVE_WIN2);
        board.makeMove(mv("i5-h"));
        assertFalse(AI.whiteOneMoveWin(board));
    }

    @Test
    public void testKingHostile() {
        Board board = new Board();
        buildBoard(board, BLACK_ONE_MOVE_WIN1);
        assertEquals(1,
                AI.kingAdjHostile(
                        board, board.kingPosition(), true).size());
    }
}
