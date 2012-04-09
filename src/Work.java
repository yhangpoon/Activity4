import java.util.Random;

/**
 * 
 */

/**
 * @author Yin
 * 
 */
public class Work implements Runnable {

    private int threadNumber;

    private ConcurrentBucketHashMap<String, String> map;

    public Work(ConcurrentBucketHashMap<String, String> map, int number) {
        this.map = map;
    }

    public void run() {
        long sleepAmount;
        Random rndGen = new Random(100);
        sleepAmount = rndGen.nextLong();

        map.remove("Key 1");
        map.remove("Key 2");

        System.out.print(map.size());

        try {
            Thread.sleep(sleepAmount);
        } catch (InterruptedException e) {
            System.err.print(e.toString());
        }

        map.put("Key 1", "New Value 1");
        map.put("Key 2", "New Value 2");
        map.put("Key 3", "New Value 3");

        System.out.print("Thread " + threadNumber + " sees map size as "
                + map.size());
    }
}
