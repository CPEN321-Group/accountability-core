# Coding Practice

## Table of Content
1. Standard Setup
2. Github workflow
3. Commit Message Guideline
4. Naming Standard
5. Coding rules of thumb
6. Peer review a pull request
7. Writting README


## 1. Standard Setup

1. VSCode (hcm-support)

2. node.js

3. Github Desktop (https://desktop.github.com/)

4. Android Studio


## Additional setup

1. Terraform (https://learn.hashicorp.com/tutorials/terraform/install-cli?in=terraform/aws-get-started)


## 2. Github workflow

Step 1: Create or clone the repo; if you are already working on the same repo on the 

Step 2: Create a new branch with the name as the issue(task) you are working on

Step 3: Make some commits (code, test, etc.)

Step 4: Create a pull request when you're ready and wait for peer review

Note: you can still add more commits; the commits made after the PR will automatically be added to the PR

Step 6: Once peer review is complete and all comments/issues are addressed, merge into the master branch using a REBASE

Note:
- The master branch should be functional at all times. It should be protected (refer below in Additional Resources)
- After the PR, you will need to merge master back to your local machine to make another new branch


Additional Resources
- Git basics (Workflow vs Feature Branching): https://gist.github.com/blackfalcon/8428401
- Rebase: https://github.blog/2016-09-26-rebase-and-merge-pull-requests/
- Protected branch: https://docs.github.com/en/github/administering-a-repository/about-protected-branches

## 3. Commit message guidelines

Source: https://github.com/angular/angular/blob/master/CONTRIBUTING.md#commit

```
<type>: <short summary>
  │            │
  │            └─⫸ Summary in present tense. Not capitalized. No period at the end.
  │       
  |
  │
  └─⫸ Commit Type: build|ci|docs|feat|fix|perf|refactor|test|revert
```
Example: docs: create coding practice documents
### type
Must be one of the following:
- build: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)
- ci: Changes to our CI configuration files and scripts (example scopes: Circle, BrowserStack, SauceLabs)
- docs: Documentation only changes
- feat: A new feature
- fix: A bug fix
- perf: A code change that improves performance
- refactor: A code change that neither fixes a bug nor adds a feature
- test: Adding missing tests or correcting existing tests
- revert: reverting a previous commit
### short summary
Use the summary field to provide a succinct description of the change:
- use the imperative, present tense: "change" not "changed" nor "changes"
- don't capitalize the first letter
- no dot (.) at the end

## 4. Naming standard 

### Overall philosophy
Name conveys meaning. A good name will make it easier for you (and other people) to read your code

### General guideline
- Don't be jokey when naming things 
- Avoid meaningless names (example: 'obj', 'result', 'boo')
- Use single-letter variables only their meaning is CLEAR to anyone who will read your code (for example, as a well-known mathematical property (e=mc^2) )


## 5. Coding rules of thumbs
General philosophy: These 'rules' below are meant to help you, understand why the 'rules' are there and USE them accordingly, not follow them blindly. It's a game of deciding between trade-offs
### Correct
- Code should be shown correct with unit tests
- All fixes & new features should include regression tests to prevent reappearance of old bugs
### Clear
- Choose clarity over cleverness (don't use programming language tricks when it's not immediately clear)
- Don’t prematurely optimize - choose clarity over performance, unless there is a serious performance issue that needs to be addressed
### Concise
- DRY (Don't Repeat Yourself) and use the Rule of Three. This means that you should refactor your code if the same piece of code appears more than twice
- Use code comments judiciously, that is, improve your code so that minimal comments are needed
- Comments are for explaining why something is needed, not how it works
### Optimize for change
- Don’t try to solve every conceivable problem up-front, instead focus on making your code easy to change when needed.
- Build composable unit of codes
- Change can come in several forms, including hardware - your code will eventually be run on a colleague's machine or a server somewhere. Without overcomplicating things, write your code with this in mind. For example, use relative paths (e.g. ./file_in_the_project_directory.R rather than /Users/my_username/development/my_project/file_in_the_project_directory.R)
### Defensive coding
- Code defensively when calling other services. Every HTTP call or boto3 request to an AWS service could error or hang - handle such failures appropriately and fail fast. Aim to provide useful information to end users and people working on the code, when something fails.


## 6. Peer review a pull request
- Ask yourselves the following questions
1. Do I understand what the code is doing? Is the code breaking any rules of thumb (correct, clear, concise, etc.)? If yes, then is there a reason behind that? 
2. Are they using default packages/libraries sensibly? If not, is there an apparent why so?
3. Does it needed to be tested and/or is it sufficiently tested (demonstrated by test coverage, etc.)?
5. Does it work?
6. Are there edge cases you can think of that might break the code in the future?
- If any questions you can't answer yourself, you have the right to ask the code owner, even if it's someone much more experienced than you. 
## 7. Writting README
- README is usually the first thing a reader will read when encountering a new repository. A README is very important in providing the general overview of your code to any reader
- For our team, you can find the template [here](https://gist.github.com/ww9/44f08d44327a40d2ab309a349bebec57)
xw