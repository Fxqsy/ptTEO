package utilz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class LoadSave {

	public static final String PLAYER_ATLAS = "char_anim.png";
	public static final String LEVEL_ATLAS = "free.png";
	public static final String MENU_BUTTONS = "button_atlas1.png";
	public static final String MENU_BACKGROUND = "Menu_bg.png";
	public static final String PAUSE_BACKGROUND = "pause_menu1.png";
	public static final String SOUND_BUTTONS = "sound_button1.png";
	public static final String URM_BUTTONS = "urm_buttons1.png";
	public static final String VOLUME_BUTTONS = "volume_buttons1.png";
	public static final String MENU_BACKGROUND_IMG = "Background_0.png";
	public static final String PLAYING_BG_IMG = "Background_10.png";
	public static final String SMALL_CLOUD = "lilcloud.png";
	public static final String SKELLY_SPRITE = "skelly_sprite.png";
	public static final String SKELLY2_SPRITE = "skelly2_sprite.png";
	public static final String GOLEM_ATLAS = "Golem.png";
	public static final String STATUS_BAR = "health_power_bar.png";
	public static final String STATUS_BAR2 = "potionsCheck2.png";
	public static final String COMPLETED_IMG = "completed_sprite.png";
	public static final String POTION_ATLAS = "potions_sprites.png";
	public static final String CONTAINER_ATLAS = "objects_sprites.png";
	public static final String TRAP_ATLAS = "trap_atlas.png";
	public static final String CANNON_ATLAS = "cannon_atlas.png";
	public static final String CANNON_BALL = "ball.png";
	public static final String FIREBALL = "fireball.png";
	public static final String DEATH_SCREEN = "death_screen.png";
	public static final String OPTIONS_MENU = "pause_menu1.png";
	public static final String NPC_ATLAS = "npc.png";
	public static final String POTION_CHECK_BG = "lil_thing.png";
	public static final String BOSS_ATLAS = "boss.png";
	public static final String ORB = "glow_orb.png";
	public static final String BOSS_HEALTHBAR = "boss_health.png";
	public static final String WIZARD_DUCK = "wizard_duck.png";
	public static final String BG_LVL3 = "BgLvl3.png";

	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
		try {
			img = ImageIO.read(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}

	public static BufferedImage[] GetAllLevels(){
		URL url = LoadSave.class.getResource("/lvls");
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		File[] files= file.listFiles();
		File[] filesSorted = new File[files.length];

		for(int i=0;i< filesSorted.length;i++)
			for(int j=0;j< files.length;j++){
				if(files[j].getName().equals((i+1) + ".png"))
					filesSorted[i] = files[j];
			}
//		for(File f : files)
//			System.out.println("file" + f.getName());
		BufferedImage[] imgs =new BufferedImage[filesSorted.length];
		for(int i=0; i <imgs.length; i++){
			try {
				imgs[i] = ImageIO.read(filesSorted[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return imgs;
	}

}