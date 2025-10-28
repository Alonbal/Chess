package chess.pieces;
import chess.board.Cell;

public abstract class Piece {
	
	final boolean isWhite;
	Cell cell;
	
	public Piece(boolean isWhite) {
		
		this.isWhite = isWhite;
		this.cell = null;
	}
	
	@Override
	public String toString() {
		return (isWhite ? "W" : "B");
	}
	
	public boolean isWhite() {
		return isWhite;
	}
	
	public Cell getCell() {
		return cell;
	}
	
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	
	public boolean isLegalMove(Cell target) {
		if (target.getCol() == this.cell.getCol() && target.getRow() == this.cell.getRow()) return false;
		if ((!target.isEmpty()) && target.getPiece().isWhite == this.isWhite) return false;
		if (isBlocked(target)) return false;
		
		return true;
	}
	
	public boolean prepareToMove(Cell target) {
		return (this.isWhite == this.cell.board.isWhiteToMove()) && this.isLegalMove(target) && (!this.moveCausesCheck(target));
	}
	
	abstract boolean isBlocked(Cell target);
	
	boolean blockedRow(Cell target) {
		for (int i = 1 + Math.min(this.cell.getCol(), target.getCol()); i < Math.max(this.cell.getCol(), target.getCol()); i++) {
			if (!target.board.board[cell.getRow()][i].isEmpty()) return true;
		}
		return false;
	}
	
	boolean blockedCol(Cell target) {
		for (int i = 1 + Math.min(this.cell.getRow(), target.getRow()); i < Math.max(this.cell.getRow(), target.getRow()); i++) {
			if (!target.board.board[i][cell.getCol()].isEmpty()) return true;
		}
		return false;
	}
	
	boolean blockedDiag(Cell target) {
		
		int rowDirection = (this.cell.getRow() < target.getRow() ? 1 : -1);
		int colDirection = (this.cell.getCol() < target.getCol() ? 1 : -1);
		
		for (int i = 1; i < Math.abs(this.cell.getRow() - target.getRow()); i++) 
			if (!target.board.board[this.cell.getRow() + (i * rowDirection)][this.cell.getCol() + (i * colDirection)].isEmpty()) return true;
		
		return false;
	}
	
	boolean isChecking() {
		king oppKing = isWhite ? cell.board.blackKing : cell.board.whiteKing;
		return this.isLegalMove(oppKing.cell);
	}
	
	public boolean isEnPassant(Cell target) {
		return false;
	}

	public boolean pawnDoubleStep(Cell target) {
		return false;
	}
	
	public boolean moveCausesCheck(Cell target) {
		if (!this.isLegalMove(target)) return true;
		if (this instanceof king) return target.threatenedCell(!isWhite);		//castling cannot cause checked position
		boolean moved = false;
		
		//backups for undoing move
		Cell original_cell = cell;
		Piece captured = target.getPiece();
		Cell en_passant_cell = cell.board.enPassantPossible;
		Piece en_passant_backup = this.isEnPassant(target) ? target.board.enPassantPossible.getPiece() : null;
		if (this instanceof rook) moved = ((rook)this).getMoved();
		
		cell.board.Move(cell, target);
		boolean res = cell.board.check(!isWhite) ? true : false;
		cell.board.Move(target, original_cell);
		
		target.assign(captured);
		if (en_passant_cell != null) cell.board.enPassantPossible = en_passant_cell;
		if (en_passant_backup != null) en_passant_cell.assign(en_passant_backup);
		if (this instanceof rook) ((rook)this).setMoved(moved);
		/*
		if (is_castle) {		//undoing castle, already unmoved king - rook remaining
			
			Cell rooks_cell_now = (target.col > cell.col) ? cell.board.board[cell.row][cell.col + 1] : cell.board.board[cell.row][cell.col - 1];
			Cell rooks_original_cell = (target.col > cell.col) ? cell.board.board[cell.row][7] : cell.board.board[cell.row][0];
				
			cell.board.Move(rooks_cell_now, rooks_original_cell);
			((king)this).moved = false;
			((rook)rooks_original_cell.piece).moved = false;
		}
		*/
		return res;
	}

	public boolean isCastle(Cell target) {
		return false;
	}
}
