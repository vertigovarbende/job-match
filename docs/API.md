# API Design

Base path:

```text
/api/v1
```

## Authentication

```http
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout
POST /auth/forgot-password
POST /auth/reset-password
```

## Candidate

```http
POST   /candidates/me
GET    /candidates/me
PUT    /candidates/me

GET    /candidates/me/skills
POST   /candidates/me/skills
PUT    /candidates/me/skills/{skillId}
DELETE /candidates/me/skills/{skillId}

GET    /candidates/me/languages
POST   /candidates/me/languages
PUT    /candidates/me/languages/{languageId}
DELETE /candidates/me/languages/{languageId}

GET    /candidates/me/experiences
POST   /candidates/me/experiences
PUT    /candidates/me/experiences/{experienceId}
DELETE /candidates/me/experiences/{experienceId}

GET    /candidates/me/educations
POST   /candidates/me/educations
PUT    /candidates/me/educations/{educationId}
DELETE /candidates/me/educations/{educationId}

GET    /candidates/me/certifications
POST   /candidates/me/certifications
PUT    /candidates/me/certifications/{certificationId}
DELETE /candidates/me/certifications/{certificationId}
```

Toplam 23 endpoint (Profile 3 + Skills/Languages/Experiences/Educations/Certifications 4'er).
`{skillId}`/`{languageId}`/`{experienceId}`/`{educationId}`/`{certificationId}` her zaman
current-user'ın (JWT'den `CurrentUserFacade` ile çözülür, bkz. REST.md #6) kendi
`candidateId`'sine ait alt-kaynaklardır -- path'te veya body'de ayrıca bir `candidateId`
gönderilmez (`CurrentCandidateFacade`, bkz. REST.md #12). Skills/Languages/Experiences/
Educations/Certifications hepsi düz liste döner, **hiçbirinde pagination yok** (bkz. REST.md
#2, #14) -- yukarıdaki "Search"/"Catalog" bölümlerindeki `JmPage`/`JmPageResponse` zarfı
burada kullanılmaz.

Örnek -- profil oluşturma:

```http
POST /api/v1/candidates/me
```

(Body yok -- `Candidate.create(userId)` yalnızca current-user'dan gelen `userId`'yi kullanır,
bkz. REST.md #13.)

Örnek response (201):

```json
{
  "time": "2026-09-17T10:00:00",
  "code": "3f2b1c4a-...",
  "success": true,
  "response": {
    "id": 42,
    "userId": 7,
    "headline": null,
    "summary": null,
    "location": null,
    "desiredSalary": null,
    "workplacePreferences": null
  }
}
```

Örnek -- profil güncelleme (`Money`/`SalaryRange`/`WorkplacePreferences` nested şekli):

```http
PUT /api/v1/candidates/me
```

```json
{
  "headline": "Senior Backend Developer",
  "summary": "8+ yıl Java/Spring deneyimi",
  "location": { "country": "Turkey", "city": "Istanbul" },
  "workplacePreferences": { "acceptedTypes": ["REMOTE", "HYBRID"] },
  "desiredSalary": {
    "min": { "amount": 80000, "currency": "USD" },
    "max": { "amount": 110000, "currency": "USD" }
  }
}
```

Örnek response (200):

```json
{
  "time": "2026-09-17T10:05:00",
  "code": "3f2b1c4b-...",
  "success": true,
  "response": {
    "id": 42,
    "userId": 7,
    "headline": "Senior Backend Developer",
    "summary": "8+ yıl Java/Spring deneyimi",
    "location": { "country": "Turkey", "city": "Istanbul" },
    "desiredSalary": {
      "min": { "amount": 80000, "currency": "USD" },
      "max": { "amount": 110000, "currency": "USD" }
    },
    "workplacePreferences": { "acceptedTypes": ["REMOTE", "HYBRID"] }
  }
}
```

`location`/`desiredSalary`/`workplacePreferences` opsiyoneldir (gönderilmezse `null` kalır),
`SalaryRange`'in kendi iç kuralları (`min <= max`, aynı `currency`) DTO'da değil domain'de
kontrol edilir (bkz. REST.md #13).

Örnek -- skill listeleme ve ekleme (Skills/Languages simetrik, bkz. REST.md #14):

```http
GET /api/v1/candidates/me/skills
```

```json
{
  "time": "2026-09-17T10:10:00",
  "code": "3f2b1c4c-...",
  "success": true,
  "response": [
    { "id": 1, "candidateId": 42, "skillId": 5, "proficiencyLevel": "ADVANCED" }
  ]
}
```

```http
POST /api/v1/candidates/me/skills
```

```json
{ "skillId": 5, "proficiencyLevel": "ADVANCED" }
```

`PUT /candidates/me/skills/{skillId}` yalnızca `{ "proficiencyLevel": "EXPERT" }` gönderir
(`skillId` path'te). `DELETE` body almaz. `GET /candidates/me/languages` aynı şekli kullanır
(`CandidateLanguageResponse(id, candidateId, languageId, proficiencyLevel)`).

Örnek -- deneyim ekleme (Experiences/Educations/Certifications simetrik, bkz. REST.md #15):

```http
POST /api/v1/candidates/me/experiences
```

```json
{
  "title": "Backend Developer",
  "company": "Example Tech",
  "location": { "country": "Turkey", "city": "Istanbul" },
  "employmentType": "FULL_TIME",
  "startDate": "2021-03-01",
  "endDate": null,
  "description": "Java/Spring Boot ile mikroservis geliştirme."
}
```

Örnek response (200):

```json
{
  "time": "2026-09-17T10:15:00",
  "code": "3f2b1c4d-...",
  "success": true,
  "response": {
    "id": 11,
    "candidateId": 42,
    "title": "Backend Developer",
    "company": "Example Tech",
    "location": { "country": "Turkey", "city": "Istanbul" },
    "employmentType": "FULL_TIME",
    "startDate": "2021-03-01",
    "endDate": null,
    "description": "Java/Spring Boot ile mikroservis geliştirme."
  }
}
```

`GET /candidates/me/educations`/`GET /candidates/me/certifications` aynı zarfla düz liste
döner (`EducationResponse`/`CertificationResponse`); `endDate >= startDate` /
`expiryDate >= issueDate` kontrolleri DTO'da değil domain'de yapılır (bkz. REST.md #5, #15).

## Companies

```http
POST   /companies
GET    /companies/{companyId}
PUT    /companies/{companyId}
POST   /companies/{companyId}/verify

GET    /companies/{companyId}/members
POST   /companies/{companyId}/members
DELETE /companies/{companyId}/members/{userId}
```

Toplam 7 endpoint (Profile 4 + Membership 3). `GET`/`PUT`/membership endpoint'lerinde
ownership/görünürlük kısıtlaması yok -- yalnızca genel `anyRequest().authenticated()`
politikası geçerli (bkz. REST.md #8); `listMembers`'daki ownership sorusu bilinçli bir
erteleme olarak açık (bkz. REST.md #9).

Örnek -- şirket oluşturma:

```http
POST /api/v1/companies
```

```json
{ "name": "Example Tech" }
```

Örnek response (201):

```json
{
  "time": "2026-09-17T10:00:00",
  "code": "3f2b1c4e-...",
  "success": true,
  "response": {
    "id": 5,
    "name": "Example Tech",
    "description": null,
    "industry": null,
    "website": null,
    "size": null,
    "headquarters": null,
    "verified": false
  }
}
```

Örnek -- profil güncelleme (`headquarters` nested şekli, bkz. REST.md #8):

```http
PUT /api/v1/companies/5
```

```json
{
  "name": "Example Tech",
  "description": "Java/Spring danışmanlık ve ürün geliştirme.",
  "industry": "Software",
  "website": "https://example-tech.com",
  "size": "SMALL_MEDIUM",
  "headquarters": { "country": "Turkey", "city": "Istanbul" }
}
```

Örnek response (200):

```json
{
  "time": "2026-09-17T10:05:00",
  "code": "3f2b1c4f-...",
  "success": true,
  "response": {
    "id": 5,
    "name": "Example Tech",
    "description": "Java/Spring danışmanlık ve ürün geliştirme.",
    "industry": "Software",
    "website": "https://example-tech.com",
    "size": "SMALL_MEDIUM",
    "headquarters": { "country": "Turkey", "city": "Istanbul" },
    "verified": false
  }
}
```

Örnek -- doğrulama:

```http
POST /api/v1/companies/5/verify
```

(Body yok -- `actorId`, current-user'dan çözülür, bkz. REST.md #6.) Response, `verified`
alanı `true` olan aynı `CompanyResponse` zarfını döner.

Örnek -- üyelik ekleme ve listeleme:

```http
POST /api/v1/companies/5/members
```

```json
{ "userId": 12 }
```

```json
{
  "time": "2026-09-17T10:10:00",
  "code": "3f2b1c50-...",
  "success": true,
  "response": { "id": 1, "companyId": 5, "userId": 12, "joinedAt": "2026-09-17T10:10:00" }
}
```

```http
GET /api/v1/companies/5/members
```

```json
{
  "time": "2026-09-17T10:11:00",
  "code": "3f2b1c51-...",
  "success": true,
  "response": [
    { "id": 1, "companyId": 5, "userId": 12, "joinedAt": "2026-09-17T10:10:00" }
  ]
}
```

`DELETE /companies/{companyId}/members/{userId}` body almaz, gerçek bir HTTP 204 değil,
200 + `BaseResponse<Void>` döner (bkz. REST.md #9). Üyelik listesi de -- Candidate'in
alt-kaynakları gibi -- pagination KULLANMAZ, düz liste döner.

## Jobs

```http
POST   /jobs
GET    /jobs/{jobId}
PUT    /jobs/{jobId}
POST   /jobs/{jobId}/publish
POST   /jobs/{jobId}/close
DELETE /jobs/{jobId}
```

## Search

```http
GET /jobs/search
```

Örnek:

```http
GET /api/v1/jobs/search?q=java+spring&skills=java,spring&workplace=REMOTE&seniority=MID&salaryMin=60000&pageable.page=1&pageable.pageSize=20&sort=relevance
```

Örnek response:

```json
{
  "content": [
    {
      "id": "job-123",
      "title": "Java Backend Developer",
      "company": {
        "id": "company-1",
        "name": "Example Tech"
      },
      "location": "Istanbul",
      "workplaceType": "HYBRID",
      "requiredSkills": ["Java", "Spring Boot", "PostgreSQL"],
      "publishedAt": "2026-08-30T15:00:00Z",
      "highlights": {
        "title": ["<em>Java</em> Backend Developer"]
      }
    }
  ],
  "pageNumber": 1,
  "pageSize": 20,
  "totalPageCount": 13,
  "totalElementCount": 243,
  "orderedBy": null,
  "filteredBy": null
}
```

**Not (pagination konvansiyonu):** Bu şekil, `feature/phase-02/rest` branch'inde `shared`
altında yazılan `JmPageable`/`JmPage`/`JmPageResponse` sınıflarının gerçek karşılığıdır (bkz.
`REST.md` #2, #10) -- daha önceki bir taslakta kullanılan `items`/`page`/`size`/`total` şekli
(0-tabanlı sayfa) **terk edildi**. Sayfa numarası **1-tabanlıdır** (`page=1` ilk sayfa, ne
istek ne de yanıtta hiçbir yerde 0-tabanlı sayfa yok). Sayfalama/sıralama parametreleri
`pageable.page`/`pageable.pageSize`/`pageable.orders[i].property`/`pageable.orders[i].direction`
şeklinde nested query param olarak gönderilir (`JmPagingRequest.pageable: JmPageable` alanının
Spring'in `@ModelAttribute` nested binding'iyle doldurulması). `filteredBy`, o endpoint'in
Presentation/Infrastructure katmanındaki filter objesinin (basit bir POJO, `Specification`
üreten `toSpecification()` metodu Jackson tarafından hiç çağrılmaz/serialize edilmez) düz JSON
karşılığıdır -- filtre uygulanmadıysa `null`. Somut, gerçek kodlanmış bir örnek için "Catalog"
bölümüne bakınız.

## Catalog

```http
GET /skills
GET /languages
```

Örnek:

```http
GET /api/v1/skills?name=java&pageable.page=1&pageable.pageSize=20
```

Örnek response:

```json
{
  "time": "2026-09-17T10:00:00",
  "code": "3f2b1c4a-...",
  "success": true,
  "response": {
    "content": [
      { "id": 1, "name": "Java" },
      { "id": 2, "name": "JavaScript" }
    ],
    "pageNumber": 1,
    "pageSize": 2,
    "totalPageCount": 1,
    "totalElementCount": 2,
    "orderedBy": null,
    "filteredBy": { "name": "java" }
  }
}
```

`name` opsiyoneldir (yoksa filtresiz tüm liste sayfalanır). `GET /languages` aynı şekli
kullanır (`LanguageResponse(id, name)`).

## Applications

```http
POST /jobs/{jobId}/applications
GET  /candidates/me/applications
GET  /jobs/{jobId}/applications
PATCH /applications/{applicationId}/status
POST /applications/{applicationId}/withdraw
```

## Matching

```http
GET /jobs/{jobId}/match
GET /candidates/me/recommended-jobs
```

Örnek response:

```json
{
  "jobId": "job-123",
  "score": 84,
  "breakdown": {
    "requiredSkills": 42,
    "preferredSkills": 10,
    "experience": 15,
    "location": 10,
    "seniority": 7,
    "salary": 0
  },
  "matchedSkills": ["Java", "Spring Boot"],
  "missingSkills": ["Kafka"]
}
```

## Error Format

Hatalar `ErrorResponse` formatında döner (`shared/presentation/response/ErrorResponse.java`, bkz. ARCHITECTURE.md #3):

```json
{
  "time": "2026-08-31T14:23:10.512",
  "code": "GEN_002",
  "header": "VALIDATION_ERROR",
  "message": "Validation failed.",
  "isSuccess": false,
  "subErrors": [
    {
      "message": "must not be blank",
      "field": "title",
      "value": "",
      "type": "NotBlank"
    }
  ]
}
```

Alan açıklamaları:

- `time`: Hatanın oluştuğu zaman.
- `code`: İlgili `ErrorCode` implementasyonundan gelen makine-okunabilir kod (örn. `GEN_002`), bkz. `CommonErrorCode` (genel/framework hataları) ve modül-özel `ErrorCode` implementasyonları (örn. ileride eklenecek `JobErrorCode`).
- `header`: Hatanın kısa kategorisi (`ErrorCode` enum sabitinin adı, örn. `VALIDATION_ERROR`).
- `message`: İnsan-okunabilir özet mesaj.
- `isSuccess`: Her zaman `false`. Başarılı response'lar `BaseResponse` ile döner (bkz. ARCHITECTURE.md #3), farklı bir zarf kullanır — `ErrorResponse` ile karıştırılmamalıdır.
- `subErrors`: Yalnızca alan bazlı doğrulama hatası varsa doludur; yoksa response'ta hiç yer almaz. Her `subError` şu alanları taşır: `message` (hata mesajı), `field` (ilgili alan adı), `value` (reddedilen değer, varsa), `type` (ihlal edilen kısıtın adı, örn. `NotBlank`, `Size`). `value` ve `type` yalnızca doluysa serialize edilir.

`subErrors` şu durumlarda doldurulur: `MethodArgumentNotValidException` (request body validasyonu), `ConstraintViolationException` (path/query parametre veya servis seviyesi validasyonu), `MethodArgumentTypeMismatchException` (tip uyuşmazlığı), `InvalidFormatException` (bozuk JSON body'de bir alanın hedef tipe uymaması — `HttpMessageNotReadableException`'ın cause'u bu tipteyse doldurulur, saf sözdizimi hatalarında `subErrors` boş kalır). Bkz. `shared/presentation/exception/handler/GlobalExceptionHandler.java`.

Yukarıdaki örnek bir validasyon hatasıdır (`GEN_002` / `VALIDATION_ERROR`); `code`, `header` ve HTTP status, hangi exception ailesinin fırladığına göre değişir (ör. "kaynak bulunamadı" durumunda 404 ile birlikte modül-özel bir kod döner, `subErrors` bu durumlarda genellikle boştur). Exception hiyerarşisi ve hangi ailenin hangi statüye eşlendiği için bkz. ARCHITECTURE.md #11 ve ADR-011.
