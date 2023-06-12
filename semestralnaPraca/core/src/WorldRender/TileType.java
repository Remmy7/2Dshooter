
package WorldRender;

/**
 * Enum, ktor� uklad� typy pol��ok. Dr�� si ve�kos� pol��ka, jeho ID,
 * �i sa po �om d� prech�dza�, a ak� m� meno.
 * @author micha
 */
public enum TileType {
    
    PATH(1, false, "Path"),
    WALL(2, true, "Wall"),
    LAVA(3, false, "Lava");
    public static final int TILE_SIZE = 16;
    
    private int mapID;
    private boolean collidable;
    private String name;
    
    /**
     * Kon�truktor enumu.
     */
    private TileType(int mapID, boolean collidable, String name) {  
        this.mapID = mapID;
        this.collidable = collidable;
        this.name = name;       
    }
    /**
     * Getter pre ve�kos� pol��ka.
     */
    public static int getTILE_SIZE() {
        return TILE_SIZE;
    }
    
    /**
     * Getter pre ID pol��ka.
     */
    public int getMapID() {
        return mapID;
    }

    /**
     * Getter pre prechodnos� pol��ka.
     */
    public boolean isCollidable() {
        return collidable;
    }
    
    
    /**
     * Getter pre meno pol��ka.
     */
    public String getName() {
        return name;
    }    
    
    /**
     * Switch pre vr�tenie typu pol��ka.
     */
    public static TileType returnTileTypeWithID(int id) {
        switch(id) {
            
            case 1:
                return TileType.PATH;
            case 2:
                return TileType.WALL;  
            case 3:
                return TileType.LAVA;
            default:
                System.out.println("couldn't find tiletype in enum");
                return null;
        }
    
    }   

    public boolean isLava() {
        return (this.mapID == 3);
    }
}
