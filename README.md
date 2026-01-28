<p align="center">
    <img src="assets/JSBSimEdit512x512.png" width="240">
</p>

# jsbsimedit2
Project for CSE3310 Fall 2025


## My Contributions
- Member of a semester-long software engineering team (CSE 3310)
- Contributed to testing, documentation, and design discussions
- Collaborated using GitHub branches, issues, and pull requests



## build instructions

```
% cd jsbsimedit
% mvn clean
% mvn compile
% mvn test
% mvn package
% mvn exec:java -Dexec.mainClass=uta.cse3310.App
```




## git instructions

if you have a github token, put it into an environment variable GITHUB_TOKEN . To set this for every shell, include in ~/.bashrc .

Then, you can use it in a single command line to do git commands without having to enter your github password.

```
git push https://BudDavis:${GITHUB_TOKEN}@github.com/utastudents/jsbsimedit2
```



