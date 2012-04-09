public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        ConcurrentBucketHashMap<String, String> map = new ConcurrentBucketHashMap<String, String>(
                3);

        for (int i = 0; i < 50; i++) {
            map.put("Key " + 1, "Value " + 1);
        }

        Work work1 = new Work(map, 1);
        Work work2 = new Work(map, 2);
        Work work3 = new Work(map, 3);

        work1.run();
        work2.run();
        work3.run();
    }
}
