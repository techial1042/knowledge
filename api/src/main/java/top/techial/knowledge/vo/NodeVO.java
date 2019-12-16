package top.techial.knowledge.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.techial.knowledge.domain.KnowledgeNode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author techial
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Set<String> labels;

    private Map<String, String> property;

    private String sortId;

    /**
     * parentId = null -> parent
     * parentId != null && parentId is exists -> child
     * parent != null && parentId is not exists -> parent
     */
    private Long parentId;

    public KnowledgeNode toKnowledgeNode() {
        return new KnowledgeNode()
            .setName(this.name)
            .setLabels(labels == null || labels.isEmpty() ? new HashSet<>() : labels)
            .setProperty(property)
            .setSortId(sortId)
            .setParentNodeId(parentId);
    }
}
