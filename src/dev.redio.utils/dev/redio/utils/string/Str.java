package dev.redio.utils.string;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Str 
    extends CharSequence, Comparable<CharSequence> {
    
    public static Str of(CharSequence cs) {
        return new StrImpl(cs);
    }

    @Override
    Str subSequence(int start, int end);

    default Str subSequence(int start) {
        return this.subSequence(start, this.length());
    }

    @Override
    default int compareTo(CharSequence other) {
        final int length = Math.min(this.length(), other.length());
        for (int i = 0; i < length; i++) {
            int dif = this.charAt(i) - other.charAt(i);
            if (dif == 0)
                continue;
            return dif;
        }
        return this.length() - other.length();
    }

    default Str concat(CharSequence other) {
        return Str.of(new LinkedSequence(this, other));
    }

    default boolean contains(CharSequence cs) {
        return this.indexOf(cs) >= 0;
    }

    default boolean containsAfter(CharSequence cs, int fromIndex) {
        return this.indexOf(cs, fromIndex) >= 0;
    }

    default boolean containsBefore(CharSequence cs, int toIndex) { 
        //toIndex is Exclusive
        final int index = this.indexOf(cs);
        return index >= 0 && index + cs.length() < toIndex;
    }

    default boolean contentEquals(CharSequence cs) {
        final int length = this.length();
        if (length != cs.length())
            return false;
        for (int i = 0; i < length; i++) 
            if (this.charAt(i) != cs.charAt(i))
                return false;
        return true;
    }

    default int indexOf(CharSequence cs) {
        return this.indexOf(cs, 0);
    }

    default int indexOf(CharSequence cs, int fromIndex) {
        final int length = this.length();
        if (cs.length() == 0)
            return -1;
        final char firstChar = cs.charAt(0);
        final int firstIndex = this.indexOf(firstChar, fromIndex);
        for (int i = firstIndex; i < length; i = this.indexOf(firstChar, i + 1)) {
            if (i == -1)
                break;
            if (this.isEqualSequence(i, cs))
                return i;
        }
        return -1;
    }

    default int indexOf(char c) {
        return this.indexOf(c, 0);
    }

    default int indexOf(char c, int fromIndex) {
        final int length = this.length();
        for (int i = fromIndex; i < length; i++) 
            if (this.charAt(i) == c)
                return i;
        return -1;
    }

    default int lastIndexOf(CharSequence cs) {
        return this.lastIndexOf(cs, this.length() - 1);
    }

    default int lastIndexOf(CharSequence cs, int fromIndex) {
        if (cs.length() == 0)
            return -1;
        final int csLength = cs.length();
        final char lastChar = cs.charAt(csLength - 1);
        final int lastIndex = this.lastIndexOf(lastChar, fromIndex);
        for (int i = lastIndex; i >= 0; i = this.lastIndexOf(lastChar, i - 1)) 
            if (this.isEqualSequenceReverse(i, cs))
                return i - csLength;
        return -1;
    }

    default int lastIndexOf(char c) {
        return this.lastIndexOf(c, this.length() - 1);
    }

    default int lastIndexOf(char c, int fromIndex) {
        for (int i = fromIndex; i >= 0; i--) 
            if (this.charAt(i) == c)
                return i;
        return -1;
    }

    default boolean endsWith(CharSequence suffix) {
        return this.containsAfter(suffix, this.length() - suffix.length());
    }

    default boolean startsWith(CharSequence prefix) {
        return this.containsBefore(prefix, prefix.length());
    }

    default Stream<Str> lines() {
        final class Splitter implements Spliterator<Str> {

            private int index;
            private final int length;

            Splitter(int index, int length) {
                this.index = index;
                this.length = length;
            }

            private Str next() {
                final int lastIndex = index;
                final int endOfLine = this.endOfLine(lastIndex);
                this.index = startOfLine(endOfLine);
                return Str.this.subSequence(lastIndex, endOfLine);
            }

            private int endOfLine(int start) {
                for (int i = start; i < this.length; i++) {
                    char c = Str.this.charAt(i);
                    if (c == '\n' || c == '\r')
                        return i;
                }
                return this.length;
            }

            private int startOfLine(int endOfLine) {
                endOfLine++;
                if (endOfLine < this.length && Str.this.charAt(endOfLine) =='\n')
                    endOfLine++;
                return endOfLine;
            }

            @Override
            public boolean tryAdvance(Consumer<? super Str> action) {
                if (index >= length)
                    return false;
                action.accept(this.next());
                return true;
            }

            @Override
            public Spliterator<Str> trySplit() {
                final int half = (this.length + this.index) >>> 1;
                final int midpoint = this.startOfLine(this.endOfLine(half));
                if (midpoint >= this.length)
                    return null;
                final int lastIndex = this.index;
                this.index = midpoint;
                return new Splitter(lastIndex, midpoint - lastIndex);
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.NONNULL;
            }
        }
        return StreamSupport.stream(new Splitter(0, this.length()), false);
    }

    default Str repeat(int count) {
        Str result = this;
        for (int i = 0; i < count; i++) 
            result = result.concat(this);
        return result;
    }

    default Str[] split(CharSequence separator) {
        var list = new ArrayList<Str>();
        int startIndex = 0;
        while (true) {
            int endIndex = this.indexOf(separator, startIndex);
            if (endIndex < 0) {
                list.add(this.subSequence(startIndex, this.length()));
                return list.toArray(Str[]::new);
            }
            list.add(this.subSequence(startIndex, endIndex));
            startIndex = endIndex + separator.length();
        }
    }

    default Str strip() {
        final int start = this.findNonWhiteSpaceIndex();
        final int end = this.findNonWhiteSpaceIndexReverse();
        return this.subSequence(start, end + 1);
    }

    default Str stripLeading() {
        final int start = this.findNonWhiteSpaceIndex();
        return this.subSequence(start);
    }

    default Str stripTrailing() {
        final int end = this.findNonWhiteSpaceIndexReverse();
        return this.subSequence(0, end + 1);
    }

    private int findNonWhiteSpaceIndex() {
        final int length = this.length();
        for (int i = 0; i < length; i++) 
            if (!Character.isWhitespace(this.charAt(i)))
                return i;
        return 0;
    }

    private int findNonWhiteSpaceIndexReverse() {
        final int length = this.length();
        for (int i = length; i >= 0; i--) 
            if (!Character.isWhitespace(this.charAt(i)))
                return i;
        return 0;
    }

    private boolean isEqualSequence(int fromIndex, CharSequence cs) {
        final int length = cs.length();
        if (this.length() - fromIndex < length)
            return false;
        for (int i = 0; i < length; i++) 
            if (this.charAt(i + fromIndex) != cs.charAt(i))
                return false;
        return true;
    }

    private boolean isEqualSequenceReverse(int fromIndex, CharSequence cs) {
        if (fromIndex - cs.length() < 0)
            return false;
        for (int i = cs.length() - 1, j = fromIndex; i >= 0; i--, j--) 
            if (this.charAt(j) != cs.charAt(i))
                return false;
        return true;
    }
}
