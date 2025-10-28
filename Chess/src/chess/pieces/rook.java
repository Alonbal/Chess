package chess.pieces;

import chess.board.*;

public class rook extends Piece {

	boolean moved;
	
	public rook(boolean isWhite) {
		super(isWhite);
		moved = false;
	}

	@Override
	public String toString() {
		return super.toString() + "R";
	}
	
	@Override
	public boolean isLegalMove(Cell target) {
		if (!(cell.sameCol(target) || cell.sameRow(target))) return false;
		if (!super.isLegalMove(target)) return false;
		
		return true;
	}

	@Override
	boolean isBlocked(Cell target) {
		if (this.cell.getRow() == target.getRow()) 
			return this.blockedRow(target);
		
		else return this.blockedCol(target);
	}

	public boolean getMoved() {
		return moved;
	}
	
	public void setMoved(boolean moved) {
		this.moved = moved;
	}

}
