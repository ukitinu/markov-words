![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# Markov Words

Random word generator based on Markov chains, with "trainable" datasets.

This project was born out of my desire to have a reliable way to create words that sound kind of similar to others,
where *others* could be a language, a dialect, the names of all the characters of Tolkien's Legendarium and so on.

I also took the opportunity to try out some of the new Java 17 features, [Picocli](https://picocli.info/), a CLI library
for Java, and [GraalVM](https://www.graalvm.org/), a JVM perfectly suited for Java CLIs.

The result is this little programme, which allows the users (via CLI) to define their own "dictionaries", each one with
its own "alphabet" and its own [n-grams](https://en.wikipedia.org/wiki/N-gram), and to use them to generate words with
a mechanism based on Markov chains.


## Installation
TODO #22  
Extract the archive where you want to run the application and then start using it: run the bash/batch/elf file to learn
how the CLI works.  
You can also get one of the pre-built dictionaries and use it immediately instead of creating your own. (TODO #11)


## Quick guide
How to call the various commands is explained using the CLI (use `mkw help command`), what follows here is more about 
the "big picture" (quite pretentious to call it so, but I couldn't find a better name).

The user can **create** dictionaries, with a *name* and an *alphabet*. An alphabet is the set of symbols that will make up the
dictionary's words. Most characters are allowed, apart from [control codes](https://en.wikipedia.org/wiki/C0_and_C1_control_codes),
and the underscore which symbolises the end of a word and is a 'reserved' character.  
After creation, texts can be **read** to the dictionary. This will slowly build up the set of n-grams of the dictionary
(where *n* goes from 1 to 3, more than that would be useless and would kill the filesystem, probably) that will later
be used to **write** words.  
The importance of a dictionary's alphabet is that, whenever a text is read, every character that is not in the alphabet
is evaluated as a word end (_) and won't appear later during word generation.  
It is possible to **delete** a dictionary and **restore** it later if the deletion is not *permanent*.  
It is also possible to **update** name and/or description of a dictionary, to **list** the available dictionaries or to
get the **info** about one.

I expect that most of these commands will go unused, apart from **list** to check the available dictionaries, **create**
to create new ones, **read** to improve their accuracy, and **write** to generate words.


## More in depth
...todo...


## Pre-build dictionaries
Some sample dictionaries can be found in [samples](./samples). They can be used immediately after being copied 
to one's own data directory.  
More info can be found in the samples dir.

