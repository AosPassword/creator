# creator
一个短网址生成器
请求的域名：``http://yangruixin.com:8080/json``</br>
参数</br>
oUrl:必填，填你准备要生成网址的对象,必须加 http:// or https:// ,例如http://www.baidu.com </br>
cUrl:选填，填你将会生成的短网址的后缀名，请填字符串，若不填，会自动生成</br>
keepTime:选填，单位毫秒，不可少于一天，不可多于一年，不填则自动生成，自动生成为30天</br>
返回值，如果创建成功则返回：例如：</br>
```
{
  "cUrl": "ehgjkasdf",
  "oUrl": "https://www.baisasdfdfgdu.com",
  "outTime": 1536531838720,
  "status": "success",
  "message": "URL created successfully"
}
你通过访问``http://yangruixin.com:8080/t/ cUrl可访问你创造的短域名
例如上面的就是``http://yangruixin.com:8080/t/ehgjkasdf``
outTime为过期时间，为时间戳，单位毫秒
如果你所想创建的oUrl已经存在则返回
{
  "cUrl": "baidu",
  "oUrl": "http://www.baidu.com",
  "outTime": 1538122931204,
  "status": "error",
  "message": "the original URL is exist"
}
返回被别人所创建的短域名和它的过期时间
如果你想创建的cUrl已经存在则返回
{
  "cUrl": "baidu",
  "id": 29,
  "oUrl": "http://www.baidu.com",
  "outTime": 1538122931204,
  "status": "error",
  "message": "the URL what you created is exist"
}
一个ip一秒只可使用该接口一次否则会返回：
{
  "timestamp": "2018-08-29T09:01:36.578+0000",
  "status": 500,
  "error": "Internal Server Error",
  "message": "超出访问次数限制",
  "path": "/json"
}
