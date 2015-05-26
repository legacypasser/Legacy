package com.opensearch.javasdk;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.ClientProtocolException;

import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.util.Encryption;
import com.opensearch.javasdk.object.KeyTypeEnum;

/**
 * Cloudsearch Client。
 *
 * 此类主要提供一下功能：
 * 1、根据请求的参数来生成签名和nonce。
 * 2、根据需求向API服务提出请求并返回response结果。
 *
 * @author guangfan.qu
 *
 */
public class CloudsearchClient {

    /**
     * 指定默认的请求方式；默认为GET.
     */
    public static final String DEFAULT_METHOD = "GET";

    /**
     * GET请求。
     */
    public static final String METHOD_GET = "GET";

    /**
     * POST请求。
     */
    public static final String METHOD_POST = "POST";

    /**
     * 用户的client id。
     *
     * 此信息由网站中提供。
     */
    private String clientId;

    /**
     * 用户的秘钥。
     *
     * 此信息由网站中提供。
     */
    private String clientSecret;

    /**
     * 请求API的base URI.
     */
    private String baseURI;

    /**
     * 当前API的版本号。
     */
    private String version = "v2";

    /**
     * 请求的domain地址。
     */
    private String host = "http://opensearch.aliyuncs.com";

    /**
     * 用户类型，包含opensearch老用户和阿里云用户
     */
    private KeyTypeEnum keyType = KeyTypeEnum.OPENSEARCH;

    /**
     * 用户阿里云网站中的accesskey,keyYype为ALIYUN使用 此信息阿里云网站中提供
     */
    private String accesskey;

    /**
     * 用户阿里云网站中的secret,keyYype为ALIYUN使用 此信息阿里云网站中提供
     */
    private String secret;

    /**
     * 构造函数。
     *
     * @param clientId 用户的client id，从网站中可以获得此信息。
     * @param clientSecret 用户的client secret，从网站中可以获得此信息。
     * @param opts 一些可选信息，包含：
     *            version 当前sdk的版本信息，默认为v2。
     *            host 指定请求的host地址，默认为this.host。
     *            timeout 指定请求超时时间，单位为：秒。用户可以根据自己的场景来设定此值，
     *                例如如果搜索可以设定时间稍短，如果推送文档，可以设定稍长的时间。
     *            connect_timeout 指定连接超时时间，单位为：秒。
     *            gzip 指定使用gzip方式传输数据
     */
    public CloudsearchClient(String clientId, String clientSecret,
            Map<String, Object> opts) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;

