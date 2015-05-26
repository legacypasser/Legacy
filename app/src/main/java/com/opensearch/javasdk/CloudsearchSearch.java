package com.opensearch.javasdk;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;

/**
 * opensearch 搜索接口。
 * 
 * 此接口提供给用户通过简单的方式来生成搜索的语法，并提交进行搜索。
 * 
 * 此接口生成的http 请求串的参数包含：query、client_id、index_name、fetch_fields、 
 * first_formula_name、formula_name和summary等。
 * 
 * example：
 * <code> 
 * Map<String,Object> param = new HashMap<String,Object>();
 * List<String> indexList = new ArrayList<String>(); 
 * indexList.add("yourindexname"); 
 * search.addIndex(indexList); 
 * search.setQueryString("id:'1387936343940'"); 
 * search.setFormat("json"); 
 * String result = search.search();
 * </code>
 * 
 * @author guangfan.qu
 * 
 * @author shuang.liu
 * 
 */
public class CloudsearchSearch {

    /**
     * 设定搜索结果集升降排序的标志。
     * 
     * 根据sort的子句来设定搜索结果集的排序，"+"为升序，"-"为降序。
     * 
     */
    public static final String SORT_INCREASE = "+";
    public static final String SORT_DECREASE = "-";

    /**
     * 和API服务进行交互的对象。
     */
    private CloudsearchClient client;

    /**
     * 此次检索指定的应用名称。
     * 
     * 可以指定单个应用名称，也可以指定多个应用名称来进行搜索。
     * 
     */
    private List<String> indexes = new ArrayList<String>();

    /**
     * 指定某些字段的一些summary展示规则。
     * 
     * 这些字段必需为可分词的text类型的字段。
     * 
     * 例如: 
     * 指定title字段使用动态摘要，则 summary_field=title 
     * 指定title长度为50：summary_len=50
     * 指定title飘红标签：summary_element=em 
     * 指定title省略符号：summary_ellipsis=...
     * 指定summary缩略段落个数：summary_snippet=1 
     * 
     * 那么当前的字段值为：
     * <code>
     * Map<String, Object> summary = new HashMap<String, Object>();
     * summary.put("summary_field", "title");
     * summary.put("summary_len", 50);
     * summary.put("summary_element", "em");
     * summary.put("summary_ellipsis", "...");
     * summary.put("summary_snippet", 1);
     * 
     * Map<String, Map<String, Object>> summaries = new HashMap<String, Map<String, Object>>();
     * summaries.put("title", summary);
     *
     * </code>
     */
    private Map<String, Map<String, Object>> summary = new HashMap<String, Map<String, Object>>();

    private static final String KEY_FORMAT = "format";
    private static final String KEY_START = "start";
    private static final String KEY_HITS = "hit";
    private static final String KEY_RERANKSIZE = "rerank_size";

    /**
     * 用户自定义的config的map
     */
    private Map<String, Object> configMap = new HashMap<String, Object>();

    /**
     * 设定排序规则。
     */
    private Map<String, String> sort = new LinkedHashMap<String, String>();

    /**
     * 设定过滤条件。
     */
    private String filter = null;

    /**
     * 自定义参数。
     */
    private Map<String, String> customParams = new HashMap<String, String>();

    /**
     * aggregate设定规则。
     */
    private List<Map<String, Object>> aggregate = new ArrayList<Map<String, Object>>();

    /**
     * distinct排序设定规则。
     */
    private Map<String, Map<String, Object>> distinct = new LinkedHashMap<String, Map<String, Object>>();

    /**
     * 返回字段过滤。
     * 
     * 如果设定了此字段，则只返回此字段里边的指定的字段。
     */
    private List<String> fetches = new ArrayList<String>();

    /**
     * query 子句。
     * 
     * query子句可以为query='鲜花'，也可以指定索引来搜索，例如：query=title:'鲜花'。 
     * 详情请浏览setQueryString(query)方法。
     */
    private String query;

    /**
     * 指定精排表达式名称，表达式名称和结构在网站中指定。
     * 
     * 用户需要现在网站管理后台中设定好当前请求的应用的精排表达式名称和表达式，在此只需设定表
     * 达式的名称即可。
     */
    private String formulaName = "";

    /**
     * 指定粗排表达式名称，表达式名称和结构在网站中指定。
     * 
     * 用户需要现在网站管理后台中设定好当前请求的应用的精排表达式名称和表达式，在此只需设定表
     * 达式的名称即可。
     */
    private String firstFormulaName = "";

    /**
     * 请求API的部分path。
     */
    private String path = "/search";

