# 🍽️ Restaurant Management System — Agent Kontextus

> Ez a fájl minden belépő agentnek szól.
> Olvasd el teljes egészében, mielőtt bármit módosítasz.

---

## Mi ez a projekt?

Egy bisztrót vezető Spring Boot webalkalmazás.
A bisztró tulajdonosa adminisztrálhatja az ételeket (Bowl), vendégeket (Customer),
rendeléseket (Order) és hozzávalókat (Ingredient) egy egyszerű webes felületen.

**Valódi use-case:** A tulajdonos hétfőn megnyitja a bevásárlólista kalkulátort,
látja mit kell piacon venni a heti rendelések alapján, és kész.

---

## Projekt adatok

| | |
|---|---|
| **Repo** | github.com/bmatthun/restaurant_management_system |
| **Branch** | `apa` (fejlesztés ide, majd PR → main) |
| **Projekt mappa** | `/home/tibor/restaurant_management_system` |
| **Framework** | Spring Boot + Maven |
| **Frontend** | Thymeleaf (szerver oldali HTML) |
| **Adatbázis** | H2 (file: `./data/summary`) |
| **Tesztek** | JUnit 5 + JaCoCo |

---

## A koordinátor személye

- **Nem Java-fejlesztő.** Koordinál, követelményeket fogalmaz meg, terminálban futtat parancsokat.
- Kódot NEM ír kézzel — agenten keresztül dolgozik.
- Minden válasz legyen rövid, konkrét, végrehajtható.
- Ne magyarázz Java-elméletet, ha nem kéri.

---

## Entitások és állapotuk

### Bowl (tál / étel)
- ✅ Model, Repository, Service, Controller kész
- ✅ Thymeleaf admin UI kész (CRUD)
- ✅ ManyToMany kapcsolat az Ingredient-ekkel (`bowl_ingredients` tábla)
- Mezők: `id`, `name`, `price`, `ingredients`

### Ingredient (hozzávaló)
- ✅ Model kész
- ✅ ManyToMany kapcsolat a Bowl-lal (mappedBy = "ingredients")
- Mezők: `id`, `name`, `unit` (pl. kg, g, dl)
- ⚠️ Nincs önálló admin UI (csak Bowl-on keresztül érhető el)

### Customer (vendég)
- ✅ Model, Repository, Service, Controller kész
- ✅ Thymeleaf admin UI kész (lista, form, szerkesztés, törlés)
- Mezők: `id`, `name`, `phone`, `email`, `address`, `note`, `orders`

### Order (rendelés)
- ✅ Model, Service, Controller kész
- ✅ Thymeleaf admin UI kész (lista, új rendelés, részletek, törlés)
- Mezők: `id`, `customer`, `date`, `time`, `orderItems`, `price`
- Kapcsolat: Order → OrderItem → Bowl → Ingredient

### OrderItem (rendelési tétel)
- ✅ Model kész
- Mezők: `id`, `order`, `bowl`, `quantity`, `unitPrice`, `lineTotal`
- `@PrePersist` automatikusan számolja a `lineTotal`-t

---

## Elkészült funkciók (időrendben)

1. **Bowl CRUD** — teljes admin felület
2. **Mock seed adatok** — `data.sql` (5 Bowl, 5 Ingredient, 2 Customer, 3 Order)
3. **Bowl ↔ Ingredient ManyToMany** — `bowl_ingredients` kapcsolótábla
4. **Heti bevásárlólista kalkulátor**
   - `GET /shopping-list?week=2026-03-02`
   - Logika: Order → OrderItem → Bowl → Ingredient → összesítés
   - `ShoppingListService.java`, `ShoppingListController.java`, `shopping-list.html`
5. **Customer admin UI** — CRUD, `CustomerController.java`, `customers/list.html`, `customers/form.html`
6. **Order admin UI** — lista, új rendelés, részletek, törlés
   - `OrderController.java`, `orders/list.html`, `orders/new.html`, `orders/detail.html`

---

## Teszt állapot

| Metrika | Érték |
|---|---|
| JUnit tesztek | 12+ |
| JaCoCo coverage | ~62% (cél: 85%) |
| Build státusz | ✅ BUILD SUCCESS |

**Fontos:** minden módosítás után kötelező:
```bash
./mvnw clean test
```
Csak zöld build esetén szabad commitolni.

---

## Git workflow

```bash
# Fejlesztési branch
git checkout apa

# Módosítás után
./mvnw clean test          # zöldnek kell lennie
git add .
git commit -m "feat: ..."  # conventional commits
git push origin apa

# PR → main csak akkor, ha minden teszt zöld
```

---

## Fájlstruktúra (fontosabb fájlok)

```
src/main/java/com/example/demo/
├── model/
│   ├── Bowl.java
│   ├── Ingredient.java
│   ├── Customer.java
│   ├── Order.java
│   └── OrderItem.java
├── repository/
│   ├── BowlRepository.java
│   ├── CustomerRepository.java
│   └── OrderRepository.java
├── service/
│   ├── BowlService.java
│   ├── IngredientService.java
│   ├── CustomerService.java
│   ├── OrderService.java
│   └── ShoppingListService.java
└── controller/
    ├── BowlController.java        → /api/bowls
    ├── ThymeLeafController.java   → / (főoldal)
    ├── CustomerController.java    → /customers
    ├── OrderController.java       → /orders
    └── ShoppingListController.java → /shopping-list

src/main/resources/
├── templates/
│   ├── customers/list.html, form.html
│   ├── orders/list.html, new.html, detail.html
│   └── shopping-list.html
└── data.sql   ← seed adatok, idempotens INSERT-ek
```

---

## Nyitott döntések (tulajdonossal egyeztetni kell)

| Döntés | Opciók |
|---|---|
| Spring Security? | Kis bisztró → valószínűleg nem kell |
| Éles adatbázis? | H2 most OK, később MySQL / PostgreSQL |
| REST API? | Thymeleaf only most, REST kell ha mobil app is lesz |
| Ingredient önálló UI? | Még nincs, ha kell: CRUD a hozzávalókhoz |

---

## Következő tervezett feladatok

1. **JaCoCo coverage 62% → 85%** — Service réteg tesztek hiányoznak
2. **Ingredient önálló admin UI** — ha a tulajdonos kéri
3. **Spring Security** — csak ha szükséges
4. **Éles DB migráció** — H2 → MySQL/PostgreSQL, Flyway migrációval

---

## Amit TILOS megtenni

- ❌ Ne változtass modell osztályokon (`@Entity` fájlok) indok nélkül
- ❌ Ne töröld a meglévő adatokat a `data.sql`-ből
- ❌ Ne commitolj piros build mellett
- ❌ Ne pusholj közvetlenül `main` branchre
- ❌ Ne írj üzleti logikát controllerbe — az a service feladata

---

*Utoljára frissítve: 2026-03-04 | Branch: `apa`*
