package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.event.DebbieEventPublisherAware;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.javaassist.JavaassistProxyBean;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/06 22:59.
 */
public class ClassBeanInfo<Bean> extends ClassInfo<Bean> implements RegistrableBeanInfo<Bean>, ClassDetailedBeanInfo {

    private final Set<String> beanNames = new HashSet<>();

    private BeanType beanType;
    private BeanProxyType beanProxyType;
    private Boolean lazyCreate;

    private volatile Bean preparedBean;
    private Bean bean;

    private final Map<String, Object> properties = new HashMap<>();

    private boolean noInterface = false;
    private Class<?> beanInterface;

    private Method initMethod;
    private Method destroyMethod;

    private List<BeanExecutableDependence> constructorBeanDependencies;
    private List<BeanExecutableDependence> initMethodBeanDependencies;
    private Map<FieldInfo, BeanInfo<?>> fieldBeanDependencies;
    private boolean virtualValue;

    private final Set<BeanCondition> conditions = new HashSet<>();

    private volatile boolean preparationCreated;
    private volatile boolean created;

    public ClassBeanInfo(Class<Bean> beanClass) {
        super(beanClass);
        Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = getClassAnnotations();
        if (classAnnotations == null || classAnnotations.isEmpty()) {
            return;
        }

        var value = classAnnotations.get(BeanComponent.class);
        if (value != null) {
            var componentInfo = BeanComponentParser.parse(value);
            setBeanComponent(componentInfo);
        } else {
            LOGGER.debug("class(" + beanClass + ") no @BeanComponent");
        }

        setMethods();
        conditions.add(ReflectionEnableCondition.INSTANCE);
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
    }

    public ClassBeanInfo(Class<Bean> beanClass, Bean preparedBean) {
        this(beanClass);
        this.preparedBean = preparedBean;
        conditions.add(ReflectionEnableCondition.INSTANCE);
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
    }

    public ClassBeanInfo(Class<Bean> beanClass, Map<Class<? extends Annotation>, BeanComponentParser> componentAnnotationTypes) {
        super(beanClass);
        Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = getClassAnnotations();
        if (classAnnotations == null || classAnnotations.isEmpty()) {
            return;
        }

        var value = classAnnotations.get(BeanComponent.class);
        if (value != null) {
            var componentInfo = BeanComponentParser.parse(value);
            setBeanComponent(componentInfo);
        } else {
            // resolve customize component annotation
            LOGGER.debug("class(" + beanClass + ") no @BeanComponent");
            for (Map.Entry<Class<? extends Annotation>, BeanComponentParser> entry : componentAnnotationTypes.entrySet()) {
                var type = entry.getKey();
                var parser = entry.getValue();
                if (classAnnotations.containsKey(type)) {
                    var info = parser.parse(classAnnotations.get(type).getOrigin(), beanClass);
                    setBeanComponent(info);
                    break;
                }
            }
        }
        setMethods();
        conditions.add(ReflectionEnableCondition.INSTANCE);
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
    }

    public ClassBeanInfo(ClassBeanInfo<Bean> classBeanInfo) {
        super(classBeanInfo);
        this.beanNames.addAll(classBeanInfo.beanNames);
        this.beanType = classBeanInfo.beanType;
        this.beanProxyType = classBeanInfo.beanProxyType;
        this.lazyCreate = classBeanInfo.lazyCreate;
        this.preparedBean = classBeanInfo.preparedBean;
        this.bean = classBeanInfo.bean;
        this.properties.putAll(classBeanInfo.properties);
        this.noInterface = classBeanInfo.noInterface;
        this.beanInterface = classBeanInfo.beanInterface;
        this.initMethod = classBeanInfo.initMethod;
        this.destroyMethod = classBeanInfo.destroyMethod;
        this.constructorBeanDependencies = classBeanInfo.constructorBeanDependencies;
        this.initMethodBeanDependencies = classBeanInfo.initMethodBeanDependencies;
        this.fieldBeanDependencies = classBeanInfo.fieldBeanDependencies;
        this.virtualValue = classBeanInfo.virtualValue;
        this.conditions.addAll(classBeanInfo.conditions);
        this.preparationCreated = classBeanInfo.preparationCreated;
        this.created = classBeanInfo.created;
        if (beanNames.isEmpty()) {
            setDefaultName();
        }
    }

