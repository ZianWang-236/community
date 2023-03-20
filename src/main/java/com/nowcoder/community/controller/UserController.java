package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

//    @Autowired
//    private CommunityUtil communityUtil;

    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage==null){
            model.addAttribute("error", "您还没有上传图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        // 生成文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存储路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常", e);
        }
        // 更新当前用户头像路径
        // http://localhost:8000/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

//    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword1, String newPassword2, Model model){
        if(oldPassword==null){
            model.addAttribute("oldError", "请输入旧密码");
            return "/site/setting";
        }
        if(newPassword1==null || newPassword2==null){
            model.addAttribute("oldError", "请输入新密码");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        // 旧密码是否和数据库中的相符(md5加密后)
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!Objects.equals(user.getPassword(), oldPassword)){
            model.addAttribute("oldError", "旧密码输入错误");
            return "/site/setting";
        }
        // 两次输入是否相同
        if(!Objects.equals(newPassword1, newPassword2)){
            model.addAttribute("newError", "新密码两次输入不相同");
            return "/site/setting";
        }
        // 新密码不能等于旧密码
        if(Objects.equals(user.getPassword(), CommunityUtil.md5(newPassword1 + user.getSalt())) || Objects.equals(user.getPassword(), CommunityUtil.md5(newPassword2 + user.getSalt()))){
            model.addAttribute("newError", "新密码不能和旧密码相同");
            return "/site/setting";
        }
        // 都符合条件，可以更新（md5加密后）
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword1 + user.getSalt()));
        return "redirect:/index";

    }


    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
//        System.out.println(fileName);
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
         response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName); // 自动执行finally方法，必须要有close方法
                OutputStream os = response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
           logger.error("读取头像失败" + e.getMessage());
        }
    }

    // 主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        return "/site/profile";
    }

}




