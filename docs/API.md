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

RFC 7807 Problem Details benzeri ortak format kullanılabilir:

```json
{
  "type": "https://api.example.com/problems/job-not-found",
  "title": "Job not found",
  "status": 404,
  "detail": "The requested job could not be found.",
  "instance": "/api/v1/jobs/123",
  "traceId": "..."
}
```
