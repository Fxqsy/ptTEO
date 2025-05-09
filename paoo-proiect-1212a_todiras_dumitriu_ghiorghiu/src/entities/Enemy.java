package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static java.lang.Math.abs;
import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.GRAVITY;
import static utilz.HelpMethods.*;

public abstract class Enemy extends Entity {
    protected int enemyType;

    protected boolean firstUpdate = true;

    protected float walkSpeed = 0.3f * Game.SCALE;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = 1 * Game.TILES_SIZE;
    protected boolean active = true;
    protected boolean attackChecked;

    public Enemy(float x, float y, int width, int height,int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
        walkSpeed = Game.SCALE * 0.35f;
    }

    protected void firstUpdateCheck(int[][] lvlData){
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir=true;
        firstUpdate=false;
    }

    protected void updateInAir(int[][] lvlData){
        if(CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)){
            hitbox.y += airSpeed;
            airSpeed+= GRAVITY;
        }else{
            inAir=false;
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
            tileY = (int)( hitbox.y / Game.TILES_SIZE);
        }
    }

    protected void move(int[][] lvlData){
        float xSpeed = 0;

        if(walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;

        if(CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            if(IsFloor(hitbox, xSpeed, lvlData)){
                hitbox.x += xSpeed;
                return;
            }

        changeWalkDir();

    }

    protected void turnTowardsPlayer(Player player){
        if(player.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player,int nrTiles){
        int playerTileY =(int) player.getHitbox().y / Game.TILES_SIZE;
        if(playerTileY == tileY)
            if(isPlayerInRange(player,nrTiles)){
                return IsSightClear(lvlData, hitbox, player.hitbox, tileY);
            }
        return false;
    }

    protected boolean canBossSeePlayer(int[][] lvlData, Player player,int nrTiles){
        int playerTileY =(int) player.getHitbox().y / Game.TILES_SIZE;
        if(abs(playerTileY -tileY)<=5 * Game.TILES_SIZE)
            if(isPlayerInRange(player,nrTiles)){
                return IsSightClear(lvlData, hitbox, player.hitbox, tileY);
            }
        return false;
    }


    protected boolean isPlayerInRange(Player player, int nrTiles) {
        int absValue =(int) abs(player.hitbox.x - hitbox.x);
        return absValue<= attackDistance * nrTiles;
    }

    protected boolean isPlayerCloseForAttack(Player player){
        int absValue =(int) abs(player.hitbox.x - hitbox.x);
        return absValue<= attackDistance;
    }


    protected void newState(int enemyState){
        this.state = enemyState;
        aniIndex = 0;
        aniTick = 0;
    }

    public void hurt(int amount){
        currentHealth-=amount;
        if(currentHealth <= 0)
            newState(DEAD);
        else
            newState(HIT);
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Player player){
        if(attackBox.intersects(player.hitbox))
            player.changeHealth(-GetEnemyDmg(enemyType));
        attackChecked = true;
    }

    protected void updateAniTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, state)) {
                aniIndex = 0;

                switch (state){
                    case ATTACK,HIT -> state= IDLE;
                    case DEAD -> active = false;
                }
            }
        }
    }

    protected void changeWalkDir() {
        if(walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    public void resetEnemy(){
        hitbox.x = x;
        hitbox.y=y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        airSpeed = 0;
    }

    public boolean isActive(){
        return active;
    }

}