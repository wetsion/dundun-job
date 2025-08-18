package site.wetsion.framework.dundunjob.template.enums;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 条件枚举
 */
public enum ConditionEnum {

    EQ(0, "等于"),

    LT(1, "小于"),

    GT(2, "大于"),

    BETWEEN(3, "区间"),

    IN(4, "包含"),

    LTE(5, "小于等于"),

    GTE(6, "大于等于"),

    NE(7, "不等于"),

    WILDCARD(8, "模糊匹配"),

    SCRIPT(9, "脚本"),

    /**
     * 多个满足一个即可
     */
    MULTI_SELECT(10, "多选"),

    EXIST_KEY(11, "字段存在"),


    REGEXP(12, "正则匹配"),

    MISSING_KEY(13, "字段不存在"),

    ;

    private final Integer code;

    private final String desc;

    ConditionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final static Map<Integer, ConditionEnum> MAP;

    static {
        MAP = Stream.of(ConditionEnum.values()).collect(Collectors.toMap(ConditionEnum::getCode,
                Function.identity()));
    }

    public static ConditionEnum getByCode(Integer code) {
        if (Objects.isNull(code)) {
            return EQ;
        }
        return MAP.getOrDefault(code, EQ);
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
