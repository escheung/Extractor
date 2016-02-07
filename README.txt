README.txt
------------
Requirements
------------
Linux, JAVA 1.7, Apache ANT.
  
-------
Unpack
-------
Unzip the package
- run "tar -zxvf Extractor.tar.gz" to unpack the file.
 
-------
Compile
-------
Using ANT:
- from a terminal, go into the "Extractor" directory you just unpacked.
- run "ant clean" to remove any previous compiled files.
- run "ant build" to compile new set of class files.

-------
Execute
-------
Using ANT:
- from a terminal, go into the "Extractor" directory.
- run "ant Start" to start execute the extraction application.
- The outputs are stored in two files:
    - "cmput690w16a1_cheung.tsv" for the primary objective.
    - "cmput690w16a1_cheung_bonus.tsv" for the bonus objective.
