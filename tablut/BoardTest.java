package tablut;

import org.junit.Test;

import static org.junit.Assert.*;

import static tablut.Piece.*;
import static tablut.Move.*;
import static tablut.Square.*;
import static tablut.TablutTests.*;

public class BoardTest {
    @Test
    public void testInitialize() {
        Board board = new Board();
        String stdBoard = String.format(" 9 - - - B B B - - -%n"
                + " 8 - - - - B - - - -%n"
                + " 7 - - - - W - - - -%n"
                + " 6 B - - - W - - - B%n"
                + " 5 B B W W K W W B B%n"
                + " 4 B - - - W - - - B%n"
                + " 3 - - - - W - - - -%n"
                + " 2 - - - - B - - - -%n"
                + " 1 - - - B B B - - -%n"
                + "   a b c d e f g h i%n");
        String stdEc = "B---BBB-------B--------W----B---W---"
                + "BBBWWKWWBBB---W---B----W--------B-------BBB---";
        assertEquals(stdBoard, board.toString());
        assertEquals(stdEc, board.encodedBoard());
    }

    @Test
    public void testLegalMove() {
        String stdEc1 = "W---BBB-------B--------W--B-B---W---"
                + "BBBWWKWW-BB---W---B----W--------B-------BBB---";
        String stdEc2 = "B---BBB-------B--------W--B-B---W---"
                + "BBBWWKWW-BB-----W-B----W--------B-------BBB---";

        Board board = new Board();
        assertEquals(BLACK, board.turn());
        board.makeMove(sq("h", "5"), sq("h", "3"));
        assertEquals(WHITE, board.turn());
        assertEquals(stdEc1, board.encodedBoard());

        board.makeMove(sq("e", "6"), sq("g", "6"));
        assertEquals(BLACK, board.turn());
        assertEquals(stdEc2, board.encodedBoard());
    }

    @Test
    public void testInvalidMove() {
        Board board = new Board();
        assertFalse(board.isLegal(mv("f5-4")));
        assertEquals(BLACK, board.turn());
        assertFalse(board.isLegal(mv("a5-8")));
        assertEquals(BLACK, board.turn());
        assertFalse(board.isLegal(mv("f1-8")));
        board.makeMove(mv("f9-7"));
        assertEquals(WHITE, board.turn());
        assertFalse(board.isLegal(mv("d9-7")));
        assertFalse(board.isLegal(mv("e7-f")));
    }

    @Test
    public void testCopy() {
        Board b1 = new Board();
        b1.setMoveLimit(20);
        assertEquals(0, b1.moveCount());
        b1.makeMove(mv("f9-7"));
        assertEquals(1, b1.moveCount());
        b1.makeMove(mv("f5-3"));
        assertEquals(2, b1.moveCount());
        Board b2 = new Board(b1);
        assertEquals(b2, b1);
        b2.makeMove(mv("f7-5"));
        assertEquals(3, b2.moveCount());
    }

    @Test
    public void testSimpleCapture() {
        String std = String.format(" 9 - - - B B B - - -%n"
                + " 8 - - - - B - - - -%n"
                + " 7 - - - - W - - - -%n"
                + " 6 B - - - - W - - B%n"
                + " 5 B B W W K W W B B%n"
                + " 4 B - - - W - - - B%n"
                + " 3 - - - B - B - - -%n"
                + " 2 - - - - B - - - -%n"
                + " 1 - - - - B - - - -%n"
                + "   a b c d e f g h i%n");
        String stdEc = "W---BB--------B--------WB---B---W---"
                + "BBBWWKWWBBB---W---B----W--------B-------BBB---";
        Board board = new Board();
        board.makeMove(mv("f1-3"));
        board.makeMove(mv("e6-f"));
        board.makeMove(mv("d1-3"));
        assertEquals(std, board.toString());
        board.undo();
        board.undo();
        assertEquals(WHITE, board.turn());
        assertEquals(stdEc, board.encodedBoard());
    }

    @Test
    public void testTrickyCapture1() {
        Board board = new Board();
        String[] moves = {
            "a4-3", "e3-i", "i6-7", "f5-7",
            "h5-7", "e5-f", "a3-e"
        };
        for (String s : moves) {
            board.makeMove(mv(s));
            if (!s.equals("a3-e")) {
                assertEquals(WHITE, board.get(sq("e", "4")));
            }
        }
        assertEquals(EMPTY, board.get(sq("e", "4")));
    }

