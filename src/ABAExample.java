
import non.blocking.binary.search.tree.ABASolver;
import non.blocking.binary.search.tree.BinarySearchTree;

public class ABAExample {
    public static void main(String[] args) {
        BinarySearchTree<String,Object> bst=new BinarySearchTree<>();
        bst.Insert("Hello","World!" );
        ABASolver<Object> as=bst.Find_ABA("Hello");
        System.out.println(as.getId());
        bst.Delete("Hello");
        bst.Insert("Hello","World2!" );
        as=bst.Find_ABA("Hello");
        System.out.println(as.getId());
    }
}
