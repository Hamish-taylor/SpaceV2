class World {



    Chunk[][] chunks; 
    int width;


    World(int width) {
        this.width = width;
        chunks = new Chunk[200][width];
        for(int y = 0; y < chunks.length; y++) {
            for(int x = 0; x < chunks[0].length; x++) {
                Chunk c = new Chunk(x*blockSize,y*blockSize);
                chunks[y][x] = c;
            }
        }
    }

    public void render() {
        for(int y = 0; y < 10; y++) {
            for(int x = 0; x < width; x++) {
                chunks[y][x].render();
            }
        }
    }

}