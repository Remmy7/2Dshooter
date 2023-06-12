
package entityHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import WorldRender.Map;

/**
 * Potomok abstraktnej triedy Entity.
 * Je to trieda hráèa, ktorá ho rozširuje o enumový typ, sprite a iný druh
 * pohybu oproti triede nepriate¾a.
 * @author micha
 */
public class Player extends Entity {
    /**
     * Konštruktor pre hráèa, ktorý preberá vlastnosti od enumu. 
     */
    public Player(float x, float y, Map map) {
        super(x, y, EntityType.PLAYER, map);
        this.enemy = false;
        
        sprite = new Sprite(new Texture("sprites/playerSprite.png"));
        this.sprite.setSize(this.width, this.height);
        this.sprite.setPosition(pos.x, pos.y);      
    }

    /**
     * Obnovovacia metóda pre hráèa. Urèuje jeho pohyb pod¾a stlaèenia klávesnice.
     */
    @Override
    public void update(float deltaTime, float speed) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) 
            sprite.setPosition(moveX(-this.speed * deltaTime), pos.y);
            
        if (Gdx.input.isKeyPressed(Input.Keys.D)) 
            sprite.setPosition(moveX(this.speed * deltaTime), pos.y);
            
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            sprite.setPosition(pos.x, moveY(this.speed * deltaTime));
            
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            sprite.setPosition(pos.x, moveY(-this.speed * deltaTime));
        
        super.update(deltaTime, speed);         
    }

    /**
     * Vykreslovacia metóda.
     * @param batch 
     */
    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
   }

    /**
     * Metóda na zmenenie života hráèa.
     * Kontroluje, èi momentálny život nie je vyšší ako maximálny.
     */
    @Override
    public void updateHealth(float amount) {
        this.health += amount;
        if (this.health > this.maxHealth)
            this.health = this.maxHealth;       
    }    
}
