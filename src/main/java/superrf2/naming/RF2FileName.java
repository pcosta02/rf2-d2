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

import java.util.List;

import superrf2.naming.file.RF2ContentSubType;
import superrf2.naming.file.RF2ContentType;
import superrf2.naming.file.RF2CountryNamespace;
import superrf2.naming.file.RF2FileType;
import superrf2.naming.file.RF2VersionDate;

/**
 * @since 0.1
 */
public final class RF2FileName extends BaseRF2Name {

	public RF2FileName(String fileName) {
		super(fileName, List.of(
			RF2FileType.class,
			RF2ContentType.class,
			RF2ContentSubType.class,
			RF2CountryNamespace.class,
			RF2VersionDate.class
		));
	}
	
}
