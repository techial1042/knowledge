package top.techial.knowledge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import top.techial.knowledge.domain.Node;
import top.techial.knowledge.dto.NodeDTO;
import top.techial.knowledge.vo.NodeVO;

/**
 * @author techial
 */
@Mapper
public interface NodeMapper {
    NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

    @Mapping(source = "labels", target = "labels.labels")
    @Mapping(source = "property", target = "property.property")
    @Mapping(source = "itemId", target = "itemId")
    Node toNode(NodeVO nodeVO);

    NodeDTO toNodeDTO(Long parentNodeId, Long id, String name);

}
