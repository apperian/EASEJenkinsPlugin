package org.jenkinsci.plugins.ease;

public interface Formatter<T> {

    public T format(T value);

}
