#!/bin/bash
source /usr/local/etc/ora11.csh
rm *.class system.out.* 
rm Advertisers_edited.dat create.sql *.log insert.sql temp1.sql drop.sql
javac -cp ojdbc5.jar adwords.java
java -cp ojdbc5.jar:. adwords