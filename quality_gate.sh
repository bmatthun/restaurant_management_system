#!/bin/bash
# =============================================================================
# quality_gate.sh — Java Spring Boot Quality Gate
# Restaurant Management System
# Version: 1.0 (2026-03-04)
# =============================================================================

set -uo pipefail

# =============================================================================
# CONFIG
# =============================================================================
COVERAGE_THRESHOLD=85
CI_COVERAGE_THRESHOLD=95
MAX_FILE_LINES=300
CI_MAX_FILE_LINES=250

# Konfig fájl betöltése ha létezik
CONF_FILE=".quality_gate.conf"
if [ -f "$CONF_FILE" ]; then
  source "$CONF_FILE"
fi

# =============================================================================
# COLORS
# =============================================================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# =============================================================================
# FLAGS
# =============================================================================
CI_MODE=false
QUICK_MODE=false

for arg in "$@"; do
  case $arg in
    --ci) CI_MODE=true ;;
    --quick) QUICK_MODE=true ;;
  esac
done

if $CI_MODE; then
  COVERAGE_THRESHOLD=$CI_COVERAGE_THRESHOLD
  MAX_FILE_LINES=$CI_MAX_FILE_LINES
fi

# =============================================================================
# COUNTERS
# =============================================================================
PASS=0
FAIL=0
WARN=0

pass() { echo -e "${GREEN}  ✅ PASS${NC}: $1"; ((PASS++)); }
fail() { echo -e "${RED}  ❌ FAIL${NC}: $1"; ((FAIL++)); }
warn() { echo -e "${YELLOW}  ⚠️  WARN${NC}: $1"; ((WARN++)); }
info() { echo -e "${BLUE}  ℹ️  INFO${NC}: $1"; }

# =============================================================================
# HEADER
# =============================================================================
echo ""
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Java Quality Gate — Restaurant System${NC}"
if $CI_MODE; then
  echo -e "${BLUE}  Mode: CI (threshold: ${COVERAGE_THRESHOLD}%)${NC}"
else
  echo -e "${BLUE}  Mode: Local (threshold: ${COVERAGE_THRESHOLD}%)${NC}"
fi
echo -e "${BLUE}============================================${NC}"
echo ""

# =============================================================================
# CHECK 1: Maven elérhető?
# =============================================================================
echo -e "${BLUE}[1/6] Maven ellenőrzés...${NC}"
if [ -f "./mvnw" ]; then
  pass "mvnw megtalálva"
else
  fail "mvnw nem található — nem Spring Boot projekt gyökér?"
  exit 1
fi

# =============================================================================
# CHECK 2: BUILD + TESZTEK
# =============================================================================
echo ""
echo -e "${BLUE}[2/6] Build + tesztek futtatása...${NC}"

if ./mvnw clean test -q 2>/dev/null; then
  pass "BUILD SUCCESS — minden teszt zöld"
else
  fail "BUILD FAILURE — tesztek nem mentek át"
  echo ""
  echo -e "${RED}Futtasd: ./mvnw clean test${NC}"
  echo ""
  exit 1
fi

if $QUICK_MODE; then
  echo ""
  echo -e "${YELLOW}Quick mode — coverage és file check kihagyva${NC}"
  echo ""
  exit 0
fi

# =============================================================================
# CHECK 3: JACOCO COVERAGE
# =============================================================================
echo ""
echo -e "${BLUE}[3/6] JaCoCo coverage ellenőrzés...${NC}"

# JaCoCo report generálás
./mvnw jacoco:report -q 2>/dev/null || true

JACOCO_CSV="target/site/jacoco/jacoco.csv"

if [ ! -f "$JACOCO_CSV" ]; then
  # Próbáljuk verify-val
  ./mvnw verify -q 2>/dev/null || true
fi

if [ -f "$JACOCO_CSV" ]; then
  # Coverage számítás a CSV-ből
  COVERAGE=$(awk -F',' '
    NR>1 {
      missed += $4 + $6
      covered += $5 + $7
    }
    END {
      total = missed + covered
      if (total > 0) printf "%.0f", (covered/total)*100
      else print "0"
    }
  ' "$JACOCO_CSV")

  if [ "$COVERAGE" -ge "$COVERAGE_THRESHOLD" ]; then
    pass "JaCoCo coverage: ${COVERAGE}% (minimum: ${COVERAGE_THRESHOLD}%)"
  else
    fail "JaCoCo coverage: ${COVERAGE}% — minimum ${COVERAGE_THRESHOLD}% szükséges"
    info "Report: target/site/jacoco/index.html"
  fi
else
  warn "JaCoCo CSV report nem található — pom.xml-ben van jacoco plugin?"
  info "Elvárt: target/site/jacoco/jacoco.csv"
fi

# =============================================================================
# CHECK 4: FÁJL MÉRET
# =============================================================================
echo ""
echo -e "${BLUE}[4/6] Fájl méret ellenőrzés (max ${MAX_FILE_LINES} sor)...${NC}"

OVERSIZED=0
while IFS= read -r file; do
  lines=$(wc -l < "$file")
  if [ "$lines" -gt "$MAX_FILE_LINES" ]; then
    warn "$file — ${lines} sor (maximum: ${MAX_FILE_LINES})"
    ((OVERSIZED++))
  fi
done < <(find src/main/java -name "*.java" 2>/dev/null)

if [ "$OVERSIZED" -eq 0 ]; then
  pass "Minden Java fájl a méretlimit alatt van"
else
  fail "${OVERSIZED} fájl túllépi a ${MAX_FILE_LINES} soros limitet"
fi

# =============================================================================
# CHECK 5: TODO / FIXME keresés
# =============================================================================
echo ""
echo -e "${BLUE}[5/6] TODO/FIXME ellenőrzés...${NC}"

TODO_COUNT=$(grep -r "TODO\|FIXME" src/main/java --include="*.java" 2>/dev/null | wc -l)

if [ "$TODO_COUNT" -eq 0 ]; then
  pass "Nincs TODO/FIXME a forráskódban"
else
  warn "${TODO_COUNT} TODO/FIXME található — commit előtt érdemes tisztázni"
  grep -r "TODO\|FIXME" src/main/java --include="*.java" -l 2>/dev/null | while read f; do
    info "  $f"
  done
fi

# =============================================================================
# CHECK 6: GIT STATUS
# =============================================================================
echo ""
echo -e "${BLUE}[6/6] Git status...${NC}"

UNCOMMITTED=$(git status --porcelain 2>/dev/null | wc -l)
BRANCH=$(git branch --show-current 2>/dev/null)

info "Aktív branch: ${BRANCH}"

if [ "$UNCOMMITTED" -eq 0 ]; then
  pass "Nincs uncommitted változtatás"
else
  warn "${UNCOMMITTED} uncommitted fájl — ne felejtsd el commitolni!"
fi

# =============================================================================
# ÖSSZEFOGLALÓ
# =============================================================================
echo ""
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  ÖSSZEFOGLALÓ${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "  ${GREEN}✅ PASS: ${PASS}${NC}"
echo -e "  ${YELLOW}⚠️  WARN: ${WARN}${NC}"
echo -e "  ${RED}❌ FAIL: ${FAIL}${NC}"
echo ""

if [ "$FAIL" -eq 0 ]; then
  echo -e "${GREEN}  🎉 QUALITY GATE: PASS${NC}"
  echo ""
  exit 0
else
  echo -e "${RED}  🚫 QUALITY GATE: FAIL — ${FAIL} kritikus hiba!${NC}"
  echo ""
  exit 1
fi
