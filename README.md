# 🌟 Spoony 🌟
![spoony-intro](https://github.com/user-attachments/assets/1e66bf9c-6929-4cc2-9bf3-4517e883dfa3)

<br>

## 🥄 SERVICE INTRODUCTION
> - Spoony는 리뷰 작성자의 **신뢰도와 영향력**에 집중하여, 신뢰성 있는 장소 정보를 제공하는 **맛집 추천 서비스**입니다. <br>
> - Spoony에서, 사용자는 다양한 사람들의 **찐 맛집 리스트**를 탐방하며, 나만의 찐 맛집 리스트를 완성해나갈 수 있습니다!

<br>

## 🥄 USER EXPERIENCE

### 📖 Spoony 사용법

>1️⃣ **장소 등록하고 수저 획득하기** <br>
>- 나만의 맛집, 분위기 좋은 카페, 핫한 펍을 **직접 등록하고 수저를 획득**하세요!
>
>2️⃣**신뢰도 높은 찐 리스트 떠먹기** <br>
>- 모은 수저로 **다른 유저의 찐 리스트를 확인**하세요!
>- 원하는 유저를 **팔로우**하고, **지역별 랭킹**을 통해 믿을 수 있는 리뷰를 찾아보세요.
>
>3️⃣ **나만의 지도 완성하기** <br>
>- 떠먹은 리스트에서 좋아하는 장소를 추가해, **나만의 맞춤형 맛집 지도**를 완성해보세요!

<br>

## 🥄 CONTRIBUTORS (SERVER)

<div align="center">

| 전윤혁<br/>([@airoca](https://github.com/airoca)) | 이수민<br/>([@dltnals317](https://github.com/dltnals317)) |
|:------------------------------------------------:|:------------------------------------------------:|
| <img width="270px" src="https://github.com/user-attachments/assets/d3546e81-6d63-4d3a-bdd3-00ec88244ea7"/> | <img width="270px" src="https://github.com/user-attachments/assets/d5dca2ea-5a3c-40d1-bc4b-180edd1556e7"/> |
| `Back-End & Infra` | `Back-End` |

</div>

<br>

## 📸 SCREENSHOTS

| VIEW  | 1 | 2 | 3 |
|:-----:|:--------------------------------------------------------------:|:--------------------------------------------------------------:|:--------------------------------------------------------------:|
| **Explore** | <img src="https://github.com/user-attachments/assets/dc0c8185-ebaf-4eb4-bc8a-f502b5c8082c" width="200"/> | <img src="https://github.com/user-attachments/assets/bd3efd23-dcf7-46ba-9444-233c1f17fe7c" width="200"/> | <img src="https://github.com/user-attachments/assets/c4ed289e-8392-47a4-9c45-c18bf34ffbf7" width="200"/> |
| **Post** | <img src="https://github.com/user-attachments/assets/3d59742f-5f49-429b-bcb4-a48acef84574" width="200"/> | <img src="https://github.com/user-attachments/assets/63d82360-49b7-4aa8-8d94-d0d0d8047658" width="200"/> | <img src="https://github.com/user-attachments/assets/6156985e-6103-42bd-b2fb-acb37e468344" width="200"/> |
| **Report** | <img src="https://github.com/user-attachments/assets/2c9486e6-5608-4676-b301-de487d42913d" width="200"/> | <img src="https://github.com/user-attachments/assets/134940f7-7aca-47c7-ad2d-e3f3e9c236fa" width="200"/> | <img src="https://github.com/user-attachments/assets/cfada0cd-93c8-4c61-bded-254b3e179e15" width="200"/> |

<br>

## 🗂️ PACKAGE CONVENTION

```
📦 spoony_server
├─ 📂 .github
│  ├─ 📂 ISSUE_TEMPLATE
│  └─ 📂 workflows
├─ 📂 .gradle
│  ├─ 📂 8.11.1
│  │  ├─ 📂 checksums
│  │  ├─ 📂 executionHistory
│  │  ├─ 📂 expanded
│  │  ├─ 📂 fileChanges
│  │  ├─ 📂 fileHashes
│  │  └─ 📂 vcsMetadata
│  ├─ 📂 buildOutputCleanup
│  └─ 📂 vcs-1
├─ 📂 .idea
│  ├─ 📂 inspectionProfiles
│  └─ 📂 modules
├─ 📂 build
│  ├─ 📂 classes
│  │  └─ 📂 java
│  │      └─ 📂 main
│  │          └─ 📂 com
│  │              └─ 📂 spoony
│  │                  └─ 📂 spoony_server
│  │                      ├─ 📂 adapter
│  │                      │  ├─ 📂 auth
│  │                      │  │  ├─ 📂 dto
│  │                      │  │  │  ├─ 📂 request
│  │                      │  │  │  ├─ 📂 response
│  │                      │  │  │  └─ 📂 verification
│  │                      │  │  │      ├─ 📂 apple
│  │                      │  │  │      └─ 📂 kakao
│  │                      │  │  ├─ 📂 in
│  │                      │  │  │  └─ 📂 web
│  │                      │  │  ├─ 📂 out
│  │                      │  │  │  ├─ 📂 external
│  │                      │  │  │  └─ 📂 persistence
│  │                      │  │  └─ 📂 verification
│  │                      │  │      └─ 📂 apple
│  │                      │  ├─ 📂 dto
│  │                      │  ├─ 📂 in
│  │                      │  │  └─ 📂 web
│  │                      │  ├─ 📂 out
│  │                      │  │  ├─ 📂 external
│  │                      │  │  ├─ 📂 infra
│  │                      │  │  │  └─ 📂 s3
│  │                      │  │  └─ 📂 persistence
│  │                      ├─ 📂 application
│  │                      │  ├─ 📂 auth
│  │                      │  │  ├─ 📂 port
│  │                      │  │  │  ├─ 📂 in
│  │                      │  │  │  └─ 📂 out
│  │                      │  │  └─ 📂 service
│  │                      │  ├─ 📂 event
│  │                      │  ├─ 📂 port
│  │                      │  │  ├─ 📂 command
│  │                      │  │  ├─ 📂 in
│  │                      │  │  └─ 📂 out
│  │                      │  └─ 📂 service
│  │                      ├─ 📂 domain
│  │                      │  ├─ 📂 feed
│  │                      │  ├─ 📂 location
│  │                      │  ├─ 📂 place
│  │                      │  ├─ 📂 post
│  │                      │  ├─ 📂 report
│  │                      │  ├─ 📂 spoon
│  │                      │  ├─ 📂 user
│  │                      │  └─ 📂 zzim
│  │                      ├─ 📂 global
│  │                      │  ├─ 📂 advice
│  │                      │  ├─ 📂 annotation
│  │                      │  ├─ 📂 auth
│  │                      │  │  ├─ 📂 annotation
│  │                      │  │  ├─ 📂 constant
│  │                      │  │  ├─ 📂 dto
│  │                      │  │  ├─ 📂 filter
│  │                      │  │  ├─ 📂 handler
│  │                      │  │  └─ 📂 jwt
│  │                      │  ├─ 📂 config
│  │                      │  ├─ 📂 dto
│  │                      │  ├─ 📂 exception
│  │                      │  └─ 📂 message
│  │                      │      ├─ 📂 auth
│  │                      │      └─ 📂 business
│  │                      └─ 📂 test
│  ├─ 📂 generated
│  ├─ 📂 reports
│  ├─ 📂 resources
│  ├─ 📂 tmp
├─ 📂 gradle
│  └─ 📂 wrapper
├─ 📂 out
│  ├─ 📂 production
│  ├─ 📂 resources
└─ 📂 src
    ├─ 📂 main
    │  ├─ 📂 generated
    │  ├─ 📂 java
    │  │  └─ 📂 com
    │  │      └─ 📂 spoony
    │  │          └─ 📂 spoony_server
    │  │              ├─ 📂 adapter
    │  │              ├─ 📂 application
    │  │              ├─ 📂 domain
    │  │              ├─ 📂 global
    │  │              └─ 📂 test
    ├─ 📂 resources
    └─ 📂 test
```

<br>

## 🚀 Tech Stack

### 🛠 **Languages & Frameworks**
- ![Java 21](https://img.shields.io/badge/Java%2021-007396?style=flat-square&logo=openjdk&logoColor=white) 
- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
- ![Spring Event](https://img.shields.io/badge/Spring%20Event-6DB33F?style=flat-square&logo=spring&logoColor=white)
- ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
- ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)

### 🗄 **Databases & Caching**
- ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
- ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

### ☁ **Infrastructure & DevOps**
- ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
- ![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=flat-square&logo=amazonaws&logoColor=white)
- ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=flat-square&logo=amazons3&logoColor=white)
- ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
- ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)

### 📜 **API & Documentation**
- ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white)
