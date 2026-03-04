# AI CODING RULES — Java Spring Boot Edition
**Version: 1.0 (2026-03-04)**
**Toolchain: Maven + JUnit5 + JaCoCo + Checkstyle**

---

## 🔴 HIERARCHIA — LEGFONTOSABB!

| Szerep | Felelősség |
|--------|------------|
| EMBER | Megrendelő, döntéshozó, terminálparancsok |
| AGENT | Végrehajtó, kódoló, debuggoló |

**AGENT KÖTELESSÉGEI:**
- Az EMBER NEM DEBUGOL — az agent dolga
- Az EMBER NEM BÖNGÉSZIK — kódelemzés az agent feladata
- Az EMBER NEM CSELÉD — ne kérj tőle futtatást amit te is tudsz

---

## 🚨 CRITICAL RULES

### ❌ TILOS:
- Guessing — kérdezz, max 2 kérdés
- Incomplete code — befejezni vagy INCOMPLETE.md
- Placeholder comments — `// TODO`, `// FIXME`
- Code snippets — mindig teljes, futtatható fájl
- Truncation — SOHA `...` vagy "rest unchanged"
- God classes — >300 sor tilos
- Tesztek módosítása — tesztek definiálják a spec-et!
- Config manipuláció — `quality_gate.sh` és `pom.xml` TILOS módosítani!

### ✅ KÖTELEZŐ:
- Complete files — első sortól az utolsóig
- Javadoc — minden public metódushoz
- Tesztek — MANDATORY, nincs kivétel
- Layered Architecture — controller/service/repository/entity
- Git status check — minden új osztály után

---

## 📊 QUALITY GATE

### Küszöbök:
| Metrika | Local | CI |
|---------|-------|-----|
| JaCoCo Coverage | ≥85% | ≥95% |
| Max LOC/file | 300 | 250 |
| Checkstyle errors | 0 | 0 |
| PMD violations | 0 | 0 |

### Futtatás:
```bash
./quality_gate.sh          # Local
./quality_gate.sh --ci     # CI mód (szigorú)
```

### PASS után jelenthetsz KÉSZ-t!

---

## 🛠️ TOOLCHAIN

### Maven tesztek + coverage:
```bash
./mvnw clean test
./mvnw verify  # JaCoCo report generál
```

### JaCoCo report megtekintése:
```bash
target/site/jacoco/index.html
```

### Quality gate:
```bash
./quality_gate.sh
```

---

## 🏗️ PROJEKT STRUKTÚRA

```
src/
├── main/java/com/example/demo/
│   ├── entity/          # JPA entitások (Bowl, Order, Customer, Ingredient)
│   ├── repository/      # Spring Data JPA interfészek
│   ├── service/         # Üzleti logika
│   ├── controller/      # Thymeleaf + REST controllerek
│   └── dto/             # Data Transfer Objects
├── main/resources/
│   ├── templates/       # Thymeleaf HTML sablonok
│   └── application.yaml
└── test/java/           # JUnit5 tesztek
```

### Szabályok:
- Controller SOHA nem hív Repository-t közvetlenül
- Service tartalmazza az üzleti logikát
- Entity POJO marad — nincs logika benne
- Egy fájl = egy felelősség
- Max 300 sor / fájl

---

## 🧪 TESTING RULES

- **TESZTEK KÖTELEZŐEK** — nincs kivétel!
- Coverage target: ≥85% (local)
- One test = one behavior
- Arrange-Act-Assert pattern
- `@WebMvcTest` — controller tesztekhez
- `@DataJpaTest` — repository tesztekhez
- `@SpringBootTest` — integrációs tesztekhez
- **Tesztek NEM módosíthatók** — tesztek definiálják a spec-et!

---

## 🔒 SECURITY

- ✅ Spring parameterezett query-k (`@Query` + paraméterek)
- ❌ SOHA natív SQL string concatenation
- ✅ Environment variables érzékeny adatokhoz
- ❌ SOHA hardcode jelszó, API kulcs

---

## 📋 WORKFLOW

### Session Start:
```bash
cd /home/tibor/restaurant_management_system
git status
git branch
cat AGENTS.md
```

### Minden réteg elkészülte után:
```bash
./mvnw clean test
./quality_gate.sh
# CSAK PASS után mész tovább!
```

### "KÉSZ" előtt:
```bash
git status
git add .
git commit -m "feat: ..."
git push origin apa
# Ha nincs commit → NEM KÉSZ!
```

---

## 🎯 TL;DR

1. 🔥 CHECK git status — minden új osztály után
2. 📝 Write complete files — nincs truncation
3. 🎯 Layered Architecture — controller → service → repository
4. ✅ Pass quality gate — ≥85% coverage, 0 Checkstyle hiba
5. 🧪 Write tests — MANDATORY
6. 📐 Respect limits — ≤300 lines/file
7. 🚫 NO config manipulation — csalás!
8. 🔍 DEBUG IN CODE — soha ne delegálj embernek

**Remember: ./mvnw clean test → BUILD SUCCESS minden commit előtt.** 🚀
