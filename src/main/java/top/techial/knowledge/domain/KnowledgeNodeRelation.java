package top.techial.knowledge.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Map;

/**
 * @author techial
 */
@Data
@Accessors(chain = true)
@RelationshipEntity(type = "RELATION")
public class KnowledgeNodeRelation {
    private Long id;

    @StartNode
    private KnowledgeNode startNode;

    @EndNode
    private KnowledgeNode endNode;

    @Properties(allowCast = true)
    private Map<String, String> property;
}