package com.TETOSOFT.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.TETOSOFT.graphics.*;
import com.TETOSOFT.input.*;
import com.TETOSOFT.test.GameCore;
import com.TETOSOFT.tilegame.sprites.*;

import javax.sound.sampled.FloatControl;


/**
 * GameManager manages all parts of the game.dsfdsd
 */
public class GameEngine extends GameCore
{
    
    public static void main(String[] args) 
    {
            new GameEngine().run();
    }
    private Sound song = new Sound("music/music5.wav");
    public static final float GRAVITY = 0.002f;
    private Font police=new Font("Arial",Font.PLAIN,18);
    private Point pointCache = new Point();
    private TileMap map;
    private MapLoader mapLoader;
    private InputManager inputManager;
    private TileMapDrawer drawer;
    
    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction start;
    private GameAction jump;
    private GameAction instructions;
    private GameAction exit;
    private GameAction pause;
    private GameAction fly;
    private boolean GameOver=false;
    private boolean IsHighScore=false;
    private boolean isInstruction = false;
    private GameAction back;
    private int collectedStars=0;
    //number of badguys killed
    private int CreaturesKilled=0;

    private int CreatureCoefficient=0;
    //time couonter variable
    private long elapsedtime=0;

    private int Score=0;

    private int numLives=2;

    public void setCreatureCoefficient(int creatureCoefficient) {
        CreatureCoefficient = creatureCoefficient;
    }

      public void setElapsedtime(long elapsedtime) {
        this.elapsedtime = elapsedtime;
    }




   
    public void init()
    {
        super.init();
        
        // set up input manager
        initInput();
        
        // start resource manager
        mapLoader = new MapLoader(screen.getFullScreenWindow().getGraphicsConfiguration());
        
        // load resources
        drawer = new TileMapDrawer();
        drawer.setBackground(mapLoader.loadImage("background.jpg"));
        
        // load first map
        map = mapLoader.loadNextMap();
    }
    
    
    /**
     * Closes any resurces used by the GameManager.
     */
    public void stop() {
        super.stop();
        
    }
    /**
     * */
    public void pauseGame() {
        System.out.println("islevel on pause : " + islevelup);
        super.pauseGame();

    }


    
    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        start = new GameAction("start",GameAction.DETECT_INITAL_PRESS_ONLY);
        jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",GameAction.DETECT_INITAL_PRESS_ONLY);
        pause=new GameAction("pause",GameAction.DETECT_INITAL_PRESS_ONLY);
        instructions = new GameAction("instruction",GameAction.DETECT_INITAL_PRESS_ONLY);
        back = new GameAction("Back",GameAction.DETECT_INITAL_PRESS_ONLY);
        fly = new GameAction("fly");


