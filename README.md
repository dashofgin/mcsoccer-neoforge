# MC Soccer - Mod do Minecraft / Minecraft Mod

## PL - Polski

Mod do piłki nożnej dla Minecraft 1.21.10 na platformie NeoForge. Dodaje piłkę z realistyczną fizyką, bramki z systemem punktacji, rękawice bramkarskie i koszulki drużynowe.

### Co dodaje mod?

**Piłka nożna**
- Piłka 3D z animacjami (model GeckoLib) - obraca się gdy leci/toczy
- Realistyczna fizyka: grawitacja, odbijanie, tarcie, opór powietrza
- Drybling - prowadzenie piłki przez chodzenie w nią
- Zaawansowane strzały: długie podanie, krótkie podanie, strzał z podkręceniem (curve), knuckleball
- Tackle: odbiór stojący i wślizg

**Bramka**
- Blok bramki z automatycznym wykrywaniem goli
- Scoreboard z punktami na sidebarze
- Wiadomość na chacie po golu
- Piłka po golu zostaje zamrożona na 3 sekundy, potem wyrzucona 6 bloków przed bramkę
- Komendy: `/mcsoccer reset`, `/mcsoccer show`, `/mcsoccer hide`

**Rękawice bramkarskie**
- Prawy przycisk myszy - odbija piłkę w zasięgu 3.75 bloków (stożek 95 stopni)
- Cooldown 1.5s po udanej obronie

**Koszulki drużynowe (16 sztuk)**
- Kluby: Real Madryt, FC Barcelona, Bayern, PSG, Manchester City, Liverpool, Juventus, AC Milan
- Reprezentacje: Polska, Brazylia, Niemcy, Argentyna, Francja, Anglia, Hiszpania, Włochy
- Noszone jako zbroja (chestplate)

### Sterowanie

| Klawisz | Akcja |
|---------|-------|
| Lewy klik | Mocny strzał |
| Prawy klik | Krótkie podanie |
| Shift + prawy klik | Podniesienie piłki |
| Chodzenie w piłkę | Drybling |
| Z | Długie podanie |
| X | Krótkie podanie |
| V | Strzał z podkręceniem |
| G | Knuckleball |
| C | Odbiór stojący |
| R | Wślizg |

Klawisze można zmienić w Options > Controls > Keybinds (kategoria "MC Soccer").

### Wymagania

- Minecraft **1.21.10**
- NeoForge **21.10.64** lub nowszy
- GeckoLib **5.3-alpha-3** lub nowszy

### Instalacja

1. Zainstaluj NeoForge 21.10.64 dla Minecraft 1.21.10
2. Pobierz GeckoLib z [CurseForge](https://www.curseforge.com/minecraft/mc-mods/geckolib) lub [Modrinth](https://modrinth.com/mod/geckolib) (wersja dla NeoForge 1.21.10)
3. Wrzuć `mcsoccer-1.4.jar` i GeckoLib JAR do folderu `mods/`
4. Uruchom grę

---

## EN - English

A football/soccer mod for Minecraft 1.21.10 on the NeoForge platform. Adds a ball with realistic physics, goals with a scoring system, goalkeeper gloves, and team jerseys.

### Features

**Soccer Ball**
- 3D ball with GeckoLib animations - spins while moving
- Realistic physics: gravity, bouncing, friction, air drag
- Dribbling - walk into the ball to push it forward
- Advanced shots: long pass, short pass, curve shot, knuckleball
- Tackles: standing tackle and slide tackle

**Goal**
- Goal block with automatic goal detection
- Scoreboard with points displayed on sidebar
- Chat message on goal
- Ball freezes for 3 seconds after goal, then ejected 6 blocks in front of goal
- Commands: `/mcsoccer reset`, `/mcsoccer show`, `/mcsoccer hide`

**Goalkeeper Gloves**
- Right click - deflects ball within 3.75 blocks range (95 degree cone)
- 1.5s cooldown after successful save

**Team Jerseys (16 total)**
- Clubs: Real Madrid, FC Barcelona, Bayern Munich, PSG, Manchester City, Liverpool, Juventus, AC Milan
- National teams: Poland, Brazil, Germany, Argentina, France, England, Spain, Italy
- Worn as armor (chestplate slot)

### Controls

| Key | Action |
|-----|--------|
| Left click | Power shot |
| Right click | Short pass |
| Shift + right click | Pick up ball |
| Walk into ball | Dribble |
| Z | Long pass |
| X | Short pass |
| V | Curve shot |
| G | Knuckleball |
| C | Standing tackle |
| R | Slide tackle |

All keybinds are configurable in Options > Controls > Keybinds (category "MC Soccer").

### Requirements

- Minecraft **1.21.10**
- NeoForge **21.10.64** or newer
- GeckoLib **5.3-alpha-3** or newer

### Installation

1. Install NeoForge 21.10.64 for Minecraft 1.21.10
2. Download GeckoLib from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/geckolib) or [Modrinth](https://modrinth.com/mod/geckolib) (NeoForge 1.21.10 version)
3. Place `mcsoccer-1.4.jar` and GeckoLib JAR in the `mods/` folder
4. Launch the game

---

## Dependencies / Zależności

| Library | Version | Purpose |
|---------|---------|---------|
| [NeoForge](https://neoforged.net/) | 21.10.64+ | Mod loader / Platforma modów |
| [GeckoLib](https://github.com/bernie-g/geckolib) | 5.3-alpha-3+ | 3D model animations / Animacje modeli 3D |

## License / Licencja

All Rights Reserved
