
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



void setup() {
    player = loadImage("Blocks/Player.png");
    background = loadImage("Blocks/BackGround1.png");
    mainMenu = loadImage("Blocks/Main Menu.png");

    size(1000,1000,P2D);
    //sky = loadImage("Blocks/Sky.png");
    loadFiles();

    createPlanet();
}

void loadFiles() {
    String[] temp = loadStrings("Blocks/Blocks.txt");
    
    for(int i = 0; i < temp.length; i++) {
        String[] lines = split(temp[i], ' ');
        Block b = new Block(lines[0],Boolean.valueOf(lines[1]),Integer.valueOf(lines[2]),false);
        blockTypes.put(numBlocks,b);
    }
}

void keyPressed() {
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

void mouseWheel(MouseEvent event) {
  selectedItem += event.getCount();
}

void draw() {
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
        translate(-playerX+(width/2.0), -playerY+(height/2.0));
        
        playerX = (int) playerX;
        playerY = (int) playerY;
        worlds.get(currentWorld).render();
        fill(255);
        //rect(playerX-(playerSize/2),playerY-(playerSize/2),playerSize,playerSize);
        image(player, playerX-(playerSize/2), playerY-(playerSize/2), playerSize, playerSize);

        noFill();
        stroke(0);
        rect((int)((mouseX+playerX-width/2)/blockSize)*blockSize,(int)((mouseY+playerY-height/2)/blockSize)*blockSize,blockSize,blockSize);

        translate(-(-playerX+(width/2.0)), -(-playerY+(height/2.0)));
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

boolean doCollision(String dir) {
    if(dir.equals("up")) {
        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX-(0.5*playerSize),playerY-abs(yVelocity)-(0.5*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX+(0.5*playerSize),playerY-abs(yVelocity)-(0.5*playerSize))).isSolid())) {
             
            if(((((int)((playerX+(0.5*playerSize))/blockSize))*blockSize) == playerX+0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY-abs(yVelocity)-(0.5*playerSize))).isSolid()) return false;
            if(((((int)((playerX-(0.5*playerSize))/blockSize))*blockSize) == playerX-0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY-abs(yVelocity)-(0.5*playerSize))).isSolid()) return false;
            if((playerY-0.5*blockSize)-(((int)((playerY-(0.5*playerSize))/blockSize))*blockSize) != 0) {
                playerY+=(((int)((playerY+(0.5*playerSize))/blockSize))*blockSize)-(playerY+0.5*blockSize);
            }         
            return true;
        }
    }
    if(dir.equals("down")) {
        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX-(0.5*playerSize),playerY+movementSpeed+(0.5*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX+(0.5*playerSize),playerY+movementSpeed+(0.5*playerSize))).isSolid())) {
            
            if(((((int)((playerX+(0.5*playerSize))/blockSize))*blockSize) == playerX+0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY+movementSpeed+(0.5*playerSize))).isSolid()) return false;
            if(((((int)((playerX-(0.5*playerSize))/blockSize))*blockSize) == playerX-0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX,playerY+movementSpeed+(0.5*playerSize))).isSolid()) return false;

            if((playerY+0.5*blockSize)-(((int)((playerY+(0.5*playerSize))/blockSize))*blockSize) != 0) {
                playerY-=(playerY+0.5*blockSize)-(((int)((playerY+(0.5*playerSize))/blockSize))*blockSize)-blockSize;
            }  
            return true;
        }
    }
    if(dir.equals("left")) {

        //if(blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5*playerSize),playerY)).isSolid()) return true;

        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5*playerSize),playerY-(0.5*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5*playerSize),playerY+(0.5*playerSize))).isSolid()) 
        ) {
            
            if(((((int)((playerY+(0.5*playerSize))/blockSize))*blockSize) == playerY+0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5*playerSize),playerY)).isSolid()) return false;
            if(((((int)((playerY-(0.5*playerSize))/blockSize))*blockSize) == playerY-0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX-movementSpeed-(0.5*playerSize),playerY)).isSolid()) return false;

            if((playerX+0.5*blockSize)-(((int)((playerX+(0.5*playerSize))/blockSize))*blockSize) != 0) {
                playerX+=(((int)((playerX+(0.5*playerSize))/blockSize))*blockSize)-(playerX+0.5*blockSize);
            }     
            return true;
        }
    }
    if(dir.equals("right")) {
        if((blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5*playerSize),playerY+(0.5*playerSize))).isSolid()) ||
        (blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5*playerSize),playerY-(0.5*playerSize))).isSolid())) {          
            if(((((int)((playerY+(0.5*playerSize))/blockSize))*blockSize) == playerY+0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5*playerSize),playerY)).isSolid()) return false;
            if(((((int)((playerY-(0.5*playerSize))/blockSize))*blockSize) == playerY-0.5*blockSize) && !blockTypes.get(worlds.get(currentWorld).getBlock(playerX+movementSpeed+(0.5*playerSize),playerY)).isSolid()) return false;
            if((playerX+0.5*blockSize)-(((int)((playerX+(0.5*playerSize))/blockSize))*blockSize) != 0) {
                playerX+=(((int)((playerX+(0.5*playerSize))/blockSize))*blockSize)-(playerX+0.5*blockSize)+blockSize;
            }      
            return true;
        }
    }  
    return false;
}

boolean isGrounded() {
    return(doCollision("down"));
}

void mousePressed() {
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

void drawGUI() {
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

void drawMain() {
    image(mainMenu,0,0); 
    
    stroke(0);
    if(mouseX > width/2-150 && mouseX < width/2+150 && mouseY > height-height/16-25 && mouseY < height-height/16+25) fill(200,200); 
    else noFill(); 
    rect(width/2-150,height-height/16-25,300,50);
    textSize(30);
    fill(255);
    text("Play Game",width/2-textWidth("Play Game")/2,height-height/16+12.5);

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