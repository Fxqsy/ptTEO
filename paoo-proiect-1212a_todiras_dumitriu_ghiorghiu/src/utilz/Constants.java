package utilz;

import main.Game;

public class Constants {

	public static final float GRAVITY = 0.04f * Game.SCALE;
	public static final int ANI_SPEED = 17;
	public static final int ANI_SPEED_CHAR = 40;
	public static final float BOOSTED_JUMP = -7.5f;

	public static class Projectiles {
		public static final int CANNON_BALL_DEFAULT_WIDTH = 15;
		public static final int CANNON_BALL_DEFAULT_HEIGHT = 15;

		public static final int CANNON_BALL_WIDTH = (int)(Game.SCALE * CANNON_BALL_DEFAULT_WIDTH);
		public static final int CANNON_BALL_HEIGHT = (int)(Game.SCALE * CANNON_BALL_DEFAULT_HEIGHT);
		public static final float SPEED = 0.75f * Game.SCALE;
	}

	public static class ObjectConstants {
		public static final int SPIKE = 255;
		public static final int RED_POTION = 100;
		public static final int BLUE_POTION = 101;
		public static final int SPECIAL_POTION = 102;
		public static final int BARREL = 200;
		public static final int BOX = 201;
		public static final int CANNON_LEFT = 150;
		public static final int CANNON_RIGHT = 151;

		public static final int RED_POTION_VALUE = 15;
		public static final int BLUE_POTION_VALUE = 10;

		public static final int CONTAINER_WIDTH_DEFAULT = 40;
		public static final int CONTAINER_HEIGHT_DEFAULT = 30;
		public static final int CONTAINER_WIDTH = (int) (Game.SCALE * CONTAINER_WIDTH_DEFAULT);
		public static final int CONTAINER_HEIGHT = (int) (Game.SCALE * CONTAINER_HEIGHT_DEFAULT);

		public static final int POTION_WIDTH_DEFAULT = 12;
		public static final int POTION_HEIGHT_DEFAULT = 16;
		public static final int POTION_WIDTH = (int) (Game.SCALE * POTION_WIDTH_DEFAULT);
		public static final int POTION_HEIGHT = (int) (Game.SCALE * POTION_HEIGHT_DEFAULT);

		public static final int SPIKE_WIDTH_DEFAULT = 32;
		public static final int SPIKE_HEIGHT_DEFAULT = 32;
		public static final int SPIKE_WIDTH = (int) (Game.SCALE * SPIKE_WIDTH_DEFAULT);
		public static final int SPIKE_HEIGHT = (int) (Game.SCALE * SPIKE_HEIGHT_DEFAULT);

		public static final int CANNON_WIDTH_DEFAULT = 40;
		public static final int CANNON_HEIGHT_DEFAULT = 26;
		public static final int CANNON_WIDTH = (int) (CANNON_WIDTH_DEFAULT * Game.SCALE);
		public static final int CANNON_HEIGHT = (int) (CANNON_HEIGHT_DEFAULT * Game.SCALE);

		public static int GetSpriteAmount(int object_type) {
			switch (object_type) {
				case RED_POTION, BLUE_POTION, SPECIAL_POTION:
					return 7;
				case BARREL, BOX:
					return 8;
				case CANNON_LEFT, CANNON_RIGHT:
					return 7;
			}
			return 1;
		}
	}

	public static class EnemyConstants { //GREEN
		public static final int SKELLY = 100;
		public static final int SKELLY2 = 250;
		public static final int GOLEM = 150;
		public static final int DUCKY = 50;
		public static final int BOSS = 123;

		public static final int IDLE = 0;
		public static final int RUNNING = 1;
		public static final int ATTACK = 2;
		public static final int HIT = 3;
		public static final int DEAD = 4;

		public static final int IDLE_2 = 0;
		public static final int RUNNING_2 = 1;
		public static final int ATTACK_2 = 2;
		public static final int HIT_2 = 3;
		public static final int DEAD_2 = 4;

		public static final int IDLE_G = 0;
		public static final int RUNNING_G = 1;
		public static final int ATTACK_G = 2;
		public static final int HIT_G = 3;
		public static final int DEAD_G = 4;

		public static final int IDLE_NPC = 0;

		public static final int IDLE_BOSS = 0;
		public static final int WALK_BOSS = 1;
		public static final int DASH_BOSS = 2;
		public static final int RANGE_ATTACK_BOSS = 3;
		public static final int HIT_BOSS = 4;
		public static final int DIE_BOSS = 5;

