package site.wetsion.framework.dundunjob.template.entity;

import java.util.List;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;

public class StandardTemplateItem {

    /**
     * 查询项的key
     */
    private String key;

    /**
     * 查询项的条件
     * @see ConditionEnum#getCode()
     */
    private Integer condition;

    /**
     * 查询项的目标值
     */
    private List<String> values;

    /**
     * 是否是动态目标值模式，如果是，则表明 {@link #values} 的元素个数以及元素值是动态生成的
     */
    private Boolean dynamicValuesMode;

    /**
     * 当 {@link #condition} 等于 {@link ConditionEnum#MULTI_SELECT} 时，将会加载这里的条件; <br/>
     * 注意：暂不支持 multiSelectCondition 中继续嵌套 multiSelectCondition
     */
    private List<StandardTemplateItem> multiSelectCondition;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public Boolean getDynamicValuesMode() {
        return dynamicValuesMode;
    }

    public void setDynamicValuesMode(Boolean dynamicValuesMode) {
        this.dynamicValuesMode = dynamicValuesMode;
    }

    public List<StandardTemplateItem> getMultiSelectCondition() {
        return multiSelectCondition;
    }

    public void setMultiSelectCondition(List<StandardTemplateItem> multiSelectCondition) {
        this.multiSelectCondition = multiSelectCondition;
    }
}
