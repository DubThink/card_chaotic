//package network;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.util.*;
//
//public class NetArrayList<T extends NetSerializable> extends NetSerializable implements List<T>, RandomAccess {
//    ArrayList<T> backing;
//
//    public NetArrayList() {
//        this.backing = new ArrayList<>();
//    }
//
//    public NetArrayList(DataInputStream dis) throws IOException {
//        super(dis);
//        deserialize(dis);
//    }
//
//    @Override
//    public int size() {
//        return backing.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return backing.isEmpty();
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        return backing.contains(o);
//    }
//
//    @Override
//    public Iterator<T> iterator() {
//        return backing.iterator();
//    }
//
//    @Override
//    public Object[] toArray() {
//        return backing.toArray();
//    }
//
//    @Override
//    public <T1> T1[] toArray(T1[] a) {
//        return backing.toArray(a);
//    }
//
//    @Override
//    public boolean add(T t) {
//        return backing.add(t);
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        return backing.remove(o);
//    }
//
//    @Override
//    public boolean containsAll(Collection<?> c) {
//        return backing.containsAll(c);
//    }
//
//    @Override
//    public boolean addAll(Collection<? extends T> c) {
//        return backing.addAll(c);
//    }
//
//    @Override
//    public boolean addAll(int index, Collection<? extends T> c) {
//        return backing.addAll(index,c);
//    }
//
//    @Override
//    public boolean removeAll(Collection<?> c) {
//        return backing.removeAll(c);
//    }
//
//    @Override
//    public boolean retainAll(Collection<?> c) {
//        return backing.retainAll(c);
//    }
//
//    @Override
//    public void clear() {
//        backing.clear();
//    }
//
//    @Override
//    public T get(int index) {
//        return backing.get(index);
//    }
//
//    @Override
//    public T set(int index, T element) {
//        return backing.set(index, element);
//    }
//
//    @Override
//    public void add(int index, T element) {
//        backing.add(index,element);
//    }
//
//    @Override
//    public T remove(int index) {
//        return backing.remove(index);
//    }
//
//    @Override
//    public int indexOf(Object o) {
//        return backing.indexOf(o);
//    }
//
//    @Override
//    public int lastIndexOf(Object o) {
//        return backing.lastIndexOf(o);
//    }
//
//    @Override
//    public ListIterator<T> listIterator() {
//        return backing.listIterator();
//    }
//
//    @Override
//    public ListIterator<T> listIterator(int index) {
//        return backing.listIterator(index);
//    }
//
//    @Override
//    public List<T> subList(int fromIndex, int toIndex) {
//        return backing.subList(fromIndex,toIndex);
//    }
//
//    @Override
//    public void serialize(DataOutputStream dos) throws IOException {
//        dos.writeInt(backing.size());
//        for(int i=0;i<backing.size();i++){
//            backing.get(i).serialize(dos);
//        }
//    }
//
//    @Override
//    protected void deserialize(DataInputStream dis) throws IOException {
//        int size = dis.readInt();
//        backing.clear();
//        backing.ensureCapacity(size);
//        for(int i=0;i<size;i++){
//            backing.add(new T(dis));
//        }
//    }
//}