		public static final int SKELLY_WIDTH_DEFAULT = 96;
		public static final int SKELLY_HEIGHT_DEFAULT = 64;
		public static final int SKELLY_WIDTH_DEFAULT_2 = 64;
		public static final int SKELLY_HEIGHT_DEFAULT_2 = 64;
		public static final int GOLEM_WIDTH_DEFAULT = 90;
		public static final int GOLEM_HEIGHT_DEFAULT = 64;
		public static final int BOSS_WIDTH_DEFAULT = 80;
		public static final int BOSS_HEIGHT_DEFAULT = 80;

		public static final int SKELLY_WIDTH = (int)(SKELLY_WIDTH_DEFAULT * Game.SCALE);
		public static final int SKELLY_HEIGHT = (int)(SKELLY_HEIGHT_DEFAULT * Game.SCALE);
		public static final int SKELLY_WIDTH_2 = (int)(SKELLY_WIDTH_DEFAULT_2 * Game.SCALE * 1.25);
		public static final int SKELLY_HEIGHT_2 = (int)(SKELLY_HEIGHT_DEFAULT_2 * Game.SCALE * 1.25);
		public static final int GOLEM_WIDTH = (int)(GOLEM_WIDTH_DEFAULT * Game.SCALE *1.5);
		public static final int GOLEM_HEIGHT = (int)(GOLEM_HEIGHT_DEFAULT * Game.SCALE*1.5);
		public static final int BOSS_WIDTH = (int)(BOSS_WIDTH_DEFAULT * Game.SCALE * 3);
		public static final int BOSS_HEIGHT = (int)(BOSS_HEIGHT_DEFAULT * Game.SCALE * 3);

		public static final int SKELLY_DRAWOFFSET_X = (int)(37 * Game.SCALE);
		public static final int SKELLY_DRAWOFFSET_Y = (int)(33 * Game.SCALE);
		public static final int SKELLY_DRAWOFFSET_X_2 = (int)(20 * Game.SCALE *1.5);
		public static final int SKELLY_DRAWOFFSET_Y_2 = (int)(33 * Game.SCALE *1.5);
		public static final int GOLEM_DRAWOFFSET_X = (int)(50 * Game.SCALE);
		public static final int GOLEM_DRAWOFFSET_Y = (int)(65 * Game.SCALE);
		public static final int BOSS_DRAWOFFSET_X = (int)(87 * Game.SCALE );
		public static final int BOSS_DRAWOFFSET_Y = (int)(150 * Game.SCALE );

		public static final int DUCKY_WIDTH_DEFAULT = 64;
		public static final int DUCKY_HEIGHT_DEFAULT = 64;

		public static final int DUCKY_WIDTH = (int)(DUCKY_WIDTH_DEFAULT * Game.SCALE);
		public static final int DUCKY_HEIGHT = (int)(DUCKY_HEIGHT_DEFAULT * Game.SCALE);

		public static final int DUCKY_DRAWOFFSET_X = (int)(21 * Game.SCALE);
		public static final int DUCKY_DRAWOFFSET_Y = (int)(32 * Game.SCALE);

		public static final int BOSS_HEALTHBAR_WIDTH_DEFAULT = 96 * 3;
		public static final int BOSS_HEALTHBAR_WIDTH = (int)(BOSS_HEALTHBAR_WIDTH_DEFAULT * Game.SCALE);

		public static final int BOSS_HEALTHBAR_HEIGHT_DEFAULT = (int)(16*1.5) ;
		public static final int BOSS_HEALTHBAR_HEIGHT = (int)(BOSS_HEALTHBAR_HEIGHT_DEFAULT * Game.SCALE);

		public static final int BOSS_HEALTH_WIDTH = BOSS_HEALTHBAR_WIDTH - (int)(80 * Game.SCALE);
		public static final int BOSS_HEALTH_HEIGHT = BOSS_HEALTHBAR_HEIGHT - (int)(8 * Game.SCALE);

		public static final int BOSS_HEALTHBAR_X_OFFSET =(int)( 39 * Game.SCALE);
		public static final int BOSS_HEALTHBAR_Y_OFFSET = (int)(4*Game.SCALE);

