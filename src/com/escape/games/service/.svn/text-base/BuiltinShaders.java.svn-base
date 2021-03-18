/*
 * Copyright 2014 eScape Technology LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.escape.games.service;

public class BuiltinShaders {
	private static final String COMMON_FragmentDestinationColor =
		    "precision mediump float;\n" +
			"varying vec4 DestinationColor;\n"+
			"void main() {\n"+
			"  gl_FragColor = DestinationColor;\n" +
			"}";
	private static final String COMMON_VertexPosition = "  gl_Position = uMVPMatrix * vec4(aPosition, 1);\n";
	private static final String COMMON_UniformMVP = "uniform mat4 uMVPMatrix;\n";
	private static final String COMMON_UniformMV = "uniform mat4 uMVMatrix;\n";
	private static final String COMMON_UniformColor = "uniform vec4 uColor;\n";
	private static final String COMMON_UniformRatio = "uniform float uRatio;\n";
	private static final String COMMON_UniformTexture1 = "uniform sampler2D uTexture;\n";
	private static final String COMMON_UniformTexture2 = "uniform sampler2D uTexture2;\n";
	private static final String COMMON_AttributePosition = "attribute vec3 aPosition;\n";
	private static final String COMMON_AttributeNormal = "attribute vec3 aNormal;\n";
	private static final String COMMON_AttributeColor = "attribute vec4 aColor;\n";
	private static final String COMMON_AttributeTexture = "attribute vec2 aTextureCoord;\n";
	private static final String COMMON_FragmentColor = "aColor";
	private static final String COMMON_FragmentTexture1 = "texture2D(uTexture, TextureCoord).rgba";
	private static final String COMMON_FragmentTexture2 = "texture2D(uTexture2, TextureCoord).rgba";
	static final String solidVertexShader =
		COMMON_UniformMVP +
		COMMON_AttributePosition +
	    "void main() {\n" +
	    COMMON_VertexPosition +
	    "}";
	static final String solidFragmentShader =
	    "precision mediump float;\n" +
	    COMMON_UniformColor +
	    "void main() {\n" +
	    "  gl_FragColor = uColor;\n" +
	    "}";
	static final String cpvVertexShader =
		COMMON_UniformMVP +
		COMMON_AttributePosition +
		COMMON_AttributeColor +
		"varying vec4 DestinationColor;\n" +
		"void main() {\n" +
		"  DestinationColor = " + COMMON_FragmentColor + ";\n" +
		COMMON_VertexPosition +
		"}";
	static final String cpvFragmentShader = COMMON_FragmentDestinationColor;
    static final String texVertexShader =
    	COMMON_UniformMVP +
		COMMON_AttributePosition +
    	COMMON_AttributeTexture +
    	"varying vec2 TextureCoord;\n" +
    	"void main() {\n" +
    	COMMON_VertexPosition +
    	"  TextureCoord = aTextureCoord;\n" +
    	"}";
	static final String texFragmentShader =
		"precision mediump float;\n" +
		COMMON_UniformTexture1 +
		"varying vec2 TextureCoord;\n" +
		"void main() {\n" +
		"  gl_FragColor = " + COMMON_FragmentTexture1 + ";\n" +
		"}";
	static final String textexFragmentShader =
		"precision mediump float;\n" +
		COMMON_UniformRatio +
		COMMON_UniformTexture1 +
		COMMON_UniformTexture2 +
		"varying vec2 TextureCoord;\n" +
		"void main() {\n" +
		"  gl_FragColor = mix(" + COMMON_FragmentTexture1 + ", " + COMMON_FragmentTexture2 + ", uRatio);\n" +
		"}";
	static final String lpvVertexShader =
		COMMON_UniformMVP +
		COMMON_UniformMV +
		"uniform vec3 uLightPos;\n" +
		COMMON_AttributePosition +
		COMMON_AttributeColor +
		COMMON_AttributeNormal +
		"varying vec4 DestinationColor;\n" +
		"void main()\n" +
		"{\n" +
		"  vec3 modelViewVertex = vec3(uMVMatrix * vec4(aPosition, 1));\n" +
		"  vec3 modelViewNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));\n" +
		"  float distance = length(uLightPos - modelViewVertex);\n" +
		"  vec3 lightVector = normalize(uLightPos - modelViewVertex);\n" +
		"  float diffuse = max(dot(modelViewNormal, lightVector), 0.1);\n" +
		"  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));\n" +
		"  DestinationColor = aColor * diffuse;\n" +
		COMMON_VertexPosition +
		"}";
	static final String lpvFragmentShader = COMMON_FragmentDestinationColor;
	static final String lpfVertexShader =
		COMMON_UniformMVP +
		COMMON_UniformMV +
		COMMON_AttributePosition +
		COMMON_AttributeColor +
		COMMON_AttributeNormal +
		"varying vec3 Position;\n" +
		"varying vec4 DestinationColor;\n" +
		"varying vec3 Normal;\n" +
		"void main()\n" +
		"{\n" +
		"  Position = vec3(uMVMatrix * vec4(aPosition, 1));\n" +
		"  DestinationColor = aColor;\n" +
		"  Normal = vec3(uMVMatrix * vec4(aNormal, 0.0));\n" +
		COMMON_VertexPosition +
		"}";
	static final String lpfFragmentShader =
		"precision mediump float;\n" +
		"uniform vec3 uLightPos;\n" +
		"varying vec3 Position;\n" +
		"varying vec4 DestinationColor;\n" +
		"varying vec3 Normal;\n" +
		"void main()\n" +
		"{\n" +
		"  float distance = length(uLightPos - Position);\n" +
		"  vec3 lightVector = normalize(uLightPos - Position);\n" +
		"  float diffuse = max(dot(Normal, lightVector), 0.1);\n" +
		"  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));\n" +
		"  gl_FragColor = DestinationColor * diffuse;\n" +
		"}";
}
