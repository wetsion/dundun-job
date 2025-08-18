package site.wetsion.framework.dundunjob.template;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import site.wetsion.framework.dundunjob.datasource.JobInfo;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplate;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateExpressionPayload;
import site.wetsion.framework.dundunjob.template.entity.StandardTemplateItem;
import site.wetsion.framework.dundunjob.template.enums.ConditionEnum;
import site.wetsion.framework.dundunjob.template.enums.InternalExpressionEnum;
import site.wetsion.framework.elastic.search.assistant.builder.QueryBuilder;
import site.wetsion.framework.elastic.search.assistant.query.Bool;
import site.wetsion.framework.elastic.search.assistant.query.SearchAfter;
import site.wetsion.framework.elastic.search.assistant.query.Sort;
import site.wetsion.framework.spanner.date.DateTimeUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class StandardTemplateParser {
    @Autowired
    private ConditionLoader conditionLoader;

    private final static Logger log = LoggerFactory.getLogger(StandardTemplateParser.class);

    private final static ExpressionParser PARSER = new SpelExpressionParser();

    private final static ParserContext PARSER_CONTEXT = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "${";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    };

    public String generateJobInstanceName(JobInfo jobInfo) {
        String content = jobInfo.getContent();
        if (StringUtils.isBlank(content)) {
            return jobInfo.getName() + "-" +  DateTimeUtil.format(new Date(), "yyyyMMddHHmmss");
        }
        StandardTemplate standardTemplate = JSON.parseObject(content, StandardTemplate.class);
        if (StringUtils.isBlank(standardTemplate.getBatchNamePattern())) {
            return jobInfo.getName() + "-" +  DateTimeUtil.format(new Date(), "yyyyMMddHHmmss");
        }
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        List<StandardTemplateExpressionPayload> expressionItems = standardTemplate.getExpressionItems();
        fillContext(evaluationContext, expressionItems, standardTemplate.getZoneId());
        return jobInfo.getName() + "-" + PARSER.parseExpression(standardTemplate.getBatchNamePattern(), PARSER_CONTEXT)
                .getValue(evaluationContext, String.class);
    }

    /**
     * 将标准模版解析为es 查询 json
     * @param standardTemplate 标准模版
     * @return es json
     */
    public String parseTemplate(StandardTemplate standardTemplate) {
        Objects.requireNonNull(standardTemplate, "模版为空");
        QueryBuilder queryBuilder = new QueryBuilder()
                //查询总数
                .trackTotalHits(true)
                .from((standardTemplate.getPageNum() - 1) * standardTemplate.getPageSize())
                .size(standardTemplate.getPageSize());
        if (StringUtils.isNotBlank(standardTemplate.getSortField())) {
            // 默认降序
            if ("asc".equalsIgnoreCase(standardTemplate.getSortType())) {
                queryBuilder.sort(new Sort().asc(standardTemplate.getSortField()));
            } else {
                queryBuilder.sort(new Sort().desc(standardTemplate.getSortField()));
            }
        } else {
            queryBuilder.sort(new Sort().desc("created"));
        }
        List<StandardTemplateItem> items = standardTemplate.getItems();
        List<StandardTemplateExpressionPayload> expressionItems = standardTemplate.getExpressionItems();
        // 是否开启表达式解析
        Boolean expressable = Boolean.FALSE;
        // 上下文
        EvaluationContext evaluationContext = null;
        if (CollectionUtils.isNotEmpty(expressionItems)) {
            expressable = Boolean.TRUE;
            evaluationContext = new StandardEvaluationContext();
            // 填充表达式
            fillContext(evaluationContext, expressionItems, standardTemplate.getZoneId());
        }
        if (CollectionUtils.isNotEmpty(items)) {
            Bool bool = new Bool();
            for (StandardTemplateItem item : items) {
                if (expressable) {
                    // 开启表达式解析才进行解析
                    parseExpression(item, evaluationContext);
                }
                buildItemFilter(bool, item, evaluationContext);
            }
            queryBuilder.query(bool);
        }
        if (Objects.nonNull(standardTemplate.getFollow())) {
            queryBuilder.searchAfter(new SearchAfter().after(standardTemplate.getFollow()));
        }
        return queryBuilder.toJsonString();
    }


    public void parseExpression(StandardTemplateItem item, EvaluationContext evaluationContext) {
        try {
            List<String> newValues = Lists.newArrayList();
            if (Boolean.TRUE.equals(item.getDynamicValuesMode())) {
                // 如果是动态目标值模式，取values的第一个表达式解析
                List value = PARSER.parseExpression(item.getValues().get(0), PARSER_CONTEXT)
                        .getValue(evaluationContext, List.class);
                newValues.addAll(value);
            } else {
                if (CollectionUtils.isNotEmpty(item.getValues())) {
                    for (String v : item.getValues()) {
                        newValues.add(PARSER.parseExpression(v, PARSER_CONTEXT).getValue(evaluationContext, String.class));
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(newValues)) {
                item.setValues(newValues);
            }
        } catch (Exception e) {
            log.error("StandardTemplateParser 解析失败, item: {}", item, e);
        }
    }

    private void fillContext(EvaluationContext evaluationContext,
                             List<StandardTemplateExpressionPayload> expressionItems,
                             String zoneOffset) {
        if (CollectionUtils.isEmpty(expressionItems)) {
            return;
        }
        for (StandardTemplateExpressionPayload item : expressionItems) {
            if (InternalExpressionEnum.UNSUPPORTED
                    .equals(InternalExpressionEnum.getByFunction(item.getLabel()))) {
                // 不支持的表达式，说明是用户自定义的
                evaluationContext.setVariable(item.getLabel(), buildExpressValue(item.getValue(), item.getValueType()));
            } else {
                // 内置的函数
                InternalExpressionEnum expressionEnum =
                        InternalExpressionEnum.getByFunction(item.getLabel());
                evaluationContext.setVariable(expressionEnum.getFunction(),
                        buildExpressValue(resolveInnerExpression(expressionEnum, zoneOffset), expressionEnum.getValueType()));
            }
        }
    }
    /**
     * 解析内置的表达式，返回字符串类型的值
     * @param expressionEnum 内置函数
     * @return 值
     */
    private String resolveInnerExpression(InternalExpressionEnum expressionEnum, String zoneOffset) {
        if (InternalExpressionEnum.NOW.equals(expressionEnum)) {
            return DateTimeUtil.now().toString();
        }

        if (InternalExpressionEnum.TODAY_BEGIN.equals(expressionEnum)) {
            ZoneId offset = ZoneId.of(StringUtils.defaultIfBlank(zoneOffset, "UTC"));
            return DateTimeUtil.getBeginOfDayTimestamp(Instant.now().getEpochSecond(), offset).toString();
        }

        if (InternalExpressionEnum.TODAY_END.equals(expressionEnum)) {
            ZoneId offset = ZoneId.of(StringUtils.defaultIfBlank(zoneOffset, "UTC"));
            return DateTimeUtil.getEndOfDayTimestamp(Instant.now().getEpochSecond(), offset).toString();
        }

        return StringUtils.EMPTY;
    }

    private void buildItemFilter(Bool bool, StandardTemplateItem item, EvaluationContext evaluationContext) {
        ConditionEnum condition = ConditionEnum.getByCode(item.getCondition());
        switch (condition) {
            case EQ:
            case LT:
            case LTE:
            case GT:
            case GTE:
            case BETWEEN:
            case IN:
            case REGEXP:
                bool.filter(conditionLoader.load(condition).translate(item));
                break;
            case MULTI_SELECT:
                bool.filter(conditionLoader.load(condition).translate(item, evaluationContext));
                break;
            case NE:
            case MISSING_KEY:
                bool.mustNot(conditionLoader.load(condition).translate(item));
                break;
            case WILDCARD:
            case SCRIPT:
            case EXIST_KEY:
                bool.must(conditionLoader.load(condition).translate(item));
                break;
            default:
                throw new RuntimeException("invalid condition");
        }
    }

    private Object buildExpressValue(String value, Class type) {
        if (Long.class.equals(type)) {
            return Long.parseLong(value);
        } else if (Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }
}
