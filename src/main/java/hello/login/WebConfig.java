package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

// 스프링 인터셉트 등록 위해 implements WebMvcConfigurer 추가
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver()); // 핸들러 메서드의 파라미터에 @Login 이 붙어있고 타입이 Member 인 경우에만 실행
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor()) // 인터셉터 등록
                .order(1) // 인터셉터 순서 지정
                .addPathPatterns("/**") // 모든 요청에 인터셉터 적용
                .excludePathPatterns("/css/**", "/*.ico", "/error"); // 전체 경로 다 되지만 인터셉터 제외할 요청
        // /css/** -> css 디렉토리 하위에 있는 모든 파일
        // /*.ico -> 확장자가 ico 인 파일
        // /error -> error 페이지

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**") // 모든 요청에 인터셉터 적용
                .excludePathPatterns("/", "/members/add", "/login", "/logout", "/css/**", "/*.ico", "/error"); // 전체 경로 다 되지만 인터셉터 제외할 요청
    }

//    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter()); // 필터 등록
        filterRegistrationBean.setOrder(1); // 필터 순서 지정
        filterRegistrationBean.addUrlPatterns("/*"); // 모든 요청에 필터 적용
        return filterRegistrationBean;
    }

//    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter()); // 필터 등록
        filterRegistrationBean.setOrder(2); // 필터 순서 지정
        filterRegistrationBean.addUrlPatterns("/*"); // 모든 요청에 필터 적용
        return filterRegistrationBean;
    }

}
