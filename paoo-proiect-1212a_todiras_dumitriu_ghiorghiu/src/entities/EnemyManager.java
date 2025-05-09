package entities;

import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] skellyArr;
    private ArrayList<Skelly> skellies = new ArrayList<>();
    private BufferedImage[][] skellyArr_2;
    private ArrayList<Skelly2> skellies_2 = new ArrayList<>();
    private BufferedImage[][] golemArr;
    private ArrayList<Golem> golem = new ArrayList<>();
    private BufferedImage[][] duckyArr;
    private ArrayList<Npc> duckies = new ArrayList<>();
    private BufferedImage[][] bossArr;
    private ArrayList<Boss> bossies = new ArrayList<>();

    BufferedImage wizard = LoadSave.GetSpriteAtlas(LoadSave.WIZARD_DUCK);

    private int flick=0;

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
    }

    public void loadEnemies(Level level) {
        skellies = level.getSkellies();
        skellies_2 = level.getSkellies_2();
        golem=level.getGolem();
        duckies = level.getDuckies();
        bossies = level.getBossies();
    }

    public void update(int[][] lvlData, Player player){
        boolean isAnyActive = false;
        for(Skelly s : skellies) {
            if (s.isActive()) {
                s.update(lvlData, player);
                isAnyActive = true;
            }
        }
        for(Skelly2 s : skellies_2) {
            if (s.isActive()) {
                s.update(lvlData, player);
                isAnyActive = true;
            }
        }

        for(Golem o : golem) {
            if (o.isActive()) {
                o.update(lvlData, player);
                isAnyActive = true;
            }
        }

        for(Boss b : bossies){
            if (b.isActive()) {
                b.update(lvlData, playing);
                isAnyActive = true;
            }
        }

        if(duckies!=null)
            for(Npc d : duckies){
                d.update(lvlData,player);
            }

        if(!isAnyActive){
//            playing.setLevelCompleted(true);
        }
    }

    public void draw(Graphics g, int xLvlOffset){
        drawSkellys(g,xLvlOffset);
        drawSkellys_2(g,xLvlOffset);
        drawGolem(g,xLvlOffset);

        drawBossies(g,xLvlOffset);
        drawDuckies(g,xLvlOffset);

        for(Skelly s : skellies) {
            s.drawHitbox(g, xLvlOffset);
        }

        for(Skelly2 s : skellies_2) {
            s.drawHitbox(g, xLvlOffset);
        }

        for(Golem o : golem) {
            o.drawHitbox(g, xLvlOffset);
        }

        for(Boss b : bossies){
            b.drawHitbox(g,xLvlOffset);
        }

        for(Npc d: duckies){
            d.drawHitbox(g,xLvlOffset);
        }

    }

    private void drawSkellys(Graphics g, int xLvlOffset) {
        for(Skelly s : skellies)
            if(s.isActive())
            {
                g.drawImage(skellyArr[s.getEnemyState()][s.getAniIndex()],
                        (int)(s.getHitbox().x - xLvlOffset- SKELLY_DRAWOFFSET_X) + s.flipX(),
                        (int)(s.getHitbox().y- SKELLY_DRAWOFFSET_Y),
                        SKELLY_WIDTH * s.flipW(),
                        SKELLY_HEIGHT,
                        null);
                s.drawHitbox(g, xLvlOffset);
                s.drawAttackBox(g, xLvlOffset);
            }
    }


    private void drawSkellys_2(Graphics g, int xLvlOffset) {
        for(Skelly2 s : skellies_2)
            if(s.isActive())
            {
                g.drawImage(skellyArr_2[s.getEnemyState()][s.getAniIndex()],
                        (int)(s.getHitbox().x - xLvlOffset- SKELLY_DRAWOFFSET_X_2) + s.flipX(),
                        (int)(s.getHitbox().y- SKELLY_DRAWOFFSET_Y_2),
                        SKELLY_WIDTH_2* s.flipW(),
                        SKELLY_HEIGHT_2,
                        null);
                s.drawHitbox(g, xLvlOffset);
                s.drawAttackBox(g, xLvlOffset);
            }
    }

    private void drawGolem(Graphics g, int xLvlOffset) {
        for (Golem o : golem)
            if (o.isActive()) {
                g.drawImage(golemArr[o.getEnemyState()][o.getAniIndex()],
                        (int) (o.getHitbox().x - xLvlOffset - GOLEM_DRAWOFFSET_X) + o.flipX(),
                        (int) (o.getHitbox().y - GOLEM_DRAWOFFSET_Y),
                        GOLEM_WIDTH * o.flipW(),
                        GOLEM_HEIGHT,
                        null);
                o.drawHitbox(g, xLvlOffset);
                o.drawAttackBox(g, xLvlOffset);
            }
    }

    private void drawBossies(Graphics g, int xLvlOffset){
        for (Boss b : bossies)
            if (b.isActive()) {
                g.drawImage(bossArr[b.getEnemyState()][b.getAniIndex()],
                        (int) (b.getHitbox().x - xLvlOffset - BOSS_DRAWOFFSET_X) + b.flipX(),
                        (int) (b.getHitbox().y - BOSS_DRAWOFFSET_Y),
                        BOSS_WIDTH * b.flipW(),
                         BOSS_HEIGHT,
                        null);
                b.drawHitbox(g, xLvlOffset);
                b.drawOrbs(g,xLvlOffset);
                drawBossHealthBar(g, b, playing.getPlayer());
            }
    }

    public void drawDuckies(Graphics g, int xLvlOffset) {
        for (Npc d : duckies) {
            g.drawImage(duckyArr[IDLE_NPC][d.getAniIndex()],
                    (int) (d.getHitbox().x - xLvlOffset - DUCKY_DRAWOFFSET_X),
                    (int) (d.getHitbox().y - DUCKY_DRAWOFFSET_Y),
                    DUCKY_WIDTH,
                    DUCKY_HEIGHT,
                    null);
            d.drawHitbox(g, xLvlOffset);


            if (d.isInteracted() && d.isPlayerInRange(playing.getPlayer(),5)) {
                if(d.getHitbox().x==504){
                    drawChatBox(g,d,0);
                }
                else if(d.getHitbox().x==1456) {
                    drawChatBox(g, d, 3);
                }
                else if(d.getHitbox().x==2464) {
                    drawChatBox(g, d, 6);
                }
                else if(d.getHitbox().x==3472) {
                    drawChatBox(g, d, 9);
                }
                else if(d.getHitbox().x==4984) {
                    drawChatBox(g, d, 12);
                }
            }

        }
    }

    private void drawChatBox(Graphics g,Npc n,int index) {
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0, 0, Game.GAME_WIDTH, 300);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.PLAIN,28));

        int textX=200;
        int textY=100;

        String[] lines = n.getDialog();
        g.drawString(lines[index],textX,textY);
        g.drawString(lines[index+1],textX,textY+35);
        g.drawString(lines[index+2],textX,textY+70);

        g.drawImage(wizard,Game.GAME_WIDTH-500,-100,440,600,null);

    }

    private boolean shouldDrawBossHealthBar(Boss boss, Player player) {
        if(abs(player.getHitbox().x -boss.getHitbox().x)<15 * Game.TILES_SIZE)
            return true;
        return false;
    }

    private void drawBossHealthBar(Graphics g, Boss boss, Player player) {
        if (!shouldDrawBossHealthBar(boss, player)) {
            return;
        }

        int barX = (Game.GAME_WIDTH - BOSS_HEALTHBAR_WIDTH) / 2;
        int barY =(int)( 15 * Game.SCALE);

       g.drawImage(LoadSave.GetSpriteAtlas(LoadSave.BOSS_HEALTHBAR),barX,barY,BOSS_HEALTHBAR_WIDTH,BOSS_HEALTHBAR_HEIGHT,null);

        float healthPercentage = (float) boss.getCurrentHealth() / GetMaxHealth(BOSS);
        int healthWidth = (int) (BOSS_HEALTH_WIDTH * healthPercentage);
        if(boss.getCurrentHealth() >GetMaxHealth(BOSS) * 0.8)
            g.setColor(new Color(255, 0, 0, 255));
        else
        if(boss.getCurrentHealth()  >GetMaxHealth(BOSS) * 0.6)
            g.setColor(new Color(255, 70, 0, 255));
        else
        if(boss.getCurrentHealth()  >GetMaxHealth(BOSS) * 0.4)
            g.setColor(new Color(159, 24, 24,200));
        else
        if(boss.getCurrentHealth()  >GetMaxHealth(BOSS) * 0.2)
            g.setColor(new Color(64, 19, 19,200));
        else
            if (boss.getCurrentHealth()>0){
                if(flick<5){
                    g.setColor(new Color(255, 0, 0, 255));
                    flick++;
                }
                if(flick>=5)
                {
                    g.setColor(new Color(255, 224, 147, 255));
                    flick++;
                    if(flick>=10)
                        flick =0;
                }
            }

        g.fillRect(barX+BOSS_HEALTHBAR_X_OFFSET, barY + BOSS_HEALTHBAR_Y_OFFSET , healthWidth, BOSS_HEALTH_HEIGHT);
    }

    public void checkNpcInteracted(Rectangle2D.Float attackBox){
        for (Npc d : duckies) {
            if (attackBox.intersects(d.getHitbox())) {
                d.setInteracted(true);
            } else {
                d.setInteracted(false);
            }
        }
    }

    public void clearNpcInteractions() {
        if (duckies != null) {
            for (Npc d : duckies) {
                d.setInteracted(false);
            }
        }
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        for (Skelly s : skellies)
            if (s.isActive())
                if (s.getCurrentHealth() > 0)
                    if (attackBox.intersects(s.getHitbox())) {
                        s.hurt(10);
                        return;
                    }

        for (Skelly2 s : skellies_2)
            if (s.isActive())
                if (s.getCurrentHealth() > 0)
                    if (attackBox.intersects(s.getHitbox())) {
                        s.hurt(10);
                        return;
                    }

        for(Golem g: golem)
            if(g.isActive())
                if(g.getCurrentHealth() > 0 )
                    if(attackBox.intersects(g.getHitbox())){
                        g.hurt(10);
                        return;
                    }

        for(Boss b: bossies)
            if(b.isActive())
                if(b.getCurrentHealth() > 0 )
                    if(attackBox.intersects(b.getHitbox())){
                        b.hurt(10);
                        return;
                    }
    }

    private void loadEnemyImgs() {
        skellyArr = new BufferedImage[5][11];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SKELLY_SPRITE);
        for (int i = 0; i < skellyArr.length; i++)
            for (int j = 0; j < skellyArr[i].length; j++)
                skellyArr[i][j] = temp.getSubimage(j * SKELLY_WIDTH_DEFAULT, i * SKELLY_HEIGHT_DEFAULT, SKELLY_WIDTH_DEFAULT, SKELLY_HEIGHT_DEFAULT);

        skellyArr_2 = new BufferedImage[5][13];
        BufferedImage temp2 = LoadSave.GetSpriteAtlas(LoadSave.SKELLY2_SPRITE);
        for (int i = 0; i < skellyArr_2.length; i++)
            for (int j = 0; j < skellyArr_2[i].length; j++)
                skellyArr_2[i][j] = temp2.getSubimage(j * SKELLY_WIDTH_DEFAULT_2, i * SKELLY_HEIGHT_DEFAULT_2, SKELLY_WIDTH_DEFAULT_2, SKELLY_HEIGHT_DEFAULT_2);


        golemArr = new BufferedImage[5][11];
        BufferedImage temp3 = LoadSave.GetSpriteAtlas(LoadSave.GOLEM_ATLAS);
        for (int i = 0; i < golemArr.length; i++)
            for (int j = 0; j < golemArr[i].length; j++)
                golemArr[i][j] = temp3.getSubimage(j * GOLEM_WIDTH_DEFAULT, i * GOLEM_HEIGHT_DEFAULT, GOLEM_WIDTH_DEFAULT, GOLEM_HEIGHT_DEFAULT);

        bossArr = new BufferedImage[6][10];
        BufferedImage temp4 = LoadSave.GetSpriteAtlas(LoadSave.BOSS_ATLAS);
        for (int i = 0; i < bossArr.length; i++)
            for (int j = 0; j < bossArr[i].length; j++)
                bossArr[i][j] = temp4.getSubimage(j * BOSS_WIDTH_DEFAULT, i * BOSS_HEIGHT_DEFAULT, BOSS_WIDTH_DEFAULT, BOSS_HEIGHT_DEFAULT);


        duckyArr = new BufferedImage[1][11];
        BufferedImage temp5 = LoadSave.GetSpriteAtlas(LoadSave.NPC_ATLAS);
        for (int j = 0; j < duckyArr[0].length; j++) {
            duckyArr[0][j] = temp5.getSubimage(
                    j * DUCKY_WIDTH_DEFAULT,
                    0,
                    DUCKY_WIDTH_DEFAULT,
                    DUCKY_HEIGHT_DEFAULT
            );
        }
    }

    public void resetAllEnemies(){
        for(Skelly s : skellies)
            s.resetEnemy();
        for(Skelly2 s : skellies_2)
            s.resetEnemy();
        for(Golem g: golem)
            g.resetEnemy();
        for(Npc d : duckies)
            d.resetEnemy();
        for(Boss b : bossies)
            b.resetEnemy();
    }
}