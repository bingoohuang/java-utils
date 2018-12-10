package com.github.bingoohuang.utils.spring;

import lombok.val;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

public class XyzClassPathScanner extends ClassPathBeanDefinitionScanner {
    private final Class factoryBeanClass;
    private final Class<? extends Annotation>[] annotationClasses;

    public XyzClassPathScanner(BeanDefinitionRegistry registry, Class factoryBeanClass, Class<? extends Annotation>... annotationClasses) {
        super(registry, false);
        this.factoryBeanClass = factoryBeanClass;
        this.annotationClasses = annotationClasses;
    }

    /**
     * Configures parent scanner to search for the right interfaces. It can search
     * for all interfaces or just for those that extends a markerInterface or/and
     * those annotated with the annotationClass
     */
    public void registerFilters() {
        addExcludeFilter((metadataReader, metadataReaderFactory) -> !metadataReader.getClassMetadata().isInterface());
        for (val annotationClass : annotationClasses) {
            addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        }
    }

    /**
     * Calls the parent search that will search and register all the candidates.
     * Then the registered objects are post processed to set them as
     * MapperFactoryBeans
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        val beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No " + factoryBeanClass.getSimpleName() + " was found in '"
                    + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            for (val holder : beanDefinitions) {
                val definition = (GenericBeanDefinition) holder.getBeanDefinition();

                if (logger.isDebugEnabled()) {
                    logger.debug("Creating " + factoryBeanClass.getSimpleName() + " with name '" + holder.getBeanName()
                            + "' and '" + definition.getBeanClassName() + "' xyzInterface");
                }

                // the mapper interface is the original class of the bean
                // but, the actual class of the bean is MapperFactoryBean
                definition.getPropertyValues().add("xyzInterface", definition.getBeanClassName());
                definition.setBeanClass(factoryBeanClass);
            }
        }

        return beanDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping " + factoryBeanClass.getSimpleName() + " with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' interface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }

}
