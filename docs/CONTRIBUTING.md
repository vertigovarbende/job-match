# Contributing Guide

Bu doküman, JobMatch projesine katkı sağlarken izlenecek branch ve commit naming convention'larını tanımlar.

## Branch Naming Convention

### Ana Branch'ler

```text
main     → production-ready, korumalı (protected). Doğrudan push edilmez, yalnızca PR ile merge edilir.
develop  → entegrasyon branch'i. Tüm çalışma branch'leri buradan açılır, buraya merge edilir.
```

### Çalışma Branch'leri

tüm işler "feature" değildir (bug fix, hotfix, docs, chore gibi farklı türler ayrı isimlendirilir), ve faz numarası her zaman iki haneli, sıfır dolgulu yazılır.

```text
<type>/phase-<NN>/<kebab-case-description>
```

- **`<type>`**: aşağıdaki tablodan biri.
- **`phase-<NN>`**: ROADMAP.md'deki ilgili faz numarası, **iki haneli, sıfır dolgulu** (`phase-00`, `phase-01`, … `phase-11`). Zero-padding zorunludur — aksi halde branch listesi alfabetik sıralandığında `phase-1`, `phase-10`, `phase-11`, `phase-2` gibi yanlış bir sıra oluşur.
- **`<kebab-case-description>`**: kısa, açıklayıcı, küçük harf, kelimeler arası tire (`-`). Opsiyonel ama önerilir — aynı fazda birden fazla branch açıldığında ayırt edici olur.
- ROADMAP'teki belirli bir faza denk gelmeyen iş (hotfix, CI config, bağımlılık güncellemesi vb.) için `phase-<NN>` segmenti atlanır: `<type>/<kebab-case-description>`.

| Type | Ne zaman kullanılır |
|---|---|
| `feature` | Yeni bir işlevsellik, ROADMAP'teki bir madde |
| `fix` | Bug fix (henüz production'a çıkmamış) |
| `hotfix` | Production'da acil düzeltme gerektiren bug (`main`'den açılır) |
| `chore` | Bağımlılık güncelleme, build/tooling değişikliği, refactor olmayan bakım işi |
| `docs` | Yalnızca dokümantasyon değişikliği |
| `refactor` | Davranışı değiştirmeyen kod iyileştirmesi |
| `test` | Yalnızca test ekleme/düzenleme |
| `ci` | CI/CD pipeline değişikliği |

### Örnekler

```text
feature/phase-01/keycloak-resource-server-integration
fix/phase-03/job-publish-invariant-check
docs/phase-00/contributing-guide
chore/phase-00/maven-failsafe-plugin
refactor/phase-07/matching-score-calculation
hotfix/production-search-500-error
ci/github-actions-verify-pipeline
```

### hotfix İstisnası

`hotfix/*` branch'leri `develop`'tan değil, doğrudan `main`'den açılır (production'da acil bir sorunu düzeltmek için). Merge sonrası hem `main`'e hem `develop`'a yansıtılır.

## Commit Message Convention

**Conventional Commits** standardı kullanılır (bkz. https://www.conventionalcommits.org).

### Format

```text
<type>(<scope>): <subject>

<body — opsiyonel>

<footer — opsiyonel>
```

### Type

Branch type'larıyla büyük ölçüde örtüşür; tek fark `feature` yerine kısa hâli `feat` kullanılmasıdır (Conventional Commits spesifikasyonunun sabit anahtar kelimesi budur, tooling uyumluluğu için değiştirilmez):

```text
feat     → yeni özellik
fix      → bug fix
docs     → yalnızca dokümantasyon
style    → formatlama, noktalı virgül vb. (davranış değişmez)
refactor → davranışı değiştirmeyen kod iyileştirmesi
perf     → performans iyileştirmesi
test     → test ekleme/düzenleme
chore    → bağımlılık, build/tooling, konfigürasyon
ci       → CI/CD pipeline
build    → build sistemi/dış bağımlılıklar (Maven, Docker)
revert   → önceki bir commit'i geri alma
```

### Scope

Proje modülleriyle eşleşir (bkz. README.md — Ana Modüller):

```text
identity, candidate, employer, job, application, search, matching, notification, shared, docs, build
```

### Kurallar

- Subject emir kipinde yazılır ("add" değil "adds"; Türkçe yazılacaksa "ekle", "düzelt" gibi emir kipi).
- Subject sonunda nokta olmaz.
- Subject 72 karakteri geçmemeli.
- Breaking change varsa footer'da `BREAKING CHANGE: <açıklama>` belirtilir.
- Body, "ne" değil "neden" yapıldığını açıklamalı — kod zaten "ne"yi gösteriyor.

### Örnekler

İyi:

```text
feat(job): add publish use case with DRAFT-only invariant

fix(application): prevent duplicate application to same job

docs(contributing): add branch and commit naming convention

chore(build): add maven-failsafe-plugin for integration tests
```

Kaçınılması gerekenler:

```text
fix: bug fixed
update stuff
WIP
asdasd
```

## Pull Request Akışı

```text
<type>/phase-<NN>/<description>  →  PR açılır  →  develop  →  (faz tamamlanınca) main
```

- PR başlığı commit convention'ına uygun yazılır (ör. `feat(job): add publish use case`).
- PR açıklamasında ilgili ROADMAP.md fazı/maddesi referans verilir.
- `main`'e merge yalnızca PR üzerinden yapılır, doğrudan push yapılmaz.
