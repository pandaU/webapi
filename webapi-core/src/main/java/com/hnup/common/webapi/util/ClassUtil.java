package com.hnup.common.webapi.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnup.common.lang.exception.DeclareException;
import com.hnup.common.webapi.config.PathConfig;
import com.hnup.common.webapi.model.CustomFieldVO;
import com.hnup.common.webapi.model.CustomMethodVO;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * The type Class util.
 *
 * @author XieXiongXiong
 * @date 2021 -06-15
 */
@Component
public class ClassUtil {
	private final static ObjectMapper mapper =new ObjectMapper();

	public static ClassPool pool = ClassPool.getDefault();

	private static String defaultPackage;

	public static final Map<String, CtClass> classMap = getCtClass();

	@Value("${venus.default.package:com.hnup.common.webapi}")
	public void setDefaultPackage(String defaultPackage) {
		ClassUtil.defaultPackage = defaultPackage;
	}

	/**
	 * Compiler boolean.
	 *
	 * @param javaAbsolutePath the java absolute path
	 * @return the boolean
	 * @author XieXiongXiong
	 * @date 2021 -06-15 08:42:25
	 */
	public static Boolean compiler(String javaAbsolutePath) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int run = compiler.run(null, null, null, "-encoding", "UTF-8", "-extdirs", PathConfig.EXT_JAVA_LIB, javaAbsolutePath);
		return run == 0;
	}

	public static Map<String, Object> generateSQLClass(String apiPath, String className, String methodName, String fieldType, String fieldName, String methodType) throws NotFoundException, CannotCompileException {
		if (StringUtils.isEmpty(className)) {
			className = defaultPackage + getClassName(null);
		}
		if (StringUtils.isEmpty(methodName)) {
			methodName = PathConfig.DEFAULT_CLASS_METHOD;
		}
		ClassLoader loader = pool.getClassLoader();
		try {
			//切换classLoad
			Thread.currentThread().setContextClassLoader(WebApiClassLoader.loader);
			String destName = className;
			CtClass clazz = pool.makeClass(destName);
			ClassFile ccFile = clazz.getClassFile();
			ConstPool constpool = ccFile.getConstPool();
			//参数
			CtClass requst2 = pool.get("javax.servlet.http.HttpServletRequest");
			CtClass ft = pool.get(fieldType);

			// 增加字段
			if (StringUtils.isEmpty(fieldName)) {
				fieldName = "service";
			}
			CtField field = new CtField(ft, fieldName, clazz);
			field.setModifiers(Modifier.PRIVATE);
			FieldInfo fieldInfo = field.getFieldInfo();

			// 属性附上注解
			AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation autowired = new Annotation("org.springframework.beans.factory.annotation.Autowired", constpool);
			fieldAttr.addAnnotation(autowired);
			fieldInfo.addAttribute(fieldAttr);
			clazz.addField(field);

			// 增加方法，javassist可以直接将字符串set到方法体中，所以使用时非常方便
			CtMethod method = new CtMethod(pool.get("java.lang.Object"), methodName, new CtClass[]{requst2}, clazz);
			method.setModifiers(java.lang.reflect.Modifier.PUBLIC);
			StringBuffer methodBody = new StringBuffer();
			methodBody.append("{return " + fieldName + ".doService($1);}");
			method.setBody(methodBody.toString());


			// 类附上注解
			AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation controller = new Annotation("org.springframework.web.bind.annotation.RestController", constpool);
			Annotation requestMapping = new Annotation(getMethods(methodType), constpool);
			classAttr.addAnnotation(controller);
			ccFile.addAttribute(classAttr);

			//方法附上注解
			AnnotationsAttribute methodAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			StringMemberValue value = new StringMemberValue(apiPath, constpool);
			ArrayMemberValue arrayValue = new ArrayMemberValue(constpool);
			MemberValue[] elements = new MemberValue[1];
			elements[0] = value;
			arrayValue.setValue(elements);
			requestMapping.addMemberValue("value", arrayValue);
			methodAttr.addAnnotation(requestMapping);
			MethodInfo info = method.getMethodInfo();
			info.addAttribute(methodAttr);

			// 添加参数注解
		/*ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(
			constpool, ParameterAnnotationsAttribute.visibleTag);
		Annotation paramAnnot = new Annotation("org.springframework.web.bind.annotation.RequestBody", constpool);
		Annotation[][] paramArrays = new Annotation[2][1];
		paramArrays[0][0] = paramAnnot;
		paramArrays[1][0] = new Annotation("",constpool);
		parameterAtrribute.setAnnotations(paramArrays);
		info.addAttribute(parameterAtrribute);*/
			clazz.addMethod(method);
			FileOutputStream fos = null;
			String dest = "";
			byte[] byteArr = new byte[0];
			try {
				byteArr = clazz.toBytecode();
			/*String[] split = destName.split("\\.");
			String suffix = split[split.length-1];
			dest = PathConfig.EXT_JAVA_DIR + suffix +".class";
			fos = new FileOutputStream(new File(dest));
			fos.write(byteArr);*/
			} catch (IOException e) {
				e.printStackTrace();
			}
			HashMap<String, Object> map = new HashMap<>();
			map.put("class", clazz.toClass());
			map.put("path", dest);
			map.put("bytes", byteArr);
			map.put("beanName", destName);
			return map;
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	public static Map<String, Object> generateApiClass(String apiPath, String className, String methodName, String fieldType, String fieldName, String body, String argsType) throws NotFoundException, CannotCompileException {
		if (StringUtils.isEmpty(className)) {
			className = defaultPackage + getClassName(null);
		}
		if (StringUtils.isEmpty(methodName)) {
			methodName = PathConfig.DEFAULT_CLASS_METHOD;
		}

		String destName = className + UUID.randomUUID().toString().replace("-", "");
		ClassLoader loader = pool.getClassLoader();
		try {
			//切换classLoad
			Thread.currentThread().setContextClassLoader(WebApiClassLoader.loader);
			CtClass clazz = pool.makeClass(destName);
			clazz.setSuperclass(pool.get("java.lang.Object"));
			ClassFile ccFile = clazz.getClassFile();
			ConstPool constpool = ccFile.getConstPool();
			//参数
			CtClass requst1 = StringUtils.isEmpty(argsType) ? pool.get("java.util.Map") : pool.get(argsType);
			// 增加字段
			if (!StringUtils.isEmpty(fieldType)) {
				CtClass ft = pool.get(fieldType);
				if (StringUtils.isEmpty(fieldName)) {
					fieldName = "service";
				}
				CtField field = new CtField(ft, fieldName, clazz);
				field.setModifiers(Modifier.PRIVATE);
				FieldInfo fieldInfo = field.getFieldInfo();
				// 属性附上注解
				AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
				Annotation autowired = new Annotation("org.springframework.beans.factory.annotation.Autowired", constpool);
				fieldAttr.addAnnotation(autowired);
				fieldInfo.addAttribute(fieldAttr);
				clazz.addField(field);
			}

			// 增加方法，javassist可以直接将字符串set到方法体中，所以使用时非常方便
			CtMethod method = new CtMethod(pool.get("java.lang.Object"), methodName, new CtClass[]{requst1}, clazz);
			method.setModifiers(java.lang.reflect.Modifier.PUBLIC);
			StringBuffer methodBody = new StringBuffer();
			if (StringUtils.isEmpty(body)) {
				methodBody.append("{return $1;}");
			} else {
				methodBody.append(body);
			}
			method.setBody(methodBody.toString());


			// 类附上注解
			AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			Annotation controller = new Annotation("org.springframework.web.bind.annotation.RestController", constpool);
			Annotation requestMapping = new Annotation("org.springframework.web.bind.annotation.RequestMapping", constpool);
			classAttr.addAnnotation(controller);
			ccFile.addAttribute(classAttr);

			//方法附上注解
			AnnotationsAttribute methodAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			StringMemberValue value = new StringMemberValue(apiPath, constpool);
			ArrayMemberValue arrayValue = new ArrayMemberValue(constpool);
			MemberValue[] elements = new MemberValue[1];
			elements[0] = value;
			arrayValue.setValue(elements);
			requestMapping.addMemberValue("value", arrayValue);
			methodAttr.addAnnotation(requestMapping);
			MethodInfo info = method.getMethodInfo();
			info.addAttribute(methodAttr);

			// 添加参数注解
			ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(
				constpool, ParameterAnnotationsAttribute.visibleTag);
			Annotation paramAnnot = new Annotation("org.springframework.web.bind.annotation.RequestBody", constpool);
			Annotation[][] paramArrays = new Annotation[1][1];
			paramArrays[0][0] = paramAnnot;
			parameterAtrribute.setAnnotations(paramArrays);
			info.addAttribute(parameterAtrribute);
			clazz.addMethod(method);
			String dest = "";
			byte[] byteArr = new byte[0];
			try {
				byteArr = clazz.toBytecode();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HashMap<String, Object> map = new HashMap<>();
			map.put("class", clazz.toClass());
			map.put("path", dest);
			map.put("bytes", byteArr);
			map.put("beanName", destName);
			return map;
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	public static Map<String, Object> generateJavaBean(String beanName, List<CustomFieldVO> fields, List<CustomMethodVO> extraMethods) throws IOException, CannotCompileException, NotFoundException {
		Map<String, CtClass> ctClass = classMap;
		String destName = defaultPackage + beanName;
		ClassLoader loader = pool.getClassLoader();
		try {
			//切换classLoad
			Thread.currentThread().setContextClassLoader(WebApiClassLoader.loader);
			CtClass clazz = pool.makeClass(destName);
			ClassFile ccFile = clazz.getClassFile();
			ConstPool constpool = ccFile.getConstPool();
			clazz.setSuperclass(pool.get("java.lang.Object"));
			fields.forEach((x) -> {
				String name = x.getFieldName();
				String fieldType = x.getFieldType();
				if (StringUtils.isEmpty(name) || StringUtils.isEmpty(fieldType)) {
					throw new DeclareException("属性名称以类型不能为空");
				}
				CtClass type = null;
				try {
					type = ctClass.get(fieldType) != null ? ctClass.get(fieldType) : pool.get(fieldType);
				} catch (NotFoundException e) {
					throw new DeclareException(fieldType + "类不存在");
				}
				try {
					CtField field = new CtField(type, name, clazz);
					field.setModifiers(Modifier.PRIVATE);
					FieldInfo fieldInfo = field.getFieldInfo();
					// 属性附上注解
					/*AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
					Annotation jsonProperty = new Annotation("com.fasterxml.jackson.annotation.JsonProperty", constpool);
					jsonProperty.addMemberValue("value",new StringMemberValue(name,constpool));
					fieldAttr.addAnnotation(jsonProperty);
					fieldInfo.addAttribute(fieldAttr);*/
					clazz.addField(field);
					final char[] chars = name.toCharArray();
					chars[0] = chars[0] >91 && chars[1] > 91 ? (char) (chars[0] - 32) : chars[0];
					String setMethod = "set" + new String(chars);
					// 增加get/set方法，javassist可以直接将字符串set到方法体中，所以使用时非常方便
					CtMethod set = new CtMethod(clazz, setMethod, new CtClass[]{type}, clazz);
					set.setModifiers(java.lang.reflect.Modifier.PUBLIC);
					StringBuffer setSb = new StringBuffer();
					setSb.append("{ $0." + name + " = $1; return this;}");
					set.setBody(setSb.toString());
					//get方法
					String getMethod = "get" + new String(chars);
					CtMethod get = new CtMethod(type, getMethod, new CtClass[]{}, clazz);
					get.setModifiers(java.lang.reflect.Modifier.PUBLIC);
					StringBuffer getSb = new StringBuffer();
					getSb.append("{return $0." + name + ";}");
					get.setBody(getSb.toString());
					clazz.addMethod(set);
					clazz.addMethod(get);
				} catch (CannotCompileException e) {
					throw new DeclareException("类编译失败");
				}
			});
			if (!CollectionUtils.isEmpty(extraMethods)){
				extraMethods.forEach(x -> {
					CtClass type = null;
					try {
						type = x.getReturnType() != null ? (ctClass.get(x.getReturnType()) != null ? ctClass.get(x.getReturnType()) : pool.get(x.getReturnType())) : null;
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
					List<String> list = x.getArgsType();
					CtClass[] classes = new CtClass[list.size()];
					list.forEach(y -> {
						int index = list.indexOf(y);
						CtClass args = null;
						try {
							args = ctClass.get(y) != null ? ctClass.get(y) : pool.get(y);
						} catch (NotFoundException e) {
							e.printStackTrace();
						}
						classes[index] = args;
					});
					String method = x.getMethodName();
					CtMethod addMethod = new CtMethod(type, method, classes, clazz);
					addMethod.setModifiers(java.lang.reflect.Modifier.PUBLIC);
					StringBuffer sb = new StringBuffer();
					sb.append(x.getBody());
					try {
						addMethod.setBody(sb.toString());
						clazz.addMethod(addMethod);
					} catch (CannotCompileException e) {
						throw new DeclareException("类编译失败");
					}
				});
			}
			HashMap<String, Object> map = new HashMap<>();
			map.put("class", clazz.toClass());
			map.put("bytes", clazz.toBytecode());
			map.put("beanName", destName);
			return map;
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	private static Map<String, CtClass> getCtClass() {
		String[] baseType = new String[]{"int", "byte", "short", "long", "char", "boolean", "double", "float"};
		Map<String, CtClass> map = Stream.of(baseType).collect(Collectors.toMap(k -> k, v -> {
			CtClass ct = null;
			switch (v) {
				case "int":
					ct = CtClass.intType;
					break;
				case "byte":
					ct = CtClass.byteType;
					break;
				case "short":
					ct = CtClass.shortType;
					break;
				case "long":
					ct = CtClass.longType;
					break;
				case "char":
					ct = CtClass.charType;
					break;
				case "boolean":
					ct = CtClass.booleanType;
					break;
				case "double":
					ct = CtClass.doubleType;
					break;
				case "float":
					ct = CtClass.floatType;
					break;
				default:
					throw new DeclareException(v + "：错误的参数类型");
			}
			return ct;
		}));
		return map;
	}

	private static String getMethods(String method) {
		method = StringUtils.isEmpty(method) ? "get" : method;
		String str = null;
		switch (method) {
			case "put":
				str = "org.springframework.web.bind.annotation.PutMapping";
				break;
			case "post":
				str = "org.springframework.web.bind.annotation.PostMapping";
				break;
			default:
				str = "org.springframework.web.bind.annotation.GetMapping";
		}
		return str;
	}

	public static String getClassName(String type){
		return  Optional.ofNullable(type).
			map(x->{ return x.equals(PathConfig.DEFAULT_JAVA_BEAN_TYPE) ? PathConfig.DEFAULT_DTO_CLASS_NAME  : PathConfig.DEFAULT_CLASS_NAME;})
			.orElse(PathConfig.DEFAULT_CLASS_NAME) + UUID.randomUUID().toString().replace("-","");
	}
	public static List<CustomFieldVO> jsonToArray(String json){
		return  Optional.ofNullable(json).map(x -> {
			List<CustomFieldVO> fieldVOArrayList = new ArrayList<>();
			try {
				List value = mapper.readValue(x, List.class);
				value.forEach(y->{
					Map<String,String> stringMap = (Map<String,String>)y;
					CustomFieldVO  fieldVO =  new CustomFieldVO();
					fieldVO.setColumn(stringMap.get("column"));
					fieldVO.setFieldName(stringMap.get("fieldName"));
					fieldVO.setFieldType(stringMap.get("fieldType"));
					fieldVOArrayList.add(fieldVO);
				});
				return fieldVOArrayList;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}).orElse(null);
	}
}
