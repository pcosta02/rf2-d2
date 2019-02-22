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
package superrf2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @since 0.1
 */
public class RF2ConceptFileTest {

	@Test
	public void detectConceptDeltaFile() throws Exception {
		var file = "sct2_Concept_Delta_INT_20190131.txt";
		var conceptFile = RF2Format.detect(file);
		assertEquals(file, conceptFile.getFileName());
		assertEquals(RF2FileType.SCT, conceptFile.getFileType());
		assertEquals(RF2ReleaseType.DELTA, conceptFile.getReleaseType());
	}
	
}