package com.metalogic.graph2;

public class TheAggregator
{
    public static SimpleGraph aggregate (SimpleGraph<String> simpleGraph)
    {
//        System.out.println ("ANALYZE!");

        final SimpleGraphColoring<String> groups = new SimpleGraphColoring<String> ();

        final AggregationColoringAlgorithm<String> algorithm
            = new AggregationColoringAlgorithm<String> (simpleGraph, groups);
        algorithm.run ();

        final int vertexCount = simpleGraph.vertices ().size ();
//        System.out.println ("simpleGraph.vertices ().size () = " + vertexCount);
        final int groupCount = groups.colorSet ().size ();
//        System.out.println ("groups.colorSet ().size () = " + groupCount);

//        System.out.println ("REDUCE!");
        final Aggregator aggregator = new Aggregator (simpleGraph, groups);
        return aggregator.run ();
    }
}
