class World {
    
    int id;
    Chunk[] chunks; 
    boolean generated = false;

    String name = "";

    World() {
        worldCount++;
        id = worldCount;
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
        if((int)(x/(chunkSize*blockSize)) < 0 || (int)(x/(chunkSize*blockSize)) > chunks.length) return 0;
        return chunks[(int)(x/(chunkSize*blockSize))].getBlock(x,y);
    }

    public void changeBlock(float x,float y,int id) {
        if((int)(x/(chunkSize*blockSize)) < 0 || (int)(x/(chunkSize*blockSize)) > chunks.length) return;
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

    public void generatePlanet() {
        noiseSeed((long)random(-99999999,99999999));
        chunks = new Chunk[chunkSize];    
        for(int i = 0; i < random(1,5); i++) {
            
        }
        for(int x = 0; x < chunks.length; x++) {
            Chunk c = new Chunk(x);
            chunks[x] = c;
        }
        generated = true;
    }
    public boolean generated() {
        return generated;
    }
}