package memoryManagement;

 

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;

import java.nio.file.Paths;

import java.util.Stack;

import java.util.stream.Stream;

 

/*************************************************************************

*  Compilation:  javac BTree.java

*  Execution:    java BTree

*

*  B-tree.

*

*  Limitations

*  -----------

*   -  Assumes M is even and M >= 4

*   -  should b be an array of children or list (it would help with

*      casting to make it a list)

*

*************************************************************************/

 

 

public class BTree<Key extends Comparable<Key>, Value>  {

    private static final int M = 4;    // max children per B-tree node = M-1

 

    private Node root;             // root of the B-tree

    private int HT;                // height of the B-tree

    private int N;                 // number of key-value pairs in the B-tree

 

    // helper B-tree node data type

    private static final class Node {

        private int m;                             // number of children

        private Entry[] children = new Entry[M];   // the array of children

        private Node(int k) { m = k; }             // create a node with k children

    }

 

    // internal nodes: only use key and next

    // external nodes: only use key and value

    private static class Entry {

        private Comparable key;

        private Object value;

        private Node next;     // helper field to iterate over array entries

        public Entry(Comparable key, Object value, Node next) {

            this.key   = key;

            this.value = value;

            this.next  = next;

        }

    }

 

    // constructor

    public BTree() { root = new Node(0); }

 

    // return number of key-value pairs in the B-tree

    public int size() { return N; }

 

    // return height of B-tree

    public int height() { return HT; }

 

 

    // search for given key, return associated value; return null if no such key

    public Value get(Key key) { return search(root, key, HT); }

    private Value search(Node x, Key key, int ht) {

        Entry[] children = x.children;

 

        // external node

        if (ht == 0) {

            for (int j = 0; j < x.m; j++) {

                if (eq(key, children[j].key)) return (Value) children[j].value;

            }

        }

 

        // internal node

        else {

            for (int j = 0; j < x.m; j++) {

                if (j+1 == x.m || less(key, children[j+1].key))

                    return search(children[j].next, key, ht-1);

            }

        }

        return null;

    }

 

 

    // insert key-value pair

    // add code to check for duplicate keys

    public void put(Key key, Value value) {

        Node u = insert(root, key, value, HT);

        N++;

        if (u == null) return;

 

        // need to split root

        Node t = new Node(2);

        t.children[0] = new Entry(root.children[0].key, null, root);

        t.children[1] = new Entry(u.children[0].key, null, u);

        root = t;

        HT++;

    }

 

 

    private Node insert(Node h, Key key, Value value, int ht) {

        int j;

        Entry t = new Entry(key, value, null);

 

        // external node

        if (ht == 0) {

            for (j = 0; j < h.m; j++) {

                if (less(key, h.children[j].key)) break;

            }

        }

 

        // internal node

        else {

            for (j = 0; j < h.m; j++) {

                if ((j+1 == h.m) || less(key, h.children[j+1].key)) {

                    Node u = insert(h.children[j++].next, key, value, ht-1);

                    if (u == null) return null;

                    t.key = u.children[0].key;

                    t.next = u;

                    break;

                }

            }

        }

 

        for (int i = h.m; i > j; i--) h.children[i] = h.children[i-1];

        h.children[j] = t;

        h.m++;

        if (h.m < M) return null;

        else         return split(h);

    }

 

    // split node in half
    private Node split(Node h) {
        Node t = new Node(M/2);
        h.m = M/2;
        for (int j = 0; j < M/2; j++)
            t.children[j] = h.children[M/2+j];
        return t;    
    }

 

    // for debugging

    public String toString() {

        return toString(root, HT, "") + "\n";

    }

    private String toString(Node h, int ht, String indent) {

        StringBuilder s = new StringBuilder();
        Entry[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.m; j++) {
                s.append(indent).append(children[j].key).append(" ").append(children[j].value).append("\n");
            }
        }
        else {
            for (int j = 0; j < h.m; j++) {
                if (j > 0) s.append(indent).append("(").append(children[j].key).append(")\n");
                s.append(toString(children[j].next, ht - 1, indent + "     "));
            }

        }

        return s.toString();

    }

 

    // in-order traversal of the BTree

    public String inOrderTraversal() {
        StringBuilder sb = new StringBuilder();
        Node current = root;
        Stack<NodeTraversal> stack = new Stack<>();

 

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(new NodeTraversal(current, 0));
                current = current.children[0].next;
            }

            NodeTraversal nodeTraversal = stack.pop();
            int index = nodeTraversal.index;
            Node node = nodeTraversal.node;

            sb.append(node.children[index].key).append("\n");
            if (index + 1 < node.m) {
                current = node.children[index + 1].next;
                stack.push(new NodeTraversal(node, index + 1));
            }
        }
        return sb.toString();
    }

    private static class NodeTraversal {
        private Node node;
        private int index;

        private NodeTraversal(Node node, int index) {
            this.node = node;
            this.index = index;
        }
    }

 

    // comparison functions - make Comparable instead of Key to avoid casts

    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    } 

    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

   /*************************************************************************

    *  test client

    *************************************************************************/

    public static void main(String[] args) throws IOException {

        BTree<String, String> st = new BTree<>();

        try (Stream<String> lines = Files.lines(Paths.get("F:\\UwindSor\\First Semester\\ACC\\Labs\\Ch 7-Memory Management\\Ch 7-Memory Management\\Memory Management\\merged.txt"))) {
            lines.forEach(word -> st.put(word.trim(), String.valueOf(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        long start = System.nanoTime();
        String inOrder = st.inOrderTraversal();
        System.out.println(inOrder);

    }
}

 