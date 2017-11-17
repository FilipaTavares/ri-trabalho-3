import SearchEngine.Evaluation.QueryMeasure;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class example {
    public static void main(String[] args) {
        double d = 0.06948650704907821;
        double scale = Math.pow(10, 5);
        d = Math.round(d * scale) / scale;
        System.out.println(d);

        List<Long> queryLatency = new ArrayList<>();
        queryLatency.add((long) 2);
        queryLatency.add((long) 2);
        queryLatency.add((long) 3);
        queryLatency.add((long) 7);
        queryLatency.add((long) 8);
        queryLatency.add((long) 8);
        queryLatency.add((long) 9);

        queryLatency.forEach(System.out::print);
        System.out.println();

        if (queryLatency.size() % 2 == 0) {
            long l = queryLatency.get((queryLatency.size() / 2) - 1) + queryLatency.get(queryLatency.size() / 2);
            System.out.println(l / 2.0);

        } else {
            System.out.println(Math.floor(queryLatency.size() / 2.0));

            System.out.println(queryLatency.get((int) Math.floor(queryLatency.size() / 2.0)));
        }
    }
}
