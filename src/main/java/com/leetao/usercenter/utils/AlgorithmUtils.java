package com.leetao.usercenter.utils;

import java.util.List;

/**
 * 算法工具类
 *
 * @author lee tao
 */
public class AlgorithmUtils {

	/**
	 * 比较两个字符串列表之间的最短编辑距离
	 * @param word1 字符串列表1
	 * @param word2 字符串列表2
	 * @return 最短编辑距离
	 */
	public static int minDistance(List<String> word1, List<String> word2){
		int n = word1.size();
		int m = word2.size();
		int[][] dp = new int[n+1][m+1];
		for(int i = 1;i <= m;i++) dp[0][i] = i;
		for(int i = 1;i <= n;i++) dp[i][0] = i;
		for(int i = 1;i <= n;i++){
			for(int j = 1;j <= m;j++){
				if(word1.get(i - 1).equals(word2.get(j - 1))) dp[i][j] = dp[i-1][j-1];
				else dp[i][j] = 1 + Math.min(dp[i][j-1],Math.min(dp[i-1][j-1],dp[i-1][j]));
			}
		}
		return dp[n][m];
	}

	/**
	 * 比较两个字符串的最短编辑距离
	 * @param word1 字符串1
	 * @param word2 字符串2
	 * @return 最短编辑距离
	 */
	public static int minDistance(String word1, String word2){
		char[] arr1 = word1.toCharArray();
		char[] arr2 = word2.toCharArray();
		int n = arr1.length;
		int m = arr2.length;
		int[][] dp = new int[n+1][m+1];
		for(int i = 1;i <= m;i++) dp[0][i] = i;
		for(int i = 1;i <= n;i++) dp[i][0] = i;
		for(int i = 1;i <= n;i++){
			for(int j = 1;j <= m;j++){
				if(arr1[i-1] == arr2[j-1]) dp[i][j] = dp[i-1][j-1];
				else dp[i][j] = 1 + Math.min(dp[i][j-1],Math.min(dp[i-1][j-1],dp[i-1][j]));
			}
		}
		return dp[n][m];
	}

}
