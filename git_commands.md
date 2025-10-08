```bash
1. git help # then follow after more commands
```
--------------------------------------------------------------------
## Symbols
```bash
1. ~ 
2. ^ 
3. HEAD # current commit
# HEAD~1	The first parent of HEAD : Go back one commit in a straight line.
# HEAD~3	The great-grandparent of HEAD : Go back three commits in a straight line.
# HEAD^1	The first parent of a merge commit : Navigate to the main branch side of a merge.
# HEAD^2	The second parent of a merge commit : Navigate to the feature branch side of a merge.
```
--------------------------------------------------------------------
## Configuration commands
```bash
1. git config # Show all git configurations commands
2. git config --list # List all your configurations
```
--------------------------------------------------------------------
## Setup commands
```bash
1. git clone [git-repo url]
2. git status
3. git add .
4. git commit -m "Your commit message"
5. git remote add origin https://github.com/username/repository.git
6. git remote -v # Verify remote origin
```
--------------------------------------------------------------------
## Log commands
```bash
1. git log # logs about commits
2. git log --oneline # concise logs about commits
```
--------------------------------------------------------------------
## Push and Pull commands
```bash
# pull = fetch(download) + rebase(merge) but we will do it separately
1. git pull origin main --rebase
2. git pull
3. git push -u origin [local-branch-name] # for first time push(origin is the default name for the remote), upsert upstream counterpart of this branch
4. git push # for subsequent pushes
5. git push origin [local-branch-name]:[remote-branch-name]
6. git push --force-with-lease origin [local-branch-name] # push safely, if remote branch has been updated, it will refuse to push, then we will need to pull, merge, resolve conflicts and then push again
```
--------------------------------------------------------------------
## Branching and Merging commands
```bash
      A---B---C   (main)
       \
        D---E---F   (your-feature-branch)
# git merge would create a new "merge commit" that ties the two histories together.
# This is safe and preserves history exactly as it happened, but it can make the project log look complex and cluttered with merge commits.
      A---B---C-------G   (main after merge)
       \             /
        D---E---F-------   (your-feature-branch)
# git rebase takes a different approach. It temporarily rewinds your commits (D, E, F), updates your branch with the latest commits from main, and then replays your commits one-by-one on top of the latest version of main.
# It looks as if you started your work after commit C was already finished. The commits D2, E2, and F2 contain the same file changes as D, E, F, but they are new commits with different SHA-1 hashes.
  A---B---C   (main)
           \
            D2--E2--F2   (your-feature-branch after rebase)
# more exploration required for rebase
1. git branch [branch-name] # create new branch
2. git branch -M [branch-name] # rename current branch to new branch name
3. git switch -c [branch-name] # create and switch to new branch
4. git merge [branch-name] # Merge branch into current branch
```
--------------------------------------------------------------------
## Undoing changes commands
```bash
1. git restore [file-name] # discard current changes and restore file of last commit
2. git restore . # discard all current changes and restore all files of last commit
3. git restore --staged [file-name] # unstage file but keep changes in working directory, if mistakenly added to staging area
# always run commit for current uncommited changes before running git revert
4, git revert [commit-hash] # create a new commit and applies the inverse of the changes from the target commit.
5. git cherry-pick [commit-hash] # create a new commit that applies the changes introduced by an existing commit from same/another branch, this is copy-paste of commit
```
--------------------------------------------------------------------
## Diff commands
```bash
1. git diff # explore this more
```