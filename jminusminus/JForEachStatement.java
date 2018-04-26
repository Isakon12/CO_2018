// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;
import java.util.ArrayList;

import java.util.HashSet;

/**
 * The AST node for a for-each-statement.
 */

class JForEachStatement extends JStatement {

	/** Variable **/
	private JVariableDeclaration init;
	
    /** Final expression. */
    private JVariable identifier;
    
    /** The body. */
    private JStatement body;
    
    /** Artificial nodes */
    private JVariableDeclaration init_iterator;
    private JExpression condition_iterator;
    private JStatement incr_iterator;
    private JStatement init_var;
    
    /**
     * The new context (built in analyze()) represented by this block.
     */
    private LocalContext context;

    /**
     * Construct an AST node for a for-each-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the for-statement occurs in the source file.
     * @param condition
     *            test expression.
     * @param body
     *            the body.
     */
    
    public JForEachStatement(int line, JVariableDeclaration init, 
    		JVariable identifier, JStatement body) {
        super(line);
        this.init = init;
        this.identifier = identifier;
        this.body = body;
        
        if(init.getVar().type() == Type.INT)
        	this.init.getVar().setInitializer(new JLiteralInt(line,"0"));
        else if (init.getVar().type() == Type.DOUBLE) 
        	this.init.getVar().setInitializer(new JLiteralDouble(line,"0.0"));
        else
        	this.init.getVar().setInitializer(new JLiteralNull(line));
        

        
        //Artificial nodes
        String string_var = "0iterator";
        JVariable var = new JVariable(line,string_var);
        
        ArrayList<JVariableDeclarator> it = new ArrayList<JVariableDeclarator>();
        it.add(new JVariableDeclarator(line,string_var,Type.INT,
        		new JLiteralInt(line,"0")));
        init_iterator = new JVariableDeclaration(line,new ArrayList<String>(),it);
        
        JFieldSelection it_length = new JFieldSelection(line,identifier,"length");
        JExpression it_len_1 = new JSubtractOp(line,it_length,new JLiteralInt(line,"1"));
        condition_iterator = new JLessEqualOp(line,var,it_len_1);
        
        JExpression it_plus_1 = new JPlusAssignOp(line,var,new JLiteralInt(line,"1"));
        it_plus_1.isStatementExpression = true;
        incr_iterator = new JStatementExpression(line,it_plus_1);
        
        JExpression ident_value = new JArrayExpression(line,identifier,var);
        JExpression asign = new JAssignOp(line,new JVariable(line,
        		init.getVar().name()),ident_value);
        asign.isStatementExpression = true;
        init_var = new JStatementExpression(line,asign);
    }
    
    /**
     * 
     * @return throwed exceptions in the body of the loop
     */
    
    public HashSet<Type> throwedExceptions() {
        return body.throwedExceptions();
    }

    /**
     * Analysis involves analyzing the test, checking its type and analyzing the
     * body statement.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JForEachStatement analyze(Context context) {
    	
    	this.context = new LocalContext(context);
    	
    	init = (JVariableDeclaration) init.analyze(this.context);
    	identifier = (JVariable) identifier.analyze(this.context);
        init.getVar().type().mustMatchExpected(line(), identifier.type().componentType());
    	body = (JStatement) body.analyze(this.context);
        
        //Artificial nodes
        init_iterator = (JVariableDeclaration) init_iterator.analyze(this.context);
        condition_iterator = (JExpression) condition_iterator.analyze(this.context);
        incr_iterator = (JStatement) incr_iterator.analyze(this.context);
        init_var = (JStatement) init_var.analyze(this.context);
        return this;
    }

    /**
     * Generate code for the for each loop.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        // Need two labels
        String test = output.createLabel();
        String out = output.createLabel();
        
        init_iterator.codegen(output);
        
        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        condition_iterator.codegen(output, out, false);
        
        init_var.codegen(output);

        // Codegen body
        body.codegen(output);
        
        incr_iterator.codegen(output);

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForEachStatement line=\"%d\">\n", line());
        p.indentRight();
        if (context != null) {
            p.indentRight();
            context.writeToStdOut(p);
            p.indentLeft();
        }
        p.printf("<InitVariable>\n");
        p.indentRight();
        init.writeToStdOut(p);
        p.indentLeft();
        p.printf("</InitVariable>\n");
        p.printf("<Variable>\n");
        p.indentRight();
        identifier.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Variable>\n");
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForEachStatement>\n");
    }

}
