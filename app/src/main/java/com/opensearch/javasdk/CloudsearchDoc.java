package com.opensearch.javasdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.opensearch.javasdk.object.DocItems;
import com.opensearch.javasdk.object.SingleDoc;


/**
 * opensearch 文档操作接口。
 * 
 * 此接口负责添加文档、更新文档、删除文档、获取指定文档状态和导入一个文档文件。
 * 
 * @author liaoran.xg
 * 
 */
public class CloudsearchDoc {

    public static final String DOC_ADD = "add";
    public static final String DOC_REMOVE = "delete";
    public static final String DOC_UPDATE = "update";

    /**
     * push数据时API返回的正确的状态值。
     */
    public static final String PUSH_RETURN_STATUS_OK = "OK";

    /**
     * push数据时验证签名的方式。
     * 
     * 如果此常量为1，且生成签名的query string中包含了items字段，则计算签名的时候items字段 将不被包含在内。否则，所有的字段将都要被计算签名。
     * 
     */
    public static final int SIGN_MODE = 1;

    /**
     * 在切割一个大数据块后push数据的频率。默认 5次/s。
     */
    public static final int PUSH_FREQUENCE = 5;

    /**
     * POST一个文件，进行切割时的单请求的最大size。默认为2MB。
     */
    public static final int PUSH_MAX_SIZE = 2 * 1024 * 1024;

    /**
     * Ha3Doc文件doc分割符。\x1E\n \x1E 对应ASCII=30
     */
    public static final String HA_DOC_ITEM_SEPARATOR = "\u001E";

    /**
     * Ha3Doc文件字段分割符 \x1F\n \x1F 对应ASCII=31
     */
    public static final String HA_DOC_FIELD_SEPARATOR = "\u001F";

    /**
     * Ha3Doc文件字段多值分割符。\x1D \x1D 对应ASCII=29
     */
    public static final String HA_DOC_MULTI_VALUE_SEPARATOR = "\u001D";

    /**
     * section weight标志符。\x1C \x1C 对应ASCII=28
     */
    public static final String HA_DOC_SECTION_WEIGHT = "\u001C";

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * CloudsearchClient实例。
     */
    private CloudsearchClient client;

    /**
     * 请求的API的URI。
     */
    private String path;

    /**
     * 进行提交的数据
     */
    private JSONArray requestArray = new JSONArray();

    /**
     * 调用client时发送的请求串信息
     */
    private StringBuffer debugInfo = new StringBuffer();

    /**
     * 构造函数。
     * 
     * @param indexName 指定操作的索引名称。
     * @param client CloudsearchClient实例。
     */
    public CloudsearchDoc(String indexName, CloudsearchClient client) {
        this.indexName = indexName;
        this.client = client;
        this.path = "/index/doc/" + this.indexName;
    }

    /**
     * 根据doc id获取doc的详细信息。
     * 
     * @param docId 指定的doc id。
     * @return 返回api返回的结果。
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public String detail(String docId) throws ClientProtocolException, IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", docId);
        return client.call(this.path, params, CloudsearchClient.METHOD_POST,
                this.debugInfo);
    }

    /**
     * 设置需要添加的属性名称和属性值，所有更新结束之后需要调用push(String tableName)方法
     * 
     * @param fields 字段名和字段值的map
     * @throws JSONException 
     */
    public void add(Map<String, Object> fields) throws JSONException {
        JSONObject JSONFields = new JSONObject();
        for (Entry<String, Object> entry : fields.entrySet()) {
            JSONFields.put(entry.getKey(), entry.getValue());
        }
        JSONObject json = new JSONObject();
        json.put("cmd", "add");
        json.put("fields", JSONFields);
        requestArray.put(json);
    }

    /**
     * 设置需要更新的属性名称和属性值，所有更新结束之后需要调用push(String tableName)方法
     * 
     * @param fields 字段名和字段值的map
     * @throws JSONException 
     */
    public void update(Map<String, Object> fields) throws JSONException {
        JSONObject JSONFields = new JSONObject();
        for (Entry<String, Object> entry : fields.entrySet()) {
            JSONFields.put(entry.getKey(), entry.getValue());
        }
        JSONObject json = new JSONObject();
        json.put("cmd", "update");
        json.put("fields", JSONFields);
        requestArray.put(json);
    }

