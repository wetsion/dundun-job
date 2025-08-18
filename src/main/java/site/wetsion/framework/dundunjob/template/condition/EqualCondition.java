package site.wetsion.framework.dundunjob.template.condition;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.template.Condition;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.elastic.search.assistant.QueryCondition;
import site.wetsion.framework.elastic.search.assistant.query.Term;

import java.util.List;

@Component
public class EqualCondition extends Condition {
    @Override
    public ConditionEnum type() {
        return ConditionEnum.EQ;
    }

    @Override
    public QueryCondition translate(StandardTemplateItem item) {
        List<String> values = item.getValues();
        return new Term(item.getKey(), CollectionUtils.isEmpty(values) ?
                StringUtils.EMPTY : item.getValues().get(0));
    }
}
