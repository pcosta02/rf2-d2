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
package com.b2international.rf2.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.b2international.rf2.RF2CreateContext;
import com.b2international.rf2.RF2TransformContext;
import com.b2international.rf2.naming.RF2FileName;
import com.b2international.rf2.spec.RF2Specification;

/**
 * @since 0.1
 */
public final class RF2Directory extends RF2File {

	public static final String ROOT_PATH = "/";
	private final RF2Specification specification;

	public RF2Directory(Path parent, RF2FileName fileName, RF2Specification specification) {
		super(parent, fileName);
		this.specification = specification;
	}

	@Override
	public void visit(Consumer<RF2File> visitor) throws IOException {
		visitor.accept(this);
		Files.walk(getPath(), 1).forEach(path -> {
			if (!path.equals(getPath())) {
				try {
					specification.detect(path).visit(visitor);
				} catch (IOException e) {
					throw new RuntimeException("Couldn't visit path: " + path, e);
				}
			}
		});
	}

	@Override
	public String getType() {
		return "Directory";
	}
	
	@Override
	public void create(RF2CreateContext context) throws IOException {
		Files.createDirectories(getPath());
	}

	@Override
	public void transform(RF2TransformContext context) throws IOException {
		// there's nothing to transform so we'll just create the directory to its new location
		final RF2File newRF2Directory = getRF2FileName().createRF2File(context.getParent(), context.getSpecification());
		
		context.task("Creating directory '%s'", getPath()).run(() -> {
			Files.createDirectories(newRF2Directory.getPath());
		});
		
		Files.walk(getPath(), 1).forEach(path -> {
			if (!path.equals(getPath())) {
				try {
					specification.detect(path).transform(context.newSubContext(newRF2Directory.getPath()));
				} catch (IOException e) {
					throw new RuntimeException("Couldn't transform path: " + path, e);
				}
			}
		});
	}

}
