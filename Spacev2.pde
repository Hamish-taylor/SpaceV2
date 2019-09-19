HashMap<Integer,Block> blockTypes = new HashMap<Integer,Block>();

//pixels
public int blockSize = 10;
float movementSpeed = 0.10;

//blocks
public int chunkSize = 32;
float playerX = 0;
float playerY = 0;

//chunks
public int height = 100;

World w;

void setup() {
    size(1000,1000);
    Block b = new Block(0,color(100,255));
    blockTypes.put(b.getId(),b);
    w = new World(1);
}


void draw() {
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