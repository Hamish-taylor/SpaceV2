import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SpaceV2 extends PApplet {

HashMap<Integer,Block> blockTypes = new HashMap<Integer,Block>();

//pixels
public int blockSize = 30;
float movementSpeed = 10;

//blocks
public int chunkSize = 32;
public int chunkHeight = 100;
float playerX = 0;
float playerY = 0;

//chunks


World w;

public void setup() {
    
    Block b = new Block(0,color(100,255));
    blockTypes.put(b.getId(),b);
    w = new World(100);
}


public void draw() {
    background(255);
    
    if(keyPressed) {
        if(key == 'w'){  
             
            playerY -= movementSpeed;
        }
        if(key == 's') {   
            playerY += movementSpeed; 
        }
        if(key == 'a'){  
            playerX -= movementSpeed;     
        }
        if(key == 'd') {  
            playerX += movementSpeed; 

        }
        
    }

    translate(-playerX+(width/2.0f), -playerY+(height/2.0f));
    w.render();
    rect(playerX-blockSize/2,playerY-blockSize/2,blockSize,blockSize);
}
class Block{

    private int id;
    private int texture;


    Block(int id,int texture) {
        this.id = id;
        this.texture = texture;
    }




    public void draw(int x,int y) {
        fill(texture);
        rect(x,y,blockSize,blockSize);
    }

    public int getId() {
        return id;
    }


}


class Chunk {
    int[][] blocks = new int[chunkHeight][chunkSize];
    int x;
    int y;
    
    Chunk(int x, int y) {
        this.x = x;
        this.y = y;
            for(int yy = 0; yy < chunkHeight; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    row[xx] = 0;
            }  
            blocks[yy] = row;
        }
    }

    public void render() {
        int pY = (int)playerY - 20;
        if (pY < 0) pY = 0;
        int pYP = (int)playerY + 20;
        if(pYP > blocks[0].length) pYP = blocks[0].length;
        println("pY: "+pY);
        println("pYP" + pYP);

            for(int yy = pY; yy < pYP; yy++) {
                for(int xx = 0; xx < chunkSize; xx++) {
                blockTypes.get(blocks[yy][xx]).draw((xx*blockSize)+x*chunkSize*blockSize,(yy*blockSize)+y*chunkSize*blockSize);
            }
        }
    }

}
class World {



    Chunk[][] chunks; 


    World(int width) {
        chunks = new Chunk[1][chunkSize];
        for(int y = 0; y < chunks.length; y++) {
            for(int x = 0; x < chunks[0].length; x++) {
                Chunk c = new Chunk(x,y);
                chunks[y][x] = c;
            }
        }
    }

    public void render() {
        int pX = (int)(playerX/(float)chunkSize)-2;
        if (pX < 0) pX = 0;
        int pXP = (int)(playerX/(float)chunkSize) + 2;
        if(pXP > chunks[0].length) pXP = chunks[0].length;
        for(int y = 0; y < 1; y++) {
            for(int x = pX; x < pXP; x++) {
                chunks[y][x].render();
            }
        }
    }

}
  public void settings() {  size(1000,1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpaceV2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
