package site.wetsion.framework.dundunjob.template.entity;

import site.wetsion.framework.dundunjob.template.enums.InternalExpressionEnum;

/**
 * 标准模版中的表达式参数
 */
public class StandardTemplateExpressionPayload {

    /**
     * 包含的表达式方法名，
     * <p>可以是内置的函数 {@link InternalExpressionEnum#getFunction()}</p>
     * <p>也可以自定义模版中的占位符表达式，例如 #age + #year 中的 age 和 year </p>
     */
    private String label;

    /**
     * 要替换的值
     */
    private String value;

    /**
     * 值类型
     */
    private Class valueType;

    public StandardTemplateExpressionPayload(String label, String value, Class valueType) {
        this.label = label;
        this.value = value;
        this.valueType = valueType;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }

    public Class getValueType() {
        return valueType;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
}
