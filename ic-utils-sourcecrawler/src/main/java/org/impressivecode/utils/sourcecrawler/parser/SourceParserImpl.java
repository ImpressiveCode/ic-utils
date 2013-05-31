/*
ImpressiveCode Depress Framework Source Crawler
Copyright (C) 2013 ImpressiveCode contributors

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.impressivecode.utils.sourcecrawler.parser;

import java.util.List;

import org.impressivecode.utils.sourcecrawler.model.ClazzType;
import org.impressivecode.utils.sourcecrawler.model.JavaClazz;
import org.impressivecode.utils.sourcecrawler.model.JavaFile;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import static com.google.common.collect.Lists.newArrayList;

/**
 * 
 * @author Paweł Nosal
 * 
 */

public class SourceParserImpl implements SourceParser {
	private String throwableClassName;

	public SourceParserImpl() {
		Throwable tmpThrowable = new Throwable();
		Class<? extends Throwable> throwableClass = tmpThrowable.getClass();
		throwableClassName = throwableClass.getName();
	}

	@Override
	public JavaFile parseSource(JavaSource sourceToParse) {
		JavaFile javaFile = new JavaFile();
		setupPackage(sourceToParse, javaFile);
		setupPath(sourceToParse, javaFile);
		List<JavaClazz> parsedClasses = parseClasses(sourceToParse);
		javaFile.setClasses(parsedClasses);
		return javaFile;
	}

	private List<JavaClazz> parseClasses(JavaSource sourceToParse) {
		JavaClass[] javaClasses = sourceToParse.getClasses();
		List<JavaClazz> parsedClasses = newArrayList();
		for (JavaClass javaClass : javaClasses) {
			JavaClazz analyzedClass = analyzeClass(javaClass);
			parsedClasses.add(analyzedClass);
		}
		return parsedClasses;
	}

	private void setupPath(JavaSource sourceToParse, JavaFile javaFile) {
		if (sourceToParse.getURL() != null) {
			String path = sourceToParse.getURL().getPath();
			javaFile.setFilePath(path);
		}
	}

	private void setupPackage(JavaSource sourceToParse, JavaFile javaFile) {
		String packageName = sourceToParse.getPackage();
		javaFile.setPackageName(packageName);
	}

	@Override
	public JavaClazz analyzeClass(JavaClass javaClass) {
		JavaClazz javaClazz = checkClassType(javaClass);
		boolean isException = checkIsThrowable(javaClass);
		javaClazz.setException(isException);
		javaClazz.setClassName(javaClass.getName());
		javaClazz.setInner(javaClass.isInner());
		return javaClazz;
	}

	private JavaClazz checkClassType(JavaClass javaClass) {
		JavaClazz javaClazz = new JavaClazz();
		if (javaClass.isEnum()) {
			javaClazz.setClassType(ClazzType.ENUM);
		} else if (javaClass.isInterface()) {
			javaClazz.setClassType(ClazzType.INTERFACE);
		} else if (javaClass.isAbstract()) {
			javaClazz.setClassType(ClazzType.ABSTRACT);
		} else {
			javaClazz.setClassType(ClazzType.CLASS);
		}
		return javaClazz;
	}

	@Override
	public boolean checkIsThrowable(JavaClass javaClass) {
		boolean isThrowable = javaClass.isA(throwableClassName);
		return isThrowable;
	}

}