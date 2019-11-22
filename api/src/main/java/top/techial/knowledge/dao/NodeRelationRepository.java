package top.techial.knowledge.dao;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import top.techial.knowledge.domain.NodeRelation;

import java.util.List;

/**
 * @author techial
 */
@Repository
public interface NodeRelationRepository extends Neo4jRepository<NodeRelation, Long> {

    /**
     * find byStartNodeName
     */
    List<NodeRelation> findByStartNodeName(String name);

    /**
     * find by start node id
     */
    List<NodeRelation> findByStartNodeId(Long id);
}
