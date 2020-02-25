package top.techial.knowledge.web.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.techial.knowledge.beans.ResultBean;
import top.techial.knowledge.service.RecordService;
import top.techial.knowledge.service.dto.RecordDTO;
import top.techial.knowledge.service.mapper.RecordMapper;

/**
 * 节点操作记录
 *
 * @author techial
 */
@RestController
@RequestMapping("/api/record")
public class RecordController {
    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/node/{id}")
    @PreAuthorize("hasAnyAuthority(#id.toString())")
    public ResultBean<Page<RecordDTO>> findByNodeId(
            @PathVariable Long id,
            @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResultBean.ok(recordService.findByNodeId(id, pageable).map(RecordMapper.INSTANCE::toRecordDTO));
    }

}
