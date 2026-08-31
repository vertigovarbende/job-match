# Requirements

## 1. Functional Requirements

### 1.1 Authentication

- Kullanıcı email ve password ile kayıt olabilir.
- Email doğrulaması yapılabilir.
- Kullanıcı login olabilir.
- Access token ve refresh token kullanılabilir.
- Kullanıcı logout olabilir.
- Şifre sıfırlama akışı bulunabilir.

### 1.2 Candidate Profile

Candidate aşağıdaki bilgileri yönetebilir:

- Ad ve soyad
- Başlık / headline
- Hakkında
- Lokasyon
- Remote çalışma tercihi
- Minimum maaş beklentisi
- Skill listesi
- Skill proficiency seviyesi
- İş deneyimleri
- Eğitim bilgileri
- Tercih edilen çalışma türleri
- Tercih edilen seniority seviyeleri

### 1.3 Employer & Company

Employer:

- Company oluşturabilir.
- Company profilini güncelleyebilir.
- Şirket çalışanlarını yönetebilir.
- İş ilanı oluşturabilir.

Company alanları:

- name
- description
- website
- industry
- companySize
- headquarters
- verified

### 1.4 Job Posting

Bir ilan:

- title
- description
- requiredSkills
- preferredSkills
- seniority
- employmentType
- workplaceType
- location
- salaryMin
- salaryMax
- currency
- experienceMinYears
- status
- publishedAt
- expiresAt

alanlarını içerebilir.

Status değerleri:

```text
DRAFT
PUBLISHED
CLOSED
EXPIRED
ARCHIVED
```

### 1.5 Job Search

Kullanıcı:

- Keyword ile arama yapabilir.
- Skill'e göre filtreleyebilir.
- Lokasyona göre filtreleyebilir.
- Remote / hybrid / onsite seçebilir.
- Seniority filtreleyebilir.
- Employment type filtreleyebilir.
- Maaş aralığı filtreleyebilir.
- Company filtreleyebilir.
- Tarihe göre sıralayabilir.
- Relevance'a göre sıralayabilir.

### 1.6 Search Experience

Sistem ileride:

- autocomplete
- typo tolerance
- synonyms
- highlighting
- popular searches
- recent searches

özellikleri sağlayabilir.

### 1.7 Applications

Candidate:

- Bir ilana başvurabilir.
- Aynı ilana ikinci kez başvuramaz.
- Başvurularını görüntüleyebilir.
- Uygun aşamada başvurusunu geri çekebilir.

Employer:

- İlan başvurularını görüntüleyebilir.
- Başvuru durumunu değiştirebilir.

Application status:

```text
SUBMITTED
UNDER_REVIEW
INTERVIEW
OFFER
REJECTED
WITHDRAWN
HIRED
```

### 1.8 Matching

Sistem Candidate ile Job arasında 0-100 arasında bir matching score hesaplar.

İlk sürümde aşağıdaki kriterler kullanılabilir:

- Required skill coverage: %45
- Preferred skill coverage: %15
- Experience compatibility: %15
- Location/workplace compatibility: %10
- Seniority compatibility: %10
- Salary compatibility: %5

Matching motoru ilk aşamada deterministik olmalıdır. ML tabanlı ranking daha sonraki aşamaya bırakılabilir.

## 2. Non-Functional Requirements

### Performance

- Search endpoint'i normal yük altında hedef olarak p95 < 300ms cevap vermelidir.
- Pagination zorunludur.
- Deep pagination kontrol altına alınmalıdır.

### Availability

- Elasticsearch geçici olarak erişilemezse job creation işlemi başarısız olmamalıdır.
- PostgreSQL write işlemi Elasticsearch bağımlılığı yüzünden rollback edilmemelidir.

### Consistency

- PostgreSQL source of truth'tur.
- Elasticsearch eventual consistent read model'dir.

### Security

- Password'ler güçlü hash algoritması ile tutulmalıdır.
- Role-based authorization uygulanmalıdır.
- Employer yalnızca yetkili olduğu company ilanlarını değiştirebilmelidir.
- IDOR kontrolleri use-case seviyesinde yapılmalıdır.

### Observability

- Structured logging kullanılmalıdır.
- Request correlation/trace ID eklenmelidir.
- Kritik operasyonlar audit edilebilir olmalıdır.
