package minesweeper;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    private static int mineNum;

    
    private int timer;
    private int counter;

    private Grid[][] board;
    private ArrayList<Grid> orderedMine = new ArrayList<>();
    private boolean gameOver;
    private boolean gameWin;

    private PImage flag, mine0, mine1, mine2, mine3, mine4, mine5, mine6, mine7, mine8, mine9, tile, tile1, tile2, wall0;

    public String configPath;

	
	public static int[][] mineCountColour = new int[][] {
            {0,0,0}, // 0 is not shown
            {0,0,255},
            {0,133,0},
            {255,0,0},
            {0,0,132},
            {132,0,0},
            {0,132,132},
            {132,0,132},
            {32,32,32}
    };
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        gameOver = false;
        gameWin = false;
        counter = 0;
        timer = 0;
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath);
		flag = loadImage(this.getClass().getResource("flag.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine0 = loadImage(this.getClass().getResource("mine0.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine1 = loadImage(this.getClass().getResource("mine1.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine2 = loadImage(this.getClass().getResource("mine2.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine3 = loadImage(this.getClass().getResource("mine3.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine4 = loadImage(this.getClass().getResource("mine4.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine5 = loadImage(this.getClass().getResource("mine5.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine6 = loadImage(this.getClass().getResource("mine6.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine7 = loadImage(this.getClass().getResource("mine7.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine8 = loadImage(this.getClass().getResource("mine8.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        mine9 = loadImage(this.getClass().getResource("mine9.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        tile = loadImage(this.getClass().getResource("tile.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        tile1 = loadImage(this.getClass().getResource("tile1.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        tile2 = loadImage(this.getClass().getResource("tile2.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        wall0 = loadImage(this.getClass().getResource("wall0.png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        
        //create attributes for data storage, eg board
        board = new Grid[18][27];
        int tempNum = mineNum;
        for (int i = 0; i < 18; i++){
            for (int j = 0; j < 27; j++){
                if (tempNum > 0){
                    board[i][j] = new Grid(true);
                    tempNum--;
                }else{
                    board[i][j] = new Grid(false);
                }             
            }
        }
        
        orderedMine.clear();
        for (int i = 0; i < 18; i++){
            for (int j = 0; j < 27; j++){
                orderedMine.add(board[i][j]);      
            }
        }

        ArrayList<Grid> temp = new ArrayList<>(orderedMine);
        Collections.shuffle(temp);
        
        int index = 0;
        for (int i = 0; i < 18; i++){
            for (int j = 0; j < 27; j++){
                board[i][j] = temp.get(index);
                index++;
            }
        }

        //claculate the number of surrounding mine
        for (int i = 0; i < 18; i++){
            for (int j = 0; j < 27; j++){
                int count = 0;
                
                if (i != 0 && j != 0 && board[i-1][j-1].getMine()){
                    count++;
                }
                if (i != 0 && board[i-1][j].getMine()){
                    count++;
                }
                if (i != 0 && j != 26 && board[i-1][j+1].getMine()){
                    count++;
                }
                if (j != 0 && board[i][j-1].getMine()){
                    count++;
                }
                if (j != 26 && board[i][j+1].getMine()){
                    count++;
                }
                if (i != 17 && j != 0 && board[i+1][j-1].getMine()){
                    count++;
                }
                if (i != 17 && board[i+1][j].getMine()){
                    count++;
                }
                if (i != 17 && j != 26 && board[i+1][j+1].getMine()){
                    count++;
                }

                board[i][j].setSurroundingMine(count);
            }
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (gameOver || gameWin){
            return;
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        if (key == 'r'){
            setup();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver || gameWin){
            return;
        }

        int x = e.getX();
        int y = e.getY();
        Grid grid = board[(y / CELLSIZE) - 2][x / CELLSIZE];
        if (e.getButton() == LEFT){
            grid.setPressed();
        }
        else if (e.getButton() == RIGHT){
            grid.setFlag();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameOver || gameWin){
            return;
        }

        int x = e.getX();
        int y = e.getY();
    
        if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT){
            return;
        }

        Grid grid = board[(y / CELLSIZE) - 2][x / CELLSIZE];

        if (e.getButton() == LEFT){
            if (!grid.getFlagged() && grid.getPressed()){
                mineDetector(grid, (y / CELLSIZE) - 2, x / CELLSIZE);
            }
        }
        boolean hasBlank = false;
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 27; j++){
                Grid tempGrid = board[i][j];
                tempGrid.reSetPressed();
                if (!tempGrid.getRevealed() && !tempGrid.getMine()){
                    hasBlank = true;
                }
            }
        }

        if (!hasBlank){
            gameWin = true;
        }
        
    }

    public boolean mineDetector(Grid grid, int i, int j){
        if (grid.getMine()){
            grid.setReveal();
            return true;
        }else{
            if (grid.getRevealed()) {
                return false;
            }
            grid.setReveal();
            if (grid.getSurroundingMine() == 0){
                if (i != 0 && j != 0){
                    mineDetector(board[i - 1][j - 1], i - 1, j - 1);
                }
                if (i != 0){
                    mineDetector(board[i - 1][j], i - 1, j);
                }
                if (i != 0 && j != 26){
                    mineDetector(board[i - 1][j + 1], i - 1, j + 1);
                }
                if (j != 0){
                    mineDetector(board[i][j - 1], i, j - 1);
                }
                if (j != 26){
                    mineDetector(board[i][j + 1], i, j + 1);
                }
                if (i != 17 && j != 0){
                    mineDetector(board[i + 1][j - 1], i + 1, j - 1);
                }
                if (i != 17){
                    mineDetector(board[i + 1][j], i + 1, j);
                }
                if (i != 17 && j != 26){
                    mineDetector(board[i + 1][j + 1], i + 1, j + 1);
                }
            }
            return false;
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        //draw game board
        
        if (gameOver){
            
            if (counter < mineNum){
                if (frameCount % 3 == 0) { 
                    orderedMine.get(counter).setReveal();
                    counter++;
                }
            }
        }
        
        // if (startBoom){
        //     if (counter < mineNum){
        //         orderedMine.get(counter).setReveal();
        //         counter++;
        //     }
        // }
        
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 27; j++){
                Grid grid = board[i][j];
                if (grid.getRevealed()){
                    if (grid.getMine()){
                        gameOver = true;
                        switch (grid.getTimer()) {
                            case 0:
                                image(mine0, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 1:
                                image(mine1, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 2:
                                image(mine2, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 3:
                                image(mine3, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 4:
                                image(mine4, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 5:
                                image(mine5, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 6:
                                image(mine6, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 7:
                                image(mine7, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 8:
                                image(mine8, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            case 9:
                                image(mine9, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                            default:
                                image(mine9, CELLSIZE * j, CELLHEIGHT * (i + 2));
                                break;
                        }
                        
                        
                        // if (!hasBoom){
                        //     hasBoom = true;
                        //     for (int k = 0; k < mineNum; k++){
                        //         if (k == (int) (counter / 3)){
                        //             orderedMine.get(k).setReveal();
                        //             counter++;
                        //         }
                        //     }
                        // }

                        grid.incTimer();
                        
                    }else{
                        image(wall0, CELLSIZE * j, CELLHEIGHT * (i + 2));
                        switch (grid.getSurroundingMine()) {
                            case 0: 
                                break;
                            case 1:
                                fill(mineCountColour[1][0], mineCountColour[1][1], mineCountColour[1][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("1", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 2:
                                fill(mineCountColour[2][0], mineCountColour[2][1], mineCountColour[2][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("2", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 3:
                                fill(mineCountColour[3][0], mineCountColour[3][1], mineCountColour[3][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("3", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 4:
                                fill(mineCountColour[4][0], mineCountColour[4][1], mineCountColour[4][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("4", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 5:
                                fill(mineCountColour[5][0], mineCountColour[5][1], mineCountColour[5][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("5", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 6:
                                fill(mineCountColour[6][0], mineCountColour[6][1], mineCountColour[6][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("6", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 7:
                                fill(mineCountColour[7][0], mineCountColour[7][1], mineCountColour[7][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("7", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 8:
                                fill(mineCountColour[7][0], mineCountColour[7][1], mineCountColour[7][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("8", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            case 9:
                                fill(mineCountColour[7][0], mineCountColour[7][1], mineCountColour[7][2]);
                                textSize(31);
                                textAlign(CENTER, CENTER);
                                text("9", CELLSIZE * j + CELLSIZE / 2, (CELLHEIGHT * (i + 2) + CELLHEIGHT / 2) - 5);
                                break;
                            default:
                                break;
                        }
                    }
                }
                else{
                    if (mouseX > CELLSIZE * j && mouseX < CELLSIZE * (j + 1) && 
                    mouseY > CELLHEIGHT * (i + 2) && mouseY < CELLHEIGHT * (i + 3) && !(gameOver || gameWin)){
                        image(tile1, CELLSIZE * j, CELLHEIGHT * (i + 2));
                    }else{
                        image(tile2, CELLSIZE * j, CELLHEIGHT * (i + 2));
                    }

                    if (grid.getFlagged()){
                        image(flag, CELLSIZE * j, CELLHEIGHT * (i + 2));
                    }
                    else if (board[i][j].getPressed()){
                        image(tile, CELLSIZE * j, CELLHEIGHT * (i + 2));
                    }
                    
                }
    
            }
        }

        for(int i = 0; i < WIDTH / 32; i++){
            image(tile, CELLSIZE * i , 0 * 32);
            image(tile, CELLSIZE * i , 1 * 32);
        }
        
        fill(0, 0, 255);
        textSize(48);
        textAlign(RIGHT, TOP);
        int num = timer / FPS;
        text("Time: " + num, 760, 8);
        if (!(gameOver || gameWin)){
            timer++;
        }
        
        if (gameOver){
            fill(255, 0, 0);
            textSize(48);
            textAlign(LEFT, TOP);
            text("YOU LOSE", 20, 8);
        }else if (gameWin){
            fill(255, 0, 0);
            textSize(48);
            textAlign(LEFT, TOP);
            text("YOU WIN", 20, 8);
        }
    }


    public static void main(String[] args) {
        PApplet.main("minesweeper.App");
        
        if (args.length == 1 && args[0].matches("^-?\\d+$")){
            int num = Integer.parseInt(args[0]);
            if (num > 0 && num < 28 * 17){
                mineNum = num;
            }else{
                System.out.println("argument format wrong, auto set to 100.");
                mineNum = 100;
            }
        }else if (args.length == 0){
            System.out.println("no argument, auto set to 100.");
            mineNum = 100;
        }else{
            System.out.println("argument format wrong, auto set to 100.");
            mineNum = 100;
        }
    }

}