        if (opts != null && opts.size() > 0) {
            if (opts.get("host") != null) {
                this.host = (String) (opts.get("host"));
            }

            if (opts.get("version") != null) {
                this.version = (String) (opts.get("version"));
            }

            if (opts.get("gzip") != null && opts.get("gzip").equals(true)) {
            }
            if (opts.get("timeout") != null) {
                int timeout = (Integer) opts.get("timeout");
                if (timeout > 0) {
                }
            }

            if (opts.get("connect_timeout") != null) {
                int connectTimeout = (Integer) opts.get("connect_timeout");
                if (connectTimeout > 0) {
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this.host.endsWith("/") ?
                this.host.substring(0, this.host.length() - 1) : this.host);
        this.baseURI = sb.toString();
    }

    /**
     * 指定连接池的最大连接数
     * 
     * @param maxConnections
     */
    public void setMaxConnections(int maxConnections) {
        if (maxConnections > 0) {
        }
    }

    /**
     * 构造函数。
     *
     * @param clientId 用户的client id，从网站中可以获得此信息。
     * @param clientSecret 用户的client secret，从网站中可以获得此信息。
     * @param opts 一些可选信息，包含：
     *            version 当前sdk的版本信息，默认为v2。
     *            host 指定请求的host地址，默认为this.host。
     *            timeout 指定请求超时时间，单位为：秒。用户可以根据自己的场景来设定此值，
     *                例如如果搜索可以设定时间稍短，如果推送文档，可以设定稍长的时间。
     *            connect_timeout 指定连接超时时间，单位为：秒。
     * @param keyType 指定当前的用户类型，例如：
     *            如果为KeyTypeEnum.OPENSEARCH则使用client_id和secret
     *            如果为KeyTypeEnum.ALIYUN则用户需要使用阿里云的access key
     */
    public CloudsearchClient(String accesskey, String secret,
            Map<String, Object> opts, KeyTypeEnum keyType) {
        this(null, null, opts);
        this.accesskey = accesskey;
        this.secret = secret;
        this.keyType = keyType;
    }

    /**
     * 向服务器发出请求并获得返回结果。
     *
     * @param path 当前请求的path路径。
     * @param params 当前请求的所有参数数组。
     * @param method 当前请求的方法，使用CloudsearchClient.METHOD_GET或者
     *        CloudsearchClient.METHOD_POST。
     * @return String 返回获取的结果。
     * @throws IOException
     * @throws UnknownHostException
     */
    public String call(String path, Map<String, String> params, String method,
            boolean isPB) throws ClientProtocolException, IOException,
            UnknownHostException {
        return call(path, params, method, isPB, new StringBuffer());
    }

    /**
     * 向服务器发出请求并获得返回结果。
     *
     * @param path 当前请求的path路径。
     * @param params 当前请求的所有参数数组。
     * @param method 当前请求的方法，使用CloudsearchClient.METHOD_GET或者
     *        CloudsearchClient.METHOD_POST。
     * @param debugInfo 当前请求的调试信息
     * @return String 返回获取的结果。
     * @throws IOException
     * @throws ClientProtocolException
     * @throws UnknownHostException
     */
    public String call(String path, Map<String, String> params, String method,
            boolean isPB, StringBuffer debugInfo)
            throws ClientProtocolException, IOException, UnknownHostException {

        String uri = "";
        if (this.keyType == KeyTypeEnum.OPENSEARCH) {
            uri = "/" + this.version + "/api";
        }
        String url = this.baseURI + uri + path;

        TreeMap<String, String> parameters = new TreeMap<String, String>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

        parameters.putAll(params);
        if (keyType == KeyTypeEnum.OPENSEARCH) {
            parameters.put("client_id", this.clientId);
            parameters.put("nonce", getNonce());
            parameters.put("sign", doSign(parameters));
        } else if (keyType == KeyTypeEnum.ALIYUN) {
            parameters.put("Version", "v2");
            parameters.put("AccessKeyId", this.accesskey);
            parameters.put("Timestamp", formatIso8601Date(new Date()));
            parameters.put("SignatureMethod", "HMAC-SHA1");
            parameters.put("SignatureVersion", "1.0");
            parameters.put("SignatureNonce", UUID.randomUUID().toString());
            parameters.put("Signature", getAliyunSign(parameters, method));
        }

        if (method == null) {
            method = DEFAULT_METHOD;
        }


        debugInfo.setLength(0);

        debugInfo.append(url + getHTTPParamsStr(parameters));

        String result = this.doRequest(url, parameters, method, isPB);
        return result;
    }

    /**
     * 获取阿里云使用的signature
     * @param parameters 参数
     * @return
     */
    private String getAliyunSign(TreeMap<String, String> sortMap, String method) {
        boolean isSignMode = false;
        String items = null;
        // 如果有items，先将items排除
        if (sortMap.get("sign_mode") != null
                && sortMap.get("sign_mode").equals("1")
                && sortMap.containsKey("items")) {
            isSignMode = true;
            items = sortMap.get("items");
            sortMap.remove("items");
        }

        try {
            String stringToSign = buildQuery(sortMap);
            stringToSign = method + "&%2F&" + percentEncode(stringToSign);

            // 以下是一段计算签名的示例代码
            final String ALGORITHM = "HmacSHA1";
            final String ENCODING = "UTF-8";

            String accessKeySecret = this.secret + "&";
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(accessKeySecret.getBytes(ENCODING),
                    ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));

            String signature = Base64.encodeToString(signData, Base64.NO_WRAP);

            if (isSignMode && items != null) {
                sortMap.put("items", items);// 将之前排除的items补上
            }
            return signature;
        } catch (Exception e) {
            return null;
        }
    }

