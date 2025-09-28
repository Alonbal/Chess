package chess.pieces;

import chess.board.Cell;

public class knight extends Piece {

	public knight(boolean isWhite) {
		super(isWhite);
	}

	@Override
	public String toString() {
		return super.toString() + "N";
	}
	
	@Override
	public boolean isLegalMove(Cell target) {
		if (Math.abs(this.cell.getCol() - target.getCol()) * Math.abs(this.cell.getRow() - target.getRow()) != 2) return false;
		if (!super.isLegalMove(target)) return false;
		
		return true;
	}

	@Override
	boolean isBlocked(Cell target) {
		//knights are never blocked
		return false;
	}

	

}
