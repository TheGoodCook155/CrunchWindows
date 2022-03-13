# CrunchCLI
Crunch for Windows CLI


This app is an implementation of the famous password generator tool called "Crunch". The main reason I invested time in building the main functionality of Crunch is the "spaghetti code"
coding style found in the source code of the original Crunch app. This is very hard to refactor and extend.
Note that I built the permutation algorithm from scratch (basically, this is my own version, didn't reverse-engineered the original app) and at this time it may not be as optimal as
the original algorithm but this is an early alpha version so feel free to test it.

This version heavily relies on data structures and if go wild make sure you have enough ram available. Memory leaks are also possible!!!

I may test it properly in the next period and may push some updates.


=========================================================================================================

Fixed memory leaks
Improved regex

