package com.metalogic.graph2;

import java.util.Set;

public class AggregationColoringAlgorithm<V>
{
//    private static final Logger LOGGER = Logger.getLogger (AggregationColoringAlgorithm.class);

    final SimpleGraph<V> graph;
    final SimpleGraphColoring<V> groups;
    private int group;


    public AggregationColoringAlgorithm (final SimpleGraph<V> graph, SimpleGraphColoring<V> groups)
    {
        this.graph = graph;
        this.groups = groups;
    }

    public static final int VISIT_FLAG__VISITED_AS_TARGETING = 0x00000001;
    public static final int VISIT_FLAG__VISITED_AS_TARGETED = 0x00000002;
    public static final int VISIT_FLAG__VISITED_IN_DEPTH_SCAN = 0x00000004;
    public static final int VISIT_FLAG__VISITED_FROM = 0x00000008;
    public static final int VISIT_FLAG__HAS_TARGETING = 0x00000010;

    public void run ()
    {
        // colors:
        // 0= unvisited
        // 1= visited during depth scan as targeting
        // 2= visited during depth scan as targeted
        // 3= visited during depth scan as targeting and targeted

        // 1 = flag: visited during depth scan as targeting
        // 2 = flag: visited during depth scan as targeted
        // 4 = flag: visited during depth scan
        // 8 = flag: visited during global scan
        // 16= flag: has targeting vertexes
        final SimpleGraphColoring<V> visitStates = new SimpleGraphColoring<V> ();

        for (V v : graph.vertices ())
        {
//            System.out.println ("v = " + v);
            int visitState = visitStates.getColor (v);
            if ((visitState & VISIT_FLAG__VISITED_FROM) != 0) continue;
            visitState &= VISIT_FLAG__VISITED_FROM;

            // At this moment vertex was either not visited at all (=> no group)
            // or visited during depth scan
            final Set<V> targetingVertices = graph.getTargetingVertices (v);

//            int vertexGroup = tryToAssignGroup (v);

            if (targetingVertices.size () > 0)
            {
//                System.out.println ("  has " + targetingVertices.size () + " targeting vertices..");
                visitStates.setColor (v, visitState & VISIT_FLAG__HAS_TARGETING);
//                System.out.println ("  set flag HAS_TARGETING");

                int commonTargetingVertexGroup = 0;

                if (targetingVertices.size () > 1 /*true*/)
                {
                    int vertexGroup = tryToAssignGroup (v);
//                    System.out.println ("  more than 1 targeting vertex, explore...");

                    for (final V targetingVertex : targetingVertices)
                    {
//                        System.out.println ("  targetingVertex = " + targetingVertex);

                        int targetingVertexGroup = groups.getColor (targetingVertex);
//                        System.out.println ("    targetingVertexGroup = " + targetingVertexGroup);
                        if (commonTargetingVertexGroup == 0) commonTargetingVertexGroup = targetingVertexGroup;
                        if (commonTargetingVertexGroup == 0) {
//                            System.out.println ("    targetingVertex has no group, and no common group yet... allocate commonTargetingVertexGroup");
                            commonTargetingVertexGroup = ++group;
//                            System.out.println ("    commonTargetingVertexGroup=" + commonTargetingVertexGroup);
                        }

                        if (targetingVertexGroup == 0)
                        {
                            targetingVertexGroup = commonTargetingVertexGroup;
//                            System.out.println ("    ##### set group  = " + targetingVertexGroup);
                            groups.setColor (targetingVertex, commonTargetingVertexGroup);
                        }
                        else {
                            if (targetingVertexGroup != commonTargetingVertexGroup) {
//                                System.out.println ("    ##### rename group " + targetingVertexGroup + " to " + commonTargetingVertexGroup);
                                changeGroup (targetingVertexGroup, commonTargetingVertexGroup);
                            }
                        }


                        int targetingVertexState = visitStates.getColor (targetingVertex);
/*
                        if ((targetingVertexState & VISIT_FLAG__VISITED_AS_TARGETING) != 0)
                        {
                            System.out.println ("    was visited as targeting, skip");
                            continue;
                        }
*/

                        targetingVertexState |= VISIT_FLAG__VISITED_AS_TARGETING;
                        visitStates.setColor (targetingVertex, targetingVertexState);


                        final Set<V> targetedVertices = graph.getTargetedVertices (targetingVertex);
                        for (final V targetedVertex : targetedVertices)
                        {
//                            System.out.println ("    targetedVertex = " + targetedVertex);
                            int targetedVertexState = visitStates.getColor (targetedVertex);
                            visitStates.setColor (targetedVertex, targetedVertexState);
//                            System.out.println ("      set flag VISITED_AS_TARGETED");
                            targetedVertexState |= VISIT_FLAG__VISITED_AS_TARGETED; // TODO
                            
                            // change color in any case
                            int targetedVertexGroup = groups.getColor (targetedVertex);
                            if (targetedVertexGroup == 0)
                            {
                                targetedVertexGroup = ++group;
//                                System.out.println ("      ##### set group of " + targetedVertex + " to " + targetedVertexGroup);
                                groups.setColor (targetedVertex, targetedVertexGroup);
                            }

                            if (targetedVertexGroup != vertexGroup)
                            {
//                                System.out.println ("      ##### rename group " + targetedVertexGroup + " to " + vertexGroup);
                                changeGroup (targetedVertexGroup, vertexGroup);
                            }
                        }
                    }
                }
            }
            else
            {
                visitStates.setColor (v, visitState);
//                System.out.println ("  no targeting vertices...");
            }
//            System.out.println ("----------------------------");
//            System.out.println ("graphColoring = " + groups);
//            System.out.println ("----------------------------");
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
