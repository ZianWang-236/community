package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaservice;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return  alphaservice.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // request
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + " : " + value);
        }
        System.out.println(request.getParameter("code"));

        // response
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter writer = response.getWriter(); // 替代finally
                ) {
            writer.write("<h1>newcoder</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET 请求（默认）

    //  /student?current=1&limit=20  用get从url获取参数
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1")int current,// 默认值，是否必须
            @RequestParam(name = "limit", required = false, defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit);
        return (current + " some students " + limit);
    }

    // /student/123 直接获取路径中参数
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return (id + " a students");
    }

    // POST
    // 静态网页
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 相应HTML数据

    @RequestMapping(path = "/teacher", method = RequestMethod.GET) //没有注解默认返回HTML
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        mav.setViewName("/demo/view"); // thymeleaf不需要写html后缀
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET) //没有注解默认返回HTML
    public String getSchool(Model model){
        model.addAttribute("name", "beijing uni");
        model.addAttribute("age", 80);
//        System.out.println("/demo/view");
        return "/demo/view";
    }

    // 相应JSON(异步请求)
    // json 兼容java 和 js

    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","zhansan");
        emp.put("age", 24);
        emp.put("salary", 8000.00);
        return emp;
    }

    // 多jason返回数据
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","zhansan");
        emp.put("age", 24);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","lisi");
        emp.put("age", 26);
        emp.put("salary", 1000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","wanger");
        emp.put("age", 29);
        emp.put("salary", 10000.00);
        list.add(emp);

        return list;
    }

}









