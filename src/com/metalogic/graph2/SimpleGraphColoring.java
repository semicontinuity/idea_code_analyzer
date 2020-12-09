package com.metalogic.graph2;

import java.util.*;

public class SimpleGraphColoring<V>
{
    private Map<V, Integer> vertex2color = new HashMap<V, Integer> ();


    public void setColor (V vertex, int color)
    {
        vertex2color.put (vertex, color);
    }

    public int getColor (V vertex)
    {
        final Integer color = vertex2color.get (vertex);
        return color == null ? 0 : color;
    }

    public Set<Integer> colorSet ()
    {
        final Set<Integer> set = new HashSet<Integer> ();
        set.addAll (vertex2color.values ());
        return set;
    }

    @Override public String toString ()
    {
        return vertex2color.toString ();
    }
}