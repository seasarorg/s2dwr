package org.seasar.dwr.creator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.create.AbstractCreator;
import org.directwebremoting.extend.Creator;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ClassUtil;

public class S2Creator extends AbstractCreator implements Creator {

    private static Log log = LogFactory.getLog(S2Creator.class);

    private String componentName;

    private Class classType;

    private Class instanceClass;

    public Object getInstance() throws InstantiationException {
        if (classType == null && componentName == null) {
            throw new IllegalStateException("Param name or class is must.");
        }
        S2Container container = SingletonS2ContainerFactory.getContainer();
        Object obj = null;
        if (classType != null) {
            obj = container.getComponent(classType);
        } else {
            obj = container.getComponent(componentName);
        }
        return obj;
    }

    public String getScope() {
        return PAGE;
    }

    public Class getType() {
        if (instanceClass == null) {
            try {
                instanceClass = getInstance().getClass();
            } catch (InstantiationException e) {
                log.warn("Failed to instantiate object to detect type.", e);
                instanceClass = Object.class;
            }
        }
        return instanceClass;
    }

    public boolean isCacheable() {
        return false;
    }

    public void setClass(String className) {
        if (componentName != null) {
            throw new IllegalStateException(
                    "Param name is already set. Set either name or class.");
        }
        classType = ClassUtil.forName(className);
    }

    public void setComponent(String componentName) {
        if (classType != null) {
            throw new IllegalStateException(
                    "Param class is already set. Set either name or class.");
        }
        this.componentName = componentName;
    }

}
