package com.metalogic.graph2;

public class SimpleEdge<V>
{
    private V source;
    private V target;

    public SimpleEdge (V source, V target)
    {
        this.source = source;
        this.target = target;
    }

    public boolean equals (Object o)
    {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        SimpleEdge that = (SimpleEdge) o;

        if (!source.equals (that.source)) return false;
        if (!target.equals (that.target)) return false;

        return true;
    }

    public int hashCode ()
    {
        int result;
        result = source.hashCode ();
        result = 31 * result + target.hashCode ();
        return result;
    }

    @Override public String toString ()
    {
        return source + "->" + target;
    }
}
