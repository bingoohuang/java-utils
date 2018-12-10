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
 * @param <T2> The type of the second nullable value held by this tuple
 */
@Getter @Setter @EqualsAndHashCode(callSuper = true) @AllArgsConstructor @NoArgsConstructor
public class Tuple2<T1, T2> extends Tuple1<T1> {
    T2 t2;


    public Tuple2(T1 t1, T2 t2) {
        super(t1);
        this.t2 = t2;
    }

    /**
     * Get the object at the given index.
     *
     * @param index The index of the object to retrieve. Starts at 0.
     * @return The object or {@literal null} if out of bounds.
     */
    public Object get(int index) {
        switch (index) {
            case 1:
                return t2;
            default:
                return super.get(index);
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
        return new Object[]{t1, t2};
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
        return 2;
    }

}
