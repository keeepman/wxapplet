package com.ymt.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.ymt.advice.ApiResult;
import com.ymt.advice.ResultCodeEnum;
import com.ymt.base.NormalConstant;
import com.ymt.bean.MediaInfo;
import com.ymt.domain.*;
import com.ymt.mapper.*;
import com.ymt.utils.RedisUtils;
import com.ymt.utils.mediaUtils;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import org.springframework.beans.BeanUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @Author mintaoyu
 * Date on 2020-04-03  15:48
 */
@RestController
@Slf4j
public class MediaController {

    public static final String FILE_KEY = "previewList";

    public static MinioClient client;

    @Resource
    private MediaBaseInfoMapper baseInfoMapper;

    @Resource
    private MediaUrlMapper mediaUrlMapper;
    @Resource
    private MediaBaseInfoMapper mediaBaseInfoMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ListTypeMapper listTypeMapper;

    @Resource
    private AuditMapper auditMapper;

    @Resource
    private RedisUtils redisUtils;

    static {
        try {
            client = new MinioClient("http://115.28.105.227:8888/", "minioadmin", "minioadmin");
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/ai")
    public void ai(String prompt, String user) {
        OpenAiService service = new OpenAiService("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");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .maxTokens(2048)
                .temperature(0.9)
                .user(user)
                .echo(true)
                .build();
        List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
        System.out.println();
    }

    /**
     * 展示所有任务列表
     */
    @GetMapping("/showList")
    public ApiResult<List<ListType>> showList() {
        List<ListType> types = listTypeMapper.findAll();
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, types);
    }


    /**
     * 保存资源基本信息
     */
    @PostMapping("/saveBasicInformation")
    public ApiResult<String> saveBasicInformation(@RequestBody MediaBaseInfo info, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String id = IdUtil.objectId();
        info.setId(id);
        info.setUserId(userId);
        baseInfoMapper.insert(info);
        Audit audit = new Audit();
        audit.setSubTime(LocalDateTimeUtil.formatNormal(LocalDateTimeUtil.now()));
        audit.setMediaBaseInfoId(id);
        audit.setUserId(userId);
        audit.setStatus(NormalConstant.NOT_REVIEWED);
        audit.setTypeId(info.getTypeId());
        auditMapper.insert(audit);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, id);
    }

