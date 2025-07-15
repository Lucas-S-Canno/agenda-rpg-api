package com.agendarpgadmin.api.services.AdminApp;

import com.agendarpgadmin.api.dtos.TagDTO;
import com.agendarpgadmin.api.entities.TagEntity;
import com.agendarpgadmin.api.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TagDTO findById(Long id) {
        Optional<TagEntity> tag = tagRepository.findById(id);
        return tag.map(this::convertToDTO).orElse(null);
    }

    public TagDTO getTagById(TagDTO tagDTO) {
        return tagDTO;
    }

    public TagDTO createTag(TagDTO tagDTO) {
        TagEntity tagEntity = convertToEntity(tagDTO);
        tagEntity = tagRepository.save(tagEntity);
        return convertToDTO(tagEntity);
    }

    public TagDTO updateTag(TagDTO tagDTO) {
        TagEntity tagEntity = convertToEntity(tagDTO);
        tagEntity = tagRepository.save(tagEntity);
        return convertToDTO(tagEntity);
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    private TagDTO convertToDTO(TagEntity tagEntity) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tagEntity.getId());
        tagDTO.setTag(tagEntity.getTag());
        return tagDTO;
    }

    private TagEntity convertToEntity(TagDTO tagDTO) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setId(tagDTO.getId());
        tagEntity.setTag(tagDTO.getTag());
        return tagEntity;
    }
}
