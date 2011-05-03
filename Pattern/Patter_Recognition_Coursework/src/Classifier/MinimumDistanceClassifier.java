package Classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MinimumDistanceClassifier implements Classifier {
	private int nLabel;
	private int nFeature;
	private HashMap<String, ArrayList<Double>> avg_fv;

	@Override
	public void trainClassifier(double[][] input, String[] output) {
		HashMap<String, ArrayList<Double>> total_fv = new HashMap<String, ArrayList<Double>>();
		HashMap<String, Integer> total_count = new HashMap<String, Integer>();
		nFeature = input[0].length;
		for (int i = 0; i < output.length; i++) {
			// Initialize if not existing
			if (!total_count.containsKey(output[i])) {

				total_fv.put(output[i], new ArrayList<Double>(nFeature));
				for (int j = 0; j < nFeature; j++) {
					total_fv.get(output[i]).add(0.0);
				}
				total_count.put(output[i], 0);
			}
			// Update values
			total_count.put(output[i], total_count.get(output[i]) + 1);
			for (int j = 0; j < nFeature; j++) {
				System.out.println(total_fv.get(output[i]));
				total_fv.get(output[i]).set(j,
						total_fv.get(output[i]).get(j) + input[i][j]);
			}
		}
		avg_fv = total_fv;
		nLabel = avg_fv.size();
		// Divide by count to get average
		for (Map.Entry<String, ArrayList<Double>> cur : avg_fv.entrySet()) {
			for (int i = 0; i < cur.getValue().size(); i++) {
				cur.getValue().set(i,
						cur.getValue().get(i) / total_count.get(cur.getKey()));
			}
		}
	}

	@Override
	public void writeClassifier(String filename) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new File(filename));
		// Write number of labels and length of each fv
		writer.println(nLabel + " " + nFeature);
		for (Map.Entry<String, ArrayList<Double>> cur : avg_fv.entrySet()) {
			writer.print(cur.getKey());
			for (int i = 0; i < cur.getValue().size(); i++) {
				writer.print(" " + cur.getValue().get(i));
			}
			writer.println();
		}
		writer.close();
	}

	@Override
	public void readClassifier(String filename) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));
		// Write number of labels and length of each fv
		nLabel = scan.nextInt();
		nFeature = scan.nextInt();
		avg_fv = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < nLabel; i++) {
			// Label label = null;
			// Class c = label.getClass();
			// try {
			// for (Constructor con : c.getConstructors()) {
			// if (con.getTypeParameters().length == 1
			// && con.getTypeParameters()[0].getName() == "java.lang.String") {
			// label = (Label) con.newInstance(new Object[] { scan
			// .next() });
			// }
			// }
			// } catch (Exception e) {
			// }
			// System.out.println(label.getClass());
			String label = scan.next();
			ArrayList<Double> fv = new ArrayList<Double>(nFeature);
			for (int j = 0; j < nFeature; j++) {
				fv.add(scan.nextDouble());
			}
			avg_fv.put(label, fv);
		}
		scan.close();
	}

	@Override
	public String classify(double[] input) {
		double minD = 1e9;
		String bestLabel = null;
		for (Map.Entry<String, ArrayList<Double>> cur : avg_fv.entrySet()) {
			double error = 0;
			for (int j = 0; j < nFeature; j++) {
				error += Math.pow(cur.getValue().get(j) - input[j], 2);
			}
			System.err.println("Error " + cur.getKey() + " = " + error);
			if (error < minD) {
				minD = error;
				bestLabel = cur.getKey();
			}
		}
		return bestLabel;
	}

}
