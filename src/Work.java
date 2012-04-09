import java.util.Random;

/**
 * @author Yin
 * 
 */
public class Work extends Thread {

    private int threadNumber;

    private ConcurrentBucketHashMap<String, String> map;

    public Work(ConcurrentBucketHashMap<String, String> map, int number) {
        this.map = map;
        this.threadNumber = number;
    }

    public void run() {
        boolean found = false;
        long sleepAmount;
        Random rndGen = new Random(100);
        sleepAmount = rndGen.nextLong() % 10;
        if (sleepAmount < 0) {
            sleepAmount *= -1;
        }

        for (int k = 0; k < 2; k++) {
            found = false;
            int i = 0;
            while (!found) {
                try {
                    System.out.println("Thread " + threadNumber
                            + " Starts Sleep");
                    Thread.sleep(sleepAmount);
                    System.out.println("Thread " + threadNumber
                            + " Ends Sleep");
                } catch (InterruptedException e) {
                    System.err.print(e.toString());
                }
                if (map.containsKey("Key" + i)) {
                    System.out.println("Thread " + threadNumber
                            + " removes 'Key" + i + "'");
                    map.remove("Key" + i);
                    System.out.println("Thread " + threadNumber
                            + " removes 'Key" + i + "'");
                    found = true;
                    System.out.println("Thread " + threadNumber
                            + " sees map size as " + map.size());
                } else {
                    i++;
                }
            }
        }

        sleepAmount = rndGen.nextLong() % 10;
        if (sleepAmount < 0) {
            sleepAmount *= -1;
        }

        try {
            System.out.println("Thread " + threadNumber + " Starts Sleep");
            Thread.sleep(sleepAmount);
            System.out.println("Thread " + threadNumber + " Ends Sleep");
        } catch (InterruptedException e) {
            System.err.print(e.toString());
        }

        for (int m = 0; m < 3; m++) {

            found = false;
            int i = 0;
            while (!found) {

                if (!map.containsKey("Key" + i)) {
                    System.out.println("Thread " + threadNumber
                            + " adds 'Key" + i + "'");
                    map.put("Key" + i, "New Value" + i);
                    found = true;
                    System.out.println("Thread " + threadNumber
                            + " sees map size as " + map.size());
                } else {
                    i++;
                }
            }
        }

        System.out.println("Thread " + threadNumber
                + " Finished and Size is " + map.size());
    }
}
