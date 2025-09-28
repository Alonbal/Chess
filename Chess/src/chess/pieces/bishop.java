package chess.pieces;
import chess.board.*;

public class bishop extends Piece {

	public bishop(boolean isWhite) {
		super(isWhite);
	}

	@Override
	public String toString() {
		return super.toString() + "B";
	}
	
	@Override
	public boolean isLegalMove(Cell target) {
		if (!cell.sameDiag(target)) return false;
		if (!super.isLegalMove(target)) return false;
		
		return true;
	}

	@Override
	boolean isBlocked(Cell target) {
		return this.blockedDiag(target);
	}

	

}
