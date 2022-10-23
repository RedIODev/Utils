package dev.redio.utils.string;

import java.util.Objects;

final class StrImpl implements Str {

    private final CharSequence cs;

    public StrImpl(CharSequence cs) {
        this.cs = Objects.requireNonNull(cs);
    }

    @Override
    public int length() {
        return this.cs.length();
    }

    @Override
    public char charAt(int index) {
        return this.cs.charAt(index);
    }

    @Override
    public Str subSequence(int start, int end) {
        return new StrImpl(this.cs.subSequence(start, end));
    }

    @Override
    public String toString() {
        return this.cs.toString();
    }
    
}
