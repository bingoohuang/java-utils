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
 * @param <T4> The type of the fourth nullable value held by this tuple
 * @param <T5> The type of the fifth nullable value held by this tuple
 * @param <T6> The type of the sixth value held by this tuple
 */
@Getter @Setter @EqualsAndHashCode(callSuper = true) @NoArgsConstructor
public class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> {
    T6 t6;

    public Tuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        super(t1, t2, t3, t4, t5);
        this.t6 = t6;
    }

    /**
     * Get the object at the given index.
     *
     * @param index The index of the object to retrieve. Starts at 0.
     * @return The object or {@literal null} if out of bounds.
     */
    public Object get(int index) {
        switch (index) {
            case 5:
                return t6;
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
        return new Object[]{t1, t2, t3, t4, t5, t6};
    }

    /**
     * Return the number of elements in this {@literal Tuples}.
     *
     * @return The size of this {@literal Tuples}.
     */
    public int size() {
        return 6;
    }
}