    /**
     * 设定kvpair
     */
    private String kvpair = "";

    /**
     * 调用search()时发送的请求串信息
     */
    private StringBuffer debugInfo = new StringBuffer();

    /**
     * 构造函数。
     * 
     * @param client 此对象由CloudsearchClient类实例化。
     */
    public CloudsearchSearch(CloudsearchClient client) {
        this.client = client;
        initCustomConfigMap();
    }

    /**
     * 初始化customConfigMap
     */
    private void initCustomConfigMap() {

        configMap.put(KEY_FORMAT, "xml");
        configMap.put(KEY_START, 0);
        configMap.put(KEY_HITS, 20);
        configMap.put(KEY_RERANKSIZE, 200);
    }

    /**
     * 执行向API提出搜索请求。
     * 
     * @param opts 此参数如果被赋值，则会把此参数的内容分别赋给相应
     * 的变量。此参数的值 可能有一下内容：
     *        query：指定的搜索查询串，可以为query=>'鲜花'，也可以为query=>"索引名:'鲜花'"。 
     *        indexex: 指定的搜索应用，可以为一个索引，也可以多个索引查询。 
     *        fetch_fetches: 设定只返回某些字段的值。 
     *        format：指定返回的数据格式，有json,xml和protobuf三种格式可选。 
     *        formula_name：指定的精排表达式名称，此名称需在网站中设定。
     *        first_formula_name: 指定的粗排表达式名称，此名称需在网站中指定。
     *        summary：指定summary字段一些标红、省略、截断等规则。 
     *        start：指定搜索结果集的偏移量。
     *        hits：指定返回结果集的数量。 
     *        sort：指定排序规则。 
     *        filter：指定通过某些条件过滤结果集。 
     *        aggregate：指定统计类的信息。
     *        distinct：指定distinct排序。
     *        kvpair: 指定的kvpair内容。
     * 
     * @return 返回搜索结果。
     * @throws IOException 
     * @throws ClientProtocolException 
     * 
     */
    public String search(Map<String, Object> opts)
            throws  IOException {
        this.extract(opts);
        return call();
    }

    /**
     * 执行向API提出搜索请求。
     * 
     * @return 返回搜索结果。
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public String search() throws IOException {
        return this.search(new HashMap<String, Object>());
    }

    /**
     * 添加一个应用列表来进行搜索。
     * 
     * @param indexes 应用名称或应用名称列表。
     */
    public void addIndex(List<String> indexes) {
        this.indexes = indexes;
    }

    /**
     * 添加一个应用来进行搜索。
     * 
     * @param indexName 要搜索的应用名称。
     */
    public void addIndex(String indexName) {
        if (this.indexes.indexOf(indexName) == -1) {
            this.indexes.add(indexName);
        }
    }

    /**
     * 在当前检索中不搜索（展示）当前应用的搜索结果。
     * 
     * @param indexName
     */
    public void removeIndex(String indexName) {
        int index = -1;
        if ((index = indexes.indexOf(indexName)) != -1) {
            indexes.remove(index);
        }
    }

    /**
     * 获取当前请求中所有的应用名列表。
     * 
     * @return 返回当前搜索的所有应用列表。
     */
    public List<String> getSearchIndexes() {
        return this.indexes;
    }

    /**
     * 设置精排表达式名称，此表达式名称和结构需要在网站中已经设定。
     * 
     * 详情请浏览官网中的应用指定的表达式名称。
     *
     * @param formulaName 表达式名称。
     */
    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    /**
     * 获取当前设置的表达式名称。
     * 
     * @return 返回当前设定的表达式名称。
     */
    public String getFormulaName() {
        return this.formulaName;
    }

    /**
     * 设置粗排表达式名称，此表达式名称和结构需要在网站中已经设定。
     * 
     * 详情请浏览官网中的应用指定的表达式名称。
     *
     * @param formulaName 表达式名称。
     */
    public void setFirstFormulaName(String formulaName) {
        this.firstFormulaName = formulaName;
    }

    /**
     * 获取当前设置的粗排表达式名称。
     * 
     * @return 返回当前设定的表达式名称。
     */
    public String getFirstFormulaName() {
        return this.firstFormulaName;
    }

