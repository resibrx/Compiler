grammar Demo;

program: (println ';')+;

expression: left=expression '*' right=expression #Mult  
		| left=expression '+' right=expression #Plus 
		| left=expression '-' right=expression #Minus 
		| number=NUMBER #number;

println: 'println(' argument=expression ')' ;

NUMBER: [0-9]+;
WHITESPACE: [ \t\n\r]+ -> skip;