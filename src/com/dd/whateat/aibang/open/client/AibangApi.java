package com.dd.whateat.aibang.open.client;

import org.apache.http.HttpHost;
import org.apache.http.message.BasicNameValuePair;

import com.dd.whateat.aibang.open.client.exception.AibangException;
import com.dd.whateat.aibang.open.client.exception.AibangIOException;
import com.dd.whateat.aibang.open.client.exception.AibangServerException;

/**
 * 封装了爱帮的开放API. 开发者应当使用创建该类的对象访问爱帮的服务.
 * 
 * <ul>
 * 说明：
 * <li>我们没有将AibangApi实现为Singleton，如果开发者有必要，可以自行封装Singleton对象使用.
 * </li>
 * <li>成员方法命名保持和API文档一致，方便开发者对应查阅. </li>
 * <li>封装的成员方法某些API的参数较多，可能导致使用不太方便。开发者可以根据自己的需求进行二次封装，
 *      例如减少参数的数量，或者使用对象包装参数.
 * </li>
 * <li>返回类型是字符串，没有进行解析，开发者可以根据项目的需要选用xml或者json返回格式，并使用
 * xml或者json类库进行解析.
 * </li>
 * <li>如果下面对各个API的描述和爱帮网站的描述不一致，请以网站为准.
 * </li>
 * <ul>
 */
public class AibangApi {
    
    /**
     * 使用apiKey创建AibangApi对象，返回值为json格式.
     * @param apiKey 开发者申请的apiKey
     */
    public AibangApi(String apiKey) {
        this(apiKey, "json");
    }
    
    /** 
     * 使用apiKey和指定的返回格式创建AibangApi对象.
     * @param apiKey 开发者申请的apiKey
     * @param format 返回格式，xml或者json
     */
    public AibangApi(String apiKey, String format) {
        client = new AibangHttpClient(SDK, HOST, apiKey, format);
    }
    
    /**
     * 设置网络请求代理.
     * @param proxy 代理服务器
     */
    public void setProxy(HttpHost proxy) {
        client.setProxy(proxy);
    }
    
