# KYC Stream Agent

# require env
java17 이상의 환경

# Install
다음 요청을 통해 실행 jar을 설치가능
```shell
curl -L -o kyc-agent-0.0.1.jar https://github.com/KeepYourCost/StreamAgent/releases/download/v0.0.1/kyc-agent-0.0.1.jar
```

# Manifest 설정

환경변수로 `KYC_MANIFEST` 등록  
해당 경로에 `kyc.manifest` 파일 작성

## manifest 형식
```manifest
/usr/kyc/dir
/usr/kyc/temp.img
```
백업을 희망하는 dir 경로과 file 경로를 라인별로 작성

# Backup 시작 
`POST localhost:5050/write/{spotID}`로 요청  
manifest 에 등록된 파일을 전부 produce

# Recover 시작
`POST localhost:5050/read/{spotID}`로 요청  
`TOPIC {spotId}`에 있는 모든 파일을 consume