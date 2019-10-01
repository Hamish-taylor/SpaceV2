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