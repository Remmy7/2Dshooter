
package WorldRender;

/**
 * Enum, ktorı ukladá typy políèok. Drí si ve¾kos políèka, jeho ID,
 * èi sa po òom dá prechádza, a aké má meno.
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
     * Konštruktor enumu.
     */
    private TileType(int mapID, boolean collidable, String name) {  
        this.mapID = mapID;
        this.collidable = collidable;
        this.name = name;       
    }
    /**
     * Getter pre ve¾kos políèka.
     */
    public static int getTILE_SIZE() {
        return TILE_SIZE;
    }
    
    /**
     * Getter pre ID políèka.
     */
    public int getMapID() {
        return mapID;
    }

    /**
     * Getter pre prechodnos políèka.
     */
    public boolean isCollidable() {
        return collidable;
    }
    
    
    /**
     * Getter pre meno políèka.
     */
    public String getName() {
        return name;
    }    
    
    /**
     * Switch pre vrátenie typu políèka.
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
