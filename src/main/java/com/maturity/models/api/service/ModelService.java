package com.maturity.models.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.model.Model;
import com.maturity.models.api.repository.ModelRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelService {
     private final ModelRepository modelRepository;

     @Transactional
     public Model createModel(String username, Model model) {
          if (model.getTitle() == null || model.getTitle().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model title cannot be empty");
          }
          return modelRepository.save(model);
     }
}
