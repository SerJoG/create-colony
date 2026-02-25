---
name: "Bug report \U0001F41B"
about: Report a functional error or unintended behavior.
title: "[BUG]"
labels: bug, high priority
assignees: SerJoG

---

### 🎯 Objective
*What is the bug? Describe the difference between the expected behavior and what is actually happening in-game.*

### 👣 Steps to Reproduce
1. [Step 1: e.g., Open the GUI]
2. [Step 2: e.g., Shift-click an item]
3. [Step 3: e.g., Observe the crash/error]

### 🛠️ Tasks
- [ ] **Investigation:** Locate the class or method causing the issue.
- [ ] **Fix:** [Describe the technical fix here].
- [ ] **Regression Test:** Verify that the fix doesn't break related systems.
- [ ] **Verification:** Confirm the bug is gone in both Singleplayer and Multiplayer.

### ✅ Acceptance Criteria
- [ ] The reported bug no longer occurs under the reproduction steps.
- [ ] The system functions as originally intended.
- [ ] No new errors appear in the console (`latest.log`) during the process.

### 🔗 Resources & References
- **Impacted System:** (e.g., Rendering, Tile Entity, Networking)
- **Environment:** (e.g., Forge 1.20.1, Dedicated Server)
- **Logs/Screenshots:** [Link to Pastebin/Gist for logs or upload images]

---

## ✅ General Definition of Done (DoD)

### ⚙️ Technical Integrity
- [ ] **No Regressions:** The fix did not break any existing features.
- [ ] **Side Safety:** Verified that the fix works on both **Client** and **Dedicated Server**.
- [ ] **Compatibility:** Tested with essential mods (if the bug was a conflict).

### 💻 Code Quality
- [ ] **Clean Fix:** Used the proper API or event rather than a "dirty" workaround.
- [ ] **Optimization:** The fix doesn't introduce memory leaks or CPU spikes.
- [ ] **Cleanup:** Removed any `System.out` or debug breakpoints used during the fix.

### 📖 Documentation & Maintenance
- [ ] **Comments:** Added a comment to the code explaining *why* this fix was necessary (to prevent future refactors from re-introducing it).
- [ ] **Version Bump:** (Optional) Updated versioning if this requires a hotfix release.
