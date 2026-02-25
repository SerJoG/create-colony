---
name: "Feature request \U0001F680"
about: Suggest a functional idea for this project.
title: "[Feature]"
labels: feature
assignees: SerJoG

---

### 🎯 Objective
*Describe the mechanics and logic. What is the functional goal? (e.g., "A block that filters items" or "A structure that spawns specific mobs"). Focus on how it interacts with the game world, not how it looks.*

### 🛠️ Tasks
- [ ] **Registry:** Register new blocks, items, or entities.
- [ ] **Logic:** Implement the core mechanical behavior (e.g., Ticking, Interaction, AI).
- [ ] **Data/NBT:** Ensure any state changes are saved and loaded correctly.
- [ ] **Integration:** Connect the feature to existing systems (e.g., recipes, loot tables).
- [ ] **Testing:** Verify functionality in a dev environment.

### 🧪 Acceptance Criteria
- [ ] The mechanic triggers correctly under defined conditions.
- [ ] The feature handles edge cases (e.g., what happens if the block is broken while active?).
- [ ] [Add specific functional requirement here]

### 🔗 Resources & References
* **Target System:**
* **Source System:**
* **Version:**

---

## ✅ General Definition of Done (DoD)

### ⚙️ Functionality
- [ ] **Requirements Met:** The task fulfills the core objective.
- [ ] **Manual Testing:** Feature tested in-game and works as intended.
- [ ] **No Regressions:** No existing features were broken.
- [ ] **Side Safety:** The logic works on both Client and Dedicated Server (no crashes).

### 💻 Technical Quality
- [ ] **Clean Code:** Logic is readable and follows project conventions.
- [ ] **Persistence:** Data (NBT/Config) saves and loads correctly after a world restart.
- [ ] **Debug Clean-up:** All temporary "test code" and print statements has been removed.

### 📖 Documentation & UI
- [ ] **User Clarity:** Tooltips or names are descriptive.
- [ ] **Comments:** Complex logic is explained for future me.
