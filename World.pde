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