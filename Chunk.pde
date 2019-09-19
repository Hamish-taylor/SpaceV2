class Chunk {
    int[][] blocks = new int[chunkSize][chunkSize];
    int x;
    int y;
    
    Chunk(int x, int y) {
        this.x = x;
        this.y = y;
            for(int yy = 0; yy < chunkSize; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    row[xx] = 0;
            }  
            blocks[yy] = row;
        }
    }

    public void render() {
        translate(x*chunkSize, y*chunkSize);
            for(int yy = 0; yy < blocks.length; yy++) {
                for(int xx = 0; xx < blocks.length; xx++) {
                blockTypes.get(blocks[yy][xx]).draw(xx*blockSize,yy*blockSize);
            }
        }
         translate(-x*chunkSize, -y*chunkSize);
    }

}