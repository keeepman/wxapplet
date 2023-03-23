package com.ymt.interceptor;

import cn.hutool.core.util.StrUtil;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ymt.advice.ApiException;
import com.ymt.advice.ApiResult;
import com.ymt.advice.ResultCodeEnum;
import com.ymt.base.NormalConstant;
import com.ymt.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author mintaoyu
 * Date on 2020-05-05  19:23
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    /**
     * 十分钟 过期时间
     */
    public static final Long EXPIRE_TIME = 600000L;


    /**
     * 请求token校验
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(NormalConstant.TOKEN);
        if (StrUtil.isNotBlank(token)) {
            DecodedJWT jwt = JwtUtil.verity(token);
            if (jwt != null) {
                Long userId = jwt.getClaim(NormalConstant.USER_ID).asLong();
                request.setAttribute(NormalConstant.USER_ID, userId);
                return true;
            }
            throw new ApiException(ApiResult.errorWith(ResultCodeEnum.TOKEN_FAILED));
        }
        throw new ApiException(ApiResult.errorWith(ResultCodeEnum.TOKEN_NULL));
    }
}

