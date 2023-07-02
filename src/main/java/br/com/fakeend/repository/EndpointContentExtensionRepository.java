package br.com.fakeend.repository;

import br.com.fakeend.model.Content;
import br.com.fakeend.model.EndpointContent;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EndpointContentExtensionRepository {

    private static final String CONTENT_ID = "content.id";
    private static final String CONTENT_BODY = "content.body";
    private static final String NAME = "endpointName";

    private final MongoTemplate mongoTemplate;

    public EndpointContentExtensionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long deleteByContentId(Integer id) {
        Query query = new Query(Criteria.where(CONTENT_ID).is(id));
        DeleteResult deleteResult = mongoTemplate.remove(query, EndpointContent.class);

        return deleteResult.getDeletedCount();
    }

    public void deleteByEndpointName(String endpointName) {
        Query query = new Query(Criteria.where(NAME).is(endpointName));
        mongoTemplate.remove(query, EndpointContent.class);
    }

    public UpdateResult updateContent(Integer id, Map<String, Object> body) {
        Update update = new Update();
        update.set(CONTENT_BODY, body);
        Query query = new Query(Criteria.where(CONTENT_ID).is(id));

        return mongoTemplate.updateFirst(query, update, EndpointContent.class);
    }

    public UpdateResult patch(Integer id, Map<String, Object> body) {
        EndpointContent endpointContent = findEndpointByContentId(id);
        if (endpointContent == null) {
            return null;
        }

        Query query = new Query(Criteria.where(CONTENT_ID).is(id));
        Update update = replacePropertiesContent(endpointContent.content(), body);

        return mongoTemplate.updateFirst(query, update, EndpointContent.class);
    }

    private Update replacePropertiesContent(Content content, Map<String, Object> body) {
        Map<String, Object> contentBody = content.body();
        List<Document> documentList = new ArrayList<>(contentBody.keySet().size());
        for (Map.Entry<String, Object> entry : contentBody.entrySet()) {
            if (body.containsKey(entry.getKey())) {
                contentBody.replace(entry.getKey(), body.get(entry.getKey()));
            }

            documentList.add(new Document(entry.getKey(), contentBody.get(entry.getKey())));
        }

        Map<String, Object> newBody = new LinkedHashMap<>();
        documentList.forEach(item -> item.keySet().forEach(key -> newBody.put(key, item.get(key))));

        Update update = new Update();
        update.set(CONTENT_BODY, newBody);

        return update;
    }

    private EndpointContent findEndpointByContentId(Integer id) {
        Query query = new Query(Criteria.where(CONTENT_ID).is(id));
        return mongoTemplate.findOne(query, EndpointContent.class);
    }
}