    protected boolean resolveBeanComponent(Annotation value) {
        if (value == null) {
            return false;
        }
        if (value.annotationType() == BeanComponent.class) {
            var beanService = ((BeanComponent) value);
            var info = new DefaultBeanComponentParser().parse(beanService);
            setBeanComponent(info);
            return true;
        }
        return false;
    }

    protected void setBeanComponent(BeanComponentInfo info) {
        if (beanNames.isEmpty()) {
            if (info.hasName()) {
                beanNames.add(info.getName());
            }
        }
        if (beanType == null) {
            beanType = info.getType();
        }

        if (lazyCreate == null) {
            lazyCreate = info.isLazy();
        } else {
            // note: default value is true
            lazyCreate = true;
        }

        if (beanProxyType == null) {
            beanProxyType = info.getProxy();
        }

        Set<Class<? extends BeanCondition>> condition = info.getCondition();
        if (condition != null && condition.size() > 0) {
            for (Class<? extends BeanCondition> conditionClass : condition) {
                if (conditionClass == DefaultBeanCondition.class) {
                    this.conditions.add(DefaultBeanCondition.INSTANCE);
                } else if (conditionClass == ReflectionEnableCondition.class) {
                    conditions.add(ReflectionEnableCondition.INSTANCE);
                } else {
                    BeanCondition beanCondition = ReflectionHelper.newInstance(conditionClass);
                    this.conditions.add(beanCondition);
                }
            }
        }
    }

    protected boolean resolveComponent(Class<? extends Annotation> key, Annotation value) {
        var info = BeanComponentParser.parse(key, value);
        if (info != null) {
            setBeanComponent(info);
            return true;
        }

        return false;
    }

