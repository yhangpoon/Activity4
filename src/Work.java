import java.util.Calendar;
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
        sleepAmount = rndGen.nextLong() % 2500;

        Calendar calendar = Calendar.getInstance();

        // Add sleep so threads start running at different time
        if (sleepAmount < 0) {
            sleepAmount *= -1;
        }

        for (int k = 0; k < 2; k++) {
            found = false;
            int i = 0;
            while (!found) {

                if (map.containsKey("Key" + i)) {
                    calendar = Calendar.getInstance();
                    map.remove("Key" + i);
                    System.out.println(calendar.getTime().toString()
                            + " Thread " + threadNumber + " removes 'Key" + i
                            + "'");
                    found = true;

                    try {
                        calendar = Calendar.getInstance();
                        System.out
                                .println(calendar.getTime().toString()
                                        + " Thread " + threadNumber
                                        + " Starts Sleep");
                        Thread.sleep(sleepAmount);
                        calendar = Calendar.getInstance();
                        System.out.println(calendar.getTime().toString()
                                + " Thread " + threadNumber + " Ends Sleep");
                    } catch (InterruptedException e) {
                        System.err.print(e.toString());
                    }
                    calendar = Calendar.getInstance();
                    System.out.println(calendar.getTime().toString()
                            + " Thread " + threadNumber + " adds 'Key"
                            + (i + 52) + "'");
                    map.put("Key" + i, "New Value" + (i + 52));
                    calendar = Calendar.getInstance();
                    System.out.println(calendar.getTime().toString()
                            + " Thread " + threadNumber
                            + " sees map size as " + map.size());
                } else {
                    i++;
                }
            }
        }

        sleepAmount = rndGen.nextLong() % 2500;
        if (sleepAmount < 0) {
            sleepAmount *= -1;
        }

        try {
            calendar = Calendar.getInstance();
            System.out.println(calendar.getTime().toString() + " Thread "
                    + threadNumber + " Starts Sleep");
            Thread.sleep(sleepAmount);
            calendar = Calendar.getInstance();
            System.out.println(calendar.getTime().toString() + " Thread "
                    + threadNumber + " Ends Sleep");
        } catch (InterruptedException e) {
            System.err.print(e.toString());
        }

        for (int m = 0; m < 3; m++) {

            found = false;
            int i = 0;
            while (!found) {

                if (!map.containsKey("Key" + i)) {
                    calendar = Calendar.getInstance();
                    System.out.println(calendar.getTime().toString()
                            + " Thread " + threadNumber + " adds 'Key" + i
                            + "'");
                    map.put("Key" + i, "New Value" + i);
                    found = true;
                    calendar = Calendar.getInstance();
                    System.out.println(calendar.getTime().toString()
                            + " Thread " + threadNumber
                            + " sees map size as " + map.size());
                } else {
                    i++;
                }
            }
        }
        calendar = Calendar.getInstance();
        System.out.println(calendar.getTime().toString() + " Thread "
                + threadNumber + " Finished and Size is " + map.size());
    }
}
