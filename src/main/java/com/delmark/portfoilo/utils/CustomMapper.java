package com.delmark.portfoilo.utils;

import com.delmark.portfoilo.models.DTO.PortfolioDto;
import com.delmark.portfoilo.models.Portfolio;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePortfolioFromDTO(PortfolioDto dto, @MappingTarget Portfolio portfolio);
}
