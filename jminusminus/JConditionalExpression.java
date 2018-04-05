// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a conditional expression. A conditional expression has a condition and
 * two expressions: a lexp and a rexp.
 */

abstract class JConditionalExpression extends JExpression {

	/** Conditions */
	protected JExpression condition;
	
    /** The lexp operand. */
    protected JExpression lexp;

    /** The rhs operand. */
    protected JExpression rexp;

    /**
     * Construct an AST node for a binary expression given its line number, the
     * binary operator, and lhs and rhs operands.
     * 
     * @param line
     *            line in which the binary expression occurs in the source file.
     * @param condition
     *            the condition expression.
     * @param lexp
     *            the lexp operand.
     * @param rexp
     *            the rexp operand.
     */

    protected JConditionalExpression(int line, JExpression condition, JExpression lexp,
            JExpression rexp) {
        super(line);
        this.condition = condition;
        this.rexp = rexp;
        this.lexp = lexp;
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JConditionalExpression line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<Condition>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Condition>\n");
        p.printf("<Lexp>\n");
        p.indentRight();
        lexp.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Lexp>\n");
        p.printf("<Rexp>\n");
        p.indentRight();
        rexp.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Rexp>\n");
        p.indentLeft();
        p.printf("</JConditionalExpression>\n");
    }

}

/**
 * The AST node for a conditional expression.
 */

class JConditionalExprOp extends JConditionalExpression {

    /**
     * Construct an AST node for a binary expression given its line number, the
     * binary operator, and lhs and rhs operands.
     * 
     * @param line
     *            line in which the binary expression occurs in the source file.
     * @param condition
     *            the condition expression.
     * @param lexp
     *            the lexp operand.
     * @param rexp
     *            the rexp operand.
     */

    public JConditionalExprOp(int line, JExpression condition, JExpression lexp, 
    		JExpression rexp) {
        super(line, condition, lexp, rexp);
    }

    /**
     * Analysis involves first analyzing the operands. If this is a string
     * concatenation, we rewrite the subtree to make that explicit (and analyze
     * that). Otherwise we check the types of the addition operands and compute
     * the result type.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        condition = (JExpression) condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        lexp = (JExpression) lexp.analyze(context);
        rexp = (JExpression) rexp.analyze(context);
        lexp.type().mustMatchExpected(line(), rexp.type());
        type = lexp.type();
        return this;
    }

    /**
     * Any string concatenation has been rewritten as a JStringConcatenationOp
     * (in analyze()), so code generation here involves simply generating code
     * for loading the operands onto the stack and then generating the
     * appropriate add instruction.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
    	
    }

}