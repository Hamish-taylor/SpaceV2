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

void setup() {
    size(1000,1000);
    Block b = new Block(0,color(100,255));
    blockTypes.put(b.getId(),b);
    w = new World(100);
}


void draw() {
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

    translate(-playerX+(width/2.0), -playerY+(height/2.0));
    w.render();
    rect(playerX-blockSize/2,playerY-blockSize/2,blockSize,blockSize);
}