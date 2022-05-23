charsheet save format:

name stamina skill escape luck intskill intluck intstam gold provisions pagenum luckpot stampot skillpot

Page format:

pgnum typeofinput dest1 dest2 encounternum  skillgain+ stamgain skillgain dest3 luckgain gold provisions

escape option is always dest1/win is dest2
lucky is always dest2 or second
type of input:
1 = input2
2 = test luck 
3 = input2 check for 3gold
4 = move straight to next page
5 = combat
6 = runcombat161 then input2
7 = runcombat161 then move on
8 = test luck, then choose whether to escape (pg17)
9 test luck if fail lose 1 stam // do i replace this with stamgain?
10 = multi monster encounter one after another (changes encounternum to reference encounterlist
11 = change combat type and lose 1 skill (pg24)
12 = escape?
13 = equipment encounter (change encounternum to inventchoice)
14 = escape or combat with possible escape
15 input3
16 enhanced combat (pg 39) (pg140) (173) (179) (storing encounter deets in inventupdates for now)
17 interrupted combat (page41)
18 diceroll encounter (pg47)
19 input5
20 test skill (page 53)
21 test luckANDskill(page 55)
22 run death (pg 64 string death)
23 test luck if unlucky lose 1 stam(pg 18)
24 customstat encounter (change encounternum to statencounter)
25 choose to eat provisions then input2
26 input4
27 choose testluck (pg82)
28 enhancedcombat (pg 86)
29 choose provisions then move on
30 enhanced combat (pg 116) multicombat
31 combat then choose provisions
32 choose to test luck or not (3 destinations)
33 test luck 3 times (pg 305)
34 test luck if unlucky die
35 win
36 input 2 then combat
37 ADD item - for following options, encounternum references Inventory Encounter
38 check for item
39 buy item
40 sacrifice item for new
41 remove item
42 remove item or throw a gold


Encounter list
Monster list repeated until no more monsters

Monster list
Number Name Skill Stamina Escape



special pages: 17 - remember for 144
24 - suffer third wound? changes combat
31 check for jewel
58 and 15 provisions?
pg72 does it mean anything?
pg105 check if remove invent
pg110 was i supposed to add gold page before?
finish page 140
143 spend provisions here
161 placehuolduoler

169 keys169 182

173 enhanced combat needs finishing

change setters to not go above initial

change monsterencounterlist to call monsters ie key mon1 mon2 mon3

read in as hashmap and then use it to turn to page object?
change pg 17 input8

pg215 invent check, 105 is too late
pg244 check for provisions
pg272 check for gold on previous and reduce here?

add method to insert spaces
fix vampire and enhanced combat encounters
fix pg 396
fix equipment