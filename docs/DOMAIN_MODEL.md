# Domain Model

> **Prensip:** Bu proje rich domain model kullanır, anemic domain model'den kaçınılır (bkz. ADR-010). Aşağıdaki modeller yalnızca veri alanlarından ibaret değildir; invariant'lar ve state geçişleri kendi davranışları (`publish()`, `close()`, `withdraw()` vb.) üzerinden yönetilir, public setter ile dışarıdan serbestçe değiştirilmezler. JPA persistence entity'leri bunlardan ayrı tutulur (bkz. ARCHITECTURE.md).

## Core Aggregates

### User

```text
User
- id
- email
- passwordHash
- role
- status
- createdAt
```

### Candidate

```text
Candidate
- id
- userId
- headline
- summary
- location
- workplacePreferences
- desiredSalary
- skills[]
- experiences[]
- educations[]
```

### Company

```text
Company
- id
- name
- description
- industry
- website
- size
- verified
```

### Job

```text
Job
- id
- companyId
- title
- description
- requiredSkills[]
- preferredSkills[]
- seniority
- employmentType
- workplaceType
- location
- salaryRange
- minimumExperience
- status
- publishedAt
- expiresAt
```

Job davranışları:

```text
publish()
close()
archive()
updateDetails()
```

Business invariant örnekleri:

- DRAFT olmayan bir job tekrar publish edilemez.
- salaryMin > salaryMax olamaz.
- PUBLISHED ilan gerekli minimum alanlara sahip olmalıdır.
- CLOSED ilan için yeni application oluşturulamaz.

### JobApplication

```text
JobApplication
- id
- jobId
- candidateId
- status
- coverLetter
- appliedAt
```

Kurallar:

- Candidate aynı job'a bir kez başvurabilir.
- Employer kendisine ait olmayan ilana ait başvuruyu değiştiremez.
- REJECTED/HIRed gibi terminal durumlar için geçiş kuralları tanımlanmalıdır.

## Value Objects

Önerilen value object'ler:

```text
EmailAddress
Money
SalaryRange
Location
SkillLevel
JobId
CandidateId
CompanyId
MatchScore
```

Örneğin:

```java
public record MatchScore(int value) {
    public MatchScore {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Match score must be between 0 and 100");
        }
    }
}
```

## Matching Domain

```text
MatchResult
- candidateId
- jobId
- totalScore
- skillScore
- experienceScore
- locationScore
- seniorityScore
- salaryScore
- missingSkills[]
- matchedSkills[]
```

Bu sonuç sonradan açıklanabilir matching için kullanılabilir.
