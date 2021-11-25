This is a silly experiment, just for the joy of it, based on [Hexing the Technical Interview](https://aphyr.com/posts/341-hexing-the-technical-interview).

In that story, an interviewee implements an algorithm, not in Java or in Closure, but by a raw byte array that 
represents a JVM class file, and is dynamically loaded at runtime. (But with more poetry, and more eldritch.)

This was such a crazy and ludicrous thing to do that I had to try it myself and see if I could do it.

Indeed, I was able to hand-craft the class file structure, working from the docs, and hand-implement the machine code. 
Whee! A sane reference implementation of the method is also provided, along with unit tests that validate the identical 
behavior of both.

Continuous integration through Github Actions: [![Tests](https://github.com/samlindsaylevine/bytecode-maddness/workflows/Tests/badge.svg)](https://github.com/samlindsaylevine/bytecode-madness/actions)