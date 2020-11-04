package com.peter.compile;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author lcc
 * @date 2020/10/24 20:35
 * 自定义的类加载器，用于实现类的动态加载
 */
@Slf4j
@Component
public class BuaaClassLoader extends ClassLoader {

    /**
     * 如果父的类加载器中都找不到name指定的类，
     * 就会调用这个方法去从磁盘上加载一个类
     * @return
     * @throws IOException
     */
//    @Override
//    public Class<?> findClass(String dir) throws ClassNotFoundException {
//        File classFile = new File(dir);
//        if(!classFile.exists()){
//            throw new ClassNotFoundException(classFile.getPath() + " 不存在") ;
//        }
//        try {
//            byte[] bos = FileUtils.readFileToByteArray(classFile);
//            return defineClass(bos, 0, bos.length);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    public Class<?> getClassFromFile(String path,String className) throws ClassNotFoundException {
        File classFile = new File(path);
        if(!classFile.exists()){
            throw new ClassNotFoundException(classFile.getPath() + " 不存在") ;
        }
        try {
            byte[] bos = FileUtils.readFileToByteArray(classFile);
            return defineClass(className,bos, 0, bos.length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
