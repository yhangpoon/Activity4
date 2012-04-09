import java.util.* ;
import java.util.concurrent.* ;
import java.util.concurrent.locks.* ;

/*
 * A SynchronizedBucketHashMap implements a subset of the Map interface.
 * It would be relatively easy (but tedious) to add the
 * missing methods to bring this into conformance with the
 * Map interface.
 *
 * The idea is that a key/value Pair objects are placed in one
 * of N Buckets based on the hashcode of the key mod N. The
 * Buckets are contained in an array, and hashcode based selector
 * is the index into the array of the appropriate Bucket.
 *
 * In this version, all synchronization is done using built-in
 * Java synchronized blocks. This is inefficient, as it does not
 * support concurrent reading, and does not allow a consistent
 * compoutation of the size.
 */

public class SynchronizedBucketHashMap<K, V> {
    final int numberOfBuckets ;
    final List<Bucket<K, V>> buckets ;

    /*
     * Immutable Pairs of keys and values. Immutability means
     * we don't have to worry about the key or value changing
     * under our feet. However, when the mapping for a given key
     * changes, we need to create a new Pair object.
     *
     * This is a pure data holder class.
     */
    class Pair<K, V> {
        final K key ;
        final V value ;

        Pair(K key, V value) {
            this.key = key ;
            this.value = value ;
        }
    }

    /*
     * A Bucket holds all the key/value pairs in the map that have
     * the same hash code (modulo the number of buckets). The
     * object consists of an extensible "contents" list protected
     * with a ReadWriteLock "rwl".
     */
    class Bucket<K, V> {
        private final List<Pair<K, V>> contents =
                new ArrayList<Pair<K, V>>() ;

        /*
         * Return the current Bucket size.
         */
        int size() {
            return contents.size() ;
        }

        /*
         * Get the Pair at location 'i' in the Bucket.
         */
        Pair<K, V> getPair(int i) {
            return contents.get(i) ;
        }

        /*
         * Replace the Pair at location 'i' in the Bucket.
         */
        void putPair(int i, Pair<K, V> pair) {
            contents.set(i, pair) ;
        }

        /*
         * Add a Pair to the Bucket.
         */
        void addPair(Pair<K, V> pair) {
            contents.add(pair) ;
        }

        /*
         * Remove a Pair from the Bucket by position.
         */
        void removePair(int index) {
            contents.remove(index) ;
        }
    }

    /*
     * Constructor for the SynchronizedBucketHashMap proper.
     */
    public SynchronizedBucketHashMap(int nbuckets) {
        numberOfBuckets = nbuckets ;
        buckets = new ArrayList<Bucket<K, V>>(nbuckets) ;

        for ( int i = 0 ; i < nbuckets ; i++ ) {
            buckets.add(new Bucket<K, V>()) ;
        }
    }

    /*
     * Does the map contain an entry for the specified
     * key?
     */
    public boolean containsKey(K key) {
        Bucket<K, V> theBucket = buckets.get(bucketIndex(key)) ;
        boolean      contains ;

        synchronized( theBucket ) {
            contains = findPairByKey(key, theBucket) >= 0 ;
        }

        return contains ;
    }

    /*
     * How many pairs are in the map?
     */
    public int size() {
        int size = 0 ;

        for ( int i = 0 ; i < numberOfBuckets ; i++ ) {
            Bucket<K, V> theBucket =  buckets.get(i) ;
            synchronized( theBucket ) {
                size += theBucket.size() ;
            }
        }
        return size ;
    }

    /*
     * Return the value associated with the given Key.
     * Returns null if the key is unmapped.
     */
    public V get(K key) {
        Bucket<K, V> theBucket = buckets.get(bucketIndex(key)) ;
        Pair<K, V>   pair      = null ;

        synchronized(theBucket) {
            int index = findPairByKey(key, theBucket) ;

            if ( index >= 0 ) {
                pair = theBucket.getPair(index) ;
            }
        }

        return (pair == null) ? null : pair.value ;
    }

    /*
     * Associates the given value with the key in the
     * map, returning the previously associated value
     * (or none if the key was not previously mapped).
     */
    public V put(K key, V value) {
        Bucket<K, V> theBucket = buckets.get(bucketIndex(key)) ;
        Pair<K, V>   newPair   = new Pair<K, V>(key, value) ;
        V            oldValue ;

        synchronized(theBucket) {
            int index = findPairByKey(key, theBucket) ;

            if ( index >= 0 ) {
                Pair<K, V> pair = theBucket.getPair(index) ;

                theBucket.putPair(index, newPair) ;
                oldValue = pair.value ;
            } else {
                theBucket.addPair(newPair) ;
                oldValue = null ;
            }
        }
        return oldValue ;
    }

    /*
     * Remove the mapping for the given key from the map, returning
     * the currently mapped value (or null if the key is not in
     * the map.
     */
    public V remove(K key) {
        Bucket<K, V> theBucket = buckets.get(bucketIndex(key)) ;
        V removedValue = null ;

        synchronized(theBucket) {
            int index = findPairByKey(key, theBucket) ;

            if ( index >= 0 ) {
                Pair<K, V> pair = theBucket.getPair(index) ;

                theBucket.removePair(index) ;
                removedValue = pair.value ;
            }
        }
        return removedValue ;
    }

    /****** PRIVATE METHODS ******/

    /*
     * Given a key, return the index of the Bucket
     * where the key should reside.
     */
    private int bucketIndex(K key) {
        return key.hashCode() % numberOfBuckets ;
    }

    /*
     * Find a Pair<K, V> for the given key in the given Bucket,
     * returnning the pair's index in the Bucket (or -1 if
     * unfound).
     *
     * Assumes the lock for the Bucket has been acquired.
     */
    private int findPairByKey(K key, Bucket<K, V> theBucket) {
        int size = theBucket.size() ;

        for ( int i = 0 ; i < size ; ++i ) {
            Pair<K, V> pair = theBucket.getPair(i) ;

            if ( key.equals(pair.key) ) {
                return i ;
            }
        }

        return (-1) ;
    }
}
