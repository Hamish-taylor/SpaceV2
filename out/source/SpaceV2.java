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

HashMap<Integer,World> worlds = new HashMap<Integer,World>();

HashMap<Integer,Integer> inventory = new HashMap<Integer,Integer>();

//pixels
public int blockSize = 32;
float movementSpeed = 10;
int playerSize = 32;

float yVelocity = 0;
float xVelocity = 0;
float gravity = 2;

//blocks
public int chunkSize = 32;
public int chunkHeight = 256;
float playerX = 5000;
float playerY = -10;
int verticalRenderDist = 20;

//chunks
int chunkRenderDist = 2;

World w;
int worldCount = -1;
int currentWorld = 0;


//PImage sky;
PImage background;
PImage player;
Gui g;

public void setup() {
    player = loadImage("Blocks/Player.png");
    background = loadImage("Blocks/BackGround1.png");
    g = new Gui();
    UIPanel p = new UIPanel(width/2-250,height/2-350,width/2,(int)(height/1.5f),color(100,100,100,240),"Main Menu",width/2-100,200,false);
    p.setTextModeCenter(true);
    p.formatText(30,color(0));
    g.addPanel(p);
    p = new UIPanel(width/2-(width/8),height/2-250,width/4,30,color(100,100,100,240),"Play",width/2-100,height/2-600,true);
    p.setTextModeCenter(true);
    p.formatText(30,color(0));
    g.addPanel(p);

    
    //sky = loadImage("Blocks/Sky.png");
    loadFiles();

    w = new World();
    worlds.put(worldCount,w);
}

public void loadFiles() {
    String[] temp = loadStrings("Blocks/Blocks.txt");
    
    for(int i = 0; i < temp.length; i++) {
        String[] lines = split(temp[i], ' ');
        Block b = new Block(i,lines[0],Boolean.valueOf(lines[1]));
        blockTypes.put(i,b);
    }
}

public void keyPressed() {
     if(key == 'w' && isGrounded()){     
            yVelocity -= 30; 
        }
        if(key == 'a' && xVelocity > -10){  
            xVelocity -= movementSpeed;     
        }
        if(key == 'd' && xVelocity < 10) {  
            xVelocity += movementSpeed; 
        }   
        key = ' ';
}

public void draw() {
    //clear();

    for(HashMap.Entry<Integer, Integer> entry : inventory.entrySet()) {
    println("entry: "+entry);
    }

    background(0, 0,255);

    if(keyPressed) {
       
    }else{
        xVelocity = 0;
    }

        if(yVelocity < 0 && !doCollision("up")) playerY += yVelocity;
        else if(yVelocity > 0 && !doCollision("down")) playerY += yVelocity;

        if(xVelocity < 0 && !doCollision("left")) playerX += xVelocity;
        else if(xVelocity > 0 && !doCollision("right")) playerX += xVelocity;

        if(yVelocity < movementSpeed) yVelocity+= gravity;
    

    translate(-playerX+(width/2.0f), -playerY+(height/2.0f));
    
    playerX = (int) playerX;
    playerY = (int) playerY;
    worlds.get(currentWorld).render();
    fill(255);
    //rect(playerX-(playerSize/2),playerY-(playerSize/2),playerSize,playerSize);
    image(player, playerX-(playerSize/2), playerY-(playerSize/2), playerSize, playerSize);
    translate(-(-playerX+(width/2.0f)), -(-playerY+(height/2.0f)));
    //g.drawUI();
}

