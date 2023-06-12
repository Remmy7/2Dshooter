
package WorldRender;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Potomok mapy. Vytvára inštanciu mapy a pridáva do nej políèka pod¾a predurèenej mapy.
 * Taktiež vytvára spawnpointy pre hráèa a nepriate¾ov na každú mapu osobitne.
 * 
 */
public class TiledGameMap extends Map {
    
    
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer mapRender; 
    /**
     * Vytvorenie mapy.
     */
    public TiledGameMap(int mapID) {
       
        this.changeMap(mapID);
        
        
    }
    /**
     * Zmena mapy pod¾a ID mapy, používa switch pre zmenu. Defaultná hodnota je
     * nekoneèná hra na druhej mape.
     */
    public void changeMap(int mapID) {
        switch(mapID) {
            case 1:
                tiledMap = null;
                tiledMap = new TmxMapLoader().load("mapAssets/finalMap1.tmx");
                mapRender = null;
                mapRender = new OrthogonalTiledMapRenderer(tiledMap);
                this.setSpawnPoints(mapID);                
                break;
            case 2:
                tiledMap = null;
                tiledMap = new TmxMapLoader().load("mapAssets/finalMap2.tmx");
                mapRender = null;
                mapRender = new OrthogonalTiledMapRenderer(tiledMap);
                this.setSpawnPoints(mapID);               
                break;
            default:
                tiledMap = null;
                tiledMap = new TmxMapLoader().load("mapAssets/finalMap2.tmx");
                mapRender = null;
                mapRender = new OrthogonalTiledMapRenderer(tiledMap);
                this.setSpawnPoints(mapID);  
                setEndless(true);
                break;
            
        }
    }
    /**
     * Ukladá súradnice pre vytváranie hráèa a nepriate¾ov na mape.
     * 
     */
    private void setSpawnPoints(int level) {
       
        if (this.mapSpawnPoint1 == null)
            this.mapSpawnPoint1 = new Vector2(1000, 1000);
        if (this.mapSpawnPoint2 == null)
            this.mapSpawnPoint2 = new Vector2(1000, 1000);
        if (this.mapSpawnPoint3 == null)
            this.mapSpawnPoint3 = new Vector2(1000, 1000);
        if (this.mapSpawnPoint4 == null)
            this.mapSpawnPoint4 = new Vector2(1000, 1000);
        if (this.mapSpawnPoint5 == null)
            this.mapSpawnPoint5 = new Vector2(1000, 1000);
        if (this.mapSpawnPointPlayer == null)
            this.mapSpawnPointPlayer = new Vector2(1000, 1000);
        
        switch (level) {
            case 1:
                this.mapSpawnPoint1.set(112, 180);
                this.mapSpawnPoint2.set(112, 384);
                this.mapSpawnPoint3.set(480, 180);
                this.mapSpawnPoint4.set(800, 580);
                this.mapSpawnPoint5.set(1184, 400);
                this.mapSpawnPointPlayer.set(580, 300);
                break;
            default:
                this.mapSpawnPoint1.set(112, 180);
                this.mapSpawnPoint2.set(112, 384);
                this.mapSpawnPoint3.set(480, 180);
                this.mapSpawnPoint4.set(800, 480);
                this.mapSpawnPoint5.set(1184, 400);
                this.mapSpawnPointPlayer.set(480, 300);
                break;
        }
    }
    
    /**
     * Vykreslovanie mapy, uloženie kamery.
     * 
     */
    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        mapRender.setView(camera);
        mapRender.render();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.render(camera, batch);
        batch.end();
    }
    

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }

    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    @Override
    public TileType getTileTypeOnCoordinates(int layer, int x, int y) {
        Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(x, y);
        
        
        if (cell != null) {
            TiledMapTile tile = cell.getTile();
            
            if (tile != null) {
                int id = tile.getId();
                return TileType.returnTileTypeWithID(id);
            }
        }
        
        
        return null;
    }

    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }

}
