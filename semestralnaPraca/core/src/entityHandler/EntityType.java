
package entityHandler;

/**
 *
 * Enun pre typ entity. Udriava si jej ID, šírku, vıšku, rıchlos a ivot.
 */
public enum EntityType {
    PLAYER("player", 46, 46, 300, 1000),
    ENEMY_1("enemy1", 30, 30, 100, 100);
    
    private String id;
    private int width, height, speed;
    private float health;

    /**
     * Konštruktor enumu. 
     * 
     */
    private EntityType(String id, int width, int height, int speed, float health) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.health = health;
    }
    /**
     * Getter pre ID.
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * Getter pre rıchlos.
     */
    public int getSpeed() {
        return this.speed;
    }
    
    /**
     * Getter pre ivot.
     */
    public float getHealth() {
        return this.health;
    }

    /**
     * Getter pre šírku.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter pre vıšku.
     */
    public int getHeight() {
        return height;
    }   
}