public boolean doCollision(String dir) {


    if(dir.equals("up")) {
        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX-(0.5f*playerSize),playerY-abs(yVelocity)-(0.5f*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX+(0.5f*playerSize),playerY-abs(yVelocity)-(0.5f*playerSize))).isSolid())) {
             
            if(((((int)((playerX+(0.5f*playerSize))/blockSize))*blockSize) == playerX+0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY-abs(yVelocity)-(0.5f*playerSize))).isSolid()) return false;
            if(((((int)((playerX-(0.5f*playerSize))/blockSize))*blockSize) == playerX-0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY-abs(yVelocity)-(0.5f*playerSize))).isSolid()) return false;

            if((playerY-0.5f*blockSize)-(((int)((playerY-(0.5f*playerSize))/blockSize))*blockSize) != 0) {
                playerY+=(((int)((playerY+(0.5f*playerSize))/blockSize))*blockSize)-(playerY+0.5f*blockSize);
            }
            
            return true;
        }
    }
    if(dir.equals("down")) {
        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX-(0.5f*playerSize),playerY+movementSpeed+(0.5f*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX+(0.5f*playerSize),playerY+movementSpeed+(0.5f*playerSize))).isSolid())) {
            
            if(((((int)((playerX+(0.5f*playerSize))/blockSize))*blockSize) == playerX+0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY+movementSpeed+(0.5f*playerSize))).isSolid()) return false;
            if(((((int)((playerX-(0.5f*playerSize))/blockSize))*blockSize) == playerX-0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY+movementSpeed+(0.5f*playerSize))).isSolid()) return false;

            if((playerY+0.5f*blockSize)-(((int)((playerY+(0.5f*playerSize))/blockSize))*blockSize) != 0) {
                playerY-=(playerY+0.5f*blockSize)-(((int)((playerY+(0.5f*playerSize))/blockSize))*blockSize)-blockSize;
            }
            
            return true;
        }

    }
    if(dir.equals("left")) {

        //if(blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5*playerSize),playerY)).isSolid()) return true;

        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5f*playerSize),playerY-(0.5f*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5f*playerSize),playerY+(0.5f*playerSize))).isSolid()) 
        ) {
            
            if(((((int)((playerY+(0.5f*playerSize))/blockSize))*blockSize) == playerY+0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5f*playerSize),playerY)).isSolid()) return false;
            if(((((int)((playerY-(0.5f*playerSize))/blockSize))*blockSize) == playerY-0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5f*playerSize),playerY)).isSolid()) return false;

            if((playerX+0.5f*blockSize)-(((int)((playerX+(0.5f*playerSize))/blockSize))*blockSize) != 0) {
                playerX+=(((int)((playerX+(0.5f*playerSize))/blockSize))*blockSize)-(playerX+0.5f*blockSize);
            }
            

            
            return true;
        }
    }
    if(dir.equals("right")) {
        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5f*playerSize),playerY+(0.5f*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5f*playerSize),playerY-(0.5f*playerSize))).isSolid())) {
           
            if(((((int)((playerY+(0.5f*playerSize))/blockSize))*blockSize) == playerY+0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5f*playerSize),playerY)).isSolid()) return false;
            if(((((int)((playerY-(0.5f*playerSize))/blockSize))*blockSize) == playerY-0.5f*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5f*playerSize),playerY)).isSolid()) return false;


           if((playerX+0.5f*blockSize)-(((int)((playerX+(0.5f*playerSize))/blockSize))*blockSize) != 0) {
                playerX+=(((int)((playerX+(0.5f*playerSize))/blockSize))*blockSize)-(playerX+0.5f*blockSize)+blockSize;
            }
            
            return true;
        }
    }
    
    return false;


}

public boolean isGrounded() {
    return(doCollision("down"));
}

public void mousePressed() {
    int blockId = worlds.get(currentWorld).getBlock(mouseX+playerX-width/2,mouseY+playerY-height/2);
    if(mouseButton == LEFT) {
        if(blockTypes.get(blockId).isSolid()) {     
            worlds.get(currentWorld).changeBlock(mouseX+playerX-width/2,mouseY+playerY-height/2,0);
            if(!inventory.containsKey(blockId)) {            
                inventory.put(blockId,0);
            }
            inventory.put(blockId,inventory.get(blockId)+1);
        }
    }
    if(mouseButton == RIGHT) {
        if(!blockTypes.get(blockId).isSolid()) {          
            worlds.get(currentWorld).changeBlock(mouseX+playerX-width/2,mouseY+playerY-height/2,1);
        }
    }
}
class Block{

    private int id;
    private PImage texture;
    private boolean solid;


    Block(int id,String texture,boolean solid) {
        this.id = id;
        this.texture = loadImage("Blocks/"+texture);
        this.solid = solid;
    }

    public boolean isSolid() {
        return solid;
    }


    public void draw(int x,int y) {
        image(texture, x, y,blockSize,blockSize);
    }

    public int getId() {
        return id;
    }


}


class Chunk {
    int[][] blocks = new int[chunkHeight][chunkSize];
    int x;
    

