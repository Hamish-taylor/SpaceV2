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

ArrayList<Integer> removeItems = new ArrayList<Integer>();



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
float playerX = 16*blockSize*chunkSize;
float playerY = -10;
int verticalRenderDist = 20;

//chunks
int chunkRenderDist = 2;

World w;
int worldCount = -1;
int currentWorld = 0;

int xOffset = 0;

int selectedItem = 0;

int numBlocks = -1;

boolean guiIsActive = false; //shouild the gui be drawn

int planetDisplay = 0; //planet currently being displayed on the menue

//PImage sky;
PImage background;
PImage player;
PImage mainMenu;
Gui g;

PVector curBlock = new PVector(-1,-1);
double startBreak = -1;

boolean main = true;



public void setup() {
    player = loadImage("Blocks/Player.png");
    background = loadImage("Blocks/BackGround1.png");
    mainMenu = loadImage("Blocks/Main Menu.png");

    
    //sky = loadImage("Blocks/Sky.png");
    loadFiles();

    createPlanet();
}

public void loadFiles() {
    String[] temp = loadStrings("Blocks/Blocks.txt");
    
    for(int i = 0; i < temp.length; i++) {
        String[] lines = split(temp[i], ' ');
        Block b = new Block(lines[0],Boolean.valueOf(lines[1]),Integer.valueOf(lines[2]),false);
        blockTypes.put(numBlocks,b);
    }
}

public void keyPressed() {
    if((key == 'w' || key == 'W')  && isGrounded()){     
        yVelocity -= 30; 
    }
    if((key == 'a' || key == 'A') && xVelocity > -10){  
        xVelocity -= movementSpeed;     
    }
    if((key == 'd' || key == 'D') && xVelocity < 10) {  
        xVelocity += movementSpeed; 
    }   
    if(key == 'e' || key == 'E') {
        guiIsActive = !guiIsActive;
        planetDisplay = currentWorld;
    }
    key = ' ';
}

public void mouseWheel(MouseEvent event) {
  selectedItem += event.getCount();
}

public void draw() {
    if(main) {
        drawMain();
    }else if(!guiIsActive) {

        if(worlds.get(currentWorld).generated() == false) worlds.get(currentWorld).generatePlanet();  //making sure the current planet has been generated

        if(mouseButton == LEFT) {
            int blockId = worlds.get(currentWorld).getBlock(mouseX+playerX-width/2,mouseY+playerY-height/2);

            if(!(sqrt(sq(playerX - (mouseX+playerX-width/2))+sq(playerY-(mouseY+playerY-height/2)))/blockSize > 5)) {
                if(blockTypes.get(blockId).isSolid() && (millis() - startBreak > (blockTypes.get(blockId).hardness() * 100)) && startBreak != -1 && curBlock.x == (int)(mouseX+playerX-width/2)/blockSize && curBlock.y == (int)(mouseY+playerY-height/2)/blockSize) {     
                    worlds.get(currentWorld).changeBlock(mouseX+playerX-width/2,mouseY+playerY-height/2,0);
                    
                    if(!inventory.containsKey(blockId)) {            
                        inventory.put(blockId,0);
                    }
                    inventory.put(blockId,inventory.get(blockId)+1);
                    curBlock = new PVector(-1,-1);
                    startBreak = -1;
                }else {
                    curBlock.x = (int)(mouseX+playerX-width/2)/blockSize;
                    curBlock.y = (int)(mouseY+playerY-height/2)/blockSize;
                    if(startBreak == -1 || !blockTypes.get(blockId).isSolid())startBreak = millis();

                }
            }
        }else {
            curBlock.x = (int)(mouseX+playerX-width/2)/blockSize;
            curBlock.y = (int)(mouseY+playerY-height/2)/blockSize;
            startBreak = -1;
        }

        //clear();
        if(selectedItem > inventory.size()-1) selectedItem = 0;
        if(selectedItem < 0)selectedItem = inventory.size()-1;
        if(inventory.size() == 0) selectedItem = 0;
        
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
   
    //inventory
        xOffset = -(int)((inventory.size()*blockSize)/2)-blockSize;

        stroke(0);
        for(Integer entry : inventory.keySet()) {
        if(inventory.get(entry) <= 0) removeItems.add(entry);
        else {
            xOffset+=blockSize;
            blockTypes.get(entry).draw((width/2)+xOffset,height-blockSize);
            noFill();
            rect((width/2)+xOffset,height-blockSize,blockSize,height-blockSize);
            fill(255);
            textSize(12);
            text(inventory.get(entry),(width/2)+xOffset+blockSize-textWidth(inventory.get(entry).toString()),height);
        } 
        }
        for(Integer i: removeItems) {
            inventory.remove(i);
        }
        removeItems.clear();
        if(inventory.size() > 0) {
            stroke(255);
            noFill();
            xOffset = -(int)((inventory.size()*blockSize)/2);
            rect((width/2)+xOffset+selectedItem*blockSize,height-blockSize,blockSize,height-blockSize);
        }
    }else {
        drawGUI();
    }
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
    if(!main) {     
        int blockId = worlds.get(currentWorld).getBlock(mouseX+playerX-width/2,mouseY+playerY-height/2);
        
        if(guiIsActive && mouseButton == LEFT) {
            if(mouseX > width/4+100 && mouseX < width/4+132 && mouseY > height/4 +80 && mouseY < height/4 +80 + 32) {
                planetDisplay--;
                if(planetDisplay < 0) planetDisplay = worlds.size()-1;
            }else if(mouseX > width/4+100+width/4 && mouseX < (width/4+100+width/4)+32 && mouseY > height/4 +80 && mouseY < height/4 +80 + 32) {
                planetDisplay++;
            }else if(mouseX > width/2-80 && mouseX < width/2+80 && mouseY > height/2-20 && mouseY < height/2+20) {
                flyToNewPlanet(planetDisplay);
            }
        }
        if(mouseButton == RIGHT) {
            if(!(sqrt(sq(playerX - (mouseX+playerX-width/2))+sq(playerY-(mouseY+playerY-height/2)))/blockSize > 5)) {
                if(inventory.size() > 0 && !blockTypes.get(blockId).isSolid()) {          
                    worlds.get(currentWorld).changeBlock(mouseX+playerX-width/2,mouseY+playerY-height/2,(int)inventory.keySet().toArray()[selectedItem]);
                    inventory.put((int)inventory.keySet().toArray()[selectedItem],inventory.get((int)inventory.keySet().toArray()[selectedItem])-1);
                }
            }
        }
    }else {
        if(mouseX > width/2-150 && mouseX < width/2+150 && mouseY > height-height/16-25 && mouseY < height-height/16+25) main = !main;
    }
}

