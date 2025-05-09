package utilz;

import Objects.*;
import entities.*;
import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.ObjectConstants.*;

public class HelpMethods {

	
	private static final Set<Integer> SOLID_TILES = new HashSet<>();

	static {
		SOLID_TILES.add(1);
		SOLID_TILES.add(2);
		SOLID_TILES.add(3);
		SOLID_TILES.add(4);
		SOLID_TILES.add(5);
		SOLID_TILES.add(6);
		SOLID_TILES.add(7);
		SOLID_TILES.add(8);
		SOLID_TILES.add(9);
		SOLID_TILES.add(13);
		SOLID_TILES.add(14);
		SOLID_TILES.add(15);
		SOLID_TILES.add(16);
		SOLID_TILES.add(17);
		SOLID_TILES.add(18);
		SOLID_TILES.add(19);
		SOLID_TILES.add(20);
		SOLID_TILES.add(21);
		SOLID_TILES.add(24);
		SOLID_TILES.add(25);
		SOLID_TILES.add(26);
		SOLID_TILES.add(48);
		SOLID_TILES.add(49);
		SOLID_TILES.add(50);
		SOLID_TILES.add(77);
		SOLID_TILES.add(78);
		SOLID_TILES.add(79);
		SOLID_TILES.add(80);
		SOLID_TILES.add(81);
		SOLID_TILES.add(82);
	}

	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		if (!IsSolid(x, y, lvlData))
			if (!IsSolid(x + width, y + height, lvlData))
				if (!IsSolid(x + width, y, lvlData))
					if (!IsSolid(x, y + height, lvlData))
						return true;
		return false;
	}

	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		int maxWidth = lvlData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWidth)
			return true;
		if (y < 0 || y >= Game.GAME_HEIGHT)
			return true;
		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;

		return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
	}

	public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData){
		return IsSolid(p.getHitbox().x + p.getHitbox().width / 2, p.getHitbox().y + p.getHitbox().height / 2, lvlData);
	}

	public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData){
		int value = lvlData[yTile][xTile];

		if (value >= 96 || value < 0 || SOLID_TILES.contains(value))
			return true;
		return false;
	}


	public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int) (hitbox.x / Game.TILES_SIZE);
		if (xSpeed > 0) {
			// Right
			int tileXPos = currentTile * Game.TILES_SIZE;
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1;
		} else
			// Left
			return currentTile * Game.TILES_SIZE;
	}

	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
		if (airSpeed > 0) {
			// Falling - touching floor
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
		} else
			// Jumping
			return currentTile * Game.TILES_SIZE;

	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
			if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
				return false;

		return true;

	}

	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
		if(xSpeed > 0)
			return IsSolid(hitbox.x + xSpeed + hitbox.width, hitbox.y + hitbox.height + 1 , lvlData);
		return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1 , lvlData);

	}

	public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile){
		int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
		int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

		if (firstXTile > secondXTile)
			return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
		else
			return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
	}

	public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData){
		for(int i=0;i<xEnd-xStart;i++)
			if(IsTileSolid(xStart + i, y,lvlData))
				return false;
		return true;
	}

	public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float enemyBox, Rectangle2D.Float playerBox, int yTile) {
		int firstXTile = (int) (enemyBox.x / Game.TILES_SIZE);

		int secondXTile;
		if (IsSolid(playerBox.x, playerBox.y + playerBox.height + 1, lvlData))
			secondXTile = (int) (playerBox.x / Game.TILES_SIZE);
		else
			secondXTile = (int) ((playerBox.x + playerBox.width) / Game.TILES_SIZE);

		if (firstXTile > secondXTile)
			return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
		else
			return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
	}

	public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
		if (IsAllTilesClear(xStart, xEnd, y, lvlData))
			for (int i = 0; i < xEnd - xStart; i++) {
				if (!IsTileSolid(xStart + i, y + 1, lvlData))
					return false;
			}
		return true;
	}



	public static int[][] GetLevelData(BufferedImage img) {

		int[][] lvlData = new int[img.getHeight()][img.getWidth()];
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 96) {
					value = 0;
				}
				lvlData[j][i] = value;
			}


		}
		return lvlData;
	}

	public static Point GetPlayerSpawn(BufferedImage img) {
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == 200)
					return new Point(i * Game.TILES_SIZE, j * Game.TILES_SIZE);
			}
		return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
	}

	public static boolean IsFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
			if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		return true;
	}


	public static ArrayList<Skelly> GetSkellys(BufferedImage img){

		ArrayList<Skelly> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == SKELLY) {
					list.add(new Skelly(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
				}
			}

		}
		return list;
	}

	public static ArrayList<Skelly2> GetSkellys_2(BufferedImage img){

		ArrayList<Skelly2> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == SKELLY2) {
					list.add(new Skelly2(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
				}
			}

		}
		return list;
	}

	public static ArrayList<Golem> GetGolem(BufferedImage img){

		ArrayList<Golem> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == GOLEM) {
					list.add(new Golem(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
				}
			}

		}
		return list;
	}

	public static ArrayList<Boss> GetBoss(BufferedImage img){

		ArrayList<Boss> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == BOSS) {
					list.add(new Boss(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
				}
			}

		}
		return list;
	}

	public static ArrayList<Npc> GetDuckies(BufferedImage img){

		ArrayList<Npc> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == DUCKY) {
					list.add(new Npc(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
					System.out.println(i* Game.TILES_SIZE);
				}
			}

		}
		return list;
	}

	public static ArrayList<Potion> GetPotions(BufferedImage img){

		ArrayList<Potion> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == RED_POTION || value == BLUE_POTION || value == SPECIAL_POTION) {
					list.add(new Potion(i * Game.TILES_SIZE, j* Game.TILES_SIZE,value));
				}
			}

		}
		return list;
	}

	public static ArrayList<GameContainer> GetContainers(BufferedImage img){

		ArrayList<GameContainer> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == BARREL || value == BOX) {
					list.add(new GameContainer(i * Game.TILES_SIZE, j* Game.TILES_SIZE,value));
				}
			}
		}
		return list;
	}


	public static ArrayList<Spike> GetSpikes(BufferedImage img) {
		ArrayList<Spike> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == SPIKE) {
					list.add(new Spike(i * Game.TILES_SIZE, j* Game.TILES_SIZE,SPIKE));
				}
			}
		}
		return list;
	}

	public static ArrayList<Cannon> GetCannons(BufferedImage img) {
		ArrayList<Cannon> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == CANNON_LEFT || value == CANNON_RIGHT) {
					list.add(new Cannon(i * Game.TILES_SIZE, j* Game.TILES_SIZE,value));
				}
			}
		}
		return list;
	}

}