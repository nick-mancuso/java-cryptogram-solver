# java-cryptogram-solver
Simple substitution cipher solver that uses a dictionary of words (dictionary.txt, included in this repo), written in java. This program uses backtracking and two types of pruning, to maximize efficiency.

Cryptograms are a type of word puzzle where a string of text is encoded using a simple letter substitution cipher. Each letter is mapped to some other letter, and must be decoded through trial and error. However, because the string of text can offer further clues to narrow down possibilities: any letters surrounded by spaces must form a valid word, for instance.

Given a cryptogram via standard in, this program will output the total of all possible decodings, then the decoded strings in lexicographical order:

Given:
```
gop pgo
```

Output:
```
4
are ear
eat tea
how who
own now
```
