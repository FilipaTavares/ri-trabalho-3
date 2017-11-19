import SearchEngine.Evaluation.QueryMeasure;
import javafx.util.Pair;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class example {
    public static void main(String[] args) {
        int n = 5;
        int freq = 3;

        double idf = Math.log10((5 / 3));
        System.out.println("----------" + idf);
        double idf2 = (double) Math.log10((5.0 / 3));
        System.out.println("----------" + idf2);


        double d = 0.06948650704907821;
        double scale = Math.pow(10, 5);
        d = Math.round(d * scale) / scale;
        System.out.println(d);

        Map<Double, Double> mapa = new LinkedHashMap<>();
        mapa.put(0.33, 0.5);
        mapa.put(0.67, 0.4);
        mapa.put(1.0, 0.43);

        List<Double> levels = new ArrayList<>();
        levels.add(0.0);
        levels.add(0.1);
        levels.add(0.2);
        levels.add(0.3);
        levels.add(0.4);
        levels.add(0.5);
        levels.add(0.6);
        levels.add(0.7);
        levels.add(0.8);
        levels.add(0.9);
        levels.add(1.0);


        List<Double> points = new ArrayList<>();
        Set<Double> keys = mapa.keySet();

        for (double level: levels) {
            double max_precision = 0.0;

            for (double rl: keys) {
                System.out.println(rl + " " + level);
                System.out.println(rl>=level);
                if (rl >= level && mapa.get(rl) > max_precision) {
                        max_precision = mapa.get(rl);
                    }
                }
                points.add(max_precision);
            }

        System.out.println(points);
    }
}

