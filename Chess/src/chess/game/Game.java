package chess.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import chess.board.*;
import chess.pieces.Piece;
import chess.pieces.pawn;

public class Game {
	
	static final Board board = new Board();

	public static void main(String[] args) {
		
		// run with no input to play, add any input to analyze a game
		if (args.length == 0) {
			
			Scanner in = new Scanner(System.in);
			String strFrom, strTo;
			Cell from, to;
			
			while (true) {
				System.out.println(board);
				
				// if there is no legal move to play, game is over
				if (board.noLegalMove(board.isWhiteToMove())) break;
				// check if player is checked
				if (board.check(!board.isWhiteToMove())) System.out.println("Check!");
				
				printMoveInstruction();
				
				// get a legal move order from player
				while (true) {
					strFrom = in.nextLine();
					strTo = in.nextLine();
					if (getLegalMove(strFrom, strTo)) break;
				}
				
				from = parse(strFrom);
				to = parse(strTo);
				
				board.Move(from, to);
				
				// promotion
				handlePromotion(from, to, in);
			}
			
			in.close();
			
			if (board.check(!board.isWhiteToMove())) {
				System.out.println("Checkmate!");
				if (board.isWhiteToMove()) System.out.println("Black won."); 
				else System.out.println("White won.");
			}
			
			else System.out.println("Stalemate.");
		}
		
		else {
			Scanner in;
			try {
				in = new Scanner(new File("./games.txt"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			in.useDelimiter(" ");
			String str = null;
			
			while (in.hasNext()) {
				
				str = in.next();

				if (isResult(str)) break;
				if (str.contains(".")) str = (str.split("\\.", 2))[1];
				
				if (str.contains("+")) str = str.split("\\+", 2)[0];
				if (str.contains("#")) str = str.split("#", 2)[0];
						
				Cell from = parsePGN(str)[0];
				Cell to = parsePGN(str)[1];
				
				board.Move(from, to);
				
				if (isPromotion(str)) board.promote(to, str.charAt(str.length()-1));
								
			}
			
			printResult(str);
			in.close();
		}
		
	}
	
	public static Cell parse(String turn) {
		if (turn == null) return null;
		if (turn.length() != 2) return null;
		
		int col = turn.charAt(0) - 'a';
		int row = turn.charAt(1) - '1';
		if (row < 0 || row > 7 || col < 0 || col > 7) return null;
		
		return board.board[row][col];
	}

	public static boolean getLegalMove(String strFrom, String strTo) {
		
		Cell from = parse(strFrom);
		Cell to = parse(strTo);
		
		if (from == null || to == null) {
			System.out.println("Wrong input. Try again.");
			return false;
		}
		
		
		if (from.isEmpty()) {
			System.out.println("This cell is empty. Try again.");
			return false;
		}
		
		if (from.getPiece().isWhite() != board.isWhiteToMove()) {
			System.out.println("Wait for your turn.");
			return false;
		}
		
		if (!from.getPiece().prepareToMove(to)) {
			System.out.println("Illegal move. Try again.");
			return false;
		}
		
		return true;
	}
	
	public static Cell[] parsePGN(String turn) {
		
		Cell[] res = new Cell[2];
		res[1] = getTargetCell(turn);
		char piece = turn.charAt(0);
		LinkedList<Piece> iter = board.getPiecesList(board.isWhiteToMove());
				
		if (turn.contains("O-O-O") || turn.contains("0-0-0")) {
			res[0] = board.isWhiteToMove() ? parse("e1") : parse("e8");
			res[1] = board.isWhiteToMove() ? parse("c1") : parse("c8");
			return res;
		}
		
		if (turn.contains("O-O") || turn.contains("0-0")) {
			res[0] = board.isWhiteToMove() ? parse("e1") : parse("e8");
			res[1] = board.isWhiteToMove() ? parse("g1") : parse("g8");
			return res;
		}
		
		if (piece < 'A' || piece > 'Z') piece = 'P';
		
		for (Piece item : iter) {
			if (item.toString().charAt(1) == piece) {
				if (item.isLegalMove(res[1])) {
					if (fromCol(turn) != 0) {
						if (fromCol(turn) != 'a' + item.getCell().getCol()) continue;
					}
					else if (fromRow(turn) != 0) {
						if (fromRow(turn) != '1' + item.getCell().getRow()) continue;
					}
					res[0] = item.getCell();
				}
			}
		}
		
		return res;
	}
	
	private static boolean isPromotion(String s) {
		return s.contains("=");
	}
	
	public static void printResult(String res) {
		System.out.println(board);
		if (res == null) System.out.println("No input.");
		
		else if (res.contains("1-0") || res.contains("1–0")) 
			System.out.println("White won.");
		
		else if (res.contains("0-1") || res.contains("0–1"))
			System.out.println("Black won.");
		
		else if (res.contains("1/2-1/2") || res.contains("1/2–1/2") || res.contains("½–½")) 
			System.out.println("It's a tie.");
		
		else System.out.println("Invalid result.");
	}
	
	public static Cell getTargetCell(String turn) {
			
		char col, row;
		String res = "";
		int end = turn.length() - 1;
		
		if (isPromotion(turn)) {
			col = turn.charAt(end-3);
			row = turn.charAt(end-2);
		} 
		else {
			col = turn.charAt(end-1);
			row = turn.charAt(end);
		}
		
		res = ("" + col) + row;
		
		return parse(res);
	}
	
	public static boolean isResult(String res) {
		if (res == null) return false;
		return res.contains("1-0") || res.contains("1–0") || res.contains("0-1") || 
				res.contains("0–1") || res.contains("1/2-1/2") || res.contains("1/2–1/2") || res.contains("½–½");
	}
	
	public static char containsFromCell(String turn, char start, char end) {
		char res = 0;
		int n = turn.length();
		
		for (int i = 0; i < n; i++) {
			
			char curr = turn.charAt(i);
			if (curr >= start && curr <= end) {
				if (res != 0) return res;
				res = curr;
			}
		}
		
		return 0;
	}
	
	public static char fromCol(String turn) {
		return containsFromCell(turn, 'a', 'h');
	}
	
	public static char fromRow(String turn) {
		return containsFromCell(turn, '1', '8');
	}
	
	private static void printMoveInstruction() {
		if (board.isWhiteToMove()) System.out.println("White's turn: move piece from _ to _");
		else System.out.println("Black's turn: move piece from _ to _");
	}
	
	private static void handlePromotion(Cell from, Cell to, Scanner in) {
		if (to.getPiece() instanceof pawn && (to.getRow() == 0 || to.getRow() == 7)) {		
			System.out.println("Promotion! Enter Q for queen, R for rook, N for knight, B for bishop");
			String choice = in.nextLine();
			while (choice.length() != 1 || (choice.charAt(0) != 'Q' && choice.charAt(0) != 'R' 
					&& choice.charAt(0) != 'N' && choice.charAt(0) != 'B')) {
				System.out.println("Wrong input. Try again.");
				choice = in.nextLine();
			}
			board.promote(to, choice.charAt(0));
		}
	}
	
	public Board getBoard() {
		return board;
	}
}
