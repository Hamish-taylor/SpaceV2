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
