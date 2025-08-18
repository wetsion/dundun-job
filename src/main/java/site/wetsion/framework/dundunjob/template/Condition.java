package site.wetsion.framework.dundunjob.template;

import org.springframework.expression.EvaluationContext;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.elastic.search.assistant.QueryCondition;

public abstract class Condition {

    public abstract ConditionEnum type();

    public abstract QueryCondition translate(StandardTemplateItem item);

    public QueryCondition translate(StandardTemplateItem item, EvaluationContext evaluationContext) {
        return translate(item);
    }
}
