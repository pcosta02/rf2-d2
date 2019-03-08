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
package com.b2international.rf2.naming;

import java.nio.file.Path;

import com.b2international.rf2.model.RF2ConceptFile;
import com.b2international.rf2.model.RF2DescriptionFile;
import com.b2international.rf2.model.RF2File;
import com.b2international.rf2.model.RF2RefsetFile;
import com.b2international.rf2.model.RF2RelationshipFile;
import com.b2international.rf2.model.RF2UnrecognizedFile;
import com.b2international.rf2.naming.file.RF2ContentSubType;
import com.b2international.rf2.naming.file.RF2ContentType;
import com.b2international.rf2.naming.file.RF2CountryNamespace;
import com.b2international.rf2.naming.file.RF2FileType;
import com.b2international.rf2.naming.file.RF2VersionDate;

/**
 * @since 0.1
 */
public final class RF2FileName extends RF2FileNameBase {

	public RF2FileName(String fileName) {
		super(fileName, 
			RF2FileType.class,
			RF2ContentType.class,
			RF2ContentSubType.class,
			RF2CountryNamespace.class,
			RF2VersionDate.class
		);
	}

	@Override
	public boolean isUnrecognized() {
		return getElements().isEmpty();
	}
	
	@Override
	public RF2File createRF2File(Path parent) {
		// first try to detect the actual RF2 file type by its name 
		RF2File file = createByName(parent);
		if (file.isUnrecognized()) {	
			// then by the content type aka header by reading the file
			file = createByContent(parent);
		}
		return file;
	}

	private RF2File createByName(Path parent) {
		return getElement(RF2ContentType.class)
				.map(contentType -> createTerminologyRF2File(parent, contentType))
				.orElse(new RF2UnrecognizedFile(parent, this));
	}
	
	private RF2File createTerminologyRF2File(Path parent, RF2ContentType contentType) {
		final String type = contentType.getContentType();
		switch (type) {
		case "Concept": 
			return new RF2ConceptFile(parent, this);
		case "Description":
		case "TextDefinition":
			return new RF2DescriptionFile(parent, this);
		case "Relationship":
		case "StatedRelationship":
			return new RF2RelationshipFile(parent, this);
		default: 
			return new RF2UnrecognizedFile(parent, this);
		}
	}
	
	private RF2File createByContent(Path parent) {
		// refset files are currently cannot be detected 100% by purely using the file name so we are falling back to content type aka header
		return RF2RefsetFile.detect(parent, this);
	}
	
}