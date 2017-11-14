
package SearchEngine.ScoringAlgorithms;

import SearchEngine.QueryProcessing.Vector;
import SearchEngine.QueryProcessing.Query;
import java.util.List;
import java.util.Map;

public class CosineScore {

    public void computeScores(Query query, Map<String, Double> wtQuery, List<Vector> vectors) {
        for (Vector vector: vectors) {
            for (Map.Entry<String, Double> pair : wtQuery.entrySet()) {
                double prod = pair.getValue()*vector.getTermWeight(pair.getKey());
                query.increaseDocScore(vector.getDocId(), prod);
            }
        }
    }

}