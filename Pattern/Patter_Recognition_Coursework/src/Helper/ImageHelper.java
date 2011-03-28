package Helper;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageHelper {

	/*
	 * Source:
	 * http://mostafanageeb.blogspot.com/2010/03/read-grayscale-image-into
	 * -2d-array.html
	 */
	public static int[][] getImage(String dir) {
		try {
			File file = new File(dir);

			BufferedImage image = ImageIO.read(file);
			Raster image_raster = image.getData();

			int[][] original; // where we'll put the image

			// get pixel by pixel
			int[] pixel = new int[1];
			int[] buffer = new int[1];

			// declaring the size of arrays
			original = new int[image_raster.getHeight()][image_raster
					.getWidth()];

			// get the image in the array
			for (int i = 0; i < image_raster.getHeight(); i++)
				for (int j = 0; j < image_raster.getWidth(); j++) {
					pixel = image_raster.getPixel(j, i, buffer);
					original[i][j] = pixel[0];
				}
			return original;
		} catch (Exception e) {
			return null;
		}
	}

	public static int[][] getBWImage(String dir) {
		int[][] arr = getImage(dir);
		if (arr == null)
			return null;
		int[][] ret = new int[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				if (arr[i][j] == 0)
					ret[i][j] = 1;
				else
					ret[i][j] = 0;
			}
		}
		return ret;
	}
}
