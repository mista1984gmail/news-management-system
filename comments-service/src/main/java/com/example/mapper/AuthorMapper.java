package com.example.mapper;

import com.example.controller.request.AuthorRequest;
import com.example.controller.response.AuthorResponse;
import com.example.controller.response.CommentResponse;
import com.example.entity.Author;
import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author dtoToEntity(AuthorDto author);
    AuthorDto entityToDto (Author author);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Author target, AuthorDto source);
    List<AuthorDto> toListDto(List<Author> authors);

    List<AuthorResponse> toListResponse(List<AuthorDto> authors);
    AuthorDto requestToDto(AuthorRequest authorRequest);
    AuthorResponse dtoToResponse(AuthorDto author);

    @Mapping(target = "id", source = "id")
    AuthorDto requestToDto(Long id, AuthorRequest authorRequest);
}
