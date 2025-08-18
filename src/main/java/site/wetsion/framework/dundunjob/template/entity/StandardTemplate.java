package site.wetsion.framework.dundunjob.template.entity;

import java.util.List;

public class StandardTemplate {
    /**
     * 模版执行生存的批次名字的定义，可为空，可使用表达式，使用的表达式要在 {@link #expressionItems} 中声明
     */
    private String batchNamePattern;

    private List<StandardTemplateItem> items;

    private List<StandardTemplateExpressionPayload> expressionItems;

    /**
     * 时区，默认UTC
     * 格式：UTC+8
     */
    private String zoneId;

    private Integer pageNum = 1;

    private Integer pageSize = 50;

    /**
     * 上一页最后一个元素，非导出场景不传。用于searchAfter方式深度搜索。
     * 使用 transient修饰暂不参与序列化
     */
    private Long follow;

    private String sortField;

    private String sortFieldJsonPath;

    private String sortType;

    public String getBatchNamePattern() {
        return batchNamePattern;
    }

    public void setBatchNamePattern(String batchNamePattern) {
        this.batchNamePattern = batchNamePattern;
    }

    public List<StandardTemplateItem> getItems() {
        return items;
    }

    public void setItems(List<StandardTemplateItem> items) {
        this.items = items;
    }

    public List<StandardTemplateExpressionPayload> getExpressionItems() {
        return expressionItems;
    }

    public void setExpressionItems(List<StandardTemplateExpressionPayload> expressionItems) {
        this.expressionItems = expressionItems;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getFollow() {
        return follow;
    }

    public void setFollow(Long follow) {
        this.follow = follow;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortFieldJsonPath() {
        return sortFieldJsonPath;
    }

    public void setSortFieldJsonPath(String sortFieldJsonPath) {
        this.sortFieldJsonPath = sortFieldJsonPath;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }
}
