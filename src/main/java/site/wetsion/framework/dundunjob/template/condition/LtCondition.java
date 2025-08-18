package site.wetsion.framework.dundunjob.template.condition;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.template.Condition;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.elastic.search.assistant.QueryCondition;
import site.wetsion.framework.elastic.search.assistant.query.Range;

import java.util.List;

@Component
public class LtCondition extends Condition {
    @Override
    public ConditionEnum type() {
        return ConditionEnum.LT;
    }

    @Override
    public QueryCondition translate(StandardTemplateItem item) {
        List<String> values = item.getValues();
        return new Range(item.getKey()).lt(CollectionUtils.isEmpty(values) ? 0 : values.get(0));
    }
}
