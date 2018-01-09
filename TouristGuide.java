/**
* Implementation of Tourist Guide problem
* @author Shida Jing, Chiara Zizza, David Neil Asanza
* Class: CSC 301-01
*/

import java.util.HashSet;
import java.util.Scanner;

public class TouristGuide {

  public static void main(String[] args) {
    int n, m;                                 /* Number of vertices (places), edges (roads) */
    int mapNum      = 0;                      /* Count of graphs generated */
    String[] places = new String[2];          /* Array to store user input */
    Scanner in      = new Scanner(System.in); /* Scanner to read user input */
    Graph map;                                /* Graph (map of locations) */
    HashSet<String> cameras;                  /* Set of articulation vertices in graph */

    /* Read in number of vertices to process */
    n = Integer.parseInt(in.nextLine());

    /* Continue generating graphs until 0 vertices should be added */
    while (0 != n) {
      mapNum++;
      map = new Graph();

      /* Populate 'map' with places */
      for (int i = 0; i < n; i++) {
        map.addVertex(in.nextLine());
      }

      /* Read in number of edges to process */
      m = Integer.parseInt(in.nextLine());

      /* Populate 'map' with roads */
      for (int i = 0; i < m; i++) {
        places = in.nextLine().split(" ", 2);
        map.addEdge(places[0], places[1]);
      }

      /* Find articulation vertices in graph */
      cameras = map.getArticulationVertices();

      /* Print out all articulation vertices */
      System.out.printf("\nCity map #%d: %d camera(s) found\n", mapNum, cameras.size());
      for (String place : cameras) {
        System.out.println(place);
      }

      /* Read in number of vertices to process */
      n = Integer.parseInt(in.nextLine());
    }

    in.close();
  } //main
} //TouristGuide Class