        inputManager = new InputManager(screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);
        
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_A);
        inputManager.mapToKey(start, KeyEvent.VK_S);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_D);
        inputManager.mapToKey(jump, KeyEvent.VK_W);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_Q);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToKey(fly, KeyEvent.VK_SPACE);

        inputManager.mapToKey(exit, KeyEvent.VK_G);
        inputManager.mapToKey(instructions, KeyEvent.VK_H);
        inputManager.mapToKey(back, KeyEvent.VK_M);

    }
    
    
    private void checkInput(long elapsedTime) 
    {


        if (exit.isPressed()) {
            stop();
        }
        
        Player player = (Player)map.getPlayer();
        if (player.isAlive()) 
        {
            float velocityX = 0;
            /** if  p key is pressed it executes pauseGame() method and make a 'return'
             * to make sure that others inputs commands are not executed */
            if(pause.isPressed()) {
                System.err.println("pause is pressed");
                pauseGame();
                return;
            }



            if (moveLeft.isPressed())
            {
                velocityX-=player.getMaxSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
            }
            if (jump.isPressed()) {
                Sound pwrup = new Sound("music/jump.wav");
                FloatControl gainControl = (FloatControl) pwrup.getMyClip().getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum());
                pwrup.play();
                player.jump(false);
            }
            if (fly.isPressed()) {
                player.fly();
            }


            player.setVelocityX(velocityX);

        }
        
    }

    public void checkStarted() {
        if(start.isPressed()){
            super.stopStarted();
        }
        if(instructions.isPressed()){
            isInstruction = true;
        }
        if(back.isPressed()){
            isInstruction = false;
        }
        if(exit.isPressed()){
            super.lazilyExit();
        }
    }

    
    public void draw(Graphics2D g) {


        System.out.println("hello!!");
        drawer.draw(g, map, screen.getWidth(), screen.getHeight());

        g.setColor(Color.WHITE);
        g.drawString("Press ESC for EXIT.",10.0f,50.0f);
        g.setColor(Color.GREEN);
        g.drawString("Score: "+Score,300.0f,80.0f);
        g.drawString("Coins: "+collectedStars,300.0f,50.0f);
        g.setColor(Color.YELLOW);
        g.drawString("Lives: "+(numLives),380.0f,50.0f );
        g.setColor(Color.WHITE);
        g.drawString("Home: "+mapLoader.currentMap,380.0f,80.0f);
        g.setFont(police);
        String time="";
        if((int)elapsedtime/1000>60) time=(int)elapsedtime/1000/60+"min "+(int)elapsedtime/1000%60+" sec";
        else time=(int)elapsedtime/1000+" sec";
        g.setColor(Color.YELLOW);
        g.drawString("Time: "+time,10.0f,20.0f);

        if(ispause) {
            if (islevelup){
                g.setColor(Color.WHITE);
                g.drawString("Press 'P' Continue", 200.0f, 235.0f);
                g.drawString("Quit 'Q' Press", 200.0f, 265.0f);       
                g.setColor(Color.BLUE);
                g.drawString("LEVEL number : " + (mapLoader.currentMap), 180.0f, 180.0f);
                g.drawString("Score : " + Score, 180.0f, 200.0f);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.setColor(Color.GREEN);
                g.drawString("LEVEL UP", 180.0f, 130.0f);
            }
            else{
                System.out.println("hello!!");
                g.setColor(Color.WHITE);
                g.drawString("Press 'P' Pause/Resume", 200.0f, 235.0f);
                g.drawString("Press 'Q' Quit", 200.0f, 265.0f);
                g.drawString("Press 'M' Enable/Disable Music and Sound effects", 200.0f, 295.0f);
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("Pause", 180.0f, 180.0f);
            }


        }

        if(GameOver) {

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD,35));
            g.drawString("Game Over",220.0f,180.0f);
            if(!IsHighScore) {
            g.setColor(Color.RED);
            g.drawString("Your score: "+Score,220.0f,240.0f);
            }
            else {
                g.setColor(Color.GREEN);
                g.drawImage(mapLoader.loadImage("Nrecord.jpg"), 230, 220, null);
                g.drawString("New HighScore score: "+Score,200.0f,360.0f);
            }

            GameoverMenu menu=new GameoverMenu(this, IsHighScore, screen, Score, mapLoader.currentMap);
            menu.update();
        }


        }


    /**this method count the score of the player thanks to this formula  10%time+20%coins+70%creatures-killed */
    public long UpdateScore( int Startsnbr) {

        Score+= (int)((0.1)*elapsedtime/1000 +0.2*Startsnbr+0.7*CreatureCoefficient)/10;
        return  Score;
    }

    public void firstDraw(Graphics2D g) {
        if(!isInstruction){
            drawer.draw(g, map, screen.getWidth(), screen.getHeight());
            g.setColor(Color.WHITE);
            g.drawString("Press 'S' To Start The Game ", 200.0f, 235.0f);
            g.drawString("Press 'H' For Help", 200.0f, 265.0f);
            g.drawString("Press 'ESC' To Quit the Game", 200.0f, 295.0f);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Start Screen", 180.0f, 180.0f);
        }else{
            drawer.draw(g, map, screen.getWidth(), screen.getHeight());
            g.setColor(Color.WHITE);
            g.drawString("This is the help page :",20.0f,100.0f);
            g.setColor(Color.WHITE);
            g.drawString("Press M to go back",400.0f,20.0f);
            g.setColor(Color.WHITE);
            g.drawString("Press 'W' to jump.",100.0f,150.0f);
            g.setColor(Color.WHITE);
            g.drawString("Press 'A' to move to the left",100.0f,200.0f);
            g.setColor(Color.WHITE);
            g.drawString("Press 'D' to move to the Right.",100.0f,250.0f);
            g.setColor(Color.WHITE);


        }




    }

    
    /**
     * Gets the current map.
     */
    public TileMap getMap() {
        return map;
    }
    
    /**
     * Gets the tile that a Sprites collides with. Only the
     * Sprite's X or Y should be changed, not both. Returns null
     * if no collision is detected.
     */
    public Point getTileCollision(Sprite sprite, float newX, float newY) 
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);
        
        // get the tile locations
        int fromTileX = TileMapDrawer.pixelsToTiles(fromX);
        int fromTileY = TileMapDrawer.pixelsToTiles(fromY);
        int toTileX = TileMapDrawer.pixelsToTiles(
                toX + sprite.getWidth() - 1);
        int toTileY = TileMapDrawer.pixelsToTiles(
                toY + sprite.getHeight() - 1);
        
        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                        map.getTile(x, y) != null) {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }
        
        // no collision found
        return null;
    }
    
    
    /**
     * Checks if two Sprites collide with one another. Returns
     * false if the two Sprites are the same. Returns false if
     * one of the Sprites is a Creature that is not alive.
     */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }
        
        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }
        
        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());
        
        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
                s2x < s1x + s1.getWidth() &&
                s1y < s2y + s2.getHeight() &&
                s2y < s1y + s1.getHeight());
    }
    
    
    /**
     * Gets the Sprite that collides with the specified Sprite,
     * or null if no Sprite collides with the specified Sprite.
     */
    public Sprite getSpriteCollision(Sprite sprite) {
        
        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }
        
        // no collision found
        return null;
    }
    
    
    /**
     * Updates Animation, position, and velocity of all Sprites
     * in the current map.
     */
    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();
        
        if(!ispause)   this.elapsedtime+=elapsedTime;

        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = mapLoader.reloadMap();
            return;
        }
        
        // get keyboard/mouse input
        checkInput(elapsedTime);
        if(!ispause) {
        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);
        
        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                } else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
        }
    }
    
    
    /**
     * Updates the creature, applying gravity for creatures that
     * aren't flying, and checks collisions.
     */
    private void updateCreature(Creature creature,
            long elapsedTime) {
        
        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                    GRAVITY * elapsedTime);
        }
        
        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
                getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        } else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                        TileMapDrawer.tilesToPixels(tile.x) -
                        creature.getWidth());
            } else if (dx < 0) {
                creature.setX(
                        TileMapDrawer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false,elapsedTime);
        }
        
        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        } else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                        TileMapDrawer.tilesToPixels(tile.y) -
                        creature.getHeight());
            } else if (dy < 0) {
                creature.setY(
                        TileMapDrawer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill,elapsedTime);
        }
        
    }
    
    
    /**
     * Checks for Player collision with other Sprites. If
     * canKill is true, collisions with Creatures will kill
     * them.
     */
    public void checkPlayerCollision(Player player,
            boolean canKill,long elapsedTime) {
        if (!player.isAlive()) {
            return;
        }
        
        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
            UpdateScore(50);
        } else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if( badguy instanceof Fly ) {
                //System.out.println("9atele faracha");
                this.CreatureCoefficient=150;
            }
            else if(badguy instanceof Grub) {
                //System.out.println("9atele doudaa");
                this.CreatureCoefficient=100;
            }
            if (canKill) {
                // kill the badguy and make player bounce
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                CreaturesKilled++;

                UpdateScore(0);
            } else {
                // player dies!
                player.setState(Creature.STATE_DYING);
                numLives--;
                Sound pwrup = new Sound("music/gameover.wav");
                FloatControl gainControl = (FloatControl) pwrup.getMyClip().getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum());
                pwrup.play();
                if(numLives==0) {
                    GameOver = true;
                    IsHighScore = UpdateHighScoreList(Score);
                    System.out.println(IsHighScore);

                    System.out.println("level u");

                    draw(screen.getGraphics());
                    screen.update();
                }

            }
        }
    }
    
    
    /**
     * Gives the player the speicifed power up and removes it
     * from the map.
     */

    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            collectedStars++;
            if(collectedStars==100)
            {
                Sound pwrup = new Sound("music/powerup.wav");
                FloatControl gainControl = (FloatControl) pwrup.getMyClip().getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum());
                pwrup.play();
                numLives++;
                collectedStars=0;
            }
            else { Sound pwrup = new Sound("music/coin.wav");
                FloatControl gainControl = (FloatControl) pwrup.getMyClip().getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMaximum());
                pwrup.play();}

        } else if (powerUp instanceof PowerUp.Music) {
            ArrayList<String> songs = new ArrayList<String>();
            songs.add("music/music1.wav");
            songs.add("music/music5.wav");
            songs.add("music/music2.wav");

            Random rn = new Random();
            int rand = rn.nextInt(3);System.out.println(rand);
            song.stop();
            song = new Sound(songs.get(rand));
            FloatControl gainControl = (FloatControl) song.getMyClip().getControl(FloatControl.Type.MASTER_GAIN);

            double gain = 0.25;
            float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            System.out.println(gainControl.getValue());
            song.loop();

        } else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            Sound pwrup = new Sound("music/stage_clear.wav");
            FloatControl gainControl = (FloatControl) pwrup.getMyClip().getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gainControl.getMaximum());
            pwrup.play();

            System.out.println("level up");
            map = mapLoader.loadNextMap();
            islevelup = true;

            pauseGame();
        }
    }
      
}
