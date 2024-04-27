import de.craftsblock.craftscore.cache.DoubleKeyedCache;

public class Test {

    public static void main(String[] args) {
        DoubleKeyedCache<String, String, Integer> doubleKeyedCache = new DoubleKeyedCache<>(10);
        for (int i = 0; i < 100; i++) {
            doubleKeyedCache.put("test" + i, "tester", i);
            System.out.println("----------------------------------------");
            System.out.println(doubleKeyedCache);
        }
    }

}
