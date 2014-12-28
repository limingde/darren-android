package com.dd.whateat.aibang.open.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.TokenIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.dd.whateat.aibang.open.client.exception.AibangException;
import com.dd.whateat.aibang.open.client.exception.AibangIOException;
import com.dd.whateat.aibang.open.client.exception.AibangServerException;

/**
 * 网络请求类, 提供GET和POST请求功能. 基于Apache的HttpClient实现.
 * 该类的作用域为package，只由{@link AibangApi}进行使用.
 * 
 * 参考: dropbox的java sdk中的AbstractSession的实现.
 */
class AibangHttpClient {
    
    /**
     * 二进制数据参数类型. 用于上传文件的post请求中.
     */
    public static class NameByteArrayPair {
        public NameByteArrayPair(String name, String fileName, byte[] bytes) {
            this.name = name;
            this.fileName = fileName;
            this.bytes = bytes;
        }
        
        public String getName() {
            return name;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public byte[] getBytes() {
            return bytes;
        }
        
        private String name;
        private String fileName;
        private byte[] bytes;
    }
    
	/**
	 * 构造函数. 创建者需要设置服务的主机地址.
	 * @param ua 发送网络请求时的User-Agent值
	 * @param host 爱帮开放API的服务器地址, 包括scheme, 不包括尾部的/, 
	 *             例如http://openapi.aibang.com, 
	 * @param apiKey 开发者申请的apikey
	 * @param format 返回格式，xml或者json
	 */
	public AibangHttpClient(String ua, String host, String apiKey, 
	        String format) {
		this.host = host;
		this.apiKey = apiKey;
		this.format = format;
		this.httpClient = createHttpClient(ua);
	}
	
	/**
	 * 设置网络请求代理.
	 * @param proxy
	 */
	public void setProxy(HttpHost proxy) {
	    this.proxy = proxy;
	}
	
	/**
	 * 向爱帮服务器发送GET请求，返回响应内容字符串.
	 * @param path     请求路径，需要以/开头
	 * @param params   querystring参数列表
	 * @return 响应字符串.
	 * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
	 */
	public String get(String path, NameValuePair... params) 
	        throws AibangException {
		HttpGet httpGet = new HttpGet(createUrl(host, path, params));
		return execute(httpGet);
	}
	
	/**
	 * 向爱帮服务器发送POST请求，返回响应内容字符串. 传入的params放在消息体中，但是认证的apikey
	 * 仍然放在url中.
	 * @param path     请求路径，需要以/开头
	 * @param params   参数列表，通过urlencode后放在发送的请求消息体中
	 * @return 响应字符串
	 * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
	 */
    public String post(String path, NameValuePair... params)
            throws AibangException {
        HttpPost httpPost = new HttpPost(createUrl(host, path));
        try {
            if (DEBUG) {
                try {
                    System.out.println(EntityUtils
                            .toString(new UrlEncodedFormEntity(
                                    stripNulls(params), HTTP.UTF_8)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(params),
                    HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new AibangException(
                    "post message body encode to utf8 failed", e);
        }
        return execute(httpPost);
    }
	
	/**
	 * 向爱帮服务器发送POST请求，含有上传的文件信息，使用multipart发送.
	 * @param path     请求路径，需要以/开头
	 * @param data     上传文件转换的二进制数据，在请求中的multipart文件名始终为unknown
	 * @param params   除了文件外的其它请求参数
	 * @return
	 * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
	 */
	public String post(String path, NameByteArrayPair byteArrayParam,
	        NameValuePair... params) 
	        throws AibangException {
        HttpPost httpPost = new HttpPost(createUrl(host, path));
        try {
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.STRICT, null, null);
            if (byteArrayParam.getBytes() != null) {
                reqEntity.addPart(byteArrayParam.getName(), new ByteArrayBody(
                        byteArrayParam.getBytes(), null));
            }
            for (NameValuePair param : params) {
                if (param.getValue() != null) {
                    reqEntity.addPart(
                            param.getName(),
                            new StringBody(param.getValue(), Charset
                                    .forName("UTF-8")));
                }
            }
            httpPost.setEntity(reqEntity);
        } catch (UnsupportedEncodingException e) {
            throw new AibangException(
                    "post message body encode to utf8 failed", e);
        }
        return execute(httpPost);
	}
	
	/**
	 * 根据参数构造完整的url地址，除了传入的参数，还会添加app_key和alt字段.
	 * @param host 主机地址, 包括scheme，不包括结尾的/，例如http://open.aibang.com
	 * @param path 路径，需要以/开头
	 * @param params querystring的参数列表
	 * @return 连接后的完整url地址
	 */
	private String createUrl(String host, String path, NameValuePair... params) {
	    List<NameValuePair> params_list = stripNulls(params);
        params_list.add(new BasicNameValuePair("app_key", apiKey));
        params_list.add(new BasicNameValuePair("alt", format));
	    String querystring = URLEncodedUtils.format(params_list, HTTP.UTF_8);
        StringBuilder query_builder = new StringBuilder();
        query_builder.append(host).append(path).append("?").append(querystring);
        if (DEBUG) {
            System.out.println(query_builder.toString());
        }
        return query_builder.toString();
	}
	
	/**
	 * 删除参数列表中的value为null参数.
	 * @param nameValuePairs
	 * @return
	 */
	private List<NameValuePair> stripNulls(NameValuePair... params) {
        List<NameValuePair> params_list = new ArrayList<NameValuePair>();
        for (int i = 0; i < params.length; i++) {
            NameValuePair param = params[i];
            if (param.getValue() != null) {
                params_list.add(param);
            }
        }
        return params_list;
    }
	
	/**
	 * 创建实际发送请求Apache的HttpClient对象.
	 * 参考: dropbox的java sdk的实现
	 * @param userAgent 发送网络请求的userAgent
	 * @return Apache的HttpClient对象.
	 */
    private HttpClient createHttpClient(String userAgent) {
	    // 设置连接参数
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 
                DEFAULT_TIMEOUT_MILLIS);
        HttpConnectionParams.setSoTimeout(httpParams, 
                DEFAULT_TIMEOUT_MILLIS);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        HttpProtocolParams.setUserAgent(httpParams, userAgent);
        
        // 设置连接池参数
        HttpParams connParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(connParams, 
                DEFAULT_MAX_CONNECTIONS);
        
        // 设置scheme方案
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // 创建连接管理器
        MyClientConnManager cm = new MyClientConnManager(connParams,
                schemeRegistry);
        
        // 基于自定义连接管理器、自定义Keep-Alive策略、自定义连接重用策略创建HttpClient
        DefaultHttpClient client = new DefaultHttpClient(cm, httpParams) {
            @Override
            protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
                return new MyKeepAliveStrategy();
            }

            @Override
            protected ConnectionReuseStrategy createConnectionReuseStrategy() {
                return new MyConnectionReuseStrategy();
            }
        };

        // 发送请求添加Accept-Encoding:gzip头，从而减少服务器返回数据的流量
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(
                    final HttpRequest request, final HttpContext context)
                        throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });

