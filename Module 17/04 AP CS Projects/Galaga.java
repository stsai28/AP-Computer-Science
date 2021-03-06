// Galaga.java
//  Created by Shahein Tajmir
//  2/13/04
//  This is a very updated form of the game I currently am working on.

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;

public class Galaga extends JPanel implements Runnable
{
    //  Constants
    final int SCREEN_WIDTH = 700;

    final int SCREEN_HEIGHT = 700;

    //  Variable Declarations
    Ship player = new PlayerShip(500, 500); // an object of the PlayerShip class

    Vector bosses = new Vector();

    boolean right = false, left = false;

    boolean intro, game, gameOver;

    int x, y;

    int shots = 0;

    int kills = 0;

    Vector bullets = new Vector();

    JFrame frame;

    Stars starfield;

    Vector stars = new Vector();

    Image imageBuffer;

    Dimension d;

    Thread thread;

    public Galaga()
    {
        //  Create JFrame and set it to close when the X is pressed in upper
        // right corner
        frame = new JFrame("Galaga");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  Sets JFrame background to black
        frame.setBackground(Color.BLACK);

        //  Adds the key listener I created (the class containing it is located
        // waaaaaaaay below
        frame.addKeyListener(new gameControlListener());

        //  This bit of code sizes the frame
        frame.pack();
        frame.getContentPane().add(this); // Adds Jpanel into JFrame
        d = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT); // Dimension - holds the
        // window size
        frame.setSize(d); // sets window to correct dimensions

        //  Initialize some variables
        intro = true; // booleans for tracking where in "game stage" we are
        game = false;
        gameOver = false;
        //  Initializes the imagebuffer
        imageBuffer = createImage(d.width, d.height);

        //  Initializes the vector of star coordinates
        starfield = new Stars(d);

        bosses.add(new BossShip(305, 100));
        bosses.add(new BossShip(355, 100));
        bosses.add(new BossShip(405, 100));
        bosses.add(new BossShip(455, 100));
        bosses.add(new BossShip(0, 500));

        //  Thread declaration and starting
        thread = new Thread(this);
        thread.setPriority(5);
        thread.start();

