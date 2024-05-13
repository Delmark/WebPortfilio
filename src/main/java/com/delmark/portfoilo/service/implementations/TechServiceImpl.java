package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.NoSuchTechException;
import com.delmark.portfoilo.exceptions.TechAlreadyExistsException;
import com.delmark.portfoilo.models.DTO.TechStatsProjection;
import com.delmark.portfoilo.models.Techs;
import com.delmark.portfoilo.repository.TechRepository;
import com.delmark.portfoilo.service.interfaces.TechService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TechServiceImpl implements TechService {

    TechRepository techRepository;

    @Override
    public Page<Techs> getAllTechs(int page) {
        return techRepository.findAll(PageRequest.of(page, 10));
    }

    @Override
    public Techs getTechsById(Long id) {
        return techRepository.findById(id).orElseThrow(NoSuchTechException::new);
    }

    @Override
    public Techs createTech(Techs tech) {
        if (techRepository.findByTechName(tech.getTechName()).isPresent()) {
            throw new TechAlreadyExistsException();
        }
        else {
            tech.setId(null);
            return techRepository.save(tech);
        }
    }

    @Override
    public Techs editTech(Long id, Techs tech) {
        Techs existingTech = techRepository.findById(id).orElseThrow(NoSuchTechException::new);

        if (tech.getTechName() != null) {
            existingTech.setTechName(tech.getTechName());
        }
        if (tech.getTechDesc() != null) {
            existingTech.setTechDesc(tech.getTechDesc());
        }

        return techRepository.save(existingTech);
    }

    @Override
    public void deleteTech(Long id) {
        Techs existingTech = techRepository.findById(id).orElseThrow(NoSuchTechException::new);
        techRepository.delete(existingTech);
    }

    @Override
    public List<Techs> getTechList() {
        return techRepository.findAll();
    }

    @Override
    public List<TechStatsProjection> getTechStatistics() {
        return techRepository.getTechStatistics();
    }
}
