//package com.hnup.common.webapi.config;
//import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//*
// * 如果没有手动配置 mybatis 数据源并明确指定 mapper 使用的 sqlsessionfactory，
// * 应该显式指定默认的一个 sqlsessionfactory，应为在引用其他依赖中可能注入多个 sqlsessionfactory。
// *
// * @author LiuHaoming
// * @date 2020/5/30 11:13
//
//
//@Configuration
//@MapperScan(basePackages = {"com.hnup.common.webapi.repository.mapper", "com.baomidou.springboot.mapper*"},
//        sqlSessionFactoryRef = "sqlSessionFactory")
//public class MybatisPlusConfiguration {
//
//*
//     * 启用分页
//     *
//     * @param
//     * @return com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor
//     * @author LiuHaoming
//     * @date 2020/4/3 17:19
//
//
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        return new PaginationInterceptor();
//    }
//}
//
