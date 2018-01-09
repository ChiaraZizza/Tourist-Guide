/**
* Builds graphs and finds articulation vertices within these graphs
* @author Shida Jing, Chiara Zizza, David Neil Asanza
* Class: CSC 301-01
*/

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Graph {

  /* Numeric values of types of edges in graph */
  private enum EdgeClass  {
    TREE,
    BACK,
    UNCLASSIFIED
  }

  /* Private class Vertex */
  private class Vertex {
    public String name;                 /* Name of vertex (city) */
    public boolean processed;           /* Is vertex processed? */
    public boolean discovered;          /* Is vertex discovered? */
    public LinkedList<Vertex> adjacent; /* List of adjacent vertices */
    public Vertex reachableAncestor;    /* Reachable ancestor vertex */
    public Vertex parent;               /* Parent vertex */
    public int treeOutDegree;           /* Number of TREE edges incident to vertex */
    public int entryTime;               /* Time vertex is considered */

    /**
    * Constructor of Vertex
    * @param name a String
    */
    public Vertex(String name)  {
      this.name = name;
      this.processed = false;
      this.discovered = false;
      this.adjacent = new LinkedList<Vertex>();
      this.reachableAncestor = null;
      this.parent = null;
      this.treeOutDegree = 0;
      this.entryTime = 0;
    } //Constructor

    /**
    * Prints the name of the vertex
    *
    * Pre-condition: Vertex is not null
    */
    public String toString()  {
      return this.name;
    } //toString

    /**
    * Inserts a new vertex into adjacency list of vertex
    * @param vtx a Vertex
    */
    public void addLocation(Vertex vtx)  {
      this.adjacent.add(vtx);
    } //addLocation
  } //Vertex Class

  private HashMap<String, Vertex> vertices;     /* HashMap of vertices in graph */
  private HashSet<String> articulationVertices; /* HashSet of articulation vertices in graph */
  private int time;                             /* Global time */

  /**
  * Constructor of Graph
  */
  public Graph()  {
    this.vertices = new HashMap<String, Vertex>();
    this.articulationVertices = new HashSet<String>();
    this.time = 0;
  } //Constructor

  /**
  * Adds a vertex to the graph
  * @param name a String
  */
  public void addVertex(String name)   {
    Vertex vtx = new Vertex(name);
    this.vertices.put(name, vtx);
  } //addVertex

  /**
  * Adds an edge (composed of two vertices) to the graph
  * @param p1 a Vertex
  * @param p2 a Vertex
  */
  public void addEdge(String p1, String p2)  {
    Vertex v1 = this.vertices.get(p1);
    Vertex v2 = this.vertices.get(p2);
    v1.addLocation(v2);
    v2.addLocation(v1);
  } //addEdge

  /**
  * Finds all articulation vertices in the graph
  * @return a set containing all articulation vertices in the graph
  */
  public HashSet<String> getArticulationVertices() {
    /* Get any vertex from the graph */
    Vertex start = vertices.values().iterator().next();
    this.DFS(start);

    return this.articulationVertices;
  } //getArticulationVertices

  /**
  * Performs depth-first search on the graph
  * @param start
  */
  private void DFS(Vertex start)  {
    /* Mark 'start' vertex as discovered and record entry time */
    start.discovered = true;
    this.time++;
    start.entryTime = this.time;

    /* Update reachableAncestor field */
    this.processVertexEarly(start);

    /* For every vertex adjacent to 'start',
    * process the edges and continue DFS, if needed
    */
    for (Vertex next : start.adjacent) {
      if (!next.discovered) {
        next.parent = start;
        this.processEdge(start, next);
        this.DFS(next);
      } else if (!next.processed) {
        this.processEdge(start, next);
      }
    }

    /* Determine any articulation vertices */
    this.processVertexLate(start);

    this.time++;

    /* Mark 'start' vertex as processed */
    start.processed = true;
  } //DFS

  /**
  * Updates reachableAncestor field of vertex
  * @param vtx a Vertex
  */
  private void processVertexEarly(Vertex vtx) {
    vtx.reachableAncestor = vtx;
  } //processVertexEarly

  /**
  * Updates graph based on type of edge given
  * @param vtx1 a Vertex
  * @param vtx2 a Vertex
  */
  private void processEdge(Vertex vtx1, Vertex vtx2) {
    EdgeClass ec = edgeCalc(vtx1, vtx2);

    if (EdgeClass.TREE == ec) {
      vtx1.treeOutDegree++;
    }

    if (((EdgeClass.BACK == ec) && (vtx1.parent != vtx2)) &&
    (vtx2.entryTime < vtx1.reachableAncestor.entryTime)) {
      vtx1.reachableAncestor = vtx2;
    }
  } //processEdge

  /**
  * Calculates the type of edge in the graph
  * @param v1 a Vertex
  * @param v2 a Vertex
  * @return an enum value of the edge
  */
  private EdgeClass edgeCalc(Vertex v1, Vertex v2)  {
    if (v2.parent == v1)  {
      return EdgeClass.TREE;
    }  else  if  (v2.discovered && !v2.processed)  {
      return EdgeClass.BACK;
    }  else  {
      return EdgeClass.UNCLASSIFIED;
    }
  } //edgeCalc

  /**
  * Determines any articulation vertices
  * @param vtx a Vertex
  */
  private void processVertexLate(Vertex vtx) {
    boolean root;   /* Is the vertex the root of the DFS tree? */
    int timeVtx;    /* Earliest reachable time for vtx */
    int timeParent; /* Earliest reachable time for vtx.parent */

    /* Test if vtx is the root */
    if (null == vtx.parent) {
      /* vtx is a root articulation vertex */
      if (vtx.treeOutDegree > 1) {
        this.articulationVertices.add(vtx.name);
      }
      return;
    }

    /* Is vtx.parent the root? */
    root = (null == vtx.parent.parent);

    /* vtx.parent is a parent articulation vertex */
    if ((vtx.reachableAncestor == vtx.parent) && (!root)) {
      this.articulationVertices.add(vtx.parent.name);
    }

    if (vtx.reachableAncestor == vtx) {
      /* vtx.parent is a bridge articulation vertex */
      if (!root) {
        this.articulationVertices.add(vtx.parent.name);
      }

      /* If vtx is not a leaf, it is a bridge articulation vertex */
      if (vtx.treeOutDegree > 0) {
        this.articulationVertices.add(vtx.name);
      }
    }

    /* Record time of vtx and its parent */
    timeVtx = vtx.reachableAncestor.entryTime;
    timeParent = vtx.parent.reachableAncestor.entryTime;

    /* Update vtx.parent's reachable ancestor if needed */
    if (timeVtx < timeParent) {
      vtx.parent.reachableAncestor = vtx.reachableAncestor;
    }
  } //processVertexLate
} //Graph Class
