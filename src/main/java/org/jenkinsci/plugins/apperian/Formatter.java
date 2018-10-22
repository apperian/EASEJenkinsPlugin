package org.jenkinsci.plugins.apperian;

public interface Formatter<T> {

    public T format(T value);

}
