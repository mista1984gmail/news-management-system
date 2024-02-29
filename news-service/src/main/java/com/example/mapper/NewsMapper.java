package com.example.mapper;

import com.example.controller.request.NewsRequest;
import com.example.controller.response.NewsResponse;
import com.example.entity.News;
import com.example.entity.dto.NewsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    News dtoToEntity(NewsDto news);
    NewsDto entityToDto (News news);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget News target, NewsDto source);
    List<NewsDto> toListDto(List<News> news);
    List<NewsResponse> toListResponse(List<NewsDto> news);
    @Mapping(target = "journalistName", source = "journalist.username")
    NewsResponse dtoToResponse(NewsDto news);

    @Mapping(target = "id", source = "id")
    NewsDto requestToDto(Long id, NewsRequest newsRequest);

    NewsDto requestToDto(NewsRequest newsRequest);
}
