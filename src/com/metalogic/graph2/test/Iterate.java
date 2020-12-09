package com.metalogic.graph2.test;

import com.metalogic.graph2.*;
import org.apache.log4j.BasicConfigurator;

public class Iterate
{
    public static void main (String[] args)
    {
        BasicConfigurator.configure ();

        final SimpleGraph<String> simpleGraph = graph1 ();

/*
        simpleGraph.addVertex ("A1");
        simpleGraph.addVertex ("A2");
        simpleGraph.addVertex ("B");

        simpleGraph.addEdge ("A1", "B");
        simpleGraph.addEdge ("A2", "B");
*/

/*

        simpleGraph.addVertex ("A1");
        simpleGraph.addVertex ("A2");
        simpleGraph.addVertex ("B1");
        simpleGraph.addVertex ("B2");
        simpleGraph.addVertex ("C1");
        simpleGraph.addVertex ("C2");

        simpleGraph.addEdge ("A1", "B1");
        simpleGraph.addEdge ("A1", "B2");
        simpleGraph.addEdge ("A2", "B1");
        simpleGraph.addEdge ("A2", "B2");

        simpleGraph.addEdge ("B1", "C1");
        simpleGraph.addEdge ("B1", "C2");
        simpleGraph.addEdge ("B2", "C1");
        simpleGraph.addEdge ("B2", "C2");
*/

        /*simpleGraph.addVertex ("O1");
        simpleGraph.addVertex ("O2");
        simpleGraph.addVertex ("A");
        simpleGraph.addVertex ("B");
        simpleGraph.addVertex ("C");

        simpleGraph.addEdge ("O1", "A");
        simpleGraph.addEdge ("O2", "A");
        simpleGraph.addEdge ("A", "B");
        simpleGraph.addEdge ("A", "C");
        simpleGraph.addEdge ("B", "C");*/

/*
        simpleGraph.addVertex ("A");
        simpleGraph.addVertex ("B");
        simpleGraph.addVertex ("C");

        simpleGraph.addEdge ("A", "B");
        simpleGraph.addEdge ("B", "C");
        simpleGraph.addEdge ("C", "A");
*/

/*
        simpleGraph.addVertex ("A1");
        simpleGraph.addVertex ("A2");
        simpleGraph.addVertex ("A3");
        simpleGraph.addVertex ("B");
        simpleGraph.addVertex ("C");
        simpleGraph.addVertex ("D");

        simpleGraph.addEdge ("A1", "B");
        simpleGraph.addEdge ("A2", "B");
        simpleGraph.addEdge ("A3", "B");
        simpleGraph.addEdge ("A1", "C");
        simpleGraph.addEdge ("A2", "C");
        simpleGraph.addEdge ("A3", "C");
        simpleGraph.addEdge ("B", "C");
        simpleGraph.addEdge ("C", "D");
*/


        final SimpleGraph aggregate1 = TheAggregator.aggregate (simpleGraph);
//        System.out.println ("aggregate1 = " + aggregate1);

//        System.out.println ("=====================================================");

        final SimpleGraph aggregate2 = TheAggregator.aggregate (aggregate1);
//        System.out.println ("aggregate2 = " + aggregate2);

        final SimpleGraphColoring<String> layerIds = new SimpleGraphColoring<String> ();
        final LayeredLayoutAlgorithm<String> layoutAlgorithm = new LayeredLayoutAlgorithm<String> (
            aggregate2, layerIds);
        layoutAlgorithm.run ();

//        System.out.println ("layerIds = " + layerIds);
    }


    /**
     *     /---C1--\
     *   B1         D1
     *  /  \---C2--/
     * A
     *  \  /---C3--\
     *   B2         D2
     *     \---C4--/
     */
    public static SimpleGraph<String> graph1 ()
    {
        final SimpleGraph<String> simpleGraph = new SimpleGraph<String> ();

        simpleGraph.addVertex ("A");
        simpleGraph.addVertex ("B1");
        simpleGraph.addVertex ("B2");
        simpleGraph.addVertex ("C1");
        simpleGraph.addVertex ("C2");
        simpleGraph.addVertex ("C3");
        simpleGraph.addVertex ("C4");
        simpleGraph.addVertex ("D1");
        simpleGraph.addVertex ("D2");

        simpleGraph.addEdge ("A", "B1");
        simpleGraph.addEdge ("A", "B2");
        simpleGraph.addEdge ("B1", "C1");
        simpleGraph.addEdge ("B1", "C2");
        simpleGraph.addEdge ("B2", "C3");
        simpleGraph.addEdge ("B2", "C4");
        simpleGraph.addEdge ("C1", "D1");
        simpleGraph.addEdge ("C2", "D1");
        simpleGraph.addEdge ("C3", "D2");
        simpleGraph.addEdge ("C4", "D2");

        return simpleGraph;

    }

    /**
     *
     *   A1-->B1
     *     \/
     *     /\
     *   A2-->B2
     *
     * 1. Mark "heavily targeted" vertices (vertices that are targeted by > 1 vertex
     * Assign a positive group number to every _group_ of these vertices.
     * Assign a negative group number to every _group_ of vertices targeting these vertices, if possible:
     *
     *   -1    1
     *
     *   -1    1
     *
     * 2. If there were "heavily targeted" groups detected, aggregate them..
     *
     *
     *   A1---\
     *         [B]
     *   A2---/
     *
     *
     * 3. Aggregate "targeting groups" (with negative group IDs)
     *
     *   [A]---[B] 
     */
    public static SimpleGraph<String> graph2 ()
    {
        final SimpleGraph<String> simpleGraph = new SimpleGraph<String> ();

        simpleGraph.addVertex ("A1");
        simpleGraph.addVertex ("A2");
        simpleGraph.addVertex ("B1");
        simpleGraph.addVertex ("B2");

        simpleGraph.addEdge ("A1", "B1");
        simpleGraph.addEdge ("A1", "B2");
        simpleGraph.addEdge ("A2", "B1");
        simpleGraph.addEdge ("A2", "B2");

        return simpleGraph;

    }


    /**
     * A1---\
     *       B1----C1
     * A2---/  \  /
     *          \/
     *          /\
     * A3---\  /  \
     *       B2----C2
     * A4---/
     * 
     */
    public static SimpleGraph<String> graph3 ()
    {
        final SimpleGraph<String> simpleGraph = new SimpleGraph<String> ();

        simpleGraph.addVertex ("A1");
        simpleGraph.addVertex ("A2");
        simpleGraph.addVertex ("A3");
        simpleGraph.addVertex ("A4");
        simpleGraph.addVertex ("B1");
        simpleGraph.addVertex ("B2");
        simpleGraph.addVertex ("C1");
        simpleGraph.addVertex ("C2");

        simpleGraph.addEdge ("A1", "B1");
        simpleGraph.addEdge ("A2", "B1");
        simpleGraph.addEdge ("A3", "B2");
        simpleGraph.addEdge ("A4", "B2");
        simpleGraph.addEdge ("B1", "C1");
        simpleGraph.addEdge ("B1", "C2");
        simpleGraph.addEdge ("B2", "C1");
        simpleGraph.addEdge ("B2", "C2");


        return simpleGraph;

    }
}
