package sg.edu.nus.iss.app.ssflovecalc.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.app.ssflovecalc.model.LoveResult;

@Repository
public class LoveResultsRepo {
    private static final String RESULTS_ENTITY = "resultslist";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void saveResult(final LoveResult lr) {
        redisTemplate.opsForList()
                .leftPush(RESULTS_ENTITY, lr.getId());
        redisTemplate.opsForHash()
                .put(RESULTS_ENTITY + "_Map", lr.getId(), lr);
    }

    public List<LoveResult> getAllResults(int startIndex) {
        List<Object> fromRedisResultsList = redisTemplate.opsForList()
                .range(RESULTS_ENTITY, startIndex, 10);
        // read from redis' list and cast the items back into LoveResult Objects
        List<LoveResult> resultsList = redisTemplate.opsForHash()
                .multiGet(RESULTS_ENTITY + "_Map", fromRedisResultsList)
                .stream()
                .filter(LoveResult.class::isInstance)
                .map(LoveResult.class::cast)
                .toList();

        return resultsList;
    }
}
