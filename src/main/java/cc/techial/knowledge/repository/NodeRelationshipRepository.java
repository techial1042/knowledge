package cc.techial.knowledge.repository;

import cc.techial.knowledge.domain.NodeRelationship;
import cc.techial.knowledge.domain.NodeRelationshipPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author techial
 */
@Repository
public interface NodeRelationshipRepository extends JpaRepository<NodeRelationship, NodeRelationshipPK>,
        QuerydslPredicateExecutor<NodeRelationship> {

    @Modifying
    @Query(
            nativeQuery = true,
            value = "insert into node_relationship (ancestor, descendant, distance, create_time, update_time)\n" +
                    "select n.ancestor, ?1, n.distance + 1, now(), now()\n" +
                    "from node_relationship n where n.descendant = ?2 union all select ?1, ?1, 0, now(), now()")
    @Transactional
    int insertNode(long id, long parentId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "insert into node_relationship (ancestor, descendant, distance, create_time, update_time)\n" +
                    "value (?1, ?1, 0, now(), now())")
    @Transactional
    void insertNode(long id);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete n from node_relationship n\n" +
                    "join node_relationship m on (n.descendant = m.descendant)\n" +
                    "where n.ancestor = ?1 or n.descendant = ?1"
    )
    @Transactional
    void deleteByNodeId(long id);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "delete n from node_relationship n\n" +
                    "join node_relationship m on (n.descendant = m.descendant)\n" +
                    "where n.ancestor in ?1"
    )
    @Transactional
    void deleteByNodeIdIn(Collection<Long> ids);
}