    Chunk(int x) {
        this.x = x;
            for(int yy = 0; yy < chunkHeight; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    float n = noise((xx+x*chunkSize)*0.04f, (yy)*0.04f,0)*255;
                   
                    if(yy < 20) {
                        
                        n *=20.0f/(yy);
                        if(n < 150 && blocks[yy-1][xx] == 0) {
                            row[xx] = 2;
                        }else if(n < 150 && blocks[yy-1][xx] == 2) {
                            row[xx] = 3;
                        }else if(n < 150 && n > 100 && blocks[yy-1][xx] == 3) {
                             row[xx] = 3;
                        }else {
                            if(n > 150) row[xx] = 0;
                            else row[xx] = 1;   
                        }
                    }else {
                        if(n > 150)  row[xx] = 0;
                        else {
                            for(int i = 5; i < blockTypes.size(); i++) {
                                float rand = i*9999;
                                float nn = noise((xx+x*chunkSize+rand)*(0.1f), (yy+rand)*(0.1f),0)*255;
                                if(nn > 180) {
                                    row[xx] = i;
                                    i = 11111;
                                }
                                else {
                                    row[xx] = 1; 
                                    
                                }
                            }                           
                        }  
                    }          
            }  
            blocks[yy] = row;
        }
    }

    public void render() {
        drawBackground();
        float pY = playerY/(float)blockSize - verticalRenderDist;
        if (pY < 0) pY = 0;
        float pYP = playerY/(float)blockSize + verticalRenderDist;
        if(pYP > blocks.length) pYP = blocks.length;
            for(float yy = pY; yy < pYP; yy++) {
                for(int xx = 0; xx < chunkSize; xx++) {
                blockTypes.get(blocks[(int)yy][xx]).draw((xx*blockSize)+x*chunkSize*blockSize,((int)yy*blockSize));
            }
        }
        
    }
    public int getBlock(float xx,float yy) {
        xx /=blockSize;
        yy/=blockSize;
        
        xx -= chunkSize*x;
   
        if(floor(yy) < 0 || floor(yy) > chunkHeight) return 0;
        if(floor(xx) < 0 || floor(xx) > chunkSize) return 0;
        return blocks[floor(yy)][floor(xx)];
        
    }

    public void changeBlock(float xx,float yy,int id) {
        xx /=blockSize;
        yy/=blockSize;
        
        xx -= chunkSize*x;
   
        if(floor(yy) < 0 || floor(yy) > chunkHeight) return;
        if(floor(xx) < 0 || floor(xx) > chunkSize) return;
        blocks[floor(yy)][floor(xx)] = id;
    }

    public void drawBackground() {
        float pY = ((float)playerY/(float)blockSize)-16;
        if(pY < 20) pY = 20;
        image(background,x*chunkSize*blockSize,pY*blockSize);
    }

}
class Gui {
    public boolean isActive = false;
    public ArrayList<UIPanel> panels = new ArrayList<UIPanel>();

    public void drawUI() {

        for(UIPanel u: panels) {
            u.drawP();
        }

    }

    public void addPanel(UIPanel u) {  
        panels.add(u);
    }


}
class UIPanel {

    int w;
    int h;
    int x;
    int y;

    int textSize = 30;

    int textX = 0;
    int textY = 0;

    int c = color(0,0,0);
    int textColor;

    String text;

    boolean textModeCenter = false;

    boolean button;

    UIPanel(int x,int y,int w,int h, int c,boolean button) {
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.c = c;
    }
    UIPanel(int x,int y,int w,int h, int c,String text,int textX,int textY,boolean button) {
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.text = text;
        this.textX = textX;
        this.textY = textY;
        this.c = c;
    }

    public void drawP() {
        fill(c);
        rect(x,y,w,h);
        textSize(textSize);
        fill(textColor);
        if (text!= null){
        if(textModeCenter)text(text,(x+w/2)-textWidth(text)/2,y+textSize*0.9f);
        else text(text,textX,textY);
        
        } 
    }

    public void addText(String text) {
        this.text = text;
    }

    public void formatText(int textSize,int textColor) {
        this.textSize = textSize;
        this.textColor = textColor;
    }

    public void setTextModeCenter(boolean b) {
        textModeCenter = b;
    }

    public boolean on(int xx,int yy) {
        return (xx > x && xx < x+w && yy > y && yy < y+h);
    }

    public void changeColor(int c) {
        this.c = c;
    }
}
class World {
    
    int id;
    Chunk[] chunks; 

    World() {
        
        worldCount++;
        id = worldCount;
        chunks = new Chunk[chunkSize];    
            for(int x = 0; x < chunks.length; x++) {
                Chunk c = new Chunk(x);
                chunks[x] = c;
            }
    }

    public void render() {
        float pX = (playerX/blockSize)/(float)chunkSize-chunkRenderDist;
        if (pX < 0) pX = 0;
        float pXP = (playerX/blockSize)/(float)chunkSize + chunkRenderDist;
        if(pXP > chunks.length) pXP = chunks.length;
            for(float x = pX; x < pXP; x++) {
                chunks[(int)x].render();
            }
    }

    public int getBlock(float x,float y) {
        if((int)(x/(chunkSize*blockSize)) < 0 || (int)(x/(chunkSize*blockSize)) > chunks.length) return 0;
        return chunks[(int)(x/(chunkSize*blockSize))].getBlock(x,y);
    }

    public void changeBlock(float x,float y,int id) {
        if((int)(x/(chunkSize*blockSize)) < 0 || (int)(x/(chunkSize*blockSize)) > chunks.length) return;
        chunks[(int)(x/(chunkSize*blockSize))].changeBlock(x,y,id);
    }

}
  public void settings() {  size(1000,1000,P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpaceV2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
