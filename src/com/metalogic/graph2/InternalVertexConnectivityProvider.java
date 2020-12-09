package com.metalogic.graph2;

import java.util.Set;

/**
 * InternalVertexDataProvider defines a set of internal attributes for every vertex.
 * These attributes are only necessary for graph algorithms to work faster.
 * A vertex may choose not to implement this interface and be independent of it - in this case
 * the provider's implementation will have to keep per-vertex storage of these attributes.
 * Typically, for every vertex there will be two object allocated - the vertex itself and
 * an object keeping vertex internal data ('shadow'). Also, a vertex must implement equals() and hashCode()
 * because it will be looked in the provider's storage using these methods.
 *
 * Alternatively, a vertex may choose to implement it - in this case, only one object per vertex is needed.
 *
 * A vertex may logically participate in different graphs; in this case, lists of targeted/targeting vertices
 * depend on a logical graph in hand, and a vertex may not implement the interface.
 */
public interface InternalVertexConnectivityProvider<V>
{
    /**
     * Returns a list of 'targeted' vertices
     */
    Set<V> getTargetedVertices();

    void addTargetedVertex (V vertex);

    void removeTargetedVertex (V vertex);

    boolean hasTargetedVertices();


    /**
     * Returns a list of 'targeted' vertices
     */
    Set<V> getTargetingVertices();

    void addTargetingVertex (V vertex);

    void removeTargetingVertex (V vertex);

    boolean hasTargetingVertices();
}
