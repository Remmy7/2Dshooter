
package WorldRender;

import entityHandler.EnemySpawner;
import entityHandler.Entity;
import entityHandler.EntityType;
import entityHandler.Player;
import entityHandler.Projectile;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
 

/**
 * Abstraktn� trieda pre vytv�ranie mapy. V tejto triede 
 * prebiehaj� v�etky d�le�it� interakcie na mape.
 * @author micha
 */
public abstract class Map {
    
    protected ArrayList<Entity> entities;
    protected ArrayList<Projectile> playerShots;
    protected Vector2 mapSpawnPointPlayer, mapSpawnPoint1, mapSpawnPoint2, mapSpawnPoint3, mapSpawnPoint4, mapSpawnPoint5;
    private long lastSpawnTime = 0;
    private long lastBulletTime1 = 0;
    private long lastBulletTime2 = 0;
    private long lastStoodOnLava = 0;
    private Sound playerDamaged;
    private Sound enemyDamaged;
    private Sound enemyDead;
    private Sound playerDead;
    private BitmapFont font;
    private int enemiesKilled;
    private int maxKillCount;
    private boolean nextLevel;
    private boolean endless;
    
    /**
     * kon�turktor pre mapu, inicializuje ArrayListy entit�t a striel hr��a pre 
     * �al�iu interakciu. Taktie� inicializuje zvukov� efekty.
     */
    public Map() {       
        this.entities = new ArrayList<Entity>();
        this.playerShots = new ArrayList<Projectile>();
        this.enemiesKilled = 0;
        font = new BitmapFont();
        this.nextLevel = false;
        this.maxKillCount = 5;  
        
        playerDamaged = Gdx.audio.newSound(Gdx.files.internal("audio/playerHurt.mp3"));
        playerDead = Gdx.audio.newSound(Gdx.files.internal("audio/playerDeath.ogg"));
        enemyDamaged = Gdx.audio.newSound(Gdx.files.internal("audio/enemyDamaged.ogg"));
        enemyDead = Gdx.audio.newSound(Gdx.files.internal("audio/enemyDeath.ogg"));
        
    }
    /**
     * Renderovanie entit�t a projektilov. Vypisuje aj moment�lny �ivot hr��a, ko�ko zabil nepriate�ov
     * a ko�ko mu e�te na zabitie ost�va.
     * 
     */
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);            
        }
        for (Projectile projectile : this.playerShots) {
            projectile.render(batch);
        }
        
       
        if (!entities.isEmpty())   //kontrola pre prvotn� spustenie na pred�jdenie spadnutia hry
            font.draw(batch, "Player Health: " + entities.get(0).getHealth(), 0, 650);
        font.draw(batch, "Enemies Killed: " + this.enemiesKilled, 0, 630);
        if (!getEndless())  // ak je vybran� nekone�n� hra
            font.draw(batch, "Enemies Remaining: " + (this.maxKillCount - this.enemiesKilled), 0, 610);
        
    } 
    /**
     * Met�da, ktor� preberie poz�ciu my�e a hr��a, a n�sledovne vystrel� projektil.
     * 
     */
    public void shootProjectile(boolean input) {
        this.playerShots.add(new Projectile(entities.get(0).getPos(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), input));
    }
    /**
     * Met�da kontroluj�ca predpripraven� mapu a koordin�ty danej entity.
     * Ak m� pol��ko na mape hodnotu true pre collidable, znamen� to �e sa na to pol��ko
     * nem��e entita pohn��.
     */
    public boolean doesRectCollideWithMap(float x, float y, int width, int height) {
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }
        for (int i = (int) (y / TileType.TILE_SIZE); i < Math.ceil((y + height) / TileType.TILE_SIZE); i++) { // math.ceil vzdy zaokruhluje nahor
            for (int j = (int) (x / TileType.TILE_SIZE); j < Math.ceil((x + width) / TileType.TILE_SIZE); j++) {
                for (int layer = 0; layer < getLayers(); layer++) {
                    TileType type = getTileTypeOnCoordinates(layer, j, i);
                    if (type != null && type.isCollidable()) {
                        return true;
                    }
                }
            }
        }
                
        return false;
    }
    /**
     * Hr�� m��e dost�va� po�kodenie od l�vy, podobn� met�da ako t� predo�l� len tu rie�ime l�vu.
     * 
     */
    public boolean doesStandOnLava() {
        for (int i = (int) (entities.get(0).getHitboxY() / TileType.TILE_SIZE); i < Math.ceil((entities.get(0).getHitboxY() + entities.get(0).getHeight()) / TileType.TILE_SIZE); i++) { // math.ceil vzdy zaokruhluje nahor
            for (int j = (int) (entities.get(0).getHitboxX() / TileType.TILE_SIZE); j < Math.ceil((entities.get(0).getHitboxX() + entities.get(0).getWidth()) / TileType.TILE_SIZE); j++) {
                for (int layer = 0; layer < getLayers(); layer++) {
                    TileType type = getTileTypeOnCoordinates(layer, j, i);
                    if (type != null && type.isLava() && TimeUtils.nanoTime() - lastStoodOnLava > 480000000) {
                        playerDamaged.play(1.0f);
                        this.entities.get(0).updateHealth(-25);
                        this.lastStoodOnLava = TimeUtils.nanoTime();
                        return true;                        
                    }
                }
            }
        }
        return false;
    }
    /**
     * Hlavn� obnovovacia met�da. Zaru�uje chod hry, opakuje sa pod�a
     * obnovovacej r�chlosti programu. Kontroluje �i boli 
     * nepriatelia zasiahnut� strelou, �i bol hr�� zasiahnut� nepriate�om, 
     * �i sa hr�� pohybuje, striela, stoj� na l�ve alebo umiera. 
     * Taktie� prehr�va zvukov� efekty s dan�mi in�tanciami, 
     * a vytv�ra nov�ch nepriate�ov v ur�it�ch n�hodn�ch intervaloch. 
     * Kontroluje, �i hr�� splnil po�iadavky prejdenia do �al�ieho levelu, alebo �i je v nekone�nom m�de.
     * 
     */
    public void update(float delta) {
        // t�to �as� prejde cez arraylisty ent�t a striel, aby zistila, �i sa niektor�
        // neprekr�vaj�, nech ich vyma�e a zahr� zvuk.
        for (int i = 0; i < this.playerShots.size(); i++) {  
            if (this.playerShots.size() > 0) {
                this.playerShots.get(i).update();
                if (this.entities.size() > 1) {
                    for (int j = 1; j < this.entities.size(); j++) {            
                        if (this.playerShots.get(i).returnHitbox().overlaps(this.entities.get(j).returnHitbox()) && this.entities.get(j).isEnemy()) { 
                                this.entities.get(j).updateHealth(-this.playerShots.get(i).returnDamage());
                                enemyDamaged.play(1.0f);
                            if (this.entities.get(j).getHealth() < 0) {
                                this.entities.remove(j);   
                                enemyDead.play(1.0f);
                                enemiesKilled++;
                                if (j > 0)
                                    j--;
                            }
                            if (!this.playerShots.get(i).isPiercing()) {                       
                                this.playerShots.remove(i); 
                                if (i > 0)
                                    i--;
                            }
                        } 
                        if (i > 0)
                        if (doesRectCollideWithMap((float) this.playerShots.get(i).returnHitbox().x, (float) this.playerShots.get(i).returnHitbox().y,
                                (int) this.playerShots.get(i).returnWidth(), (int) this.playerShots.get(i).returnHeight())) {
                            this.playerShots.remove(i);
                            i--;
                        }
                            

                        
                    }
                }
            }
        }   
        // kontrola, �i enemy nezasiahol hr��a.
        for (int j = 1; j < this.entities.size(); j++) {       
            if (this.entities.get(0).returnHitbox().overlaps(this.entities.get(j).returnHitbox()) && this.entities.get(j).isEnemy()) {
                this.entities.get(0).updateHealth(-50);
                System.out.println("Player health: " + this.entities.get(0).getHealth());
                this.entities.remove(j);
                j--;
                playerDamaged.play(1.0f);               
            }
        }
        
        
        
        
        // kontrola, �i n�hodou hr�� nie je m�tvy.
        if (entities.isEmpty()) {
            this.entities.add(new Player(mapSpawnPointPlayer.x, mapSpawnPointPlayer.y, this));
            this.enemiesKilled = 0;
        }
        
        
        
        
        // lav� klik pre strelu
        if (Gdx.input.isButtonPressed(Buttons.LEFT) && TimeUtils.nanoTime() - lastBulletTime1 > 200000000) {
            this.shootProjectile(false);
            this.lastBulletTime1 = TimeUtils.nanoTime();
        }
        // prav� klik pre strelu
        if (Gdx.input.isButtonPressed(Buttons.RIGHT) && TimeUtils.nanoTime() - lastBulletTime2 > 700000000) {
            this.shootProjectile(true);
            this.lastBulletTime2 = TimeUtils.nanoTime();
        }
        
        
        
        // vol� met�du update pre v�etky entity
        for (int i = 0; i < this.entities.size(); i++) {
            entities.get(i).update(delta, 1);
            
            if (i > 0) {
                entities.get(i).followPlayer(this.entities.get(0).getX(),this.entities.get(0).getY(), delta);
            }
        }
        
       // spawn pre nepriate�ov
        if (TimeUtils.nanoTime() - lastSpawnTime > 450000000) {
            int spawnPoint = MathUtils.random(1, 5);
            int difficulty = MathUtils.random(1, 3);
            int size = MathUtils.random(1, 2);
            if (spawnPoint == 1)
                entities.add(new EnemySpawner(mapSpawnPoint1.x, mapSpawnPoint1.y, EntityType.ENEMY_1, this, difficulty, size));
            if (spawnPoint == 2)
                entities.add(new EnemySpawner(mapSpawnPoint2.x, mapSpawnPoint2.y, EntityType.ENEMY_1, this, difficulty, size));
            if (spawnPoint == 3)
                entities.add(new EnemySpawner(mapSpawnPoint3.x, mapSpawnPoint3.y, EntityType.ENEMY_1, this, difficulty, size));
            if (spawnPoint == 4)
                entities.add(new EnemySpawner(mapSpawnPoint4.x, mapSpawnPoint4.y, EntityType.ENEMY_1, this, difficulty, size));
            if (spawnPoint == 5)
                entities.add(new EnemySpawner(mapSpawnPoint5.x, mapSpawnPoint5.y, EntityType.ENEMY_1, this, difficulty, size));
            
            this.lastSpawnTime = TimeUtils.nanoTime();
        }
        this.doesStandOnLava(); // kontrola �i stoj� na l�ve
        if (this.entities.get(0).getHealth() <= 0) { // kontrola �i hr�� neumrel
            playerDead.play(1.0f);
            entities.clear();
            this.enemiesKilled = 0;
        }
        if (endless) { // kontrola �i nie je nekone�n� hra
            this.maxKillCount = Integer.MAX_VALUE / 2;
        }
        if (enemiesKilled == maxKillCount) { // kontrola, �i hr�� splnil po�iadavky na �al�� level
            this.nextLevel = true;
            this.enemiesKilled = 0;
            entities.subList(1, entities.size()).clear();
            entities.get(0).setPosition(mapSpawnPointPlayer.x, mapSpawnPointPlayer.y);
        }
        
        
    }
    /**
     * abstraktn� met�da pre menenie mapy.
    */
    public abstract void changeMap(int mapID);
    /**
     * libgdx uklad� v�etko do RAM pam�te, dispose met�da ich vyma�e z danej pam�te.
     */
    public abstract void dispose();
    /**
     * abstraktn� met�da pre vr�tenie ��rky mapy.
     * 
     */
    public abstract int getWidth();
    /**
     * abstraktn� met�da pre vr�tenie v��ky mapy.
     * 
     */
    public abstract int getHeight();
    /**
     * abstraktn� met�da pre vr�tenie vrstvy mapy.
     * 
     */
    public abstract int getLayers();
    /**
     * abstraktn� met�da pre vr�tenie typu pol��ka na dan�ch s�radniciach a vrstve.
     * 
     */
    public abstract TileType getTileTypeOnCoordinates(int layer, int x, int y);    
    /**
     * met�da pre vr�tenie typu pol��ka z danej vrstvy, dan� indexom pol��ka, nie s�radnicami.
     * 
     */
    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeOnCoordinates(layer, (int) (x / TileType.getTILE_SIZE()), (int) (y / TileType.getTILE_SIZE()));
    }
    
    /**
     * met�da pre vr�tenie ��rky pol��ka.
     *  
     */
    private float getPixelWidth() {
        return this.getWidth() * TileType.getTILE_SIZE();
    }
    
    /**
     * met�da pre vr�tenie ��rky pol��ka.
     *  
     */
    private float getPixelHeight() {
        return this.getHeight() * TileType.getTILE_SIZE();
    }
    
    /**
     * met�da pre vr�tenie true/false hodnoty, �i sa m� meni� level.
     *  
     */
    public boolean getNextLevel() {
        return this.nextLevel;
    }
    
    /**
     * met�da pre zmenenie levelu.
     *  
     */
    public void setNextLevel(boolean value) {
        this.nextLevel = value;
    }
    
    /**
     * met�da pre zistenie povolenia nekone�nej hry.
     *  
     */
    public boolean getEndless() {
        return this.endless;
    }
    
    /**
     * met�da pre povolenie nekone�nej hry.
     *  
     */
    public void setEndless(boolean value) {
        this.endless = value;
    }
}
