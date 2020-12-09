package com.metalogic.graph2;

import java.util.HashSet;
import java.util.Set;

class SimpleVertexConnectivityProvider<V> implements InternalVertexConnectivityProvider<V> {

    private Set<V> targetedVertices = new HashSet<V> ();
    private Set<V> targetingVertices = new HashSet<V> ();

    public Set<V> getTargetedVertices ()
    {
        return targetedVertices;
    }

    public void addTargetedVertex (V vertex)
    {
        targetedVertices.add (vertex);
    }

    public void removeTargetedVertex (V vertex)
    {
        targetedVertices.remove (vertex);
    }

    public boolean hasTargetedVertices ()
    {
        return !targetedVertices.isEmpty ();
    }



    public Set<V> getTargetingVertices ()
    {
        return targetingVertices;
    }

    public void addTargetingVertex (V vertex)
    {
        targetingVertices.add (vertex);
    }

    public void removeTargetingVertex (V vertex)
    {
        targetingVertices.remove (vertex);
    }

    public boolean hasTargetingVertices ()
    {
        return !targetingVertices.isEmpty ();
    }
}
