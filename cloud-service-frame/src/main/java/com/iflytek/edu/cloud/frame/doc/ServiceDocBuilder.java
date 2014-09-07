/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.doc;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.iflytek.edu.cloud.frame.annotation.ServiceMethod;
import com.iflytek.edu.cloud.frame.doc.model.ClassDoc;
import com.iflytek.edu.cloud.frame.doc.model.JavaBeanDoc;
import com.iflytek.edu.cloud.frame.doc.model.JavaBeanDoc.FieldDoc;
import com.iflytek.edu.cloud.frame.doc.model.MethodDoc;
import com.iflytek.edu.cloud.frame.doc.model.ParamDoc;
import com.iflytek.edu.cloud.frame.doc.model.ReturnDoc;
import com.iflytek.edu.cloud.frame.utils.EnvUtil;
import com.iflytek.edu.cloud.frame.utils.SystemPropertyUtil;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class ServiceDocBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDocBuilder.class);
	
	private JavaDocBuilder builder;
	
	public ServiceDocBuilder() {
		builder = new JavaDocBuilder();
		builder.setEncoding("UTF-8");
		addJavaSource();
	}
	
	public List<ClassDoc> buildDoc() {
		List<ClassDoc> classDocs = new ArrayList<ClassDoc>();
		
		List<JavaClass> javaClasses = findRestServices();
		
		for(JavaClass javaClass : javaClasses) {
			ClassDoc classDoc = new ClassDoc();
			classDoc.setServiceDesc(javaClass.getComment());
			classDoc.setServiceName(javaClass.getTagByName("serviceName").getValue());
			
			List<JavaMethod> javaMethods = findMethods(javaClass);
			for(JavaMethod method : javaMethods) {
				//方法文档信息
				MethodDoc methodDoc = new MethodDoc();
				Annotation[] annotations = method.getAnnotations();
				for(Annotation ann : annotations) {
					 if(ServiceMethod.class.getName().equals(ann.getType().getFullyQualifiedName())) {
						 String name = (String)ann.getNamedParameter("value");
						 String version = (String)ann.getNamedParameter("version");
						 methodDoc.setName(StringUtils.strip(name.trim(), "\""));
						 methodDoc.setVersion(StringUtils.strip(version.trim(), "\""));
						 break;
					 }
				}
				methodDoc.setDescription(method.getComment());
			
				//返回值文档信息
				ReturnDoc returnDoc = new ReturnDoc();
				returnDoc.setDataType(method.getReturnType().getFullyQualifiedName());
				
				if("java.util.List".equals(returnDoc.getDataType())) {
					Type type = method.getReturnType().getActualTypeArguments()[0];
					if(!ClassUtils.isPrimitiveOrWrapper(type.getFullyQualifiedName())) {
						returnDoc.setBeanName(type.getFullyQualifiedName());
						JavaBeanDoc beanDoc = getJavaBeanDoc(type.getFullyQualifiedName());
						classDoc.getBeanDocs().add(beanDoc);
					}
				}
				
				if(method.getTagByName("return") ==null) {
					throw new RuntimeException("方法 " + method.getName() + " 没有return注释");
				}
				
				//解析返回实例数据，例如返回值文档注释格式： @return userid A2342312 $$ 用户ID 
				String returnDesc = method.getTagByName("return").getValue();
				String[] desces = returnDesc.split("\\$\\$");
				if(desces.length > 1) {
					returnDoc.setExampleData(desces[0]);
					returnDoc.setDescription(desces[1]);
				} else {
					returnDoc.setDescription(returnDesc);
				}
				
				returnDoc.setDimensions(method.getReturnType().getDimensions());
				methodDoc.setReturnDoc(returnDoc);
				
				//方法参数文档信息
				JavaParameter[] parameters = method.getParameters();
				DocletTag[] paramTags = method.getTagsByName("param");
				for(int i=0, len=parameters.length; i<len; i++) {
					JavaParameter parameter = parameters[i];
					String type = parameter.getType().getFullyQualifiedName();
					String paramName = parameter.getName();
					
					ParamDoc paramDoc = new ParamDoc();
					paramDoc.setName(paramName);
					paramDoc.setDataType(type);
					
					//解析参数实例数据，例如参数文档注释格式： @param userid A2342312 $$ 用户ID 
					String paramDesc = getParamDesc(paramName, paramTags);
					desces = paramDesc.split("\\$\\$");
					if(desces.length > 1) {
						paramDoc.setExampleData(desces[0].trim());
						paramDoc.setDescription(desces[1].trim());
					} else {
						paramDoc.setDescription(paramDesc);
					}
					
					methodDoc.getParamDocs().add(paramDoc);
					
					//基本类型，和javax.servlet.*相关的类排除掉
					if(!ClassUtils.isPrimitiveOrWrapper(type) && !type.startsWith("javax.servlet")) {
						classDoc.getBeanDocs().add(getJavaBeanDoc(type));
					}
				}
				
				classDoc.getMethodDocs().add(methodDoc);
			}
			
			boolean isNew = true;
			for(ClassDoc doc : classDocs) {
				if(doc.getServiceName().equals(classDoc.getServiceName())) {
					doc.getBeanDocs().addAll(classDoc.getBeanDocs());
					doc.getMethodDocs().addAll(classDoc.getMethodDocs());
					isNew = false;
					break;
				}
			}
			
			if(isNew)
				classDocs.add(classDoc);
		}
		
		return classDocs;
	}
	
	private JavaBeanDoc getJavaBeanDoc(String type) {
		JavaClass javaClass = builder.getClassByName(type);
		JavaField[] fields = javaClass.getFields();
		JavaBeanDoc beanDoc = new JavaBeanDoc();
		beanDoc.setName(javaClass.getFullyQualifiedName());
		beanDoc.setDescription(javaClass.getComment());
		for(JavaField field : fields) {
			FieldDoc fieldDoc = new FieldDoc();
			fieldDoc.setName(field.getName());
			fieldDoc.setDataType(field.getType().getFullyQualifiedName());
			fieldDoc.setDescription(field.getComment());
			
			DocletTag tag = field.getTagByName("data");
			if(tag != null)
				fieldDoc.setExampleData(tag.getValue());
			
			beanDoc.getFieldDocs().add(fieldDoc);
			
			Annotation[] anns = field.getAnnotations();
			for(Annotation ann : anns) {
				String typeName = ann.getType().getFullyQualifiedName();
				
				if(NotEmpty.class.getName().equals(typeName) || 
						NotNull.class.getName().equals(typeName)) {
					fieldDoc.setRequired(true);
					break;
				}
			}
		}
		
		return beanDoc;
	}
	
	/**
	 * 获取参数信息；
	 * 
	 * @return
	 */
	private String getParamDesc(String paramName, DocletTag[] paramTags) {
		for(DocletTag tag : paramTags) {
			String desc = tag.getValue();
			if(desc != null && desc.trim().startsWith(paramName)) {
				desc = desc.substring(paramName.length()).trim();
				return desc;
			}
		}
		
		return "";
	}
	
	public List<JavaClass> findRestServices() {
		List<JavaClass> restJavaClasses = new ArrayList<JavaClass>();
		
		JavaClass[] javaClasses = builder.getClasses();
		for(JavaClass javaClass : javaClasses) {
			Annotation[] annotations = javaClass.getAnnotations();
			for(Annotation ann : annotations) {
				 if(RestController.class.getName().equals(ann.getType().getFullyQualifiedName())) {
					 restJavaClasses.add(javaClass);
					 break;
				 }
			}
		}
		
		return restJavaClasses;
	}
	
	public List<JavaMethod> findMethods(JavaClass javaClass) {
		List<JavaMethod> restMethods = new ArrayList<JavaMethod>();
		
		JavaMethod[] methods = javaClass.getMethods();
		for(JavaMethod method : methods) {
			Annotation[] annotations = method.getAnnotations();
			for(Annotation ann : annotations) {
				 if(ServiceMethod.class.getName().equals(ann.getType().getFullyQualifiedName())) {
					 restMethods.add(method);
					 break;
				 }
			}
		}
		
		return restMethods;
	}
	
	private void addJavaSource() {
		String sourcePath = null;
		if(EnvUtil.isDevelopment()) {
			sourcePath = EnvUtil.getProjectBaseDir() + separator + 
					"src" + separator + "main" + separator + "java";
			
			File sourceDir = new File(sourcePath);
			builder.addSourceTree(sourceDir);
		} else {
			try {
				sourcePath = SystemPropertyUtil.get("BASE_HOME") + separator + "source" + separator
						+ EnvUtil.getProjectName() + "-" + EnvUtil.getBuildVersion() + "-sources.jar";
				doServiceJavaSource(sourcePath);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	private void doServiceJavaSource(String jarFileUrl)
			throws IOException {
		JarFile jarFile = new JarFile(jarFileUrl);
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
			}
			
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = entries.nextElement();
				
				String entryPath = entry.getName();
				if(entryPath.endsWith(".java")) {
					InputStream inputStream = jarFile.getInputStream(entry);
					builder.addSource(new InputStreamReader(inputStream, "UTF-8"));
				}
			}
		}
		finally {
			jarFile.close();
		}
	}
}
