package Assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Assignment0.Moments;
import Helper.ImageHelper;
import Helper.MyMatrix;
import Jama.LUDecomposition;
import Jama.Matrix;

/*
 * Same as last time but difference is:
 * - 2 track + 4 sectors
 * - Min Sum Square Error Classifier
 */
public class EnhancedChainCode {
	// 0,1 image
	public int[][] img;
	// 1 for contour (used for debugging)
	public int[][] contour;
	// Number of tracks and sectors
	int nTrack;
	int nSector;

	public EnhancedChainCode(int nT, int nS) {
		nTrack = nT;
		nSector = nS;
	}

	// E SE S SW W NW N NE
	int[] di = { 0, 1, 1, 1, 0, -1, -1, -1 };
	int[] dj = { 1, 1, 0, -1, -1, -1, 0, 1 };

	// Center Of Mass
	double x_bar;
	double y_bar;

	// Max radius from center of mass
	double radius;

	// Valid point in image
	private boolean valid(int i, int j) {
		return (i >= 0 && i < img.length && j >= 0 && j < img[0].length);
	}

	// Get the track number of a point
	public int getTrack(int i, int j) {
		double r = Math.hypot(i - y_bar, j - x_bar) / (radius);
		r = Math.max(0.0, Math.min(1.0 - 1e-9, r));
		return (int) (r * nTrack);
	}

	// Get the sector number of a point
	public int getSector(int i, int j) {
		double theta = (Math.atan2(i - y_bar, j - x_bar) + Math.PI)
				/ (2 * Math.PI);
		theta = Math.max(0.0, Math.min(1.0 - 1e-9, theta));
		return (int) (theta * nSector);
	}

