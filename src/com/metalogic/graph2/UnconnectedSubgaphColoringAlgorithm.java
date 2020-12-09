package com.metalogic.graph2;

/** Assigns different colors (groups) to the different mutually unconnected subgraphs of a given graph */
public class UnconnectedSubgaphColoringAlgorithm<V>
{
    final SimpleGraph<V> graph;
    final SimpleGraphColoring<V> groups;

    private int group;


    public UnconnectedSubgaphColoringAlgorithm (final SimpleGraph<V> graph, SimpleGraphColoring<V> groups)
    {
        this.graph = graph;
        this.groups = groups;
    }

    public void run ()
    {
        for (V v : graph.vertices ())
        {
            int vertexGroup = tryToAssignGroup (v);

            for (final V targetedVertex : graph.getTargetedVertices (v))
            {
                final int targetedVertexGroup = groups.getColor (targetedVertex);
                if (targetedVertexGroup == 0) {
                    groups.setColor (targetedVertex, vertexGroup);
                }
                else if (targetedVertexGroup != vertexGroup) {
                    changeGroup (targetedVertexGroup, vertexGroup);
                }
            }
        }
    }


    private int tryToAssignGroup (final V v)
    {
        int vertexGroup = groups.getColor (v);
        if (vertexGroup == 0)
        {
            vertexGroup = ++group;
//            System.out.println ("  ############ set group of " + v + " to newly allocated value " + vertexGroup);
            groups.setColor (v, vertexGroup);
        }
        return vertexGroup;
    }

    public void changeGroup (int oldGroup, int newGroup)
    {
        for (V v : graph.vertices ())
        {
            final int color = groups.getColor (v);
            if (color == oldGroup)
                groups.setColor (v, newGroup);
        }
    }
}
