package com.github.bingoohuang.utils.tuple;


import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A tuple that holds two non-null values.
 *
 * @param <T1> The type of the first nullable value held by this tuple
 */
@Getter @Setter @EqualsAndHashCode @AllArgsConstructor @NoArgsConstructor
public class Tuple1<T1> implements Iterable {
    T1 t1;

    /**
     * Get the object at the given index.
     *
     * @param index The index of the object to retrieve. Starts at 0.
     * @return The object or {@literal null} if out of bounds.
     */
    public Object get(int index) {
        switch (index) {
            case 0:
                return t1;
            default:
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
    }

    /**
     * Turn this {@literal Tuples} into a plain Object list.
     *
     * @return A new Object list.
     */
    public List<Object> toList() {
        return Arrays.asList(toArray());
    }

    /**
     * Turn this {@literal Tuples} into a plain Object array.
     *
     * @return A new Object array.
     */
    public Object[] toArray() {
        return new Object[]{t1};
    }

    @Override
    public Iterator<?> iterator() {
        return Collections.unmodifiableList(toList()).iterator();
    }

    /**
     * Return the number of elements in this {@literal Tuples}.
     *
     * @return The size of this {@literal Tuples}.
     */
    public int size() {
        return 1;
    }

    /**
     * A Tuple String representation is the comma separated list of values, enclosed
     * in square brackets.
     *
     * @return the Tuple String representation
     */
    @Override
    public final String toString() {
        return Tuples.tupleStringRepresentation(toArray()).insert(0, '[').append(']').toString();
    }
}
