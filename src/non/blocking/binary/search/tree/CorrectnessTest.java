package non.blocking.binary.search.tree;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;

class CorrectnessTest extends Thread{
    BinarySearchTree tree;
    int max;
    int opCount;
    double addPercentage;
    Random r;
    LinkedList<Integer> added= new LinkedList<>();
    LinkedList<Integer> removed= new LinkedList<>();

    public CorrectnessTest(BinarySearchTree tree, int max, int opCount, double addPercentage) {
        this.tree = tree;
        this.max = max;
        this.opCount = opCount;
        this.addPercentage = 1-addPercentage;
        this.r=new Random();
    }
    
    
    public LinkedList<Integer> getAdded() {
        return added;
    }
    
    public LinkedList<Integer> getRemoved() {
        return removed;
    }
    
    @Override
    public void run() {
        for(int i=opCount;i>0;i--){
            Integer key=r.nextInt(max);
            if(r.nextDouble()<addPercentage){
                if(tree.Insert(key,0)){
                    added.add(key);
                }
            }else{
                if(tree.Delete(key)!=null){
                    removed.add(key);
                }
            }
        }
    }
    public static void main(String[] args) {
        int max=5;
        int threadNum=10;
        int opCount=200;
        double addPercentage=0.5;
        BinarySearchTree<Integer,Integer> nbbst=new BinarySearchTree();
        CorrectnessTest threads[]=new CorrectnessTest[threadNum];
        for(int i=0;i<threadNum;i++ ){
            threads[i]=new CorrectnessTest(nbbst, max,opCount,addPercentage);
        }
        for (CorrectnessTest thread : threads) {
            thread.start();
        }
        LinkedList<Integer> addedTot= new LinkedList<>();
        LinkedList<Integer> removedTot= new LinkedList<>();
        for (CorrectnessTest thread : threads) {
            try {
                thread.join();
            }catch (InterruptedException ex) {
                
            }
        }
        for (CorrectnessTest thread : threads) {
            addedTot.addAll(thread.getAdded());
            removedTot.addAll(thread.getRemoved());
        }
        System.out.println(addedTot);
        System.out.println(removedTot);
        for(Integer i : removedTot){
            addedTot.removeFirstOccurrence(i);
        }
        LinkedList<Integer> foundNodes=new  LinkedList<>();
        
        nbbst.getNodes(foundNodes);
        Collections.sort(addedTot);
        Collections.sort(foundNodes);
        System.out.println(addedTot);
        System.out.println(foundNodes);
        LinkedHashSet<Integer> set_uniques=new LinkedHashSet<>(foundNodes);
        
        if(addedTot.containsAll(foundNodes)&&foundNodes.containsAll(addedTot)&&(set_uniques.size()==foundNodes.size())){
            System.out.println("Everything seems fine");
        }else{
            System.err.println("error in the code");
        }
        for(Integer i : addedTot){
            foundNodes.removeFirstOccurrence(i);
        }
        assert(foundNodes.isEmpty());
    }
}