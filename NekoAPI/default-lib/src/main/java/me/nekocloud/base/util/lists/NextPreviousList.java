package me.nekocloud.base.util.lists;

import java.io.Serial;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NextPreviousList<E> extends ArrayList<E> {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Object lock = new Object();
    private int cursor;

    @Override
    public boolean add(E e) {
        synchronized (lock) {
            return super.add(e);
        }
    }

    @Override
    public void add(int index, E element) {
        synchronized (lock) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        synchronized (lock) {
            return super.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        synchronized (lock) {
            return super.addAll(index, c);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            super.clear();
        }
    }

    @Override
    public Object clone() {
        synchronized (lock) {
            NextPreviousList<?> clist = (NextPreviousList<?>)super.clone();
            try {
                clist.modCount = 0;
                Field f = ArrayList.class.getDeclaredField("elementData");
                f.setAccessible(true);
                f.set(clist, Arrays.copyOf((Object[])f.get(this), this.size()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return clist;
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (lock) {
            return super.contains(o);
        }
    }

    @Override
    public void ensureCapacity(int minCapacity) {
        synchronized (lock) {
            super.ensureCapacity(minCapacity);
        }
    }

    @Override
    public E get(int index) {
        synchronized (lock) {
            return super.get(index);
        }
    }

    public E getNext() {
        synchronized (lock) {
            Object element = super.get(this.cursor);
            ++this.cursor;
            if (this.cursor == super.size()) {
                this.cursor = 0;
            }
            return (E) element;
        }
    }

    public E getPrevious() {
        synchronized (lock) {
            Object element = super.get(this.cursor);
            --this.cursor;
            if (this.cursor == 0) {
                this.cursor = super.size();
            }
            return (E) element;
        }
    }

    @Override
    public int indexOf(Object o) {
        synchronized (lock) {
            return super.indexOf(o);
        }
    }
    @Override
    public int lastIndexOf(Object o) {
        synchronized (lock) {
            return super.lastIndexOf(o);
        }
    }

    @Override
    public E remove(int index) {
        synchronized (this.lock) {
            Object element = super.remove(index);
            if (this.cursor >= super.size() - 1) {
                this.cursor = 0;
            }
            return (E) element;
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (lock) {
            boolean remove = super.remove(o);
            if (this.cursor >= super.size() - 1) {
                this.cursor = 0;
            }
            return remove;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        synchronized (lock) {
            this.cursor = 0;
            return super.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        synchronized (lock) {
            return super.retainAll(c);
        }
    }

    @Override
    public E set(int index, E element) {
        synchronized (lock) {
            return super.set(index, element);
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        synchronized (lock) {
            return super.subList(fromIndex, toIndex);
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (lock) {
            return super.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        synchronized (lock) {
            return super.toArray(a);
        }
    }

    @Override
    public void trimToSize() {
        synchronized (lock) {
            super.trimToSize();
        }
    }
}

