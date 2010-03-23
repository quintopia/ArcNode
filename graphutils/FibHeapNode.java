/*
 * Created on Oct 31, 2006
 *
 * TODO 
 */
package graphutils;

public class FibHeapNode<E> {
        private boolean mark;
        private int degree;
        private E o;
        private double key;
        private FibHeapNode<E> child = null;
        private FibHeapNode<E> prev;
        private FibHeapNode<E> next;
        private FibHeapNode<E> parent = null;
        /**
         * @param left
         * @param right
         * @param prev
         * @param next
         */
        public FibHeapNode(E o, double key, FibHeapNode<E> prev, FibHeapNode<E> next) {
            super();
            this.prev = prev;
            this.next = next;
            this.o = o;
            this.key = key;
            degree = 0;
        }
        /**
         * @return Returns the degree.
         */
        public int getDegree() {
            return degree;
        }
        /**
         * @return Returns the mark.
         */
        public boolean isMarked() {
            return mark;
        }
        /**
         * @param mark The mark to set.
         */
        public void setMark(boolean mark) {
            this.mark = mark;
        }
        /**
         * @return Returns the left.
         */
        public FibHeapNode<E> getChild() {
            return child;
        }
        /**
         * @param left The left to set.
         */
        public void setChild(FibHeapNode<E> child) {
            this.child = child;
        }
        /**
         * @return Returns the next.
         */
        public FibHeapNode<E> getNext() {
            return next;
        }
        /**
         * @param next The next to set.
         */
        public void setNext(FibHeapNode<E> next) {
            this.next = next;
        }
        /**
         * @return Returns the prev.
         */
        public FibHeapNode<E> getPrev() {
            return prev;
        }
        /**
         * @param prev The prev to set.
         */
        public void setPrev(FibHeapNode<E> prev) {
            this.prev = prev;
        }
        /**
         * @return Returns the key.
         */
        public double getKey() {
            return key;
        }
        /**
         * @param key The key to set.
         */
        public void setKey(double key) {
            this.key = key;
        }
        /**
         * @return Returns the id.
         */
        public E getObject() {
            return o;
        }
        /**
         * @return Returns the parent.
         */
        public FibHeapNode<E> getParent() {
            return parent;
        }
        /**
         * @param parent The parent to set.
         */
        public void setParent(FibHeapNode<E> parent) {
            this.parent = parent;
        }
        /**
         * @param degree The degree to set.
         */
        public void setDegree(int degree) {
            this.degree = degree;
        }
        
        /**
         * @return whether this node has a child
         */
        public boolean hasChild() {
            return child!=null;
        }
        
        public void remove() {
            prev.next=next;
            next.prev=prev;
        }
        
        public void insertBefore(FibHeapNode<E> z) {
            z.prev.next=this;
            prev=z.prev;
            z.prev=this;
            next=z;
        }
}
