class Chunk {
    int[][] blocks = new int[chunkHeight][chunkSize];
    int x;
    int y;
    
    Chunk(int x, int y) {
        this.x = x;
        this.y = y;
            for(int yy = 0; yy < chunkHeight; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    row[xx] = 0;
            }  
            blocks[yy] = row;
        }
    }

    public void render() {
        int pY = (int)playerY - 20;
        if (pY < 0) pY = 0;
        int pYP = (int)playerY + 20;
        if(pYP > blocks[0].length) pYP = blocks[0].length;
        println("pY: "+pY);
        println("pYP" + pYP);

            for(int yy = pY; yy < pYP; yy++) {
                for(int xx = 0; xx < chunkSize; xx++) {
                blockTypes.get(blocks[yy][xx]).draw((xx*blockSize)+x*chunkSize*blockSize,(yy*blockSize)+y*chunkSize*blockSize);
            }
        }
    }

}