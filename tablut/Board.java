package tablut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Formatter;

import static tablut.Move.*;
import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Utils.*;


/**
 * The state of a Tablut Game.
 *
 * @author Junyi Cao
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 9;

    /**
     * The throne (or castle) square and its four surrounding squares.
     */
    static final Square THRONE = sq(4, 4),
            NTHRONE = sq(4, 5),
            STHRONE = sq(4, 3),
            WTHRONE = sq(3, 4),
            ETHRONE = sq(5, 4);

    /**
     * Initial positions of attackers.
     */
    static final Square[] INITIAL_ATTACKERS = {
            sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
            sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
            sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
            sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /**
     * Initial positions of defenders of the king.
     */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init(model);
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _state = new HashMap<>();
        _stateList = new ArrayList<>();

        _turn = BLACK;
        _winner = null;
        _moveCount = 0;
        _repeated = false;
        _limitCount = -1;

        put(KING, THRONE);
        for (Square sq : INITIAL_DEFENDERS) {
            put(WHITE, sq);
        }
        for (Square sq : INITIAL_ATTACKERS) {
            put(BLACK, sq);
        }
        for (Square sq : SQUARE_LIST) {
            if (!_state.containsKey(sq)) {
                put(EMPTY, sq);
            }
        }

        _stateList.add(encodedBoard());
    }

    /**
     * Initialize the board as the setting.
     *
     * @param model Another board that should be copied.
     */
    void init(Board model) {
        _state = new HashMap<>();
        _stateList = new ArrayList<>();

        _turn = model.turn();
        _winner = model.winner();
        _moveCount = model.moveCount();
        _repeated = model.repeatedPosition();
        _limitCount = model.moveLimit();

        _state.putAll(model._state);
        _stateList.addAll(model._stateList);
    }

    /**
     * Set the move limit to n.  It is an error if 2*LIM <= moveCount().
     *
     * @param n The move limit.
     */
    void setMoveLimit(int n) {
        if (2 * n <= moveCount()) {
            throw error("Illegal move limit.\n"
                    + "Move limit: %d, moveCount: %d", n, moveCount());
        }
        _limitCount = n;
    }

    /**
     * Return current move limit. If not set, it should be -1.
     */
    int moveLimit() {
        return _limitCount;
    }

    /**
     * Return a Piece representing whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the winner in the current position, or null if there is no winner
     * yet.
     */
    Piece winner() {
        return _winner;
    }

    /**
     * Returns true iff this is a win due to a repeated position.
     */
    boolean repeatedPosition() {
        return _repeated;
    }

    /**
     * Return true iff KING is currently at edge.
     */
    private void checkWhiteWins() {
        if (winner() != null) {
            return;
        }
        if (kingPosition().isEdge()) {
            _winner = WHITE;
        }
    }

    /**
     * Record current position and set winner() me if the current
     * position is a repeat.
     * If winner already exists, do not change it.
     */
    private void checkRepeated() {
        if (winner() != null) {
            _moveCount++;
            _stateList.add(encodedBoard());
            return;
        }
        if (!_stateList.contains(encodedBoard())) {
            _moveCount++;
            _stateList.add(encodedBoard());
        } else {
            _repeated = true;
            _winner = turn();
        }
    }

    /**
     * Set winner() my opponent if I am exceeding moveLimit.
     * If winner already exists, do not change it.
     */
    private void checkMoveExceeds() {
        if (_winner != null || moveLimit() == -1) {
            return;
        }
        if (moveCount() >= 2 * moveLimit()) {
            _winner = turn().opponent();
        }
    }

    /**
     * Set winner() my opponent if I don't have any legal move.
     * If winner already exists, do not change it.
     */
    private void checkNoLegalMove() {
        if (_winner != null) {
            return;
        }
        if (!hasMove(turn())) {
            _winner = turn().opponent();
        }
    }

    /**
     * Return the number of moves since the initial position that have not been
     * undone.
     */
    int moveCount() {
        return _moveCount;
    }

    /**
     * Return location of the king.
     */
    Square kingPosition() {
        int counts = 0;
        Square square = null;
        for (Square sq : pieceLocations(WHITE)) {
            if (get(sq) == KING) {
                counts++;
                if (counts > 1) {
                    throw new RuntimeException(
                            String.format("More than ONE KING found.\n"
                                    + "Current State:\n%s", this)
                    );
                }
                square = sq;
            }
        }
        return square;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return _state.get(sq(col, row));
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        _state.put(s, p);
    }

    /**
     * Set square S to P and record for undoing.
     */
    final void revPut(Piece p, Square s) {
        _state.put(s, p);
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /**
     * Return true iff FROM - TO is an unblocked rook move on the current
     * board.  For this to be true, FROM-TO must be a rook move and the
     * squares along it, other than FROM, must be empty.
     */
    boolean isUnblockedMove(Square from, Square to) {
        if (!from.isRookMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        SqList path = ROOK_SQUARES[from.index()][dir];
        for (Square sq : path) {
            if (get(sq) != EMPTY) {
                return false;
            }
            if (sq == to) {
                break;
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /**
     * Return true iff FROM-TO is a valid move.
     */
    boolean isLegal(Square from, Square to) {
        if (!isLegal(from)) {
            return false;
        }
        if (to == THRONE && get(from) != KING) {
            return false;
        }
        return isUnblockedMove(from, to);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }
        return isLegal(move.from(), move.to());
    }

    /**
     * Return true iff MOVE is an eligible move in the current
     * position, REGARDLESS of the current side.
     */
    boolean isEligible(Move move) {
        if (move == null) {
            return false;
        }
        Square from = move.from();
        Square to = move.to();
        if (to == THRONE && get(from) != KING) {
            return false;
        }
        return isUnblockedMove(from, to);
    }

    /**
     * Move FROM-TO, assuming this is a legal move.
     */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to)
                : String.format("Illegal Move.\nFrom: %s, to: %s",
                from, to);
        Piece chess = get(from);
        put(EMPTY, from);
        put(chess, to);
        Square[] squares = to.triSquares(from.direction(to));
        for (Square s : squares) {
            capture(to, s);
        }
        _turn = turn().opponent();
        checkRepeated();
        checkWhiteWins();
        checkMoveExceeds();
        checkNoLegalMove();
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /**
     * Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     * SQ0 and the necessary conditions are satisfied.
     */
    private void capture(Square sq0, Square sq2) {
        if (sq2 == null) {
            return;
        }
        Square center = sq0.between(sq2);
        if (get(sq0).side() == get(center).side()) {
            return;
        }
        if (get(center) != KING
                || (get(center) == KING && !(isKingInThrone()))) {
            if (get(sq0).side() == get(sq2).side()
                    || (sq2 == THRONE && get(sq2) == EMPTY)) {
                if (get(center) == KING) {
                    _winner = BLACK;
                }
                put(EMPTY, center);
                return;
            }
            if (get(center) == WHITE && get(sq2) == KING
                    && isThroneHostileToWhite()) {
                put(EMPTY, center);
            }
        } else {
            if (kingPosition() == THRONE
                    && get(sq0) == get(sq2)
                    && get(sq0) == get(sq0.diag1(center))
                    && get(sq0) == get(sq0.diag2(center))) {
                put(EMPTY, center);
                _winner = BLACK;
            } else if (kingPosition() != THRONE
                    && (get(sq0) == get(sq2)
                    || sq2 == THRONE)
                    && (get(sq0) == get(sq0.diag1(center))
                    || sq0.diag1(center) == THRONE)
                    && (get(sq0) == get(sq0.diag2(center))
                    || sq0.diag2(center) == THRONE)) {
                put(EMPTY, center);
                _winner = BLACK;
            }
        }
    }

    /**
     * Whether KING is currently in one of the thrones.
     *
     * @return true if KING is currently in one of the thrones.
     */
    boolean isKingInThrone() {
        Square pos = kingPosition();
        return pos == THRONE
                || pos == ETHRONE
                || pos == WTHRONE
                || pos == STHRONE
                || pos == NTHRONE;
    }

    /**
     * Whether the THRONE is hostile to white.
     *
     * @return true if the THRONE is hostile to white.
     */
    private boolean isThroneHostileToWhite() {
        if (kingPosition() != THRONE) {
            return false;
        }
        int counts = 0;
        if (get(NTHRONE) == BLACK) {
            counts++;
        }
        if (get(STHRONE) == BLACK) {
            counts++;
        }
        if (get(WTHRONE) == BLACK) {
            counts++;
        }
        if (get(ETHRONE) == BLACK) {
            counts++;
        }
        return counts == 3;
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (moveCount() > 0) {
            undoPosition();
            _state = decodedString(_stateList.get(_stateList.size() - 1));
            if (winner() != null) {
                _winner = null;
            }
        }
    }

    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     */
    private void undoPosition() {
        if (moveCount() <= 0) {
            return;
        }
        if (repeatedPosition()) {
            _repeated = false;
            return;
        }
        _repeated = false;
        _stateList.remove(_stateList.size() - 1);
        _moveCount--;
    }

    /**
     * Clear the undo stack and board-position counts. Does not modify the
     * current position or win status.
     */
    void clearUndo() {
        _stateList = new ArrayList<>();
        _stateList.add(encodedBoard());
        _moveCount = 0;
    }

    /**
     * Return a new mutable list of all legal moves on the current board for
     * SIDE (ignoring whose turn it is at the moment).
     */
    List<Move> legalMoves(Piece side) {
        List<Move> lists = new MoveList();
        for (Square sq : pieceLocations(side)) {
            for (int i = 0; i < 4; i++) {
                MoveList moves = ROOK_MOVES[sq.index()][i];
                for (Move move : moves) {
                    if (isEligible(move)) {
                        lists.add(move);
                    }
                }
            }
        }
        return lists;
    }

    /**
     * Return a new mutable list of all legal moves on the current board for
     * SQUARE (ignoring whose turn it is at the moment).
     */
    List<Move> legalMoves(Square square) {
        if (get(square) == EMPTY) {
            return null;
        }
        List<Move> lists = new MoveList();
        for (int i = 0; i < 4; i++) {
            MoveList moves = ROOK_MOVES[square.index()][i];
            for (Move move : moves) {
                if (isEligible(move)) {
                    lists.add(move);
                }
            }
        }
        return lists;
    }

    /**
     * Return true iff SIDE has a legal move.
     */
    boolean hasMove(Piece side) {
        return !legalMoves(side).isEmpty();
    }


    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Return a text representation of this Board.  If COORDINATES, then row
     * and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /**
     * Return the locations of all pieces on SIDE.
     */
    HashSet<Square> pieceLocations(Piece side) {
        assert side != null;
        HashSet<Square> squares = new HashSet<>();
        for (Square sq : _state.keySet()) {
            if (get(sq).side() == side.side()) {
                squares.add(sq);
            }
        }
        return squares;
    }

    /**
     * Return the contents of _board in the order of SQUARE_LIST as a sequence
     * of characters: the toString values of the current turn and Pieces.
     */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /**
     * Decode the encodedBoard for reset usage.
     *
     * @param ec Encoded String.
     * @return A state that represents EC.
     */
    private HashMap<Square, Piece> decodedString(String ec) {
        HashMap<Square, Piece> ecState = new HashMap<>();
        if (!ec.matches("[WB][-WBK]{81}")) {
            throw new IllegalArgumentException(
                    String.format("Failed to decode string. "
                            + "\n string: %s", ec));
        }
        if (ec.charAt(0) == 'W') {
            _turn = WHITE;
        } else if (ec.charAt(0) == 'B') {
            _turn = BLACK;
        }
        for (int i = 1; i < ec.length(); i++) {
            switch (ec.charAt(i)) {
            case '-': {
                ecState.put(sq(i - 1), EMPTY);
                break;
            }
            case 'B': {
                ecState.put(sq(i - 1), BLACK);
                break;
            }
            case 'W': {
                ecState.put(sq(i - 1), WHITE);
                break;
            }
            case 'K': {
                ecState.put(sq(i - 1), KING);
                break;
            }
            default: {
                throw new IllegalArgumentException(String.format(
                        "Wrong encoded board string.\n ec: %s", ec
                ));
            }
            }
        }
        return ecState;
    }

    @Override
    public boolean equals(Object T) {
        return T instanceof Board
                && turn() == ((Board) T).turn()
                && winner() == ((Board) T).winner()
                && moveCount() == ((Board) T).moveCount()
                && repeatedPosition() == ((Board) T).repeatedPosition()
                && moveLimit() == ((Board) T).moveLimit()
                && _state.equals(((Board) T)._state)
                && _stateList.equals(((Board) T)._stateList);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Get the state list of the board. For test usage.
     *
     * @return state list.
     */
    ArrayList<String> getLists() {
        return _stateList;
    }

    /**
     * Piece whose turn it is (WHITE or BLACK).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or null if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * Number of (still undone) moves since initial position.
     */
    private int _moveCount;
    /**
     * True when current board is a repeated position (ending the game).
     */
    private boolean _repeated;
    /**
     * Limit move count.
     */
    private int _limitCount;
    /**
     * The current state of the board, i.e., record the content of each square.
     */
    private HashMap<Square, Piece> _state;
    /**
     * Record of the states in previous.
     */
    private ArrayList<String> _stateList;
}
