package com.github.bingoohuang.utils.lang;

public class Throws {
    /**
     * Java 8 Type-Inference, Which simply means that every T in “T extends Throwable” is generously
     * inferred to be a RuntimeException if an inference of a more concrete type is not possible.
     *
     * @param t   Exception
     * @param <T> Subclass of Exception
     * @param <R> Return type
     * @return Anything required
     * @throws T RuntimeException
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T {
        throw (T) t; // ( ͡° ͜ʖ ͡°)
    }
}