    /**
     * 添加一条动态摘要信息。
     * 
     * 增加了此内容后，fieldName字段可能会被截断、飘红等。
     * 
     * @param fieldName 指定的生效的字段。此字段必需为可分词的text类型的字段。
     * @param len 指定结果集返回的词字段的字节长度，一个汉字为2个字节。
     * @param element 指定命中的query的标红标签，可以为em等。
     * @param ellipsis 指定用什么符号来标注未展示完的数据，例如“...”。
     * @param snippet 指定query命中几段summary内容。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addSummary(String fieldName, Integer len, String element,
            String ellipsis, Integer snippet) {

        if (fieldName == null || fieldName.equals("")) {
            return false;
        }

        Map<String, Object> summaryMap = new HashMap<String, Object>();

        summaryMap.put("summary_field", fieldName);
        if (len != null) {
            summaryMap.put("summary_len", len);
        }

        if (element != null) {
            summaryMap.put("summary_element", element);
        }

        if (ellipsis != null) {
            summaryMap.put("summary_ellipsis", ellipsis);
        }

        if (snippet != null) {
            summaryMap.put("summary_snippet", snippet);
        }

        this.summary.put(fieldName, summaryMap);
        return true;
    }

    /**
     * 添加一条动态摘要信息。
     * 
     * 增加了此内容后，fieldName字段可能会被截断、飘红等。
     * 
     * @param fieldName 指定的生效的字段。此字段必需为可分词的text类型的字段。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addSummary(String fieldName) {
        return this.addSummary(fieldName, null, null, null, null);
    }

    /**
     * 添加一条动态摘要信息。
     * 
     * 增加了此内容后，fieldName字段可能会被截断、飘红等。
     * 
     * @param fieldName 指定的生效的字段。此字段必需为可分词的text类型的字段。
     * @param len 指定结果集返回的词字段的字节长度，一个汉字为2个字节。
     * @param ellipsis 指定用什么符号来标注未展示完的数据，例如“...”。
     * @param snippet 指定query命中几段summary内容。
     * @param elementPrefix 指定标签前缀。
     * @param elementPostfix 指定标签后缀。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addSummary(String fieldName, Integer len, String ellipsis,
            Integer snippet, String elementPrefix, String elementPostfix) {
        if (fieldName == null || fieldName.equals("")) {
            return false;
        }
        Map<String, Object> summaryMap = new HashMap<String, Object>();

        summaryMap.put("summary_field", fieldName);
        summaryMap.put("summary_len", len == null ? 0 : len);
        summaryMap.put("summary_ellipsis", ellipsis == null ? "" : ellipsis);
        summaryMap.put("summary_snippet", snippet == null ? 0 : snippet);
        summaryMap.put("summary_element_prefix", elementPrefix == null ? ""
                : elementPrefix);
        summaryMap.put("summary_element_postfix", elementPostfix == null ? ""
                : elementPostfix);

        this.summary.put(fieldName, summaryMap);
        return true;
    }

    /**
     * 获取当前的所有设定的summary信息。
     * 
     * @return 返回summary信息。
     */
    public Map<String, Map<String, Object>> getSummary() {
        return this.summary;
    }

    /**
     * 获取指定字段的summary信息。
     * 
     * @param fieldName 指定的字段名称。
     * 
     * @return 返回指定字段的summary信息。
     */
    public Map<String, Object> getSummary(String fieldName) {
        Map<String, Object> specialSummary = new HashMap<String, Object>();
        if (this.summary.containsKey(fieldName)) {
            specialSummary = this.summary.get(fieldName);
        }
        return specialSummary;
    }

    /**
     * 把summary信息生成字符串并返回。
     * 
     * @return 返回字符串的summary信息。
     */
    public String getSummaryString() {
        StringBuilder summaryStr = new StringBuilder();

        if (this.getSummary().size() > 0) {
            for (Map<String, Object> item : this.getSummary().values()) {
                StringBuilder summarySubStr = new StringBuilder();
                for (Entry<String, Object> entry : item.entrySet()) {
                    summarySubStr.append(",").append(entry.getKey())
                            .append(":").append(entry.getValue());
                }
                summaryStr.append(";").append(summarySubStr.substring(1));
            }
            return summaryStr.substring(1);
        }

        return summaryStr.toString();
    }

    /**
     * 设置返回的数据格式名称。
     * 
     * @param format 数据格式名称，有xml, json和protobuf 三种类型。
     */
    public void setFormat(String format) {
        configMap.put(KEY_FORMAT, format);
    }

    /**
     * 获取当前的数据格式名称。
     * 
     * @return 返回当前的数据格式名称。
     */
    public String getFormat() {
        return String.valueOf(configMap.get(KEY_FORMAT));
    }

    /**
     * 设置返回结果的offset偏移量。
     * 
     * @param start 偏移量。
     */
    public void setStartHit(int start) {
        configMap.put(KEY_START, start);
    }