    protected void setMethods() {
        Set<Method> methods = getMethods();
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> {
                // beanMethods.put(method.getName(), method);
                if (method.getAnnotation(BeanInit.class) != null) {
                    initMethod = method;
                    this.setInitMethod(initMethod);
                }
                if (method.getAnnotation(BeanDestroy.class) != null) {
                    this.setDestroyMethod(method);
                }
            });
        }
    }

    public void setConstructorBeanDependencies(List<BeanExecutableDependence> constructorBeanDependencies) {
        this.constructorBeanDependencies = constructorBeanDependencies;
    }

    public void addConstructorBeanDependent(Integer index, BeanInfo<?> beanInfo) {
        if (this.constructorBeanDependencies == null) {
            this.constructorBeanDependencies = new ArrayList<>();
        }
        this.constructorBeanDependencies.add(new BeanExecutableDependence(index, beanInfo, beanInfo.getBeanClass(), beanInfo.getName()));
    }

    public void setInitMethodBeanDependencies(List<BeanExecutableDependence> initMethodBeanDependencies) {
        this.initMethodBeanDependencies = initMethodBeanDependencies;
    }

    public void addInitMethodBeanDependent(Integer index, BeanInfo<?> beanInfo) {
        if (this.initMethodBeanDependencies == null) {
            this.initMethodBeanDependencies = new ArrayList<>();
        }
        this.initMethodBeanDependencies.add(new BeanExecutableDependence(index, beanInfo, beanInfo.getBeanClass(), beanInfo.getName()));
    }

    public void setFieldBeanDependencies(Map<FieldInfo, ? extends BeanInfo<?>> fieldBeanDependencies) {
        if (this.fieldBeanDependencies == null) {
            this.fieldBeanDependencies = new HashMap<>();
        }
        this.fieldBeanDependencies.putAll(fieldBeanDependencies);
    }

    public List<BeanExecutableDependence> getInitMethodBeanDependencies() {
        if (initMethodBeanDependencies == null) {
            initMethodBeanDependencies = new ArrayList<>();
        }
        return initMethodBeanDependencies;
    }

    public boolean isInitMethodBeanDependentHasValue() {
        for (BeanExecutableDependence dependence : initMethodBeanDependencies) {
            if (!dependence.isPresent()) {
                return false;
            }
        }
        return true;
    }

    public List<BeanExecutableDependence> getConstructorBeanDependencies() {
        if (constructorBeanDependencies == null) {
            constructorBeanDependencies = new ArrayList<>();
        }
        return constructorBeanDependencies;
    }

    public void getCircleDependencyInConstructor() {
        if (constructorBeanDependencies == null) {
            return;
        }
        for (BeanExecutableDependence dependence : constructorBeanDependencies) {
            getCircleDependencyInConstructor(this, dependence.getBeanInfo(), new StringBuilder());
        }
    }

    public void getCircleDependencyInConstructor(BeanInfo<?> beanInfo, BeanInfo dependency,
                                                 StringBuilder dependencyLine) {
        if (dependency == null) {
            return;
        }
        if (dependency instanceof ClassBeanInfo<?> classBeanInfo) {
            if (classBeanInfo.constructorBeanDependencies == null) {
                return;
            }
            Class<?> beanType = beanInfo.getBeanClass();
            for (BeanExecutableDependence dependence : classBeanInfo.constructorBeanDependencies) {
                var type = dependence.getType();
                LOGGER.trace(beanType + " ===>>> " + type);
                if (beanType == type) {
                    LOGGER.error(beanType + " --> " + type);
                } else {
                    getCircleDependencyInConstructor(beanInfo, dependence.getBeanInfo(), dependencyLine);
                }
            }
        }
    }

    public boolean isConstructorBeanDependentHasValue() {
        for (BeanExecutableDependence dependence : constructorBeanDependencies) {
            BeanInfo<?> beanInfo = dependence.getBeanInfo();
            if (beanInfo instanceof DebbieReflectionBeanFactory<?> reflectionBeanFactory) {
                if (!(reflectionBeanFactory.isPreparationCreated() || reflectionBeanFactory.isCreated())
                        && dependence.getValue() == null) {
                    return false;
                }
            }
            if (beanInfo instanceof BeanFactory localFactoryBeanInfo) {
                if (!localFactoryBeanInfo.isCreated() && dependence.getValue() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isConstructorBeanDependentHasVirtualValue() {
        if (constructorBeanDependencies != null && !constructorBeanDependencies.isEmpty()) {
            for (BeanExecutableDependence dependence : constructorBeanDependencies) {
                BeanInfo<?> beanInfo = dependence.getBeanInfo();
                if (beanInfo instanceof DebbieReflectionBeanFactory<?> reflectionBeanFactory) {
                    if (reflectionBeanFactory.hasVirtualValue()) {
                        return true;
                    }
                }
                if (beanInfo instanceof BeanFactory localFactoryBeanInfo) {
                    if (!localFactoryBeanInfo.isCreated() || dependence.getValue() == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isFieldBeanDependencyHasVirtualValue() {
        if (fieldBeanDependencies != null && !fieldBeanDependencies.isEmpty()) {
            Set<Map.Entry<FieldInfo, BeanInfo<?>>> entries = fieldBeanDependencies.entrySet();
            for (Map.Entry<FieldInfo, BeanInfo<?>> entry : entries) {
                FieldInfo fieldInfo = entry.getKey();
                if (!fieldInfo.hasValue()) {
                    return true;
                }
                BeanInfo<?> beanInfo = entry.getValue();
                if (beanInfo instanceof DebbieReflectionBeanFactory<?> reflectionBeanFactory) {
                    if (reflectionBeanFactory.hasVirtualValue()) {
                        return true;
                    }
                }
                if (beanInfo instanceof BeanFactory localFactoryBeanInfo) {
                    if (!localFactoryBeanInfo.isCreated()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Map<FieldInfo, BeanInfo<?>> getHasVirtualValueFieldBeanDependencies() {
        Map<FieldInfo, BeanInfo<?>> map = new HashMap<>();
        if (fieldBeanDependencies != null && !fieldBeanDependencies.isEmpty()) {
            Set<Map.Entry<FieldInfo, BeanInfo<?>>> entries = fieldBeanDependencies.entrySet();
            for (Map.Entry<FieldInfo, BeanInfo<?>> entry : entries) {
                FieldInfo fieldInfo = entry.getKey();
                if (!fieldInfo.hasValue()) {
                    map.put(entry.getKey(), entry.getValue());
                    continue;
                }
                BeanInfo<?> beanInfo = entry.getValue();
                if (beanInfo instanceof DebbieReflectionBeanFactory<?> reflectionBeanFactory) {
                    if (reflectionBeanFactory.hasVirtualValue()) {
                        map.put(entry.getKey(), entry.getValue());
                        continue;
                    }
                }
                if (beanInfo instanceof BeanFactory localFactoryBeanInfo) {
                    if (!localFactoryBeanInfo.isCreated()) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return map;
    }

    public Map<FieldInfo, BeanInfo<?>> getFieldBeanDependencies() {
        return fieldBeanDependencies;
    }

    public Boolean getLazyCreate() {
        return lazyCreate;
    }

    public void setLazyCreate(Boolean lazyCreate) {
        this.lazyCreate = lazyCreate;
    }

    public Bean getBean() {
        return bean;
    }

    public void setPreparedBean(Bean preparedBean) {
        this.preparedBean = preparedBean;
    }

    public Bean getPreparedBean() {
        if (preparationCreated) {
            return preparedBean;
        }
        return null;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public boolean isNoInterface() {
        return noInterface;
    }

    public void setNoInterface(boolean noInterface) {
        this.noInterface = noInterface;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> getInterface(Collection<Class<?>> ignoreInterfaces) {
        if (noInterface) {
            return null;
        }
        if (beanInterface == null) {
            Class<?> clazz = super.getClazz();
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 0) {
                LOGGER.trace(() -> clazz.getName() + " has no direct interface");
                noInterface = true;
                beanInterface = null;
                return null;
            }
            outer: for (Class<?> classInterface : interfaces) {
                for (Class<?> ignoreInterface : ignoreInterfaces) {
                    if (classInterface.getPackageName().startsWith("java.") || classInterface == ignoreInterface) {
                        continue outer;
                    }
                }
                // 取第一个符合条件的接口
                this.beanInterface = classInterface;
                noInterface = false;
                break;
            }
            if (this.beanInterface == null) {
                noInterface = true;
            }
        }
        if (!noInterface) {
            return (Class<T>) beanInterface;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> getBeanInterface() {
        if (noInterface) {
            return null;
        }
        if (beanInterface == null) {
            Class<?> clazz = super.getClazz();
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 0) {
                LOGGER.trace(() -> clazz.getName() + " has no direct interface");
                noInterface = true;
                beanInterface = null;
            } else {
                beanInterface = interfaces[0];
                noInterface = false;
                if (beanInterface.getPackageName().startsWith("java.")
                        || beanInterface == JavaassistProxyBean.class
                        || beanInterface == DebbieProperties.class || beanInterface == DebbieConfiguration.class
                        || beanInterface == ApplicationContextAware.class || beanInterface == ClassLoaderAware.class
                        || beanInterface == GlobalBeanFactoryAware.class
                        || beanInterface == DebbieEventPublisherAware.class) {
                    beanInterface = null;
                    noInterface = true;
                }
            }
        }
        return (Class<T>) beanInterface;
    }

    public void setBeanInterface(Class<?> beanInterface) {
        this.beanInterface = beanInterface;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public boolean hasVirtualValue() {
        return virtualValue;
    }

    public void setVirtualValue(boolean virtualValue) {
        this.virtualValue = virtualValue;
    }

    public boolean hasNoVirtualValue() {
        return !virtualValue;
    }

    public boolean isPreparationCreated() {
        return preparationCreated;
    }

    public void setPreparationCreated(boolean preparationCreated) {
        this.preparationCreated = preparationCreated;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.getClazz();
    }

    @Override
    public BeanProxyType getBeanProxyType() {
        return beanProxyType;
    }

    @Override
    public boolean needProxy() {
        return beanProxyType != BeanProxyType.NO;
    }

    @Override
    public Set<BeanCondition> getConditions() {
        return conditions;
    }

    @Override
    public boolean isLazyCreate() {
        return lazyCreate != null && lazyCreate;
    }

    @Override
    public BeanType getBeanType() {
        return beanType;
    }

    @Override
    public boolean isSingleton() {
        return this.beanType != null && this.beanType == BeanType.SINGLETON;
    }

    @Override
    public boolean containName(String name) {
        if (beanNames.isEmpty()) {
            String localName = getBeanClass().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(localName);
            beanNames.add(localName);
            beanNames.add(getBeanClass().getName());
        }
        return beanNames.contains(name);
    }

    @Override
    public Set<String> getBeanNames() {
        return beanNames;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public void setBeanProxyType(BeanProxyType beanProxyType) {
        this.beanProxyType = beanProxyType;
    }

    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }

    @Override
    public String getName() {
        String name = this.beanNames.isEmpty() ? null : this.beanNames.iterator().next();
        if (name == null || name.isBlank()) {
            name = super.getClazz().getSimpleName();
            name = StringUtils.toFirstCharLowerCase(name);
            this.beanNames.add(name);
            this.beanNames.add(getBeanClass().getName());
        }
        return name;
    }

    public void addBeanName(String... beanName) {
        for (String s : beanName) {
            if (s != null && !s.isBlank()) {
                this.beanNames.add(s);
            }
        }
    }

    public void addBeanName(Set<String> beanNames) {
        this.beanNames.addAll(beanNames);
    }

    public void setBean(Bean bean) {
        this.preparationCreated = true;
        this.preparedBean = bean;
        this.created = true;
        this.bean = bean;
    }

    public <T extends Bean> void setBean(Supplier<T> bean) {
        this.bean = bean.get();
    }

    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    public void destruct(ApplicationContext applicationContext) {
        beanNames.clear();
        properties.clear();

        bean = null;
        if (constructorBeanDependencies != null) {
            constructorBeanDependencies.clear();
        }
        constructorBeanDependencies = null;
        if (initMethodBeanDependencies != null) {
            initMethodBeanDependencies.clear();
        }
        initMethodBeanDependencies = null;
        if (fieldBeanDependencies != null) {
            fieldBeanDependencies.clear();
        }
        fieldBeanDependencies = null;
        virtualValue = false;
    }

    @Override
    public ClassBeanInfo<Bean> copy() {
        ClassBeanInfo<Bean> beanFactory = new ClassBeanInfo<>(getClazz());
        if (!beanNames.isEmpty()) {
            beanFactory.beanNames.addAll(beanNames);
        }
        if (beanType != null) {
            beanFactory.setBeanType(beanType);
        }

        if (beanProxyType != null) {
            beanFactory.setBeanProxyType(beanProxyType);
        }

        if (!properties.isEmpty()) {
            beanFactory.properties.putAll(properties);
        }

        return beanFactory;
    }

    public void copyFrom(ClassBeanInfo<Bean> beanInfo) {
        this.beanNames.addAll(beanInfo.beanNames);
        this.preparedBean = beanInfo.preparedBean;
        this.bean = beanInfo.bean;
        this.properties.putAll(beanInfo.properties);
        this.constructorBeanDependencies = beanInfo.constructorBeanDependencies;
        this.initMethodBeanDependencies = beanInfo.initMethodBeanDependencies;
        this.fieldBeanDependencies = beanInfo.fieldBeanDependencies;
        this.virtualValue = beanInfo.virtualValue;
        this.conditions.addAll(beanInfo.conditions);
        this.preparationCreated = beanInfo.preparationCreated;
        this.created = beanInfo.created;
    }

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }

    @Override
    public String toString() {
        return "\"{" +
                "\"beanClass\":" + getBeanClass() + "," +
                "\"beanNames\":" + beanNames + "," +
                "\"beanType\":" + beanType + "," +
                "\"lazyCreate\":" + lazyCreate + "," +
                "\"bean\":" + bean + "," +
                "\"noInterface\":" + noInterface + "," +
                "\"beanInterface\":" + beanInterface + "," +
                "\"initMethod\":" + initMethod + "," +
                "\"destroyMethod\":" + destroyMethod + "," +
                "\"constructorBeanDependencies\":" + constructorBeanDependencies + "," +
                "\"initMethodBeanDependencies\":" + initMethodBeanDependencies + "," +
                "\"fieldBeanDependencies\":" + fieldBeanDependencies + "," +
                "\"virtualValue\":" + virtualValue + "}";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassBeanInfo.class);
}
