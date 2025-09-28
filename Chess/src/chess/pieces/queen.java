package chess.pieces;

import chess.board.Cell;

public class queen extends Piece {

	public queen(boolean isWhite) {
		super(isWhite);
	}

	@Override
	public String toString() {
		return super.toString() + "Q";
	}
	
	@Override
	public boolean isLegalMove(Cell target) {
		if (!(cell.sameCol(target) || cell.sameRow(target) || cell.sameDiag(target))) return false;
		if (!super.isLegalMove(target)) return false;
		
		return true;
	}

	@Override
	boolean isBlocked(Cell target) {
		if (this.cell.getRow() == target.getRow()) return this.blockedRow(target);
		if (this.cell.getCol() == target.getCol()) return this.blockedCol(target);
		return this.blockedDiag(target);
	}

	

}
