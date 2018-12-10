package com.github.bingoohuang.utils.tuple;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A tuple that holds two non-null values.
 *
 * @param <T1> The type of the first nullable value held by this tuple
 * @param <T2> The type of the second nullable value held by this tuple
 * @param <T3> The type of the third nullable value held by this tuple
 */
@Getter @Setter @EqualsAndHashCode(callSuper = true) @NoArgsConstructor
public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
    T3 t3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        super(t1, t2);
        this.t3 = t3;
    }

    /**
     * Get the object at the given index.
     *
     * @param index The index of the object to retrieve. Starts at 0.
     * @return The object or {@literal null} if out of bounds.
     */
    public Object get(int index) {
        switch (index) {
            case 2:
                return t3;
            default:
                return super.get(index);
        }
    }

    /**
     * Turn this {@literal Tuples} into a plain Object array.
     *
     * @return A new Object array.
     */
    public Object[] toArray() {
        return new Object[]{t1, t2, t3};
    }

    /**
     * Return the number of elements in this {@literal Tuples}.
     *
     * @return The size of this {@literal Tuples}.
     */
    public int size() {
        return 3;
    }
}
