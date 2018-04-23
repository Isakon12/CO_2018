// Copyright 2011 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * The AST node for a method declaration.
 */

class JBlockDeclaration extends JAST implements JMember {

    /** Method modifiers. */
    protected ArrayList<String> mods;

    /** Method body. */
    protected JBlock body;

    /** Is method static. */
    protected boolean isStatic;
    
    /** Is method private. */
    protected boolean isPrivate;
    
    /** Is method public. */
    protected boolean isPublic;
    
    /** Is method protected. */
    protected boolean isProtected;
    
    /** Is method abstrac. */
    protected boolean isAbstract;


    /**
     * Construct an AST node for a block declaration given the
     * line number, and the method body.
     * 
     * @param line
     *                line in which the block declaration occurs
     *                in the source file.
     * @param mods
     *                modifiers.
     * @param body
     *                method body.
     */

    public JBlockDeclaration(int line, ArrayList<String> mods, JBlock body)

    {
        super(line);
        this.mods = mods;
        this.body = body;
        this.isStatic = mods.contains("static");
        this.isPublic = mods.contains("public");
        this.isPrivate = mods.contains("private");
        this.isProtected = mods.contains("protected");
        this.isAbstract = mods.contains("abstract");
    }
    
    /**
     * Return the list of modifiers.
     * 
     * @return list of modifiers.
     */

    public ArrayList<String> mods() {
        return mods;
    }

    /**
     * Declare this block in the parent (class) context.
     * 
     * @param context
     *                the parent (class) context.
     * @param partial
     *                the code emitter (basically an abstraction
     *                for producing the partial class).
     */

    public void preAnalyze(Context context, CLEmitter partial) {
    	if (isAbstract || isPublic || isPrivate || isProtected) {
    		JAST.compilationUnit.reportSemanticError(line(),
                    "bad modifier");
    	}
    }

    /**
     * 
     * @param context
     *                context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
        if (body != null) {
            body = body.analyze(context);
        }
        return this;
    }

    /**
     * Generate code for the method declaration.
     * 
     * @param output
     *                the code emitter (basically an abstraction
     *                for producing the .class file).
     */

    public void codegen(CLEmitter output) {
    	if(body != null) {
        	body.codegen(output);
        }
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JBlockDeclaration line=\"%d\">\n", line());
        p.indentRight();
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }
        if (body != null) {
            p.println("<Body>");
            p.indentRight();
            body.writeToStdOut(p);
            p.indentLeft();
            p.println("</Body>");
        }
        p.indentLeft();
        p.println("</JBlockDeclaration>");
    }
}
