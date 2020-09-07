(ns performancepaper.core
  (:require [criterium.core :as c])
  (:import bench.core
           [java.util Collections Arrays ArrayList]))

(defmacro with-unchecked [& body]
  `(do (set! *unchecked-math* :warn-on-boxed)
       ~@body
       (set! *unchecked-math* false)
       nil))

;; The idea behind this approach was that seeing the way the languages perform in
;; common fundamental tasks wouldgive the reader an idea of how the languages will
;; perform in their application.The reason that the fundamental areas selected were
;; separated into their ownexperiments rather than putting them all into the same
;; program, was so thatthe reader could more easily predict which language is
;; better for their specific tasks.

;;The Java version that was used to execute both the Clojure and the Java
;;codewas 1.8.0_60. The JVM was run with the arguments –Xmx11g and -Xss11gto
;;increase the max heap and stack space to 11 gigabytes when needed forthe
;;experiments.

;; 3.1

;; The recursion experiment consisted of a number of recursion calls with only
;; acounter as a parameter and a simple exit condition. It was designed to test
;; theperformance of function calls in the two languages. The counter was a
;; prim-itive integer in both languages and was decreased by one for each
;; recursivecall.

;;Executiontimes were measured for problem sizes of 2000, 20000, 200000, 2000000
;;and 20000000,

;;3.1 Pure Recursion

;; private void Recurse(int cnt)
;; {if (cnt > 0)
;;  Recurse (cnt - 1);}

;; performancepaper.core> (c/quick-bench (bench.core/Recurse 10))
;; Evaluation count : 49359366 in 6 samples of 8226561 calls.
;; Execution time mean : 10.123866 ns
;; Execution time std-deviation : 0.199596 ns
;; Execution time lower quantile : 9.852564 ns ( 2.5%)
;; Execution time upper quantile : 10.309200 ns (97.5%)
;; Overhead used : 1.797578 ns

(defn pure-recursion [cnt]
  (if  (>  cnt  0)
    (pure-recursion 
     (- cnt  1))))

;; performancepaper.core> (c/quick-bench (pure-recursion 10))
;; Evaluation count : 5648448 in 6 samples of 941408 calls.
;; Execution time mean : 108.779894 ns
;; Execution time std-deviation : 3.259179 ns
;; Execution time lower quantile : 105.783939 ns ( 2.5%)
;; Execution time upper quantile : 113.458706 ns (97.5%)
;; Overhead used : 1.797578 ns

(with-unchecked
  (defn pure-recursion2 [^long cnt]
    (if  (pos?   cnt)
      (pure-recursion  (dec  cnt))))
  )

(defn pure-recursion3 [cnt]
  (if (> cnt 0)
    (recur (dec cnt))))

;; Evaluation count : 9460254 in 6 samples of 1576709 calls.
;; Execution time mean : 62.493096 ns
;; Execution time std-deviation : 0.772831 ns
;; Execution time lower quantile : 61.875935 ns ( 2.5%)
;; Execution time upper quantile : 63.504731 ns (97.5%)
;; Overhead used : 1.797578 ns
;; nil

;;faster than java.

(defn pure-recursion4 [^long cnt]
  (if (> cnt 0)
    (recur (dec cnt))))

;; performancepaper.core> (c/quick-bench (pure-recursion4 10))
;; Evaluation count : 68567172 in 6 samples of 11427862 calls.
;; Execution time mean : 6.972233 ns
;; Execution time std-deviation : 0.059662 ns
;; Execution time lower quantile : 6.887818 ns ( 2.5%)
;; Execution time upper quantile : 7.030860 ns (97.5%)
;; Overhead used : 1.797578 ns
;; nil

(with-unchecked
  (defn pure-recursion5 [^long cnt]
    (if (> cnt 0)
      (recur (dec cnt))))
  )


;; Evaluation count : 73179480 in 6 samples of 12196580 calls.
;; Execution time mean : 6.533542 ns
;; Execution time std-deviation : 0.173063 ns
;; Execution time lower quantile : 6.235797 ns ( 2.5%)
;; Execution time upper quantile : 6.687093 ns (97.5%)
;; Overhead used : 1.797578 ns

;; Found 1 outliers in 6 samples (16.6667 %)
;; low-severe	 1 (16.6667 %)
;; Variance from outliers : 13.8889 % Variance is moderately inflated by outliers

;;3.2

;; The sorting experiment consisted of sorting a collection of integers. In Clojure
;; this was done by sorting alistof integers, shuffled by theshufflefunc-tion,
;; using thesortfunction, all of which are included in theclojure.corelibrary. In
;; Java this was done similarly by sorting an array of primitive in-tegers, which
;; was shuffled usingjava.util.Collections.shuffle, using theAr-rays.sortfunction.

 ;;Execution times were measured for collec-tions with 2000, 20000, 200000,
 ;;2000000 and 20000000 integers.

;; private  int[]  createArray (int  size)
;; {int  counter  =  Integer.MIN_VALUE;
;;  ArrayList <Integer>  arrList= new  ArrayList <Integer>(size) ;
;;  for(int i = 0; i < size ; ++ i)
;;          arrList.add (counter ++);
;;  java.util.Collections.shuffle(arrList);
;;  int[] retArr = new int[size] ;
;;  for(int i  = 0; i < size ; ++ i )
;;          retArr [i] = arrList.get(i);
;;  return retArr;}

;;  Arrays.sort(array) ;

;; performancepaper.core> (c/quick-bench (core/createArray 100))
;; Evaluation count : 138942 in 6 samples of 23157 calls.
;; Execution time mean : 4.369374 µs
;; Execution time std-deviation : 63.001723 ns
;; Execution time lower quantile : 4.310739 µs ( 2.5%)
;; Execution time upper quantile : 4.467841 µs (97.5%)
;; Overhead used : 1.797578 ns

;;Clojure implemention underspecified

;; (let [list  (−>  (create−list  size (atom  Integer/MIN_VALUE))
;;                   (shuffle))]
;;   ...) ;;author elides this, and `create-list` is not provided.

;; (sort  list)

;;9x
(defn create-sorted-array [n]
  (->>   (range Integer/MIN_VALUE 0 1)
         (take n)
         (java.util.collections/shuffle)
         sort))

;; Evaluation count : 17322 in 6 samples of 2887 calls.
;; Execution time mean : 36.425740 µs
;; Execution time std-deviation : 1.031385 µs
;; Execution time lower quantile : 35.312439 µs ( 2.5%)
;; Execution time upper quantile : 37.844605 µs (97.5%)
;; Overhead used : 1.797578 ns

;;3x
(defn create-sorted-array2 [^long n]
  (let [^ArrayList alist
          (->> (range Integer/MIN_VALUE 0 1)
               (transduce (take n)
                          (completing (fn [^ArrayList acc  n]
                                        (doto acc (.add n))))
                          (java.util.ArrayList. n)))
        _   (java.util.Collections/shuffle alist)]
    (doto (int-array alist) Arrays/sort)))

;; Evaluation count : 46506 in 6 samples of 7751 calls.
;; Execution time mean : 12.985146 µs
;; Execution time std-deviation : 570.944434 ns
;; Execution time lower quantile : 12.451225 µs ( 2.5%)
;; Execution time upper quantile : 13.917159 µs (97.5%)
;; Overhead used : 1.800162 ns

;; Found 1 outliers in 6 samples (16.6667 %)
;; low-severe	 1 (16.6667 %)
;; Variance from outliers : 13.8889 % Variance is moderately inflated by outliers
;; nil

(with-unchecked
  (defn create-sorted-array3 [^long size]
    (let [^ArrayList alist
          (loop [^ArrayList acc (java.util.ArrayList. size)
                 counter  (int Integer/MIN_VALUE)
                 n        0]
            (if (< n size)
              (let [c (inc counter)]
                (recur (doto acc (.add c))
                       c
                       (inc n)))
              acc))
          _   (Collections/shuffle alist)
          res (int-array size)]
      (dotimes [i size] (aset res i ^int (.get alist i)))
      (doto res Arrays/sort))))

;; performancepaper.core> (c/quick-bench (create-sorted-array3 100))
;; Evaluation count : 130794 in 6 samples of 21799 calls.
;; Execution time mean : 4.669894 µs
;; Execution time std-deviation : 179.454425 ns
;; Execution time lower quantile : 4.477268 µs ( 2.5%)
;; Execution time upper quantile : 4.902860 µs (97.5%)
;; Overhead used : 1.800162 ns

;;3.3 Map Creation

;; The map creation experiment consisted of adding integers as keys and valuesto a
;; map. In Java they were added to aHashMapfrom thejava.util library, andin Clojure
;; they were added to the built-inpersistent mapdata structure.

;;Execution times were measured for20000, 63246, 200000, 632456 and 2000000
;;different key-value pairs.

;; private  HashMap<Integer ,  Integer> createMap (int  sze)
;; {HashMap<Integer ,  Integer> retMap= new HashMap<Integer , Integer>(sze) ;
;;  for (int i = 0; i < sze ;)
;;     retMap.put(i , ++ i ) ;
;;  return  retMap ;}

;; Evaluation count : 538998 in 6 samples of 89833 calls.
;; Execution time mean : 1.178573 µs
;; Execution time std-deviation : 40.404054 ns
;; Execution time lower quantile : 1.142367 µs ( 2.5%)
;; Execution time upper quantile : 1.237344 µs (97.5%)
;; Overhead used : 1.800162 ns

;;9x
(defn create-map [size]
  (loop [map  (transient  {}),
         i    (int size)]
    (if  (>  i  0)
      (recur  (assoc! map i  (+ i 1))  (- i  1) )
      (persistent!  map))))

;; Evaluation count : 61686 in 6 samples of 10281 calls.
;; Execution time mean : 9.874480 µs
;; Execution time std-deviation : 96.973621 ns
;; Execution time lower quantile : 9.750675 µs ( 2.5%)
;; Execution time upper quantile : 9.964194 µs (97.5%)
;; Overhead used : 1.800162 ns

(with-unchecked
  (defn create-map2 [size]
    (loop [^clojure.lang.ITransientAssociative
           map  (transient  {}),
           i    (int size)]
      (if  (>  i  0)
        (recur  (.assoc map i  (+ i 1))
                (- i  1))
        (persistent!  map)))))

(with-unchecked
  (defn create-map3 [^ long size]
    (let [^java.util.HashMap map  (java.util.HashMap. size)]
      (dotimes [i size]
        (.put map i  (+ i 1))))))

;; Evaluation count : 450894 in 6 samples of 75149 calls.
;; Execution time mean : 1.338286 µs
;; Execution time std-deviation : 27.816513 ns
;; Execution time lower quantile : 1.304915 µs ( 2.5%)
;; Execution time upper quantile : 1.372651 µs (97.5%)
;; Overhead used : 1.800162 ns


;;3.4 Object Creation

;; The object creation experiment consisted of creating a linked list without
;; val-ues. In Java a custom class was used to create the links while in Clojure
;; nestedpresistent maps were used. The links were created backwards in both
;; lan-guages, meaning that the first object created would have a next-pointer with
;; anull value, and the second object created would point to the first, and so on.

;; Execution times were measured for 100000, 316228, 1000000, 3162278and 10000000
;; linked objects

;; private  class  LLNode{
;;  public  LLNode  next ;
;;  public  LLNode (LLNode  next ){
;;  this.next  =  next ;}

;; ;;
;; private LLNode create Objects (int count )
;; {LLNode last = null ;
;;  for (int i = 0; i < count; ++ i)
;;           last = new LLNode(last) ;
;;           return last;}

;; performancepaper.core> (c/quick-bench (bench.core/createObjects 100))
;; Evaluation count : 2368566 in 6 samples of 394761 calls.
;; Execution time mean : 249.927510 ns
;; Execution time std-deviation : 4.557640 ns
;; Execution time lower quantile : 244.464795 ns ( 2.5%)
;; Execution time upper quantile : 254.444188 ns (97.5%)
;; Overhead used : 1.800162 ns

(defn create-objects[count]
  (loop [last nil
         i (int  count)]
    (if  (=  0  i )
      last
      (recur  {:next  last} (- i  1)))))

;; Evaluation count : 916590 in 6 samples of 152765 calls.
;; Execution time mean : 673.619823 ns
;; Execution time std-deviation : 26.588156 ns
;; Execution time lower quantile : 647.556044 ns ( 2.5%)
;; Execution time upper quantile : 701.464334 ns (97.5%)
;; Overhead used : 1.800162 ns

(with-unchecked
  (defn create-objects2 [count]
    (loop [last nil
           i (int  count)]
      (if  (==  i 0)
        last
        (recur  {:next  last} (- i  1))))))

;; Evaluation count : 933462 in 6 samples of 155577 calls.
;; Execution time mean : 646.923626 ns
;; Execution time std-deviation : 11.946099 ns
;; Execution time lower quantile : 634.453274 ns ( 2.5%)
;; Execution time upper quantile : 664.344180 ns (97.5%)
;; Overhead used : 1.800162 ns

;;records are faster to construct, but implement a bunch of
;;stuff and carry more state, so more setup.  Still very
;;much faster to create when you have fixed fields, like
;;the node class.
(defrecord ll-node [next])
(defn create-objects3 [count]
  (loop [last nil
         i (int  count)]
    (if  (==  i 0)
      last
      (recur  (ll-node.  last) (- i  1)))))

;; Evaluation count : 1699422 in 6 samples of 283237 calls.
;; Execution time mean : 348.583970 ns
;; Execution time std-deviation : 6.587955 ns
;; Execution time lower quantile : 337.022098 ns ( 2.5%)
;; Execution time upper quantile : 354.655388 ns (97.5%)
;; Overhead used : 1.800162 ns

;; Found 1 outliers in 6 samples (16.6667 %)
;; low-severe	 1 (16.6667 %)
;; Variance from outliers : 13.8889 % Variance is moderately inflated by outliers

;;revisit
(with-unchecked
  (defn create-objects4 [^long count]
    (loop [last nil
           i    count]
      (if  (zero? i) #_(==  i 0)
        last
        (recur  (ll-node.  last) (dec i))))))


;;types have less to setup, very barebones like the node class.
(deftype ll-node-type [next])

(with-unchecked
  (defn create-objects5 [^long count]
    (loop [last nil
           i    count]
      (if  (==  i 0)
           last
           (recur  (ll-node-type.  last) (dec i))))))
;; Evaluation count : 2440158 in 6 samples of 406693 calls.
;; Execution time mean : 249.399392 ns
;; Execution time std-deviation : 5.009429 ns
;; Execution time lower quantile : 244.748218 ns ( 2.5%)
;; Execution time upper quantile : 256.732288 ns (97.5%)
;; Overhead used : 1.800162 ns

;;3.5 Binary Tree DFS

;; The binary tree DFS experiment consisted of searching a binary tree for a
;; valueit did not contain using depth first search. The depth first search was
;; implemented recursively in both languages. In Java the binary tree was
;; representedby a custom class while in Clojure they were represented using nested
;; persistent maps.


;; public BinaryTreeNode createBinaryTree (int depth, int[] counter)
;; {if (depth == 0) return null;
;;  int value = counter[0]++;
;;  BinaryTreeNode btn = new BinaryTreeNode(value);
;;  btn.left = createBinaryTree(depth - 1, counter) ;
;;  btn.right = createBinaryTree(depth - 1 , counter) ;
;;  return  btn ;}

;;  public boolean binaryTreeDFS(BinaryTreeNode root, int target)
;;  {if (root == null) return false ;
;;   return root.value == target ||
;;     binaryTreeDFS(root.left, target) ||
;;     binaryTreeDFS (root.right, target);}

;;Added by joinr
;; public boolean binaryTreeDFSTest(int depth, int target)
;; {
;;  int[] counter = new int[0];
;;  counter[0] = 0;
;;  return binaryTreeBFS(createBinaryTree(depth,counter),target);
;;  }

(c/quick-becnh (bench.core/binaryTreeDFSTest 7 128))

(defn create−binary−tree [depth counter−atom]
  (when (> depth  0)
    (let  [val  @counter−atom]
      (swap! counter−atom  inc )
      {:value val
       :left  (create−binary−tree  (- depth  1) counter−atom )
       :right (create−binary−tree  (- depth  1) counter−atom )})))

(defn binary−tree−DFS [root  target]
  (if  (nil?  root)
    false
    (or (=  (:value  root)  target)
        (binary−tree−DFS  (:left  root)  target)
        (binary−tree−DFS  (:right root)  target))))

(defrecord binary-node [^int value left right])

;;3.6 Binary Tree BFS

;;The binary tree BFS, similar to the binary tree DFS experiment consisted
;;ofsearching a binary tree for a value it did not contain, but using breadth
;;first search. The breadth first search was implemented iteratively in both
;;languages.In Java the binary tree was represented by a custom class while in
;;Clojure theywere represented using nested persistent maps.

;; public boolean binaryTreeBFS(BinaryTreeNode root, int target)
;;   {Queue<BinaryTreeNode >queue= new LinkedList <BinaryTreeNode>() ;
;;    queue.add(root) ;
;;    while (! queue.isEmpty())
;;    {BinaryTreeNode item = queue.poll();
;;     if (item.value == target) return  true;
;;     if (item.left != null) queue.add (item.left);
;;     if (item.right != null) queue.add (item.right);}
;;     return false;}

;;Added by joinr
;; public boolean binaryTreeBFSTest(int depth, int target)
;; {
;;  int[] counter = new int[0];
;;  counter[0] = 0;
;;  return binaryTreeBFS(createBinaryTree(depth,counter),target);
;;  }

(defn binary−tree−BFS [root target]
  (loop [queue (conj clojure.lang.PersistentQueue/EMPTY root)]
    (if (empty? queue)
      false
      (let [item (peek queue)]
        (if (= target (:value item))
          true
          (recur (as−> (pop queue) $
                       (if (nil?  (:left item))
                         $
                         (conj $ (:left item)))
                       (if (nil? (:right item))
                         $
                         (conj $ (:right item))))))))))
