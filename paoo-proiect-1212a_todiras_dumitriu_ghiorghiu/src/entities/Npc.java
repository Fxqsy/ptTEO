package entities;

import static utilz.Constants.EnemyConstants.*;

public class Npc extends Enemy {

    private boolean isInteracted = false;
    private String[] dialog=new String[]{
            "Buna, Vrajitorule. Esti pregatit sa mi te alaturi in aceasta aventura?",
            "",
            "",
            "Poti sari apasand butonul SPACE",
            "",
            "",
            "Pazea vrajitorule! O capcana se afla in fata ta.",
            "Ai grija sa nu cazi!" ,
            "Pentru a termina aventura trebuie sa colectezi potiunile magice.",
            "Poti ataca apasand CLICK STANGA sau Q.",
            "Daca te uiti in coltul stanga-jos, acolo iti poti vedea viata",
            "si energia de care ai nevoie pentru un atac la distanta.",
            "Pentru a termina nivelul trebuie",
            "sa ajungi la cripta sacra.",
            ""
    };

    public Npc(float x, float y) {
        super(x, y, DUCKY_WIDTH, DUCKY_HEIGHT, DUCKY);
        initHitbox(26, 26);
        this.state = IDLE_NPC;
    }

    public void update(int[][] lvlData, Player player) {
        updateBehavior(lvlData, player);
    }

    private void updateBehavior(int[][] lvlData, Player player) {
            updateAniTick();
    }

    public void setInteracted(boolean interacted) {
        this.isInteracted = interacted;
    }

    public boolean isInteracted() {
        return isInteracted;
    }

    @Override
    public void resetEnemy() {
       setInteracted(false);
    }

    public float getX(){
        return x;
    }

    public String[] getDialog(){
        return dialog;
    }



}