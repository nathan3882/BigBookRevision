# BigBookRevision
- Had a non chronological Business book that was 300+ pages long
had to write notes on all of it. This program makes separate word documents in segmented intervals + supports future modification of these pages + their names
- For example, a 737 page book that you want to write notes on.
- Each new file generated could consist of X pages, but in this case I shall say 50 (can be different)
- 15 new files will be generated 0-50, 51-100, 101-150, 151-200..... 651-700, 701-737
- The user can then go aheady an  change the upper bound of the file "0-50" to "0-125", "51-100" will change to 126-127" and "101-150" will change to 128-129" etcetera. So the user doesn't have to go in and change potentially 15 different file names
# File Naming
- You can name the files what ever you want, as long as it contains "(p|P)[0-9] to (p|P)[0-9]" ie "Chapter 5 p123 to p126" would be valid
- Additionally, changing files is easy, just type into the shell "change p0 to p35" and the corresponding lower/upper bound will be changed.
- Took me around 1 hour to write and test
