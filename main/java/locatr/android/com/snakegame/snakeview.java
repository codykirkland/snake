package locatr.android.com.snakegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import androidx.annotation.Nullable;

/*
this is the view that is called from snake. this will make and paint the board, snake, wall, and food.
it also controls the snake
 */
public class snakeview extends View implements Runnable {
    int ROWS = 25;//the number of rows the board has
    int COLS = 15;//the number of columns the board has
    tile[][] mTiles = new tile[COLS][ROWS];//this is the board
    List<tile> body;//this keeps track of where the body is on the board
    tile head;//this keeps track of where the head is on the board
    int snakeLength = 0;//this is the length of the snake
    boolean grow = false;//this will indicate that the snake will grow


    //this is the time stuff
    long nextFrameTime = System.currentTimeMillis();//makes an update be triggered
    private final long FPS = 5;//this is the fps you want
    private final long MILLIS_PER_SECOND = 1000;//get the milliseconds in a second
    boolean isPlaying = true;//this will indicate that the game if playing

    float size;//size of the tile
    float xmargin, ymargin;//the margins of the view

    Paint spaint, fpaint, wpaint, scorepaint;

    Thread drawThread = null;//this creates  a thread
    Random random = new Random();

    //this is the directions the snake can move
    public enum Dir {UP, RIGHT, DOWN, LEFT}
    public Dir dir;//this direction the snake will move

    /**
     * this is used when you are creating the view
     * @param context
     * @param attrs
     */
    public snakeview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //the paint stuff
        spaint = new Paint();
        spaint.setColor(Color.GREEN);//the snake color
        fpaint = new Paint();
        fpaint.setColor(Color.RED);//the food color
        wpaint = new Paint();
        wpaint.setColor(Color.WHITE);// the wall color
        scorepaint = new Paint();
        scorepaint.setColor(Color.WHITE);//the text color
        scorepaint.setTextSize(100);


