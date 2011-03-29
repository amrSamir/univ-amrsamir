package Helper;

public class MathHelper {
	public static long int_pow(long a, int b) {
		if( b == 0 )
			return 1;
		long res = int_pow(a, b/2);
		if( b%2 == 0 )
			return res*res;
		else
			return res*res*a;
	}
}