public void drawGUI() {
    //Gui
    fill(200,200);
    image(loadImage("blocks/StarBackground.png"),width/4,height/4,width/2,height/2);
    if(mouseX > width/4+100 && mouseX < width/4+132 && mouseY > height/4 +80 && mouseY < height/4 +80 + 32) image(loadImage("blocks/arrowFill.png"),width/4+116,height/4 +80);
    else image(loadImage("blocks/arrow.png"),width/4+116,height/4 +80);
    if(mouseX > width/4+100+width/4 && mouseX < (width/4+100+width/4)+32 && mouseY > height/4 +80 && mouseY < height/4 +80 + 32) image(loadImage("blocks/arrow1Fill.png"),width/4+100+width/4,height/4 +80);
    else image(loadImage("blocks/arrow1.png"),width/4+100+width/4,height/4 +80);
    
    fill(255);
    textSize(30);
    text("Planet Menu",width/2-textWidth("Planet Menu")/2,height/4+50);
    fill(100,100);
    if(mouseX > width/2-80 && mouseX < width/2+80 && mouseY > height/2-20 && mouseY < height/2+20) fill(255,100);
    rect(width/2-80,height/2-20,160,40);

    //Planet information
    fill(255);
    if(worlds.get(planetDisplay) == null) createPlanet();
    text(worlds.get(planetDisplay).name(),width/2-textWidth(worlds.get(planetDisplay).name())/2,height/4+105);

    text("Current planet: " + worlds.get(currentWorld).name(),width/2-textWidth("Current planet: " + worlds.get(currentWorld).name())/2,-50+height*3/4);
    textSize(20);
    stroke(255);
    text("Travel to planet",width/2-textWidth("Travel to planet")/2,height/2+8);
    text(worlds.get(planetDisplay).description(),width/2-textWidth(worlds.get(planetDisplay).description())/2,height/4+150);
}

public void drawMain() {
    image(mainMenu,0,0); 
    
    stroke(0);
    if(mouseX > width/2-150 && mouseX < width/2+150 && mouseY > height-height/16-25 && mouseY < height-height/16+25) fill(200,200); 
    else noFill(); 
    rect(width/2-150,height-height/16-25,300,50);
    textSize(30);
    fill(255);
    text("Play Game",width/2-textWidth("Play Game")/2,height-height/16+12.5f);

}

public void createPlanet() {
    World w = new World();
    worlds.put(worldCount,w);
}

//method to move the player to a new planet
public void flyToNewPlanet(int planetNum) {
    if(planetNum != currentWorld) {
        this.currentWorld = planetNum;
        playerX = 16*blockSize*chunkSize;
        playerY = -10;
        if(!worlds.get(currentWorld).generated()) worlds.get(currentWorld).generatePlanet();
        guiIsActive = false;
    }
}
public String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
class Block{

    private int id;
    private PImage texture;
    private boolean solid;

    int hardness;

    int c;
    boolean ore;

    Block(String texture,boolean solid,int hardness,boolean ore) {
        numBlocks++;
        this.id = numBlocks;
        this.texture = loadImage("Blocks/"+texture);
        this.solid = solid;
        this.hardness = hardness;
        this.ore = ore;
        if(ore) {
            c = color(random(255),random(255),random(255));
        }
    }

    public boolean isSolid() {
        return solid;
    }


    public void draw(int x,int y) {
        
        if(ore) {
            stroke(0,0);
            fill(c);
            rect(x,y,blockSize,blockSize);
        }
        image(texture, x, y,blockSize,blockSize);
    }

