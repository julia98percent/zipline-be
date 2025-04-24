package com.zipline.service.survey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.entity.enums.QuestionType;
import com.zipline.entity.survey.Question;
import com.zipline.global.exception.survey.errorcode.SurveyErrorCode;
import com.zipline.global.exception.survey.SurveyException;

@Component
public class FileQuestionMapper {

	@Value("${survey.questionUid-delimiter}")
	private String questionUidDelimiter;

	public Map<Long, MultipartFile> mapFilesToQuestions(List<MultipartFile> files, List<Question> questions) {
		Map<Long, MultipartFile> questionFileMap = new HashMap<>();

		for (MultipartFile file : files) {
			String originalFilename = file.getOriginalFilename();
			Long extractedQuestionUid = extractQuestionUid(originalFilename, questionUidDelimiter);

			validateFileUploadQuestionExists(questions, extractedQuestionUid);
			questionFileMap.put(extractedQuestionUid, file);
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
}
