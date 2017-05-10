package non.blocking.binary.search.tree;

class Info {
    public String getSimpleName(){
        return getClass().getSimpleName()+"@"+System.identityHashCode(this);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); 
    }
    
}

class DInfo extends Info{
    Internal gp,p;
    Node l;
    Update pupdate;
    
    public DInfo(Internal gp, Internal p, Node l, Update pupdate) {
        this.gp = gp;
        this.p = p;
        this.l = l;
        this.pupdate = pupdate;
    }
    
    public Internal getGp() {
        return gp;
    }
    
    public void setGp(Internal gp) {
        this.gp = gp;
    }
    
    public Internal getP() {
        return p;
    }
    
    public void setP(Internal p) {
        this.p = p;
    }
    
    public Node getL() {
        return l;
    }
    
    public void setL(Node l) {
        this.l = l;
    }
    
    public Update getPupdate() {
        return pupdate;
    }
    
    public void setPupdate(Update pupdate) {
        this.pupdate = pupdate;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DInfo){
            DInfo d=(DInfo) obj;
            return d.getGp().equals(gp) && d.getL().equals(l)&&d.getP().equals(p)&&d.getPupdate().equals(pupdate);
        }
        return false;
    }
}

class IInfo extends Info {
    Internal p;
    Internal newInternal;
    Node l;
    
    public IInfo(Internal p, Node l, Internal newInternal) {
        this.p=p;
        this.newInternal = newInternal;
        this.l = l;
    }
    
    public Internal getP() {
        return p;
    }
    
    public void setP(Internal p) {
        this.p=p;
    }
    
    public Internal getNewInternal() {
        return newInternal;
    }
    
    public void setNewInternal(Internal newInternal) {
        this.newInternal = newInternal;
    }
    
    public Node getL() {
        return l;
    }
    
    public void setL(Node l) {
        this.l = l;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof IInfo){
            IInfo i=(IInfo) obj;
            return i.getL().equals(l) && i.getP().equals(p) && i.getNewInternal().equals(newInternal);
        }
        return false;
    }
}