package gamestates;

import main.Game;
import main.ScoreDatabase;
import ui.MenuButton;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import static gamestates.Gamestate.MENU;

public class Scores extends State implements Statemethods {
    private MenuButton[] buttons = new MenuButton[1];
    private ScoreDatabase scoreDatabase;
    private List<ScoreDatabase.CumulativeScoreEntry> topScores;
    private BufferedImage backgroundImg, scoresBackgroundImg;
    private int bgX, bgY, bgW, bgH;

    public Scores(Game game) {
        super(game);
        loadButtons();
        loadBackground();
        loadScores();
    }

    private void loadButtons() {
        int menuX = (int) (387 * Game.SCALE);
        int menuY = (int) (325 * Game.SCALE);
        buttons[0] = new MenuButton(Game.GAME_WIDTH / 2, (int) (290 * Game.SCALE), 2, MENU);
    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
        bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
        bgX = 0;
        bgY = 0;
    }

    private void loadScores() {
        scoreDatabase = game.getScoreDatabase();
        topScores = scoreDatabase.getCumulativeScores(10);
    }

    @Override
    public void update() {
        for (MenuButton mb : buttons)
            mb.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(scoresBackgroundImg, bgX, bgY, bgW, bgH, null);
        drawScores(g);
        for (MenuButton mb : buttons)
            mb.draw(g);
    }

    private void drawScores(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("TOP SCORES", bgX + bgW / 2 - 70, bgY + 50);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        int yOffset = 80;

        if (topScores.isEmpty()) {
            g.drawString("No scores yet!", bgX + bgW / 2 - 50, bgY + yOffset);
            return;
        }

        for (int i = 0; i < topScores.size(); i++) {
            ScoreDatabase.CumulativeScoreEntry entry = topScores.get(i);
            String scoreText = String.format("%d. %s - %d", i + 1, entry.getPlayerName(), entry.getTotalScore());
            g.drawString(scoreText, bgX + 30, bgY + yOffset);
            yOffset += 30;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton mb : buttons) {
            if (isIn(e, mb)) {
                if (mb.isMousePressed())
                    mb.applyGamestate();
                if (mb.getState() == Gamestate.PLAYING)
                    game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLvlIndex());
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for (MenuButton mb : buttons)
            mb.resetBools();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuButton mb : buttons)
            mb.setMouseOver(false);

        for (MenuButton mb : buttons)
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }
    }

    @Override public void mouseClicked(MouseEvent e) {}

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Gamestate.state = Gamestate.MENU;
    }

    @Override public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Gamestate.state = Gamestate.MENU;
    }
}
