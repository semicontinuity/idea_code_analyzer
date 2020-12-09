package com.metalogic.graph2;

/**
 * InternalVertexDataProvider defines a color for every vertex.
 * A vertex may choose not to implement this interface and be independent of it - in this case
 * the provider's implementation will have to keep per-vertex storage of color.
 * Typically, for every vertex in the graph there will be two object allocated - the vertex itself and
 * an object keeping vertex internal data ('shadow'). Also, a vertex must implement equals() and hashCode()
 * because it will be looked in the provider's storage using these methods.
 *
 * Alternatively, a vertex may choose to implement it - in this case, only one object per vertex is needed.
 *
 * A vertex may logically participate in different colorings;
 * in this case a vertex may not implement the interface.
 */
public interface InternalVertexColoringProvider
{
    /**
     * Sets a 'color' of vertex.
     * Necessary for algorithms that 'color' the graph.
     */
    void setColor(int color);

    /**
     * Gets a 'color' of vertex.
     * Necessary for algorithms that 'color' the graph.
     */
    int getColor();
}