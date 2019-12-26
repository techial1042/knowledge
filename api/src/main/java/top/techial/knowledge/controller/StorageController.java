package top.techial.knowledge.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.techial.beans.ResultBean;
import top.techial.knowledge.service.NodeTextService;
import top.techial.knowledge.service.StorageService;
import top.techial.knowledge.service.storage.FileStorageService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author techial
 */
@RestController
@RequestMapping("/api/storage")
@Log4j2
public class StorageController {
    private final StorageService storageService;
    private final NodeTextService nodeTextService;
    private final FileStorageService fileStorageService;

    public StorageController(StorageService storageService, NodeTextService nodeTextService, FileStorageService fileStorageService) {
        this.storageService = storageService;
        this.nodeTextService = nodeTextService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 文本上传，对应 node 节点
     */
    @PostMapping("/text/{id}")
    public ResultBean<Long> save(@Valid @RequestBody String text, @PathVariable Long id) {
        nodeTextService.save(text, id);
        return new ResultBean<>(id);
    }

    /**
     * 文本获取，对应 node 节点
     */
    @GetMapping("/text/{id}")
    public ResultBean<String> findById(@PathVariable Long id) {
        return new ResultBean<>(nodeTextService.findById(id).getText());
    }

    @PostMapping
    public Map<String, String> save(@RequestParam MultipartFile file) {
        String str = fileStorageService.store(file);
        Map<String, String> map = new HashMap<>();
        map.put("code", "0");
        map.put("msg", "Success");
        map.put("link", "api/storage/preview/" + str);
        return map;
    }

    @DeleteMapping("/{id}")
    public ResultBean<Boolean> delete(@PathVariable String id) {
        fileStorageService.delete(id);
        return new ResultBean<>(true);
    }


    @GetMapping(value = "/preview/{id}")
    public ResponseEntity<Resource> preview(@PathVariable String id) {
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_TYPE, storageService.findBySha1(id).getType())
            .body(fileStorageService.loadAsResource(id));
    }

}