        // 处理gzip响应类型，进行解压操作
        client.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(final HttpResponse response,
                    final HttpContext context) throws HttpException,
                    IOException {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null) {
                        HeaderElement[] codecs = ceheader.getElements();
                        for (HeaderElement codec : codecs) {
                            if (codec.getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new GzipDecompressingEntity(
                                        response.getEntity()));
                                return;
                            }
                        }
                    }
                }
            }
        });

        return client;
    }
    
    /**
     * 发送网络请求，并将结果转换为字符串.
     * @param req 网络请求对象
     * @return 结果字符串
     * @throws AibangServerException 服务器有响应，但是没有返回预期结果，例如若请求参数错误，
     *              服务器会返回状态码为400的响应
     * @throws AibangIOException 网络异常
     * @throws AibangException 未知异常
     */
    private String execute(HttpUriRequest req) throws AibangException {
        String result = null;
        int statusCode = 200;
        try {
            ConnRouteParams.setDefaultProxy(httpClient.getParams(), proxy);
            HttpResponse response = httpClient.execute(req);
            statusCode = response.getStatusLine().getStatusCode();
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new AibangIOException(e);
        } catch (Exception e) {
            throw new AibangException("Unknown aibang exception", e);
        }
        if (statusCode != 200) {
            throw new AibangServerException(statusCode, result);
        }
        return result;
    }
	
	private static class MyClientConnManager extends ThreadSafeClientConnManager {
        public MyClientConnManager(HttpParams params, SchemeRegistry schreg) {
            super(params, schreg);
        }

        @Override
        public ClientConnectionRequest requestConnection(HttpRoute route,
                Object state) {
            IdleConnectionCloserThread.ensureRunning(this, 
                    KEEP_ALIVE_DURATION_SECS, KEEP_ALIVE_MONITOR_INTERVAL_SECS);
            return super.requestConnection(route, state);
        }
    }
	
	private static class MyKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            // Keep-Alive超时时间取：min(20秒、服务器指定的超时时间)
            long timeout = KEEP_ALIVE_DURATION_SECS * 1000;

            HeaderElementIterator i = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (i.hasNext()) {
                HeaderElement element = i.nextElement();
                String name = element.getName();
                String value = element.getValue();
                if (value != null && name.equalsIgnoreCase("timeout")) {
                    try {
                        timeout = Math.min(timeout, Long.parseLong(value) * 1000);
                    } catch (NumberFormatException e) {}
                }
            }

            return timeout;
        }
    }
	
	private static class MyConnectionReuseStrategy extends DefaultConnectionReuseStrategy {

        /**
         * Implements a patch out in 4.1.x and 4.2 that isn't available in 4.0.x which
         * fixes a bug where connections aren't reused when the response is gzipped.
         * See https://issues.apache.org/jira/browse/HTTPCORE-257 for info about the
         * issue, and http://svn.apache.org/viewvc?view=revision&revision=1124215 for
         * the patch which is copied here.
         */
        @Override
        public boolean keepAlive(final HttpResponse response,
                final HttpContext context) {
            if (response == null) {
                throw new IllegalArgumentException(
                        "HTTP response may not be null.");
            }
            if (context == null) {
                throw new IllegalArgumentException(
                        "HTTP context may not be null.");
            }

            // Check for a self-terminating entity. If the end of the entity
            // will
            // be indicated by closing the connection, there is no keep-alive.
            ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
            Header teh = response.getFirstHeader(HTTP.TRANSFER_ENCODING);
            if (teh != null) {
                if (!HTTP.CHUNK_CODING.equalsIgnoreCase(teh.getValue())) {
                    return false;
                }
            } else {
                Header[] clhs = response.getHeaders(HTTP.CONTENT_LEN);
                // Do not reuse if not properly content-length delimited
                if (clhs == null || clhs.length != 1) {
                    return false;
                }
                Header clh = clhs[0];
                try {
                    int contentLen = Integer.parseInt(clh.getValue());
                    if (contentLen < 0) {
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    return false;
                }
            }

            // Check for the "Connection" header. If that is absent, check for
            // the "Proxy-Connection" header. The latter is an unspecified and
            // broken but unfortunately common extension of HTTP.
            HeaderIterator hit = response.headerIterator(HTTP.CONN_DIRECTIVE);
            if (!hit.hasNext())
                hit = response.headerIterator("Proxy-Connection");

            // Experimental usage of the "Connection" header in HTTP/1.0 is
            // documented in RFC 2068, section 19.7.1. A token "keep-alive" is
            // used to indicate that the connection should be persistent.
            // Note that the final specification of HTTP/1.1 in RFC 2616 does
            // not
            // include this information. Neither is the "Connection" header
            // mentioned in RFC 1945, which informally describes HTTP/1.0.
            //
            // RFC 2616 specifies "close" as the only connection token with a
            // specific meaning: it disables persistent connections.
            //
            // The "Proxy-Connection" header is not formally specified anywhere,
            // but is commonly used to carry one token, "close" or "keep-alive".
            // The "Connection" header, on the other hand, is defined as a
            // sequence of tokens, where each token is a header name, and the
            // token "close" has the above-mentioned additional meaning.
            //
            // To get through this mess, we treat the "Proxy-Connection" header
            // in exactly the same way as the "Connection" header, but only if
            // the latter is missing. We scan the sequence of tokens for both
            // "close" and "keep-alive". As "close" is specified by RFC 2068,
            // it takes precedence and indicates a non-persistent connection.
            // If there is no "close" but a "keep-alive", we take the hint.

            if (hit.hasNext()) {
                try {
                    TokenIterator ti = createTokenIterator(hit);
                    boolean keepalive = false;
                    while (ti.hasNext()) {
                        final String token = ti.nextToken();
                        if (HTTP.CONN_CLOSE.equalsIgnoreCase(token)) {
                            return false;
                        } else if (HTTP.CONN_KEEP_ALIVE.equalsIgnoreCase(token)) {
                            // continue the loop, there may be a "close"
                            // afterwards
                            keepalive = true;
                        }
                    }
                    if (keepalive)
                        return true;
                    // neither "close" nor "keep-alive", use default policy

                } catch (ParseException px) {
                    // invalid connection header means no persistent connection
                    // we don't have logging in HttpCore, so the exception is
                    // lost
                    return false;
                }
            }

            // default since HTTP/1.1 is persistent, before it was
            // non-persistent
            return !ver.lessEquals(HttpVersion.HTTP_1_0);
        }
    }

	private static class IdleConnectionCloserThread extends Thread {
        private final MyClientConnManager manager;
        private final int idleTimeoutSeconds;
        private final int checkIntervalMs;
        private static IdleConnectionCloserThread thread = null;

        public IdleConnectionCloserThread(MyClientConnManager manager,
                int idleTimeoutSeconds, int checkIntervalSeconds) {
            super();
            this.manager = manager;
            this.idleTimeoutSeconds = idleTimeoutSeconds;
            this.checkIntervalMs = checkIntervalSeconds * 1000;
        }

        public static synchronized void ensureRunning(
                MyClientConnManager manager, int idleTimeoutSeconds,
                int checkIntervalSeconds) {
            if (thread == null) {
                thread = new IdleConnectionCloserThread(manager,
                        idleTimeoutSeconds, checkIntervalSeconds);
                thread.start();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        wait(checkIntervalMs);
                    }
                    manager.closeExpiredConnections();
                    manager.closeIdleConnections(idleTimeoutSeconds, 
                            TimeUnit.SECONDS);
                    synchronized (IdleConnectionCloserThread.class) {
                        if (manager.getConnectionsInPool() == 0) {
                            thread = null;
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                thread = null;
            }
        }
    }
	
	private static class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException {

            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();

            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }
    }
	
	private HttpClient httpClient;
	private HttpHost proxy;
	private String host;
	private String apiKey;
	private String format;
	
	private static final int DEFAULT_TIMEOUT_MILLIS = 30000;
	private static final int DEFAULT_MAX_CONNECTIONS = 20;
	private static final int KEEP_ALIVE_DURATION_SECS = 20;
    private static final int KEEP_ALIVE_MONITOR_INTERVAL_SECS = 5;
    
    private static final boolean DEBUG = false;
}
