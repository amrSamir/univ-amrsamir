package Classifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import Jama.Matrix;

public class MinimumSumSquareErrorClassifier implements Classifier {
	private int nLabel;
	private int nFeature;
	private HashSet<String> Labels;
	private ArrayList<Integer> ValidCols;
	private HashMap<String, ArrayList<Double>> weights;

	@Override
	public void trainClassifier(double[][] input, String[] output) {
		nFeature = input[0].length;
		Labels = new HashSet<String>();
		for (int i = 0; i < output.length; i++) {
			Labels.add(output[i]);
		}
		nLabel = Labels.size();

		// Remove Invalid Columns (zero)
		ValidCols = new ArrayList<Integer>();

		for (int j = 0; j < nFeature; j++) {
			boolean allZero = true;
			for (int i = 0; i < input.length; i++) {
				if (input[i][j] != 0)
					allZero = false;
			}
			if (!allZero) {
				ValidCols.add(j);
			}
		}

		// v_input id input but without the 0 columns
		double[][] v_input = new double[input.length][ValidCols.size()];
		for (int i = 0; i < input.length; i++) {
			int cj = 0;
			for (int j = 0; j < nFeature; j++) {
				if (ValidCols.contains(j))
					v_input[i][cj++] = input[i][j];
			}
		}

		weights = new HashMap<String, ArrayList<Double>>();

		for (String label : Labels) {

			// sample in lecture
			// double [][] cur_z =
			// {{1,1,1},{2,2,1},{3,4,1},{4,3,1},{5,5,1},{1,-1,-1},{3,3,-1},{5,4,-1},{-2,2,-1}};
			// double [][] b = {{1},{1},{1},{1},{1},{1},{1},{1},{1}};

			double[][] cur = new double[v_input.length][v_input[0].length + 1];
			double[][] b = new double[v_input.length][1];
			for (int i = 0; i < cur.length; i++) {
				b[i][0] = 1;
				for (int j = 0; j < cur[i].length; j++) {
					if (j == cur[i].length - 1)
						cur[i][j] = 1; // bias term
					else
						cur[i][j] = v_input[i][j];
					if (!output[i].equals(label))
						cur[i][j] *= -1;
				}
			}
			Matrix m_b = new Matrix(b);
			Matrix m_z = new Matrix(cur);
			Matrix m_zt = m_z.transpose();
			Matrix m_z2 = m_zt.times(m_z);
			Matrix m_res = (m_z2.inverse().times(m_zt)).times(m_b);
			ArrayList<Double> w = new ArrayList<Double>();
			for (int i = 0; i < cur[0].length; i++) {
				w.add(m_res.get(i, 0));
			}
			weights.put(label, w);
		}

	}

	@Override
	public void writeClassifier(String filename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(filename));
		pw.println(nLabel + " " + nFeature);
		// Write the indices of columns in the file
		pw.print(ValidCols.size() + " ");
		for (int i = 0; i < ValidCols.size(); i++) {
			pw.print(ValidCols.get(i) + " ");
		}
		pw.println();

		for (String label : Labels) {

			// Write label to file
			pw.print(label + " ");
			// Write the weights
			for (int i = 0; i < weights.get(label).size(); i++) {
				pw.print(weights.get(label).get(i) + " ");
			}
			pw.println();
		}
		pw.close();

	}

	@Override
	public void readClassifier(String filename) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filename));
		nLabel = sc.nextInt();
		nFeature = sc.nextInt();
		
		int nCol = sc.nextInt();
		ValidCols = new ArrayList<Integer>();
		for (int i = 0; i < nCol; i++) {
			ValidCols.add(sc.nextInt());
		}
		weights = new HashMap<String, ArrayList<Double>>();
		for (int i = 0; i < nLabel; i++) {
			String label = sc.next();
			ArrayList<Double> cur = new ArrayList<Double>();
			for (int j = 0; j < ValidCols.size(); j++) {
				cur.add(sc.nextDouble());
			}
			// Bias term
			cur.add(sc.nextDouble());
			weights.put(label, cur);
		}
		sc.close();
	}

	@Override
	public String classify(double[] input) {
		double[] nfv = new double[ValidCols.size()];
		int ind = 0;
		for (int i = 0; i < input.length; i++) {
			if (ValidCols.contains(i))
				nfv[ind++] = input[i];
		}
		double bestV = -1e9;
		String bestLabel = null;
		for (Map.Entry<String, ArrayList<Double>> cur : weights.entrySet()) {
			double val = 0;
			for (int j = 0; j < cur.getValue().size(); j++) {
				if( j < nfv.length )
					val += nfv[j]*cur.getValue().get(j);
				else  
					val += cur.getValue().get(j);  // bias term
			}
			System.err.println("Error " + cur.getKey() + " = " + val);
			if (val > bestV) {
				bestV = val;
				bestLabel = cur.getKey();
			}
		}
		return bestLabel;
	}

}
