class Chunk {
    int[][] blocks = new int[chunkHeight][chunkSize];
    int x;
    
    float surfaceSize;
    float caveAmount;

    Chunk(int x,int oreStart,int oreEnd,float surfaceSize,float caveAmount) {
        this.x = x;
        this.surfaceSize = surfaceSize;
        this.caveAmount = caveAmount;
            for(int yy = 0; yy < chunkHeight; yy++) {
                int[] row = new int[chunkSize];
                for(int xx = 0; xx < chunkSize; xx++) {
                    float n = noise((xx+x*chunkSize)*0.04, (yy)*0.04,0)*255;
                    
                    if(yy < surfaceSize) {
                        
                        n *=surfaceSize/(yy);
                        if(n < caveAmount && blocks[yy-1][xx] == 0) {
                            row[xx] = 2;
                        }else if(n < caveAmount && blocks[yy-1][xx] == 2) {
                            row[xx] = 4;
                        }else if(n < caveAmount && n > 100 && blocks[yy-1][xx] == 4) {
                             row[xx] = 4;
                        }else {
                            if(n > caveAmount) row[xx] = 0;
                            else row[xx] = 1;   
                        }
                    }else {
                        if(n > caveAmount)  row[xx] = 0;
                        else {
                            for(int i = oreStart; i < oreEnd; i++) {
                                float rand = i*9999;
                                float nn = noise((xx+x*chunkSize+rand)*(0.1), (yy+rand)*(0.1),0)*255;
                                if(nn > 180) {
                                    row[xx] = i;
                                    i = 11111;
                                }
                                else {
                                    row[xx] = 1;                                    
                                }
                            }                           
                        }  
                    }   
                    if(yy == chunkHeight-1) row[xx] = 3;       
            }  
            blocks[yy] = row;
        }
    }

    public void render() {
        drawBackground();
        float pY = playerY/(float)blockSize - verticalRenderDist;
        if (pY < 0) pY = 0;
        float pYP = playerY/(float)blockSize + verticalRenderDist;
        if(pYP > blocks.length) pYP = blocks.length;
            for(float yy = pY; yy < pYP; yy++) {
                for(int xx = 0; xx < chunkSize; xx++) {
                blockTypes.get(blocks[(int)yy][xx]).draw((xx*blockSize)+x*chunkSize*blockSize,((int)yy*blockSize));
            }
        }
        
    }
    public int getBlock(float xx,float yy) {
        xx /=blockSize;
        yy/=blockSize;
        
        xx -= chunkSize*x;
   
        if(floor(yy) < 0 || floor(yy) > chunkHeight-1) return 0;
        if(floor(xx) < 0 || floor(xx) > chunkSize-1) return 0;
        return blocks[floor(yy)][floor(xx)];
        
    }

    public void changeBlock(float xx,float yy,int id) {
        xx /=blockSize;
        yy/=blockSize;
        
        xx -= chunkSize*x;
   
        if(floor(yy) < 0 || floor(yy) > chunkHeight) return;
        if(floor(xx) < 0 || floor(xx) > chunkSize) return;
        blocks[floor(yy)][floor(xx)] = id;
    }

    void drawBackground() {
        float pY = ((float)playerY/(float)blockSize)-16;
        if(pY < surfaceSize) pY = surfaceSize;
        image(background,x*chunkSize*blockSize,pY*blockSize);
    }

}