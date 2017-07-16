package de.letsbuildacompiler.compiler;

import de.letsbuildacompiler.parser.DemoBaseVisitor;
import de.letsbuildacompiler.parser.DemoParser.PlusContext;
import de.letsbuildacompiler.parser.DemoParser.PrintlnContext;
import de.letsbuildacompiler.parser.DemoParser.ZahlContext;

public class MyVisitor extends DemoBaseVisitor<String> {

    @Override
    public String visitPrintln(PrintlnContext ctx) {
        return "  getstatic java/lang/System/out Ljava/io/PrintStream;\n" + //wie Syso 
                visit(ctx.argument) + "\n" +
                "  invokevirtual java/io/PrintStream/println(I)V\n"; //nimmt so viele Objekte vom Stack wie viele Parameter es hat
    }

    @Override
    public String visitPlus(PlusContext ctx) { //Label 1
        return visitChildren(ctx) + "\n" + //visitChildren: Subknoten
                "ldc " + ctx.rechts.getText() + "\n" +
                "iadd";
    }

    @Override
    public String visitZahl(ZahlContext ctx) { //Label 2
        return "ldc " + ctx.zahl.getText(); //ldc = load constance
    }

    //Test
    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }
        return aggregate + "\n" + nextResult;
    }
}
