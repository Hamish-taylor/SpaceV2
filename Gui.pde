class Gui {
    public boolean isActive = false;
    public ArrayList<UIPanel> panels = new ArrayList<UIPanel>();

    public void drawUI() {
        if(isActive) {
            for(UIPanel u: panels) {
                u.drawP();
            }
        }
    }

    public void addPanel(UIPanel u) {  
        panels.add(u);
    }

    public void setActive() {
        isActive = !isActive;
    }

    public boolean isActive() {
        return isActive;
    }

}