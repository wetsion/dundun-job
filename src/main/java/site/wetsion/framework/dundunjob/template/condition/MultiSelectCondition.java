package site.wetsion.framework.dundunjob.template.condition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.template.Condition;
import site.wetsion.framework.dundunjob.template.ConditionLoader;
import site.wetsion.framework.dundunjob.template.StandardTemplateParser;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.elastic.search.assistant.QueryCondition;
import site.wetsion.framework.elastic.search.assistant.query.Bool;

import java.util.List;
import java.util.Objects;

@Component
public class MultiSelectCondition extends Condition {

    @Autowired
    private ConditionLoader conditionLoader;
    @Autowired
    private StandardTemplateParser standardTemplateParser;
    @Override
    public ConditionEnum type() {
        return ConditionEnum.MULTI_SELECT;
    }

    @Override
    public QueryCondition translate(StandardTemplateItem item) {
        Bool nestBool = new Bool();
        List<StandardTemplateItem> nestItems = item.getMultiSelectCondition();
        for (StandardTemplateItem nestItem : nestItems) {
            ConditionEnum nestCondition =
                    ConditionEnum.getByCode(nestItem.getCondition());
            nestBool.should(conditionLoader.load(nestCondition).translate(nestItem));
        }
        return nestBool;
    }

    @Override
    public QueryCondition translate(StandardTemplateItem item, EvaluationContext evaluationContext) {
        if (Objects.isNull(evaluationContext)) {
            return translate(item);
        }
        Bool nestBool = new Bool();
        List<StandardTemplateItem> nestItems = item.getMultiSelectCondition();
        for (StandardTemplateItem nestItem : nestItems) {
            standardTemplateParser.parseExpression(nestItem, evaluationContext);
            ConditionEnum nestCondition =
                    ConditionEnum.getByCode(nestItem.getCondition());
            nestBool.should(conditionLoader.load(nestCondition).translate(nestItem));
        }
        return nestBool;
    }
}
