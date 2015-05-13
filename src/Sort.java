/*
 * 
 */
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

// TODO: Auto-generated Javadoc
/**
 * The Class Sort.
 */
public class Sort {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int l = new Integer(JOptionPane.showInputDialog("輸入測試數組的大小"))
				.intValue();
		String s = "sort數據量："+l;
		int[] arr1 = new int[l];
		int[] arr2 = new int[l];
		int[] arr3 = new int[l];
		int[] arr4 = new int[l];
		int[] arr5 = new int[l];
		int[] arr6 = new int[l];
		int[] arr7 = new int[l];
		long time;

		for (int i = 0; i < l; i++) {
			arr1[i] = ran.nextInt(l);
			arr2[i] = arr1[i];
			arr3[i] = arr1[i];
			arr4[i] = arr1[i];
			arr5[i] = arr1[i];
			arr6[i] = arr1[i];
			arr7[i] = arr1[i];
		}
		// Sort.shellsort_asc(arr1, 0, arr1.length - 1);
		// Sort.shellsort_desc(arr2, 0, arr2.length - 1);
		time = System.currentTimeMillis();
		Sort.quicksort_asc(arr1, 0, arr1.length - 1, true);
		System.out.println("quicksort_asc耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\nquicksort_asc耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		time = System.currentTimeMillis();
		Arrays.sort(arr3);
		System.out.println("Arrays.sort耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\nArrays.sort耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		time = System.currentTimeMillis();
		Sort.megersort_asc(arr2, 0, arr2.length - 1);
		System.out.println("megersort_asc耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\nmegersort_asc耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		time = System.currentTimeMillis();
		Sort.shellsort_asc(arr4, 0, arr4.length - 1);
		System.out.println("shellsort_asc耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\nshellsort_asc耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		time = System.currentTimeMillis();
		Sort.insertsort_asc(arr5, 0, arr5.length - 1);
		System.out.println("insertsort_asc耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\ninsertsort_asc耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		time = System.currentTimeMillis();
		Sort.maopaosort_asc(arr6, 0, arr6.length - 1);
		System.out.println("maopaosort_asc耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\nmaopaosort_asc耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		time = System.currentTimeMillis();
		Sort.selectsort_asc(arr7, 0, arr7.length - 1);
		System.out.println("selectsort_asc耗時："
				+ (System.currentTimeMillis() - time));
		s = s + "\nselectsort_asc耗時：" + (System.currentTimeMillis() - time)+"毫秒";

		JOptionPane.showMessageDialog(null, s);

		System.out.println();
	}

	/**
	 * Quicksort_asc.
	 *
	 */
	private static Random ran = new Random();

	/**
	 * Swap.
	 *
	 * @param arr
	 *            the arr
	 * @param i
	 *            the i
	 * @param j
	 *            the j
	 */
	private static void swap(int[] arr, int i, int j) {
		int temp;
		temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	/**
	 * Quicksort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param random
	 *            the random
	 */
	public static void quicksort_asc(int[] arr, int start, int end,
			boolean random) {
		if (start >= end)
			return;

		if (end - start <= 7) {
			Sort.maopaosort_asc(arr, start, end);
			return;
		}
		int i, j = end;
		// 生成一個隨機數用來替換首個充當隨機PIVOT
		if (random == true) {
			i = ran.nextInt(end - start) + start + 1;
			if (arr[i] != arr[start])
				swap(arr, start, i);
		}
		i = start;

		// 開始排序
		while (i < j) {
			// 從后往前查詢第一個小于PIVOT的，替換（此時PIVOT即ARR[I]）
			while (arr[i] <= arr[j] && i < j)
				j--;
			if (i < j) {
				swap(arr, i, j);
				i++;
			}
			// 從前往后查詢第一個大于PIVOT的，替換（此時PIVOT即ARR[j]）
			while (arr[i] <= arr[j] && i < j)
				i++;
			if (i < j) {
				swap(arr, i, j);
				j--;
			}

		}
		// 遞歸調用PIVOT左右兩邊的範圍
		if (i - 1 > start)
			quicksort_asc(arr, start, i - 1, random);
		if (j + 1 < end)
			quicksort_asc(arr, j + 1, end, random);
	}

	/**
	 * Quicksort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void quicksort_asc(int[] arr, int start, int end) {
		quicksort_asc(arr, start, end, true);
	}

	/**
	 * Quicksort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param random
	 *            the random
	 */
	public static void quicksort_desc(int[] arr, int start, int end,
			boolean random) {
		if (start >= end)
			return;

		if (end - start <= 7) {
			Sort.maopaosort_desc(arr, start, end);
			return;
		}

		int i, j = end;
		// 生成一個隨機數用來替換首個充當隨機PIVOT
		if (random == true) {
			i = ran.nextInt(end - start) + start + 1;
			if (arr[i] != arr[start])
				swap(arr, start, i);
		}
		i = start;

		// 開始排序
		while (i < j) {
			// 從后往前查詢第一個大于PIVOT的，替換（此時PIVOT即ARR[I]）
			while (arr[i] >= arr[j] && i < j)
				j--;
			if (i < j) {
				swap(arr, i, j);
				i++;
			}
			// 從前往后查詢第一個小于PIVOT的，替換（此時PIVOT即ARR[j]）
			while (arr[i] >= arr[j] && i < j)
				i++;
			if (i < j) {
				swap(arr, i, j);
				j--;
			}

		}
		// 遞歸調用PIVOT左右兩邊的範圍
		if (i - 1 > start)
			quicksort_desc(arr, start, i - 1, random);
		if (j + 1 < end)
			quicksort_desc(arr, j + 1, end, random);
	}

	/**
	 * Quicksort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void quicksort_desc(int[] arr, int start, int end) {
		quicksort_desc(arr, start, end, true);
	}

	/**
	 * Megersort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void megersort_asc(int[] arr, int start, int end) {
		if (start >= end)
			return;
		int mid = (start + end) / 2;
		megersort_asc(arr, start, mid);
		megersort_asc(arr, mid + 1, end);
		megerarray_asc(arr, start, mid, end);
	}

	/**
	 * Megerarray_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param mid
	 *            the mid
	 * @param end
	 *            the end
	 */
	private static void megerarray_asc(int[] arr, int start, int mid, int end) {

		if (end - start <= 7) {
			Sort.maopaosort_asc(arr, start, end);
			return;
		}

		int[] temparray = new int[end - start + 1];
		int i = start, j = mid + 1, k = 0;
		while (i <= mid && j <= end) {
			if (arr[i] <= arr[j])
				temparray[k++] = arr[i++];
			else
				temparray[k++] = arr[j++];
		}
		while (i <= mid)
			temparray[k++] = arr[i++];
		while (j <= end)
			temparray[k++] = arr[j++];

		i = start;
		k = 0;
		while (k <= temparray.length - 1)
			if (arr[i++] != temparray[k++])
				arr[i - 1] = temparray[k - 1];

	}

	/**
	 * Megersort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void megersort_desc(int[] arr, int start, int end) {
		if (start >= end)
			return;
		int mid = (start + end) / 2;
		megersort_desc(arr, start, mid);
		megersort_desc(arr, mid + 1, end);
		megerarray_desc(arr, start, mid, end);
	}

	/**
	 * Megerarray_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param mid
	 *            the mid
	 * @param end
	 *            the end
	 */
	private static void megerarray_desc(int[] arr, int start, int mid, int end) {

		if (end - start <= 7) {
			Sort.maopaosort_desc(arr, start, end);
			return;
		}

		int[] temparray = new int[end - start + 1];
		int i = start, j = mid + 1, k = 0;
		while (i <= mid && j <= end) {
			if (arr[i] >= arr[j])
				temparray[k++] = arr[i++];
			else
				temparray[k++] = arr[j++];
		}
		while (i <= mid)
			temparray[k++] = arr[i++];
		while (j <= end)
			temparray[k++] = arr[j++];

		i = start;
		k = 0;
		while (k <= temparray.length - 1)
			if (arr[i++] != temparray[k++])
				arr[i - 1] = temparray[k - 1];

	}

	/**
	 * Maopaosort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void maopaosort_asc(int[] arr, int start, int end) {
		for (int i = start; i <= end; i++)
			for (int j = i + 1; j <= end; j++)
				if (arr[i] > arr[j])
					swap(arr, i, j);
	}

	/**
	 * Maopaosort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void maopaosort_desc(int[] arr, int start, int end) {
		for (int i = start; i <= end; i++)
			for (int j = i + 1; j <= end; j++)
				if (arr[i] < arr[j])
					swap(arr, i, j);
	}

	/**
	 * Selectsort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void selectsort_asc(int[] arr, int start, int end) {
		int min;
		for (int i = start; i < end; i++) {
			min = i;
			for (int j = i + 1; j <= end; j++)
				if (arr[j] < arr[min])
					min = j;
			if (min != i)
				swap(arr, i, min);
		}
	}

	/**
	 * Selectsort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void selectsort_desc(int[] arr, int start, int end) {
		int max;
		for (int i = start; i < end; i++) {
			max = i;
			for (int j = i + 1; j <= end; j++)
				if (arr[j] > arr[max])
					max = j;
			if (max != i)
				swap(arr, i, max);
		}
	}

	/**
	 * Insertsort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void insertsort_asc(int[] arr, int start, int end) {
		for (int i = start + 1; i <= end; i++)
			for (int j = i; j > start; j--)
				if (arr[j] < arr[j - 1])
					swap(arr, j, j - 1);
				else
					break;
	}

	/**
	 * Insertsort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void insertsort_desc(int[] arr, int start, int end) {
		for (int i = start + 1; i <= end; i++)
			for (int j = i; j > start; j--)
				if (arr[j] > arr[j - 1])
					swap(arr, j, j - 1);
				else
					break;
	}

	/**
	 * Shellsort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void shellsort_asc(int[] arr, int start, int end) {
		shellsort_asc(arr, start, end, (end - start) / 2);
	}

	/**
	 * Shellsort_asc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param d
	 *            the d
	 */
	public static void shellsort_asc(int[] arr, int start, int end, int d) {
		for (int i = start + d; i <= end; i += d)
			for (int j = i; j - d >= start; j -= d)
				if (arr[j] < arr[j - d])
					swap(arr, j, j - d);
				else
					break;

		if (d/2>= 1)
			shellsort_asc(arr, start, end, d /2);
	}

	/**
	 * Shellsort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public static void shellsort_desc(int[] arr, int start, int end) {
		shellsort_desc(arr, start, end, (end - start) / 2);
	}

	/**
	 * Shellsort_desc.
	 *
	 * @param arr
	 *            the arr
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param d
	 *            the d
	 */
	public static void shellsort_desc(int[] arr, int start, int end, int d) {
		for (int i = start + d; i <= end; i += d)
			for (int j = i; j - d >= start; j -= d)
				if (arr[j] > arr[j - d])
					swap(arr, j, j - d);
				else
					break;

		if (d / 2 >= 1)
			shellsort_desc(arr, start, end, d / 2);
	}

	
}
