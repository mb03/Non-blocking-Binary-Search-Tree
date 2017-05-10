
import non.blocking.binary.search.tree.BinarySearchTree;


public class Example {
    public static void main(String[] args) {
        BinarySearchTree<String,Object> bst=new BinarySearchTree<>();
        bst.Insert("Hello","World!" );
        bst.Insert("Value",1 );
        bst.Delete("Value");
    }
}