    /**
     * 设置需要删除的属性名称和属性值，所有更新结束之后需要调用push(String tableName)方法
     * 
     * @param fields 字段名和字段值的map
     * @throws JSONException 
     */
    public void remove(Map<String, Object> fields) throws JSONException {
        JSONObject JSONFields = new JSONObject();
        for (Entry<String, Object> entry : fields.entrySet()) {
            JSONFields.put(entry.getKey(), entry.getValue());
        }
        JSONObject json = new JSONObject();
        json.put("cmd", "delete");
        json.put("fields", JSONFields);
        requestArray.put(json);
    }

    /**
     * 提交字段的修改
     * 
     * @param tableName 表名称
     * @return 返回的数据
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public String push(String tableName) throws ClientProtocolException, IOException {
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "push");
        params.put("items", requestArray.toString());
        params.put("table_name", tableName);
        params.put("sign_mode", String.valueOf(SIGN_MODE));
        String result = client.call(this.path, params,
                CloudsearchClient.METHOD_POST, debugInfo);
        requestArray = new JSONArray();
        return result;
    }

    /**
     * 操作docs。
     * 
     * @param docs 此docs为用户push的数据，此字段为json_encode的字符串。
     * @param tableName 操作的表名。
     * @return 请求API并返回相应的结果。
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public String push(String docs, String tableName) throws IOException {

        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "push");
        params.put("items", docs);
        params.put("table_name", tableName);
        params.put("sign_mode", String.valueOf(SIGN_MODE));
        return client.call(this.path, params, CloudsearchClient.METHOD_POST,
                debugInfo);
    }

    /**
     * 导入HA3 doc数据到指定的应用的指定表中。
     * 
     * 文件编码：UTF-8
     * 支持CMD: add, delete, update。如果给出的字段不是全部，add会在未给出的字段加默认值，
     * 覆盖原值；update只会更新给出的字段，未给出的不变。
     * 
     * 文件分隔符：
     * <pre>
     * 编码--------------描述-----------------------显示形态--------------------
     * "\x1E\n"         每个doc的分隔符.             ^^(接换行符) 
     * "\x1F\n"         每个字段key和value分隔.      ^_(接换行符)
     * "\x1D"           多值字段的分隔符.             ^]
     * </pre>
     * 示例：
     * <pre>
     * CMD=add^_
     * url=http://www.etao.com/index.html^_
     * title=一淘^_
     * body=xxxxx_xxxx^_
     * multi_value_feild=123^]1234^]12345^_
     * ^^
     * CMD=update^_
     * ...
     * </pre>
     * NOTE: 文件结尾的分隔符也必需为"^^\n"，最后一个换行符不能省略。
     * 
     * 
     * @param filePath 指定的文件路径。
     * @param tableName 指定push数据的表名。
     * @return 返回成功或者错误信息。
     * @throws JSONException 
     */
    public String pushHADocFile(String filePath, String tableName) throws JSONException {
        return pushHADocFile(filePath, tableName, 0);
    }

