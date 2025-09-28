package chess.board;
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
		/*
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((!board[i][j].isEmpty) && (board[i][j].piece.isWhite == whiteMoves)) {
					Piece piece = board[i][j].piece;
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {
							if (piece.prepareToMove(board[k][l])) return false;
						}
					}
				}
			}
		}
		
		return true;
		*/
		
		LinkedList<Piece> iter = whiteMoves ? WhitePieces : BlackPieces;
		int n = iter.size();
		
		for (int ind = 0; ind < n; ind++) {
			Piece piece = iter.getFirst();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (piece.prepareToMove(board[i][j])) return false;
					if (iter.getFirst() == piece) iter.add(iter.removeFirst());
				}
			}
		}
		
		return true;
		
	}
	
	boolean checkmate(boolean whiteMates) {
		return this.check(whiteMates) && this.noLegalMove(!whiteMates);
	}
	
	boolean stalemate() {
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
		if (from.piece instanceof king) ((king)from.piece).setMoved(); 
		if (from.piece instanceof rook) ((rook)from.piece).setMoved();
		
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
		
		K.setMoved();
		((rook)rook_target_cell.piece).setMoved();
		
		whiteToMove = !whiteToMove;
	}
	
	public void promote(Cell in, char choice) {	//choices are Q,R,N,B
		if (choice == 'Q') in.assign(new queen(in.piece.isWhite()));
		else if (choice == 'R') { in.assign(new rook(in.piece.isWhite())); ((rook)in.piece).setMoved();}
		else if (choice == 'N') in.assign(new knight(in.piece.isWhite()));
		else in.assign(new bishop(in.piece.isWhite()));
	}
	
	public Cell getCell(int row, int col) {
		return board[row][col];
	}
	
}
