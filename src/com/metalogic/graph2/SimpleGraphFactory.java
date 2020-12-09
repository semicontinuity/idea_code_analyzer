package com.metalogic.graph2;

public class SimpleGraphFactory implements GraphFactory
{
    public SimpleGraph newGraph ()
    {
        return new SimpleGraph ();
    }
}
