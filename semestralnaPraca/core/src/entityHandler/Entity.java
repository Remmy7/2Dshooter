
package entityHandler;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import WorldRender.Map;

import com.badlogic.gdx.math.Vector2;

/**
 *  Abstraktn· trieda entity. Vyjadruje, akÈ vlastnosti bude maù hr·Ë a nepriatelia,
 *  vytv·ra pre ne hitbox, uklad· si ich polohu na mape, urËuje im typ podæa enumu.
 *  UrËuje, ako sa bude pohybovaù na mape (nepriateæ nah·Úa hr·Ëa, hr·Ë sa h˝be 
 *  tlaËidlami WASD).
 *  Taktieû obsahuje metÛdy na pr·cu so ûivotom entity.
 * 
 */
public abstract class Entity {
    protected Vector2 pos;
    protected EntityType type;
    protected Map map;
    protected boolean enemy;
    protected Sprite sprite;
    protected Rectangle hitbox;
    
    protected int speed;
    protected float health;
    protected float maxHealth;
    protected int width;
    protected int height;
    /**
     * Konötruktor pre entitu, vyjadruje na akom mieste sa vytvorÌ, akÈho
     * je typu, a mapu na ktorej sa m· pohybovaù.
     * 
     */
    public Entity(float x, float y, EntityType type, Map map) {
       
        this.pos = new Vector2(x, y);
        this.type = type;
        this.map = map;
        this.speed = type.getSpeed();
        this.health = type.getHealth();
        this.maxHealth = health;
        this.width = type.getWidth();
        this.height = type.getHeight();
        this.hitbox = new Rectangle(x, y, this.width, this.height);
        
    }
    
    /**
     * MetÛda pre obnovovanie entity. 
     */
    public void update(float deltaTime, float speed) {             
          
    }
    
    /**
     * MetÛda pre nepriateæsk˙ entitu, ktor· nah·Úa hr·Ëa.
     */
    public void followPlayer(float x, float y, float deltaTime) {
        if (pos.x != x && pos.y != y) {
            if (pos.x < x) {
                moveX(this.speed * deltaTime);
            }
            if (pos.x > x) {
                moveX(-this.speed * deltaTime);
            }
            if (pos.y < y) {
                moveY(this.speed * deltaTime);
            }
            if (pos.y > y) {
                moveY(-this.speed * deltaTime);
            }
        }
        sprite.setPosition(pos.x, pos.y);
        hitbox.x = pos.x;
        hitbox.y = pos.y;
    }
    /**
     * Abstraktn· metÛda pre zmenenie hodnoty ûivota.
     */
    public abstract void updateHealth(float amount); 
    
    /**
     * Zv˝öenie maxim·lneho ûivota entity.
     */
    public void increaseMaxHealth(float amount) {
        if (this.maxHealth + amount > 0) {
            this.maxHealth += amount;
            this.health += amount;
        }
    }
    /**
     * Niekoækon·sobnÈ zv˝öenie ûivota entity.
     * @param amount 
     */
    public void increaseMaxHealthExponentially(float amount) {
        float increasedHealth = this.maxHealth;
        this.maxHealth *= amount;
        this.health += (this.maxHealth - increasedHealth);
    }
    /**
     * Vykreslovanie entity.
     *
     */
    public abstract void render (SpriteBatch batch);   
    
    
    /**
     * Zmena Xovej s˙radnice entity. Riadi sa kolÌziou so stenami okolo seba. 
     */
    protected float moveX(float amount) {
        float newX = pos.x + amount;
        if (!map.doesRectCollideWithMap(newX, pos.y, getWidth(), getHeight())) {
            this.pos.x = newX;
            this.hitbox.x = newX;
        }
        return this.pos.x;
    }
    /**
     * Vracia hitbox entity.
     *  
     */
    public Rectangle returnHitbox() {
        return this.hitbox;
    }
    
    /**
     * Zmena Yovej s˙radnice entity. Riadi sa kolÌziou so stenami okolo seba. 
     */
    protected float moveY(float amount) {
        float newY = pos.y + amount;
        if (!map.doesRectCollideWithMap(pos.x, newY, getWidth(), getHeight())) {
            this.pos.y = newY;
            this.hitbox.y = newY;
        }
        return this.pos.y;
    }   
    /**
     * Vracia vector2 pozÌciu entity.
     * 
     */
    public Vector2 getPos() {
        return pos;
    }
    
    /**
     * Vracia Xkoordin·t spritu.
     * 
     */
    public float getX() {
        return pos.x;
    }
    
    /**
     * Vracia Ykoordin·t spritu. 
     */
    public float getY() {
        return pos.y;
    }
    
    /**
     * Vracia Xkoordin·t hitboxu.
     * 
     */
    public float getHitboxX() {
        return hitbox.x;
    }
    
    /**
     * Vracia Ykoordin·t hitboxu.
     * 
     */
    public float getHitboxY() {
        return hitbox.y;
    }
    
    /**
     * Vracia öÌrku entity.
     *
     */
    public int getWidth() {
        return this.width;
    }
    
    /**
     * Vracia v˝öku entity.
     * @return 
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * MenÌ veækosù entity n·sobkom vstupu.
     * 
     */
    public void setDimensions(float amount) {
        this.width *= amount;
        this.height *= amount;
        sprite.setSize(this.width, this.height);
    }
    
    /**
     * Getter na ûivot.
     * 
     */
    public float getHealth() {
        return this.health;
    }
    
    /**
     * Getter na maxim·lny ûivot.
     * 
     */
    public float getMaxHealth() {
        return this.maxHealth;
    }

    /**
     * Getter na enum typ entity.
     * 
     */
    public EntityType getType() {
        return type;
    }
    
    /**
     * Zmena pozÌcie entity.
     * 
     */
    public void setPosition(float x, float y) {
        pos.x = x;
        pos.y = y;       
        hitbox.x = x;
        hitbox.y = y;
        sprite.setPosition(x, y);
    }

    
    /**
     * Boolean Ëi je entita nepriateæ.
     * 
     */
    public boolean isEnemy() {
        return this.enemy;
    }
    
    
}
 