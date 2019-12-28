package top.techial.knowledge.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.techial.beans.ResultBean;
import top.techial.knowledge.dto.NodeBaseDTO;
import top.techial.knowledge.service.KnowledgeNodeService;

import java.util.List;
import java.util.Map;

/**
 * @author techial
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final KnowledgeNodeService knowledgeNodeService;

    public SearchController(KnowledgeNodeService knowledgeNodeService) {
        this.knowledgeNodeService = knowledgeNodeService;
    }

    @GetMapping
    public ResultBean<Page<Map<String, List<NodeBaseDTO>>>> search(
        @RequestParam(name = "q") String question,
        @PageableDefault Pageable pageable
    ) {
        question = String.format("*%s*", question);
        Page<Map<String, List<NodeBaseDTO>>> result = knowledgeNodeService.findAllByNameLike(question, pageable)
            .map(it -> knowledgeNodeService.getChildAndParent(it.getId(), 10));
        return new ResultBean<>(result);
    }

}
