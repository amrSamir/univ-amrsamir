package Helper;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
public class MyMatrix {
	public double[][] mat;
	public int row;
	public int col;
	public static final double EPS = 1e-9;

	public MyMatrix(int r, int c) {
		row = r;
		col = c;
		mat = new double[row][col];
		for (int i = 0; i < mat.length; i++) {
			Arrays.fill(mat[i], 0);
		}
	}

	public MyMatrix(int[][] img) {
		row = img.length;
		col = img[0].length;
		mat = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				mat[i][j] = img[i][j];
			}
		}
	}

	public MyMatrix add(MyMatrix m) {
		if (m.row != row || m.col != col)
			return null;
		MyMatrix res = new MyMatrix(row, col);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				res.mat[i][j] = mat[i][j] + m.mat[i][j];
			}
		}
		return res;
	}

	public MyMatrix multiply(int v) {
		MyMatrix res = new MyMatrix(row, col);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				res.mat[i][j] = mat[i][j] * v;
			}
		}
		return res;
	}

	public MyMatrix multiply(MyMatrix m) {
		if (col != m.row)
			return null;
		MyMatrix res = new MyMatrix(row, m.col);
		for (int k = 0; k < m.row; k++) {
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < m.col; j++) {
					res.mat[i][j] = mat[i][k] * m.mat[k][j];
				}
			}
		}
		return res;
	}

	private boolean zeros(ArrayList<Double> row) {
		for (Double double1 : row) {
			if (Math.abs(double1) > EPS)
				return false;
		}
		return true;
	}

	private boolean swapRow(int i, ArrayList<ArrayList<Double>> mat) {
		for (int j = i + 1; j < mat.size(); j++) {
			if (Math.abs(mat.get(j).get(i)) > EPS) {
				ArrayList<Double> temp = mat.get(i);
				mat.set(i, mat.get(j));
				mat.set(j, temp);
				return true;
			}
		}
		return false;
	}

	private boolean divideRow(int i, double val,
			ArrayList<ArrayList<Double>> mat) {
		for (int j = 0; j < mat.get(i).size(); j++) {
			if (Math.abs(mat.get(j).get(i)) > EPS) {
				ArrayList<Double> temp = mat.get(i);
				mat.set(i, mat.get(j));
				mat.set(j, temp);
				return true;
			}
		}
		return false;
	}

	// matrix inversioon
	// the result is put in Y
	void MatrixInversion(double[][] A, int order, double[][] Y) {
		// get the determinant of a
		double det = 1.0 / CalcDeterminant(A, order);

		// memory allocation
		double[][] minor = new double[order - 1][order - 1];

		for (int j = 0; j < order; j++) {
			for (int i = 0; i < order; i++) {
				// get the co-factor (matrix) of A(j,i)
				GetMinor(A, minor, j, i, order);
				Y[i][j] = det * CalcDeterminant(minor, order - 1);
				if ((i + j) % 2 == 1)
					Y[i][j] = -Y[i][j];
			}
		}

	}

	// calculate the cofactor of element (row,col)
	int GetMinor(double[][] src, double[][] dest, int row, int col, int order) {
		// indicate which col and row is being copied to dest
		int colCount = 0, rowCount = 0;

		for (int i = 0; i < order; i++) {
			if (i != row) {
				colCount = 0;
				for (int j = 0; j < order; j++) {
					// when j is not the element
					if (j != col) {
						dest[rowCount][colCount] = src[i][j];
						colCount++;
					}
				}
				rowCount++;
			}
		}

		return 1;
	}

	// Calculate the determinant recursively.
	double CalcDeterminant(double[][] mat, int order) {
		// order must be >= 0
		// stop the recursion when matrix is a single element
		if (order == 1)
			return mat[0][0];

		// the determinant value
		double det = 0;

		// allocate the cofactor matrix
		double[][] minor = new double[order - 1][order - 1];

		for (int i = 0; i < order; i++) {
			// get minor of element (0,i)
			GetMinor(mat, minor, 0, i, order);
			// the recusion is here!

			det += (i % 2 == 1 ? -1.0 : 1.0) * mat[0][i]
					* CalcDeterminant(minor, order - 1);
			// det += pow( -1.0, i ) * mat[0][i] * CalcDeterminant(
			// minor,order-1 );
		}

		// release memory
		return det;
	}

	public MyMatrix inverse() {
		if (col != row)
			return null;
		// TODO
		return null;
	}

	public void display() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				System.out.print(mat[i][j] + " ");
			}
			System.out.println();
		}
	}
}
