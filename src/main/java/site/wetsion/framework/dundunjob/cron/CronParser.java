package site.wetsion.framework.dundunjob.cron;

import org.springframework.scheduling.support.CronExpression;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CronParser {

    /**
     * 获取指定时间点之后的N个触发时间
     *
     * @param cronExpression cron表达式
     * @param startTime      起始时间
     * @param count          获取的触发时间数量
     * @return 触发时间列表
     */
    public static List<Long> getNextNTimestamps(String cronExpression, ZonedDateTime startTime, int count) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Invalid cron expression");
        }
        CronExpression cron = CronExpression.parse(cronExpression);
        List<Long> timestamps = new ArrayList<>(count);
        ZonedDateTime currentTime = startTime;

        for (int i = 0; i < count; i++) {
            // 获取下一次触发时间
            ZonedDateTime nextTime = cron.next(currentTime);
            if (nextTime == null) {
                break; // 无更多触发时间
            }
            // 转换为毫秒级时间戳
            timestamps.add(nextTime.toInstant().toEpochMilli());
            // 以下一次时间为起点计算下下一次
            currentTime = nextTime;
        }

        return timestamps;
    }
}