    /**
     * 获取返回结果的offset偏移量。
     * 
     * @return 返回当前设定的偏移量。
     */
    public int getStartHit() {
        try {
            int startHit = Integer.valueOf(configMap.get(KEY_START).toString());
            return startHit;
        } catch (Exception e) {
            // ignore
        }
        return 0;
    }

    /**
     * 设置当前返回结果集的doc个数。
     * 
     * @param hits 指定的doc个数。
     */
    public void setHits(int hits) {
        if (hits < 0) {
            hits = 0;
        }
        configMap.put(KEY_HITS, hits);
    }

    /**
     * 获取当前设定的结果集的doc数。
     * 
     * @return 返回当前指定的doc个数。
     */
    public int getHits() {
        try {
            int hits = Integer.valueOf(configMap.get(KEY_HITS).toString());
            return hits;
        } catch (Exception e) {
            // ignore
        }
        return 20;
    }

    /**
     * 增加一个排序字段及排序方式。
     * 
     * @param field 需要排序的字段名称。
     * @param sortChar 排序方式，有升序“+”和降序“-”两种方式。
     */
    public void addSort(String field, String sortChar) {
        sort.put(field, sortChar);
    }

    /**
     * 增加一个排序字段。
     * @param field 指定排序的字段名称。
     */
    public void addSort(String field) {
        this.addSort(field, SORT_DECREASE);
    }

    /**
     * 删除指定字段的排序。
     * 
     * @param field 指定的字段名称。
     */
    public void removeSort(String field) {
        if (this.sort.size() > 0 && this.sort.containsKey(field)) {
            this.sort.remove(field);
        }
    }

    /**
     * 获取排序信息。
     * 
     * @return 返回当前所有的排序字段及升降序方式。
     */
    public Map<String, String> getSort() {
        return this.sort;
    }

    /**
     * 把排序信息生成字符串并返回。
     * 
     * @return 返回字符串类型的排序规则。
     */
    public String getSortString() {

        StringBuilder sortStr = new StringBuilder();

        if (this.sort.size() > 0) {
            for (Entry<String, String> entry : this.sort.entrySet()) {
                sortStr.append(";").append(entry.getValue()).append(
                        entry.getKey());
            }

            return sortStr.substring(1);
        }

        return sortStr.toString();
    }

    /**
     * 增加一个自定义参数。
     * 
     * @param paramKey 参数名称。
     * @param paramValue 参数值。
     */
    public void addCustomParam(String paramKey, String paramValue) {
        this.customParams.put(paramKey, paramValue);
    }

    public Map<String, String> getCustomParam() {
        return this.customParams;
    }

    /**
     * 增加加过滤规则。
     * 
     * @param filter 过滤规则，例如fieldName >= 1。
     * @param operator 操作符，可以为 AND OR。
     */
    public void addFilter(String filter, String operator) {
        if (operator == null) {
            operator = "AND";
        }
        if (this.filter == null) {
            this.filter = filter;
        } else {
            this.filter += new StringBuilder().append(" ").append(operator)
                    .append(" ").append(filter).toString();
        }
    }

    /**
     * 增加过滤规则。
     * 
     * @param filter 过滤规则。
     */
    public void addFilter(String filter) {
        this.addFilter(filter, "AND");
    }

    /**
     * 获取过滤规则。
     * 
     * @return 返回字符串类型的过滤规则。
     */
    public String getFilter() {
        return this.filter;
    }

    /**
     * 添加统计信息相关参数。
     * 
     * 一个关键词通常能命中数以万计的文档，用户不太可能浏览所有文档来获取信息。而用户感兴趣的
     * 可 能是一些统计类的信息，比如，查询“手机”这个关键词，想知道每个卖家所有商品中的最高价格。
     * 则可以按照卖家的user_id分组，统计每个小组中最大的price值，例如：
     * groupKey:user_id,aggFun:max(price)
     * 
     * 相关wiki，请查询：
     * @link http://wiki.opensearch.etao.com/index.php?title=Aggregate%E5%AD%90%E5%8F%A5
     * 
     * @param groupKey 指定需要统计的字段名称。
     * @param aggFun 指定统计的方法。当前支持：count、max、min、sum等。
     * @param range 指定统计范围。
     * @param maxGroup 最大组个数。
     * @param aggFilter 指定过滤某些统计。
     * @param aggSamplerThresHold 指定抽样的伐值。
     * @param aggSamplerStep 指定抽样的步长。
     * 
     * @return 返回添加成功或失败。
     */
    public boolean addAggregate(String groupKey, String aggFun, String range,
            String maxGroup, String aggFilter, String aggSamplerThresHold,
            String aggSamplerStep) {
        if (groupKey == null || aggFun == null) {
            return false;
        }

        Map<String, Object> aggregate = new HashMap<String, Object>();

        aggregate.put("group_key", groupKey);
        aggregate.put("agg_fun", aggFun);

        if (range != null) {
            aggregate.put("range", range);
        }

        if (maxGroup != null) {
            aggregate.put("max_group", maxGroup);
        }

        if (aggFilter != null) {
            aggregate.put("agg_filter", aggFilter);
        }

        if (aggSamplerThresHold != null) {
            aggregate.put("agg_sampler_threshold", aggSamplerThresHold);
        }

        if (aggSamplerStep != null) {
            aggregate.put("agg_sampler_step", aggSamplerStep);
        }

        this.aggregate.add(aggregate);
        return true;
    }

