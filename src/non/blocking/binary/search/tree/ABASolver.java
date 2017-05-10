package non.blocking.binary.search.tree;

/**
 * Wrapper class for the stored values. The insantiation is protected, so only 
 * the getter functions may be of use.
 * 
 */
public class ABASolver<V> {
    private V value;
    private int id;

    protected ABASolver(V value, int id) {
        this.value = value;
        this.id = id;
    }

    /**
     * Getter of the value. The stored object is the one specified by the user. 
     * The interesting feature of this class, whose usage is optional, is the id.
     * 
     * @return the value inside the wrapper
     */
    public V getValue() {
        return value;
    }

    /**
     * Getter of the id. Each value has an unique ID the defines it. 
     * This is achieved though the class 
     * java.util.concurrent.atomic.AtomicInteger.
     * 
     * @return the id associated with given value, it is unique
     */
    public int getId() {
        return id;
    }
    
}
