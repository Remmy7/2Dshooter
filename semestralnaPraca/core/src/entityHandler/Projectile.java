
package entityHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 *  Trieda pre projektily. Rie�i kde sa vytvor� projektil, kam p�jde, ak� je
 * jeho typ, r�chlos� at�.
 * 
 */
public class Projectile {
    
    private Rectangle hitbox;
    private static final float velocity = 5;
    private static final int damage = 100;
    private float velocityX, velocityY, angle;
    private Sprite sprite;
    private boolean piercing;
    
    /**
     * Kon�truktor pre projektil. Rie�i jeho �tartovn� poz�ciu (hr��) a jeho smer (kurzor).
     * Vytv�ra ��rku a d�ku projektilu pod�a typu kliknutia, vypo��ta uhol
     * ktor�m p�jde dan� projektil a v�po�tom mu zad� hodnotu aby plynulo
     * prech�dzal cez mapu.
     */
    public Projectile(Vector2 start, float mouseX, float mouseY,
            boolean piercingProjectile) {
        if (piercingProjectile == true) {
            this.piercing = true;
            this.hitbox = new Rectangle(start.x + 23, start.y + 23, 6, 6);
            sprite = new Sprite(new Texture("sprites/projectile1.png"));
            sprite.setPosition(hitbox.x, hitbox.y);
            sprite.setSize(6, 6);
        } else {
            this.piercing = false;
            this.hitbox = new Rectangle(start.x + 23, start.y + 23, 12, 6);
            sprite = new Sprite(new Texture("sprites/projectile1.png"));
            sprite.setPosition(hitbox.x, hitbox.y);
            sprite.setSize(12, 6);
        }       
        angle = MathUtils.atan2(mouseY - start.y - 23, mouseX - start.x - 23);
        
        velocityX = (float) Math.cos(angle) * 125 * Gdx.graphics.getDeltaTime();
        velocityY = (float) Math.sin(angle) * 125 * Gdx.graphics.getDeltaTime();
        
        sprite.setRotation(angle);
        
      
    }
  
    /**
     * Met�da na pohyb projektilu pod�a jeho r�chlosti a uhlu ktor�m sa pohybuje.
     */
    public void moveProjectile() {
        this.hitbox.x += velocityX * velocity;        
        this.hitbox.y += velocityY * velocity;
        sprite.setPosition(hitbox.x, hitbox.y);
        
    }
    
    /**
     * Vracia, �i projektil prech�dza cez viacero nepriate�ov.
     */
    public boolean isPiercing() {
        return this.piercing;
    }
    /**
     * Vracia hitbox projektilu.
     * 
     */
    public Rectangle returnHitbox() {
        return this.hitbox;
    }
    
    /**
     * Vracia po�kodenie projektilu.
     * 
     */
    public int returnDamage() {
        return this.damage;
    }
    
    /**
     * Vracia sprite projektilu.
     */
    public Sprite returnSprite() {
        return this.sprite;
    }
    
    /**
     * Vracia ��rku projektilu.
     *  
     */
    public float returnWidth() {
        return sprite.getWidth();
    }
    
    /**
     * Vracia v��ku projektilu.
     *  
     */
    public float returnHeight() {
        return sprite.getHeight();
    }
    
    /**
     * Kresl� projektil.
     *  
     */
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    /**
     * Obnovuje projektil.
     *  
     */
    public void update() {
        this.moveProjectile();
    }
}
