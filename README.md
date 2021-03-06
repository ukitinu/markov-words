![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# Markov Words
### Random word generator based on Markov chains, with "trainable" datasets.

<img src="https://user-images.githubusercontent.com/52630493/155839196-a6a9895a-2f44-449a-9fda-bc67fb11a7f4.PNG" width="694">

This project was born out of my desire to have a reliable way to create words that sound kind of similar to others,
where *others* could be a language, a dialect, the names of all the characters of Tolkien's Legendarium and so on.

I also took the opportunity to try out some of the new Java 17 features, [Picocli](https://picocli.info/), a CLI library
for Java, and [GraalVM](https://www.graalvm.org/), a JVM perfectly suited for Java CLIs.

The result is this little programme, which allows the users (via CLI) to define their own "dictionaries", each one with
its own "alphabet" and its own [n-grams](https://en.wikipedia.org/wiki/N-gram), and to use them to generate words with
a mechanism based on Markov chains.


## Installation & requirements
Download the [latest release](https://github.com/ukitinu/markov-words/releases/latest) for your platfom. There are two "types"
of version, *jar* and *nat-img*.  
- The *jar* versions may be used via the bash/batch script provided in the archive. They **require Java 17 to run**. Despite the
name, I think that the script provided in the *linux-jar* version should run on Mac-OS too, as it is basic shell, but I
have no MacOS environment to check.  
- The *nat-img* version, native image, contains an executable built with GraalVM, I tested it with **Java 17** and **Java 11**
and had no issues. As I had some troubles to create versions for all platforms,
([see here](https://github.com/ukitinu/markov-words/issues/25)), there is only a Linux (Ubuntu) native image.

The executable version is significantly faster: for simple tasks it is **approximately 100 times faster** (basically instantaneous),
while it is "only" **10 times as fast** during more computationally heavy tasks.

The first time the programme runs, it will create the [default properties file](./mkw.properties) and exit.

On Windows (I don't know if it also applies for Mac-OS) it is necessary to enable case sensitive file names for the data directory.
I followed [this guide](https://docs.microsoft.com/en-us/windows/wsl/case-sensitivity), but it didn't work (I got
a "request not supported" error or something like that). **This step is necessary if you want to create new dictionaries on Windows.**


## Quick guide
Use `mkw --help` to see the list of available commands, while how to call them is explained using the CLI
(use `mkw help command`), what follows here is more about the "big picture".

The user can `create` dictionaries, with a *name* and an *alphabet*. An alphabet is the set of symbols that will make up the
dictionary's words. Most characters are allowed, apart from [control codes](https://en.wikipedia.org/wiki/C0_and_C1_control_codes),
and the underscore which symbolises the end of a word and is a "reserved" character.  
After creation, texts can be `read` to the dictionary. This will slowly build up the set of n-grams of the dictionary
(where *n* goes from 1 to 3, more than that would be useless and would kill the filesystem, probably) that will later
be used to `write` words.  

The importance of a dictionary's alphabet is that, whenever a text is read, every character that is not in the alphabet
is evaluated as a word end (_) and won't appear later during word generation.  
It is possible to `delete` a dictionary and `restore` it later if the deletion is not *permanent*.  
It is also possible to `update` name and/or description of a dictionary, to `list` the available dictionaries or to
get the `info` about one.

I expect that most of these commands will go unused, apart from `list` to check the available dictionaries, `create`
to create new ones, `read` to improve their accuracy, and `write` to generate words.


## Pre-built dictionaries
Some sample dictionaries can be found in [samples](./samples). They can be used immediately after being extracted
to one's own data directory.

