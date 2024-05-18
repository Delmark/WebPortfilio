package com.delmark.portfoilo.utils;

import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.DTO.PortfolioDto;
import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.models.Workplace;
import com.delmark.portfoilo.models.Portfolio;
import com.delmark.portfoilo.models.Projects;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePortfolioFromDTO(PortfolioDto dto, @MappingTarget Portfolio portfolio);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDTO(ProjectsDto dto, @MappingTarget Projects project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWorkplaceFromDTO(PlacesOfWorkDto dto, @MappingTarget Workplace workplace);

    Portfolio toEntity(PortfolioDto portfolioDto);

    Projects toEntity(ProjectsDto projectsDto);
}
