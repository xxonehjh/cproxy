package com.xxonehjh.cproxy.util;

import java.util.List;

import com.xxonehjh.cproxy.Constants;

public class ByteArrayUtil {

	public static byte[] concat(List<byte[]> arr) {
		if (null == arr || 0 == arr.size()) {
			return Constants.EMPTY_BYTES;
		}
		if (1 == arr.size()) {
			return arr.get(0);
		}

		// Merge the specified arrays into one array.
		int length = 0;
		for (byte[] a : arr) {
			if (Integer.MAX_VALUE - length < a.length) {
				throw new IllegalArgumentException("The total length of the specified arrays is too big.");
			}
			length += a.length;
		}

		if (length == 0) {
			return Constants.EMPTY_BYTES;
		}

		byte[] mergedArray = new byte[length];
		for (int i = 0, j = 0; i < arr.size(); i++) {
			byte[] a = arr.get(i);
			System.arraycopy(a, 0, mergedArray, j, a.length);
			j += a.length;
		}

		return mergedArray;
	}

}
