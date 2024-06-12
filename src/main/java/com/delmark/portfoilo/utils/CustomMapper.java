package com.delmark.portfoilo.utils;

import com.delmark.portfoilo.controller.requests.ProjectsRequest;
import com.delmark.portfoilo.controller.requests.WorkplaceRequest;
import com.delmark.portfoilo.controller.requests.PortfolioRequest;
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
    void updatePortfolioFromDTO(PortfolioRequest dto, @MappingTarget Portfolio portfolio);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDTO(ProjectsRequest dto, @MappingTarget Projects project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWorkplaceFromDTO(WorkplaceRequest dto, @MappingTarget Workplace workplace);

    Portfolio toEntity(PortfolioRequest portfolioRequest);

    Projects toEntity(ProjectsRequest projectsRequest);
}
