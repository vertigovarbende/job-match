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
GET    /candidates/me
PUT    /candidates/me
POST   /candidates/me/skills
DELETE /candidates/me/skills/{skillId}
POST   /candidates/me/experiences
PUT    /candidates/me/experiences/{experienceId}
DELETE /candidates/me/experiences/{experienceId}
```

## Companies

```http
POST /companies
GET  /companies/{companyId}
PUT  /companies/{companyId}
```

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
GET /api/v1/jobs/search?q=java+spring&skills=java,spring&workplace=REMOTE&seniority=MID&salaryMin=60000&page=0&size=20&sort=relevance
```

Örnek response:

```json
{
  "items": [
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
  "page": 0,
  "size": 20,
  "total": 243
}
```

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
