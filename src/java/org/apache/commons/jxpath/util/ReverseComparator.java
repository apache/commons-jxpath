package org.apache.commons.jxpath.util;

import java.util.Comparator;

/**
 * Reverse comparator.
 *
 * @author Dmitri Plotnikov
 * @version $Revision:$ $Date:$
 */
public class ReverseComparator implements Comparator {
    /**
     * Singleton reverse comparator instance.
     */
    public static final Comparator INSTANCE = new ReverseComparator();

    private ReverseComparator() {
    }

    /**
     * {@inheritDoc}
     */
    public int compare(Object o1, Object o2) {
        return ((Comparable) o2).compareTo(o1);
    }

}
