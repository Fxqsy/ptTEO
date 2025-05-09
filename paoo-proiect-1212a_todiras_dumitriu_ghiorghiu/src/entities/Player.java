package entities;

import Objects.Projectile;
import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.Constants.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

public class Player extends Entity {
	private BufferedImage[][] animations;

	private boolean moving = false, attacking = false, shooting = false;
	private boolean left, right, jump;
	private int[][] lvlData;
	private float xDrawOffset = 9 * Game.SCALE;
	private float yDrawOffSet = 11 * Game.SCALE;

	// Jumping / Gravity
	private float jumpSpeed = -2.50f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

	// potionBar


	private int bgImgX = (int)(525 * Game.SCALE);
	private int bgImgY = (int)(365 * Game.SCALE);

	private int bgImgWidth = (int)(208 * Game.SCALE);
	private int bgImgHeight = (int)(70 * Game.SCALE);

	private BufferedImage bgImg = LoadSave.GetSpriteAtlas(LoadSave.POTION_CHECK_BG);

	private int spacing = 50;

	private int potionsCheckBarX = (int)(550 * Game.SCALE);
	private int potionsCheckBarY = (int)(385 * Game.SCALE);

	private int potionCheckWidth = (int)(32 * Game.SCALE);
	private int potionCheckHeight = (int)(32 * Game.SCALE);

	private int potionCounter = 0;
	private int iGotThisManyPotionsForThisLevel=0;


	//status bar
	private BufferedImage statusBarImg;

	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (380 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);

