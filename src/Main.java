public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        ConcurrentBucketHashMap<String, String> map = new ConcurrentBucketHashMap<String, String>(
                3);

        for (int i = 0; i < 50; i++) {
            map.put("Key" + i, "Value" + i);
        }

        System.out.println("Program Started");
        System.out.println("Starting map size = " + map.size());
        System.out.println("____________________________________");

        Work work1 = new Work(map, 1);
        Work work2 = new Work(map, 2);
        Work work3 = new Work(map, 3);

        System.out.println("Thread 1 Begins");
        work1.start();
        System.out.println("Thread 2 Begins");
        work2.start();
        System.out.println("Thread 3 Begins");
        work3.start();
    }
}
