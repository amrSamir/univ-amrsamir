package Assignment3;

import java.util.Arrays;

import Helper.ImageHelper;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

class Pair implements Comparable<Pair> {
	double first;
	int index;

	public Pair(double first, int index) {
		super();
		this.first = first;
		this.index = index;
	}

	@Override
	// Reversed
	public int compareTo(Pair o) {
		if (first > o.first)
			return -1;
		if (first < o.first)
			return 1;
		return 0;
	}

}

public class PCA {
	
	public static int W;
	public static int H;
	public static void main(String[] args) {
		double[][] x = GetVectors();
		int nV = x.length;
		int nF = x[0].length;

		// Get average
		double[] x_bar = new double[nF];
		double[][] x_center = new double[nV][nF];
		for (int j = 0; j < nF; j++) {
			for (int i = 0; i < nV; i++) {
				x_bar[j] += x[i][j];
			}
			x_bar[j] /= nV;
			// System.out.println(x_bar[j]);
			for (int i = 0; i < nV; i++) {
				x_center[i][j] = x[i][j] - x_bar[j];
			}
		}
		System.out.println("X-BAR DONE");
		double[][] covx = new double[nF][nF];
		for (int i = 0; i < nF; i++) {
			for (int j = 0; j < nF; j++) {
				covx[i][j] = 0;
				for (int k = 0; k < nV; k++) {
					covx[i][j] += x_center[k][i] * x_center[k][j];
				}
				covx[i][j] /= nV;
				// System.out.print(covx[i][j] + " ");
			}
			// System.out.println();
		}
		System.out.println("COV DONE");
		Matrix m = new Matrix(covx);
		EigenvalueDecomposition e = m.eig();
		System.out.println("EIG CALC DONE");
		double[][] T = e.getV().getArray();
		double[] eigen_values = e.getRealEigenvalues();
		Pair[] eigen_values_sorted = new Pair[eigen_values.length];
		for (int i = 0; i < eigen_values_sorted.length; i++) {
			eigen_values_sorted[i] = new Pair(eigen_values[i], i);
		}
		Arrays.sort(eigen_values_sorted);
//		for (int i = 0; i < eigen_values.length; i++) {
//			System.out.println(eigen_values_sorted[i].first);
//		}
		double[][] T_sorted = new double[nF][nF];
		for (int j = 0; j < nF; j++) {
			for (int i = 0; i < nF; i++) {
				T_sorted[i][j] = T[i][eigen_values_sorted[j].index];
			}
		}
		System.out.println("EIG SORT DONE");
		Matrix T_m = new Matrix(T_sorted);
//		T_m.print(10, 2);
		Matrix Tt = T_m.transpose();
		System.out.println("INVERSE DONE");
		// double [][] y = new double[nV][nF];
//		System.out
//				.println(Tt.getColumnDimension() + " " + Tt.getRowDimension());
		// System.out.println(x_center.length + " " + x_center[0].length);
		Matrix y = Tt.times((new Matrix(x_center)).transpose());
		System.out.println("Y DONE");
		Matrix inv_Tt = Tt.inverse();
		// System.out.println((8.0 / Math.sqrt(2)) + " " + (4.0 / Math.sqrt(2))
		// + " " + (-4.0 / Math.sqrt(2)) + " " + (-8.0 / Math.sqrt(2)));
		for(int cnt = 0; cnt <= 64 ; ++cnt ) {
			double [][] y_val = y.getArray();
			int CNT_DEL =cnt;
			for (int i = y_val.length-CNT_DEL; i < y_val.length; i++) {
				for (int j = 0; j < y_val[i].length; j++) {
					y_val[i][j] = 0;
				}
			}
			
			
			Matrix new_x = inv_Tt.times(new Matrix(y_val));
			double [][] res_x = new_x.transpose().getArray();
			System.out.println(res_x.length + " " + res_x[0].length);
			for (int j = 0; j < nF; j++) {
				for (int i = 0; i < nV; i++) {
					res_x[i][j] += x_bar[j];
				}
			}
			ReconstructVectors(res_x,W,H,"Files/Assignment3/test2/"+cnt+".jpg");
		}
	}
	
	

	private static void ReconstructVectors(double[][] res_x, int w, int h, String output) {
		int [][] img = new int[h][w];
		for (int ci = 0; ci < h; ci+=8) {
			for (int cj = 0; cj < w; cj+=8) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						img[ci+i][cj+j] = (int)Math.ceil(res_x[(ci/8)*(w/8)+(cj/8)][i*8+j]);
					}
				}
			}
		}
		ImageHelper.setImage(output, img);
	}



	private static double[][] GetVectors() {
		// ImageHelper.test("Files/Assignment3/test.bmp",
		// "Files/Assignment3/test2.bmp");
		int[][] img_orig = ImageHelper
				.getImage("Files/Assignment3/original.bmp");
		int height = H = img_orig.length;
		int width = W = img_orig[0].length;

		if (height % 8 != 0 && width % 8 != 0) {
			int[][] img_trimmed = new int[height - (height % 8)][width
					- (width % 8)];
			for (int i = 0; i < img_trimmed.length; i++) {
				for (int j = 0; j < img_trimmed[i].length; j++) {
					img_trimmed[i][j] = img_orig[i][j];
				}
			}
			img_orig = img_trimmed;
			H = height = img_trimmed.length;
			W = width = img_trimmed[0].length;
//			ImageHelper.setImage("Files/Assignment3/trimmed.jpg", img_trimmed);
		}
		double [][] allV = new double[(height/8)*(width/8)][64];
		for (int ci = 0; ci < height; ci+=8) {
			for (int cj = 0; cj < width; cj+=8) {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						allV[(ci/8)*(width/8)+(cj/8)][i*8+j] = img_orig[ci+i][cj+j];
					}
				}
			}
		}
		return allV;
		// double[][] res = { { 4, 4 }, { 2, 2 }, { -2, -2 }, { -4, -4 } };
		// return res;
	}
}
