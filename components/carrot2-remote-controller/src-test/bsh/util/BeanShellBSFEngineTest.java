package bsh.util;

import org.apache.bsf.*;

import java.util.Vector;

/**
 * A test case for the {@link bsh.util.C2BeanShellBSFEngine}.
 * 
 * @author Pat Niemeyer
 * @author Dawid Weiss
 */
public class BeanShellBSFEngineTest extends junit.framework.TestCase {

    private BSFManager mgr;
    
    static {
        final String [] extensions = { "bsh" };
        BSFManager.registerScriptingEngine( 
            "beanshell", "bsh.util.BeanShellBSFEngine", extensions );
    }

    public BeanShellBSFEngineTest(String s) {
        super(s);
    }
    
    protected void setUp() throws Exception {
        this.mgr = new BSFManager();
    }

    protected void tearDown() throws Exception {
        this.mgr = null;
    }

    public void testBSFRegistration() {
        assertTrue(BSFManager.isLanguageRegistered("beanshell")); 
    }

    public void testBeans() throws BSFException {
        mgr.declareBean("foo", "fooString", String.class);
        mgr.declareBean("bar", "barString", String.class);
        mgr.registerBean("gee", "geeString");

        BSFEngine beanshellEngine = mgr.loadScriptingEngine("beanshell");

        String script = "foo + bar + bsf.lookupBean(\"gee\")";
        Object result = beanshellEngine.eval( "Test eval...", -1, -1, script );

        assertTrue( result.equals("fooStringbarStringgeeString" ) );
    }

    public void testApply() throws BSFException {
        BSFEngine beanshellEngine = mgr.loadScriptingEngine("beanshell");

        Vector names = new Vector();
        names.addElement("name");
        Vector vals = new Vector();
        vals.addElement("Pat");

        String script = "name + name";
        
        Object result = beanshellEngine.apply( 
            "source string...", -1, -1, script, names, vals );
    
        assertTrue( result.equals("PatPat" ) );

        result = beanshellEngine.eval( "Test eval...", -1, -1, "name" );

        // name should not be set 
        assertTrue( result == null );

        // Verify the primitives are unwrapped
        result = beanshellEngine.eval( "Test eval...", -1, -1, "1+1");

        assertTrue( result instanceof Integer 
            && ((Integer)result).intValue() == 2 );
    }

    public void testScriptEvalError() throws BSFException {
        BSFEngine beanshellEngine = mgr.loadScriptingEngine("beanshell");

        String testScript = "int a, int b;";
        try {
            beanshellEngine.exec("", -1, -1, testScript);
            fail();
        } catch (BSFException e) {
            // Ok, expected.
        }
    }
    
    public void testScriptExecutionError() throws BSFException {
        BSFEngine beanshellEngine = mgr.loadScriptingEngine("beanshell");

        String testScript = "throw new RuntimeException(\"abc\")";
        try {
            beanshellEngine.exec("", -1, -1, testScript);
            fail();
        } catch (BSFException e) {
            System.out.println(e);
            assertTrue(e.getTargetException() != null);
            assertTrue(e.getTargetException() instanceof RuntimeException);
            assertEquals("abc", e.getTargetException().getMessage());
        }
    }
}

