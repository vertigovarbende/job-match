# Security Design

## Authentication

Kimlik doğrulama Keycloak üzerinden yapılır (bkz. ADR-008). Uygulama kendi access/refresh token'ını issue etmez.

Model:

- Keycloak, OAuth2/OIDC Identity Provider olarak short-lived access token ve rotating refresh token üretir.
- Spring Security, `spring-boot-starter-oauth2-resource-server` ile OAuth2 Resource Server olarak çalışır; gelen Bearer JWT, Keycloak'ın JWK Set endpoint'i üzerinden doğrulanır.
- Refresh token revocation ve session yönetimi Keycloak admin console/API üzerinden yapılır.
- Email doğrulama ve şifre sıfırlama akışları Keycloak'ın kendi built-in flow'ları kullanılarak sağlanır.

## Multi-Factor Authentication (MFA)

MFA stratejisi role bazlı ve aşamalı olarak uygulanır (bkz. ROADMAP Phase 1 ve Phase 11):

- **Candidate:** Password + email verification (Keycloak native) + social login (Google/LinkedIn, identity broker). Düşük sürtünme önceliklidir; TOTP candidate için opsiyonel bırakılabilir.
- **Employer / Admin:** Password + TOTP zorunlu. Keycloak'ın conditional authentication flow'u (`Condition - User Role`) ile yalnızca bu rollere uygulanır.
- **İleri faz:** Admin (ve opsiyonel olarak Employer) için WebAuthn/Passkeys (native, Keycloak 26.4+).
- **Custom SPI gerektiren yöntemler** (email OTP, SMS OTP) Keycloak'ta native değildir; gerçek kullanıcı talebi/ihtiyacı ölçülmeden geliştirilmez. SMS OTP ayrıca SMS gateway maliyeti ve telefon numarası verisinin KVKK kapsamında işlenmesini gerektirir.

Genel ilke: native yöntemler (TOTP, WebAuthn, social login) önceliklidir; custom SPI yatırımı yalnızca ölçülebilir bir ihtiyaç ortaya çıkınca yapılır.

## Authorization

Roller:

```text
ROLE_CANDIDATE
ROLE_EMPLOYER
ROLE_ADMIN
```

Bu roller Keycloak'ta realm veya client role olarak tanımlanır; JWT'nin rol claim'i (`realm_access.roles` veya client role) Spring Security tarafından `ROLE_*` authority'lerine map edilir.

Sadece role check yeterli değildir.

Örneğin employer job update ederken:

```text
currentUser -> employer -> company membership -> job.companyId
```

yetki zinciri doğrulanmalıdır.

## IDOR Prevention

Şu endpoint:

```text
PUT /jobs/{jobId}
```

için yalnızca `ROLE_EMPLOYER` kontrolü yapılması yeterli değildir.

Employer'ın ilgili job'ın sahibi/yetkilisi olduğu application layer'da kontrol edilmelidir.

## Password Security

Password hashing ve saklama Keycloak tarafından yönetilir (bkz. ADR-008); uygulama password hash'i tutmaz veya işlemez.

- Password policy (uzunluk, karmaşıklık, hashing algoritması) Keycloak admin console üzerinden konfigüre edilir.
- Password reset akışı Keycloak'ın kendi token tabanlı flow'unu kullanır; token tek kullanımlık ve kısa ömürlüdür.

## Input Validation

Presentation katmanı:

- format validation
- required fields
- length limits

Domain/application katmanı:

- business invariants
- authorization ownership
- state transitions

## Rate Limiting

Özellikle:

```text
/auth/login
/auth/forgot-password
/jobs/search
/jobs/{id}/applications
```

endpoint'lerinde abuse kontrolü düşünülmelidir.

Redis distributed limit + local fallback uygulanabilir.

## Search Security

Client'tan raw Elasticsearch DSL alınmamalıdır.

Kullanıcı yalnızca kontrollü API query parametreleri sağlar; DSL backend tarafından üretilir.

## Sensitive Data

Elasticsearch'e ihtiyaç olmayan PII taşınmamalıdır.

Örneğin candidate email/telefon gibi alanların job search index'inde bulunmasına gerek yoktur.

## Audit

Audit edilebilecek işlemler:

- Job publish/close
- Application status changes
- Company verification
- Admin moderation actions
