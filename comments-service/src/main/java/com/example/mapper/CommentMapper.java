package com.example.mapper;

import com.example.controller.response.AuthorResponse;
import com.example.controller.response.CommentResponse;
import com.example.entity.Comment;
import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment dtoToEntity(CommentDto comment);
    CommentDto entityToDto (Comment comment);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Comment target, CommentDto source);
    List<CommentDto> toListDto(List<Comment> comments);

    List<CommentResponse> toListResponse(List<CommentDto> comments);

    @Mapping(target = "authorName", source = "author.username")
    CommentResponse dtoToResponse(CommentDto comment);

}
