package de.letsbuildacompiler.compiler;

import java.util.HashMap;
import java.util.Map;

import de.letsbuildacompiler.parser.ResiBaseVisitor;
import de.letsbuildacompiler.parser.ResiParser.AssignmentContext;
import de.letsbuildacompiler.parser.ResiParser.DivContext;
import de.letsbuildacompiler.parser.ResiParser.MinusContext;
import de.letsbuildacompiler.parser.ResiParser.MultContext;
import de.letsbuildacompiler.parser.ResiParser.NumberContext;
import de.letsbuildacompiler.parser.ResiParser.PlusContext;
import de.letsbuildacompiler.parser.ResiParser.PrintlnContext;
import de.letsbuildacompiler.parser.ResiParser.VarDeclarationContext;
import de.letsbuildacompiler.parser.ResiParser.VariableContext;

public class MyVisitor extends ResiBaseVisitor<String> {

    private Map<String, Integer> variables = new HashMap<>();

    @Override
    public String visitPrintln(PrintlnContext ctx) {
        return "  getstatic java/lang/System/out Ljava/io/PrintStream;\n" + //wie Syso 
                visit(ctx.argument) + "\n" +
                "  invokevirtual java/io/PrintStream/println(I)V\n"; //nimmt so viele Objekte vom Stack wie viele Parameter es hat
    }

    @Override
    public String visitPlus(PlusContext ctx) { //Label 1
        return visitChildren(ctx) + "\n" + //visitChildren: Subknoten
                "iadd";
    }

    @Override
    public String visitMinus(MinusContext ctx) {
        return visitChildren(ctx) + "\n" +
                "isub";
    }

    @Override
    public String visitDiv(DivContext ctx) {
        return visitChildren(ctx) + "\n" +
                "idiv";
    }

    @Override
    public String visitMult(MultContext ctx) {
        return visitChildren(ctx) + "\n" +
                "imul";
    }

    @Override
    public String visitNumber(NumberContext ctx) {
        return "ldc " + ctx.number.getText();
    }

    @Override
    public String visitVarDeclaration(VarDeclarationContext ctx) {
        //wir können uns nur den Index ausgeben und nicht "gib mir die variable a, b, .."
        variables.put(ctx.varName.getText(), variables.size());
        return "";
    }

    @Override
    public String visitAssignment(AssignmentContext ctx) {
        return visit(ctx.expr) + "\n" +
                "istore" + variables.get(ctx.varName.getText()); //nimmt obersten integer vom stack und speichert in tabelle
    }

    @Override
    public String visitVariable(VariableContext ctx) {
        return "iload" + variables.get(ctx.varName.getText()); //nimmt variable an der defin. position und legt sie wieder oben auf den stack
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