    /**
     * 添加统计信息相关参数。
     * 
     * @param groupKey 指定需要统计的字段名称。
     * @param aggFun 指定统计的方法。当前支持：count、max、min、sum等。
     * 
     * @return 返回添加成功或失败。
     */
    public boolean addAggregate(String groupKey, String aggFun) {
        return addAggregate(groupKey, aggFun, null, null, null, null, null);
    }

    /**
     * 获取用户设定的统计相关信息。
     * 
     * @return 返回用户设定的统计信息。
     */
    public List<Map<String, Object>> getAggregate() {
        return this.aggregate;
    }

    /**
     * 返回字符串类型的统计信息。
     * 
     * @return 返回字符串类型的统计信息。
     */
    public String getAggregateString() {

        StringBuilder aggregateStr = new StringBuilder();
        if (this.getAggregate() != null && this.getAggregate().size() > 0) {
            for (int i = 0; i < this.getAggregate().size(); i++) {
                StringBuilder aggregateSubStr = new StringBuilder();
                for (Entry<String, Object> entry : this.getAggregate().get(i)
                        .entrySet()) {
                    aggregateSubStr.append(",").append(entry.getKey()).append(
                            ":").append(entry.getValue());
                }
                aggregateStr.append(";").append(aggregateSubStr.substring(1));
            }
            return aggregateStr.substring(1);
        }

        return aggregateStr.toString();
    }

