import org.jetbrains.annotations.NotNull;

interface J<A> extends T<A> {
    @Override
    <B> int foofoofoo(@NotNull U<A> a, @NotNull B b);
}

abstract class J1<X> implements J<U<X>> {
    @Override
    public <C> int foofoofoo(@NotNull U<U<X>> xu, @NotNull C c) {
        throw new UnsupportedOperationException();
    }
}

abstract class J2 extends J1<String> {
    @Override
    public <C> int foofoofoo(@NotNull U<U<String>> xu, @NotNull C c) {
        throw new UnsupportedOperationException();
    }
}