package non.blocking.binary.search.tree;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Binary Search Tree based on the paper "Non-blocking binary search trees" 
 * (By Faith Ellen et al.).
 * It provides dictionary like functionalities for storing couple key-value in
 * a concurrent manner without any limiation, using atomic instruction to avoid 
 * locks.
 * This implementation has generic typing and provides a wrapper class to 
 * address the ABA problem
 * 
 * @param <K> The Key type. This class must implement the Comparable interface
 * @param <V> The Value type. This class can be of any type. 
 */
public class BinarySearchTree<K extends Comparable<K>,V> {
    private static int infiniteType1=2;
    private static int infiniteType2=1;
    private K infiniteKeyPlaceholder=null;
    private AtomicInteger abaUniqueIdentifier;
    private Internal root;
    private static final int CLEAN=0;
    private static final int DFLAG=1;
    private static final int IFLAG=2;
    private static final int MARK=3;
    private static final String[] names={"CLEAN","DFLAG","IFLAG","MARK"};

    /**
     * Constructor, takes two templates parameter, key and value classes. 
     * You should instantiate this class in a single threaded enviroment.
     */
    public BinarySearchTree() {
        root=new Internal(infiniteKeyPlaceholder,CLEAN,infiniteType1);
        root.setUpdate(new Update(null,CLEAN));
        Node left=new Leaf(infiniteKeyPlaceholder,infiniteType2);
        Node right=new Leaf(infiniteKeyPlaceholder,infiniteType1);
        root.setLeft(left);
        root.setRight(right);
        abaUniqueIdentifier=new AtomicInteger();
    }
    
    private SearchReturnValue Search(K key) {
        Internal gp=null;
        Internal p=null;
        Node l=root;
        Update pupdate=null, gpupdate=null;
        int i=0;
        while(l instanceof Internal){
            gp=p;
            p=(Internal) l;
            gpupdate=(Update) copy(pupdate);
            pupdate=(Update) copy(p.getUpdate());
            if (l.isGreater(key)){
                l=p.getLeft();
            }else{                 
                l=p.getRight();
            }
            i++;
        }
        return new SearchReturnValue(gp, p, l, pupdate, gpupdate);
    }
    
    /**
     * Find and returns, in a concurrent-safe way, the value associated 
     * to given key
     * 
     * @param key Key of the value you are looking for.
     * @return The ABASolver wrapper containing the inserted value, if present,
     *         null otherwise.
     */
    public ABASolver<V> Find_ABA(K key){
        SearchReturnValue search = Search(key);
        Node l=search.getL();
        if(l.hasSameKey(key)){
            return l.getValue();
        }else{
            return null;
        }
    }

    /**
     * Find and returns, in a concurrent-safe way, the value associated 
     * to given key
     * 
     * @param key Key of the value you are looking for.
     * @return The inserted value, if present,
     *         null otherwise.
     */
    public V Find(K key){
        ABASolver<V> as = Find_ABA(key);
        if(as!=null)
            return as.getValue();
        return null;
    }
    
    /**
     * Insert a value associated to the given key. Their type depend on the 
     * constructor specification.
     * The value should be non null.
     * 
     * @param key Key to store the value
     * @param value Value associated to the given key
     * @return Whether the insertion was successful or the key is already 
     * present in the tree.
     */
    public boolean Insert(K key, V value){
        Internal newInternal;
        Node newSibiling;
        Node newL=new Leaf(key);
        newL.setValue(new ABASolver<V>(value,abaUniqueIdentifier.getAndIncrement()));
        while(true){
            SearchReturnValue search=Search(key);
            Node l = search.getL();
            Update pupdate = search.getPupdate();
            Internal p = search.getP();
            if (l.hasSameKey(key)){
                return false;
            }
            if(pupdate.getState()!= CLEAN){
                Help(pupdate,search.getP());
            }else{
                if(l.isInfiniteType()){
                    newSibiling=new Leaf(infiniteKeyPlaceholder,l.getInfiniteType());
                }else{
                    newSibiling=new Leaf(l.getKey());
                    newSibiling.setValue(l.getValue());
                }
                if(l.isInfiniteType()){
                    newInternal=new Internal(infiniteKeyPlaceholder,CLEAN,l.getInfiniteType());
                }else{
                    if(l.isGreater(key)){
                        newInternal=new Internal(l.getKey(),CLEAN);
                    }else{
                        newInternal=new Internal(key,CLEAN);
                    }
                }
                if(newSibiling.isGreater(newL)){
                    newInternal.setLeft(newL);
                    newInternal.setRight(newSibiling);
                }else{
                    newInternal.setLeft(newSibiling);
                    newInternal.setRight(newL);
                }
                IInfo op=new IInfo(p,l,newInternal);
                Update result = p.getUpdate();
                if(p.getUpdate().getInfoRef().compareAndSet(pupdate.getInfo(), op, pupdate.getState(), IFLAG)){
                    HelpInsert(op);
                    return true;
                }else{
                    Help(result,p);
                }
            }
        }
    }
    
