package Assignment0;

import Helper.ImageHelper;

public class Moments {
	public static int Calc_Regular_Moments_2d(int[][] img, int p, int q) {
		return 0;
	}

	public static int Calc_Central_Moments_2d(int[][] img, int p, int q) {
		return 0;
	}

	public static int Calc_Normalized_Central_Moments_2d(int[][] img, int p,
			int q) {
		return 0;
	}

	public static void main(String[] args) {
		for (int num = 1; num <= 5; ++num)
			for (int i = 1; i <= 2; ++i) {
				int [][] img = ImageHelper.getBWImage("C:\\Users\\Amr\\Documents\\Workspace\\Patter_Recognition_Coursework\\Sample_Images\\Assignment0\\"
						+ num + i + ".bmp");
				System.out.println(num+" "+i);
				for (int ii = 0; ii < img.length; ii++) {
					for (int jj = 0; jj < img[ii].length; jj++) {
						System.out.print(img[ii][jj]+" ");
					}
					System.out.println();
				}
			}
	}
}