    public String pushHADocFile(String filePath, String tableName, long offset) throws JSONException {
        BufferedReader reader = null;
        DocItems docItems = new DocItems();
        SingleDoc singleDoc = new SingleDoc();
        // 当前行号，用来记录当前已经解析到了第多少行。
        int lineNumber = 1;
        // 最新成功push数据的行号，用于如果在重新上传的时候设定offset偏移行号。
        int lastLineNumber = 0;

        // 最后更新的doc中的字段名，如果此行没有字段结束符，则下行的数据会被添加到这行的字段上。
        // 有一些富文本，在当前行没有结束此字段，则要记录最后的字段名称。
        // 例如：
        // rich_text=鲜花
        // 礼品专卖店^_
        // other_field=xxx^_
        String lastField = "";
        String lastKey = "";
        // 当前还未上传的文档的大小。单位MB.
        long totalSize = 0;

        Queue<Long> timeLimitQueue = new LinkedList<Long>();

        long time = System.currentTimeMillis();
        timeLimitQueue.offer(time);

        String result = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // 如果当前的行号小于设定的offset行号时跳过。
                if (lineNumber < offset) {
                    lineNumber++;
                    continue;
                }
                // 获取结果当前行的最后两个字符。
                String separator = line.substring(line.length() - 1);

                // 如果当前结束符是文档的结束符^^\n，则当前doc解析结束。并计算buffer+当前doc文档的
                // 大小，如果大于指定的文档大小，则push buffer到api，并清空buffer，同时把当前doc
                // 文档扔到buffer中。
                if (separator.equals(HA_DOC_ITEM_SEPARATOR)) {
                    lastField = "";
                    // 获取当前文档生成json并urlencode之后的size大小。
                    String jsonString = singleDoc.getJSONObject().toString();
                    int currentSize = URLEncoder.encode(jsonString, "utf-8").getBytes().length;
                    totalSize = URLEncoder.encode(docItems.getJSONString(), "utf-8").getBytes().length;
                    // 如果计算的大小+buffer的大小大于等于限定的阀值self::PUSH_MAX_SIZE，则push buffer数据。
                    if (currentSize + totalSize >= PUSH_MAX_SIZE) {
                        // 计算每秒的频率限制
                        timeLimitCheck(timeLimitQueue);
                        result = push(docItems.getJSONString(), tableName);
                        JSONObject resultJSON = new JSONObject(result);
                        if (!resultJSON.has("status") || !"OK".equals(resultJSON.get("status"))) {
                            return "last put not OK , line " + lastLineNumber;
                        }
                        lastLineNumber = lineNumber;
                        docItems = new DocItems();
                    }
                    docItems.addDoc(singleDoc);
                    singleDoc = new SingleDoc();
                } else if (separator.equals(HA_DOC_FIELD_SEPARATOR)) {
                    // 表示当前字段结束。
                    String detail = line.substring(0, line.length() - 1);
                    // 表示当前行非第一行数据，则获取最后生成的字段名称并给其赋值。
                    if (isNotBlank(lastField)) {
                        singleDoc.addField(lastKey, lastField + detail);
                    } else {
                        int middleIndex = detail.indexOf("=");
                        if (middleIndex > 0) {
                            String key = detail.substring(0, middleIndex);
                            String value = detail.substring(middleIndex + 1);
                            if ("CMD".equals(key)) {
                                singleDoc.setCmd(value);
                            } else {
                                singleDoc.addField(key, value);
                            }
                        }
                    }

                    lastField = "";
                } else {
                    // 此else 表示富文本的非最后一行。
                    if (isNotBlank(lastField)) {
                        lastField = lastField + line;
                    }
                }
                lineNumber++;
            }
            result = push(docItems.getJSONString(), tableName);
        } catch (Exception e) {
            return "Exception" + e.getMessage() + "\tlastLineNumber:" + lastLineNumber;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return result;
    }

    /**
     * 检查发送频率限制。
     * 
     * @param Queue<Long> timeLimitQueue
     */
    private void timeLimitCheck(Queue<Long> timeLimitQueue) {
        if (timeLimitQueue.size() < PUSH_FREQUENCE) {
            timeLimitQueue.offer(System.currentTimeMillis());
        } else {
            long firstTime = timeLimitQueue.poll();
            long currentTime = System.currentTimeMillis();
            if (currentTime - firstTime < 1000) {
                try {
                    Thread.sleep(currentTime - firstTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            timeLimitQueue.offer(System.currentTimeMillis());
        }
    }
    
    private boolean isNotBlank(String str) {
        if (str != null && !str.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 获取上次请求的信息
     * 
     * @return String
     */
    public String getDebugInfo() {
        return this.debugInfo.toString();
    }
}
