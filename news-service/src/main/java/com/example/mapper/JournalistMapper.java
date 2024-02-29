package com.example.mapper;

import com.example.controller.request.JournalistRequest;
import com.example.controller.response.JournalistResponse;
import com.example.entity.Journalist;
import com.example.entity.dto.JournalistDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JournalistMapper {

    Journalist dtoToEntity(JournalistDto journalist);
    JournalistDto entityToDto (Journalist journalist);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Journalist target, JournalistDto source);
    List<JournalistDto> toListDto(List<Journalist> journalists);

    List<JournalistResponse> toListResponse(List<JournalistDto> journalists);
    JournalistDto requestToDto(JournalistRequest journalistRequest);
    JournalistResponse dtoToResponse(JournalistDto journalist);

    @Mapping(target = "id", source = "idJournalist")
    JournalistDto requestToDto(Long idJournalist, JournalistRequest journalistRequest);
}
