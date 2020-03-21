package com.mall.adminweb;

import com.mall.adminweb.util.MD5Util;


/**
 * @author zheng haijain
 * @createTime 2020-03-21 15:43
 * @description
 */
public class UtilTest {

    public static void main(String[] args) {
        Integer pwd = 123;
        System.out.println(MD5Util.MD5Encode(pwd.toString(), "utf-8"));
    }

}
