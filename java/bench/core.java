package bench;

import java.util.HashMap;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import bench.LLNode;
import bench.BinaryTreeNode;

public class core {
// The idea behind this approach was thatseeing the way the languages perform in
// common fundamental tasks wouldgive the reader an idea of how the languages will
// perform in their application.The reason that the fundamental areas selected were
// separated into their ownexperiments rather than putting them all into the same
// program, was so thatthe reader could more easily predict which language is
// better for their specific tasks.

//The Java version that was used to execute both the Clojure and the Java
//codewas 1.8.0_60. The JVM was run with the arguments –Xmx11g and -Xss11gto
//increase the max heap and stack space to 11 gigabytes when needed forthe
//experiments.

// 3.1

// The recursion experiment consisted of a number of recursion calls with only
// acounter as a parameter and a simple exit condition. It was designed to test
// theperformance of function calls in the two languages. The counter was a
// prim-itive integer in both languages and was decreased by one for each
// recursivecall.

//Executiontimes were measured for problem sizes of 2000, 20000, 200000, 2000000
//and 20000000,

//3.1 Pure Recursion

public static void Recurse(int cnt)
{if (cnt > 0)
 Recurse (cnt - 1);}


// (defn  pure−recursion [cnt]
//   (if  (>  cnt  0)
//     (pure−recursion
//      (− cnt  1))))


//3.2

// The sorting experiment consisted of sorting a collection of integers. In Clojure
// this was done by sorting alistof integers, shuffled by theshufflefunc-tion,
// using thesortfunction, all of which are included in theclojure.corelibrary. In
// Java this was done similarly by sorting an array of primitive in-tegers, which
// was shuffled usingjava.util.Collections.shuffle, using theAr-rays.sortfunction.

 //Execution times were measured for collec-tions with 2000, 20000, 200000,
 //2000000 and 20000000 integers.

public static  int[]  createArray (int  size)
{int  counter  =  Integer.MIN_VALUE;
 ArrayList <Integer>  arrList= new  ArrayList <Integer>(size) ;
 for(int i = 0; i < size ; ++ i)
         arrList.add (counter ++);
         java.util.Collections.shuffle(arrList);
 int[] retArr = new int[size] ;
 for(int i  = 0; i < size ; ++ i )
         retArr [i] = arrList.get(i);
 Arrays.sort(retArr);
 return retArr;}

// (let [list  (−>  (create−list  size (atom  Integer/MIN_VALUE))
//                   (shuffle))]
//   ...) //author elides this, and `create-list` is not provided.

// (sort  list)

//3.3 Map Creation

// The map creation experiment consisted of adding integers as keys and valuesto a
// map. In Java they were added to aHashMapfrom thejava.util library, andin Clojure
// they were added to the built-inpersistent mapdata structure.

//Execution times were measured for20000, 63246, 200000, 632456 and 2000000
//different key-value pairs.

public static  HashMap<Integer ,  Integer> createMap (int  sze)
{HashMap<Integer ,  Integer> retMap= new HashMap<Integer , Integer>(sze) ;
 for (int i = 0; i < sze ;)
    retMap.put(i , ++ i ) ;
 return  retMap ;}

// (defn create−map[size]
//   (loop [map  (transient  {}),
//          i    (int size)]
//     (if  (>  i  0)
//       (recur  (assoc! map i  (+ i 1))  (− i  1) )
//       (persistent!  map))))

//3.4 Object Creation

// The object creation experiment consisted of creating a linked list without
// val-ues. In Java a custom class was used to create the links while in Clojure
// nestedpresistent maps were used. The links were created backwards in both
// lan-guages, meaning that the first object created would have a next-pointer with
// anull value, and the second object created would point to the first, and so on.

// Execution times were measured for 100000, 316228, 1000000, 3162278and 10000000
// linked objects

//
public static LLNode createObjects (int count )
{LLNode last = null ;
 for (int i = 0; i < count; ++ i)
          last = new LLNode(last) ;
 return last;}

// (defn create−objects [count]
//   (loop [last nil
//          i (int  count)]
//     (if  (=  0  i )
//       last
//       (recur  {:next  last} (− i  1)))))

//3.5 Binary Tree DFS

// The binary tree DFS experiment consisted of searching a binary tree for a
// valueit did not contain using depth first search. The depth first search was
// implemented recursively in both languages. In Java the binary tree was
// representedby a custom class while in Clojure they were represented using nested
// persistent maps.

public static BinaryTreeNode createBinaryTree (int depth, int[] counter)
{if (depth == 0) return null;
 int value = counter[0]++;
 BinaryTreeNode btn = new BinaryTreeNode(value);
 btn.left = createBinaryTree(depth - 1, counter);
 btn.right = createBinaryTree(depth - 1 , counter);
 return  btn ;}

 public static boolean binaryTreeDFS(BinaryTreeNode root, int target)
 {if (root == null) return false ;
  return root.value == target ||
    binaryTreeDFS(root.left, target) ||
    binaryTreeDFS (root.right, target);}

public static boolean binaryTreeDFSTest(int depth, int target)
 {
     int[] counter = new int[1];
     counter[0] = 0;
     return binaryTreeDFS(createBinaryTree(depth,counter),target);
  }

    // (defn create−binary−tree [depth counter−atom]
    //   (when (> depth  0)
    //     (let  [val  @counter−atom]
    //       (swap! counter−atom  inc )
    //       {:value val
    //        :left  (create−binary−tree  (− depth  1) counter−atom )
    //        :right (create−binary−tree  (− depth  1) counter−atom )})))

    // (defn binary−tree−DFS [root  target]
    //   (if  (nil?  root)
    //     false
    //     (or (=  (:value  root)  target)
    //         (binary−tree−DFS  (:left  root)  target)
    //         (binary−tree−DFS  (:right root)  target))))

//3.6 Binary Tree BFS

//The binary tree BFS, similar to the binary tree DFS experiment consisted
//ofsearching a binary tree for a value it did not contain, but using breadth
//first search. The breadth first search was implemented iteratively in both
//languages.In Java the binary tree was represented by a custom class while in
//Clojure theywere represented using nested persistent maps.

public static boolean binaryTreeBFS(BinaryTreeNode root, int target)
  {Queue<BinaryTreeNode >queue= new LinkedList <BinaryTreeNode>() ;
   queue.add(root) ;
   while (! queue.isEmpty())
   {BinaryTreeNode item = queue.poll();
    if (item.value == target) return  true;
    if (item.left != null) queue.add (item.left);
    if (item.right != null) queue.add (item.right);}
    return false;}

public static boolean binaryTreeBFSTest(int depth, int target)
{
    int[] counter = new int[1];
    counter[0] = 0;
    return binaryTreeBFS(createBinaryTree(depth,counter),target);
    }

// (defn binary−tree−BFS [root target]
//   (loop [queue (conj clojure.lang.PersistentQueue/EMPTY root)]
//     (if (empty? queue)
//       false
//       (let [item (peek queue)]
//         (if (= target (:value item))
//           true
//           (recur (as−> (pop queue) $
//                        (if (nil?  (:left item))
//                          $
//                          (conj $ (:left item)))
//                        (if (nil? (:right item))
//                          $
//                          (conj $ (:right item ))))))))))
}
