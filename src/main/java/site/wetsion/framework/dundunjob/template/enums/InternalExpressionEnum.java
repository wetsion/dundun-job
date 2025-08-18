package site.wetsion.framework.dundunjob.template.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplate;

/**
 * 内置表达式枚举
 *
 * @author wetsion
 */
public enum InternalExpressionEnum {

    /**
     * 不支持
     */
    UNSUPPORTED("unsupported", "不支持的表达式", Void.class),

    /**
     * 当前时间
     */
    NOW("now", "当前时间", Long.class),

    /**
     * 今天第一秒时间戳，时区从 {@link StandardTemplate#getZoneId()} 获取，默认UTC
     */
    TODAY_BEGIN("todayBegin", "今天第一秒时间戳", Long.class),

    /**
     * 今天最后一秒时间戳，时区从 {@link StandardTemplate#getZoneId()} 获取，默认UTC
     */
    TODAY_END("todayEnd", "今天最后一秒时间戳", Long.class),

    ;

    private final String function;

    private final String desc;
    /**
     * 返回值类型
     */
    private final Class valueType;

    InternalExpressionEnum(String function, String desc, Class valueType) {
        this.function = function;
        this.desc = desc;
        this.valueType = valueType;
    }

    private static Map<String, InternalExpressionEnum> MAP;

    static {
        MAP = Arrays.stream(values()).collect(Collectors.toMap(InternalExpressionEnum::getFunction, Function.identity()));
    }

    public static InternalExpressionEnum getByFunction(String function) {
        return MAP.getOrDefault(function, UNSUPPORTED);
    }

    public String getDesc() {
        return desc;
    }

    public Class getValueType() {
        return valueType;
    }

    public String getFunction() {
        return function;
    }
}
