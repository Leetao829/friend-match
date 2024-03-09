package com.leetao.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leetao.usercenter.common.ErrorCode;
import com.leetao.usercenter.constant.UserConstants;
import com.leetao.usercenter.exception.BusinessException;
import com.leetao.usercenter.model.vo.UserVO;
import com.leetao.usercenter.service.UserService;
import com.leetao.usercenter.model.domain.User;
import com.leetao.usercenter.mapper.UserMapper;
import com.leetao.usercenter.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.leetao.usercenter.constant.UserConstants.ADMIN_ROLE;
import static com.leetao.usercenter.constant.UserConstants.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
* @author taoLi
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

	private static final String  SALT = "taoLi";
	@Resource
	private UserMapper userMapper;

	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
		//1.非空校验
		if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
		}
		//2.1 账号长度校验
		if(userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
		}
		//2.2 密码长度校验
		if(userPassword.length() < 8 || checkPassword.length() < 8){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
		}
		//星球编号长度校验
		if(planetCode.length() > 5){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球账号过长");
		}
		//2.3 密码和校验码相同
		if(!userPassword.equals(checkPassword)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验码不一致");
		}
		//2.4 账号中不能包含重复字符
		String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
		if(matcher.find()){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号中包含特殊字符");
		}
		//2.5 账号重复校验，从数据库中查询
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_account",userAccount);
		long count = userMapper.selectCount(queryWrapper);
		if(count > 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"该账号已存在");
		}
		//校验星球编号是否重复
		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("planet_code",planetCode);
		count = userMapper.selectCount(queryWrapper);
		if(count > 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
		}
		//3.对密码使用加密算法进行加密
		String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

		//4. 将数据插入到数据库中
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(newPassword);
		user.setPlanetCode(planetCode);
		boolean res = this.save(user);
		if(!res){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册失败,请联系管理员");
		}
		return user.getId();
	}

	@Override
	public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		//1.账号和密码非空
		if(StringUtils.isAnyBlank(userAccount,userPassword)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码不能为空");
		}
		//2.账号长度不能小于4位
		if(userAccount.length() < 4){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
		}
		//3.密码长度不能小于8位
		if(userPassword.length() < 8){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
		}
		//4.账号中不能包含特殊字符
		String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
		if(matcher.find()){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号中不能包含特殊字符");
		}
		//5.将账号和密码和数据库中进行查询对比(加密之后)
		String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_account",userAccount);
		queryWrapper.eq("user_password",newPassword);
		User user = userMapper.selectOne(queryWrapper);
		if(user == null){
			log.info("user login failed, userAccount can not match userPassword");
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码错误");
		}
		//6.返回脱敏之后的用户信息
		User safetyUser = getSafetyUser(user);
		//7.保存登录状态到session中
		request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
		return safetyUser;
	}

	/**
	 * 用户信息脱敏
	 * @param originUser 原用户对象
	 * @return 脱敏之后的用户对象
	 */
	@Override
	public User getSafetyUser(User originUser){
		if(originUser == null){
			return null;
		}
		User safetyUser = new User();
		safetyUser.setId(originUser.getId());
		safetyUser.setUserName(originUser.getUserName());
		safetyUser.setUserAccount(originUser.getUserAccount());
		safetyUser.setAvatarUrl(originUser.getAvatarUrl());
		safetyUser.setGender(originUser.getGender());
		safetyUser.setPhone(originUser.getPhone());
		safetyUser.setEmail(originUser.getEmail());
		safetyUser.setUserStatus(originUser.getUserStatus());
		safetyUser.setCreateTime(originUser.getCreateTime());
		safetyUser.setUserRole(originUser.getUserRole());
		safetyUser.setPlanetCode(originUser.getPlanetCode());
		safetyUser.setTags(originUser.getTags());
		safetyUser.setProfile(originUser.getProfile());
		return safetyUser;
	}

	@Override
	public int userLogout(HttpServletRequest request) {
		//从session中移除登录状态
		request.getSession().removeAttribute(USER_LOGIN_STATE);
		return 1;
	}

	@Override
	public List<User> searchUsersByTags(List<String> tagNameList) {
		if(CollectionUtils.isEmpty(tagNameList)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//方法一：使用模糊查询
		//QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		//拼接and查询
		//使用like模糊查询
		//for(String tagName : tagNameList){
		//	queryWrapper = queryWrapper.like("tags",tagName);
		//}
		//List<User> userList = userMapper.selectList(queryWrapper);
		//return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

		//使用查询所有用户的方式进行查询，

		//1.将所有的用户信息查询到内存中
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		List<User> userList = userMapper.selectList(queryWrapper);
		Gson gson = new Gson();
		//2.对所有的用户信息进行过滤，筛选掉所有不包含标签的用户
		return userList.stream().filter(user -> {
			//将用户信息中的tags字符串转换成为java对象再进行判断
			String tags = user.getTags();
			//标签不存在返回false
			if(StringUtils.isBlank(tags)){
				return false;
			}
			//将标签转换为集合判断
			Set<String> tagSet = gson.fromJson(tags, new TypeToken<Set<String>>() {
			}.getType());
			//用户标签集合中不包含传入标签中任意一个返回false
			for(String tagName : tagNameList){
				if (!tagSet.contains(tagName)) {
					return false;
				}
			}
			return true;
		}).map(this::getSafetyUser).collect(Collectors.toList());
	}


	@Override
	public int updateUser(User user,User loginUser) {
		Long userId = user.getId();
		if(userId == null || userId <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//如果是管理员，能够修改任意用户
		//如果是普通用户，只能够修改自己的信息
		if(!isAdmin(loginUser) && userId.longValue() != loginUser.getId().longValue()){
			throw new BusinessException(ErrorCode.NO_AUTH);
		}
		//判断当前用户是否存在
		User oldUser = userMapper.selectById(userId);
		if(oldUser == null){
			throw new BusinessException(ErrorCode.NULL_ERROR);
		}
		return userMapper.updateById(user);
	}

	@Override
	public User getLoginUser(HttpServletRequest request) {
		if(request == null) {
			return null;
		}
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		if(userObj == null){
			throw new BusinessException(ErrorCode.NOT_LOGIN);
		}
		return (User) userObj;
	}

	@Override
	public boolean isAdmin(HttpServletRequest request) {
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		User user = (User) userObj;
		return user != null && user.getUserRole() == ADMIN_ROLE;
	}

	@Override
	public boolean isAdmin(User loginUser) {
		return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
	}

	@Override
	public List<User> matchUsers(int num, User loginUser) {
		String tags = loginUser.getTags();
		List<User> userList = this.list();
		Gson gson = new Gson();
		List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
		}.getType());
		//用户  列表下标=> 相似度
		//排序列表，按照值（相似度）进行排序
		SortedMap<Integer,Long> indexDistanceMap = new TreeMap<>();
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			String userTags = user.getTags();
			if(StringUtils.isBlank(userTags)){
				continue;
			}
			List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
			}.getType());
			long minDistance = AlgorithmUtils.minDistance(tagList, userTagList);
			indexDistanceMap.put(i,minDistance);
		}
		//取出前几名排序的下标
		List<Integer> maxDistanceIndex = indexDistanceMap.keySet().stream().limit(num).collect(Collectors.toList());
		return maxDistanceIndex.stream().map(index -> getSafetyUser(userList.get(index))).collect(Collectors.toList());
	}

}




