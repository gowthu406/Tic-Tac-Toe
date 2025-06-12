import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class TicTacToeGame extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel, scoreLabel;
    private char currentPlayer = 'X';
    private boolean singlePlayer = true;
    private int xWins = 0, oWins = 0;
    private boolean gameOver = false;
    private Random rand = new Random();

    @SuppressWarnings("unused")
    public TicTacToeGame() {
        setTitle("Tic Tac Toe");
        setSize(500, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel board = new JPanel(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 60);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(font);
                buttons[i][j].addActionListener(this);
                board.add(buttons[i][j]);
            }
        }

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        scoreLabel = new JLabel("X: 0   O: 0", SwingConstants.CENTER);
        statusLabel = new JLabel("Current Player: X", SwingConstants.CENTER);
        JButton resetButton = new JButton("Restart Game");
        resetButton.addActionListener(e -> resetGame());

        bottomPanel.add(scoreLabel);
        bottomPanel.add(statusLabel);
        bottomPanel.add(resetButton);

        JMenuBar menuBar = new JMenuBar();
        JMenu modeMenu = new JMenu("Mode");
        JMenuItem single = new JMenuItem("Single Player");
        JMenuItem twoPlayer = new JMenuItem("Two Player");

        single.addActionListener(e -> {
            singlePlayer = true;
            resetGame();
        });
        twoPlayer.addActionListener(e -> {
            singlePlayer = false;
            resetGame();
        });

        modeMenu.add(single);
        modeMenu.add(twoPlayer);
        menuBar.add(modeMenu);
        setJMenuBar(menuBar);

        add(board, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        JButton btn = (JButton) e.getSource();
        if (!btn.getText().equals("")) return;

        btn.setText(String.valueOf(currentPlayer));
        btn.setEnabled(false);
        playSound("click.wav");

        if (checkWin()) {
            statusLabel.setText("Player " + currentPlayer + " wins!");
            updateScore();
            highlightWinningLine();
            gameOver = true;
            return;
        }

        if (isBoardFull()) {
            statusLabel.setText("It's a draw!");
            gameOver = true;
            return;
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        statusLabel.setText("Current Player: " + currentPlayer);

        if (singlePlayer && currentPlayer == 'O') {
            computerMove();
        }
    }

    private void computerMove() {
        Timer timer = new Timer(500, e -> {
            int row, col;
            do {
                row = rand.nextInt(3);
                col = rand.nextInt(3);
            } while (!buttons[row][col].getText().equals(""));

            buttons[row][col].setText("O");
            buttons[row][col].setEnabled(false);
            playSound("click.wav");

            if (checkWin()) {
                statusLabel.setText("Player O wins!");
                updateScore();
                highlightWinningLine();
                gameOver = true;
            } else if (isBoardFull()) {
                statusLabel.setText("It's a draw!");
                gameOver = true;
            } else {
                currentPlayer = 'X';
                statusLabel.setText("Current Player: X");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean checkWin() {
        // Check rows, cols, diagonals
        for (int i = 0; i < 3; i++) {
            if (equal(i, 0, i, 1, i, 2)) return true;
            if (equal(0, i, 1, i, 2, i)) return true;
        }
        return equal(0, 0, 1, 1, 2, 2) || equal(0, 2, 1, 1, 2, 0);
    }

    private boolean equal(int r1, int c1, int r2, int c2, int r3, int c3) {
        String a = buttons[r1][c1].getText();
        String b = buttons[r2][c2].getText();
        String c = buttons[r3][c3].getText();
        return !a.equals("") && a.equals(b) && b.equals(c);
    }

    private boolean isBoardFull() {
        for (JButton[] row : buttons)
            for (JButton b : row)
                if (b.getText().equals("")) return false;
        return true;
    }

    private void updateScore() {
        if (currentPlayer == 'X') xWins++;
        else oWins++;
        scoreLabel.setText("X: " + xWins + "   O: " + oWins);
    }

    private void resetGame() {
        for (JButton[] row : buttons)
            for (JButton b : row) {
                b.setText("");
                b.setBackground(null);
                b.setEnabled(true);
            }
        currentPlayer = 'X';
        gameOver = false;
        statusLabel.setText("Current Player: X");
    }

    private void playSound(String file) {
        try {
            File soundFile = new File(file);
            if (!soundFile.exists()) return;
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception ignored) {}
    }

    private void highlightWinningLine() {
        Color winColor = Color.GREEN;
        for (int i = 0; i < 3; i++) {
            if (equal(i, 0, i, 1, i, 2)) {
                buttons[i][0].setBackground(winColor);
                buttons[i][1].setBackground(winColor);
                buttons[i][2].setBackground(winColor);
                return;
            }
            if (equal(0, i, 1, i, 2, i)) {
                buttons[0][i].setBackground(winColor);
                buttons[1][i].setBackground(winColor);
                buttons[2][i].setBackground(winColor);
                return;
            }
        }
        if (equal(0, 0, 1, 1, 2, 2)) {
            buttons[0][0].setBackground(winColor);
            buttons[1][1].setBackground(winColor);
            buttons[2][2].setBackground(winColor);
        }
        if (equal(0, 2, 1, 1, 2, 0)) {
            buttons[0][2].setBackground(winColor);
            buttons[1][1].setBackground(winColor);
            buttons[2][0].setBackground(winColor);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGame::new);
    }
}