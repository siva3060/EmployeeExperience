package io.dawn.ivrauto.util;

import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.service.QuestionService;
import io.dawn.ivrauto.service.ScreeningService;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.transaction.annotation.Transactional;

/** Class in charge of parsing the JSON file that contains the list of questions. */
@Slf4j
public class ScreeningParser {
  ScreeningService screeningService;
  QuestionService questionService;

  public ScreeningParser() {}

  public ScreeningParser(ScreeningService screeningService, QuestionService questionService) {
    this.screeningService = screeningService;
    this.questionService = questionService;
  }

  /**
   * This method is called on app initialization. It will insert the questions in the DB every time
   * the application starts
   *
   * @param filePath path for the .json file
   */
  public void parse(String filePath) {
    FileReader reader = null;
    try {
      reader = new FileReader(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) jsonParser.parse(reader);
    } catch (IOException e) {
      log.debug("Error reading from screening file");
    } catch (ParseException e) {
      log.debug("Error while parsing JSON screening file");
    }

    String title = (String) jsonObject.get("title");
    JSONArray questions = (JSONArray) jsonObject.get("questions");

    loadScreeningData(title, questions);
  }

  @Transactional
  public void loadScreeningData(String title, JSONArray questions) {
    Screening screening = new Screening(title, new Date());
    screeningService.create(screening);
    Question newQuestion;

    for (Object question : questions) {
      JSONObject obj = (JSONObject) question;
      String body = (String) obj.get("body");
      String type = (String) obj.get("type");
      String validation = (String) obj.get("validation");
      newQuestion = new Question(body, type, validation, screening, LocalDate.now());
      questionService.save(newQuestion);
    }
  }
}
