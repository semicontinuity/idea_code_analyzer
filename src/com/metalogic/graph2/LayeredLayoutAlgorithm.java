package com.metalogic.graph2;

public class LayeredLayoutAlgorithm<V>
{

    final SimpleGraph<V> graph;
    final SimpleGraphColoring<V> layers;


    public LayeredLayoutAlgorithm (final SimpleGraph<V> graph, SimpleGraphColoring<V> layers)
    {
        this.graph = graph;
        this.layers = layers;
    }

    public void run ()
    {
        init ();

        int relaxationCount;
        do
        {
            relaxationCount = 0;
//            System.out.println ("RELAXATION LOOP");
            
            for (V v : graph.vertices ())
            {
                final int layerId = layers.getColor (v);

                for (V targetedVertex : graph.getTargetedVertices (v))
                {
                    final int targetedVertexLayerId = layers.getColor (targetedVertex);
                    if (layerId < Integer.MAX_VALUE && targetedVertexLayerId > layerId + 1)
                    {
                        relaxationCount++;
                        layers.setColor (targetedVertex, layerId + 1);
                    }
                }
            }
        }
        while (relaxationCount > 0);
    }

    public void init ()
    {
        for (V v : graph.vertices ())
        {
            layers.setColor (
                v, graph.getTargetingVertices (v).size () == 0
                ? 0
                : Integer.MAX_VALUE);
        }
    }
}
