package chess.board;
import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.LinkedList;
import chess.pieces.*;

public class Board {
	
	public final Cell[][] board = new Cell[8][8];
	public final king whiteKing, blackKing;
	LinkedList<Piece> WhitePieces = new LinkedList<>();
	LinkedList<Piece> BlackPieces = new LinkedList<>();
	boolean whiteToMove = true;
	public Cell enPassantPossible = null;
	
	public boolean isWhiteToMove() {
		return whiteToMove;
	}
	
	public void otherToMove() {
		whiteToMove = !whiteToMove;
	}
	
	public Board() {
		whiteKing = new king(true);
		blackKing = new king(false);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				board[i][j] = new Cell(this, i, j);
				
				if (i == 0 || i == 7) {
					switch(j) {
					
					case 0: case 7:
						board[i][j].assign(new rook(i == 0));
						break;
					case 1: case 6:
						board[i][j].assign(new knight(i == 0));
						break;
					case 2: case 5:
						board[i][j].assign(new bishop(i == 0));
						break;
					case 3:
						board[i][j].assign(new queen(i == 0));
						break;
					case 4:
						board[i][j].assign(i == 0 ? whiteKing : blackKing);
						break;
					}
				}
				
				if (i == 1 || i == 6) {
					board[i][j].assign(new pawn(i == 1));
				}
				
				
			}
		}
	}
	
	@Override
	public String toString() {
		String res = "  " + "-".repeat(16 + 9 + 16) + "\n";
		for (int i = 7; i >= 0; i--) {
			res +=  (i+1) + " |";
			for (int j = 0; j < 8; j++) {
				res += board[i][j].toString() + "|";
			}
			res += "\n  " + "-".repeat(41) + "\n";
		}
		res += "   ";
		for (char col = 'a'; col <= 'h'; col++) {
			res += "  " + col + "  ";
		}
		res += "\n";
		
		return res;
	}
	
	public LinkedList<Piece> getPiecesList(boolean isWhite) {
		return isWhite ? this.WhitePieces : this.BlackPieces;
	}
	
	public boolean check(boolean whiteChecks) {
		return (whiteChecks ? blackKing.getCell() : whiteKing.getCell()).threatenedCell(whiteChecks);
	}
	
	public boolean noLegalMove(boolean whiteMoves) {
		Iterator<Piece> iter = whiteMoves ? ((LinkedList<Piece>) WhitePieces.clone()).iterator() : ((LinkedList<Piece>) BlackPieces.clone()).iterator();
		
		while (iter.hasNext()) {
			Piece piece = iter.next();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (piece.prepareToMove(board[i][j])) return false;
				}
			}
		}
		
		return true;
		
	}
	
	private void noLegalMoveDebug(boolean whiteMoves) {
		Iterator<Piece> iter = whiteMoves ? ((LinkedList<Piece>) WhitePieces.clone()).iterator() : ((LinkedList<Piece>) BlackPieces.clone()).iterator();
		
		while (iter.hasNext()) {
			Piece piece = iter.next();
			System.out.println(piece.toString() + " in row " + piece.getCell().getRow() + " col " + piece.getCell().getRow());
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (piece.prepareToMove(board[i][j])) {
						System.out.println("can move to row " + i + " col " + j);
						//if (!board[i][j].threatenedCell(piece.isWhite())) board[i][j].threatenedCellDebug(whiteMoves);
						return;
					}
				}
			}
		}
		
		
	}
	
	public boolean checkmate(boolean whiteMates) {
		if (this.check(whiteMates) && this.noLegalMove(!whiteMates)) {
			// this.noLegalMoveDebug(!whiteMates);
			return true;
		}
		return false;
	}
	
	public boolean stalemate() {
		return this.noLegalMove(whiteToMove) && (!this.check(!whiteToMove));
	}
	
	public void Move(Cell from, Cell to) {		
		
		if (from.piece.isEnPassant(to)) board[from.row][to.col].empty();
		enPassantPossible = null;
		if (from.piece.pawnDoubleStep(to)) enPassantPossible = to;
		
		if (from.piece.isCastle(to)) {
			this.castle(to.col > from.col);
			return;
		}
		if (from.piece instanceof king) ((king)from.piece).setMoved(true); 
		if (from.piece instanceof rook) ((rook)from.piece).setMoved(true);
		
		to.assign(from.empty());
		whiteToMove = !whiteToMove;
	}
	
	void castle(boolean kingside) {		//true for kingside, false for queenside
		king K = whiteToMove ? whiteKing : blackKing;
		int row = K.getCell().row;
		int col = K.getCell().col;
		
		Cell king_target_cell = kingside ? board[row][col + 2] : board[row][col - 2];
		Cell rook_original_cell = kingside ? board[row][7] : board[row][0];
		Cell rook_target_cell = kingside ? board[row][col+1] : board[row][col-1];
		
		king_target_cell.assign(K.getCell().empty());
		rook_target_cell.assign(rook_original_cell.empty());
		
		K.setMoved(true);
		((rook)rook_target_cell.piece).setMoved(true);
		
		whiteToMove = !whiteToMove;
	}
	
	public void promote(Cell in, char choice) {	//choices are Q,R,N,B
		if (choice == 'Q') in.assign(new queen(in.piece.isWhite()));
		else if (choice == 'R') { in.assign(new rook(in.piece.isWhite())); ((rook)in.piece).setMoved(true);}
		else if (choice == 'N') in.assign(new knight(in.piece.isWhite()));
		else in.assign(new bishop(in.piece.isWhite()));
	}
	
	public Cell getCell(int row, int col) {
		return board[row][col];
	}
	
}

