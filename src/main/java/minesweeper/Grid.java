package minesweeper;

import java.util.*;
import processing.core.*;

public class Grid {
    private boolean isRevealed;
    private boolean isFlagged;
    private boolean isMine;
    private boolean isPressed;
    private int surroundingMine;
    private int timer;
    
    Random random = new Random();
    
    public Grid(boolean hasBoom){
        isFlagged = false;
        isRevealed = false;
        surroundingMine = -1;
        isPressed = false;
        isMine = hasBoom;
        timer = 0;
    }

    public void setReveal(){
        this.isRevealed = true;
    }

    public void setFlag(){
        if (this.isFlagged){
            this.isFlagged = false;
        }else{
            this.isFlagged = true;
        }
    }

    public void setSurroundingMine(int num){
        if (!this.isMine){
            this.surroundingMine = num;
        }
    }

    public void setPressed(){
        isPressed = true;
    }

    public void incTimer(){
        this.timer++;
    }

    public void reSetPressed(){
        isPressed = false;
    }


    public boolean getFlagged(){
        return isFlagged;
    }

    public boolean getRevealed(){
        return isRevealed;
    }

    public boolean getMine(){
        return isMine;
    }

    public boolean getPressed(){
        return isPressed;
    }

    public int getSurroundingMine(){
        return surroundingMine;
    }

    public int getTimer(){
        return (int) (timer / 2); 
    }

}
