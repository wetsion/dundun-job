package site.wetsion.framework.dundunjob.store.redis;

public interface JobCacheConstant {

    String JOB_INSTANCE_CACHE = "dundunjob:job_instance_cache";

    String JOB_INSTANCE_CACHE_SPLITTER = ":";

    String JOB_QUEUE = "dundunjob:job_queue";

    String WAIT_SCHEDULE_QUEUE = "dundunjob:wait_schedule_queue";

    String MOVE_EXPIRED_JOB_SCRIPT =
            "local zsetKey = KEYS[1]; " +
                    "local listKey = KEYS[2]; " +
                    "local currentTime = tonumber(ARGV[1]); " +
                    "local expiredMembers=redis.call('ZRANGEBYSCORE', zsetKey, '0', currentTime, 'LIMIT', '0', '10'); " +
                    "if #expiredMembers==0 then " +
                    "return 0; " +
                    "end; " +
                    "for i=1,#expiredMembers do " +
                    "redis.call('ZREM', zsetKey, expiredMembers[i]); " +
                    "end; " +
                    "for i=#expiredMembers,1,-1 do " +
                    "redis.call('RPUSH', listKey, expiredMembers[i]); " +
                    "end; " +
                    "return #expiredMembers;";
}
