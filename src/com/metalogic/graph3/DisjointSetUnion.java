package com.metalogic.graph3;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import one.util.streamex.EntryStream;

public class DisjointSetUnion<E> {
    private final Map<E, E> parents = new HashMap<>();

    public void add(E e) {
        parents.put(e, e);
    }


    E findRepresentative(E e) {
        E parent = parents.get(e);
        if (e == parent)
            return e;
        return findRepresentative(parent);
    }

    public void merge(E e1, E e2) {
        E a = findRepresentative(e1);
        E b = findRepresentative(e2);
        if (a != b) {
            parents.put(b, a);
        }
    }

    public Collection<List<E>> disjointSets() {
        return EntryStream.of(parents)
                .keys()
                .groupingBy(this::findRepresentative)
                .values();
    }
}
