import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


class Node{
    //indexes are 1-based, -1 means null
    Integer index;
    public Node left;
    Integer index_left;
    public Node right;
    Integer index_right;
    public Node parent;
    Integer index_parent;
    // depth is 1 based
    Integer depth;
    public Node(){
        index=-1;
        index_left=-1;
        index_right=-1;
        index_parent=-1;
        left = null;
        right = null;
        parent = null;        
        depth = 1;
    }
    public Node(Integer index, Node left, Node right, Node parent){
        this.index =index;
        this.left = left;
        if(this.left==null)
            this.index_left =-1;
        else
            this.index_left = this.left.index;
        this.right = right;
        if(this.right==null)
            this.index_right =-1;
        else
            this.index_right = this.right.index;
                    
        this.parent = parent; 
        if(this.parent==null)
            this.index_parent =-1;
        else{
            this.index_parent = this.parent.index;               
            this.depth =  this.parent.depth+1;
        }
    }
    public void Swap(){
        Node tmp=left;
        left=right;
        right= tmp;
    }
    
}

class Tree{
    Node root;
    Integer maxDepth;
    public Tree(List<List<Integer>> indexes){
        Integer index=1;
        HashMap<Integer,Node> tmpMap = new HashMap<>();
        for (List<Integer> node_info : indexes) {
            Node newNode = new Node();
            newNode.index = index;           
            newNode.index_left = node_info.get(0);
            newNode.index_right = node_info.get(1);
            tmpMap.put(index, newNode);
            index++;
        }
        for (Map.Entry<Integer, Node> entry : tmpMap.entrySet()) {
            Node node = entry.getValue();
            if(node.index==1){
                root = node;
                node.depth = 1;
            }
            if(node.index_left!=-1){
                node.left = tmpMap.get(node.index_left);
                node.index_left = -1;//should not use this anymore
                node.left.parent = node;
                node.left.index_parent = node.index;
                node.left.depth = node.depth+1;
            }else{
                 node.left = null;
            }
            if(node.index_right!=-1){
                node.right = tmpMap.get(node.index_right);
                node.index_right = -1;//should not use this anymore
                node.right.parent = node;
                node.right.index_parent = node.index;
                node.right.depth = node.depth+1;
            }else{
                node.right = null;
            }            
        }
        //_printTree(root,"",false);

    }
    public void _printTree(Node n,String prefix, boolean isLeft) {
        if (n != null) {
            System.out.println (prefix + (isLeft ? "|-- " : "\\-- ") + n.index+ ":"+ n.depth);
            _printTree(n.left,prefix + (isLeft ? "|   " : "    "),  true);
            _printTree(n.right,prefix + (isLeft ? "|   " : "    "),  false);
        }
    }

    private  void _PreOrderSwapAll(Node curNode, Integer depth){
        if(curNode.depth%depth==0){
            System.out.printf("Swapping children for node %d at depth %d\n", curNode.index, curNode.depth);
            curNode.Swap();
        }
        if(curNode.left!=null){
            _PreOrderSwapAll(curNode.left,depth);
        }
        
        if(curNode.right!=null){
            _PreOrderSwapAll(curNode.right,depth);
        }        
    }    

    public void SwapAll(Integer depth){
        _PreOrderSwapAll(root,depth);
        //_printTree(root,"",false);        
    }
    private List<Integer> _InOrderNormalize(Node curNode){
        List<Integer> retList = new ArrayList<>();
        if(curNode.left!= null)
        {
            List<Integer> leftList = _InOrderNormalize(curNode.left);
            for (Integer node_idx : leftList) {
                retList.add(node_idx);
            }
        }
        retList.add(curNode.index);
        if(curNode.right!= null)
        {
            List<Integer> rightList = _InOrderNormalize(curNode.right);
            for (Integer node_idx : rightList) {
                retList.add(node_idx);
            }
        }
        return retList;
    }    
    public List<Integer> InOrderNormalize(){       
        List<Integer> retList = new ArrayList<>(); 
        if(root == null){
            retList.add(-1);
            return retList;
        }
        return _InOrderNormalize(root);
    }
}

class Result {

    /*
     * Complete the 'swapNodes' function below.
     *
     * The function is expected to return a 2D_INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. 2D_INTEGER_ARRAY indexes
     *  2. INTEGER_ARRAY queries
     */

    public static List<List<Integer>> swapNodes(List<List<Integer>> indexes, List<Integer> queries) {
    // Write your code here
        List<List<Integer>> retList = new ArrayList<>();
        Tree tree= new Tree(indexes);
        for (Integer depth : queries) {
            tree.SwapAll(depth);
            retList.add(tree.InOrderNormalize());
        }
        return retList;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        List<List<Integer>> indexes = new ArrayList<>();

        IntStream.range(0, n).forEach(i -> {
            try {
                indexes.add(
                    Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                        .map(Integer::parseInt)
                        .collect(toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        int queriesCount = Integer.parseInt(bufferedReader.readLine().trim());

        List<Integer> queries = IntStream.range(0, queriesCount).mapToObj(i -> {
            try {
                return bufferedReader.readLine().replaceAll("\\s+$", "");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        })
            .map(String::trim)
            .map(Integer::parseInt)
            .collect(toList());

        List<List<Integer>> result = Result.swapNodes(indexes, queries);

        result.stream()
            .map(
                r -> r.stream()
                    .map(Object::toString)
                    .collect(joining(" "))
            )
            .map(r -> r + "\n")
            .collect(toList())
            .forEach(e -> {
                try {
                    bufferedWriter.write(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

        bufferedReader.close();
        bufferedWriter.close();
    }
}
