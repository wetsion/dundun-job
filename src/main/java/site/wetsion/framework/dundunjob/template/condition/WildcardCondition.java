package site.wetsion.framework.dundunjob.template.condition;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.template.Condition;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.elastic.search.assistant.QueryCondition;
import site.wetsion.framework.elastic.search.assistant.query.WildCard;

@Component
public class WildcardCondition extends Condition {
    @Override
    public ConditionEnum type() {
        return ConditionEnum.WILDCARD;
    }

    @Override
    public QueryCondition translate(StandardTemplateItem item) {
        String value = CollectionUtils.isEmpty(item.getValues()) ? StringUtils.EMPTY : item.getValues().get(0);
        value = org.springframework.util.StringUtils.trimLeadingCharacter(value, '*');
        value = org.springframework.util.StringUtils.trimTrailingCharacter(value, '*');
        return new WildCard(item.getKey(), String.format("*%s*", value));
    }
}
