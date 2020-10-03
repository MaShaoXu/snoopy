package com.sam.hadoop.annotation;

import com.sam.hadoop.model.HTable;
import com.sam.hadoop.model.HTableHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class HbaseTableSelector implements ImportSelector, ResourceLoaderAware, EnvironmentAware {

    private ResourcePatternResolver resourcePatternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    private Environment environment;

    private String formatSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "%s/**/*.class";

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableHbaseTable.class.getName()));
        String[] basePackages = annoAttrs.getStringArray("basePackages");
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        String active = environment.getProperty("spring.profiles.active");
        for (String basePackage : basePackages) {
            String packageSearchPath = String.format(formatSearchPath, resolveBasePackage(basePackage));

            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
                        String className = metadata.getClassName();
                        generatorTable(className, active);
                    }
                }
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new String[0];
    }

    private void generatorTable(String className, String active) throws ClassNotFoundException {
        Class<?> aClass = Class.forName(className);
        HbaseTable hbaseTable = aClass.getAnnotation(HbaseTable.class);
        if (hbaseTable == null) {
            return;
        }
        String namespace = hbaseTable.namespace();
        String tableName = hbaseTable.tableName();
        if (StringUtils.isEmpty(tableName)) {
            tableName = aClass.getSimpleName();
        }
        HTable hTable;
        if (StringUtils.isNotEmpty(namespace)) {
            String fullTableName = namespace + ":" + tableName;
            hTable = new HTable(namespace, fullTableName);
        } else if (StringUtils.isNotEmpty(active)) {
            String fullTableName = active + ":" + tableName;
            hTable = new HTable(active, fullTableName);
        } else {
            String fullTableName = "PUB:" + tableName;
            hTable = new HTable("PUB", fullTableName);
        }
        Field[] declaredFields = aClass.getDeclaredFields();
        Set<String> families = new HashSet<>();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(HbaseColumn.class)) {
                HbaseColumn hbaseColumn = field.getAnnotation(HbaseColumn.class);
                String family = hbaseColumn.family();
                if (StringUtils.isNotEmpty(family)) {
                    families.add(family);
                } else {
                    families.add(tableName);
                }
            }
        }
        hTable.setFamilies(families);
        log.info("Find HTable {}", hTable);
        HTableHolder.add(hTable);
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
    }

    private boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        TypeFilter typeFilter = new AnnotationTypeFilter(HbaseTable.class);
        if (typeFilter.match(metadataReader, metadataReaderFactory)) {
            return true;
        }
        return false;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
