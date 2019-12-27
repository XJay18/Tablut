package tablut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static tablut.Board.THRONE;
import static tablut.Move.*;
import static tablut.Piece.*;
import static tablut.Square.sq;
import static tablut.Utils.*;

/**
 * A Player that
 * automatically generates moves.
 *
 * @author Junyi Cao
 */
class AI extends Player {

    /**
     * A position-score magnitude indicating a win (for white if positive,
     * black if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        System.out.println("* " + findMove());
        return findMove().toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        int sense = myPiece() == WHITE ? 1 : -1;
        findMove(b, maxDepth(b), true, sense, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (board.winner() != null) {
            return board.winner() == WHITE ? WINNING_VALUE : -WINNING_VALUE;
        } else if (depth == 0) {
            return staticScore(board);
        } else {
            if (sense == 1) {
                List<Move> moves = board.legalMoves(WHITE);
                for (Move mv : moves) {
                    board.makeMove(mv);
                    int a = Math.max(alpha, findMove(
                            board, depth - 1, false, -1, alpha, beta));
                    if (a > alpha) {
                        alpha = a;
                        if (saveMove) {
                            _lastFoundMove = mv;
                        }
                    }
                    if (alpha >= beta) {
                        board.undo();
                        return beta;
                    }
                    board.undo();
                }
                return alpha;
            } else if (sense == -1) {
                List<Move> moves = board.legalMoves(BLACK);
                for (Move mv : moves) {
                    board.makeMove(mv);
                    int b = Math.min(beta, findMove(
                            board, depth - 1, false, 1, alpha, beta));
                    if (b < beta) {
                        beta = b;
                        if (saveMove) {
                            _lastFoundMove = mv;
                        }
                    }
                    if (alpha >= beta) {
                        board.undo();
                        return alpha;
                    }
                    board.undo();
                }
                return beta;
            } else {
                throw error("Sense: %d which isn't 1 or -1.", sense);
            }
        }
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private static int maxDepth(Board board) {
        if (whiteOneMoveWin(board) || blackOneMoveWin(board)) {
            return 1;
        } else {
            return 3;
        }
    }

    /**
     * Check whether white can win in one move.
     *
     * @param board The current board.
     * @return whether white can win in one move.
     */
    static boolean whiteOneMoveWin(Board board) {
        Square king = board.kingPosition();

        int row = king.row();
        int col = king.col();
        Square[] edges = new Square[]{
                sq(8, row), sq(col, 0),
                sq(0, row), sq(col, 8)};
        for (Square sq : edges) {
            if (mv(king, sq) != null
                    && board.isLegal(mv(king, sq))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether black can win in one move.
     *
     * @param board The current board.
     * @return whether black can win in one move.
     */
    static boolean blackOneMoveWin(Board board) {
        Square king = board.kingPosition();

        HashSet<Square> blackSq = board.pieceLocations(BLACK);
        ArrayList<Square> absents = kingAdjHostile(board, king, true);
        ArrayList<Square> presents = kingAdjHostile(board, king, false);
        if (!board.isKingInThrone()) {
            for (Square presentBlack : presents) {
                int dir = presentBlack.direction(king);
                for (Square black : blackSq) {
                    Move move = mv(black, king.rookMove(dir, 1));
                    if (move != null
                            && board.isLegal(move)) {
                        return true;
                    }
                }
            }
        } else if (king == THRONE) {
            if (presents.size() < 3) {
                return false;
            } else if (presents.size() == 3) {
                for (Square black : blackSq) {
                    Move move = mv(black, absents.get(0));
                    if (move != null
                            && board.isLegal(move)) {
                        return true;
                    }
                }
            }
        } else {
            if (presents.size() < 2) {
                return false;
            } else if (presents.size() == 2) {
                Square absent;
                if (absents.get(0) == THRONE) {
                    absent = absents.get(1);
                } else if (absents.get(1) == THRONE) {
                    absent = absents.get(0);
                } else {
                    throw error("Can't reach here!");
                }
                for (Square black : blackSq) {
                    Move move = mv(black, absent);
                    if (move != null
                            && board.isLegal(move)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private static int staticScore(Board board) {
        int score = 0;
        if (board.kingPosition().isEdge()) {
            score = WINNING_VALUE;
        } else if (board.kingPosition() == null) {
            score = -WINNING_VALUE;
        } else {
            score += kingPosValue(board);
            int whites = board.pieceLocations(WHITE).size();
            int blacks = board.pieceLocations(BLACK).size();
            score += (whites + 7 - blacks);
        }
        return score;
    }

    /**
     * Whether KING can move to EDGE in two continuous moves.
     * Preconditions: KING cannot move to EDGE in one step.
     *
     * @param board The current board.
     * @return How many ways can king move to edge in two moves.
     */
    private static int kingTwoMoveAtEdge(Board board) {
        int counts = 0;
        Square king = board.kingPosition();
        List<Move> firstMoves = board.legalMoves(king);
        for (Move mv : firstMoves) {
            board.put(EMPTY, mv.from());
            board.put(KING, mv.to());
            int row = mv.to().row();
            int col = mv.to().col();
            Square[] edges = new Square[]{
                    sq(8, row), sq(col, 0),
                    sq(0, row), sq(col, 8)};
            for (Square sq : edges) {
                if (sq != null && board.isEligible(mv(mv.to(), sq))) {
                    counts++;
                }
            }
            board.put(KING, king);
            board.put(EMPTY, mv.to());
        }
        return counts;
    }

    /**
     * Return a value of current King Position Value.
     *
     * @param board The current board.
     */
    static int kingPosValue(Board board) {
        return addKingPosValue(board) + minusKingPosValue(board);
    }

    /**
     * Return a positive value of current King Position Value.
     *
     * @param board The current board.
     */
    private static int addKingPosValue(Board board) {
        int value = 0;
        Square king = board.kingPosition();

        int row = king.row();
        int col = king.col();
        Square[] edges = new Square[]{
                sq(8, row), sq(col, 0),
                sq(0, row), sq(col, 8)};
        int edgeCounts = 0;
        for (Square sq : edges) {
            if (mv(king, sq) != null
                    && board.isEligible(mv(king, sq))) {
                edgeCounts++;
            }
        }
        if (edgeCounts >= 2) {
            value = WILL_WIN_VALUE;
        } else if (edgeCounts == 1 && board.turn() == WHITE) {
            value = WILL_WIN_VALUE;
        } else {
            value = edgeCounts * 5;
        }

        if (value == 0) {
            value = kingTwoMoveAtEdge(board) > 0 ? 3 : 0;
            value = board.turn() == WHITE ? value * 2 : value;
        }
        return value;
    }

    /**
     * Return a negative value of current King Position Value.
     *
     * @param board The current board.
     */
    private static int minusKingPosValue(Board board) {
        int value = 0;
        Square king = board.kingPosition();

        HashSet<Square> blackSq = board.pieceLocations(BLACK);
        ArrayList<Square> absents = kingAdjHostile(board, king, true);
        int blackCounts = 4 - absents.size();

        if (blackCounts == 3) {
            value = minusKPVHelper1(board, king, blackSq);
        } else if (blackCounts == 2) {
            value = minusKPVHelper2(board, king, blackSq);
        } else if (blackCounts == 1) {
            value = minusKPVHelper3(board, king, blackSq);
        }
        return value;
    }

    /**
     * Return a negative value of current King Position Value.
     * Helper sub function for blackCounts equals 3.
     *
     * @param board   The current board.
     * @param king    The king position at current board.
     * @param blackSq All black pieces' square at current board.
     */
    private static int minusKPVHelper1(Board board, Square king,
                                       HashSet<Square> blackSq) {
        int value = 0;
        ArrayList<Square> absents = kingAdjHostile(board, king, true);
        for (Square sq : blackSq) {
            if (mv(sq, absents.get(0)) != null
                    && board.isLegal(mv(sq, absents.get(0)))) {
                value = -WILL_WIN_VALUE;
                break;
            }
        }
        value = value == 0 ? -10 : value;
        return value;
    }

    /**
     * Return a negative value of current King Position Value.
     * Helper sub function for blackCounts equals 2.
     *
     * @param board   The current board.
     * @param king    The king position at current board.
     * @param blackSq All black pieces' square at current board.
     */
    private static int minusKPVHelper2(Board board, Square king,
                                       HashSet<Square> blackSq) {
        int value = 0;
        ArrayList<Square> absents = kingAdjHostile(board, king, true);
        ArrayList<Square> presents = kingAdjHostile(board, king, false);
        if (king == THRONE) {
            value = -5;
        } else if (board.isKingInThrone()) {
            Square valid = absents.get(0)
                    == THRONE ? absents.get(1) : absents.get(0);
            for (Square sq : blackSq) {
                if (mv(sq, valid) != null
                        && board.isLegal(mv(sq, valid))) {
                    value = -WILL_WIN_VALUE;
                    break;
                }
            }
            value = value == 0 ? -10 : value;
        } else {
            int dir1 = presents.get(0).direction(king);
            int dir2 = presents.get(1).direction(king);
            for (Square sq : blackSq) {
                Move move1 = mv(sq, king.rookMove(dir1, 1));
                Move move2 = mv(sq, king.rookMove(dir2, 1));
                if (move1 != null
                        && board.isLegal(move1)) {
                    value = -WILL_WIN_VALUE;
                    break;
                }
                if (move2 != null
                        && board.isLegal(move2)) {
                    value = -WILL_WIN_VALUE;
                    break;
                }
            }
        }
        return value;
    }

    /**
     * Return a negative value of current King Position Value.
     * Helper sub function for blackCounts equals 1.
     *
     * @param board   The current board.
     * @param king    The king position at current board.
     * @param blackSq All black pieces' square at current board.
     */
    private static int minusKPVHelper3(Board board, Square king,
                                       HashSet<Square> blackSq) {
        int value = 0;
        ArrayList<Square> presents = kingAdjHostile(board, king, false);
        if (board.turn() == BLACK) {
            int direction = presents.get(0).direction(king);
            for (Square sq : blackSq) {
                Move move = mv(sq, king.rookMove(direction, 1));
                if (move != null
                        && board.isLegal(move)) {
                    value = -WILL_WIN_VALUE;
                    break;
                }
            }
        }
        if (value > -WILL_WIN_VALUE) {
            value -= 5;
        }
        return value;
    }


    /**
     * Return a lists consists of the EMPTY adjacent squares of KING.
     *
     * @param board  The current board.
     * @param king   The king position at current board.
     * @param absent Whether return absents or presents.
     */
    static ArrayList<Square> kingAdjHostile(Board board, Square king,
                                            boolean absent) {
        ArrayList<Square> dirs = new ArrayList<>();
        Square[] adjs = new Square[]{
                king.rookMove(0, 1),
                king.rookMove(1, 1),
                king.rookMove(2, 1),
                king.rookMove(3, 1)
        };
        for (Square sq : adjs) {
            if (absent) {
                if (sq != null && board.get(sq) != BLACK) {
                    dirs.add(sq);
                }
            } else {
                if (sq != null && board.get(sq) == BLACK) {
                    dirs.add(sq);
                }
            }
        }
        return dirs;
    }

}
