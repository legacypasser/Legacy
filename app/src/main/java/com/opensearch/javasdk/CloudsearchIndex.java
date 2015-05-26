package com.opensearch.javasdk;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Opensearch索引接口。
 * 
 * 主要功能、创建索引、查看索引内容、删除索引和修改索引名称。
 * 
 * @author liaoran.xg
 * 
 */
public class CloudsearchIndex {

    /**
     * CloudsearchClient实例。
     */
    private CloudsearchClient client;


    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 请求的API的URI。
     */
    private String path;

    /**
     * 调用client时发送的请求串信息
     */
    private StringBuffer debugInfo = new StringBuffer();

    /**
     * CloudsearchIndex的构造函数
     * 
     * @param indexName 索引的名称
     * @param client 提交请求的client
     */
    public CloudsearchIndex(String indexName, CloudsearchClient client) {
        this.indexName = indexName;
        this.client = client;
        this.path = "/index/" + this.indexName;
    }

    /**
     * 根据一个已有的模板的名称，创建一个新的应用。
     * 
     * @param templateName 模板名称
     * @param opts 包含应用的备注信息
     * @return 返回api返回的正确或错误的结果
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String createByTemplateName(String templateName, Map<String, String> opts)
                    throws ClientProtocolException, IOException {

        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "create");
        params.put("template", templateName);

        if (opts != null && opts.get("desc") != null) {
            params.put("index_des", opts.get("desc"));
        }

        if (opts != null && opts.get("package_id") != null) {
            params.put("package_id", opts.get("package_id"));
        }

        return client.call(this.path, params, CloudsearchClient.METHOD_GET,
                this.debugInfo);
    }

    public String createByTemplateName(String templateName) throws ClientProtocolException, IOException {
        return createByTemplateName(templateName, new HashMap<String, String>());
    }

    /**
     * 更新当前索引的索引名称和备注信息。
     * 
     * @param toIndexName 索引名称
     * @param opts 相关参数
     * @return 返回api返回的正确或错误的结果
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String rename(String toIndexName, Map<String, String> opts) throws JSONException, ClientProtocolException,
                    IOException {

        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "update");
        params.put("new_index_name", toIndexName);

        if (opts != null && opts.get("desc") != null) {
            params.put("description", opts.get("desc"));
        }

        String result = client.call(this.path, params,
                CloudsearchClient.METHOD_GET, debugInfo);

        JSONObject jsonResult = new JSONObject(result);

        if (jsonResult != null && jsonResult.getString("status").equals("OK")) {
            this.indexName = toIndexName;
            this.path = "/index/" + this.indexName;
        }

        return result;
    }

    /**
     * 删除当前的索引。
     * 
     * @return 返回api返回的正确或错误的结果
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String delete() throws ClientProtocolException, IOException {
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "delete");
        return client.call(this.path, params, CloudsearchClient.METHOD_GET,
                debugInfo);
    }


    /**
     * 查看当前索引的状态。
     * 
     * @return 返回api返回的正确或错误的结果
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String status() throws ClientProtocolException, IOException {
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "status");
        return client.call(this.path, params, CloudsearchClient.METHOD_GET,
                debugInfo);
    }

    /**
     * 列出所有索引
     * 
     * @param page 开始的页码
     * @param pageSize 获取的记录数
     * @return 返回api返回的正确或错误的结果
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String listIndexes(Integer page, Integer pageSize) throws ClientProtocolException, IOException {

        Map<String, String> params = new HashMap<String, String>();

        params.put("page", page == null ? "1" : String.valueOf(page));
        params.put("page_size", pageSize == null ? "10" : String.valueOf(pageSize));

        return client.call("/index", params, CloudsearchClient.METHOD_GET,
                debugInfo);
    }

    /**
     * 创建一条导入数据或重建索引的任务。
     * 
     * 如果operate为import，则需要指定tableName，反之如果operate为build，则不需指定。 例如： 如果想全量导入某个表的数据且导入完毕后重建索引，则可以调用如下：
     * <code>
     * createTask("import", "my_table", true);
     * </code>
     * 
     * 如果只想全量导入某个表的数据但不建索引。 <code>
     * createTask("import", "my_table", false);
     * </code>
     * 
     * 如果只想重建索引： <code>
     * createTask("build");
     * </code>
     * 
     * 或
     * 
     * <code>
     * createTask("build", "", false);
     * </code>
     * 
     * @param operate 操作符，包含"build"、"import"，build为重建索引，import为数据导入。
     * @param tableName 指定的表的名称，此表必须已经配置了数据源，如果为build则此字段无效。
     * @param needBuild 指定是否重建索引。
     * @return 返回api的结果。
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String createTask(String operate, String tableName, boolean needBuild) throws ClientProtocolException,
                    IOException {
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "createTask");
        params.put("operate", operate);
        if (tableName != null) {
            params.put("table_name", tableName);
        } else {
            params.put("table_name", "");
        }
        params.put("need_build", needBuild == true ? "1" : "0");

        return client.call(path, params, CloudsearchClient.METHOD_GET,
                debugInfo);
    }

    /**
     * 创建一条重建索引的任务。
     * 
     * @return 返回api返回的结果。
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String createBuildTask() throws ClientProtocolException, IOException {
        return createTask("build", "", false);
    }

    /**
     * 创建一条数据导入的任务。
     * 
     * @param tableName 指定的表的名称，此表必须配置了数据源。
     * @param needBuild 指定是否重建索引。
     * @return 返回api返回的结果。
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String createImportTask(String tableName, boolean needBuild) throws ClientProtocolException, IOException {
        return createTask("import", tableName, needBuild);
    }

    /**
     * 获取当前索引的索引名称。
     * 
     * @return 索引名称
     */
    public String getIndexName() {
        return this.indexName;
    }

    /**
     * 获取错误信息
     * 
     * @param page 开始页数
     * @param pageSize 每页的记录数
     * @return API返回的错误信息
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String getErrorMessage(int page, int pageSize) throws ClientProtocolException, IOException {
        Map<String, String> params = new HashMap<String, String>();

        params.put("page", String.valueOf(page));
        params.put("page_size", String.valueOf(pageSize));

        return client.call("/index/error/" + indexName, params,
                CloudsearchClient.METHOD_GET, debugInfo);
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
