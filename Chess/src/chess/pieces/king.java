package chess.pieces;
//import java.util.Iterator;

import chess.board.*;

public class king extends Piece {
	
	boolean moved;

	public king(boolean isWhite) {
		super(isWhite);
		moved = false;
	}
	
	@Override
	public String toString() {
		return super.toString() + "K";
	}

	@Override
	public boolean isLegalMove(Cell target) {
		if (cell.getRow() == target.getRow() && Math.abs(cell.getCol() - target.getCol()) == 2) return this.isCastle(target);
		if (!(Math.abs(this.cell.getCol() - target.getCol()) <= 1 && Math.abs(this.cell.getRow() - target.getRow()) <= 1)) return false;
		if (!super.isLegalMove(target)) return false;
		if (target.threatenedCell(!isWhite)) return false;
		return true;
		
		
	}
	
	@Override
	public boolean isCastle(Cell target) {
		
		if (this.moved) return false;
		
		if (target.getRow() != this.cell.getRow()) return false;

		Cell rooks_cell, rooks_target_cell;
		if (this.cell.getCol() - target.getCol() == 2) {		//castle to the left AKA queenside AKA long castling
			rooks_cell = cell.board.board[cell.getRow()][0];
			rooks_target_cell = cell.board.board[cell.getRow()][cell.getCol() - 1];
		}
		else if (target.getCol() - this.cell.getCol() == 2) {		//castle to the right AKA kingside AKA short castling
			rooks_cell = cell.board.board[cell.getRow()][7];
			rooks_target_cell = cell.board.board[cell.getRow()][cell.getCol() + 1];
		}
		else return false;

		if (rooks_cell.isEmpty()) return false;

		if (!(rooks_cell.getPiece() instanceof rook)) return false;

		rook R = (rook)rooks_cell.getPiece();
		if (R.moved) return false;

		if (R.isBlocked(cell)) return false;
	
		if (cell.threatenedCell(!isWhite) || target.threatenedCell(!isWhite) || 
				rooks_target_cell.threatenedCell(!isWhite)) return false;

		return true;
	}

	@Override
	boolean isBlocked(Cell target) {
		return false;
	}
	
	public boolean getMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

}
