package com.peter.compile;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import java.io.File;

/**
 * @author lcc
 * @date 2020/10/24 21:00
 */
@Slf4j
@Component
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BuaaCompiler {
    private JavaCompiler javaCompiler;
    private BuaaClassLoader classLoader;


    public boolean isClassFileExist(String cpath){
        File classFile = new File(cpath);
        return classFile.exists();
    }

    public boolean javac(String srcPath, String destDirPath) {
        return javac(srcPath,destDirPath,false);
    }
    public boolean javac(String srcPath, String destDirPath,boolean overwrite) {
        String cpath = StringUtils.replace(srcPath,".java", ".class");
        if (!overwrite&&isClassFileExist(cpath)){
            log.info(cpath+ " is exist! compile skipped!");
            return true;
        }
        File src = new File(srcPath);
        //JavaCompiler最核心的方法是run, 通过这个方法编译java源文件, 前3个参数传null时,
        //分别使用标准输入/输出/错误流来 处理输入和编译输出. 使用编译参数-d指定字节码输出目录.
//            int compileResult = javac.run(null, null, null, "-d", distDir.getAbsolutePath(), javaFile
//            .getAbsolutePath());
        int compileResult = javaCompiler.run(null, null, null,
                "-d", destDirPath, src.getAbsolutePath());
        //run方法的返回值: 0-表示编译成功, 否则表示编译失败
        if (compileResult != 0) {
            log.error(srcPath + "编译失败!!");
            return false;
        } else {
            log.info(srcPath + "编译成功!!");
            return true;
        }
    }

    public Class<?> loadClass(String fileName, String className) {
        Class<?> tempClass = null;
        try {
            tempClass = classLoader.getClassFromFile(fileName, className);
        } catch (ClassNotFoundException e) {
            log.error("failed to get class from " + fileName);
            e.printStackTrace();
        }
        return tempClass;
    }
}
