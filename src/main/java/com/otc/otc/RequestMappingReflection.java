package com.otc.otc;

import com.otc.otc.annotations.Controller;
import com.otc.otc.annotations.MethodMapping;
import com.otc.otc.annotations.RequestMapping;
import com.otc.otc.dto.MethodEnum;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * 간단한 ReuqestMapping 코드
 * 내부적으로 이렇게 돌아가겠구나 하는 이해를 위해 파라미터 형변환 및 테스트하는 용도로 구현해봄
 */
@Log
public class RequestMappingReflection {
    private static final Logger log = Logger.getLogger(TcpWasServer.class.getName());

    public Method getRoutedMethod(String requestUri, MethodEnum methodEnum) throws NoSuchMethodException {
        // 패키지 이름
        //TODO 패키지 이름을 외부 파일에서 가져온다
        String packageName = "com.otc.otc";

        // 패키지 내 모든 클래스를 가져와서 검사합니다.
        try {
            // 패키지 내 클래스들을 가져옵니다.
            List<Class<?>> classes = getClasses(packageName);

            // 클래스마다 어노테이션을 검사합니다.
            for (Class<?> clazz : classes) {
                //Controller 검사
                if (!clazz.isAnnotationPresent(Controller.class)) continue;

                // Controller 어노테이션이 있는 클래스일 경우, RequestMapping 어노테이션을 가져옵니다.
                Annotation[] annotations = clazz.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (!(annotation instanceof RequestMapping)) continue;
                    RequestMapping requestMapping = (RequestMapping) annotation;

                    String mappingUri = requestMapping.url();
                    StringBuilder stringBuilder = new StringBuilder(mappingUri);
                    String onlyUri = requestUri.indexOf("?") != -1 ?
                            requestUri.substring(0,requestUri.indexOf("?")) : requestUri;

                    //uri와 일치하는 메서드 있으면 가져옴
                    Method routeredMethod = getRouteredMethod(onlyUri, stringBuilder, clazz, methodEnum);
                    if (routeredMethod != null) {
                        return routeredMethod;
                    } else {
                        continue;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new NoSuchMethodException("일치하는 메서드가 없습니다.");
    }



    private Method getRouteredMethod(String requestUri,StringBuilder mappingUriBuilder,Class<?> clazz,MethodEnum methodEnum) {
        log.info(requestUri + "," + mappingUriBuilder.toString());

        if(requestUri.contains(mappingUriBuilder.toString())){
            //일치하는 어노테이션이면 uri 및 메서드 일치 확인
            //일치하면 method를 리턴
            for(Method method : clazz.getDeclaredMethods()){
                MethodMapping methodMapping = method.getAnnotation(MethodMapping.class);
                StringBuilder fullMappingUriBuilder = new StringBuilder(mappingUriBuilder.toString()).append(methodMapping.url());

                if(fullMappingUriBuilder.toString().equals(requestUri) && methodEnum == methodMapping.method()){
                    return method;
                }
            }
        }
        //일치하는 메서드 없음
        return null;

    }

    // 패키지 및 하위 패키지 내의 모든 클래스를 가져오는 메서드
    private static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            // 패키지의 경로를 변환합니다.
            String path = packageName.replace('.', '/');
            // 클래스 로더를 가져옵니다.
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 패키지의 모든 리소스를 가져옵니다.
            Enumeration<URL> resources = classLoader.getResources(path);
            // 모든 리소스를 탐색하면서 클래스 파일을 로드합니다.
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                classes.addAll(findClasses(resource, packageName));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    // 디렉토리 내의 클래스 파일을 탐색하는 메서드
    private static List<Class<?>> findClasses(URL directory, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File dir = new File(directory.getFile());
        if (dir.exists()) {
            // 디렉토리에 있는 파일들을 가져옵니다.
            File[] files = dir.listFiles();
            // 파일들을 순회하면서 클래스로 변환합니다.
            for (File file : files) {
                if (file.isDirectory()) {
                    // 하위 디렉토리의 클래스 파일도 로드합니다.
                    classes.addAll(findClasses(file.toURI().toURL(), packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    // 클래스 이름을 가져옵니다.
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    // 클래스를 로드하고 리스트에 추가합니다.
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }
}








