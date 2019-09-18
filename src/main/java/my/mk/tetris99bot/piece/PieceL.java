package my.mk.tetris99bot.piece;


public class PieceL extends Piece {



    @Override public byte number() { return 5; }
    @Override public char character() { return 'L'; }
    @Override protected int[] widths() { return new int[] {3, 2, 3, 2  }; }
    @Override protected int[] heights() { return new int[] {  2, 3, 2, 3}; }
    @Override protected PieceShape[] shapes() {
        return new PieceShape[] {

                new PieceShape(3,new Point(1,0), new Point(1,1), new Point(0,2), new Point(1,2)),
                new PieceShape(3,new Point(0,0), new Point(0,1), new Point(1,1), new Point(2,1)),
                new PieceShape(3,new Point(0,0), new Point(1,0), new Point(0,1), new Point(0,2)),
                new PieceShape(4,new Point(0,0), new Point(1,0), new Point(2,0), new Point(2,1))
        };
    }
}
