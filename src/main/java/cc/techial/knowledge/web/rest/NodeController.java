package cc.techial.knowledge.web.rest;

import cc.techial.knowledge.aop.authority.FlushAuthority;
import cc.techial.knowledge.beans.ResultBean;
import cc.techial.knowledge.repository.ItemRepository;
import cc.techial.knowledge.repository.RecordRepository;
import cc.techial.knowledge.repository.search.NodeSearchRepository;
import cc.techial.knowledge.security.UserPrincipal;
import cc.techial.knowledge.service.NodeService;
import cc.techial.knowledge.service.mapper.NodeMapper;
import cc.techial.knowledge.service.valid.Insert;
import cc.techial.knowledge.service.valid.Update;
import cc.techial.knowledge.web.rest.errors.ItemNotFoundException;
import cc.techial.knowledge.web.rest.vm.NodeVM;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author techial
 */
@RestController
@RequestMapping("/api/node")
public class NodeController {
    private final NodeService nodeService;
    private final ItemRepository itemRepository;
    private final NodeMapper nodeMapper;
    private final RecordRepository recordRepository;
    private final NodeSearchRepository nodeSearchRepository;

    public NodeController(NodeService nodeService,
                          ItemRepository itemRepository,
                          NodeMapper nodeMapper,
                          RecordRepository recordRepository,
                          NodeSearchRepository nodeSearchRepository) {
        this.nodeService = nodeService;
        this.itemRepository = itemRepository;
        this.nodeMapper = nodeMapper;
        this.recordRepository = recordRepository;
        this.nodeSearchRepository = nodeSearchRepository;
    }

    @GetMapping("/{id}")
    public ResultBean findById(@PathVariable long id) {
        final var node = nodeService.findById(id);
        return ResultBean.ok(nodeMapper.toNodeInfoDTO(node));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ITEM_' + #nodeVM.itemId.toString())")
    @FlushAuthority
    public ResultBean save(@AuthenticationPrincipal UserPrincipal userPrincipal,
                           @Validated(value = Insert.class) @RequestBody NodeVM nodeVM) {
        return ResultBean.ok(nodeService.save(userPrincipal.getId(), nodeVM));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ITEM_' + #nodeVM.itemId.toString()) AND hasAnyAuthority(#id)")
    public ResultBean update(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable long id,
                             @Validated(value = Update.class) @RequestBody NodeVM nodeVM) {
        final var node = nodeService.update(id, nodeVM, userPrincipal.getId());
        return ResultBean.ok(nodeMapper.toNodeInfoDTO(node));
    }

    @PutMapping("/{id}/movement")
    @PreAuthorize("hasAnyAuthority(#target) AND hasAnyAuthority(#id)")
    public ResultBean move(@PathVariable long id,
                           @RequestParam Long target) {
        if (Objects.equals(id, target)) {
            throw new IllegalArgumentException(String.format("id: [%s], target: [%s]", id, target));
        }
        nodeService.move(id, target);
        return ResultBean.ok(true);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ITEM_' + #itemId.toString()) AND hasAnyAuthority(#id)")
    @FlushAuthority
    public ResultBean deleteById(@RequestParam Integer itemId,
                                 @PathVariable long id) {
        nodeService.deleteById(id);
        return ResultBean.ok(true);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ITEM_' + #itemId) AND hasAnyAuthority(#ids)")
    @FlushAuthority
    public ResultBean deleteByIds(@RequestBody Set<Long> ids,
                                  @RequestParam int itemId) {
        nodeService.deleteIdsAndRelationship(ids);
        nodeSearchRepository.deleteByIdIn(ids);
        recordRepository.deleteByNodeIdIn(ids);

        return ResultBean.ok(true);
    }

    @GetMapping
    public ResultBean findByRootNode(@RequestParam int itemId,
                                     @RequestParam(defaultValue = "1") Integer depth) {
        final var rootNodeId = itemRepository.findRootNodeId(itemId).orElseThrow(ItemNotFoundException::new);
        return ResultBean.ok(nodeService.findByChildNode(rootNodeId, depth));
    }

    @GetMapping("/{id}/graph")
    public ResultBean findByIdGraph(@PathVariable long id) {
        return ResultBean.ok(nodeService.findByIdGraph(id));
    }

    @GetMapping("/{id}/link")
    public ResultBean getChildAndParent(@PathVariable long id) throws ExecutionException, InterruptedException {
        return ResultBean.ok(nodeService.getChildAndParent(id));
    }

    @GetMapping("/{id}/child")
    public ResultBean findChildNode(@PathVariable long id,
                                    @RequestParam(required = false, defaultValue = "1") int depth) {
        return ResultBean.ok(nodeService.findByChildNode(id, depth));
    }

    @GetMapping("/name")
    public ResultBean findByName(@RequestParam(name = "query") String name,
                                 @RequestParam int itemId) {
        return ResultBean.ok(nodeService.findByNameLike(name, itemId));
    }
}
