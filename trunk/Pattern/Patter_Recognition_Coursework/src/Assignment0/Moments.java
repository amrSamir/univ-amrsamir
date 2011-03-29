package Assignment0;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import Helper.ImageHelper;
import Helper.MathHelper;

/*
 * Class to calculate the moments of b/w images
 * Assumption: the (0,0) point of the image is top-left corner 
 */
public class Moments {
	public static double Calc_Regular_Moments_2d(int[][] img, int p, int q) {
		double result = 0;
		for (int y = 0; y < img.length; y++) {
			for (int x = 0; x < img[y].length; x++) {
				result += Math.pow(x, p) * Math.pow(y, q) * img[y][x];
			}
		}
		return result;
	}

	public static double Calc_Central_Moments_2d(int[][] img, int p, int q) {
		double x_bar = Calc_Regular_Moments_2d(img, 1, 0)
				/ Calc_Regular_Moments_2d(img, 0, 0);
		double y_bar = Calc_Regular_Moments_2d(img, 0, 1)
				/ Calc_Regular_Moments_2d(img, 0, 0);
		double result = 0;
		for (int y = 0; y < img.length; y++) {
			for (int x = 0; x < img[y].length; x++) {
				result += Math.pow(x - x_bar, p) * Math.pow(y - y_bar, q)
						* img[y][x];
			}
		}
		return result;
	}

	public static double Calc_Normalized_Central_Moments_2d(int[][] img, int p,
			int q) {
		double gamma = (p + q + 2.0) / 2.0;
		return Calc_Central_Moments_2d(img, p, q)
				/ Math.pow(Calc_Central_Moments_2d(img, 0, 0), gamma);
	}

	public static void main(String[] args) throws FileNotFoundException {
		int nNumber = 5;
		int nImagePerNumber = 2;
		int maxP = 2;
		int maxQ = 2;
		double[][] Normal_Moments = new double[nNumber * nImagePerNumber][maxP
				* maxQ];
		double[][] Central_Moments = new double[nNumber * nImagePerNumber][maxP
				* maxQ];
		double[][] Normalized_Central_Moments = new double[nNumber
				* nImagePerNumber][maxP * maxQ];
		PrintWriter writer = new PrintWriter(new File("Normalized_Central_Moments.csv"));
		for (int num = 1; num <= nNumber; ++num) {
			for (int i = 1; i <= nImagePerNumber; ++i) {
				// Windows
				// int [][] img =
				// ImageHelper.getBWImage("C:\\Users\\Amr\\Documents\\Workspace\\Patter_Recognition_Coursework\\Files\\Assignment0\\"
				// + num + i + ".bmp");
				int[][] img = ImageHelper
						.getBWImage("Files/Assignment0/" + num + i
								+ ".bmp");
				for (int p = 1; p <= maxP; ++p) {
					for (int q = 1; q <= maxQ; ++q) {
						Normal_Moments[(num - 1) * nImagePerNumber + (i - 1)][(p - 1)
								* maxQ + (q - 1)] = Calc_Regular_Moments_2d(
								img, p, q);
						writer.print(Calc_Normalized_Central_Moments_2d(img, p, q) + ",");
						Central_Moments[(num - 1) * nImagePerNumber + (i - 1)][(p - 1)
								* maxQ + (q - 1)] = Calc_Central_Moments_2d(
								img, p, q);
						Normalized_Central_Moments[(num - 1) * nImagePerNumber
								+ (i - 1)][(p - 1) * maxQ + (q - 1)] = Calc_Normalized_Central_Moments_2d(
								img, p, q);
					}
				}
				writer.println();
				// Displaying only
				// System.out.println(num + " " + i);
				// for (int ii = 0; ii < img.length; ii++) {
				// for (int jj = 0; jj < img[ii].length; jj++) {
				// System.out.print(img[ii][jj] + " ");
				// }
				// System.out.println();
				// }
			}
		}
		writer.close();
	}
}
