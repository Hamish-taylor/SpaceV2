class Block{

    private int id;
    private PImage texture;
    private boolean solid;


    Block(int id,String texture,boolean solid) {
        this.id = id;
        this.texture = loadImage("Blocks/"+texture);
        this.solid = solid;
    }

    public boolean isSolid() {
        return solid;
    }


    public void draw(int x,int y) {
        image(texture, x, y,blockSize,blockSize);
    }

    public int getId() {
        return id;
    }


}