    /**
     * 记得一定要设置policy政策
     * *  ----->a readandwrite
     *
     * @param request
     */
    @PostMapping("/uploadFile")
    public ApiResult uploadFile(HttpServletRequest request, String mediaBaseInfoId) {
        System.out.println("进入uploadFile");
        MultiValueMap<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        if (fileMap.containsKey(FILE_KEY)) {
            try {
                List<MultipartFile> fileList = fileMap.get(FILE_KEY);
                for (MultipartFile file : fileList) {
                    // 图片名称
                    String fileName = file.getOriginalFilename();
                    // 上传文件
                    // 检查存储桶是否已经存在
                    boolean isExist = client.bucketExists("wxapplets");
                    if (!isExist) {
                        // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                        client.makeBucket("wxapplets");
                    }
                    // 使用putObject上传一个文件到存储桶中。
                    client.putObject("wxapplets", fileName, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
                    String url = client.getObjectUrl("wxapplets", fileName);
                    // 判断资源类型
                    String[] split = url.split("\\.");
                    String TypeName = split[split.length - 1];
                    Integer mediaType = mediaUtils.judgeType(TypeName);
                    if (mediaType != null) {
                        MediaUrl mediaUrl = new MediaUrl();
                        mediaUrl.setUrl(url);
                        mediaUrl.setMediaBaseInfoId(mediaBaseInfoId);
                        mediaUrlMapper.insert(mediaUrl);
                        MediaBaseInfo info = baseInfoMapper.selectByPrimaryKey(mediaBaseInfoId);
                        info.setMediaType(mediaType);
                        baseInfoMapper.updateByPrimaryKeySelective(info);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        } else {
            return ApiResult.errorWith(ResultCodeEnum.IMG_UPLOAD_FAIL);
        }
    }

    /**
     * @param page
     * @param pageSize
     * @param mediaType 0 图片 1 视频
     * @return
     */
    @GetMapping("/showMedia")
    public ApiResult<List<MediaInfo>> showMedia(int page, int pageSize, Integer mediaType,Long userId, HttpServletRequest request) {
        //Long userId = (Long) request.getAttribute("userId");
        User user = userMapper.selectByPrimaryKey(userId);
        Long bindId = user.getBindId();
        PageHelper.startPage(page, pageSize);
        List<MediaBaseInfo> infos = mediaBaseInfoMapper.findAllByMediaTypeAndUserId(mediaType, userId, bindId);
        List<MediaInfo> list = new ArrayList<>();
        for (MediaBaseInfo info : infos) {
            MediaInfo mediaInfo = new MediaInfo();
            BeanUtils.copyProperties(info, mediaInfo);
            String id = info.getId();
            List<MediaUrl> mediaUrlList = mediaUrlMapper.findAllByMediaBaseInfoId(id);
            mediaInfo.setMediaUrls(mediaUrlList);
            list.add(mediaInfo);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, list);
    }

    //@SneakyThrows
    //@PostMapping("/getBase")
    //public void getBase(@RequestParam("fileName") List<MultipartFile> files) {
    //    String bucketName = "img";
    //    log.info(String.valueOf(files.size()));
    //    for (MultipartFile file : files) {
    //        //// 图片名称
    //        String fileName = file.getOriginalFilename();
    //        // 上传文件
    //        // 检查存储桶是否已经存在
    //        boolean isExist = client.bucketExists(bucketName);
    //        if (!isExist) {
    //            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
    //            client.makeBucket(bucketName);
    //        }
    //        // 使用putObject上传一个文件到存储桶中。
    //        client.putObject(bucketName, fileName, file.getInputStream(), file.getInputStream().available(), "application/octet-stream");
    //        String url = client.getObjectUrl(bucketName, fileName);
    //        // 判断资源类型
    //        // 保存图片
    //        log.info(url);
    //
    //    }
    //}


    @SneakyThrows
    @PostMapping("/getBaseImage")
    public void getBase(HttpServletRequest request) {
        String bucketName = "secret";
        String json = request.getParameter("base64");
        if (json != null) {
            List<String> baseUrlList = JSONUtil.toList(json, String.class);
            for (String baseUrl : baseUrlList) {
                // 将base64转图片
                File file = Base64.decodeToFile(baseUrl, FileUtil.createTempFile());
                String absolutePath = file.getAbsolutePath();
                System.out.println("存储路径为->" + absolutePath);
                //// 图片名称
                String fileName = file.getName().replace(".tmp", ".jpeg");
                // 上传文件
                // 检查存储桶是否已经存在
                boolean isExist = client.bucketExists(bucketName);
                if (!isExist) {
                    // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                    client.makeBucket(bucketName);
                }
                // 使用putObject上传一个文件到存储桶中。
                client.putObject(bucketName, fileName, FileUtil.getInputStream(file), FileUtil.getInputStream(file).available(), "application/octet-stream");
                String url = client.getObjectUrl(bucketName, fileName);
                System.out.println(url);
            }
        } else {
            System.out.println("json为null");
        }
    }

    @SneakyThrows
    @GetMapping("/del")
    public void del() {
        String bucketName = "img";
        boolean isExist = client.bucketExists(bucketName);
        if (isExist) {
            try {
                // 递归列举某个bucket下的所有文件，然后循环删除
                Iterable<Result<Item>> iterable = client.listObjects(bucketName);
                for (Result<Item> itemResult : iterable) {
                    Item item = itemResult.get();
                    client.removeObject(bucketName, item.objectName());
                }
            } catch (Exception e) {

            }
        }
    }

    @SneakyThrows
    @GetMapping("/getAllImage")
    public List<String> getAllImage() {
        String bucketName = "img";
        List<String> urls = new ArrayList<>();
        boolean isExist = client.bucketExists(bucketName);
        if (isExist) {
            try {
                // 递归列举某个bucket下的所有文件，然后循环删除
                Iterable<Result<Item>> iterable = client.listObjects(bucketName);
                for (Result<Item> itemResult : iterable) {
                    String name = itemResult.get().objectName();
                    name = "http://115.28.105.227:8888/" + bucketName + "/" + name;
                    urls.add(name);
                }
                return urls;
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static void main(String[] args) {
        int a = 0;
        int b = a++;
        int c = a;
        System.out.println(b);
        System.out.println(c);
    }
}
