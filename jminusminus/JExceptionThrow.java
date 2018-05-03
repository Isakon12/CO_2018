// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.ATHROW;

import java.util.ArrayList;
import java.util.HashSet;

class JExceptionThrow extends JStatement {

    /** Arguments. */
    private ArrayList<JExpression> args;

    /** Type name */
    private Type name;
    
    /** New exception */
    private JNewOp exception;

    /**
     * Construct an AST node for a throw exception given the line number,
     * parameters, and name.
     * 
     * @param line
     *            line in which the variable declaration occurs in the source
     *            file.
     * @param mods
     *            modifiers.
     * @param decls
     *            variable declarators.
     */

    public JExceptionThrow(int line, Type name,
    		ArrayList<JExpression> args) {
        super(line);
        this.name = name;
        this.args = args;
        
        exception = new JNewOp(line, name, args);
    }

    /**
     * We declare the variable(s). Initializations are rewritten as assignment
     * statements.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JStatement analyze(Context context) {
    	name = name.resolve(context);
    	exception = (JNewOp) exception.analyze(context);
        if(!(Throwable.class.isAssignableFrom(exception.type().classRep()))) {
        	JAST.compilationUnit.reportSemanticError(line(),
				    "Throwed parameter must be throwable");
        }
        return this;
    }

    /**
     * Return the throwed exception
     * 
     */
    public HashSet<Type> throwedExceptions() {
    	HashSet<Type> tmp = new HashSet<Type>();
    	tmp.add(name);
    	return tmp;
    }
    
    /**
     * Local variable initializations (rewritten as assignments in analyze())
     * are generated here.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
    	exception.codegen(output);
    	output.addNoArgInstruction(ATHROW);
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JExceptionThrow line=\"%d\" exception=\"%s\">\n",line(),
        		name.toString());
        p.indentRight();
        if (args != null) {
            p.println("<Arguments>");
            p.indentRight();
            for (JExpression expr : args) {
                expr.writeToStdOut(p);
            }
            p.indentLeft();
            p.println("</Arguments>");
        }
        p.indentLeft();
        p.println("</JExceptionThrow>");
    }

}
