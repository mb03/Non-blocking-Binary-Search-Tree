## Non-blocking Binary Search Tree

The present project is a fully customizable implementation of a Non-blocking Binary Search Tree, as described in the related [paper](http://delivery.acm.org/10.1145/1840000/1835736/p131-ellen.pdf?ip=193.205.210.41&id=1835736&acc=ACTIVE%20SERVICE&key=296E2ED678667973%2E532136EDD1F8E584%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35&CFID=755787675&CFTOKEN=37018692&__acm__=1493290649_24c43d99d03a765400030cdee77ec1fa).
This is an implementation of a **thread-safe Binary Search Tree** in **Java**.
The tree allows to store key/value pairs, and to retrieve contained items in a multi-threaded environment, ***without locks***. Both key and value can be of any type, but the first has to implement the **comparable** interface. For this reason, native types cannot be used, however any class that implements *comparable* interface can be used, like Integer, Double and String class. 

### Testing

We do not provide a formal prove or check via formal methods, since we do not know a specific tool encompassing inheritance and polymorphism. For this reason, we provide a test that suggests the correctness of our implementation. 
This test verifies whether the evolution of the tree and its final content match or not. This test is performed storing the additions and deletions for each thread and for each key. Eventually correctness is verified matching additions and deletions of each key and the actual content of the tree. We executed many time this test with an high variety of settings; each execution consists of an high number of concurrent threads and an high number of operations on a small number of keys. The operations performed by each thread are random. Our algorithm always passed this test with any setting we tried, whereas many other algorithms publicly available on github failed. This is the reason that led us to make this code available.

### Additional Features and Differences

This implementation provides also a simple solution to the ABA problem, using timestamps. The Binary Search Tree class provides two functions to address the ABA problem. The Find_ABA and Delete_ABA functions return an object of the ABASolver class. The ABASolver class contains a unique integer that identifies the object and the stored object matching the provided key. 
This feature allows each thread to check whether the retrieved object has been removed and then replaced with a new object.
There are some differences from original code due to the presence of templates, but there are also 3 additional conditions on the helper functions that appear only in our code.
For these differences, and for the rest of the code, *we appreciate any help or suggestion about bugs or enhancements*.

### USE, INSTALL, COMPILE
This library was developed using java 7. We used the provided tools, so it may not have backward compatibility with older versions. We suppose that for recent ones, little tweaks may suffice.
To use the library, you can import the java files or build a jar.
In order to perfrorm the latter operation, you can use either maven or ant in the base directory, with the following syntax:
* ant -f ant_build.xml (*jar* is located at *./dist/Non-BlockingBinarySearchTree.jar*)
    or
* mvn clean install -f maven_build.xml (*jar* is located at *./target/Non-BlockingBinarySearchTree.jar*)

Then you have a jar in the output folder and you can import it as a library.

### Provided Methods
###### BinarySearchTree
* **public BinarySearchTree()**: constructor, it is instantiated as a normal class. E.g. *BinarySearchTree<Integer,Object> nbbst=new BinarySearchTree();*.
* **public boolean Insert(K key, V value)**: Insert the value with given key. Return whether it is successful, therefore if no other item with given key is present.
* **public V Find(K key)**: Search and returns the value defined by given key if present, *null* othewise.
* **public ABASolver<V> Find_ABA(K key)**: Search and return the value defined by given key, inside the wrapper class ABASolver, if present, *null* othewise.
* **public V Delete(K key)**: Search and deletes the couple key-value defined by given parameter, if present. Returns *null* if no such element is found.
* **public ABASolver<V> Delete_ABA(K key)**: Search and deletes the couple key-value defined by given parameter, inside the wrapper class ABASolver, if present. Returns *null* if no such element is found.
###### ABASolver
* **public V getValue()**: Given the value type (*V*) of the tree, it returns the element contained inside the class.
* **public int getId()**: Returns an unique identifier for the node.

### LICENSE
The code ships with the [GNU Affero General Public License](https://www.gnu.org/licenses/agpl.txt).

### EXAMPLES
###### Normal
A simple example, extracted from source files:
```java
import non.blocking.binary.search.tree.BinarySearchTree;

public class Example {
   public static void main(String[] args) {
    BinarySearchTree<String,Object> bst=new BinarySearchTree<>();
    bst.Insert("Hello","World!" );
    bst.Insert("Value",1 );
    bst.Delete("Value");
  }
}
```
###### ABA
An ABA example, extracted from source files. Notice that the id should be different for the two values, since they are different objects.
```java
import non.blocking.binary.search.tree.ABASolver;
import non.blocking.binary.search.tree.BinarySearchTree;

public class ABAExample {
    public static void main(String[] args) {
        BinarySearchTree<String,Object> bst=new BinarySearchTree<>();
        bst.Insert("Hello","World!" );
        ABASolver<Object> as=bst.Find_ABA("Hello");
        System.out.println(as.getId());
        bst.Delete("Hello");
        bst.Insert("Hello","World!2" );
        as=bst.Find_ABA("Hello");
        System.out.println(as.getId());
    }
}
```
### Contributors
  - [mb03](https://github.com/mb03)
  - [giTorto](https://github.com/giTorto)