    /**
     * 添加一条distinct排序信息。
     * 
     * 例如：检索关键词“手机”共获得10个结果，分别为：doc1，doc2，doc3，doc4，doc5，doc6，
     * doc7，doc8，doc9，doc10。其中前三个属于用户A，doc4-doc6属于用户B，剩余四个属于
     * 用户C。如果前端每页仅展示5个商品，则用户C将没有展示的机会。但是如果按照user_id进行抽
     * 取，每轮抽取1个，抽取2次，并保留抽取剩余的结果，则可以获得以下文档排列顺序：doc1、
     * doc4、doc7、doc2、doc5、doc8、doc3、doc6、doc9、doc10。可以看出，通过distinct
     * 排序，各个用户的 商品都得到了展示机会，结果排序更趋于合理。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * @param distCount 为一次抽取的document数量，默认值为1。
     * @param distTimes 为抽取的次数，默认值为1。
     * @param reserved 为是否保留抽取之后剩余的结果，true为保留，false则丢弃，
     *        丢弃时totalHits的个数会减去被distinct而丢弃的个数，但这个结果不一定准确，
     *        默认为true。
     * @param distFilter 为过滤条件，被过滤的doc不参与distinct，只在后面的 排序
     *        中，这些被过滤的doc将和被distinct出来的第一组doc一起参与排序。默认是全部参
     *        与distinct。
     * @param updateTotalHit 当reserved为false时，设置update_total_hit为
     *        true，则最终total_hit会减去被distinct丢弃的的数目（不一定准确），为false
     *        则不减； 默认为false。
     * @param grade 指定档位划分阈值。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key, int distCount, int distTimes,
            String reserved, String distFilter, String updateTotalHit,
            double grade) {

        if (key == null) {
            return false;
        }

        Map<String, Object> distinct = new LinkedHashMap<String, Object>();

        distinct.put("dist_key", key);

        if (distCount > 0) {
            distinct.put("dist_count", distCount);
        }

        if (distTimes > 0) {
            distinct.put("dist_times", distTimes);
        }

        if (reserved != null) {
            distinct.put("reserved", reserved);
        }

        if (distFilter != null) {
            distinct.put("dist_filter", distFilter);
        }

        if (updateTotalHit != null) {
            distinct.put("update_total_hit", updateTotalHit);
        }

        if (grade > 0) {
            distinct.put("grade", grade);
        }

        this.distinct.put(key, distinct);

        return true;
    }

    /**
     * 添加一条排序信息。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key) {
        return this.addDistinct(key, 0, 0, null, null, null, 0);
    }

    /**
     * 添加一条排序信息。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * @param distCount 为一次抽取的document数量，默认值为1。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key, int distCount) {
        return this.addDistinct(key, distCount, 0, null, null, null, 0);
    }

    /**
     * 添加一条排序信息。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * @param distCount 为一次抽取的document数量，默认值为1。
     * @param distTimes 为抽取的次数，默认值为1。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key, int distCount, int distTimes) {
        return this.addDistinct(key, distCount, distTimes, null, null, null, 0);
    }

    /**
     * 添加一条排序信息。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * @param distCount 为一次抽取的document数量，默认值为1。
     * @param distTimes 为抽取的次数，默认值为1。
     * @param reserved 为是否保留抽取之后剩余的结果，true为保留，false则丢弃，丢
     *        弃时totalHits的个数会减去被distinct而丢弃的个数，但这个结果不一定准确，默认
     *        为true。
     *        
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key, int distCount, int distTimes,
            String reserved) {
        return this.addDistinct(key, distCount, distTimes, reserved, null,
                null, 0);
    }

    /**
     * 添加一条排序信息。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * @param distCount 为一次抽取的document数量，默认值为1。
     * @param distTimes 为抽取的次数，默认值为1。
     * @param reserved 为是否保留抽取之后剩余的结果，true为保留，false则丢弃，丢
     *        弃时totalHits的个数会减去被distinct而丢弃的个数，但这个结果不一定准确，默认
     *        为true。
     * @param distFilter 为过滤条件，被过滤的doc不参与distinct，只在后面的 排序
     *        中，这些被过滤的doc将和被distinct出来的第一组doc一起参与排序。默认是全部参
     *        与distinct。
     *        
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key, int distCount, int distTimes,
            String reserved, String distFilter) {
        return this.addDistinct(key, distCount, distTimes, reserved,
                distFilter, null, 0);
    }

    /**
     * 添加一条distinct排序信息。
     * 
     * @param key 为用户用于做distinct抽取的字段，该字段要求为可过滤字段。
     * @param distCount 为一次抽取的document数量，默认值为1。
     * @param distTimes 为抽取的次数，默认值为1。
     * @param reserved 为是否保留抽取之后剩余的结果，true为保留，false则丢弃，
     *        丢弃时totalHits的个数会减去被distinct而丢弃的个数，但这个结果不一定准确，默
     *        认为true。
     * @param distFilter 为过滤条件，被过滤的doc不参与distinct，只在后面的 排序
     *        中，这些被过滤的doc将和被distinct出来的第一组doc一起参与排序。默认是全部参
     *        与distinct。
     * @param updateTotalHit 当reserved为false时，设置update_total_hit为
     *        true，则最终total_hit会减去被distinct丢弃的的数目（不一定准确），为false
     *        则不减； 默认为false。
     * 
     * @return 返回是否添加成功。
     */
    public boolean addDistinct(String key, int distCount, int distTimes,
            String reserved, String distFilter, String updateTotalHit) {
        return this.addDistinct(key, distCount, distTimes, reserved,
                distFilter, updateTotalHit, 0);
    }

    /**
     * 删除某个字段的所有distinct排序信息。
     * 
     * @param distinctKey 要删除的dist key字段名称。
     */
    public void removeDistinct(String distinctKey) {
        if (this.getDistinct().containsKey(distinctKey)) {
            this.getDistinct().remove(distinctKey);
        }
    }

    /**
     * 返回某所有的distinct排序信息。
     * 
     * @return 返回所有的distinct信息。
     */
    public Map<String, Map<String, Object>> getDistinct() {
        return this.distinct;
    }

    /**
     * 返回string类型的所有的distinct信息。
     * 
     * @return 返回字符串类型的distinct信息。
     */
    public String getDistinctString() {

        StringBuilder distinctStr = new StringBuilder();

        if (this.getDistinct().size() > 0) {
            for (Map<String, Object> item : this.getDistinct().values()) {
                StringBuilder distinctSubStr = new StringBuilder();
                for (Entry<String, Object> entry1 : item.entrySet()) {
                    distinctSubStr.append(",").append(entry1.getKey()).append(
                            ":").append(entry1.getValue());
                }
                distinctStr.append(";").append(distinctSubStr.substring(1));
            }
            return distinctStr.substring(1);
        }

        return distinctStr.toString();
    }

