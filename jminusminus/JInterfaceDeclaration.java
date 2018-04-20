// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * A interface declaration has a list of modifiers, a name, a super interface and a
 * interface block; it distinguishes between instance fields and static (interface)
 * fields for initialization, and it defines a type. It also introduces its own
 * (interface) context.
 */

class JInterfaceDeclaration extends JAST implements JTypeDecl {

    /** interface modifiers. */
    private ArrayList<String> mods;

    /** Interface name. */
    private String name;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;

    /** Super interface types. */
    private ArrayList<Type> superInterfaces;

    /** This interface type. */
    private Type thisType;

    /** Context for this interface. */
    private ClassContext context;

    /**
     * Construct an AST node for a class declaration given the line number, list
     * of class modifiers, name of the class, its super class type, and the
     * class block.
     * 
     * @param line
     *            line in which the class declaration occurs in the source file.
     * @param mods
     *            class modifiers.
     * @param name
     *            class name.
     * @param superType
     *            super class type.
     * @param classBlock
     *            class block.
     */

    //Interface work as abstract class, but methods has no body and must be implemented

    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name,
    		ArrayList<Type> superInterfaces, ArrayList<JMember> interfaceBlock) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.superInterfaces = superInterfaces;
        this.interfaceBlock = interfaceBlock;
    }

    /**
     * Return the interface name.
     * 
     * @return the interface name.
     */

    public String name() {
        return name;
    }

    /**
     * Return the type that this interface declaration defines.
     * 
     * @return the defined type.
     */

    public Type thisType() {
        return thisType;
    }
    
    public Type superType() {
        return Type.VOID;
    }

    /**
     * Declare this interface in the parent (compilation unit) context.
     * 
     * @param context
     *            the parent (compilation unit) context.
     */

    public void declareThisType(Context context) {
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        mods.add("interface");
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null,
                false); 
        thisType = Type.typeFor(partial.toClass());
        context.addType(line, thisType);
    }

    /**
     * Pre-analyze the members of this declaration in the parent context.
     * Pre-analysis extends to the member headers (including method headers) but
     * not into the bodies.
     * 
     * @param context
     *            the parent (compilation unit) context.
     */

    public void preAnalyze(Context context) {
    	
        if(mods.contains("final")) {
        	JAST.compilationUnit.reportSemanticError(line,
                    "Interface cannot be a final type");
        }

        // Construct a class context
        this.context = new ClassContext(this, context);

        // Resolve superInterfaces
        ArrayList typeTmp = new ArrayList<Type>();
        for (Type inter : superInterfaces) {
            inter = inter.resolve(this.context);
            typeTmp.add(inter);
        }
        superInterfaces =  typeTmp;
        
        for (Type inter : superInterfaces) {
     	   if(!inter.isInterface()) {
     		   JAST.compilationUnit.reportSemanticError(line,
                        "%s is not an interface", inter.toString());
     	   }
        }

        // Create the (partial) class
        CLEmitter partial = new CLEmitter(false);

        // Add the class header to the partial class
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        ArrayList<String> stringInterfaces = new ArrayList<String>();
        for(Type inter : superInterfaces) {
        	stringInterfaces.add(inter.jvmName());
        }
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), 
        		stringInterfaces, false);

        // Pre-analyze the members and add them to the partial
        // class
        for (JMember member : interfaceBlock) {
        	JMethodDeclaration method = (JMethodDeclaration) member;
            method.setAbstract();
        }
        
        for (JMember member : interfaceBlock) {
            member.preAnalyze(this.context, partial);
        }

        // Get the Class rep for the (partial) class and make it
        // the
        // representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }
    }

    /**
     * Perform semantic analysis on the interface and all of its members within the
     * given context. Analysis includes field initializations and the method
     * bodies.
     * 
     * @param context
     *            the parent (compilation unit) context. Ignored here.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
    	// Analyze all members
        for (JMember member : interfaceBlock) {
            ((JAST) member).analyze(this.context);
        }

        return this;
    }

    /**
     * Generate code for the interface declaration.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .interface file).
     */

    public void codegen(CLEmitter output) {
    	String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
    	ArrayList<String> stringInterfaces = new ArrayList<String>();
        for(Type inter : superInterfaces) {
        	stringInterfaces.add(inter.jvmName());
        }
        output.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), 
        		stringInterfaces, false);

        // The members
        for (JMember member : interfaceBlock) {
            ((JAST) member).codegen(output);
        }

    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
    	String interfaces = "";
    	if(superInterfaces.isEmpty()) {
    		interfaces = "null";
    	} else {
    		for(Type t : superInterfaces)
    			interfaces = interfaces + t.toString() + ",";
    		interfaces = interfaces.substring(0, interfaces.length() - 1);
    	}
    	
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\""
                + " super=\"%s\">\n", line(), name, interfaces);
        p.indentRight();
        if (context != null) {
            context.writeToStdOut(p);
        }
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }
        if (interfaceBlock != null) {
            p.println("<InterfaceBlock>");
            for (JMember member : interfaceBlock) {
                ((JAST) member).writeToStdOut(p);
            }
            p.println("</InterfaceBlock>");
        }
        p.indentLeft();
        p.println("</JInterfaceDeclaration>");
    }

}