        //  Set frame visible
        frame.show();
    }

    //*****************************************************************************************
    //  Creates the buffer. Think of this method as the "paint" method.
    //*****************************************************************************************
    public void prepareImageBuffer()
    {
        //  Gets a graphical context -- the buffered image
        Graphics g = imageBuffer.getGraphics();

        //  Clears screen
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        //  Calls the method of starfield that draws the stars to the buffer
        starfield.drawStars(g);

        //  Info such as score, etc will be written in here

        //  This preserves the sprites
        g.setXORMode(Color.BLACK);

        if (intro)
        {
            paintIntro(g);
        }
        if (game)
        {
            //  Draw Player Ship
            drawPlayer(g);

            for (int i = 0; i < bosses.size(); i++)
            {
                Ship tempShip = (Ship) bosses.get(i);
                tempShip.draw(g, tempShip.getXCoordinate(), tempShip
                        .getYCoordinate());
            }

            if (right && (player.getRightSide() < d.width))
            {
                player.translate(5, 0);
            }
            if (left && player. etLeftSide() > 2)
            {
                player.translate(-5, 0);
            }
            moveBullets(g);
        }
        if (gameOver)
        {
            paintGameOver(g);
        }
    }

    //  This is the function that is executed each time the thread is called
    // (repeatedly)

    /*
     * public void run() { while(true) { try {
     * 
     * if(game) { gameStart(this); Thread.sleep(5); }
     * 
     * if(gameOver) { //gameOver(); } } catch(Exception e) { } } }
     */

    public void run()
    {
        while (true)
        {

            starfield.moveStars();
            shipCollision();
            //collisions();
            prepareImageBuffer();
            repaint();
            try
            {
                Thread.sleep(8);
            } catch (InterruptedException e)
            {

            }
        }
    }

    //*****************************************************************************************
    //  BulletManaging - This moves the bullets
    //*****************************************************************************************
    private void moveBullets(Graphics g)
    {
        //int tempY;
        for (int i = 0; i < bullets.size(); i++)
        {
            Point bul = (Point) bullets.get(i);
            bul.y -= 12;
            if (bul.y < 1)
                bullets.remove(i--);
            else
            {
                g.setColor(Color.white);
                g.fillRect(bul.x, bul.y, 2, 15);
            }
        }
    }

    public void collisions()
    {
        //  i - refers to the number of enemies on screen
        for (int i = 0; i < bosses.size(); i++)
        {
            // Tests for collission between enemy bullet and player ship
            // a - refers to the individual bullet coordinates
            for (int a = 0; a < bullets.size(); a++)
            {
                /*
                 * if(bOpen[a] == 1) { if(bY[a] > shipY & bY[a] < shipY + 32) {
                 * if(bX[a] > shipX & bX[a] < shipX + 32) { bOpen[a] = 0; health -=
                 * 5; } } }
                 */
                //  Tests for collision between player bullets and enemy ships
                if (bullets.size() != 0 && bosses.size() != 0)
                {
                    Point tempPoint = (Point) bullets.get(a);
                    Ship tempShip = (Ship) bosses.get(i);
                    if (tempPoint.y > tempShip.getYCoordinate()
                            & tempPoint.y < tempShip.getYCoordinate() + 48)
                    {
                        if (tempPoint.x > tempShip.getXCoordinate()
                                & tempPoint.x < tempShip.getXCoordinate() + 45)
                        {
                            bosses.remove(i);
                            kills++;
                        }
                    }
                }
                //  Tests for colision between enemy and player ship

                if (bosses.size() != 0)
                {
                    // Point tempPoint = (Point) bullets.get(a);
                    Ship tempShip = (Ship) bosses.get(i);
                    if ((player.getXCoordinate() + player.getDimension().width > tempShip
                            .getXCoordinate())
                            && (player.getXCoordinate() < tempShip
                                    .getXCoordinate()
                                    + tempShip.getDimension().width))
                    {
                        if (((player.getYCoordinate() + player.getDimension().height) > (tempShip
                                .getYCoordinate()))
                                && ((player.getYCoordinate() < tempShip
                                        .getYCoordinate()
                                        + tempShip.getDimension().height)))
                        {
                            //  Initialize explosion graphic
                            bosses.remove(i);
                            game = false;
                            gameOver = true;
                            repaint();
                        }
                    }
                }
            }
        }
    }

    public void shipCollision()
    {
        if (bosses.size() != 0)
        {
            //          i - refers to the number of enemies on screen
            for (int i = 0; i < bosses.size(); i++)
            {

                // Point tempPoint = (Point) bullets.get(a);
                Ship tempShip = (Ship) bosses.get(i);
                if ((player.getXCoordinate() + player.getDimension().width > tempShip
                        .getXCoordinate())
                        && (player.getXCoordinate() < tempShip.getXCoordinate()
                                + tempShip.getDimension().width))
                {
                    if (((player.getYCoordinate() + player.getDimension().height) > (tempShip
                            .getYCoordinate()))
                            && ((player.getYCoordinate() < tempShip
                                    .getYCoordinate()
                                    + tempShip.getDimension().height)))
                    {
                        //  Initialize explosion graphic
                        bosses.remove(i);
                        game = false;
                        gameOver = true;
                        repaint();
                    }
                }
            }
        }
    }

    //*****************************************************************************************
    //  Draw the player ship
    //*****************************************************************************************
    private void drawPlayer(Graphics g)
    {
        player.draw(g, player.getXCoordinate(), player.getYCoordinate());
    }

    //*****************************************************************************************
    //  This starts the running of the game
    //*****************************************************************************************
    /*
     * private void gameStart(Galaga gallag) { if(right && ship.getRightSide() <
     * 600) { //clearShip(); ship.translate(2, 0); drawShip(); } if(left &&
     * ship.getLeftSide() > 2) { //clearShip(); ship.translate(-2, 0);
     * drawShip(); } //moveBullets(); }
     */

    //*****************************************************************************************
    //  Painting Functions Located Below Here
    //*****************************************************************************************
    public void paint(Graphics g)
    {
        g.drawImage(imageBuffer, 0, 0, this);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    //*****************************************************************************************
    //  Paints the Intro Screen
    //*****************************************************************************************
    private void paintIntro(Graphics g)
    {
        Font galaga = new Font("Papyrus", Font.BOLD | Font.ITALIC, 26);
        Font title = new Font("Papyrus", Font.PLAIN, 18);
        Font instructions = new Font("Arial", Font.PLAIN, 12);
        g.setFont(galaga);
        g.setColor(Color.ORANGE);
        g.drawString("Galaga", 300, 100);
        g.setFont(title);
        g.drawString("Basic Instructions", 200, 275);
        g.setFont(instructions);
        g.setColor(Color.white);
        g
                .drawString(
                        "Use the arrow keys to move left and right.  Press Spacebar to fire.",
                        200, 300);
        g
                .drawString(
                        "This is not even an Alpha version of Galaga--more like version -1.",
                        200, 315);
        g
                .drawString(
                        "So, there is no actual game present.  Thus, you can't normally access the closing screen..",
                        200, 330);
        g
                .drawString(
                        "To do that, you must press escape during the game.  Yes...ESCAPE!",
                        200, 345);
        g
                .drawString(
                        "However, if you want to see my \"game\", press enter.  Understood?",
                        200, 360);
    }

    //*****************************************************************************************
    //  Paints the GameOver Screen
    //*****************************************************************************************
    private void paintGameOver(Graphics g)
    {
        Font gameOver = new Font("Papyrus", Font.BOLD | Font.ITALIC, 26);
        Font title = new Font("Papyrus", Font.PLAIN, 18);
        Font text = new Font("Papyrus", Font.PLAIN, 14);

        Dimension d = frame.getSize();
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setFont(gameOver);
        g.setColor(Color.ORANGE);
        g.drawString("Game Over", 300, 100);
        g.setFont(title);
        g.setColor(Color.white);
        g.drawString("Shots:  ", 200, 200);
        g.setFont(text);
        String string = "" + shots;
        g.drawString(string, 275, 200);
        g.drawString("Enemies Destroyed:  ", 200, 230);
        g.setFont(text);
        String string2 = "" + kills;
        g.drawString(string2, 350, 230);
        g.drawString("Hit Percentage:  ", 200, 260);
        g.setFont(text);
        if (shots == 0)
            shots = 1;
        String string3 = "" + (((double) kills / shots) * 100) + '%';
        g.drawString(string3, 340, 260);
    }

    //*****************************************************************************************
    //  gameControlListener Class - Encapsulates the key listener as a seperate
    // class
    //*****************************************************************************************
    public class gameControlListener implements KeyListener

    {
        public gameControlListener()
        {

        }

        //*****************************************************************************************
        //  keys pressed
        //*****************************************************************************************
        public void keyPressed(KeyEvent ke)
        {
            if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
                right = true;
            if (ke.getKeyCode() == KeyEvent.VK_LEFT)
                left = true;
            if (intro && ke.getKeyCode() == KeyEvent.VK_ENTER)
            {
                intro = false;
                game = true;
                repaint();
            }
            if (game && ke.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
                game = false;
                gameOver = true;
                repaint();

            }
        }

        //*****************************************************************************************
        //  keys released
        //*****************************************************************************************
        public void keyReleased(KeyEvent ke)
        {
            if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
                right = false;
            if (ke.getKeyCode() == KeyEvent.VK_LEFT)
                left = false;
            if (ke.getKeyCode() == KeyEvent.VK_SPACE)
            {
                if (bullets.size() < 2)
                {
                    bullets.add(new Point(player.getXCoordinate() + (49 / 2),
                            player.getYCoordinate() - 15));
                    shots++;
                }
            }
        }

        //*****************************************************************************************
        //  keys typed
        //*****************************************************************************************
        public void keyTyped(KeyEvent ke)
        {

        }
    }

    public static void main(String[] args)
    {
        Galaga hey = new Galaga();
    }
}