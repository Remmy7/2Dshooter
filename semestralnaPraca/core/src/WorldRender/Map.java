
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
 * Abstraktn· trieda pre vytv·ranie mapy. V tejto triede 
 * prebiehaj˙ vöetky dÙleûitÈ interakcie na mape.
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
     * konöturktor pre mapu, inicializuje ArrayListy entitÌt a striel hr·Ëa pre 
     * Ôalöiu interakciu. Taktieû inicializuje zvukovÈ efekty.
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
     * Renderovanie entitÌt a projektilov. Vypisuje aj moment·lny ûivot hr·Ëa, koæko zabil nepriateæov
     * a koæko mu eöte na zabitie ost·va.
     * 
     */
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);            
        }
        for (Projectile projectile : this.playerShots) {
            projectile.render(batch);
        }
        
       
        if (!entities.isEmpty())   //kontrola pre prvotnÈ spustenie na predÛjdenie spadnutia hry
            font.draw(batch, "Player Health: " + entities.get(0).getHealth(), 0, 650);
        font.draw(batch, "Enemies Killed: " + this.enemiesKilled, 0, 630);
        if (!getEndless())  // ak je vybran· nekoneËn· hra
            font.draw(batch, "Enemies Remaining: " + (this.maxKillCount - this.enemiesKilled), 0, 610);
        
    } 
    /**
     * MetÛda, ktor· preberie pozÌciu myöe a hr·Ëa, a n·sledovne vystrelÌ projektil.
     * 
     */
    public void shootProjectile(boolean input) {
        this.playerShots.add(new Projectile(entities.get(0).getPos(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), input));
    }
    /**
     * MetÛda kontroluj˙ca predpripraven˙ mapu a koordin·ty danej entity.
     * Ak m· polÌËko na mape hodnotu true pre collidable, znamen· to ûe sa na to polÌËko
     * nemÙûe entita pohn˙ù.
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
     * Hr·Ë mÙûe dost·vaù poökodenie od l·vy, podobn· metÛda ako t· predoöl· len tu rieöime l·vu.
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
     * Hlavn· obnovovacia metÛda. ZaruËuje chod hry, opakuje sa podæa
     * obnovovacej r˝chlosti programu. Kontroluje Ëi boli 
     * nepriatelia zasiahnut˝ strelou, Ëi bol hr·Ë zasiahnut˝ nepriateæom, 
     * Ëi sa hr·Ë pohybuje, striela, stojÌ na l·ve alebo umiera. 
     * Taktieû prehr·va zvukovÈ efekty s dan˝mi inötanciami, 
     * a vytv·ra nov˝ch nepriateæov v urËit˝ch n·hodn˝ch intervaloch. 
     * Kontroluje, Ëi hr·Ë splnil poûiadavky prejdenia do Ôalöieho levelu, alebo Ëi je v nekoneËnom mÛde.
     * 
     */
    public void update(float delta) {
        // t·to Ëasù prejde cez arraylisty entÌt a striel, aby zistila, Ëi sa niektorÈ
        // neprekr˝vaj˙, nech ich vymaûe a zahr· zvuk.
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
        // kontrola, Ëi enemy nezasiahol hr·Ëa.
        for (int j = 1; j < this.entities.size(); j++) {       
            if (this.entities.get(0).returnHitbox().overlaps(this.entities.get(j).returnHitbox()) && this.entities.get(j).isEnemy()) {
                this.entities.get(0).updateHealth(-50);
                System.out.println("Player health: " + this.entities.get(0).getHealth());
                this.entities.remove(j);
                j--;
                playerDamaged.play(1.0f);               
            }
        }
        
        
        
        
        // kontrola, Ëi n·hodou hr·Ë nie je m‡tvy.
        if (entities.isEmpty()) {
            this.entities.add(new Player(mapSpawnPointPlayer.x, mapSpawnPointPlayer.y, this));
            this.enemiesKilled = 0;
        }
        
        
        
        
        // lav˝ klik pre strelu
        if (Gdx.input.isButtonPressed(Buttons.LEFT) && TimeUtils.nanoTime() - lastBulletTime1 > 200000000) {
            this.shootProjectile(false);
            this.lastBulletTime1 = TimeUtils.nanoTime();
        }
        // prav˝ klik pre strelu
        if (Gdx.input.isButtonPressed(Buttons.RIGHT) && TimeUtils.nanoTime() - lastBulletTime2 > 700000000) {
            this.shootProjectile(true);
            this.lastBulletTime2 = TimeUtils.nanoTime();
        }
        
        
        
        // vol· metÛdu update pre vöetky entity
        for (int i = 0; i < this.entities.size(); i++) {
            entities.get(i).update(delta, 1);
            
            if (i > 0) {
                entities.get(i).followPlayer(this.entities.get(0).getX(),this.entities.get(0).getY(), delta);
            }
        }
        
       // spawn pre nepriateæov
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
        this.doesStandOnLava(); // kontrola Ëi stojÌ na l·ve
        if (this.entities.get(0).getHealth() <= 0) { // kontrola Ëi hr·Ë neumrel
            playerDead.play(1.0f);
            entities.clear();
            this.enemiesKilled = 0;
        }
        if (endless) { // kontrola Ëi nie je nekoneËn· hra
            this.maxKillCount = Integer.MAX_VALUE / 2;
        }
        if (enemiesKilled == maxKillCount) { // kontrola, Ëi hr·Ë splnil poûiadavky na ÔalöÌ level
            this.nextLevel = true;
            this.enemiesKilled = 0;
            entities.subList(1, entities.size()).clear();
            entities.get(0).setPosition(mapSpawnPointPlayer.x, mapSpawnPointPlayer.y);
        }
        
        
    }
    /**
     * abstraktn· metÛda pre menenie mapy.
    */
    public abstract void changeMap(int mapID);
    /**
     * libgdx uklad· vöetko do RAM pam‰te, dispose metÛda ich vymaûe z danej pam‰te.
     */
    public abstract void dispose();
    /**
     * abstraktn· metÛda pre vr·tenie öÌrky mapy.
     * 
     */
    public abstract int getWidth();
    /**
     * abstraktn· metÛda pre vr·tenie v˝öky mapy.
     * 
     */
    public abstract int getHeight();
    /**
     * abstraktn· metÛda pre vr·tenie vrstvy mapy.
     * 
     */
    public abstract int getLayers();
    /**
     * abstraktn· metÛda pre vr·tenie typu polÌËka na dan˝ch s˙radniciach a vrstve.
     * 
     */
    public abstract TileType getTileTypeOnCoordinates(int layer, int x, int y);    
    /**
     * metÛda pre vr·tenie typu polÌËka z danej vrstvy, dan· indexom polÌËka, nie s˙radnicami.
     * 
     */
    public TileType getTileTypeByLocation(int layer, float x, float y) {
        return this.getTileTypeOnCoordinates(layer, (int) (x / TileType.getTILE_SIZE()), (int) (y / TileType.getTILE_SIZE()));
    }
    
    /**
     * metÛda pre vr·tenie öÌrky polÌËka.
     *  
     */
    private float getPixelWidth() {
        return this.getWidth() * TileType.getTILE_SIZE();
    }
    
    /**
     * metÛda pre vr·tenie öÌrky polÌËka.
     *  
     */
    private float getPixelHeight() {
        return this.getHeight() * TileType.getTILE_SIZE();
    }
    
    /**
     * metÛda pre vr·tenie true/false hodnoty, Ëi sa m· meniù level.
     *  
     */
    public boolean getNextLevel() {
        return this.nextLevel;
    }
    
    /**
     * metÛda pre zmenenie levelu.
     *  
     */
    public void setNextLevel(boolean value) {
        this.nextLevel = value;
    }
    
    /**
     * metÛda pre zistenie povolenia nekoneËnej hry.
     *  
     */
    public boolean getEndless() {
        return this.endless;
    }
    
    /**
     * metÛda pre povolenie nekoneËnej hry.
     *  
     */
    public void setEndless(boolean value) {
        this.endless = value;
    }
}
