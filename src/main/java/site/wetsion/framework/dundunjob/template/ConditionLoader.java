package site.wetsion.framework.dundunjob.template;

import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class ConditionLoader {

    @Resource
    private List<Condition> conditions;

    public Condition load(ConditionEnum type) {
        return conditions.stream().filter(condition -> Objects.equals(type, condition.type()))
                .findFirst().orElseThrow(() -> new RuntimeException("invalid condition"));
    }
}