	// Calculates the Feature vector
	public int[][][] getFVs() {
		int[][][] res = new int[nTrack][nSector][8];
		contour = new int[img.length][img[0].length];

		// Get center of image
		x_bar = Moments.Get_x_bar(img);
		y_bar = Moments.Get_y_bar(img);

		// Get farthest point from center
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[i].length; j++) {
				if (img[i][j] == 1)
					radius = Math.max(radius, Math.hypot(j - x_bar, i - y_bar));
			}
		}

		// Find the first pixel on the contour
		int si = 0, sj = 0;
		boolean found = false;
		for (int i = 0; i < img.length; ++i) {
			for (int j = 0; j < img[i].length; j++) {
				if (img[i][j] == 1) {
					si = i;
					sj = j;
					found = true;
					break;
				}
			}
			if (found)
				break;
		}
		if (!found) // Empty picture
			return null;

		int curi = si, curj = sj;

		// search around the first pixel to get direction
		int dir = -1;
		for (int k = 0; k < 8; ++k) {
			int ni = si + di[k];
			int nj = sj + dj[k];
			// System.out.println("N " + ni + " " + nj);
			if (valid(ni, nj) && img[ni][nj] == 1) {
				curi = ni;
				curj = nj;
				dir = k;
				break;
			}
		}

		if (dir == -1)
			return null; // one pixel

		// Iterate over contour
		while (curi != si || curj != sj) {
			contour[curi][curj] = 2;
			// From previous to current = dir
			// From current to previous = (dir+4)%8
			int ndir = (dir + 4) % 8;
			int nxti = curi, nxtj = curj;
			for (int k = 1; k < 9; ++k) {
				nxti = curi + di[(ndir + k) % 8];
				nxtj = curj + dj[(ndir + k) % 8];
				if (valid(nxti, nxtj) && img[nxti][nxtj] == 1) {
					ndir = (ndir + k) % 8;
					break;
				}
			}
			// Find out track and sector, assume we will use curi & curj
			// (previous)
			int track = getTrack(curi, curj);
			int sector = getSector(curi, curj);
			dir = ndir;
			curi = nxti;
			curj = nxtj;
			res[track][sector][dir]++;
		}
		return res;
	}

	// Normalizes the fv (divides by total sum)
	public static double[] normalize(int[][][] fv) {
		double sum = 0;
		int sz = 0;
		for (int i = 0; i < fv.length; i++) {
			for (int j = 0; j < fv[i].length; j++) {
				for (int k = 0; k < fv[i][j].length; k++) {
					sum += fv[i][j][k];
					sz++;
				}
			}
		}
		double[] res = new double[sz];
		int cnt = 0;
		for (int i = 0; i < fv.length; i++) {
			for (int j = 0; j < fv[i].length; j++) {
				for (int k = 0; k < fv[i][j].length; k++) {
					res[cnt++] += fv[i][j][k] / sum;
				}
			}
		}
		return res;
	}

	public static void main(String[] args) throws FileNotFoundException {

		// Training Part
//		Training();

		// Testing Part
		 Testing();

	}

	public static void Testing() throws FileNotFoundException {
		EnhancedChainCode cc = new EnhancedChainCode(2, 4);
		cc.img = ImageHelper.getBWImage("Files/Assignment2/unknown4.bmp");
		double[] fv = EnhancedChainCode.normalize(cc.getFVs());

		Scanner sc = new Scanner(new File("Files/Assignment2/train.txt"));
		int nCol = sc.nextInt();
		ArrayList<Integer> cols = new ArrayList<Integer>();
		for (int i = 0; i < nCol; i++) {
			cols.add(sc.nextInt());
		}
		double[] nfv = new double[nCol];
		int ind = 0;
		for (int i = 0; i < fv.length; i++) {
			if (cols.contains(i))
				nfv[ind++] = fv[i];
		}
		double best = -1e9;
		int besti = -1;
		for (int num = 0; num < 10; ++num) {
			int label = sc.nextInt();
			double val = 0;
			for (int i = 0; i < nfv.length; i++) {
				double wi = sc.nextDouble();
				val += wi * nfv[i];
			}
			val += sc.nextDouble();
			if( val > best ) {
				best = val;
				besti = num;
			}
//			if (val > 0)
//				System.out.println("Label: " + label + " SAME");
//			else
//				System.out.println("Label: " + label + " NOT SAME");
		}
		System.out.println("BEST " + besti);
		sc.close();
	}

	public static void Training() throws FileNotFoundException {
		EnhancedChainCode cc = new EnhancedChainCode(2, 4);
		// Output file
		PrintWriter pw = new PrintWriter(
				new File("Files/Assignment2/train.txt"));
		double[][] Z = new double[10 * 3][64];
		for (int num = 0; num < 10; ++num) {
			for (int ind = 1; ind <= 3; ind++) {
				cc.img = ImageHelper
						.getBWImage("Files/Assignment2/training data/" + num
								+ ind + ".bmp");
				double[] fv = EnhancedChainCode.normalize(cc.getFVs());
				for (int i = 0; i < fv.length; i++) {
					Z[num * 3 + (ind - 1)][i] = fv[i];
				}
				System.out.println("Successfuly calculated fv of " + num + "/"
						+ ind);
			}
		}

		// Remove Invalid Columns (zero)
		ArrayList<Integer> ValidCols = new ArrayList<Integer>();

		for (int j = 0; j < Z[0].length; j++) {
			boolean allZ = true;
			for (int i = 0; i < Z.length; i++) {
				if (Z[i][j] != 0)
					allZ = false;
			}
			if (!allZ) {
				ValidCols.add(j);
			}
		}

		// Good_z is Z but without the 0 columns
		double[][] good_z = new double[10 * 3][ValidCols.size()];
		for (int i = 0; i < Z.length; i++) {
			int cj = 0;
			for (int j = 0; j < Z[i].length; j++) {
				if (ValidCols.contains(j))
					good_z[i][cj++] = Z[i][j];
			}
		}

		// Write the indices of columns in the file
		pw.print(ValidCols.size() + " ");
		for (int i = 0; i < ValidCols.size(); i++) {
			pw.print(ValidCols.get(i) + " ");
		}
		pw.println();

		for (int num = 0; num < 10; ++num) {
			// sample in lecture
			// double [][] cur_z =
			// {{1,1,1},{2,2,1},{3,4,1},{4,3,1},{5,5,1},{1,-1,-1},{3,3,-1},{5,4,-1},{-2,2,-1}};
			// double [][] b = {{1},{1},{1},{1},{1},{1},{1},{1},{1}};

			// cur_z is the Z but for class num
			double[][] cur_z = new double[good_z.length][good_z[0].length + 1];
			double[][] b = new double[good_z.length][1];
			for (int i = 0; i < cur_z.length; i++) {
				b[i][0] = 1;
				for (int j = 0; j < cur_z[i].length; j++) {
					if (j == cur_z[i].length - 1) {
						cur_z[i][j] = (i / 3 == num ? +1 : -1); // bias term
					} else {
						cur_z[i][j] = good_z[i][j] * (i / 3 == num ? +1 : -1);
					}
				}
			}
			System.out.println("MATRIX OF " + num);
			Matrix m_b = new Matrix(b);
			Matrix m_z = new Matrix(cur_z);
			Matrix m_zt = m_z.transpose();
			Matrix m_z2 = m_zt.times(m_z);
			Matrix m_res = (m_z2.inverse().times(m_zt)).times(m_b);
			// Write label to file
			pw.print(num + " ");
			// Write the weights
			for (int i = 0; i < cur_z[0].length; i++) {
				pw.print(m_res.get(i, 0) + " ");
			}
			pw.println();
		}
		pw.close();
	}
}
