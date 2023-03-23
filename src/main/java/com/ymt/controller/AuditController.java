package com.ymt.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.ymt.advice.ApiResult;
import com.ymt.advice.ResultCodeEnum;
import com.ymt.base.NormalConstant;
import com.ymt.domain.Audit;
import com.ymt.domain.ListType;
import com.ymt.domain.User;
import com.ymt.mapper.AuditMapper;
import com.ymt.mapper.ListTypeMapper;
import com.ymt.mapper.MediaBaseInfoMapper;
import com.ymt.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 审核
 */
@RestController
public class AuditController {

    @Resource
    private AuditMapper auditMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MediaBaseInfoMapper mediaBaseInfoMapper;

    @Resource
    private ListTypeMapper listTypeMapper;


    /**
     * 0:未审核，1:审核通过，2:审核未通过
     * 展示对方的审核列表
     *
     * @return
     */
    @GetMapping("/auditList")
    public ApiResult<List<Audit>> auditList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectByPrimaryKey(userId);
        Long bindId = user.getBindId();
        List<Audit> auditList = auditMapper.findAllByStatusAndUserId(NormalConstant.NOT_REVIEWED, bindId);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, auditList);
    }

    /**
     * x
     * 1:通过 2:未通过
     *
     * @return
     */
    @PostMapping("check")
    public ApiResult check(Audit audit) {
        int i = auditMapper.updateByPrimaryKeySelective(audit);
        if (i > 0) {
            String subTime = audit.getSubTime();
            DateTime parse = DateUtil.parse(subTime, "yyyy-MM-dd");
            String time = DateUtil.format(parse, "yyyy-MM-dd");
            List<Audit> audits = auditMapper.selectAllByUserIdAndSubTimeAndStatus(audit.getUserId(), time, NormalConstant.REVIEWED_SUCCESS);
            if (audits.size() == 0) {
                ListType type = listTypeMapper.selectByPrimaryKey(audit.getTypeId());
                Integer score = type.getScore();
                User user = userMapper.selectByPrimaryKey(audit.getUserId());
                user.setTotalScore(user.getTotalScore() + score);
                userMapper.updateByPrimaryKeySelective(user);
            }
        } else {
            return ApiResult.errorWith(ResultCodeEnum.CHECK_FAIL);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    public static void main(String[] args) {
        String subTime = "2022-07-30 21:57:49";
        DateTime parse = DateUtil.parse(subTime, "yyyy-MM-dd");
        String time = DateUtil.format(parse, "yyyy-MM-dd");
        System.out.println(time);
    }
}
