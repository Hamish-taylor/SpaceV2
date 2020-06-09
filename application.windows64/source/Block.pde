class Block{

    private int id;
    private PImage texture;
    private boolean solid;

    int hardness;

    color c;
    boolean ore;

    Block(String texture,boolean solid,int hardness,boolean ore) {
        numBlocks++;
        this.id = numBlocks;
        this.texture = loadImage("Blocks/"+texture);
        this.solid = solid;
        this.hardness = hardness;
        this.ore = ore;
        if(ore) {
            c = color(random(255),random(255),random(255));
        }
    }

    public boolean isSolid() {
        return solid;
    }


    public void draw(int x,int y) {
        
        if(ore) {
            stroke(0,0);
            fill(c);
            rect(x,y,blockSize,blockSize);
        }
        image(texture, x, y,blockSize,blockSize);
    }

    public int getId() {
        return id;
    }

    public int hardness() {
        return hardness;
    }
}
