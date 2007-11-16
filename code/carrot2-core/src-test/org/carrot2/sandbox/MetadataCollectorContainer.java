package org.carrot2.sandbox;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import org.carrot2.core.Configurable;
import org.carrot2.core.parameters.Parameter;
import org.carrot2.core.parameters.ParameterGroup;
import org.picocontainer.DefaultPicoContainer;

/**
 * 
 */
@SuppressWarnings("serial")
public class MetadataCollectorContainer 
    extends DefaultPicoContainer
{
    private RequestLifecycleStrategy lifecycleStrategy;
    private final static Logger logger = Logger.getLogger(MetadataCollectorContainer.class.getName());

    public MetadataCollectorContainer(RequestLifecycleStrategy s)
    {
        super(s, null);
        this.lifecycleStrategy = s;
    }
    
    /**
     * 
     */
    public <T> Iterable<ParameterGroup> resolveInstantiationParameters(Class<T> algorithmClass)
    {
        final ArrayList<ParameterGroup> pg = new ArrayList<ParameterGroup>();
        final HashSet<Class<?>> visited = new HashSet<Class<?>>();
        resolveInstantiationParameters(algorithmClass, pg, visited);
        return pg;
    }

    /**
     * TODO: Question: how can we resolve the instantiation parameters without the
     * actual values? :-) Should we rely on default values only? Once this problem is solved
     * we need to replace the parameter of {@link ConfigurableType} with a parameter
     * of {@link ClassType} here.
     * 
     * ANSWER: We resolve only the declarations of instantiation parameters so that we know
     * what to put in the dependency-injection container... If, for example, you'd like to declare
     * a required interface parameter (used in the constructor, so it has to be injected), then
     * you could at the same time provide some default value (implementation class) for it. This class
     * would be instantiated and added to the container in case no overriden value were provided. 
     * I was just playing around though... it's still quite vague to me how it could actually work.
     */
    private <T> void resolveInstantiationParameters(
        Class<T> algorithmClass, 
        ArrayList<ParameterGroup> pg, 
        HashSet<Class<?>> visited)
    {
        try
        {
            visited.add(algorithmClass);

            final Method method = algorithmClass.getMethod("getInstantiationParameters");
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                && ParameterGroup.class.isAssignableFrom(method.getReturnType()))
            {
                final ParameterGroup result = (ParameterGroup) method.invoke(null);
                pg.add(result);

                // Descend recursively into unresolved types.
                for (Parameter p : result.getParameters())
                {
                    if (Configurable.class.isAssignableFrom(p.type.getType()))
                    {
                        final Class<?> sub = p.type.getType();
                        if (!visited.contains(sub)) {
                            resolveInstantiationParameters(sub, pg, visited);
                        }
                    }
                }
            }
        }
        catch (SecurityException e)
        {
            throw new RuntimeException("The container must have privileges for accessing methods.", e);
        }
        catch (NoSuchMethodException e)
        {
            // Ignore silently.
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            rethrow(e);
        }
    }

    /**
     * 
     */
    private void rethrow(InvocationTargetException e)
    {
        if (e.getCause() instanceof RuntimeException) {
            throw ((RuntimeException) e.getCause());
        }
        if (e.getCause() instanceof Error) {
            throw ((Error) e.getCause());
        }
        throw new RuntimeException("Invocation target exception (checked): " + e.toString(), e);
    }
}
