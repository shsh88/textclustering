package fi.uef.cs;

import java.util.*;

import com.aliasi.cluster.Dendrogram;
import com.aliasi.util.Scored;

public class ShortTextSimilarity {
	private SimilarityMetric similarityMetric = new SimilarityMetric();
	
	class ShortTextComparison {
		public String str1, str2;
		public double similarity;
		ShortTextComparison(String str1, String str2, double similarity) {
			this.str1 = str1;
			this.str2 = str2;
			this.similarity = similarity;
		}
	}
	
	private double getMaxSimilarity(String word, List<String> wordArray,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		double maxSimilarity = 0;
		for (String w : wordArray) {
			maxSimilarity = Math.max(maxSimilarity, this.similarityMetric
					.getSimilarity(w, word, method, type, firstSenseOnly));
		}
		return maxSimilarity;
	}

	public double getSimilarity(List<String> wordArray1, List<String> wordArray2,
			SimilarityMetric.Method method, String type, boolean firstSenseOnly) {
		if (wordArray1.size() < wordArray2.size()) {
			List<String> temp = wordArray1;
			wordArray1 = wordArray2;
			wordArray2 = temp;
		}
		double result = 0;
		for (String word : wordArray1) {
			result += getMaxSimilarity(word, wordArray2, method, type,
					firstSenseOnly);
		}
		return result/wordArray1.size();
	}
	
	public Dendrogram<String> getDendrogramForString(ArrayList<String> data, SimilarityMetric.Method method, HashMap<TwoStrings, Double> similarityMap) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = similarityMap.get(new TwoStrings(data.get(i), data.get(j)));
			}
		}
		return HierachicalClustering.getDendrogram(data, method, similarityMatrix);
	}
	
	public HashMap<TwoStrings, Double> getSimilarityMap(ArrayList<String> data,
			SimilarityMetric.Method method) {
		HashMap<TwoStrings, Double> map = new HashMap<TwoStrings, Double>();
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				double value = this.similarityMetric.getSimilarity(data.get(i),
						data.get(j), method, "n", false);
				map.put(new TwoStrings(data.get(i), data.get(j)), value);
			}
		}
		return map;
	}
	
	public double[][] getSimilarityMatrix(ArrayList<List<String>> data, SimilarityMetric.Method method) {
		double[][] similarityMatrix = new double[data.size()][data.size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				similarityMatrix[i][j] = similarityMatrix[j][i] = getSimilarity(
						data.get(i), data.get(j), method, "n", false);
			}
		}
		return similarityMatrix;
	}
	
	public Dendrogram<List<String>> getDendrogramForStringList(ArrayList<List<String>> data, SimilarityMetric.Method method) {
		double[][] similarityMatrix = getSimilarityMatrix(data, method);
		return HierachicalClustering.getDendrogram(data, method, similarityMatrix);
	}
	
	
	
	static class PairScore<E> implements Scored {
        final Dendrogram<E> mDendrogram1;
        final Dendrogram<E> mDendrogram2;
        final double mScore;
        public PairScore(Dendrogram<E> dendrogram1, Dendrogram<E> dendrogram2,
                         double score) {
            mDendrogram1 = dendrogram1;
            mDendrogram2 = dendrogram2;
            mScore = score;
        }
        public double score() {
            return mScore;
        }
        @Override
        public String toString() {
            return "ps("
                + mDendrogram1
                + ","
                + mDendrogram2
                + ":"
                + mScore
                + ") ";
        }
    }
}
