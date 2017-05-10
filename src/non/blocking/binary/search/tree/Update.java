package non.blocking.binary.search.tree;

import java.util.concurrent.atomic.AtomicStampedReference;

class Update {
    AtomicStampedReference<Info> infoRef;

    public Update(Info info, int state) {
        this.infoRef = new AtomicStampedReference<>(info,state);
    }
    
    public int getState() {
        return infoRef.getStamp();
    }
    
    
    public Info getInfo() {
        return infoRef.getReference();
    }
    
    public AtomicStampedReference<Info> getInfoRef() {
        return infoRef;
    }
    
    public void setInfoRef(AtomicStampedReference<Info> info) {
        this.infoRef = info;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Update){
            Update other=(Update) obj;
            AtomicStampedReference<Info> otherRef=other.getInfoRef();
            return infoRef.getStamp()==otherRef.getStamp()
                    && infoRef.getReference()==otherRef.getReference();
        }
        return false;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Update(getInfo(), getState());
    }
    
}