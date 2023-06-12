package semestralnapraca;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;



import WorldRender.Map;
import WorldRender.TiledGameMap;


/**
 * Vytvorenie hry, preberá mapu a renderuje na òu.
 */
public class Hra extends ApplicationAdapter {

    SpriteBatch batch;

    OrthographicCamera camera;
    private int level;
    private boolean infinite;

    Map gameMap;

    /**
     * Metóda pre inicializáciu. kamery, vykreslovania, mapy.
     */
    @Override
    public void create () {
            batch = new SpriteBatch();
            this.level = 1;
            infinite = false;
            camera = new OrthographicCamera();
            camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.update();

            gameMap = new TiledGameMap(level);


    }

    /**
     * Metóda pre opakované renderovanie zmien na mape.
     */
    @Override
    public void render () {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.enableBlending();


            camera.update();
            if (gameMap.getNextLevel()) {
                this.nextLevel();
                gameMap.setNextLevel(false);
            }

            gameMap.render(camera, batch);
            gameMap.update(Gdx.graphics.getDeltaTime());                
    }

    /**
     * 
     * Metóda pre vymazanie inštancií z pamäte.
     */
    @Override
    public void dispose () {
            batch.dispose();

    }
    /**
     * Metóda na zmenu levelu pod¾a parametra.
     * 
     */
    private void changeLevel(int level) {
        this.level = level;
        this.gameMap.changeMap(level);
    }
    /**
     * Metóda pre zmenenie levelu o 1 nahor.
     */
    private void nextLevel() {
        this.level++;
        this.gameMap.changeMap(level);
    }
}
