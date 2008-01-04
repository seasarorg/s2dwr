/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dwr.creator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.create.AbstractCreator;
import org.directwebremoting.extend.Creator;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.ClassUtil;

/**
 * DWRからS2Container上のコンポーネントを利用するためのクラスです。
 * <p>
 * dwr.xmlに設定を記述することで、 S2Container上にあるコンポーネントをDWRを経由してJavaScriptから使えるようにします。
 * S2Creatorを利用するためには、 以下のようにdwr.xmlを設定します。
 * </p>
 * 
 * <pre>
 * &lt;dwr&gt;
 *     &lt;init&gt;
 *         &lt;creator id=&quot;s2&quot; class=&quot;org.seasar.dwr.creator.S2Creator&quot; /&gt;
 *     &lt;/init&gt;
 *     &lt;allow&gt;
 *         &lt;create creator=&quot;s2&quot; javascript=&quot;Hello&quot;&gt;
 *             &lt;param name=&quot;component&quot; value=&quot;HelloService&quot;/&gt;
 *         &lt;/create&gt;
 *         &lt;create creator=&quot;s2&quot; javascript=&quot;Echo&quot;&gt;
 *             &lt;param name=&quot;class&quot; value=&quot;dwr.example.EchoService&quot;/&gt;
 *         &lt;/create&gt;
 *     &lt;/allow&gt;
 * &lt;/dwr&gt;
 * </pre>
 */
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
            S2Container container = SingletonS2ContainerFactory.getContainer();
            ComponentDef componentDef = null;
            if (classType != null) {
                componentDef = container.getComponentDef(classType);
            } else {
                componentDef = container.getComponentDef(componentName);
            }
            if (componentDef != null) {
                instanceClass = componentDef.getComponentClass();
            } else {
                log.warn("Failed to instantiate object to detect type.");
                instanceClass = Object.class;
            }
        }
        return instanceClass;
    }

    public boolean isCacheable() {
        return false;
    }

    /**
     * インスタンスを生成する対象のコンポーネントをクラスで指定します。
     * <p>
     * このプロパティを指定する場合は、 componentプロパティを指定していてはいけません。
     * </p>
     * 
     * @param className
     *                クラス名をFQNで指定
     */
    public void setClass(String className) {
        if (componentName != null) {
            throw new IllegalStateException(
                    "Param name is already set. Set either name or class.");
        }
        classType = ClassUtil.forName(className);
    }

    /**
     * インスタンスを生成する対象のコンポーネントをコンポーネント名で指定します。
     * <p>
     * このプロパティを指定する場合は、 classプロパティを指定していてはいけません。
     * </p>
     * 
     * @param componentName
     *                コンポーネント名
     */
    public void setComponent(String componentName) {
        if (classType != null) {
            throw new IllegalStateException(
                    "Param class is already set. Set either name or class.");
        }
        this.componentName = componentName;
    }

}