    private void HelpInsert(Info op_){
        if(op_ instanceof IInfo){
            assert op_!=null;
            IInfo op=(IInfo) op_;
            CAS_child(op.getP(), op.getL(), op.getNewInternal());
            op.getP().getUpdate().getInfoRef().compareAndSet(op, op, IFLAG,CLEAN);
        }
    }
    
    /**
     * Removes the element specified by given key and returns it, if present, 
     * wrapped in the ABASolver class. If not present, it returns null.
     * 
     * @param key The key of the sought value
     * @return The sought value or null, if it is not present
     */
    public ABASolver<V> Delete_ABA(K key){
        while(true){
            SearchReturnValue search = Search(key);
            Node l = search.getL();
            Update pupdate =  search.getPupdate();
            Update gpupdate = search.getGpupdate();
            Internal p = search.getP();
            Internal gp = search.getGp();
            if(!l.hasSameKey(key)){
                return null;
            }
            if(gpupdate.getState()!=CLEAN){
                Help(gpupdate,search.getGp());
            }else if (pupdate.getState()!=CLEAN){
                Help(pupdate,search.getP());
            }else{
                DInfo op=new DInfo(gp,p,l,pupdate);
                Update result =gp.getUpdate();
                if(gp.getUpdate().getInfoRef().compareAndSet(gpupdate.getInfo(), op, gpupdate.getState() , DFLAG)){
                    if(HelpDelete(op)){
                        return l.getValue();
                    }
                }else{
                    Help(result,gp);
                }
            }
        }
    }
    
    /**
     * Removes the element specified by given key and returns it, if present. If
     * not present, it returns null.
     * 
     * @param key The key of the sought value
     * @return The sought value or null, if it is not present
     */
    public V Delete(K key){
        ABASolver<V> as = Delete_ABA(key);
        if(as!=null)
            return as.getValue();
        return null;
    }
    
    private boolean HelpDelete(Info op_){
        //Note: following condition is not present in the paper
        if(op_ instanceof DInfo){
            assert op_!=null;
            DInfo op=(DInfo) op_;
            Update result = op.getP().getUpdate();
            if(op.getP().getUpdate().getInfoRef().compareAndSet(op.getPupdate().getInfo(), op,op.getPupdate().getState() , MARK)||
                    (op.getP().getUpdate().infoRef.getReference().equals(op)&&op.getP().getUpdate().infoRef.getStamp()==MARK )){
                HelpMarked(op);
                return true;
            }else{
                Help(result,op.getP());
                op.getGp().getUpdate().getInfoRef().compareAndSet(op,op,DFLAG, CLEAN);
                return false;
            }
        }
        return false;
    }
    
    private void HelpMarked(Info op_){
        //Note: following condition is not present in the paper
        if(op_ instanceof DInfo){
            assert(op_!=null);
            DInfo op=(DInfo) op_;
            Node other;
            if(op.getP().getRight().equals(op.getL())){
                other=op.getP().getLeft();
            }else{
                other=op.getP().getRight();
            }
            CAS_child(op.getGp(),op.getP(),other);
            op.getGp().getUpdate().getInfoRef().compareAndSet(op,op,DFLAG,CLEAN);
        }
    }
    
    private void Help(Update u, Node n){
        //Note: following conditions are not present in the paper
        if (u.getInfo()!=null && n instanceof Internal){ 
            switch(u.getInfoRef().getStamp()){
                case IFLAG:HelpInsert(  u.getInfo());break;
                case MARK:HelpMarked(  u.getInfo());break;
                case DFLAG:HelpDelete(  u.getInfo());break;
                default:
            }
        }
    }
    
    private void CAS_child(Internal parent,Node old,Node newN){
        assert(parent !=null && newN!=null);
        if(parent.isGreater(newN)){
            parent.getLeftReference().compareAndSet(old, newN);
        }else{
            parent.getRightReference().compareAndSet(old, newN);
        }
    }
    
    private Object copy(Update o){
        if(o!=null){
            try {
                return o.clone();
            } catch (CloneNotSupportedException ex) {
                
            }
        }
        return null;
    }
    //private custom functions for testing
    void getNodes(LinkedList<K> foundNodes) {
        getNodes(foundNodes,root);
    }
    private void getNodes(LinkedList<K> foundNodes,Node ref) {
        if(ref!=null){
            if(ref.isLeaf())
                foundNodes.add((K)ref.getKey());
            getNodes(foundNodes,ref.getLeft());
            getNodes(foundNodes,ref.getRight());
            
        }
    }
    
    class SearchReturnValue {
        private Internal gp;
        private Internal p;
        private Node l;
        private Update pupdate;
        private Update gpupdate;
        
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
        
        public Update getGpupdate() {
            return gpupdate;
        }
        
        public void setGpupdate(Update gpupdate) {
            this.gpupdate = gpupdate;
        }
        
        public SearchReturnValue(Internal gp, Internal p, Node l, Update pupdate, Update gpupdate) {
            this.gp = gp;
            this.p = p;
            this.l = l;
            this.pupdate = pupdate;
            this.gpupdate = gpupdate;
        }
    }
    
}
