package com.mole.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/13 - 12 - 13 - 21:04
 */
@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TreeNode root = new TreeNode();

    //注解标识这是个初始化方法，在服务启动时这个方法被调用
    @PostConstruct
    public void init(){
        //构造树形
        try(
                //在target/classes下的文件路径,这个文件就在classes之下，所以直接写文件名
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //得到一个缓冲流，效率高
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
                String keyword;
                while ((keyword = reader.readLine()) != null){
                    //添加到前缀树
                    this.addKeyword(keyword);
                }
        }catch (IOException e){
            LOGGER.error("加载敏感词文件失败:" + e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TreeNode tmpNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            //试图去获取子节点
            TreeNode subNode = tmpNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TreeNode();
                tmpNode.addSubNode(c, subNode);
            }
            //指向子节点，进入下一轮循环
            tmpNode = subNode;
            //设置结束标识
            if(i == keyword.length() - 1){
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return  过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){ return null; }
        //指针1
        TreeNode tmpNode = root;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (begin < text.length()){
            //这个判断放止判断最后一部分疑似敏感词时position越界
            if(position >= text.length()){
                //以begin为开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                begin++;
                position = begin;
                //指针3重新指向根节点
                tmpNode = root;
                //判断是否超限
                if(begin >= text.length()){
                    break;
                }
            }
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(tmpNode == root){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tmpNode = tmpNode.getSubNode(c);
            if(tmpNode == null){
                //以begin为开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                begin++;
                position = begin;
                //指针3重新指向根节点
                tmpNode = root;
            }else if(tmpNode.isKeywordEnd()){
                //发现敏感词，将begin到position这段字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                position++;
                begin = position;
                //指针3重新指向根节点
                tmpNode = root;
            }else {
                //继续检查下一个字符
                position++;
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //判断是不是普通字符
        //0x2E80 - 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义前缀树内部类，不允许外界访问（基本只会在这个工具类中用到）
    private class TreeNode{

        //关键词结束的标识
        private boolean isKeywordEnd = false;

        //子节点(key是下级节点字符，value是下级节点)
        private Map<Character, TreeNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c, TreeNode node){
            subNodes.put(c, node);
        }
        //获取子节点
        public TreeNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
