
package entityHandler;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import WorldRender.Map;

/**
 * Potomok entity. Vytvára inštancie nepriate¾ov, pridáva im sprite a mení ich
 * konštanty pod¾a vstupu a enumu.
 * 
 */
public class EnemySpawner extends Entity {
    public EnemySpawner(float x, float y, EntityType type, Map map, 
            float difficulty, float setDimension) {
        super(x, y, type, map); //xd
        this.sprite = new Sprite(new Texture("sprites/enemy.png"));
        this.setDimensions(setDimension);
        this.increaseMaxHealthExponentially(difficulty);
        this.sprite.setSize(this.width, this.height);
        this.sprite.setPosition(pos.x, pos.y);
        this.enemy = true;
    }
    /**
     * Vykreslovanie.
     * 
     */
    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    
    /**
     * Obnova pre daného nepriate¾a
     */
    @Override
    public void update(float deltatime, float speed) {
        super.update(deltatime, speed);
        
    }

    /**
     * Zmena hodnoty života, kontroluje èi maximálny život nie je menší ako momentálny
     */
    @Override
    public void updateHealth(float amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;   
        }
    }    
}
