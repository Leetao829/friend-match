package com.leetao.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leetao.usercenter.model.domain.UserTeam;
import com.leetao.usercenter.service.UserTeamService;
import com.leetao.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author taoLi
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-02-22 22:47:17
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




