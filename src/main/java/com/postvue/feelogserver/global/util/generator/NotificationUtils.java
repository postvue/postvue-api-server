package com.postvue.feelogserver.global.util.generator;

public final class NotificationUtils {
	// 주어진 값이 수열에 속하는지 확인하는 함수
	public static boolean isNotificationInSequence(int value, int minNum, int sequenceNum) {
		// 홀수 인덱스 항 체크: minNum * n^k 형태인지 확인
		if (isPowerOfM(value / minNum, sequenceNum) && value % minNum == 0) {
			return true;
		}
		// 짝수 인덱스 항 체크: m^k 형태인지 확인
		return isPowerOfM(value, sequenceNum);
	}

	public static boolean isNotificationInSequence(
		int value, int n, int m,
		Boolean isCondition) {
		if (isCondition) {
			return true;
		}

		// 홀수 인덱스 항 체크: n * m^k 형태인지 확인
		if (isPowerOfM(value / n, m) && value % n == 0) {
			return true;
		}

		// 짝수 인덱스 항 체크: m^k 형태인지 확인
		return isPowerOfM(value, m);
	}

	// 주어진 값이 m^k 꼴인지 확인하는 함수
	private static boolean isPowerOfM(int value, int m) {
		if (value < 1) {
			return false;
		}
		// 값이 10으로 나눠질 수 있는지 반복적으로 확인
		while (value % m == 0) {
			value /= m;
		}
		// 최종적으로 1이 되면 10^k 형태임
		return value == 1;
	}
}
