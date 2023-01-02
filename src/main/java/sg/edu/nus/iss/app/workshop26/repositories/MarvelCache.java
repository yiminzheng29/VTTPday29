package sg.edu.nus.iss.app.workshop26.repositories;

import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;


import jakarta.json.*;
import sg.edu.nus.iss.app.workshop26.AppConfig;
import sg.edu.nus.iss.app.workshop26.models.Comment;
import sg.edu.nus.iss.app.workshop26.models.SuperHero;

@Repository
public class MarvelCache {

    private static final String MARVEL_REVIEWS = "marvel_reviews";

    @Autowired @Qualifier(AppConfig.CACHE_MARVEL)
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void cache(String key, List<SuperHero> values) {

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        values.stream()
            .forEach(c -> { 
                arrBuilder.add(c.toJson());
            });
        ops.set(key, arrBuilder.build().toString(), Duration.ofSeconds(300));
    }

    public Optional<List<SuperHero>> get(String name) {

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(name);
        if (null == value)
            return Optional.empty();

        JsonReader reader = Json.createReader(new StringReader(value));
        JsonArray results = reader.readArray();

        List<SuperHero> heros = results.stream()
                .map(v -> (JsonObject)v)
                .map(v -> SuperHero.fromCache(v))
                .toList();

        return Optional.of(heros);
    }
    
    public List<Comment> search(String id) {
        Criteria c = Criteria.where("characterId").is(id);
        Query q = Query.query(c);

        return mongoTemplate.find(q, Document.class, MARVEL_REVIEWS)
            .stream()
            .map(d -> Comment.create(d))
            .toList();
    }

    public Comment insertComment(Comment c) {
        return mongoTemplate.insert(c, MARVEL_REVIEWS);
    }
}