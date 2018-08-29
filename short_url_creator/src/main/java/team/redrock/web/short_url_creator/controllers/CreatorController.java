package team.redrock.web.short_url_creator.controllers;


import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import team.redrock.web.short_url_creator.annotations.RequestLimit;
import team.redrock.web.short_url_creator.beens.NetConnection;
import team.redrock.web.short_url_creator.mappers.ConnectionMapper;
import team.redrock.web.short_url_creator.utils.FormatUtil;
import team.redrock.web.short_url_creator.utils.HashUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
@Controller
public class CreatorController {

    @Autowired
    ConnectionMapper connectionMapper;
    @Autowired
    FormatUtil formatUtil;
    @Autowired
    HashUtil hashUtil;
////    @Autowired
////    MessageBeen messageBeen;
//    @RequestLimit(count = 5)
    @RequestMapping(value = "/creator")
    public String creator(@RequestParam("oUrl") String oUrl,
                          @RequestParam("keepTime")String keepTime,
                          @RequestParam(value = "cUrl",required = false)String cUrl,
                          Map<String,Object> map){
        Map<String,String> hashMap=new HashMap<>();
        long nowtime=new Date().getTime();
        long time;
        if (keepTime!=null){
            time=Long.parseLong(keepTime);
            time=time*1000;
            long up=31536000000l;

            long down=60 * 60 * 24 * 1000;
            try {
                if (time > up || time < down) {
                    System.out.println("up--->"+up);
                    System.out.println("xiagao time error");
                    System.out.println("down->"+down);
                    map.put("reasonMap","time error");
                    return "error";
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("e.time error");
                map.put("reasonMap","time error");
                return "error";
            }
        }else {
            time=60*60*24*30*1000;
        }
        boolean isMatch = Pattern.matches("^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", oUrl);
        if (!isMatch){
            if (!Pattern.matches("^https://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", oUrl)) {
                System.out.println("url. the URL what you send is error");
                map.put("reasonMap", "the URL what you send is error");
                return "error";
            }
        }

        NetConnection netConnection=connectionMapper.findAllByOUrl(oUrl);
        if (netConnection!=null){
            if (nowtime<=netConnection.getOutTime()){
                hashMap.put("message","the original URL is exist");
                hashMap.put("oUrl",netConnection.getoUrl());
                hashMap.put("cUrl",netConnection.getcUrl());
                hashMap.put("outTime",formatUtil.timeStamptoDate(netConnection.getOutTime()));
                map.put("result",hashMap);
                return "created";
            }else {
                connectionMapper.deleteConnectionByOUrl(oUrl);
            }
        }
        if (cUrl!=null) {
            if (!cUrl.isEmpty()) {
                NetConnection cUrlConnection = connectionMapper.findAllByCUrl(cUrl);
                if (cUrlConnection != null) {
                    if (nowtime <= cUrlConnection.getOutTime()) {
                        hashMap.put("message", "the URL what you created is exist");
                        hashMap.put("oUrl", cUrlConnection.getoUrl());
                        hashMap.put("cUrl", cUrlConnection.getcUrl());
                        hashMap.put("outTime", formatUtil.timeStamptoDate(cUrlConnection.getOutTime()));
                        map.put("result", hashMap);
                        return "created";
                    }
                }
            }
        }else {
            String md5Str=hashUtil.hash(oUrl+nowtime,"MD5");
            cUrl=md5Str.substring(0,7);
            String flag= connectionMapper.findOUrlByCUrl(cUrl);
            if (!flag.isEmpty()){
                cUrl=hashUtil.hash(oUrl+nowtime,"SHA1").substring(0,7);
            }
        }
        NetConnection createdConnection=new NetConnection();
        createdConnection.setcUrl(cUrl);
        createdConnection.setoUrl(oUrl);
        createdConnection.setOutTime(nowtime+time);
        String outTime=formatUtil.timeStamptoDate(nowtime+time);
        try{
            connectionMapper.insertConnection(createdConnection);
            hashMap.put("message","URL created successfully");
            hashMap.put("oUrl",createdConnection.getoUrl());
            hashMap.put("cUrl",createdConnection.getcUrl());
            hashMap.put("outTime",outTime);
            map.put("result",hashMap);
            return "created";
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("un unknown error,please tell the administrator about this ");
            map.put("reasonMap","unknown error,please tell the administrator about this error");
            return "error";
        }
    }
}
