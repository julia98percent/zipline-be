INSERT INTO password_questions (question)
SELECT '당신이 태어난 도시는 어디인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '당신이 태어난 도시는 어디인가요?'
);

INSERT INTO password_questions (question)
SELECT '당신이 처음 다닌 학교의 이름은 무엇인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '당신이 처음 다닌 학교의 이름은 무엇인가요?'
);

INSERT INTO password_questions (question)
SELECT '기억에 남는 선생님의 성함은 무엇인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '기억에 남는 선생님의 성함은 무엇인가요?'
);

INSERT INTO password_questions (question)
SELECT '당신이 가장 좋아하는 음식은 무엇인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '당신이 가장 좋아하는 음식은 무엇인가요?'
);

INSERT INTO password_questions (question)
SELECT '당신이 처음 키운 반려동물의 이름은 무엇인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '당신이 처음 키운 반려동물의 이름은 무엇인가요?'
);

INSERT INTO password_questions (question)
SELECT '어릴 적 가장 친했던 친구의 이름은 무엇인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '어릴 적 가장 친했던 친구의 이름은 무엇인가요?'
);

INSERT INTO password_questions (question)
SELECT '당신이 가장 좋아하는 영화는 무엇인가요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '당신이 가장 좋아하는 영화는 무엇인가요?'
);

INSERT INTO password_questions (question)
SELECT '어릴 적 장래희망은 무엇이었나요?'
WHERE NOT EXISTS (
  SELECT 1 FROM password_questions WHERE question = '어릴 적 장래희망은 무엇이었나요?'
);