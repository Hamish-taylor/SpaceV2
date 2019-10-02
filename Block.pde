class Block{

    private int id;
    private PImage texture;
    private boolean solid;

    int hardness;

    Block(int id,String texture,boolean solid,int hardness) {
        this.id = id;
        this.texture = loadImage("Blocks/"+texture);
        this.solid = solid;
        this.hardness = hardness;
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

    public int hardness() {
        return hardness;
    }
}