	private int powerBarWidth = (int) (104 * Game.SCALE);
	private int powerBarHeight = (int) (3 * Game.SCALE);
	private int powerBarXStart = (int) (44 * Game.SCALE);
	private int powerBarYStart = (int) (34 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	private int powerMaxValue = 100;
	private int powerValue = powerMaxValue;

	private BufferedImage potionCheckImg;

	private boolean facingRight = true;

	private int healthWidth = healthBarWidth;

	private int flipX = 0;
	private int flipW = 1;

	private boolean jumpBoostActive = false;
	private long jumpBoostEndTime = 0;
	private boolean attackChecked;

	private Playing playing;

	private int tileY=0;

	private ArrayList<Projectile> spells = new ArrayList<>();

	private int powerGrowSpeed = 10;
	private int powerGrowTick;

	private boolean interacting = false;

	private int flick=0;

	//private String playerName;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
	//this.playerName = playing.getGame().getPlayerName();
		this.iGotThisManyPotionsForThisLevel=playing.poz2;
		this.state = IDLE;
		this.aniIndex = 0;
		this.aniTick = 0;
		this.maxHealth = 10;
		this.currentHealth = maxHealth;
		this.walkSpeed = Game.SCALE * 1.0f;
		loadAnimations();
		initHitbox(16,26);
		initAttackBox();

	}
/// ///////AICI AM MODIFICAT EU TEO era public void serSpawn(POINT spawn)
	public void setSpawn(Point spawn){
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x,y,(int) (20*Game.SCALE) , (int) (30*Game.SCALE));
	}

	public void update() {
		updateHealthBar();
		updatePowerBar();
		if(currentHealth <= 0){
			if(state != DEAD){
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			} else if(aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1){
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);

			}else
				updateAnimationTick();

			return;
		}
		if (jumpBoostActive && System.currentTimeMillis() > jumpBoostEndTime) {
			jumpBoostActive = false;
		}


		updateAttackBox();

		updatePos();
		if(moving) {
			checkPotionTouched();
			checkSpikesTouched();
			tileY = (int)(hitbox.y / Game.TILES_SIZE);
		}
		if(attacking)
			checkAttack();

		if(interacting)
			checkInteract();
		if(shooting){
			playing.getGame().getAudioPlayer().playAttackRanged();
		}

		int tileX = (int)((hitbox.x + hitbox.width / 2) / Game.TILES_SIZE);
		int tileY = (int)((hitbox.y + hitbox.height / 2) / Game.TILES_SIZE);

		if (tileY >= 0 && tileY < lvlData.length && tileX >= 0 && tileX < lvlData[0].length) {
			int tileIndex = lvlData[tileY][tileX];
			if (tileIndex == 85) {
				playing.completeLevel();
				playing.setLevelCompleted(true);


			}
		}

		updateAnimationTick();
		setAnimation();
	}

	public void shootProjectile() {

		if (powerValue >= 60) {
			shooting = true;
			changePower(60);
			state = SHOOT_PROJ;

			int dir = isFacingRight() ? 1 : -1;
			int projectileX = (int) (hitbox.x + (dir == 1 ? hitbox.width / 2 - 60 : -hitbox.width / 2 + 60));
			int projectileY = (int) (hitbox.y + height / 3);
			spells.add(new Projectile(projectileX, projectileY, dir));
		}
	}

	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
	}

	private void checkAttack() {
		if(attackChecked || aniIndex!=4)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();

	}

	private void checkInteract(){
		if(interacting){
			playing.checkNpcInteract(attackBox);
		} else{
			playing.clearNpcInteractions();
		}
	}

	public void updateSpells() {
			for (Projectile s : spells) {
				if (s.isActive()) {
					s.updatePos(1.5f);
				}
			}
	}

	public void drawSpell(Graphics g, int xLvlOffset) {
		ArrayList<Projectile> spellsToDraw = new ArrayList<>(spells);
		int dir = isFacingRight() ? 1 : -1;
		for (Projectile p : spellsToDraw) {
			if (p.isActive()) {
				g.drawImage(LoadSave.GetSpriteAtlas(LoadSave.FIREBALL),
						(int) (p.getHitbox().x - xLvlOffset),
						(int) (p.getHitbox().y -30),
						50*dir, 50, null);
			}
		}
	}

	private void updateAttackBox() {
		if(right){
			attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 2);
		}else if(left){
			attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 4);
		}

		attackBox.y = hitbox.y + (Game.SCALE * 1);
	}

	private void updateHealthBar() {
		healthWidth = (int) (healthBarWidth * currentHealth / (float)( maxHealth));
	}

	private void updatePowerBar(){
		powerWidth = (int)(powerBarWidth * powerValue / (float)(powerMaxValue));

		powerGrowTick++;
		if(powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(2);
		}
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[state][aniIndex],
				(int) (hitbox.x - xDrawOffset) - lvlOffset + flipX ,
				(int) (hitbox.y - yDrawOffSet),
				width * flipW, height, null);
		drawHitbox(g, lvlOffset);
		drawAttackBox(g,lvlOffset);

		drawUI(g);
	}



	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);

		if(currentHealth >maxHealth * 0.7)
			g.setColor(new Color(255, 0, 0, 255));
		else
			if(currentHealth >maxHealth * 0.4)
				g.setColor(new Color(255, 221, 0, 255));
			else
			{
				if(flick<5){
					g.setColor(new Color(255, 89, 0, 255));
					flick++;
				}
				if(flick>=5){
					g.setColor(new Color(255, 255, 255, 200));
					flick++;
					if(flick >=10)
						flick = 0;
				}
			}
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);

		if(powerValue<=25)
			g.setColor(new Color(255, 255, 255,200));
		else if(powerValue <=50)
			g.setColor(new Color(42, 66, 110, 255));
		else if(powerValue <=75)
			g.setColor(new Color(65, 98, 161, 255));
		else
			g.setColor(new Color(54, 108, 214, 255));

		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);

		g.drawImage(bgImg,bgImgX,bgImgY,bgImgWidth,bgImgHeight,null);
		if(iGotThisManyPotionsForThisLevel>0)
		{
			for(int i=0;i<iGotThisManyPotionsForThisLevel;i++)
				g.drawImage(potionCheckImg,
						potionsCheckBarX+i*spacing,
						potionsCheckBarY,
						 potionCheckWidth,
						 potionCheckHeight,
						null);

		}
