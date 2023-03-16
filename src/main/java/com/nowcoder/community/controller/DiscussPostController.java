package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403, "你还没有登录"); //没有权限
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "发布成功！");

    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    // 不用responsebody，返回html
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post", post);
        // 根据userid查user详情
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 查分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论

        // 评论
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffSet(), page.getLimit());

        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList!=null){
            // 拿到每一条评论
            for(Comment comment : commentList){
                // view object
                Map<String, Object> commentVo = new HashMap<>();
                // view object 添加回复和作者
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList !=null){
                    for(Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 回复的作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标（当前帖子为空，回复评论为评论作者）
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                    commentVo.put("replys", replyVoList);

                    // 查询回复数量
                    int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("replyCount", replyCount);

                    commentVoList.add(commentVo);
                }
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }


}








