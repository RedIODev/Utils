package dev.redio.utils.string;

import java.util.Objects;

public final class LinkedSequence 
    implements CharSequence {

    private final CharSequence c1;
    private final CharSequence c2;

    public LinkedSequence(CharSequence c1, CharSequence c2) {
        this.c1 = Objects.requireNonNull(c1);
        this.c2 = Objects.requireNonNull(c2);
    }

    @Override
    public int length() {
        return this.c1.length() + this.c2.length();
    }

    @Override
    public char charAt(int index) {
        Objects.checkIndex(index, this.length());
        final int midpoint = this.c1.length();
        return (index < midpoint) ?
            this.c1.charAt(index) :
            this.c2.charAt(index - midpoint);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        final int midpoint = this.c1.length();
        if (start > midpoint)
            return this.c2.subSequence(start - midpoint, end - midpoint);
        if (end <= midpoint)
            return this.c1.subSequence(start, end);
        return new LinkedSequence(
            this.c1.subSequence(start, midpoint), 
            this.c2.subSequence(0, end - midpoint));
    }

    @Override
    public String toString() {
        return this.c1.toString() + this.c2.toString();
    }
}
