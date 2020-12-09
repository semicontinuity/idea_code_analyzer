package com.metalogic.graph2;

import java.util.*;

public class SimpleGraph<V>
{
    // IMPL NOTE: Set<V>?
    private List<V> vertices = new ArrayList<V> ();
//    private Map<V, V> vertex2targetedVertices = new HashMap<V, V> ();
//    private Map<V, V> vertex2targetingVertices = new HashMap<V, V> ();
    private Map<V, InternalVertexConnectivityProvider<V>> vertex2connectivityProvider
        = new HashMap<V, InternalVertexConnectivityProvider<V>> ();

    private Set<SimpleEdge<V>> edges = new HashSet<SimpleEdge<V>> ();


    public Set<V> getTargetedVertices(V v) {
        return vertex2connectivityProvider.get (v).getTargetedVertices ();
    }

    public Set<V> getTargetingVertices(V v) {
        return vertex2connectivityProvider.get (v).getTargetingVertices ();
    }


    public void addVertex (V vertex)
    {
        vertices.add (vertex);
        vertex2connectivityProvider.put (vertex, new SimpleVertexConnectivityProvider<V> ());
    }

    public boolean hasVertex (V vertex)
    {
        return vertices.contains (vertex);
    }


    public void addEdge (V source, V target)
    {
        if (!hasVertex (source)) throw new IllegalArgumentException ("Source is not part of a graph!");
        if (!hasVertex (target)) throw new IllegalArgumentException ("Target is not part of a graph!");

        edges.add (new SimpleEdge<V> (source, target));

        final InternalVertexConnectivityProvider<V> sourceConnections
            = vertex2connectivityProvider.get (source);
        sourceConnections.addTargetedVertex (target);

        final InternalVertexConnectivityProvider<V> targetConnections
            = vertex2connectivityProvider.get (target);
        targetConnections.addTargetingVertex (source);
    }

    public Collection<V> vertices ()
    {
        return vertices;
    }

    @Override public String toString ()
    {
        StringBuilder s = new StringBuilder("GRAPH " + Integer.toHexString(hashCode()) +  " {\n  " + vertices.size () + " vertices: ");
        s.append ('(');
        for (V vertice : vertices)
        {
            s.append (vertice instanceof SimpleGraph ? "GRAPH[" + Integer.toHexString(vertice.hashCode()) + "]" : vertice.toString ());
            s.append (',');
        }
        s.append (')');

        return s +

             ";\n  " + edges.size () + " edges: " + edges.toString () + "\n}";
    }
}

