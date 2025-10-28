package chess.ui;

import chess.board.Board;
import chess.game.Game; // <-- your engine
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessUI extends JFrame {
    private final PanelUI boardPanel;
    private final Game game;

    public ChessUI(Game game) {
        this.game = game;
        this.boardPanel = new PanelUI(game);

        setTitle("Chess Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);

        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
}