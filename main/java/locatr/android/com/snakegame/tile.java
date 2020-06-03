package locatr.android.com.snakegame;

/*
this is what the board is made out of
 */
public class tile {
    boolean head = false;//the head
    boolean food = false;//the food
    boolean body = false;//the body
    boolean wall = false;//the wall

    int tiley,tilex;//this is the x and y of the tile on the board

    public tile(int tiley, int tilex){
        this.tiley = tiley;
        this.tilex = tilex;
    }

    /**
     * these are the getters and setters
     *
     */
    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }

    public boolean isWall() {
        return wall;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public int getTiley() {
        return tiley;
    }

    public void setTiley(int tiley) {
        this.tiley = tiley;
    }

    public int getTilex() {
        return tilex;
    }

    public void setTilex(int tilex) {
        this.tilex = tilex;
    }

    public boolean isHead() {
        return head;
    }

    public void setHead(boolean head) {
        this.head = head;
    }

    public boolean isFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }
}
