package team.redrock.web.short_url_creator.filters;


import org.apache.catalina.filters.RemoteIpFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import team.redrock.web.short_url_creator.utils.HttpRequestUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
public class RedisFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    RedisTemplate redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        ServletContext sc = request.getSession().getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sc);
        if(ctx != null && ctx.getBean("redisTemplate") != null && redisTemplate == null) {
            redisTemplate = (RedisTemplate) ctx.getBean("redisTemplate");
        }

        String ip= HttpRequestUtil.getIpAddr(request);
        System.out.println("ip->"+ip);
        String url = request.getRequestURL().toString();

        long count = redisTemplate.opsForValue().increment(ip,1);
        //刚创建
        if (count == 1) {
            //设置1分钟过期
            redisTemplate.expire(ip,3000, TimeUnit.MILLISECONDS);
        }
        if (count > 2) {
            logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + 2 + "]");
            response.getWriter().write("超出访问次数限制");
            response.setHeader("refresh", "1;url=" + request.getContextPath());
            throw new RuntimeException("超出访问次数限制");
        }else {
            filterChain.doFilter(request,response);
        }
    }

    @Override
    public void destroy() {

    }
}
