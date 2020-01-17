package top.techial.knowledge.controller;

import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.techial.beans.ResultBean;
import top.techial.knowledge.domain.Item;
import top.techial.knowledge.domain.KnowledgeNode;
import top.techial.knowledge.dto.SearchDTO;
import top.techial.knowledge.mapper.KnowledgeNodeMapper;
import top.techial.knowledge.service.ItemService;
import top.techial.knowledge.service.KnowledgeNodeService;
import top.techial.knowledge.service.NodeTextService;

import java.util.concurrent.Future;

/**
 * @author techial
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final KnowledgeNodeService knowledgeNodeService;
    private final NodeTextService nodeTextService;
    private final ItemService itemService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private static final String REGEX = "<[^>]*>|&nbsp;";

    public SearchController(
            KnowledgeNodeService knowledgeNodeService,
            NodeTextService nodeTextService,
            ItemService itemService,
            ThreadPoolTaskExecutor threadPoolTaskExecutor
    ) {
        this.knowledgeNodeService = knowledgeNodeService;
        this.nodeTextService = nodeTextService;
        this.itemService = itemService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @GetMapping
    public ResultBean<Object> search(
            @RequestParam(name = "q") String question,
            @RequestParam(required = false, defaultValue = "false") Boolean tips,
            @PageableDefault Pageable pageable
    ) {
        question = String.format("*%s*", question);
        if (Boolean.TRUE.equals(tips)) {
            return new ResultBean<>(knowledgeNodeService.findAllByNameLike(question, pageable)
                    .map(KnowledgeNodeMapper.INSTANCE::toNodeBaseDTO));
        }
        Page<SearchDTO> result = knowledgeNodeService.findAllByNameLike(question, pageable)
                .map(this::convent);
        return new ResultBean<>(result);
    }

    @SneakyThrows
    private SearchDTO convent(KnowledgeNode it) {
        String text = nodeTextService.findById(it.getId()).getText();
        Future<String> result = threadPoolTaskExecutor.submit(() -> getText(text));

        Future<Item> itemFuture = threadPoolTaskExecutor.submit(
                () -> itemService.findById(it.getItemId()).orElse(new Item()));

        return new SearchDTO()
                .setText(result.get())
                .setUser(itemFuture.get().getAuthor().getNickName())
                .setNode(KnowledgeNodeMapper.INSTANCE.toNodeInfoDTO(it));
    }

    private static String getText(String text) {
        text = text == null ? "" : text;
        text = text.replaceAll(REGEX, "").trim();
        text = text.substring(0, Math.min(200, text.length()));
        return text;
    }

}
