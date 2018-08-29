package team.redrock.web.short_url_creator.configs;

        import org.springframework.boot.web.servlet.FilterRegistrationBean;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import team.redrock.web.short_url_creator.filters.RedisFilter;

        import java.util.ArrayList;
        import java.util.List;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean RedisFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new RedisFilter());
        registrationBean.setOrder(1);
        List<String> urlList = new ArrayList<String>();
        urlList.add("/json");
        urlList.add("/creator");
        registrationBean.setUrlPatterns(urlList);
        registrationBean.setName("RedisFilter");
        return registrationBean;
    }
}