		public static int GetSpriteAmount(int enemy_type, int enemy_state) {
			switch (enemy_type) {
				case SKELLY:
					switch(enemy_state) {
						case IDLE:
							return 8;
						case RUNNING:
							return 10;
						case ATTACK:
							return 11;
						case HIT:
							return 5;
						case DEAD:
							return 11;
					}
				case SKELLY2:
					switch(enemy_state) {
						case IDLE_2:
							return 4;
						case RUNNING_2:
							return 12;
						case ATTACK_2:
							return 13;
						case HIT_2:
							return 3;
						case DEAD_2:
							return 13;
					}
				case GOLEM:
					switch(enemy_state) {
						case IDLE_G:
							return 8;
						case RUNNING_G:
							return 10;
						case ATTACK_G:
							return 11;
						case HIT_G:
							return 4;
						case DEAD_G:
							return 11;
					}
				case DUCKY:
					switch (enemy_state){
						case IDLE_NPC:
							return 11;
					}
				case BOSS:
					switch (enemy_state){
						case IDLE_BOSS:
							return 4;
						case WALK_BOSS:
							return 4;
						case DASH_BOSS:
							return 6;
						case RANGE_ATTACK_BOSS:
							return 10;
						case DIE_BOSS:
							return 10;
						case HIT_BOSS:
							return 3;
					}
			}
			return 0;
		}

		public static int GetMaxHealth(int enemy_type) {
			switch(enemy_type) {
				case SKELLY:
					return 10;
				case SKELLY2:
					return 10;
				case GOLEM:
					return 150;
				case BOSS:
					return 100;
				default:
					return 1;
			}
		}

		public static int GetEnemyDmg(int enemy_type) {
			switch(enemy_type) {
				case SKELLY:
					return 2;
				case SKELLY2:
					return 2;
				case GOLEM:
					return 8;
				case BOSS:
					return 1;
				default:
					return 1;
			}
		}
	}

	public static class Environment {
		public static final int SMALL_CLOUD_WIDTH_DEFAULT = 100;
		public static final int SMALL_CLOUD_HEIGHT_DEFAULT = 50;

		public static final int SMALL_CLOUD_WIDTH = (int)(SMALL_CLOUD_WIDTH_DEFAULT * Game.SCALE);
		public static final int SMALL_CLOUD_HEIGHT = (int)(SMALL_CLOUD_HEIGHT_DEFAULT * Game.SCALE);
	}

	public static class UI {
		public static class Buttons {
			public static final int B_WIDTH_DEFAULT = 140;
			public static final int B_HEIGHT_DEFAULT = 56;
			public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
			public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
		}

		public static class PauseButtons {
			public static final int SOUND_SIZE_DEFAULT = 42;
			public static final int SOUND_SIZE = (int) (SOUND_SIZE_DEFAULT * Game.SCALE);
		}

		public static class URMButtons {
			public static final int URM_DEFAULT_SIZE = 56;
			public static final int URM_SIZE = (int) (URM_DEFAULT_SIZE * Game.SCALE);
		}

		public static class VolumeButtons {
			public static final int VOLUME_DEFAULT_WIDTH = 28;
			public static final int VOLUME_DEFAULT_HEIGHT = 44;
			public static final int SLIDER_DEFAULT_WIDTH = 215;

			public static final int VOLUME_WIDTH = (int) (VOLUME_DEFAULT_WIDTH * Game.SCALE);
			public static final int VOLUME_HEIGHT = (int) (VOLUME_DEFAULT_HEIGHT * Game.SCALE);
			public static final int SLIDER_WIDTH = (int) (SLIDER_DEFAULT_WIDTH * Game.SCALE);
		}
	}

	public static class Directions {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}

	public static class PlayerConstants {
		public static final int IDLE = 0;
		public static final int SHOOT_PROJ = 1;
		public static final int RUNNING = 2;
		public static final int FALLING = 3;
		public static final int JUMP = 4;
		public static final int HIT = 5;
		public static final int DEAD = 6;
		public static final int ATTACK_1 = 7;

		public static int GetSpriteAmount(int player_action) {
			switch (player_action) {
				case DEAD:
				case RUNNING:
				case ATTACK_1:
					return 8;
				case IDLE:
					return 4;
				case HIT:
					return 2;
				case JUMP:
				case FALLING:
					return 6;
				case SHOOT_PROJ:
					return 3;
				default:
					return 1;
			}
		}
	}
}