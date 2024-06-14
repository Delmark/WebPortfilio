package com.delmark.portfoilo.utils;

import com.delmark.portfoilo.models.DTO.PortfolioDTO;
import com.delmark.portfoilo.models.DTO.ProjectsDTO;
import com.delmark.portfoilo.models.DTO.WorkplaceDTO;
import com.delmark.portfoilo.models.portfolio.Workplace;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.portfolio.Projects;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePortfolioFromDTO(PortfolioDTO dto, @MappingTarget Portfolio portfolio);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDTO(ProjectsDTO dto, @MappingTarget Projects project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWorkplaceFromDTO(WorkplaceDTO dto, @MappingTarget Workplace workplace);

    Portfolio toEntity(PortfolioDTO portfolioDTO);

    Projects toEntity(ProjectsDTO projectsDTO);
}