    public int getId() {
        return id;
    }

    public int hardness() {
        return hardness;
    }
}


class Chunk {
    int[][] blocks = new int[chunkHeight][chunkSize];
    int x;
    
    float surfaceSize;
    float caveAmount;

    Chunk(int x,int oreStart,int oreEnd,float surfaceSize,float caveAmount) {
        this.x = x;
        this.surfaceSize = surfaceSize;
        this.caveAmount = caveAmount;
            for(int yy = 0; yy < chunkHeight; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    float n = noise((xx+x*chunkSize)*0.04f, (yy)*0.04f,0)*255;
                    
                    if(yy < surfaceSize) {
                        
                        n *=surfaceSize/(yy);
                        if(n < caveAmount && blocks[yy-1][xx] == 0) {
                            row[xx] = 2;
                        }else if(n < caveAmount && blocks[yy-1][xx] == 2) {
                            row[xx] = 4;
                        }else if(n < caveAmount && n > 100 && blocks[yy-1][xx] == 4) {
                             row[xx] = 4;
                        }else {
                            if(n > caveAmount) row[xx] = 0;
                            else row[xx] = 1;   
                        }
                    }else {
                        if(n > caveAmount)  row[xx] = 0;
                        else {
                            for(int i = oreStart; i < oreEnd; i++) {
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
                    if(yy == chunkHeight-1) row[xx] = 3;       
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
   
        if(floor(yy) < 0 || floor(yy) > chunkHeight-1) return 0;
        if(floor(xx) < 0 || floor(xx) > chunkSize-1) return 0;
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
        if(pY < surfaceSize) pY = surfaceSize;
        image(background,x*chunkSize*blockSize,pY*blockSize);
    }

}
class Gui {
    public boolean isActive = false;
    public ArrayList<UIPanel> panels = new ArrayList<UIPanel>();

    public void drawUI() {
        if(isActive) {
            for(UIPanel u: panels) {
                u.drawP();
            }
        }
    }

    public void addPanel(UIPanel u) {  
        panels.add(u);
    }

    public void setActive() {
        isActive = !isActive;
    }

    public boolean isActive() {
        return isActive;
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
    boolean generated = false;
    int oreStartIndex;
    int oreEndIndex;
    int numOre;
    String name = "";

    float surfaceSize = 30;
    float caveAmount = 150; //lower is more

    World() {
        worldCount++;
        id = worldCount;
        surfaceSize = random(5,100);
        caveAmount = random(100,250);
        numOre = (int)random(1,5);
        genName();   
    }

    public void render() {
        if(generated == false) generatePlanet();
        float pX = (playerX/blockSize)/(float)chunkSize-chunkRenderDist;
        if (pX < 0) pX = 0;
        float pXP = (playerX/blockSize)/(float)chunkSize + chunkRenderDist;
        if(pXP > chunks.length) pXP = chunks.length;
            for(float x = pX; x < pXP; x++) {
                chunks[(int)x].render();
            }
    }

    public int getBlock(float x,float y) {
        if((int)(x/(chunkSize*blockSize)) < 0 || (int)(x/(chunkSize*blockSize)) > chunks.length-1) return 0;
        return chunks[(int)(x/(chunkSize*blockSize))].getBlock(x,y);
    }

    public void changeBlock(float x,float y,int id) {
        if((int)(x/(chunkSize*blockSize)) < 0 || (int)(x/(chunkSize*blockSize)) > chunks.length-1) return;
        chunks[(int)(x/(chunkSize*blockSize))].changeBlock(x,y,id);
    }


    private void genName() {
        for(int i = 0; i < random(2,5); i++) {
            name = name + alphabet[(int)random(0,25)];
        }
        name = name + " " + (int)random(0,99999);
    }

    public String name() {
        return name;
    }

    public String description() {
        String desc = "";
        if(caveAmount < 120) desc = desc + "Massive caverns \n";
        else if(caveAmount < 150) desc = desc + "Lots of caves \n";
        else if(caveAmount < 250) desc = desc + "Solid crust \n";

        if(surfaceSize > 80) desc = desc + "Mountainous \n";
        else if(surfaceSize > 50) desc = desc + "Hilly \n";
        else if(surfaceSize > 20) desc = desc + "Mostly flat \n";
        else desc = desc + "Barren \n";

        if(numOre > 3) desc = desc + "Resource rich \n";
        else desc = desc + "Some resources \n";
        return desc;
    }

    public void generatePlanet() {
        noiseSeed((long)random(-99999999,99999999));
        chunks = new Chunk[chunkSize];   
        oreStartIndex = blockTypes.size()-1; 
        for(int i = 0; i < numOre; i++) {
            Block b = new Block("Iron.png",true,8,true);
            blockTypes.put(numBlocks,b);
        }
        oreEndIndex = blockTypes.size()-1;
        for(int x = 0; x < chunks.length; x++) {
            Chunk c = new Chunk(x,oreStartIndex,oreEndIndex,surfaceSize,caveAmount);
            chunks[x] = c;
        }
        generated = true;
    }
    public boolean generated() {
        return generated;
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