    /**
     * 搜索接口. 商户搜索接口根据指定的城市、地址（或坐标）和关键词等参数查询满足条件的商户，返回
     * 商户列表。如果不指定地址，则在全市搜索。若不指定关键字，则返回地址周边的商户。地址和关键字
     * 必须至少有一个。最多返回前300个满足指定搜索条件的商户。
     * 
     * <p>
     * 参数中地址a和经纬度lng|lat都可以指定中心点，如果只指定了a而没有lng|lat，则在a指定的地址
     * 范围内查询，搜索引擎会根据地址a计算对应的中心点；如果只指定了lng|lat而没有a，则以lng|lat
     * 指定的经纬度作为中心点；如果同时指定a和lng|lat，则在地址a范围内检索，同时把lng|lat指定的
     * 中心点作为a对应的中心点。
     * </p>
     * 
     * @param city  城市，不能为空
     * @param a     地址名称，地址分为区域地址（如海淀区）和点地址（如安全大厦），区域地址的查询
     *              不受距离参数as的限制；若是点地址，则以此作为中心点，以as为半径的区域内搜索
     * @param lng   地址经度
     * @param lat   地址纬度
     * @param q     关键词，例如“餐馆”、“KTV”
     * @param cate  类别，限于“爱帮类别列表”
     * @param rc    排序方式，1：默认排序，根据综合因素排序；2：按距离排序；3：按可信度排序；
     *                      5：按热度排序；6：按照星级排序；
     * @param as    距离限制，0：无限制，默认；负数：建议限制距离为其绝对值，但搜索引擎若认为
     *                      结果不好则扩展至全市搜索；正数：强制在此距离范围内；
     * @param from  返回结果在总结果的起始位置。最小为1，默认为1；（最多为300）
     * @param to    返回结果在总结果的结束位置。to和from组成闭区间[from, to]。
     *              默认from+9；（最多为300）
     * @return 搜索的商户列表
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String search(String city, String a, Float lng, Float lat, String q,
            String cate, Integer rc, Integer as, Integer from, Integer to) 
            throws AibangException {
        return client.get("/search", 
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("a", a),
                new BasicNameValuePair("lng", getFloatParam(lng)),
                new BasicNameValuePair("lat", getFloatParam(lat)),
                new BasicNameValuePair("q", q),
                new BasicNameValuePair("cate", cate),
                new BasicNameValuePair("rc", getIntegerParam(rc)),
                new BasicNameValuePair("as", getIntegerParam(as)),
                new BasicNameValuePair("from", getIntegerParam(from)),
                new BasicNameValuePair("to", getIntegerParam(to)));
    }
    
    /**
     * 地址定位接口，根据地址名称查询该地址的经纬度.
     * @param city 城市，不能为空
     * @param addr 地址，如五道口、北苑路20号
     * @return 地址的经纬度
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String locate(String city, String addr) throws AibangException {
        return client.get("/locate", 
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("addr", addr));
    }
    
    /**
     * 商户信息查询.
     * @param bid 商户编号，不能为空
     * @return 指定商户的信息
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String biz(String bid) throws AibangException {
        return client.get("/biz/" + bid);
    }
    
    /**
     * 商户点评查询. 一次最多返回30条记录
     * @param bid   商户编号，不能为空
     * @param from  返回结果在总结果的起始位置，最小为1，默认为1；
     * @param to    返回结果在总结果的起始位置，默认from+9，最多为from+29(30条记录)。结果
     *              为from和to组成的闭区间[from, to]。
     * @return 商户点评列表，每次最多返回30条记录
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String comments(String bid, Integer from, Integer to) 
            throws AibangException {
        return client.get("/biz/" + bid + "/comments", 
                new BasicNameValuePair("from", getIntegerParam(from)),
                new BasicNameValuePair("to", getIntegerParam(to)));
    }
    
    /**
     * 对指定商户发表点评接口.
     * @param uname     用户名，不能为空
     * @param bid       商户编号，不能为空
     * @param score     对该商户的综合打分，介于[1,5]之间的整数，不能为空
     * @param cost      人均消费，单位：元
     * @param content   点评内容，字节数不大于50KB，不能为空
     * @return
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String postComment(String uname, String bid, Integer score, Integer cost, 
            String content) throws AibangException {
        return client.post("/biz/" + bid + "/comment", 
                new BasicNameValuePair("uname", uname),
                new BasicNameValuePair("score", getIntegerParam(score)),
                new BasicNameValuePair("cost", getIntegerParam(cost)),
                new BasicNameValuePair("content", content));
    }
    
    /**
     * 商户图片查询. 一次最多返回30条记录
     * @param bid   商户编号，不能为空
     * @param from  返回结果在总结果的起始位置，最小为1，默认为1；
     * @param to    返回结果在总结果的起始位置，默认from+9，最多为from+29(30条记录)。结果
     *              为from和to组成的闭区间[from, to]。
     * @return 商户图片列表，每次最多返回30条记录
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String pics(String bid, Integer from, Integer to) 
            throws AibangException {
        return client.get("/biz/" + bid + "/pics", 
                new BasicNameValuePair("from", getIntegerParam(from)),
                new BasicNameValuePair("to", getIntegerParam(to)));
    }
    
    /**
     * 对指定商户上传图片.
     * @param uname 用户名，不能为空
     * @param bid   商户编号，不能为空
     * @param title 图片标题，字节数不大于60B
     * @param data  上传图片数据，不能为空，图片的数据部分，字节数不大于5MB
     * @return
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String postPic(String uname, String bid, String title, byte[] data) 
            throws AibangException {
        return client.post("/biz/" + bid + "/pic", 
                new AibangHttpClient.NameByteArrayPair("data", null, data),
                new BasicNameValuePair("uname", uname),
                new BasicNameValuePair("title", title));
    }
    
    /**
     * 修改商户信息接口.
     * 
     * @param uname     用户名称，不能为空或者空字符串
     * @param bid       商户编号. 不能为空
     * @param name      商户名称.
     * @param status    商户状态，0：正常营业；1：停业；2：暂停营业； 
     * @param tel       商户电话，多个电话使用半角分号;分隔
     * @param city      商户所在城市
     * @param county   商户所在区县
     * @param addr      商户地址
     * @param desc      商户简介
     * @param lng       商户经度
     * @param lat       商户纬度
     * @param cate      商户经营类别，多个类别用半角分号分隔，可能值参见商户分类表
     * @param worktime  商户经营时间
     * @param cost      人均消费，单位：元
     * @param site_url  商户官方网站url
     * @return 
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String modifyBiz(String uname, String bid, String name, Integer status, 
            String tel, String city, String county, String addr, 
            String desc, Float lng, Float lat, String cate, String worktime, 
            String cost, String site_url) throws AibangException {
        return client.post("/biz/" + bid, 
                new BasicNameValuePair("uname", uname),
                new BasicNameValuePair("name", name),
                new BasicNameValuePair("status", getIntegerParam(status)),
                new BasicNameValuePair("tel", tel),
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("country", county),
                new BasicNameValuePair("addr", addr),
                new BasicNameValuePair("desc", desc),
                new BasicNameValuePair("lng", getFloatParam(lng)),
                new BasicNameValuePair("lat", getFloatParam(lat)),
                new BasicNameValuePair("cate", cate),
                new BasicNameValuePair("worktime", worktime),
                new BasicNameValuePair("cost", cost),
                new BasicNameValuePair("site_url", site_url));
    }
    
    /**
     * 添加商户接口.
     * 
     * @param uname     用户名，不能为空
     * @param name      商户名称，不能为
     * @param tel       商户电话，多个电话使用半角分号;分隔，不能为空
     * @param city      商户所在城市，不能为空
     * @param county   商户所在区县，不能为空
     * @param cate      商户经营类别，多个类别用半角分号分隔，可能值参见商户分类表，不能为空
     * @param addr      商户地址
     * @param desc      商户简介，不能超过50KB
     * @param lng       商户经度
     * @param lat       商户纬度
     * @param worktime  商户经营时间
     * @param cost      人均消费，单位：元
     * @param site_url  商户官方网站url
     * @return 
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String addBiz(String uname, String name, 
            String tel, String city, String county, String cate, String addr, 
            String desc, Float lng, Float lat, String worktime, 
            String cost, String site_url) throws AibangException {
        return client.post("/biz", 
                new BasicNameValuePair("uname", uname),
                new BasicNameValuePair("name", name),
                new BasicNameValuePair("tel", tel),
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("county", county),
                new BasicNameValuePair("cate", cate),
                new BasicNameValuePair("addr", addr),
                new BasicNameValuePair("desc", desc),
                new BasicNameValuePair("lng", getFloatParam(lng)),
                new BasicNameValuePair("lat", getFloatParam(lat)),
                new BasicNameValuePair("worktime", worktime),
                new BasicNameValuePair("cost", cost),
                new BasicNameValuePair("site_url", site_url));
    }
    
    /**
     * 公交换乘接口. 该接口根据指定的起点和终点查询公交换乘方案. 
     * @param city          城市名，不能为空
     * @param start_addr    起点地址，
     * @param end_addr      终点地址
     * @param start_lng     起点经度
     * @param start_lat     起点纬度
     * @param end_lng       终点经度
     * @param end_lat       终点纬度
     * @param rc            排序方式。默认是0。0：综合排序，1：换乘次数最少，2：步行距离最短，
     *                          3：时间最少，4：距离最短，5：地铁优先
     * @param count         最大返回记录数量，默认为10，最大值不能超过10
     * @param with_xys      返回内容是否包含坐标信息，默认为0，不包含各个站点和路线的坐标信息，
     *                      如果为1，则包括
     * @return
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String bus(String city, String start_addr, String end_addr,
            Float start_lng, Float start_lat, Float end_lng, Float end_lat,
            Integer rc, Integer count, Integer with_xys) 
            throws AibangException {
        return client.get("/bus/transfer", 
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("start_addr", start_addr),
                new BasicNameValuePair("end_addr", end_addr),
                new BasicNameValuePair("start_lng", getFloatParam(start_lng)),
                new BasicNameValuePair("start_lat", getFloatParam(start_lat)),
                new BasicNameValuePair("end_lng", getFloatParam(end_lng)),
                new BasicNameValuePair("end_lat", getFloatParam(end_lat)),
                new BasicNameValuePair("rc", getIntegerParam(rc)),
                new BasicNameValuePair("count", getIntegerParam(count)),
                new BasicNameValuePair("with_xys", getIntegerParam(with_xys)));
    }
    
    /**
     * 公交线路查询. 该接口根据关键字查询匹配的线路.
     * @param city      城市名称，不能为空
     * @param q         查询线路，不能为空
     * @param with_xys  返回内容是否包含坐标信息，默认为0，不包含各个站点和路线的坐标信息，
     *                      如果为1，则包括
     * @return
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String busLines(String city, String q, Integer with_xys) 
            throws AibangException {
        return client.get("/bus/lines", 
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("q", q),
                new BasicNameValuePair("with_xys", getIntegerParam(with_xys)));
    }
    
    /**
     * 公交站点查询. 该接口根据关键字查询匹配的站点.
     * @param city  城市名称，不能为空
     * @param q     查询线路，不能为空
     * @return
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String busStats(String city, String q) throws AibangException {
        return client.get("/bus/stats", 
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("q", q));
    }
    
    /**
     * 公交周边站点查询. 该接口根据经纬度坐标查询其附近的站点.
     * @param city  城市名称，不能为空
     * @param lng   经度，不能为空
     * @param lat   纬度，不能为空
     * @param dist  距离，不能为空，单位为米
     * @return
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    public String busStatsXy(String city, Float lng, Float lat, Integer dist) 
            throws AibangException {
        return client.get("/bus/stats_xy", 
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("lng", getFloatParam(lng)),
                new BasicNameValuePair("lat", getFloatParam(lat)),
                new BasicNameValuePair("dist", getIntegerParam(dist)));
    }
    
    private String getFloatParam(Float value) {
        return value != null ? String.valueOf(value) : null;
    }
    
    private String getIntegerParam(Integer value) {
        return value != null ? String.valueOf(value) : null;
    }

    private static final String HOST = "http://openapi.aibang.com";
    private static final String SDK = "AibangAndroidOpenSdk_v1.0";
    private AibangHttpClient client;
}
