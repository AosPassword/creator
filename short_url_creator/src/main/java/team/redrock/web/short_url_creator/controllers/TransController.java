package team.redrock.web.short_url_creator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import team.redrock.web.short_url_creator.beens.NetConnection;
import team.redrock.web.short_url_creator.mappers.ConnectionMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Controller
public class TransController {
    @Autowired
    ConnectionMapper connectionMapper;

    @RequestMapping("/t/**")
    public String trans(HttpServletRequest request,
                        HttpServletResponse response,
                        Map<String,String> map) throws IOException {
        String uri=request.getRequestURI();
        if (uri.length()<=3){
            map.put("reasonMap","你输入的网址不对，请确认后重试");
            return "error";
        }
        String target=uri.substring(3);
        NetConnection netConnection=connectionMapper.findAllByCUrl(target);
        long time=System.currentTimeMillis();
        if (time>=netConnection.getOutTime()){
            connectionMapper.deleteConnectionByCUrl(target);
            map.put("reasonMap","此网址已过期，如果您想继续访问请重新创建");
            return "error";
        }try {
            response.sendRedirect(netConnection.getoUrl());
        }catch (Exception e){
            e.printStackTrace();
            map.put("reasonMap","出现未知bug，请将您的情况向管理员反应");
            return "error";
        }
        return null;
    }

}
