package entities;

import Objects.Potion;
import Objects.Projectile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.ObjectConstants.RED_POTION;
import static utilz.HelpMethods.*;

import gamestates.Playing;
import utilz.LoadSave;

public class Boss extends Enemy {
    private boolean dying = false;
    private boolean dead = false;
    private boolean preDash= true;
    private int tickAfterDashInIdle;
    private int dashDurationTick, dashDuration = 300;
    private int tickSinceLastDmgToPlayer;
    private int dashCounter = 0;

    private ArrayList<Projectile> orbs = new ArrayList<>();

    private boolean hasFired = false;

    private Random rnd= new Random();
    private int random;


    public Boss(float x, float y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS);
        initHitbox(51, 31);

    }


    public void update(int[][] lvlData, Playing playing) {
        if (dead || !active)
            return;

        updateBehavior(lvlData, playing);
        updateAniTick();

        updateProjectiles(lvlData,playing.getPlayer());

        if (dying && aniIndex >= GetSpriteAmount(enemyType, state) - 1) {
            dead = true;
            active = false;
        }
    }

    private void updateBehavior(int[][] lvlData, Playing playing) {
        if (firstUpdate)
            firstUpdateCheck(lvlData);

        if (inAir) {
            updateInAir(lvlData);
        } else {
            switch(state) {
                case IDLE_BOSS:
                    preDash = true;
                    if(tickAfterDashInIdle >=120){
                        if(IsFloor(hitbox,lvlData))
                            newState(WALK_BOSS);
                        else
                            inAir= true;
                        tickAfterDashInIdle = 0;
                        tickSinceLastDmgToPlayer = 60;
                    }
                    else
                        tickAfterDashInIdle++;
                    break;
                case WALK_BOSS:
                    if (canBossSeePlayer(lvlData, playing.getPlayer(),10)) {
                        newState(DASH_BOSS);
                        turnTowardsPlayer(playing.getPlayer());
                        dashCounter++;
                        random = rnd.nextInt(10)+1;
                        System.out.println(random);
                        if(random>=6)
                            playing.getObjectManager().addPotion(new Potion((int)getHitbox().x,(int)getHitbox().y,RED_POTION));

                    }
                    move(lvlData);
                    break;
                case DASH_BOSS:
                    if(preDash){
                        if(aniIndex>=3)
                            preDash = false;
                    }
                    else{
                        move(lvlData,playing);
                        checkDmgToPlayer(playing.getPlayer());
                        checkDashOver();
                    }
                    break;
                case RANGE_ATTACK_BOSS:
                    if(aniIndex==6){
                        fireProjectile();
                    }
                    if(aniIndex >=GetSpriteAmount(enemyType,state) -1){
                        newState(IDLE_BOSS);
                        hasFired=false;
                    }
                    break;
                case HIT_BOSS:
                    if (aniIndex >= GetSpriteAmount(enemyType, state) - 1) {
                        if (currentHealth <= 0) {
                            dying = true;
                            newState(DIE_BOSS);
                        } else {
                            newState(WALK_BOSS);
                        }
                    }
                    tickAfterDashInIdle = 120;
                    break;
                case DIE_BOSS:
                    break;
            }
        }
    }


    private void fireProjectile() {
        if(!hasFired){
            int dir = isFacingRight()? 1:-1;
            int projectileX = (int) (hitbox.x + (dir == 1 ? hitbox.width : -20));
            int projectileY = (int) (hitbox.y + hitbox.height / 2);

            orbs.add(new Projectile(projectileX, projectileY, dir));
            hasFired=true;
        }
    }


    private void checkDashOver() {

        dashDurationTick++;
        if (dashDurationTick >= dashDuration) {
            newState(IDLE_BOSS);
            dashDurationTick = 0;
        }
        if(dashCounter >=3)
        {
            newState(RANGE_ATTACK_BOSS);
            dashCounter=0;
        }
    }

    public void updateProjectiles(int[][] lvlData, Player player) {
        for (int i = 0; i < orbs.size(); i++) {
            Projectile orb = orbs.get(i);
            if (orb.isActive()) {
                orb.updatePos(2);
                if (orb.getHitbox().intersects(player.getHitbox())) {
                    player.changeHealth(-GetEnemyDmg(enemyType) *2);
                    orb.setActive(false);
                }
                else if(IsProjectileHittingLevel(orb, lvlData)){
                    orb.setActive(false);
                }
            } else {
                orbs.remove(i);
                i--;
            }
        }
    }

    public void drawOrbs(Graphics g, int xLvlOffset){
        ArrayList<Projectile> orbsToDraw = new ArrayList<>(orbs);
        int dir = isFacingRight()? -1:1;
        for(Projectile p : orbsToDraw){
            if (p.isActive()) {
                g.drawImage(LoadSave.GetSpriteAtlas(LoadSave.ORB),
                        (int) (p.getHitbox().x - xLvlOffset),
                        (int) (p.getHitbox().y -90),
                        70*dir, 100, null);
            }
        }
    }


    private void checkDmgToPlayer(Player player) {
        if (hitbox.intersects(player.getHitbox()))
            if (tickSinceLastDmgToPlayer >= 60) {
                tickSinceLastDmgToPlayer = 0;
                player.changeHealth(-GetEnemyDmg(enemyType));
            } else
                tickSinceLastDmgToPlayer++;
    }

    protected void move(int[][] lvlData, Playing playing) {
        float xSpeed = 0;

        if (walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;

        if (state == DASH_BOSS) {
            xSpeed *= MakeBossFaster();
        }

        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            if (IsFloor(hitbox, xSpeed, lvlData)) {
                hitbox.x += xSpeed;
                return;
            }

        if (state == DASH_BOSS) {
            System.out.println("dash end early");
            newState(IDLE_BOSS);
            preDash = true;

            dashDurationTick = 0;
        }

        changeWalkDir();

    }

    private float MakeBossFaster() {
        if(currentHealth >GetMaxHealth(BOSS) * 0.8)
            return 4;
        else
        if(currentHealth >GetMaxHealth(BOSS) * 0.6)
            return 6;
        else
        if(currentHealth >GetMaxHealth(BOSS) * 0.4)
            return 8;
        else
        if(currentHealth >GetMaxHealth(BOSS) * 0.2)
            return 10;
        else
            return 12;
    }


    public void hurt(int amount) {
        if (dying || dead || !active) return;

        super.hurt(amount);
        if (currentHealth <= 0) {
            newState(HIT_BOSS);
        }
        else
            newState(HIT_BOSS);
    }

    public boolean isDead() {
        return dead;
    }

    public int flipX() {
        return walkDir == RIGHT ? width : 0;
    }

    public int flipW() {
        return walkDir == RIGHT ? -1 : 1;
    }


    public void resetEnemy() {
        super.resetEnemy();
        dying = false;
        dead = false;
        active = true;
        dashCounter = 0;

    }

    public boolean isFacingRight() {
        return walkDir == RIGHT;
    }


    public boolean isActive() {
        return active && !dead;
    }
}