    /**
     * 设定指定索引字段范围的搜索关键词。
     * 
     * 此query是查询必需的一部分，可以指定不同的索引名，并同时可指定多个查询及之间的关系 
     * （AND, OR, ANDNOT, RANK）。
     * 
     * 例如查询subject索引字段的query:“手机”，可以设置为 query=subject:'手机'。
     * 
     * 上边例子如果查询price 在1000-2000之间的手机，其查询语句为： query=subject:'手机' 
     * AND price:[1000,2000]
     * 
     * NOTE: text类型索引在建立时做了分词，而string类型的索引则没有分词。
     * 
     * @link http://wiki.opensearch.etao.com/index.php?title=Query%E5%AD%90%E5%8F%A5
     * 
     * @param query 设定搜索的查询语法。
     */
    public void setQueryString(String query) {
        this.query = query;
    }

    /**
     * 返回当前指定的查询词内容。
     * 
     * @return 返回当前设定的查询query子句内容。
     */
    public String getQuery() {
        return this.query;
    }

    /**     *
     * 设定当前的kvpair。
     * 
     * @param String
     *
     */
    public void setPair(String pair) {
        this.kvpair = pair;
    }

    /**
     * 获取当前的kvpair。
     *
     * @return String 返回当前设定的kvpair。
     */
    public String getPair() {
        return this.kvpair;
    }

    /**
     * 设定rerank_size
     * 
     * @param int
     */
    public void setRerankSize(int rerank_size) {
        configMap.put(KEY_RERANKSIZE, rerank_size);
    }

    /**
     * 获取当前rerank_size，默认200
     * 
     * rerankSize表示参与精排算分的文档个数，一般不用使用默认值就能满足，不用设置,会自动使用默认值200
     * @return int 当前设定的rerank_size
     */
    public int getRerankSize() {
        try {
            int rerank_size = Integer.valueOf(configMap.get(KEY_RERANKSIZE)
                    .toString());
            return rerank_size;
        } catch (Exception e) {
            // ignore
        }
        return 200;//默认200
    }

    /**
     * 添加指定结果集返回的字段。
     * 
     * @param fields 结果集返回的字段。
     */
    public void addFetchFields(List<String> fields) {
        this.fetches.addAll(fields);
    }

    /**
     * 添加某个字段到指定的返回字段结果集中。
     * 
     * @param field 指定的字段名称。
     */
    public void addFetchField(String field) {
        this.fetches.add(field);
    }

    /**
     * 获取指定果集返回的字段列表。
     * 
     * @return 返回指定返回字段的列表。
     */
    public List<String> getFetchFields() {
        return this.fetches;
    }

    /**
     * 清空所有用户添加的搜索条件。
     */
    public void clear() {
        this.aggregate.clear();
        this.customParams.clear();
        this.distinct.clear();
        this.fetches.clear();
        this.filter = null;
        this.firstFormulaName = "";
        this.formulaName = "";
        this.indexes.clear();
        this.kvpair = "";
        this.query = "";
        this.sort.clear();
        this.summary.clear();
        initCustomConfigMap();
    }

