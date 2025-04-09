# Docker 환경변수 문제 트러블슈팅

## 문제 상황
GitHub Actions 워크플로우에서 Docker 컨테이너 실행 시 환경변수 설정 과정에서 오류가 발생했습니다.

## 트러블슈팅 과정

### 1. 초기 문제: 환경변수 설정 오류

**문제 현상:**
```
"docker run" requires at least 1 argument.
See 'docker run --help'.
ocker run [OPTIONS] IMAGE [COMMAND] [ARG...]
Create and run a new container from an image
zsh:2***: command not found: -e
zsh:44: command not found: -e
zsh:46: command not found: -e
202***/04/08 08:***2:***9 Process exited with status 127
Error: Process completed with exit code 1.
```
**원인:**
- 쉘 스크립트에서 `-e` 플래그로 환경변수를 설정할 때 특수문자가 포함된 값들이 쉘 인터프리터에 의해 잘못 해석됨
- 특히 여러 환경변수가 연속적으로 사용될 때 문제가 더욱 심화됨
**시도한 해결책:**

#### Before
  ```
-e REDIS_URL=${{ secrets.REDIS_URL }} \
```
#### After
```
-e REDIS_URL="${{ secrets.REDIS_URL }}" \
```

- 환경변수 값에 따옴표(`""`) 적용
- 결과: 3개의 에러 중 2개가 해결되었으나 1개의 에러가 여전히 남아있음

### 2. 남은 문제: 파일 디스크립터 오류

**문제 현상:**
```
docker: open /dev/fd/6***: no such file or directory.
See 'docker run --help'.
202***/04/08 1***:48:11 Process exited with status 12***
```

**원인:**
- 프로세스 치환 방식(`<(cat << EOL...)`)이 EC2 인스턴스의 쉘 환경에서 제대로 지원되지 않음
- Docker가 파일 디스크립터를 읽을 수 없는 상황 발생

### 3. 최종 해결책: 환경변수 파일 사용

**해결 방법:**
1. 로컬에서 임시 환경변수 파일 생성
   ```bash
   cat << 'EOF' > /tmp/docker-env-vars
   PORT=${{ secrets.PORT }}
   DB_PORT=${{ secrets.DB_PORT }}
   # ... 기타 환경변수들 ...
   EOF
   ```

2. 환경변수 파일을 사용하여 Docker 컨테이너 실행
   ```bash
   sudo docker run -d -p ${{ secrets.PORT }}:${{ secrets.PORT }} \
   --name ${{ secrets.PROJECT_NAME }} \
   --env-file /tmp/docker-env-vars \
   ${{ secrets.DOCKERHUB_USERNAME }}/${{ secrets.PROJECT_NAME }}
   ```

3. 사용 후 환경변수 파일 삭제
   ```bash
   rm /tmp/docker-env-vars
   ```

## 교훈

1. **쉘 스크립트에서 환경변수 처리 시 주의사항**
   - 특수문자가 포함된 환경변수는 적절한 이스케이프나 따옴표 처리가 필요
   - 환경변수가 많을 경우 개별 `-e` 플래그보다 환경변수 파일 사용이 안정적

2. **Docker 환경변수 설정 방법**
   - `--env-file` 옵션을 사용하면 여러 환경변수를 안전하게 전달 가능
   - 임시 파일을 생성하고 사용 후 삭제하는 패턴은 보안 측면에서도 권장됨

3. **CI/CD 파이프라인 디버깅**
   - 환경변수 관련 문제는 종종 특수문자, 공백, 줄바꿈 등에서 발생
   - 단계적으로 문제를 분리하여 해결하는 접근이 효과적

## 최종 적용된 해결책

환경변수 파일을 사용한 Docker 실행 방식을 GitHub Actions 워크플로우에 적용하여 문제를 해결했습니다. 이 방식은 특수문자가 포함된 환경변수도 안전하게 처리할 수 있으며, 스크립트의 가독성과 유지보수성도 향상시킵니다.


## 위험성 

ec2인스턴스 내에 중요한 비밀정보가 있는 파일이 생성되었다 삭제되는 구조라 보안에 취약할 수도 있을것 같습니다.
