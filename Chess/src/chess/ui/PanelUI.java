package chess.ui;

import chess.board.Board;
import chess.board.Cell;
import chess.game.*;
import chess.pieces.Piece;
import chess.pieces.pawn;
import chess.pieces.rook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelUI extends JPanel {
    private static final int TILE_SIZE = 70;
    private final Game game;
    private int selectedRow = -1, selectedCol = -1;

    public PanelUI(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(8 * TILE_SIZE, 8 * TILE_SIZE));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	Board board = game.getBoard();
                int row = flipRow(e.getY() / TILE_SIZE);
                int col = e.getX() / TILE_SIZE;
                
                if (selectedRow == -1) {
                	
                    // first click → select piece
                    if (!(board.getCell(row, col).isEmpty())) {
                        selectedRow = row;
                        selectedCol = col;
                    }
                } else {
                    // second click → attempt move
                	Cell from = board.getCell(selectedRow, selectedCol);
                	Cell to = board.getCell(row, col);
                	
                	selectedRow = -1;
                    selectedCol = -1;
                    
                    if (from != to) {
                    	boolean moved = game.getLegalMove(from, to);
                        
                        if (moved) {
                        	board.Move(from, to);
                        	promotionUI(to);
                        }
                    }
            		
                }
                repaint();
                if (board.checkmate(!board.isWhiteToMove())) showGameOverDialog((!board.isWhiteToMove() ? "White" : "Black") + " wins " + (!board.isWhiteToMove() ? "1:0" : "0:1"));
                //else if (board.checkmate(board.isWhiteToMove())) showGameOverDialog((board.isWhiteToMove() ? "White" : "Black") + " wins " + (board.isWhiteToMove() ? "1:0" : "0:1"));
                else if (board.stalemate()) showGameOverDialog("Stalement! 1/2 : 1/2");
            }

			
        });
    }

    private void promotionUI(Cell to) {
		if (to.getPiece() instanceof pawn) {
			if (to.getRow() == 0 || to.getRow() == 7) {
				game.getBoard().promote(to, askPromotion());
			}
		}
	}
    
    public char askPromotion() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        
        String choice = (String) JOptionPane.showInputDialog(
            this, 
            "Choose a piece for promotion:", 
            "Pawn Promotion", 
            JOptionPane.PLAIN_MESSAGE, 
            null, 
            options, 
            options[0] // default: Queen
        );
        
        if (choice == null) {
            choice = "Queen"; // default if player closes dialog
        }
        return choice.charAt(0);
    }
    
    public void showGameOverDialog(String message) {
        int choice = JOptionPane.showOptionDialog(
            this,
            message + "\nWould you like to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Restart", "Exit"},
            "Restart"
        );

        if (choice == JOptionPane.YES_OPTION) {
            Game.restartGame();
        } else {
            System.exit(0);
        }
    }
    

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Board board = game.getBoard();
        
        // draw board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean light = (row + col) % 2 == 1;
                g.setColor(light ? new Color(240, 217, 181) : new Color(181, 136, 99));
                g.fillRect(col * TILE_SIZE, flipRow(row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                
                // highlight selection
                if (row == selectedRow && col == selectedCol) {
                    g.setColor(new Color(0, 255, 0, 100));
                    g.fillRect(col * TILE_SIZE, flipRow(row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                // highlight checked king
                if (board.check(!board.isWhiteToMove())) {
                	Cell c = board.isWhiteToMove() ? board.whiteKing.getCell() : board.blackKing.getCell();
                	if (c.getCol() == col && c.getRow() == row) {
                		g.setColor(new Color(255, 0, 0, 190));
                		g.fillRect(col * TILE_SIZE, flipRow(row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                	}
                }

                // draw piece if exists
                Piece piece = board.getCell(row, col).getPiece();
                if (piece != null) {
                    Image img = Images.getImage(piece.toString());
                    g.drawImage(img, col * TILE_SIZE, flipRow(row) * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }
    }
	
	private int flipRow(int row) {
		return 7 - row;
	}
}