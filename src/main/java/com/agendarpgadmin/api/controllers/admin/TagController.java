package com.agendarpgadmin.api.controllers.admin;
import java.util.UUID;

import com.agendarpgadmin.api.dtos.ResponseDTO;
import com.agendarpgadmin.api.dtos.TagDTO;
import com.agendarpgadmin.api.services.admin.TagService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@Observed(name = "admin.tag.controller")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    @Observed(name = "admin.tag.controller.getall", contextualName = "http-get-all-tags")
    public ResponseEntity<ResponseDTO<List<TagDTO>>> getAllTags() {
        try {
            List<TagDTO> tags = tagService.getAllTags();
            ResponseDTO<List<TagDTO>> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    tags);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<List<TagDTO>> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    @Observed(name = "admin.tag.controller.getbyid", contextualName = "http-get-tag-by-id")
    public ResponseEntity<TagDTO> getTagById(@PathVariable UUID id) {
        try {
            TagDTO tag = tagService.findById(id);
            TagDTO response = tagService.getTagById(tag);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<TagDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(500).body(response.getData());
        }
    }

    @PostMapping
    @Observed(name = "admin.tag.controller.create", contextualName = "http-create-tag")
    public ResponseEntity<ResponseDTO<TagDTO>> createTag(@RequestBody TagDTO tagDTO) {
        try {
            TagDTO createdTag = tagService.createTag(tagDTO);
            ResponseDTO<TagDTO> response = new ResponseDTO<>(
                    HttpStatus.CREATED.value(),
                    HttpStatus.CREATED.getReasonPhrase(),
                    createdTag);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ResponseDTO<TagDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping
    @Observed(name = "admin.tag.controller.update", contextualName = "http-update-tag")
    public ResponseEntity<ResponseDTO<TagDTO>> updateTag(@RequestBody TagDTO tagDTO) {
        try {
            TagDTO updatedTag = tagService.updateTag(tagDTO);
            ResponseDTO<TagDTO> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    updatedTag);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<TagDTO> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Observed(name = "admin.tag.controller.delete", contextualName = "http-delete-tag")
    public ResponseEntity<ResponseDTO<String>> deleteTag(@PathVariable UUID id) {
        try {
            TagDTO tag = tagService.findById(id);
            if (tag == null) {
                ResponseDTO<String> response = new ResponseDTO<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Tag não encontrada",
                        null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            tagService.deleteTag(id);
            String message = "Tag deletada: " + tag.getTag();
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.getReasonPhrase(),
                    message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}