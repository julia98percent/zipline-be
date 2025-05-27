package com.zipline.service.survey;

import com.zipline.entity.survey.Question;
import com.zipline.entity.survey.SurveyAnswer;
import com.zipline.entity.survey.SurveyResponse;
import com.zipline.global.config.S3Folder;
import com.zipline.global.request.SurveyFileDTO;
import com.zipline.global.util.S3FileUploader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileAnswerProcessor {

  private final S3FileUploader s3FileUploader;
  private final FileQuestionMapper fileQuestionMapper;
  private final SurveyAnswerFactory surveyAnswerFactory;

  public List<SurveyAnswer> processFileAnswers(List<MultipartFile> files, List<Question> questions,
      SurveyResponse surveyResponse) {
    if (files == null || files.isEmpty()) {
      return Collections.emptyList();
    }

    Map<Long, SurveyFileDTO> questionUidSurveyFileVOMap = fileQuestionMapper.mapFilesToQuestions(
        files, questions);
    Map<Long, SurveyFileDTO> questionUidUploadedUrlMap = s3FileUploader.uploadSurveyFiles(
        questionUidSurveyFileVOMap, S3Folder.SURVEYS);

    return questionUidUploadedUrlMap.entrySet()
        .stream()
        .map(entry -> surveyAnswerFactory.createFileAnswer(
            entry.getKey(),
            entry.getValue().getUploadedUrl(),
            questions,
            surveyResponse,
            entry.getValue().getFileName()))
        .collect(Collectors.toList());
  }
}