    @Test
    public void testTrickyCapture2() {
        Board board = new Board();
        buildBoard(board, CAPTURE_2);
        board.makeMove(mv("g2-5"));
        assertEquals(EMPTY, board.get(sq("f", "5")));
    }

    @Test
    public void testTrickyCapture3() {
        Board board = new Board();
        buildBoard(board, CAPTURE_3);
        board.makeMove(mv("c6-e"));
        assertEquals(EMPTY, board.get(sq("e", "5")));
    }

    @Test
    public void testTrickyCapture4() {
        Board board = new Board();
        buildBoard(board, CAPTURE_4);
        board.makeMove(mv("c6-d"));
        assertEquals(EMPTY, board.get(sq("d", "5")));
    }

    @Test
    public void testTrickyCapture5() {
        Board board = new Board();
        buildBoard(board, CAPTURE_5);
        board.makeMove(mv("c1-5"));
        assertEquals(EMPTY, board.get(sq("d", "5")));
    }

    @Test
    public void testTrickyCapture6() {
        Board board = new Board();
        buildBoard(board, CAPTURE_6);
        board.makeMove(mv("d2-4"));
        board.makeMove(mv("c2-4"));
        assertEquals(EMPTY, board.get(sq("b", "4")));
        assertEquals(EMPTY, board.get(sq("d", "4")));
        assertEquals(EMPTY, board.get(sq("c", "5")));
    }

    @Test
    public void testTrickyCapture7() {
        Board board = new Board();
        buildBoard(board, CAPTURE_7);
        board.makeMove(mv("c2-4"));
        assertEquals(EMPTY, board.get(sq("b", "4")));
        assertEquals(EMPTY, board.get(sq("d", "4")));
        assertEquals(EMPTY, board.get(sq("c", "5")));
    }

    @Test
    public void testTrickyCapture8() {
        Board board = new Board();
        buildBoard(board, CAPTURE_8);
        board.makeMove(mv("g2-6"));
        assertEquals(EMPTY, board.get(sq("h", "6")));
        assertEquals(BLACK, board.winner());
    }

    @Test
    public void testNonCapture1() {
        Board board = new Board();
        buildBoard(board, NON_CAPTURE_1);
        board.makeMove(mv("a4-e"));
        assertEquals(WHITE, board.get(sq("f", "5")));
    }

    @Test
    public void testNonCapture2() {
        Board board = new Board();
        buildBoard(board, NON_CAPTURE_2);
        board.makeMove(mv("b4-d"));
        assertEquals(KING, board.get(sq("d", "5")));
    }

    @Test
    public void testNonCapture3() {
        Board board = new Board();
        buildBoard(board, NON_CAPTURE_3);
        board.makeMove(mv("c5-b"));
        assertEquals(WHITE, board.get(sq("a", "5")));
    }

    @Test
    public void testNonCapture4() {
        Board board = new Board();
        buildBoard(board, NON_CAPTURE_4);
        board.makeMove(mv("d9-6"));
        assertEquals(KING, board.get(sq("d", "5")));
    }

    @Test
    public void testMoveLimit() {
        Board board = new Board();
        board.setMoveLimit(2);
        board.makeMove(mv("a6-c"));
        assertNull(board.winner());
        board.makeMove(mv("f5-6"));
        assertNull(board.winner());
        board.makeMove(mv("a4-c"));
        assertNull(board.winner());
        board.makeMove(mv("e5-f"));
        assertEquals(WHITE, board.winner());
    }

    @Test
    public void testRepeated() {
        Board board = new Board();
        String[] moves = {
            "f1-4", "d5-3", "f4-1", "d3-7",
            "b5-4", "d7-5", "b4-5", "e3-i",
            "e2-d", "i3-e", "f1-3", "e3-d",
            "d2-f", "d3-4", "f2-1", "e4-3",
            "f3-2", "d4-e", "f2-e"};
        for (String s : moves) {
            board.makeMove(mv(s));
            if (!s.equals("f2-e")) {
                assertNull(board.winner());
            }
        }
        assertEquals(WHITE, board.winner());
    }

}
