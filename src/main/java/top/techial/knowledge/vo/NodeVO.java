package top.techial.knowledge.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author techial
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Set<String> labels;

    private Map<String, List<String>> property;

    /**
     * parentId = null -> parent
     * parentId != null && parentId is exists -> child
     * parent != null && parentId is not exists -> parent
     */
    private Long parentId;

    @NotNull
    private Integer itemId;

    private Record record;

    @Data
    public static class Record implements Serializable {

        private static final long serialVersionUID = 1L;

        private String operator;

        private String message;

    }
}