//		g.drawImage(potionCheckImg, potionsCheckBarX+i*potionsCheckBarX,potionsCheckBarY,3* potionCheckWidth,3* potionCheckHeight,null);


	}

	public void addMilestone() {
		iGotThisManyPotionsForThisLevel++;
		playing.getGame().getAudioPlayer().playPotionEffect();
	}

	public int getPotionCounter(){
		return potionCounter;
	}

	public int getWhatIGot(){
		return iGotThisManyPotionsForThisLevel;
	}

	public void setPotionCounter(int v){
		potionCounter=v;
	}

	public void setWhatIGot(int v){
		iGotThisManyPotionsForThisLevel=v;
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED_CHAR) {
			aniTick = 0;
			aniIndex++;

			if (aniIndex >= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
				shooting = false;
			}
			if (state == SHOOT_PROJ && aniIndex == 2) {
				shootProjectile();
				setShooting(false);
				state= IDLE;
			}
		}
	}


	private void setAnimation() {
		int startAni = state;

		if (shooting) {
			state = SHOOT_PROJ;
			if(startAni != SHOOT_PROJ) {
				aniIndex = 0;
				aniTick = 0;
				return;
			}
		}else

		if(state!=SHOOT_PROJ) {
			if (moving)
				state = RUNNING;
			else
				state = IDLE;

			if (inAir) {
				if (airSpeed < 0)
					state = JUMP;
				else
					state = FALLING;
			}

			if (attacking) {
				state = ATTACK_1;
				if (startAni != ATTACK_1) {
					aniIndex = 3;
					aniTick = 0;
					return;
				}
			}

		}
		if (startAni != state)
			resetAniTick();
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {
		moving = false;

		if (jump)
			jump();

		if (!inAir)
			if ((!left && !right) || (right && left))
				return;

		float xSpeed = 0;

		if (left){
			xSpeed -= walkSpeed;
			flipX = width;
			flipW = -1;
		}

		if (right){
			xSpeed += walkSpeed;
			flipX = 0;
			flipW = 1;
		}

		if (!inAir)
			if (!IsEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		moving = true;
	}

	private void jump() {
		if (inAir)
			return;

		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);

		inAir = true;
		if(!jumpBoostActive)
			airSpeed = jumpSpeed;
		else
			airSpeed = BOOSTED_JUMP;

	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;

	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}

	}

	public void changeHealth(int value) {
		currentHealth+= value;
		if(currentHealth <= 0)
		{
			currentHealth = 0;
			//gameover
		}else if(currentHealth >= maxHealth){
			currentHealth = maxHealth;
		}
	}

	public void changePower(int value){
		powerValue += value;
		if(powerValue >= powerMaxValue)
			powerValue = powerMaxValue;
		else if(powerValue <= 0)
			powerValue =0;
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[8][8];
		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(i * 32, j * 32, 32, 32);

		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
		potionCheckImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR2);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
		spells.clear();

	}

	public void activateJumpBoost(int durationSeconds) {
		jumpBoostActive = true;
		jumpBoostEndTime = System.currentTimeMillis() + (durationSeconds * 1000);
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public void setShooting(boolean shooting){
		if (shooting && powerValue >= 50) {
			this.shooting = shooting;
		} else if (!shooting) {
			this.shooting = shooting;
		}
//		System.out.println("Shooting set to: " + shooting);
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isFacingRight(){
		return facingRight;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetPotionCounter(){
		potionCounter = 0;
	}

	public void resetWhatIGot(){
		iGotThisManyPotionsForThisLevel = 0;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		attackChecked = false;
		moving = false;
		airSpeed = 0f; //BUG
		shooting = false;
		state = IDLE;
		currentHealth = maxHealth;
		powerValue = powerMaxValue;
		hitbox.x = x;
		hitbox.y = y;
		if(!IsEntityOnFloor(hitbox,lvlData))
			inAir = true;
	}


	public void kill() {
		currentHealth = 0;
	}

	public int getTileY(){
		return tileY;
	}

	public ArrayList<Projectile> getSpells() {
		return spells;
	}

	public void setFacingRight(boolean b) {
		this.facingRight = b;
	}

	public void clearSpells() {
		spells.clear();
	}

	public boolean isShooting() {
		return shooting;
	}

	public void setInteracting(boolean b) {
		this.interacting = b;
	}

	public int getHealth(){
		return currentHealth;
	}

}