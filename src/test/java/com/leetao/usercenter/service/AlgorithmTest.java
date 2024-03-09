package com.leetao.usercenter.service;

import com.leetao.usercenter.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AlgorithmTest {
	@Test
	void test(){
		List<String> list1 = Arrays.asList("大二", "java", "男");
		List<String> list2 = Arrays.asList("java", "大二", "女");
		List<String> list3 = Arrays.asList("java", "大二", "男");
		System.out.println(AlgorithmUtils.minDistance(list1,list2));
		System.out.println(AlgorithmUtils.minDistance(list1,list3));

	}


}