    /**
     * 从opts变量中抽取所有的需要的参数并复制到属性中。
     * 
     * @param opts
     */
    @SuppressWarnings("unchecked")
    private void extract(Map<String, Object> opts) {

        if (opts != null && opts.size() > 0) {
            // 设置客户端自定义的config
            if (opts.containsKey("config")) {
                if (opts.get("config") instanceof Map) {
                    Map<String, Object> customConfigMap = (Map<String, Object>) opts
                            .get("config");
                    if (customConfigMap != null) {
                        for (Entry<String, Object> entry : customConfigMap
                                .entrySet()) {
                            addCustomConfig(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            if (opts.get("query") != null) {
                this.setQueryString((String) opts.get("query"));
            }

            if (opts.get("indexes") != null) {
                this.addIndex((List<String>) opts.get("indexes"));
            }

            if (opts.get("fetch_field") != null) {
                this.addFetchFields((List<String>) opts.get("fetch_field"));
            }

            if (opts.get("format") != null) {
                this.setFormat((String) opts.get("format"));
            }

            if (opts.get("formula_name") != null) {
                this.setFormulaName((String) opts.get("formula_name"));
            }

            if (opts.get("first_formula_name") != null) {
                this.setFormulaName((String) opts.get("first_formula_name"));
            }

            if (opts.get("summary") != null) {
                this.summary = (Map<String, Map<String, Object>>) opts
                        .get("summary");
            }

            if (opts.get("start") != null) {
                this.setStartHit(Integer.valueOf(opts.get("start").toString()));
            }

            if (opts.get("hits") != null) {
                this.setHits(Integer.valueOf(opts.get("hits").toString()));
            }

            if (opts.get("sort") != null) {
                this.sort = (Map<String, String>) opts.get("sort");
            }

            if (opts.get("filter") != null) {
                this.addFilter((String) opts.get("filter"));
            }

            if (opts.get("aggregate") != null) {
                this.aggregate = (List<Map<String, Object>>) opts
                        .get("aggregate");
            }

            if (opts.get("distinct") != null) {
                this.distinct = (Map<String, Map<String, Object>>) opts
                        .get("distinct");
            }

            if (opts.get("kvpair") != null) {
                this.setPair((String) opts.get("kvpair"));
            }
        }

    }

    /**
     * 生成HTTP的请求串，并通过CloudsearchClient类向API服务发出请求并返回结果。
     * 
     * query参数中的query子句和config子句必需的，其它子句可选。
     * 
     * @return 返回API返回的结果。
     * @throws IOException 
     * @throws ClientProtocolException
     * @throws UnknownHostException 
     */
    private String call() throws ClientProtocolException, IOException,
            UnknownHostException {
        Map<String, String> params = new HashMap<String, String>();

        String haQuery = new StringBuilder().append("config=").append(
                this.clauseConfig()).append("&&").append("query=").append(
                isNotBlank(this.getQuery()) ? this.getQuery() : "''").append(
                isNotBlank(this.getSortString()) ? "&&sort="
                        + this.getSortString() : "").append(
                isNotBlank(this.getFilter()) ? "&&filter=" + this.getFilter()
                        : "").append(
                isNotBlank(this.getDistinctString()) ? "&&distinct="
                        + this.getDistinctString() : "").append(
                isNotBlank(this.getAggregateString()) ? "&&aggregate="
                        + this.getAggregateString() : "")
                .append(isNotBlank(this.getPair()) ? "&&kvpairs="
                        + this.getPair() : "").toString();

        StringBuilder searchIndexes = new StringBuilder();

        if (this.getSearchIndexes() != null
                && this.getSearchIndexes().size() > 0) {
            for (String index : this.getSearchIndexes()) {
                searchIndexes.append(";").append(index);
            }
        }

        params.put("query", haQuery);
        if (searchIndexes.length() > 0) {
            params.put("index_name", searchIndexes.substring(1));
        } else {
            params.put("index_name", "");
        }
        params.put("format", this.getFormat());

        if (isNotBlank(this.getFormulaName())) {
            params.put("formula_name", this.getFormulaName());
        }

        if (isNotBlank(this.getFirstFormulaName())) {
            params.put("first_formula_name", this.getFirstFormulaName());
        }

        if (isNotBlank(this.getSummaryString())) {
            params.put("summary", this.getSummaryString());
        }

        if (this.getFetchFields() != null && this.getFetchFields().size() > 0) {

            StringBuilder fetchFields = new StringBuilder();

            for (String fetchField : this.getFetchFields()) {
                fetchFields.append(";").append(fetchField);
            }

            params.put("fetch_fields", fetchFields.substring(1));
        }

        Map<String, String> customParam = this.getCustomParam();
        if (customParam != null && customParam.size() > 0) {
            for (Entry<String, String> entry : customParam.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        boolean isPB = "protobuf".equals(getFormat());
        return client.call(this.path, params, CloudsearchClient.METHOD_GET,
                isPB,this.debugInfo);
    }

    /**
     * 检查是否是空字符。
     * @param str 要检查的字符串。
     * @return 如果是空字符则返回false，否则返回true。
     */
    private boolean isNotBlank(String str) {
        if (str != null && !str.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 生成搜索的config子句并返回。
     * 
     * @return 返回config子句的string内容。
     */
    private String clauseConfig() {
        StringBuilder sb = new StringBuilder();
        if (configMap != null && configMap.size() > 0) {
            for (Entry<String, Object> entry : configMap.entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue())
                        .append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);// 删除最后一个‘,’
            }
        }
        return sb.toString();
    }

    public void addCustomConfig(String key, Object value) {
        configMap.put(key, value);
    }

    public void removeCustomConfig(String key) {
        configMap.remove(key);
    }
    
    /**
     * 获取上次search请求的信息
     * 
     * @return String
     */
    public String getDebugInfo() {
        return this.debugInfo.toString();
    }
}
