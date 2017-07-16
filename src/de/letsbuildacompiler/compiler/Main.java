package de.letsbuildacompiler.compiler;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import de.letsbuildacompiler.parser.DemoLexer;
import de.letsbuildacompiler.parser.DemoParser;

public class Main {

    public static void main(String[] args) throws Exception {
        ANTLRInputStream input = new ANTLRFileStream("code.demo"); //Lesen hier unsere Demo Datei ein
        DemoLexer lexer = new DemoLexer(input); //Lexer der den input bekommt | liest Zeichenstream und unterteilt stream in Token
        CommonTokenStream tokens = new CommonTokenStream(lexer); //liest Token ein
        DemoParser parser = new DemoParser(tokens); //parser wird mit Token gefüttert

        ParseTree tree = parser.addition(); //Regel mit der der Parser anfangen soll zu parsen
        System.out.println(createJasminFile(new MyVisitor().visit(tree))); //Visitor wird parsetree übergeben mit Jasmine File
    }

    private static String createJasminFile(String instructions) {
        return ".class public HelloWorld\n" +
                ".super java/lang/Object\n" +
                "\n" +
                ".method public static main([Ljava/lang/String;)V\n" +
                "  .limit stack 100\n" + //kann 100 aufnehmen
                "  .limit locals 100\n" +
                "  getstatic java/lang/System/out Ljava/io/PrintStream;\n" + //wie Syso 
                instructions + "\n" +
                "  invokevirtual java/io/PrintStream/println(I)V\n" + //nimmt so viele Objekte vom Stack wie viele Parameter es hat
                "  return\n" +
                ".end method";
    }
}
