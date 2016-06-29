//package com.me2me.config;
//
//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
//import com.google.common.base.Charsets;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.web.accept.ContentNegotiationManager;
//import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//import org.springframework.web.servlet.view.JstlView;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Properties;
//
///**
// * 上海拙心网络科技有限公司出品
// * Author: 赵朋扬
// * Date: 2016/6/28.
// */
//@Configuration
//@EnableWebMvc
//public class SpringBootMvcConfig extends WebMvcConfigurerAdapter {
//
//
////    @Override
////    public void addInterceptors(InterceptorRegistry registry) {
////        AccessSecurityHandler accessSecurityHandler = new AccessSecurityHandler();
////        registry.addInterceptor(accessSecurityHandler).addPathPatterns("/**");
////        super.addInterceptors(registry);
////    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charsets.UTF_8);
//        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
//        fastJsonHttpMessageConverter.setCharset(Charsets.UTF_8);
//        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.valueOf("application/json;charset=UTF-8")));
//        converters.add(stringHttpMessageConverter);
//        converters.add(fastJsonHttpMessageConverter);
//        super.configureMessageConverters(converters);
//    }
//
//    @Bean
//    public ContentNegotiationManager contentNegotiationManager(){
//        ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
//        Properties properties = new Properties();
//        properties.put("xml","application/xml");
//        properties.put("json","application/json");
//        factoryBean.setMediaTypes(properties);
//        factoryBean.setIgnoreAcceptHeader(true);
//        factoryBean.setFavorPathExtension(true);
//        return factoryBean.getObject();
//    }
//
//    @Bean
//    public InternalResourceViewResolver viewResolver(){
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/WEB-INF/jsp");
//        viewResolver.setSuffix(".jsp");
//        viewResolver.setViewClass(JstlView.class);
//        return viewResolver;
//    }
//
////    @Bean
////    public CommonsMultipartResolver multipartResolver(){
////        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
////        multipartResolver.setMaxUploadSize(10485760);
////        return multipartResolver;
////    }
//
//}
