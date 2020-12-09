package com.metalogic.graph2;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates a provided graph using provided information about grouping
 * RESTRICTION: vertices may not target themselves!
 */
public class Aggregator
{
    final SimpleGraph graph;
    final SimpleGraphColoring groups;

    private transient Map<Integer, Object> group2aggregatedVertex;
    private transient SimpleGraph aggregatedGraph;


    public Aggregator (final SimpleGraph graph, SimpleGraphColoring groups)
    {
        this.graph = graph;
        this.groups = groups;
    }


    /**
     * @return new, aggregated graph
     *         If several vertices are aggregated, a new subgraph is created for them, and edges to/from these
     *         vertices are replaced with edges to/from subgraph.
     *         Ungrouped vertices are included in the aggregated graph as is
     */
    public SimpleGraph run ()
    {
        aggregatedGraph = new SimpleGraph ();
        group2aggregatedVertex = new HashMap<Integer, Object> ();


        for (Object v : graph.vertices ())
        {
            Object aggregatedVertex = getAggregatedVertex (v);

            final SimpleGraph mySubgraph = ((aggregatedVertex instanceof SimpleGraph)
                ? (SimpleGraph)aggregatedVertex
                : null);


            for (Object targetingVertex : graph.getTargetingVertices (v))
            {
                final Object targetingAggregatedVertex = getAggregatedVertex (targetingVertex);
                if (targetingAggregatedVertex == aggregatedVertex)
                {
                    // ===
                    // assume vertices may not target themselves.
                    // it means that if aggregated vertices are the same => aggregated vertices are subgraphs
                    // ==

                    // we're in the same group!
                    // add an edge within the group only
                    mySubgraph.addEdge (targetingVertex, v);
//                    System.out.println ("=============================================");
//                    System.out.println ("mySubgraph = " + mySubgraph);
//                    System.out.println ("targetingVertex = " + targetingVertex);
//                    System.out.println ("=============================================");
                }
                else{
//                    System.out.println ("Adding edge " + targetingAggregatedVertex + "->" + aggregatedVertex);
                    aggregatedGraph.addEdge (targetingAggregatedVertex, aggregatedVertex);
                }
            }


            for (Object targetedVertex : graph.getTargetedVertices (v))
            {
                final Object targetedAggregatedVertex = getAggregatedVertex (targetedVertex);
                if (targetedAggregatedVertex == aggregatedVertex)
                {
                    // ===
                    // assume vertices may not target themselves.
                    // it means that if aggregated vertices are the same => aggregated vertices are subgraphs
                    // ==

                    // we're in the same group!
                    // add an edge within the group only
                    mySubgraph.addEdge (v, targetedVertex);
//                    System.out.println ("=============================================");
//                    System.out.println ("mySubgraph = " + mySubgraph);
//                    System.out.println ("targetedVertex = " + targetedVertex);
//                    System.out.println ("=============================================");
                }
                else{
//                    System.out.println ("Adding edge " + aggregatedVertex + "->" + targetedAggregatedVertex);
                    aggregatedGraph.addEdge (aggregatedVertex, targetedAggregatedVertex);                    
                }
            }
        }

        return aggregatedGraph;
    }


    private Object getAggregatedVertex (Object v)
    {
        final int group = groups.getColor (v);

        // ------
        if (group == 0) {
            if (!aggregatedGraph.hasVertex (v))
                aggregatedGraph.addVertex (v);
            return v;
        }
        // ------

//        System.out.println ("Vertex has group!!!");
        Object aggregatedVertex = group2aggregatedVertex.get (group);
        if (aggregatedVertex == null)
        {
            if (verticesInGroup (group) == 1)
            {
//                System.out.println ("But it's alone in the group!!!");
                aggregatedVertex = v;
            }
            else
            {
//                System.out.println ("And there are several vertices!!!");
                final SimpleGraph subgraph = new SimpleGraph ();
                subgraph.addVertex (v);
                
                aggregatedVertex = subgraph;
            }

            group2aggregatedVertex.put (group, aggregatedVertex);
        }
        else {
            if (aggregatedVertex instanceof SimpleGraph) {
                final SimpleGraph subgraph = (SimpleGraph) aggregatedVertex;
                if (!subgraph.hasVertex (v) && (subgraph != v)) subgraph.addVertex (v);
            }
        }

        if (!aggregatedGraph.hasVertex (aggregatedVertex))
            aggregatedGraph.addVertex (aggregatedVertex);
        
        return aggregatedVertex;
    }



    public int verticesInGroup (int group)
    {
        int count = 0;
        for (Object v : graph.vertices ())
        {
            if (groups.getColor (v) == group) count++;
        }

        return count;
    }
}
