package team.redrock.web.short_url_creator.controllers;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import team.redrock.web.short_url_creator.annotations.RequestLimit;
import team.redrock.web.short_url_creator.beens.NetConnection;
import team.redrock.web.short_url_creator.mappers.ConnectionMapper;
import team.redrock.web.short_url_creator.utils.FormatUtil;
import team.redrock.web.short_url_creator.utils.HashUtil;

import java.util.Date;
import java.util.regex.Pattern;

@RestController
public class JsonController {
//    @Autowired
//    MessageBeen messageBeen;
    @Autowired
    ConnectionMapper connectionMapper;
    @Autowired
    FormatUtil formatUtil;
    @Autowired
    HashUtil hashUtil;
//    @RequestLimit(count = 5)
    @RequestMapping("/json")
    public JSONObject getJson(@RequestParam("oUrl") String oUrl,
                              @RequestParam(value = "cUrl",required = false) String cUrl,
                              @RequestParam(value = "keepTime",required = false)String keepTime){
        JSONObject jsonObject;
        long time;
        if (keepTime!=null){
            try {
                time = Integer.parseInt(keepTime);
                long up=31536000000l;
                long down=60 * 60 * 24 * 1000;
                if (time >= up|| time <= down) {
                    jsonObject = new JSONObject();
                    jsonObject.put("status", "error");
                    jsonObject.put("message", "time out");
                    return jsonObject;
                }
            }catch (Exception e){
                jsonObject = new JSONObject();
                jsonObject.put("status", "error");
                jsonObject.put("message", "time error");
                return jsonObject;
            }
        }else {
            time=60*60*24*30*1000;
        }
        boolean isMatch = Pattern.matches("^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", oUrl);
        if (!isMatch){
            if (!Pattern.matches("^https://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", oUrl)) {
                jsonObject = new JSONObject();
                jsonObject.remove("id");
                jsonObject.put("status", "error");
                jsonObject.put("message", "the URL what you send is error");
                return jsonObject;
            }
        }
        long nowtime=new Date().getTime();

        System.out.println(nowtime);

        NetConnection netConnection=connectionMapper.findAllByOUrl(oUrl);
        if (netConnection!=null){
            if (nowtime<=netConnection.getOutTime()){
                jsonObject=JSONObject.fromObject(netConnection);
                jsonObject.remove("id");
                jsonObject.put("status","error");
                jsonObject.put("message","the original URL is exist");
                return jsonObject;
            }else {
                connectionMapper.deleteConnectionByOUrl(oUrl);
            }
        }
        if (cUrl!=null) {
            if (!cUrl.isEmpty()) {
                NetConnection cUrlConnection = connectionMapper.findAllByCUrl(cUrl);
                if (cUrlConnection != null) {
                    if (nowtime <= cUrlConnection.getOutTime()) {
                        jsonObject = JSONObject.fromObject(cUrlConnection);
                        jsonObject.put("status", "error");
                        jsonObject.put("message", "the URL what you created is exist");
                        return jsonObject;
                    }else {
                        connectionMapper.deleteConnectionByCUrl(cUrl);
                    }
                }
            }
        }else {
            String md5Str=hashUtil.hash(oUrl+nowtime,"MD5");
            cUrl=md5Str.substring(0,7);
            String flag= connectionMapper.findOUrlByCUrl(cUrl);
            if (flag!=null){
                cUrl=hashUtil.hash(oUrl+nowtime,"SHA1").substring(0,7);
            }
        }
        NetConnection createdConnection=new NetConnection();
        createdConnection.setcUrl(cUrl);
        createdConnection.setoUrl(oUrl);
        createdConnection.setOutTime(nowtime+time);
        try{
            connectionMapper.insertConnection(createdConnection);
            jsonObject=JSONObject.fromObject(createdConnection);
            jsonObject.remove("id");
            jsonObject.put("status","success");
            jsonObject.put("message","URL created successfully");
            return jsonObject;
        }catch (Exception e){
            jsonObject =new JSONObject();
            jsonObject.put("status","error");
            jsonObject.put("message","unknown error,please tell the administrator about this bug");
            e.printStackTrace();
        }
        jsonObject =new JSONObject();
        jsonObject.put("status","error");
        jsonObject.put("message","unknown error,please tell the administrator about this bug");
        return jsonObject;
    }
}
