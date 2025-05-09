package levels;

import Objects.Cannon;
import Objects.GameContainer;
import Objects.Potion;
import Objects.Spike;
import entities.*;
import main.Game;
import utilz.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.HelpMethods.*;



public class Level {

	private BufferedImage img;
	private int[][] lvlData;

	private ArrayList<Skelly> skellies;
	private ArrayList<Skelly2> skellies_2;
	private ArrayList<Golem> golem;
	private ArrayList<Npc> duckies;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private ArrayList<Cannon> cannons;
	private ArrayList<Boss> bossies;


	private int lvlTilesWide;
	private int maxTilesOffset;
	private int maxLvlOffsetX;
	private Point playerSpawn;

	public Level(BufferedImage img) {
		this.img = img;

		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		createSpikes();
		createCannons();
		calcLvlOffsets();
		calcPlayerSpawn();

	}

	private void createCannons() {
		cannons = HelpMethods.GetCannons(img);
	}

	private void createSpikes() {
		spikes = HelpMethods.GetSpikes(img);
	}

	private void createContainers() {
		containers = HelpMethods.GetContainers(img);
	}

	private void createPotions() {
		potions = HelpMethods.GetPotions(img);
	}

	private void calcPlayerSpawn() {
		playerSpawn = GetPlayerSpawn(img);
	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide- Game.TILES_IN_WIDTH;
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
	}

	private void createEnemies() {
		skellies = GetSkellys(img);
		skellies_2 = GetSkellys_2(img);
		golem = GetGolem(img);
		bossies = GetBoss(img);
		duckies = GetDuckies(img);
	}

	private void createLevelData() {
		lvlData = GetLevelData(img);
	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}

	public int[][] getLevelData() {
		return lvlData;
	}

	public int getLvlOffset(){
		return maxLvlOffsetX;
	}

	public ArrayList<Skelly> getSkellies(){
		return skellies;
	}

	public ArrayList<Skelly2> getSkellies_2(){
		return skellies_2;
	}

	public ArrayList<Golem> getGolem(){
		return golem;
	}

	public ArrayList<Boss> getBossies() {
		return bossies;
	}

	public ArrayList<Npc> getDuckies(){
		return duckies;
	}

	public Point getPlayerSpawn(){
		return playerSpawn;
	}

	public ArrayList<Potion> getPotions() {
		return potions;
	}

	public ArrayList<GameContainer> getContainers()
	{
		return containers;
	}

	public ArrayList<Spike> getSpikes()
	{
		return spikes;
	}

	public ArrayList<Cannon> getCannons()
	{
		return cannons;
	}


}