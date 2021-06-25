package com.hnup.common.webapi.config;

import java.io.File;

/**
 * <p>
 * The type Path config.
 *
 * @author XieXiongXiong
 * @date 2021 -06-15
 */
public class PathConfig {
    /**
     * EXT_JAVA_DIR
     */
    public final static String EXT_JAVA_DIR= System.getProperty("os.name").toLowerCase().contains("win") ?  "E:\\jitClass\\" : "/usr/local/src/jitClass/";

    /**
     * EXT_JAVA_LIB
     */
    public final static String EXT_JAVA_LIB=EXT_JAVA_DIR +"lib"+ File.separator;

    /**
     * PACKAGE
     */
    public final static String PACKAGE="package";

    /**
     * JAVA_SUFFIX
     */
    public final static String JAVA_SUFFIX=".java";

	public final static String DEFAULT_JAVA_BEAN_TYPE="DTO";

	public final static String DEFAULT_CLASS_NAME=".ExtClass";

	public final static String DEFAULT_DTO_CLASS_NAME=".DTO";

	public final static String DEFAULT_CLASS_METHOD = "action";

	public final static String INSERT = "insert";

	public final static String UPDATE = "update";

	public final static String SELECT = "select";

	public final static String URL_PREFIX = "/webapi";


}
