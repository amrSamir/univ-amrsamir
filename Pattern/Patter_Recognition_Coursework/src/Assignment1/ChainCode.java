package Assignment1;

import java.io.FileNotFoundException;

import Classifier.Classifier;
import Classifier.MinimumDistanceClassifier;
import Helper.ImageHelper;

/*
 * Calculate number of each direction
 * fv  = 8 dirs
 * training data for 3 versions of numbers from 1-10 (diffrent font)
 * then use minimum distance classifier
 * for each of the training data, get average fv
 * and then to classify, iterate on labels and find the minimum distance classifier
 */
public class ChainCode {
	public int[][] img;
	public int[][] contour;

	// E SE S SW W NW N NE
	int[] di = { 0, 1, 1, 1, 0, -1, -1, -1 };
	int[] dj = { 1, 1, 0, -1, -1, -1, 0, 1 };

	private boolean valid(int i, int j) {
		return (i >= 0 && i < img.length && j >= 0 && j < img[0].length);
	}

	public int[] getFV() {
		int[] res = new int[8];
		contour = new int[img.length][img[0].length];

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
		if (!found)
			return null;

		// System.out.println(si + " " + sj);

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

		// System.out.println(curi + " " + curj + " " + dir);

		while (curi != si || curj != sj) {
			contour[curi][curj] = 2;
			// I came from "dir", so the prev pixel is (dir+4)%8
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
			dir = ndir;
			curi = nxti;
			curj = nxtj;
			res[dir]++;
		}

		return res;
	}

	public static double[] normalize(int[] fv) {
		double sum = 0;
		for (int i = 0; i < fv.length; i++) {
			sum += fv[i];
		}
		double[] res = new double[fv.length];
		for (int i = 0; i < fv.length; i++) {
			res[i] = fv[i] / sum;
		}
		return res;
	}

	public static void main(String[] args) throws FileNotFoundException {

		// Training Part
		// train();

		// Classification part
		classify();
	}

	public static void classify() throws FileNotFoundException {
		Classifier minD = new MinimumDistanceClassifier();
		minD.readClassifier("Files/Assignment1/train_test2.txt");

		ChainCode cc = new ChainCode();
		cc.img = ImageHelper.getBWImage("Files/Assignment1/unknown4.bmp");
		double[] normlized_fv = ChainCode.normalize(cc.getFV());

		String bestLabel = minD.classify(normlized_fv);
		System.out.println("The closest number is " + bestLabel);
	}

	public static void train() throws FileNotFoundException {
		ChainCode cc = new ChainCode();
		double[][] input = new double[30][8];
		String[] output = new String[30];
		Classifier minD = new MinimumDistanceClassifier();
		for (int num = 0; num < 10; ++num) {
			for (int ind = 1; ind <= 3; ind++) {
				cc.img = ImageHelper
						.getBWImage("Files/Assignment1/training data/" + num
								+ ind + ".bmp");
				input[num * 3 + ind - 1] = ChainCode.normalize(cc.getFV());
				output[num * 3 + ind - 1] = num + "";
			}
		}
		minD.trainClassifier(input, output);
		minD.writeClassifier("Files/Assignment1/train_test2.txt");
	}
}
