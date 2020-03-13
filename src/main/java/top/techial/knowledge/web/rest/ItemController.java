package top.techial.knowledge.web.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.techial.knowledge.aop.authority.FlushAuthority;
import top.techial.knowledge.beans.ResultBean;
import top.techial.knowledge.domain.Item;
import top.techial.knowledge.domain.Node;
import top.techial.knowledge.repository.ItemRepository;
import top.techial.knowledge.repository.NodeRepository;
import top.techial.knowledge.repository.UserRepository;
import top.techial.knowledge.security.UserPrincipal;
import top.techial.knowledge.service.ItemService;
import top.techial.knowledge.service.NodeService;
import top.techial.knowledge.service.UserService;
import top.techial.knowledge.service.dto.ItemDTO;
import top.techial.knowledge.service.mapper.ItemMapper;
import top.techial.knowledge.service.valid.Insert;
import top.techial.knowledge.web.rest.errors.ItemNotFoundException;
import top.techial.knowledge.web.rest.errors.UserNotFoundException;
import top.techial.knowledge.web.rest.vm.ItemVM;

/**
 * @author techial
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final NodeService nodeService;
    private final UserService userService;
    private final NodeRepository nodeRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    public ItemController(
            ItemService itemService,
            ItemRepository itemRepository,
            NodeService nodeService,
            UserService userService,
            NodeRepository nodeRepository,
            ItemMapper itemMapper,
            UserRepository userRepository
    ) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.nodeService = nodeService;
        this.userService = userService;
        this.nodeRepository = nodeRepository;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
    }

    @GetMapping("share")
    public ResultBean<Page<ItemDTO>> share(@PageableDefault Pageable pageable) {
        Page<ItemDTO> list = itemRepository.findByShare(true, pageable)
                .map(itemMapper::toItemDTO);
        return ResultBean.ok(list);
    }

    @GetMapping("/{id}")
    public ResultBean<ItemDTO> findById(@PathVariable Integer id) {
        Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        return ResultBean.ok(itemMapper.toItemDTO(item));
    }

    @GetMapping
    public ResultBean<Page<ItemDTO>> findByUserId(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault Pageable pageable
    ) {
        return ResultBean.ok(itemRepository.findAllByAuthorId(userPrincipal.getId(), pageable)
                .map(itemMapper::toItemDTO));
    }

    @PostMapping
    @FlushAuthority
    public ResultBean<ItemDTO> save(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Validated(value = Insert.class) @RequestBody ItemVM itemVM
    ) {
        if (itemVM.getName() == null || itemVM.getName().isEmpty()) {
            throw new IllegalArgumentException(String
                    .format("itemVO error. %s", itemVM.toString()));
        }
        Node node = new Node().setName("root");
        node = nodeService.saveItemRoot(node);
        Item item = itemMapper.toItem(itemVM)
                .setAuthor(userRepository.findById(userPrincipal.getId()).orElseThrow(UserNotFoundException::new))
                .setRootNode(node);
        item = itemRepository.save(item);
        nodeRepository.save(node.setItemId(item.getId()));
        itemRepository.insert(userPrincipal.getId(), item.getId());

        return ResultBean.ok(itemMapper.toItemDTO(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ITEM_' + #id.toString())")
    public ResultBean<ItemDTO> update(@RequestBody ItemVM itemVM, @PathVariable Integer id) {
        Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (itemVM != null && itemVM.getDescription() != null) {
            item.setDescription(itemVM.getDescription());
        }
        if (itemVM != null && itemVM.getShare() != null) {
            item.setShare(itemVM.getShare());
        }
        if (itemVM != null && itemVM.getName() != null) {
            item.setName(itemVM.getName());
        }
        if (itemVM != null && itemVM.getImage() != null) {
            item.setImage(itemVM.getImage());
        }
        return ResultBean.ok(itemMapper.toItemDTO(itemRepository.save(item)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ITEM_' + #id.toString())")
    @FlushAuthority
    public ResultBean<Object> deleteById(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        itemRepository.deleteTmpByUserIdAndItemId(userPrincipal.getId(), id);
        itemRepository.deleteById(id);
        nodeService.deleteByItemId(id);

        return ResultBean.ok();
    }

}
