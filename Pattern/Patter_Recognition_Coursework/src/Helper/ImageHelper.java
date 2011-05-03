package Helper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class ImageHelper {

	public static void test(String st, String en) {
		// Retrieve Image
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(new File(st));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// bimg = bimg.getSubimage(0, 0, 256, 256);

		// Access it's pixel
		int w = bimg.getWidth(null);
		int h = bimg.getHeight(null);
		int[][] pixels = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int rgb = bimg.getRGB(x, y);
				byte r = (byte) ((rgb & 0x00ff0000) >> 16);
				byte g = (byte) ((rgb & 0x0000ff00) >> 8);
				byte b = (byte) (rgb & 0x000000ff);
				pixels[x][y] = (int) Math.round(0.299 * r + 0.587 * g + 0.114
						* b);
				// pixels[x][y] = g;
				// pixels[x][y] = rgb;
			}
		}

		// processImage();
		BufferedImage nbimg = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int rgb = (pixels[x][y]) | ((pixels[x][y]) << 16)
						| ((pixels[x][y]) << 8);
				nbimg.setRGB(x, y, rgb);
				// byte r = (byte) ((rgb & 0x00ff0000) >> 16);
				// byte g = (byte) ((rgb & 0x0000ff00) >> 8);
				// byte b = (byte) (rgb & 0x000000ff);
			}
		}
		// Draw back
		// Save as JPEG
		File file = new File(en);
		try {
			ImageIO.write(nbimg, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int[][] getImageMatlab(String dir) {
		try {
			Scanner sc = new Scanner(new File(dir));
			ArrayList<ArrayList<Integer>> r = new ArrayList<ArrayList<Integer>>();
			while (sc.hasNextLine()) {
				StringTokenizer st = new StringTokenizer(sc.nextLine(), ",");
				r.add(new ArrayList<Integer>());
				while (st.hasMoreTokens()) {
					r.get(r.size() - 1).add(Integer.parseInt(st.nextToken()));
				}
			}
			int[][] res = new int[r.size()][r.get(0).size()];
			for (int i = 0; i < res.length; i++) {
				for (int j = 0; j < res[i].length; j++) {
					res[i][j] = r.get(i).get(j);
				}
			}
			return res;
		} catch (Exception e) {
			return null;
		}
	}

	public static void setImageMatlab(String dir, int[][] img) {
		try {
			PrintWriter pw = new PrintWriter(new File(dir));
			for (int i = 0; i < img.length; i++) {
				if (i != 0)
					pw.println();
				for (int j = 0; j < img[i].length; j++) {
					if (j != 0)
						pw.print(",");
					pw.print(img[i][j]);
				}
			}
			pw.close();

		} catch (Exception e) {

		}
	}

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
			int[] buffer = new int[3];

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

	public static void setImage(String dir, int[][] pixels) {
		try {
			int w = pixels[0].length;
			int h = pixels.length;
			BufferedImage nbimg = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_GRAY);
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					int rgb = (pixels[y][x]) | ((pixels[y][x]) << 16)
							| ((pixels[y][x]) << 8);
					nbimg.setRGB(x, y, rgb);
				}
			}
			// Draw back
			// Save as JPEG
			File file = new File(dir);
			try {
				ImageIO.write(nbimg, "jpg", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			// BufferedImage image = ImageIO.read(file);
			// Raster image_raster = image.getData();
			//
			// int[][] original; // where we'll put the image
			//
			// // get pixel by pixel
			// int[] pixel = new int[1];
			// int[] buffer = new int[3];
			//
			// // declaring the size of arrays
			// original = new int[image_raster.getHeight()][image_raster
			// .getWidth()];
			//
			// // get the image in the array
			// for (int i = 0; i < image_raster.getHeight(); i++)
			// for (int j = 0; j < image_raster.getWidth(); j++) {
			// pixel = image_raster.getPixel(j, i, buffer);
			// original[i][j] = pixel[0];
			// }
			// return original;
		} catch (Exception e) {
			System.out.println("ERROR");
			// return null;
		}
	}

	public static BufferedImage getImageFromArray(int[] pixels, int width,
			int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = (WritableRaster) image.getData();
		raster.setPixels(0, 0, width, height, pixels);
		return image;
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
