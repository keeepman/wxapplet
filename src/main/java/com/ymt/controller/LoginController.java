package com.ymt.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ymt.advice.ApiResult;
import com.ymt.advice.ResultCodeEnum;
import com.ymt.bean.InitBean;
import com.ymt.bean.LoginBean;
import com.ymt.domain.User;
import com.ymt.mapper.UserMapper;
import com.ymt.service.UserService;
import com.ymt.utils.JwtUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class LoginController {
    private static final String APP_ID = "wxe561812cd4a0f510";
    private static final String SECRET = "592e27f6d2d080744c8e4e9e769e55a1";

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @Resource
    private InitBean initBean;

    @Resource
    private PlatformTransactionManager platformTransactionManager;

    @Resource
    private TransactionDefinition transactionDefinition;


    /**
     * 无法获取手机号 除非是认证企业微信小程序
     *
     * @param loginBean
     * @return 返回token
     */
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody LoginBean loginBean) {
        System.out.println(loginBean);
        // 获取code值请求微信API或者openid
        String code = loginBean.getCode();
        String rawData = loginBean.getRawData();
        JSON rawJson = JSONUtil.parse(rawData);
        String nickName = rawJson.getByPath("nickName", String.class);
        String avatarUrl = rawJson.getByPath("avatarUrl", String.class);
        String response = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session?appid=" + APP_ID + "&secret=" + SECRET + "&js_code=" + code + "&grant_type=authorization_code");
        JSON parse = JSONUtil.parse(response);
        String openId = parse.getByPath("openid", String.class);
        User user = userMapper.findByOpenId(openId);
        String sign;
        if (user == null) {
            User u = new User();
            u.setOpenId(openId);
            u.setAvatarUrl(avatarUrl);
            u.setNickName(nickName);
            u.setTotalScore(0L);
            userMapper.insert(u);
            Long id = u.getId();
            sign = JwtUtil.sign(id);
        } else {
            Long id = user.getId();
            sign = JwtUtil.sign(id);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, sign);
    }

    @PostMapping("/userInfo")
    public ApiResult<User> userInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectByPrimaryKey(userId);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, user);
    }

    @PostMapping("/talk")
    public ApiResult<String> talk(HttpServletRequest request,String prompt) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        //选择模型
        json.set("model", "text-davinci-003");
        //添加我们需要输入的内容
        json.set("prompt", prompt);
        json.set("temperature", 0);
        json.set("max_tokens", 2048);
        json.set("top_p", 1);
        json.set("frequency_penalty", 0.0);
        json.set("presence_penalty", 0.6);
        HttpResponse response = HttpRequest.post("https://api.openai.com/v1/completions")
                .headerMap(headers, false)
                .bearerAuth("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")
                .body(String.valueOf(json))
                .timeout(5 * 60 * 1000)
                .execute();
        String body = response.body();
        JSON parse = JSONUtil.parse(body);
        String reply = parse.getByPath("choices[0].text", String.class);
        System.out.println(reply);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, reply);
    }

    @SneakyThrows
    @PostMapping("/test")
    @Transactional(rollbackFor = Exception.class)
    public void test(HttpServletRequest request) {
        User user = userMapper.selectByPrimaryKey(180L);
        throw new Exception("1111");
    }

    @PostMapping("/test/1")
    public void test1(HttpServletRequest request) {
        String age = initBean.getAge();
        String name = initBean.getName();
        System.out.println(name + age);
    }

    public static void main(String[] args) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        JSONObject json = new JSONObject();
        //选择模型
        json.set("model", "text-davinci-003");
        //添加我们需要输入的内容
        json.set("prompt", "如何获得女神的喜爱？");
        json.set("temperature", 0);
        json.set("max_tokens", 2048);
        json.set("top_p", 1);
        json.set("frequency_penalty", 0.0);
        json.set("presence_penalty", 0.6);

        HttpResponse response = HttpRequest.post("https://api.openai.com/v1/completions")
                .headerMap(headers, false)
                .bearerAuth("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")
                .body(String.valueOf(json))
                .timeout(5 * 60 * 1000)
                .execute();

        System.out.println(response.body());
        String body = response.body();
        JSON parse = JSONUtil.parse(body);
        String reply = parse.getByPath("choices[0].text", String.class);
        System.out.println();
    }


}