        newgame();//this starts a new game
        start();//this starts the loop
    }

    /**
     * this draws the board, snake, walls, food and score.
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        size = getHeight() / (ROWS + 1);//the size of the tiles

        xmargin = (getHeight() - ROWS * size) / 2;//the margin at the bottom and top of screen
        ymargin = (getWidth() - COLS * size) / 2;//the margin at the left and right of the maze


        canvas.translate(ymargin, xmargin);


        //draw the board
        for (int y = 0; y < COLS; y++) {
            for (int x = 0; x < ROWS; x++) {



                //this draws the body of the snake
                if (mTiles[y][x].isBody()) {
                    canvas.drawRect(
                            y * size,
                            x * size,
                            (y + 1) * size,
                            (x + 1) * size,
                            spaint);
                }
                //this draws the food
                if (mTiles[y][x].isFood()) {
                    canvas.drawRect(
                            y * size,
                            x * size,
                            (y + 1) * size,
                            (x + 1) * size,
                            fpaint);
                }
                //this draws the head of the snake
                if (mTiles[y][x].isHead()) {
                    canvas.drawRect(
                            y * size,
                            x * size,
                            (y + 1) * size,
                            (x + 1) * size,
                            spaint);
                }
                //this draws the walls around the board
                if (mTiles[y][x].isWall()) {
                    canvas.drawRect(
                            y * size ,
                            x * size ,
                            (y + 1) * size ,
                            (x + 1) * size ,
                            wpaint);
                }



            }
        }
        //this draws the score the player is at
        canvas.drawText(String.valueOf(snakeLength), 7 * size, 13 * size, scorepaint);
    }

    /**
     * this makes a new board
     */
    public void newgame() {

        for (int y = 0; y < COLS; y++) {

            for (int x = 0; x < ROWS; x++) {

                mTiles[y][x] = new tile(y, x);//create a new tile

                //this sets the walls of the board
                if (y == 0 || y == COLS - 1 || x == 0 || x == ROWS - 1) {
                    mTiles[y][x].setWall(true);
                }

            }
        }
        body = new ArrayList<>();//this makes a list to keep track to the body
        getSnake();//this gets the place of the head of the snake
        getFood();//this gets the food
    }
    /**
     * this puts the snake one the board
     */
    public void getSnake() {

        mTiles[5][10].setHead(true); //this make the head
        head = mTiles[5][10];//this keeps track of what is the head
        snakeLength = 0;//this starts the length of the snake at 0
        dir= Dir.UP;//the snake will move up at the start
    }

    /**
     * this sets the food on the board
     */
    public void getFood() {
        //this places the food on the board
        mTiles[(random.nextInt(COLS - 2) + 1)][(random.nextInt(ROWS - 2) + 1)].setFood(true);

    }

    /**
     * this starts the loop
     */
    public void start() {
        //this creates a new thread if there was not one
        if (drawThread == null) drawThread = new Thread(this);
        drawThread.start();// this starts the loop
    }

    @Override
    public void run() {

        while (isPlaying) {
            //this updates 5 time a second
            if (updateRequired()) {
                check();
                postInvalidate();
            }
        }
    }

    /**
     * this gets the timeframe of the game
     * @return
     */
    public boolean updateRequired() {

        //checks if an update is needed
        if (nextFrameTime <= System.currentTimeMillis()) {
            //sets when the next update will be needed
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            return true;
        }
        return false;
    }

    /**
     * this checks if the snake has either eaten food or dies
     */
    void check() {

        //this checks to see if the snake has eaten food
        if (head.isFood()) {
            grow = true;// this will indicate the snake will grow
            snakeLength++;//adds 1 to snakelength
            head.setFood(false);//this gets rid of the food on that tile
            getFood();//this gets some new food
        }
        move();//this moves the snake
        //this checks if the snake has died
        if (dead()){
            newgame();//resets the game
        }
    }

    /**
     * this moves the snake
     */
    public void move() {
        //this checks if the snake has a body
        if (snakeLength > 0) {
            //this will check if the snake will grow
            if (grow) {
                body.add(head);//adds the head space to the body list
                head.setBody(true);//make the head space the body
                grow = false;//makes grow false
            } else {
                body.get(0).setBody(false);
                body.remove(0);//this removes the first tile in the list
                body.add(head);//this add the head to the list
                head.setBody(true);//this makes the head the body
            }
        }

        // this moves the head
        switch (dir) {
            //this moves the head up
            case UP:
                head.setHead(false);//this make the head false
                head = mTiles[head.getTiley()][head.getTilex() - 1];//this moves head up 1
                head.setHead(true);//this makes the tile the head
                break;
            //this moves the head right
            case RIGHT:
                head.setHead(false);//this make the head false
                head = mTiles[head.getTiley() + 1][head.getTilex()];//this moves head right 1
                head.setHead(true);//this makes the tile the head
                break;
            //this moves the head down
            case DOWN:
                head.setHead(false);//this make the head false
                head = mTiles[head.getTiley()][head.getTilex() + 1];//this moves head down 1
                head.setHead(true);//this makes the tile the head
                break;
            //this moves the head left
            case LEFT:
                head.setHead(false);;//this make the head false
                head = mTiles[head.getTiley() - 1][head.getTilex()];//this moves head left 1
                head.setHead(true);;//this makes the tile the head
                break;

        }


    }

    /**
     * this checks this the snake hits a wall or hits its body
     * @return
     */
    public boolean dead() {

        //checks if the snake has hit a wall
        if (head.isWall()) {
            return true;
        } else if (head.isBody()) {//checks if the snake has hit its body
            return true;

        }


        return false;
    }

    /**
     * this is how the snake will be controlled
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //get the location of the middle of head of the snake
        float pcenterx = ymargin + (head.tiley + 0.5f) * size;
        float pcentery = xmargin + (head.tilex + 0.5f) * size;

        int maskedAction = event.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                //gets how far away your finger is from the head of the snake
                float y = event.getY() - pcentery;
                float x = event.getX() - pcenterx;

                //gets the axis snake will move
                boolean updown = Math.abs(x) < Math.abs(y);

                //this determines if your finger is far enough away from player
                boolean thresholdx = Math.abs(x) > size / 2;
                boolean thresholdy = Math.abs(y) > size / 2;

                //checks what direction the snake will move and if the snake can move
                if (thresholdx && !updown && x > 0 && dir != Dir.LEFT) {//right
                    dir = Dir.RIGHT;

                } else if (thresholdx && !updown && x < 0 && dir != Dir.RIGHT) {//left
                    dir = Dir.LEFT;
                } else if (thresholdy && updown && y < 0 && dir != Dir.DOWN) {//up
                    dir = Dir.UP;
                } else if (thresholdy && updown && y > 0 && dir != Dir.UP) {//down
                    dir = Dir.DOWN;
                }
                break;
        }
        return true;
    }

}
