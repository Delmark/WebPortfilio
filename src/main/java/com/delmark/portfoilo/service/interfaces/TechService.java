package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.Techs;
import org.springframework.data.domain.Page;

public interface TechService {
    Page<Techs> getAllTechs(int page);
    Techs getTechsById(Long id);
    Techs createTech(Techs tech);
    Techs editTech(Long id, Techs tech);
    void deleteTech(Long id);
}
