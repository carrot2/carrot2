---
name: apply-dependency-pr
description: Apply and merge a Dependabot dependency-update PR in this repo — checkout the PR, regenerate versions.lock, run the full build, then commit the lock, push, and squash-merge. Use when the user wants to review/apply/merge a dependency (e.g. "dependencies" labeled) PR.
---

# Apply a dependency-update PR

Workflow for landing a Dependabot dependency bump in carrot2. Dependabot updates
`gradle/libs.versions.toml` and the Gradle wrapper but does **not** regenerate the
root `versions.lock`; because this repo uses a lockfile, the `check` task fails
against a stale lock. So the lock must be regenerated locally, verified, committed
back to the PR branch, and only then merged.

## Steps

1. **Pick the PR.** If the user didn't name one, list candidates and ask which to apply:
   ```
   gh pr list --label dependencies --state open --limit 20
   ```

2. **Checkout the PR branch:**
   ```
   gh pr checkout <num>
   ```

3. **Regenerate the lock and run the full check** (compiles against the new deps and
   runs the whole test suite — this is the authoritative gate). Takes a few minutes:
   ```
   ./gradlew writeLocks check
   ```

4. **Evaluate the result:**
   - **BUILD FAILED or test regressions** → stop. Report the failing task/output to
     the user. Do not commit or merge; investigate first.
   - **BUILD SUCCESSFUL, no regressions** → continue.
   - Watch behavioral-change risk areas: minor Lucene bumps (analysis/tokenization
     tests) and morfologik bumps (Polish stemming/spell tests).

5. **Inspect and commit the regenerated lock.** `writeLocks` normally only touches
   `versions.lock`; confirm the diff is pure version bumps for the expected deps:
   ```
   git diff versions.lock
   git add versions.lock   # plus any other files writeLocks changed
   git commit -m "Regenerate versions.lock for java-deps bump (#<num>)"
   git push
   ```
   Skip this step if `writeLocks` produced no changes.

6. **Squash-merge and delete the branch** (matches the repo's dependency-merge history):
   ```
   gh pr merge <num> --squash --delete-branch
   ```

7. **Verify:**
   ```
   gh pr view <num> --json state,mergedAt   # state == MERGED
   git log --oneline -3                      # squashed commit on master
   ```

## Notes

- Do **not** add a `Co-Authored-By` / Claude attribution trailer to the commit.
- Local `check` passing is the authoritative signal; the merge does not wait on
  remote CI unless the user asks otherwise.
- Default merge method is squash + delete-branch. Confirm with the user if they want
  a merge-commit or rebase instead.