    public String call(String path, Map<String, String> params, String method)
            throws ClientProtocolException, IOException {
        return call(path, params, method, false);
    }

    public String call(String path, Map<String, String> params, String method,
            StringBuffer debugInfo) throws ClientProtocolException,
            IOException, UnknownHostException {
        return call(path, params, method, false, debugInfo);
    }

    protected String doRequest(String url, Map<String, String> requestParams,
            String method, boolean isPB) throws ClientProtocolException,
            IOException {
        String result = "";
        if (method.equals(METHOD_POST)) {
            result = LegacyClient.getInstance().aliPush(url, requestParams);
        } else if (method.equals(METHOD_GET)) {
            url = url + getHTTPParamsStr(requestParams);
            result = LegacyClient.getInstance().aliSearch(url);
        }
        return result;
    }

    /**
     * 向服务器发出请求并获得返回结果,默认使用Get方法。
     *
     * @param path 当前请求的path路径。
     * @param params 当前请求的所有参数数组。
     * @return 返回获取的结果。
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String call(String path, Map<String, String> params, boolean isPB)
            throws ClientProtocolException, IOException {
        return call(path, params, DEFAULT_METHOD, isPB);
    }

    protected String getHTTPParamsStr(Map<String, String> params) {
        if (params == null || params.size() <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Entry<String, String> entry : params.entrySet()) {

            String value = null;
            String key = null;
            try {
                key = percentEncode(entry.getKey());
                value = percentEncode(entry.getValue());
            } catch (UnsupportedEncodingException e) {
                // ignore
            }

            sb.append("&").append(key).append("=").append(value);
        }

        return "?" + sb.substring(1);
    }

    /**
     * 生成当前的nonce值。
     *
     * NOTE: time为10位的unix时间戳。
     *
     * @return 返回生成的nonce串。
     */
    private String getNonce() {

        long timestemp = System.currentTimeMillis() / 1000;

        String signStr = new StringBuilder().append(this.clientId).append(
                this.clientSecret).append(timestemp).toString();


        // MD5 加密

        return new StringBuilder().append(Encryption.encrypt(signStr))
                .append(".").append(timestemp).toString();
    }

    /**
     * 根据参数生成当前的签名。
     *
     * 如果指定了sign_mode且sign_mode为1，则参数中的items将不会被计算签名。 注意
     *
     * @param params 需要计算签名的参数列表。
     * @return 返回计算的签名。
     */
    private String doSign(TreeMap<String, String> sortMap) {

        boolean isSignMode = false;
        String items = null;
        if (sortMap.get("sign_mode") != null
                && sortMap.get("sign_mode").equals("1")
                && sortMap.containsKey("items")) {
            isSignMode = true;
            items = sortMap.get("items");
            sortMap.remove("items");
        }


        // MD5 加密
        String md5 = Encryption.encrypt(buildQuery(sortMap) + this.clientSecret);
        if (isSignMode && items != null) {
            sortMap.put("items", items);
        }
        return md5;
    }

    /**
     * 把Map生成http请求需要的参数。
     *
     * @param  params 需要转换为参数的Map。
     * @return 返回转换完毕的字符串。
     */
    private String buildQuery(TreeMap<String, String> sortMap) {

        StringBuilder query = new StringBuilder();
        try {
            for (Entry<String, String> entry : sortMap.entrySet()) {
                query.append("&").append(percentEncode(entry.getKey())).append(
                        "=").append(percentEncode(entry.getValue()));

            }
        } catch (UnsupportedEncodingException e) {
            // ignore
        }

        return query.substring(1);
    }

    /**
     * Java的URLEncode是按照“application/x-www-form-urlencoded”的MIME类型的规则进行编码的。 需要手动替换掉'+'，'*'和'%7E'
     *
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    private String percentEncode(String value)
            throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, "UTF-8").replace("+",
                "%20").replace("*", "%2A").replace("%7E", "~") : null;
    }

    private final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * 生成符合规范的TimeStamp字符串
     * @param date 时间
     * @return
     */
    private String formatIso8601Date(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

}
