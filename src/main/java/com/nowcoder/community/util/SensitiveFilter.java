package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct // 该bean初始化时，调用构造器之后调用该‘postconstruct' 方法
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt"); // 到target/classes下读文件
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword=reader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyword);
            }
        }catch(IOException e){
            logger.error("加载敏感词文件失败： " + e.getMessage());
        }
    }

    // 将敏感词添加到前缀树
    private void addKeyWord(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0; i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            //没有现成节点，新建节点，接到根节点之下
            if (subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指针移动到子节点
            tempNode = subNode;
            // 最后一个字符打标
            if(i==keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }

    }

    /**
     * 过滤敏感词，参数是带过滤文本，返回过滤后结果
     * @param text
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        /**
         * 指针1：tempNode 树上
         * 指针2：begin 串慢指针
         * 指针3：position 串快指针
         */

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);
            // 跳过符号
            if(isSymbol(c)){
                // 若指针1在根节点，更新过滤结果，1,2往前走
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在哪都往前走3
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // begin 开头的不是敏感词，更新过滤结果
                sb.append(text.charAt(begin));
                // 更新两个指针2， 3
                position = ++begin;
                // 重置指针1
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){ //完整的敏感词发现 begin 到 position之间
                sb.append(REPLACEMENT);
                begin = ++position;
                // 重置指针1
                tempNode = rootNode;
            }else{
                //下一个字符
                position++;
            }
        }
        // 3到尾， 2没到
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀树
    private class TrieNode{

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点(key下级节点，value下级节点字符)
        private Map<Character, TrieNode> subNodes= new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
