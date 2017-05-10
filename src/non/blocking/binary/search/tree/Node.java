package non.blocking.binary.search.tree;

import java.util.concurrent.atomic.AtomicReference;

class Node<K extends Comparable<K>,V>{
    private K key;
    private ABASolver<V> value;
    private int infiniteType;
    AtomicReference<Node> left,right;
    
    public Node() {
        left=new AtomicReference<>();
        right=new AtomicReference<>();
        infiniteType=-1;
    }
    
    public Node(K key) {
        left=new AtomicReference<>();
        right=new AtomicReference<>();
        this.key=key;
        infiniteType=-1;
    }
    public Node(K key,int infiniteType) {
        left=new AtomicReference<>();
        right=new AtomicReference<>();
        this.key=key;
        this.infiniteType=infiniteType;
    }
    
    public ABASolver<V> getValue() {
        return value;
    }
    
    public void setValue(ABASolver<V> value) {
        this.value = value;
    }
    
    boolean isInfiniteType(){
        return infiniteType>0;
    }
    
    public int getInfiniteType() {
        return infiniteType;
    }
    
    boolean isGreater(Node other){
        
        boolean isOtherInf=other.isInfiniteType();
        boolean isInf=isInfiniteType();
        if(isOtherInf && isInf){
            return other.infiniteType<infiniteType;
        }else if(isOtherInf){
            return false;
        }else if(isInf){
            return true;
        }
        return this.key.compareTo((K)other.key)>0;
    }
    
    boolean isGreater(K key){
        if(isInfiniteType()){
            return true;
        }
        return this.key.compareTo(key)>0;
    }
    
    boolean hasSameKey(K key){
        if(isInfiniteType()){
            return false;
        }
        return this.key.compareTo(key)==0;
    }
    
    public K getKey() {
        return key;
    }
    
    public void setKey(K key) {
        this.key = key;
    }
    
    public boolean isLeaf(){
        return (this instanceof Leaf) && !isInfiniteType();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node){
            Node n=(Node) obj;
            return n.getInfiniteType()==infiniteType&&n.getKey()==key;
        }
        return false;
    }
    
    public AtomicReference<Node> getLeftReference() {
        return left;
    }
    
    public void setLeftReference(AtomicReference<Node> left) {
        this.left = left;
    }
    
    public AtomicReference<Node> getRightReference() {
        return right;
    }
    
    public void setRightReference(AtomicReference<Node> right) {
        this.right = right;
    }
    
    public Node getLeft() {
        return left.get();
    }
    
    public Node getRight() {
        return right.get();
    }
    
    public void setLeft(Node left) {
        this.left.set(left);
    }
    
    public void setRight(Node right) {
        this.right.set(right);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()+"@"+System.identityHashCode(this);
    }
    
}

class Leaf<K extends Comparable<K>> extends Node{
    public Leaf(K key) {
        super(key);
    }
    public Leaf(K key,int infiniteType) {
        super(key,infiniteType);
    }
}

class Internal<K extends Comparable<K>> extends Node{
    Update update;
    
    public Internal(Update update, K key) {
        super(key);
        this.update=update;
    }
    public Internal(K key, int state) {
        super(key);
        update=new Update(null,state);
    }
    public Internal(K key, int state,int infiniteType) {
        super(key,infiniteType);
        update=new Update(null,state);
    }
    public Update getUpdate() {
        return update;
    }
    
    public void setUpdate(Update update) {
        this.update=update;
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

