class Block{

    private int id;
    private color texture;


    Block(int id,color texture) {
        this.id = id;
        this.texture = texture;
    }




    public void draw(int x,int y) {
        fill(texture);
        rect(x,y,blockSize,blockSize);
    }

    public int getId() {
        return id;
    }


}


