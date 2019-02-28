/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package superrf2.naming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @since 0.1
 */
public abstract class BaseRF2Name {

	public static final String ELEMENT_SEPARATOR = "_";
	public static final String FILE_EXT_SEPARATOR = ".";
	
	private final String fileName;
	private final String extension;
	private final List<RF2NameElement> elements;
	private final List<String> unrecognizedElements;
	private final List<Class<?>> missingElements;
	
	public BaseRF2Name(String fileName, List<Class<?>> expectedRF2NameElements) {
		if (fileName == null || fileName.isBlank()) {
			throw new IllegalArgumentException("FileName argument cannot be null or empty");
		}
		this.fileName = fileName;
		this.extension = fileName.contains(FILE_EXT_SEPARATOR) ? fileName.substring(fileName.lastIndexOf(FILE_EXT_SEPARATOR) + 1) : "";
		
		this.elements = new ArrayList<>(expectedRF2NameElements.size());
		this.unrecognizedElements = new ArrayList<>(expectedRF2NameElements.size());
		this.missingElements = new ArrayList<>(expectedRF2NameElements.size());
		
		String actualFileName = fileName.split("\\"+FILE_EXT_SEPARATOR)[0];
		
		// detect existing element
		Iterator<String> actualElements = Arrays.asList(actualFileName.split(ELEMENT_SEPARATOR)).iterator();
		Iterator<Class<?>> expectedElements = expectedRF2NameElements.iterator();
		
		// consume both iterators in order, since an RF2 file has a strict specification
		while (actualElements.hasNext() && expectedElements.hasNext()) {
			String actualElement = actualElements.next();
			Class<?> expectedElement = expectedElements.next();
			
			Matcher matcher = RF2NameElement.getNamingPattern(expectedElement).matcher(actualElement);
			if (matcher.matches()) {
				String[] args = new String[matcher.groupCount()];
				for (int i = 0; i < matcher.groupCount(); i++) {
					args[i] = Objects.toString(matcher.group(i + 1), "");
				}
				try {
					elements.add((RF2NameElement) expectedElement.getConstructors()[0].newInstance(args));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				unrecognizedElements.add(actualElement);
				missingElements.add(expectedElement);
			}
		}

		// if at this point we still have actualElements, report them as unrecognized
		while (actualElements.hasNext()) {
			unrecognizedElements.add(actualElements.next());
		}
		
		// if at this point we still have expectedElements, report them as missing
		while (expectedElements.hasNext()) {
			missingElements.add(expectedElements.next());
		}
	}

	public final String getFileName() {
		return fileName;
	}

	public final String getExtension() {
		return extension;
	}
	
	public final List<RF2NameElement> getElements() {
		return Collections.unmodifiableList(elements);
	}
	
	public final List<String> getUnrecognizedElements() {
		return Collections.unmodifiableList(unrecognizedElements);
	}
	
	public final List<Class<?>> getMissingElements() {
		return Collections.unmodifiableList(missingElements);
	}

	public final boolean hasUnrecognizedElement() {
		return !unrecognizedElements.isEmpty();
	}
	
	@Override
	public final String toString() {
		return String.format("%s%s%s", elements.stream().map(Object::toString).collect(Collectors.joining(ELEMENT_SEPARATOR)), FILE_EXT_SEPARATOR, extension);
	}
	
}
