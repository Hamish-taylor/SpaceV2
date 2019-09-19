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
public int blockSize = 10;
float movementSpeed = 0.10f;

//blocks
public int chunkSize = 32;
float playerX = 0;
float playerY = 0;

//chunks
public int height = 100;

World w;

public void setup() {
    
    Block b = new Block(0,color(100,255));
    blockTypes.put(b.getId(),b);
    w = new World(1);
}


public void draw() {
    println("playerX: "+playerX);
    background(255);
    

    if(keyPressed) {
        if(key == 'w'){  
            translate(0, -movementSpeed);  
            playerY -= movementSpeed;
        }
        if(key == 's') { 
            translate(0, movementSpeed);  
            playerY += movementSpeed; 
        }
        if(key == 'a'){  
            translate(-movementSpeed, 0);  
            playerX -= movementSpeed;     
        }
        if(key == 'd') { 
            translate(movementSpeed, 0);  
            playerX += movementSpeed; 

        }
        
    }


    w.render();

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
    int[][] blocks = new int[chunkSize][chunkSize];
    int x;
    int y;
    
    Chunk(int x, int y) {
        this.x = x;
        this.y = y;
            for(int yy = 0; yy < chunkSize; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    row[xx] = 0;
            }  
            blocks[yy] = row;
        }
    }

    public void render() {
        translate(x*chunkSize, y*chunkSize);
            for(int yy = 0; yy < blocks.length; yy++) {
                for(int xx = 0; xx < blocks.length; xx++) {
                blockTypes.get(blocks[yy][xx]).draw(xx*blockSize,yy*blockSize);
            }
        }
         translate(-x*chunkSize, -y*chunkSize);
    }

}
class World {



    Chunk[][] chunks; 
    int width;


    World(int width) {
        this.width = width;
        chunks = new Chunk[200][width];
        for(int y = 0; y < chunks.length; y++) {
            for(int x = 0; x < chunks[0].length; x++) {
                Chunk c = new Chunk(x*blockSize,y*blockSize);
                chunks[y][x] = c;
            }
        }
    }

    public void render() {
        for(int y = 0; y < 10; y++) {
            for(int x = 0; x < width; x++) {
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
