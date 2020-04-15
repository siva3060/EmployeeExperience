package io.dawn.ivrauto.service;

import io.dawn.ivrauto.model.Response;
import io.dawn.ivrauto.repository.ResponseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
  private ResponseRepository responseRepository;

  @Autowired
  public ResponseService(ResponseRepository responseRepository) {
    this.responseRepository = responseRepository;
  }

  public void save(Response response) {
    responseRepository.save(response);
  }

  public void delete(Long id) {
    responseRepository.deleteById(id);
  }

  public void deleteAll() {
    responseRepository.deleteAll();
  }

  public Long count() {
    return responseRepository.count();
  }

  public List<Response> findAll() {
    return responseRepository.findAll();
  }

  /*public Optional<Response> find(Long id) {
    return responseRepository.findOne(id);
  }*/

  public Response findProperResponse(String sessionSid, Long questionId) {
    return responseRepository.findProperResponse(sessionSid, questionId);
  }
}
