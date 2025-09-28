package chess.pieces;

import chess.board.*;

public class pawn extends Piece {
	
	
	public pawn(boolean isWhite) {
		super(isWhite);
		
	}

	@Override
	public String toString() {
		return super.toString() + "P";
	}
	
	@Override
	public boolean isLegalMove(Cell target) {
		if (!super.isLegalMove(target)) return false;
		
		if (this.isEnPassant(target)) return true;		//google en passant
		
		if (this.isSingleStep(target)) return true;
		
		if (this.regularCapture(target) && !target.isEmpty()) return true;
		
		if (this.pawnDoubleStep(target)) return true;		
		
		
		return false;
	}
	
	@Override
	public boolean isEnPassant(Cell target) {
		if (target.board.enPassantPossible == null) return false;
		Cell en_passant_cell = target.board.enPassantPossible;
		if (target.getCol() != en_passant_cell.getCol()) return false; 
		if (!((isWhite && target.getRow() == cell.getRow() + 1) || (((!isWhite) && target.getRow() == cell.getRow() - 1)))) return false;
		return (en_passant_cell.getRow() == this.cell.getRow() && ((this.isWhite && this.cell.getRow() == 4) || 
				((!this.isWhite) && this.cell.getRow() == 3)) &&
					Math.abs(this.cell.getCol() - en_passant_cell.getCol()) == 1);
		
	}
	
	public boolean regularCapture(Cell target) {
		int sign = this.isWhite ? 1 : -1;
		if (this.cell.getRow() == target.getRow() - sign) {	//next row
			if (Math.abs(this.cell.getCol() - target.getCol()) == 1) {		//taking a piece		
				return true;
			}
		}
		return false;
	}
	
	boolean isSingleStep(Cell target) {
		int sign = this.isWhite ? 1 : -1;
		if (cell.getRow() == target.getRow() - sign) {
			if (cell.getCol() == target.getCol())
				return target.isEmpty();
		}
		return false;
	}
	
	@Override
	public boolean pawnDoubleStep(Cell target) {
		if ((this.cell.getRow() == 1 && this.isWhite) || ((!this.isWhite) && this.cell.getRow() == 6))
			if (Math.abs(this.cell.getRow() - target.getRow()) == 2 && this.cell.getCol() == target.getCol()) return target.isEmpty();
		return false;
	}
	

	@Override
	boolean isBlocked(Cell target) {
		if (this.cell.getCol() == target.getCol()) return this.blockedCol(target);
		return false;
	}

}
