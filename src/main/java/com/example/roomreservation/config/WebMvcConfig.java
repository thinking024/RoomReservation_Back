package com.example.roomreservation.config;

import com.example.roomreservation.common.JacksonObjectMapper;
import com.example.roomreservation.interceptor.AuthenticationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Resource
    private AuthenticationInterceptor interceptor;

//    private final String backStaticSource = "/backend/**";
//    private final String frontStaticSource = "/front/**";

    /*@Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        *//*registry.addResourceHandler(backStaticSource).addResourceLocations("classpath:/backend/");
        registry.addResourceHandler(frontStaticSource).addResourceLocations("classpath:/front/");*//*
        super.addResourceHandlers(registry);
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册后台拦截器");
        registry.addInterceptor(interceptor);
/*
        ArrayList<String> backPath = new ArrayList<>();
        backPath.add("/employee/**");
        registry.addInterceptor(backendInterceptor).addPathPatterns(backPath)
                .excludePathPatterns("/employee/login", "/category/list", "/dish/list", "/setmeal/list", "/common/download", "/setmeal/dish/**");
*/

    }

    /**
     * 扩展mvc框架的消息转换器
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);
    }
}