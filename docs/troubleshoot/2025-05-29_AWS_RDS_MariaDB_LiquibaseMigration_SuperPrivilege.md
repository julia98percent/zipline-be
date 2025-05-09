# MariaDB RDS 트러블슈팅: Liquibase Migration 권한 문제 해결

## 문제 상황

Spring Boot 애플리케이션에서 Liquibase를 통한 데이터베이스 마이그레이션 수행 중 권한 오류가 발생했습니다.

### 오류 메시지
```
Caused by: java.sql.SQLException: (conn=33555) You do not have the SUPER privilege and binary logging is enabled (you *might* want to use the less safe log_bin_trust_function_creators variable)
```

### 오류 원인
AWS RDS MariaDB에서 트리거를 생성하거나 수정할 때 필요한 SUPER 권한이 없었습니다. 바이너리 로깅이 활성화된 상태에서는 이러한 권한이 필수적입니다.

## 해결 과정

### 1. 문제 분석

| 항목 | 설명 |
|------|------|
| 문제점 | Liquibase가 트리거를 관리하는 SQL 실행 시 권한 부족 |
| 영향 | 애플리케이션 시작 실패 |
| 원인 | AWS RDS에서는 보안상의 이유로 SUPER 권한을 기본적으로 제공하지 않음 |
| 해결 방안 | `log_bin_trust_function_creators` 파라미터 활성화 |

### 2. 해결 단계

#### 2.1. 현재 상태 확인

MariaDB 접속 후 현재 설정 확인:
```sql
SHOW VARIABLES LIKE 'log_bin_trust_function_creators';
```

#### 2.2. 임시 설정 시도 (실패)

```sql
SET GLOBAL log_bin_trust_function_creators = 1;
```

결과: 
```
ERROR 1227 (42000): Access denied; you need (at least one of) the BINLOG ADMIN privilege(s) for this operation
```

#### 2.3. AWS CLI를 통한 파라미터 변경

1. DB 인스턴스 식별자 확인:
   ```bash
   aws rds describe-db-instances --query 'DBInstances[*].[DBInstanceIdentifier]' --output text
   ```
   결과: `ziplinedb`

2. 파라미터 그룹 수정:
   ```bash
   aws rds modify-db-parameter-group \
     --db-parameter-group-name ziplinedb \
     --parameters "ParameterName=log_bin_trust_function_creators,ParameterValue=1,ApplyMethod=immediate"
   ```
   결과: 성공적으로 적용됨

3. 인스턴스 상태 확인:
   ```bash
   aws rds describe-db-instances --db-instance-identifier ziplinedb --query 'DBInstances[0].DBInstanceStatus' --output text
   ```
   결과: `modifying` 상태 확인

4. 대기 후 재부팅:
   ```bash
   aws rds reboot-db-instance --db-instance-identifier ziplinedb
   ```

## 해결 결과

| 항목 | 이전 | 이후 |
|------|------|------|
| 파라미터 설정 | log_bin_trust_function_creators = 0 | log_bin_trust_function_creators = 1 |
| 애플리케이션 상태 | 시작 실패 | 정상 작동 |
| Liquibase 마이그레이션 | 실패 | 성공 |

## 파라미터 설정 상세 설명

| 파라미터 | 설명 | 기본값 | 변경값 |
|----------|------|--------|--------|
| log_bin_trust_function_creators | 바이너리 로깅이 활성화된 상태에서 SUPER 권한 없이 트리거, 함수, 프로시저를 생성/수정할 수 있도록 허용 | 0 (비활성) | 1 (활성) |

## AWS RDS 파라미터 그룹 설정 방법

### AWS CLI 사용 방법

```bash
# 파라미터 그룹 확인
aws rds describe-db-instances --db-instance-identifier [DB_INSTANCE_NAME] --query 'DBInstances[0].DBParameterGroups[0].DBParameterGroupName' --output text

# 파라미터 수정
aws rds modify-db-parameter-group \
  --db-parameter-group-name [PARAMETER_GROUP_NAME] \
  --parameters "ParameterName=log_bin_trust_function_creators,ParameterValue=1,ApplyMethod=immediate"

# 인스턴스 재부팅
aws rds reboot-db-instance --db-instance-identifier [DB_INSTANCE_NAME]
```

### AWS 콘솔 사용 방법

1. AWS 관리 콘솔에 로그인
2. RDS 서비스로 이동
3. 왼쪽 메뉴에서 "파라미터 그룹" 선택
4. 해당 파라미터 그룹 찾기 및 선택
5. "파라미터 편집" 클릭
6. `log_bin_trust_function_creators` 검색
7. 값을 `1` 또는 `true`로 변경
8. 변경 사항 저장
9. 필요시 DB 인스턴스 재부팅

## 추가 권장 사항
1. 보안 고려사항: `log_bin_trust_function_creators` 활성화는 보안 수준을 약간 낮추므로, 필요한 경우에만 사용하는 것이 좋습니다.
2. 권한 관리: 개발 환경과 프로덕션 환경에서 다른 접근 방식을 사용하는 것이 좋습니다.

