package top.techial.knowledge.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.techial.knowledge.aspect.FlushAuthority;
import top.techial.knowledge.beans.ResultBean;
import top.techial.knowledge.domain.Item;
import top.techial.knowledge.domain.Node;
import top.techial.knowledge.dto.ItemDTO;
import top.techial.knowledge.exception.ItemException;
import top.techial.knowledge.exception.UserException;
import top.techial.knowledge.mapper.ItemMapper;
import top.techial.knowledge.security.UserPrincipal;
import top.techial.knowledge.service.ItemService;
import top.techial.knowledge.service.NodeService;
import top.techial.knowledge.service.UserService;
import top.techial.knowledge.valid.Insert;
import top.techial.knowledge.vo.ItemVO;

/**
 * @author techial
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;
    private final NodeService nodeService;
    private final UserService userService;

    public ItemController(ItemService itemService, NodeService nodeService, UserService userService) {
        this.itemService = itemService;
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @GetMapping("share")
    public ResultBean<Page<ItemDTO>> share(@PageableDefault Pageable pageable) {
        Page<ItemDTO> list = itemService.findByShare(true, pageable)
                .map(ItemMapper.INSTANCE::toItemDTO);
        return new ResultBean<>(list);
    }

    @GetMapping("/{id}")
    public ResultBean<ItemDTO> findById(@PathVariable Integer id) {
        Item item = itemService.findById(id).orElseThrow(() -> new ItemException(id));
        return new ResultBean<>(ItemMapper.INSTANCE.toItemDTO(item));
    }

    @GetMapping
    public ResultBean<Page<ItemDTO>> findByUserId(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault Pageable pageable
    ) {
        return new ResultBean<>(itemService.findByUserId(userPrincipal.getId(), pageable)
                .map(ItemMapper.INSTANCE::toItemDTO));
    }

    @PostMapping
    @FlushAuthority
    public ResultBean<ItemDTO> save(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Validated(value = Insert.class) @RequestBody ItemVO itemVO
    ) {
        if (itemVO.getName() == null || itemVO.getName().isEmpty()) {
            throw new IllegalArgumentException(String
                    .format("itemVO error. %s", itemVO.toString()));
        }
        Node node = new Node().setName("root");
        node = nodeService.saveItemRoot(node);
        Item item = ItemMapper.INSTANCE.toItem(itemVO)
                .setAuthor(userService.findById(userPrincipal.getId()).orElseThrow(UserException::new))
                .setRootNode(node);
        item = itemService.save(item);
        nodeService.save(node.setItemId(item.getId()));
        itemService.insert(userPrincipal.getId(), item.getId());

        return new ResultBean<>(ItemMapper.INSTANCE.toItemDTO(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ITEM_' + #id.toString())")
    public ResultBean<ItemDTO> update(@RequestBody ItemVO itemVO, @PathVariable Integer id) {
        Item item = itemService.findById(id).orElseThrow(() -> new ItemException(id));
        if (itemVO != null && itemVO.getDescription() != null) {
            item.setDescription(itemVO.getDescription());
        }
        if (itemVO != null && itemVO.getShare() != null) {
            item.setShare(itemVO.getShare());
        }
        if (itemVO != null && itemVO.getName() != null) {
            item.setName(itemVO.getName());
        }
        if (itemVO != null && itemVO.getImage() != null) {
            item.setImage(itemVO.getImage());
        }
        return new ResultBean<>(ItemMapper.INSTANCE.toItemDTO(itemService.save(item)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ITEM_' + #id.toString())")
    @FlushAuthority
    public ResultBean<Object> deleteById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        itemService.deleteByUserIdAndItemId(userPrincipal.getId(), id);
        itemService.deleteById(id);
        nodeService.deleteByItemId(id);

        return new ResultBean<>();
    }

}
