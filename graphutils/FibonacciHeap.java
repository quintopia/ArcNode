/*
 * Created on Oct 31, 2006
 *
 * TODO 
 */
package graphutils;

import java.util.Hashtable;

public class FibonacciHeap<E> {
    FibHeapNode<E> min = null;
    Hashtable<E,FibHeapNode<E>> datamap = new Hashtable<E,FibHeapNode<E>>();

    int n = 0;

    public FibonacciHeap() {
        // empty constructor
    }

    private FibHeapNode<E> nodeOf(E o) {
    	return datamap.get(o);
    }
    
    /**
     * Extract highest priority element's object
     */
    public E extractMin() {
        FibHeapNode<E> z = min;
        FibHeapNode<E> c, t, q;
        if (z != null) {
            c = z.getChild();
            t = c;
            if (c != null) {
                do {
                    // put t in root list
                    q = t.getNext();
                    t.insertBefore(z);
                    t.setParent(null);
                    t = q;
                } while (t != c);
                z.setChild(null);
            }
            // remove z from root list
            z.remove();
            if (z == z.getNext()) {
                min = null;
            } else {
                min = z.getNext();
                consolidate();
            }
            n--;
            return z.getObject();
        }
        return null;
    }

    /**
     * insert new element
     * 
     */
    public void insert(E o, double key) {
        FibHeapNode<E> out = null;
        if (min == null) {
            min = new FibHeapNode<E>(o, key, null, null);
            min.setNext(min);
            min.setPrev(min);
            out = min;
        } else {
            out = new FibHeapNode<E>(o, key, min.getPrev(), min);
            min.getPrev().setNext(out);
            min.setPrev(out);
            if (key < min.getKey()) {
                min = out;
            }
        }
        n++;
        datamap.put(o,min);
        return;
    }

    public void decreaseKey(E o, double key) throws KeyNotLessException {
        FibHeapNode<E> x = nodeOf(o);
        FibHeapNode<E> y;
        if (key >= x.getKey()) {
            throw new KeyNotLessException(
                    "New key greater than or equal to old key");
        }
        x.setKey(key);
        y = x.getParent();
        if (y != null && x.getKey() < y.getKey()) {
            cut(x, y);
            cascadingCut(y);
        }
        if (x.getKey() < min.getKey())
            min = x;
    }

    public void delete(E o) {
        try {
            decreaseKey(o, Integer.MIN_VALUE);
        } catch (Exception e) {
            // Not likely to happen and doesn't matter if it does
        }
        extractMin();
    }
    
    public double getKey(E o) {
    	return nodeOf(o).getKey();
    }

    private void cascadingCut(FibHeapNode<E> y) {
        FibHeapNode<E> z = y.getParent();
        if (z != null) {
            if (!y.isMarked())
                y.setMark(true);
            else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    private void cut(FibHeapNode<E> x, FibHeapNode<E> y) {
        if (x.getPrev() == x) {
            y.setChild(null);
        } else {
            x.remove();
            if (y.getChild() == x)
                y.setChild(x.getNext());
        }
        y.setDegree(y.getDegree() - 1);
        x.insertBefore(min);
        x.setParent(null);
        x.setMark(false);
    }

    private void consolidate() {
        int maxdeg = n; // very cautious overestimation
        FibHeapNode<?>[] a = new FibHeapNode<?>[maxdeg];
        FibHeapNode<?>[] sp = new FibHeapNode<?>[maxdeg];
        FibHeapNode<E> t, x, y, p;
        int d, i;
        t = min;
        i = 0;
        do {
            sp[i++] = t;
            t = t.getNext();
        } while (t != min);
        for (int j = 0; j < i; j++) {
            x = (FibHeapNode<E>)sp[j];
            d = x.getDegree();
            while (a[d] != null) {
                y = (FibHeapNode<E>)a[d];
                if (x.getKey() > y.getKey()) {
                    p = x;
                    x = y;
                    y = p;
                }
                link(y, x);
                a[d] = null;
                d++;
            }
            a[d] = x;
        }
        min = null;
        for (i = 0; i < maxdeg; i++) {
            if (a[i] != null) {
                if (min == null) {
                    min = (FibHeapNode<E>)a[i];
                    min.setNext(min);
                    min.setPrev(min);
                } else {
                    ((FibHeapNode<E>)a[i]).insertBefore(min);
                    if (a[i].getKey() < min.getKey()) {
                        min = (FibHeapNode<E>)a[i];
                    }
                }
            }
        }
    }

    private void link(FibHeapNode<E> y, FibHeapNode<E> x) {
        y.remove();
        y.setParent(x);
        if (x.getChild() == null) {
            x.setChild(y);
            y.setNext(y);
            y.setPrev(y);
        } else {
            y.insertBefore(x.getChild());
        }
        if (y.getNext() == x) {
            printTree();
        }
        x.setDegree(x.getDegree() + 1);
        y.setMark(false);
    }

    public boolean isEmpty() {
        return min == null;
    }
    
    public E peek() {
        if (min == null)
            return null;
        else
            return min.getObject();
    }

    /**
     * print me to stdout
     * 
     */
    public void printTree() {
        System.out.println("Tree begins here:");
        printTree(min, -1);
    }

    /**
     * Print tree at root to stdout
     * 
     * @param root
     *            node in root list of tree to be printed
     */
    public static <E> void printTree(FibHeapNode<E> root, int treeDepth) {
        if (root != null) {
            treeDepth++;
            FibHeapNode<E> t = root;
            do {
                for (int i = 0; i < treeDepth; i++)
                    System.out.print(" ");
                System.out.print(t.getObject());
                if (t.getParent() != null)
                    System.out.print(":" + t.getParent().getObject());
                System.out.println();
                printTree(t.getChild(),treeDepth);
                t = t.getNext();
            } while (t != root);
            treeDepth--;
        }
    }
}
