package site.wetsion.framework.dundunjob.template.condition;

import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.template.Condition;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.elastic.search.assistant.QueryCondition;
import site.wetsion.framework.elastic.search.assistant.query.Terms;

@Component
public class NeCondition extends Condition {
    @Override
    public ConditionEnum type() {
        return ConditionEnum.NE;
    }

    @Override
    public QueryCondition translate(StandardTemplateItem item) {
        return new Terms<>(item.getKey(), item.getValues());
    }
}
