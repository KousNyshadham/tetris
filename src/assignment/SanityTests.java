package assignment;

import static org.junit.Assert.*;
import org.junit.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * A simple set of tests meant for checking that your implementation is sane.
 *
 * These tests are for your utility only; they are not graded as part of the
 * testing sections and you are still responsible for providing your own
 * testing. They are also very basic and do not cover edge cases, all behavior,
 * or any of the details of your implementation.
 *
 * You are welcome to use them as inspiration for your own testing.
 */
public class SanityTests {

    /**
     * A piece string representing the horizontal stick.
     */
    public static final String stickPiece = "0 0  1 0  2 0  3 0";

    /**
     * A overall integration test to check that your piece class "kinda works.",
     * and also enforces some clarifications made via Piazza/discussion section
     * questions.
     *
     * Note that this does NOT constitute "good testing" (though it is one
     * component of good testing) - can you think why?
     */
    @Test
    public void testPiece() {
        // We should be able to getPiece() and get a piece back.
        Piece piece1 = new TetrisPiece(Piece.PieceType.STICK);
        assertNotNull(piece1);

        // We should be able to obtain it's rotation.
        Piece piece2 = piece1.counterclockwisePiece();
        assertNotNull(piece2);

        // A piece is not equal to it's rotation; but it should equal itself.
        assertFalse(piece1 == piece2);
        assertNotEquals(piece1, piece2);
        assertEquals(piece1, piece1);

        // Rotating the piece 4 time should produce the same piece (literally).
        assertTrue(piece1 == piece1.counterclockwisePiece().counterclockwisePiece()
                                .counterclockwisePiece().counterclockwisePiece());

        // Check the proper widths/heights are returned.
        assertEquals(4, piece1.getWidth());
        assertEquals(4, piece1.getHeight());

        // The skirt should be based on the minimum bounding box.
        assertArrayEquals(new int[] { 2, 2, 2, 2 }, piece1.getSkirt());
        assertArrayEquals(new int[] { Integer.MAX_VALUE, 0, Integer.MAX_VALUE, Integer.MAX_VALUE }, piece2.getSkirt());

        // We should get a non-empty, correct array from getBody(); note that the
        // coordinates here are also based on the minimum bounding box.
        assertNotNull(piece1.getBody());

        Set<Point> expected = new HashSet<>();
        expected.add(new Point(0, 2));
        expected.add(new Point(1, 2));
        expected.add(new Point(2, 2));
        expected.add(new Point(3, 2));

        Set<Point> actual = new HashSet<>(Arrays.asList(piece1.getBody()));
        assertEquals(expected, actual);
    }

    /**
     * An overall integration test to check that your board class "kinda works";
     * this runs your board implementation through a single possible set of
     * actions.
     *
     * Note that this does NOT constitute "good testing" (though it is one
     * component of good testing) - can you think why?
     */
    @Test
    public void testBoard() {
        // We should be able to create "non-standard" board sizes.
        Board board = new TetrisBoard(JTetris.WIDTH, 5 * JTetris.HEIGHT);

        // Hopefully the board should equal itself.
        assertEquals(board, board);

        // We should be able to get the width/height appropriately.
        assertEquals(board.getWidth(), JTetris.WIDTH);
        assertEquals(board.getHeight(), 5 * JTetris.HEIGHT);

        // Trying to move results in NO_PIECE initially, and should result in
        // no change of board state.
        assertEquals(Board.Result.NO_PIECE, board.move(Board.Action.DOWN));

        // We should be able to gauge the drop height of a piece on an empty board.
        Piece piece = new TetrisPiece(Piece.PieceType.STICK);
        //assertEquals(0, board.dropHeight(piece, JTetris.WIDTH / 2));

        // We should be able to give the board a piece.
        board.nextPiece(piece, new Point(JTetris.WIDTH/2, JTetris.HEIGHT));

        // We should be able to TEST a move, where we create a new board
        // which is non-null and reflects the results of trying to take the
        // given action.
        Board board2 = board.testMove(Board.Action.LEFT);
        assertNotNull(board2);
        assertNotEquals(board, board2);

        // The new board should have the resulting action, but the old
        // board should not (as it is completely seperate).
        assertEquals(Board.Result.SUCCESS, board2.getLastResult());
        assertEquals(Board.Action.LEFT, board2.getLastAction());
        assertEquals(Board.Action.DOWN, board.getLastAction());

        // We should be able to use the standard movements on the board.
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.DOWN));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.NOTHING));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.DOWN));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.LEFT));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.CLOCKWISE));
        assertEquals(Board.Result.SUCCESS, board.move(Board.Action.COUNTERCLOCKWISE));

        // The last action/last result should change appropriately.
        assertEquals(Board.Result.SUCCESS, board.getLastResult());
        assertEquals(Board.Action.COUNTERCLOCKWISE, board.getLastAction());

        // We should be able to drop the piece and place it; due to variance
        // in implementation, the actual PLACE could occur either after the
        // drop or after a down after the drop.
        Board.Result dropResult = board.move(Board.Action.DROP);
        Board.Result downResult = board.move(Board.Action.DOWN);
        assertTrue(dropResult == Board.Result.PLACE || downResult == Board.Result.PLACE);

        // The last action/last result should change appropriately.
        assertEquals(Board.Action.DOWN, board.getLastAction());
        assertEquals(downResult, board.getLastResult());

        // We shouldn't have a piece anymore again.
        assertEquals(Board.Result.NO_PIECE, board.move(Board.Action.DOWN));

        // The row width of the bottom piece should be 4 exactly.
        assertEquals(4, board.getRowWidth(0));

        // The max row height should now be 1.
        assertEquals(1, board.getMaxHeight());
    }
}
