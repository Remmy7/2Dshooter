
package entityHandler;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import WorldRender.Map;

/**
 * Potomok entity. Vytv�ra in�tancie nepriate�ov, prid�va im sprite a men� ich
 * kon�tanty pod�a vstupu a enumu.
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
     * Obnova pre dan�ho nepriate�a
     */
    @Override
    public void update(float deltatime, float speed) {
        super.update(deltatime, speed);
        
    }

    /**
     * Zmena hodnoty �ivota, kontroluje �i maxim�lny �ivot nie je men�� ako moment�lny
     */
    @Override
    public void updateHealth(float amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;   
        }
    }    
}
