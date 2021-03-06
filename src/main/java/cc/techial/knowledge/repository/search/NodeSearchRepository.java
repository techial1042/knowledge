package cc.techial.knowledge.repository.search;

import cc.techial.knowledge.domain.Node;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author techial
 */
@Repository
public interface NodeSearchRepository extends ElasticsearchRepository<Node, Long> {
    @Transactional
    void deleteByItemId(Integer itemId);

    @Transactional
    void deleteByIdIn(Collection<Long> ids);
}
