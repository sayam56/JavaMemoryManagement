package memoryManagement;

 

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.util.*;

 

/*************************************************************************
*  Compilation:  javac Multiway.java
*  Execution:    java Multiway input1.txt input2.txt input3.txt ...
*  Dependencies: IndexMinPQ.java In.java StdOut.java
* 
*  Merges together the sorted input stream given as command-line arguments
*  into a single sorted output stream on standard output.
*
*  % more m1.txt 
*  A B C F G I I Z
*
*  % more m2.txt 
*  B D H P Q Q
* 
*  % more m3.txt 
*  A B E F J N
*
*  % java Multiway m1.txt m2.txt m3.txt 
*  A A B B B C D E F F G H I I J N P Q Q Z 
*
*************************************************************************/

 

/**
*  The <tt>Multiway</tt> class provides a client for reading in several
*  sorted text files and merging them together into a single sorted
*  text stream.
*  This implementation uses a {@link IndexMinPQ} to perform the multiway
*  merge. 
*  <p>
*  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a>
*  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
*
*  @author Robert Sedgewick
*  @author Kevin Wayne
*/

 

public class Multiway {

 

    // This class should not be instantiated.
    private Multiway() { }

 

    // merge together the sorted input streams and write the sorted result to standard output
    private static List<String> merge(In[] streams) { 
        int N = streams.length; 
        String a[]= {};
        List<String> arrlist = new ArrayList<String>(Arrays.asList(a));
        IndexMinPQ<String> pq = new IndexMinPQ<String>(N); 
        for (int i = 0; i < N; i++) 
            if (!streams[i].isEmpty()) 
               pq.insert(i, streams[i].readString()); 

        // Extract and print min and read next from its stream. 
        while (!pq.isEmpty()) {
//            StdOut.print(pq.minKey() + " ");
            //a[i] = pq.minKey();
            arrlist.add(pq.minKey());
            int i = pq.delMin(); 
            if (!streams[i].isEmpty()) 
                pq.insert(i, streams[i].readString()); 
        }
//        StdOut.println();
        return arrlist;
    }

 

    public static void CreateFile (String filename, List<String> arrlist) {

        try {
          File myObj = new File(filename);
          if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
            try {
                BufferedWriter myWriter = new BufferedWriter(new FileWriter(filename));
                for (String element : arrlist) {
                   myWriter.write(element);
                   myWriter.newLine();
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
              } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
              }
          } else {
            System.out.println("File already exists.");
          }
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }

    }

 

    /**
     *  Reads sorted text files specified as command-line arguments;
     *  merges them together into a sorted output; and writes
     *  the results to standard output.
     *  Note: this client does not check that the input files are sorted.
     */
    public static void main(String[] args) { 
        String[] fnames = {"m1.txt","m2.txt","m3.txt","m4.txt"};
        int N = fnames.length; 
        In[] streams = new In[N]; 
        for (int i = 0; i < N; i++) 
            streams[i] = new In(fnames[i]); 
        List<String> k = merge(streams);

        CreateFile("merged.txt",k);
    } 
} 