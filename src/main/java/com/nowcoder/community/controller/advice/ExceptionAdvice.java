//package com.nowcoder.community.controller.advice;
//
//import com.nowcoder.community.util.CommunityUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//@ControllerAdvice(annotations = Controller.class) // 扫描带有controller注解的方法
//public class ExceptionAdvice {
//
//    @Autowired
//    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
//
//    @ExceptionHandler({Exception.class})
//    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        logger.error("服务器异常" + e.getMessage());
//        for(StackTraceElement element : e.getStackTrace()){
//            logger.error(element.toString());
//        }
//
//        // 判断是普通请求还是异步请求，普通请求返回500页面，异步请求返回JSON
//        String xRequestedWith = request.getHeader("x-requested-with");
//        if("XMLHttpRequest".equals(xRequestedWith)){
//            // 异步请求
//            response.setContentType("application/plain;charset=utf-8");
//            PrintWriter writer = response.getWriter();
//            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
//        }else{// 同步请求
//            response.sendRedirect(request.getContextPath() + "/error");
//        }
//    }
//
//}
