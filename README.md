# Word Count with Akka
A simple example of [Akka](http://akka.io/) usage for teaching purposes.

## Project Description 
A [WordCount](https://wiki.apache.org/hadoop/WordCount) example: it reads text files and counts how often words occur.

In particular: 

The `Main` object: 
- creates a new `ActorSystem`;
- creates a new `WordCounterActor`;
- gets a file path (or the path of the default [hamlet.txt](https://github.com/Vincibean/AkkaWordCount/blob/master/src/main/resources/hamlet.txt) 
file from [Project Gutenberg](https://www.gutenberg.org/)); 
- asks (`?` operator) the `WordCounterActor` for a `Future` indicating how often each word occurs in the
given file.

The `WordCounterActor`: 
- receives the `StartProcessingFile` message containing the file path from the `Main` object;
- saves a reference to the process invoker (the `Main` object);
- gets a `Stream` of `String`s representing each line of the text file;
- saves how many lines are contained into the input file;
- for each of these lines, it creates a new `StringCounterActor` and sends it (`!` operator) a `ProcessLine` message 
with the content of the given line.
 
The `StringCounterActor`: 
- receives the `ProcessLine` message containing the text line to be processed;
- turns the whole line to lower case;
- splits the line using the `\W+` regular expression;
- keeps the non-empty strings;
- groups by each word; the output of this phase will be a `Map` where the keys are all the words contained in the line 
and the values are a sequence of instances of the given word (e.g. the pair `(hamlet, Array(hamlet, hamlet, hamlet))`);
- uses the `mapValues` method to get the length of each sequence of instances of words;
- sends back to the sender (`WordCounterActor` in this example) a `LineProcessed` message containing the result of the 
computation.

The `WordCounterActor` then:
- receives the `LineProcessed` message containing the result of `StringCounterActor`'s computation;
- adds this result to its internal list of results;
- increment by one the number of lines processed;
- if the number of lines processed equals (or is greater than) the number of lines contained in the input file, it uses 
a `reduce` function to turn the internal list of results into a single, exhaustive result;
- sends a `FileProcessed` message, containing the final result, to the original sender (the `Main` object, in this 
example)

The `Main` object then: 
- receives a `Future` containing the `FileProcessed` message with the final word count;
- prints the result to the console;
- `terminate`s the `ActorSystem`. 

## Thanks
This project is a reimplementation of [a similar project](https://www.toptal.com/scala/concurrency-and-fault-tolerance-made-easy-an-intro-to-akka) 
by [Diego Castorina](https://www.toptal.com/resume/diego-castorina) 

## License
Unless stated elsewhere, all files herein are licensed under the GPLv3 license. 
For more information, please see the [LICENSE](https://github.com/Vincibean/AkkaWordCount/blob/master/LICENSE) file.