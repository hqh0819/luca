/**
 * 
 */

/**
 * @author qiheng.hu
 * @date 2015年5月13日
 */
public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動產生的方法 Stub
		int[] sort = new int[] { 1, 0, 10, 20, 3, 5, 6, 4, 9, 8, 12, 17, 34, 11 };

	}

	private void build(int[] arr) {
		int start = this.getParentIndex(arr.length - 1);

	}

	/**
	 * 父节点位置
	 * 
	 * @param current
	 * @return
	 */
	private static int getParentIndex(int current) {
		return (current - 1) >> 1;
	}

	/**
	 * 左子节点position注意括号，加法优先级更高
	 * 
	 * @param current
	 * @return
	 */
	private static int getChildLeftIndex(int current) {
		return (current << 1) + 1;
	}

	/**
	 * 右子节点position
	 * 
	 * @param current
	 * @return
	 */
	private static int getChildRightIndex(int current) {
		return (current << 1) + 2;
	}

}
