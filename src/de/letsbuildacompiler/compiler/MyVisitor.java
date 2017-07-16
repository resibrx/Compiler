package de.letsbuildacompiler.compiler;

import de.letsbuildacompiler.parser.DemoBaseVisitor;
import de.letsbuildacompiler.parser.DemoParser.PlusContext;
import de.letsbuildacompiler.parser.DemoParser.ZahlContext;

public class MyVisitor extends DemoBaseVisitor<String> {
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
