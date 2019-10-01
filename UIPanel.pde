class UIPanel {

    int w;
    int h;
    int x;
    int y;

    int textSize = 30;

    int textX = 0;
    int textY = 0;

    color c = color(0,0,0);
    color textColor;

    String text;

    boolean textModeCenter = false;

    boolean button;

    UIPanel(int x,int y,int w,int h, color c,boolean button) {
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.c = c;
    }
    UIPanel(int x,int y,int w,int h, color c,String text,int textX,int textY,boolean button) {
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.text = text;
        this.textX = textX;
        this.textY = textY;
        this.c = c;
    }

    public void drawP() {
        fill(c);
        rect(x,y,w,h);
        textSize(textSize);
        fill(textColor);
        if (text!= null){
        if(textModeCenter)text(text,(x+w/2)-textWidth(text)/2,y+textSize*0.9);
        else text(text,textX,textY);
        
        } 
    }

    public void addText(String text) {
        this.text = text;
    }

    public void formatText(int textSize,color textColor) {
        this.textSize = textSize;
        this.textColor = textColor;
    }

    public void setTextModeCenter(boolean b) {
        textModeCenter = b;
    }

    public boolean on(int xx,int yy) {
        return (xx > x && xx < x+w && yy > y && yy < y+h);
    }

    public void changeColor(color c) {
        this.c = c;
    }
}