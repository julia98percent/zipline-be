package com.zipline.service.survey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.enums.QuestionType;
import com.zipline.entity.survey.Question;
import com.zipline.global.exception.survey.SurveyException;
import com.zipline.global.exception.survey.errorcode.SurveyErrorCode;
import com.zipline.global.request.SurveyFileDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileQuestionMapper {

	@Value("${survey.questionUid-delimiter}")
	private String questionUidDelimiter;

	public Map<Long, SurveyFileDTO> mapFilesToQuestions(List<MultipartFile> files, List<Question> questions) {
		Map<Long, SurveyFileDTO> questionFileMap = new HashMap<>();

		for (MultipartFile file : files) {
			String originalFilename = file.getOriginalFilename();
			Long extractedQuestionUid = extractQuestionUid(originalFilename, questionUidDelimiter);
			String extractFileName = extractFileName(originalFilename, questionUidDelimiter);

			validateFileUploadQuestionExists(questions, extractedQuestionUid);
			questionFileMap.put(extractedQuestionUid,
				SurveyFileDTO.createSurveyFileDTOWithoutUploadedUrl(extractFileName, file));
		}
		return questionFileMap;
	}

	private void validateFileUploadQuestionExists(List<Question> questions, Long uid) {
		if (questions.stream().noneMatch(q -> q.getUid().equals(uid)
			&& q.getQuestionType() == QuestionType.FILE_UPLOAD)) {
			throw new SurveyException(SurveyErrorCode.QUESTION_NOT_FOUND);
		}
	}

	private Long extractQuestionUid(String originalFileName, String questionUidDelimiter) {
		int pos = originalFileName.indexOf(questionUidDelimiter);
		Long questionUid = Long.parseLong(originalFileName.substring(0, pos));
		return questionUid;
	}

	private String extractFileName(String originalFilename, String questionUidDelimiter) {
		int pos = originalFilename.indexOf(questionUidDelimiter);
		return originalFilename.substring(pos + questionUidDelimiter.length());
	}
}
