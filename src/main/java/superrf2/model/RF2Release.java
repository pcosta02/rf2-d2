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

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

import superrf2.naming.RF2ReleaseName;

/**
 * @since 0.1 
 */
public final class RF2Release extends RF2File {

	public RF2Release(Path parent, RF2ReleaseName fileName) {
		super(parent, fileName);
	}

	@Override
	public void visit(Consumer<RF2File> visitor) throws IOException {
		visitor.accept(this);
		
		try (FileSystem zipfs = FileSystems.newFileSystem(URI.create("jar:" + getPath().toUri()), Map.of("create", !Files.exists(getPath())))) {
			for (Path root : zipfs.getRootDirectories()) {
				Files.walk(root, 1).forEach(path -> {
					if (!RF2Directory.ROOT_PATH.equals(path.toString())) {
						try {
							RF2File.detect(path).visit(visitor);
						} catch (IOException e) {
							throw new RuntimeException("Couldn't visit path: " + path, e);
						}
					}
				});
			}
		}
	}
	
	@Override
	public String getType() {
		return "Release";
	}
	
}
