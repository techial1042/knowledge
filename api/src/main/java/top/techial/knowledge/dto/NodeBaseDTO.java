package top.techial.knowledge.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import top.techial.knowledge.domain.KnowledgeNode;

import java.io.Serializable;

/**
 * @author techial
 */
@Data
@Accessors(chain = true)
public class NodeBaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Boolean isParentNode;

    private Long id;

    private String name;

    public NodeBaseDTO(KnowledgeNode knowledgeNode) {
        this.isParentNode = knowledgeNode.getIsParentNode();
        this.id = knowledgeNode.getId();
        this.name = knowledgeNode.getName();
    }
}
