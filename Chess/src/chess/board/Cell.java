package chess.board;
import java.util.AbstractSequentialList;
import java.util.Iterator;
import chess.pieces.*;

public class Cell {
	public final Board board;
	final int row, col;
	Piece piece;
	boolean isEmpty;
	
	public Cell(Board board, int row, int col) {
		this.board = board;
		this.row = row;
		this.col = col;
		this.isEmpty = true;
	}
	
	@Override
	public String toString() {
		return (this.isEmpty ? "    " : " " + piece.toString() + " ");
	}

	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	Piece empty() {
		Piece deleted = this.piece;
		if (!isEmpty) {
			if (this.piece.isWhite()) board.WhitePieces.remove(piece);
			else board.BlackPieces.remove(piece);
			this.piece.setCell(null);			
			
			isEmpty = true;
			piece = null;
		}
		return deleted;
	}
	
	public void assign(Piece piece) {
		if (piece == null) {
			this.isEmpty = true;
			this.piece = null;
			return;
		}
		this.empty();
		if (piece.isWhite()) board.WhitePieces.add(piece);
		else board.BlackPieces.add(piece);
		this.piece = piece;
		piece.setCell(this);
		isEmpty = false;
	}
	
	public boolean sameDiag(Cell target) {
		return (Math.abs(row - target.row) == Math.abs(col - target.col));
	}
	
	public boolean sameCol(Cell target) {
		return col == target.col;
	}
	
	public boolean sameRow(Cell target) {
		return row == target.row;
	}
	
	public boolean threatenedCell(boolean whiteMoves) {
		Piece backup = piece;
		this.empty();
		
		Iterator<Piece> iter = whiteMoves ? ((AbstractSequentialList<Piece>) board.WhitePieces.clone()).iterator() : ((AbstractSequentialList<Piece>) board.BlackPieces.clone()).iterator();
		while (iter.hasNext()) {
			Piece piece = iter.next();
			Cell cell = piece.getCell();
			
			if (piece instanceof king) {
				if (Math.abs(cell.getCol() - col) <= 1 && Math.abs(cell.getRow() - row) <= 1) {
					this.assign(backup);
					return true;
				}
			}
			else if (piece instanceof pawn) {
				if (((pawn)piece).regularCapture(this)) {
					this.assign(backup);
					return true;
				}
			}
			else if (piece.isLegalMove(this)) {
				this.assign(backup);
				return true;
			}
		}
		this.assign(backup);
		return false;
	}
	
}
