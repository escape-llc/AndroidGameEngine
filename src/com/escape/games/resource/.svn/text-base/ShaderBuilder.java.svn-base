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
package com.escape.games.resource;

/**
 * Fluent builder for creating custom shaders.
 * Set up uniform color ("basic" AGE shader):
 * builder
 * 		.mvpMatrix()
 * 		.position()
 * 		.fragmentUniform(GL_Vec4, UniformColor)
 * 		.stdVertexPosition()
 * 		.fragmentColor(UniformColor)
 * Set up for per-vertex color ("cpv" AGE shader):
 * 		.mvpMatrix()
 * 		.position()
 * 		.color()
 * 		.varying(GL_Vec4, "DestinationColor")
 * 		.inVertex(assignment("DestinationColor", AttributeColor))
 * 		.stdVertexPosition()
 * 		.fragmentColor("DestinationColor")
 * Set up to texture ("tex" AGE shader):
 * 		.mvpMatrix()
 * 		.position()
 * 		.texture()
 * 		.textureUnit(UniformTexture1)
 * 		.varying(GL_Vec2, "TextureCoord")
 * 		.stdVertexPosition()
 * 		.inVertex(assignment("TextureCoord", AttributeTexture))
 * 		.fragmentColor(texture2D(UniformTexture1, "TextureCoord"))
 * Set up to mix 2 textures ("textex" AGE shader):
 * 		.mvpMatrix()
 * 		.position()
 * 		.texture()
 * 		.textureUnit(UniformTexture1)
 * 		.textureUnit(UniformTexture2)
 * 		.fragmentUniform(GL_Float, UniformRatio)
 * 		.varying(GL_Vec2, "TextureCoord")
 * 		.stdVertexPosition()
 * 		.inVertex(assignment("TextureCoord", AttributeTexture))
 * 		.fragmentColor(mix(texture2D(UniformTexture1, "TextureCoord"), texture2D(UniformTexture2, "TextureCoord"), UniformRatio))
 * @author escape-llc
 *
 */
public class ShaderBuilder {
	public static final String GL_Vec2 = "vec2";
	public static final String GL_Vec3 = "vec3";
	public static final String GL_Vec4 = "vec4";
	public static final String GL_Mat4 = "mat4";
	public static final String GL_Float = "float";
	public static final String GL_Texture = "sampler2D";
	public static final String GL_Position = "gl_Position";
	public static final String GL_FragColor = "gl_FragColor";
	public static final String UniformMVP = "uMVPMatrix";
	public static final String UniformMV = "uMVMatrix";
	public static final String UniformColor = "uColor";
	public static final String UniformRatio = "uRatio";
	public static final String UniformAlpha = "uAlpha";
	public static final String UniformTexture1 = "uTexture";
	public static final String UniformTexture2 = "uTexture2";
	public static final String UniformLightPosition = "uLightPos";
	public static final String AttributePosition = "aPosition";
	public static final String AttributeNormal = "aNormal";
	public static final String AttributeColor = "aColor";
	public static final String AttributeTexture = "aTextureCoord";
	static final String FMT_SHADER_VERTEX = "%0$s\nvoid main() {\n %1$s\n%2$s \n}";
	static final String FMT_SHADER_FRAGMENT = "precision mediump float;\n%0$s\nvoid main() {\n %1$s\n%2$s \n}";
	static final String FMT_Texture2D = "texture2D(%0$s, %1$s).rgba";
	static final String FMT_Assignment = "%0$s = %1$s;\n";
	static final String FMT_AssignmentLocal = "%0$s %1$s = %2$s;\n";
	final StringBuilder vdecl;
	final StringBuilder fdecl;
	final StringBuilder vertex;
	final StringBuilder frag;
	String fragColor;
	String vertexPosition;
	/**
	 * Assignment statement.
	 * @param name LHS
	 * @param expr RHS
	 * @return "name = expr"
	 */
	public static String assignment(String name, String expr) { return String.format(FMT_Assignment, name, expr); }
	/**
	 * Assignment statement with type (local variable).
	 * @param name LHS
	 * @param expr RHS
	 * @return "type name = e1"
	 */
	public static String assignment(String type, String name, String expr) { return String.format(FMT_AssignmentLocal, type, name, expr); }
	/**
	 * Format an expression involving GLSL mix() function.
	 * @param expr1 expression 1
	 * @param expr2 expression 2
	 * @param ratio mix ratio
	 * @return "mix(e1, e2, ratio)"
	 */
	public static String mix(String expr1, String expr2, String ratio) { return String.format("mix(%0$s, %1$s, %2$s)", expr1, expr2, ratio); }
	/**
	 * Format expression surrounded by parens.
	 * @param expr1 expression
	 * @return "(e1)".
	 */
	public static String expr(String expr1) { return String.format("(%0$s)", expr1); }
	/**
	 * Format an expression involving multiplication.
	 * Does not consider operator precedence of the two terms; supply parens if unsure!
	 * @param expr1 expression 1
	 * @param expr2 expression 2
	 * @return "(e1 * e2)".
	 */
	public static String multiply(String expr1, String expr2) { return String.format("(%0$s * %1$s)", expr1, expr2); }
	/**
	 * Format an expression involving division.
	 * Does not consider operator precedence of the two terms; supply parens if unsure!
	 * @param expr1 expression 1
	 * @param expr2 expression 2
	 * @return "(e1 / e2)".
	 */
	public static String divide(String expr1, String expr2) { return String.format("(%0$s / %1$s)", expr1, expr2); }
	/**
	 * Format an expression involving addition.
	 * Does not consider operator precedence of the two terms; supply parens if unsure!
	 * @param expr1 expression 1
	 * @param expr2 expression 2
	 * @return "(e1 + e2)".
	 */
	public static String add(String expr1, String expr2) { return String.format("(%0$s + %1$s)", expr1, expr2); }
	/**
	 * Format an expression involving subtraction.
	 * Does not consider operator precedence of the two terms; supply parens if unsure!
	 * @param expr1 expression 1
	 * @param expr2 expression 2
	 * @return "(e1 - e2)".
	 */
	public static String subtract(String expr1, String expr2) { return String.format("(%0$s - %1$s)", expr1, expr2); }
	/**
	 * Format a vec3 to vec4 conversion, supplying W component.
	 * @param expr
	 * @param ww
	 * @return "vec4(vec3, float)"
	 */
	public static String vec3ToVec4(String expr, String ww) { return String.format("vec4(%0$s, %1$s)", expr, ww); }
	/**
	 * Format in function call syntax.
	 * @param name "function" name.
	 * @param expr parameter.
	 * @return "name(e1)"
	 */
	public static String call(String name, String expr) { return String.format("%0$s(%1$s)", name, expr); }
	/**
	 * Format in function call syntax.
	 * @param name "function" name.
	 * @param expr parameter list.
	 * @return "name(e1 ... eN)"
	 */
	public static String call(String name, String... expr) {
		final StringBuilder sb = new StringBuilder(name);
		sb.append("(");
		for(int ix = 0; ix < expr.length; ix++) {
			if(ix > 0) sb.append(", ");
			sb.append(expr[ix]);
		}
		sb.append(")");
		return sb.toString();
	}
	/**
	 * Format an expression involving GLSL texture2D() function.
	 * @param tex texture uniform
	 * @param varying texture coordinate (varying)
	 * @return "texture2D(tex, varying).rgba"
	 */
	public static String texture2D(String tex, String varying) { return String.format(FMT_Texture2D, tex, varying); }
	/**
	 * Ctor.
	 */
	public ShaderBuilder() {
		vdecl = new StringBuilder();
		fdecl = new StringBuilder();
		vertex = new StringBuilder();
		frag = new StringBuilder();
	}
	/**
	 * Simple substring check for string.
	 * @param sb Contents to check.
	 * @param name name to check for.
	 * @return true: found; false: not found.
	 */
	boolean checkDecl(StringBuilder sb, String name) { return (sb.indexOf(name) >= 0); }
	/**
	 * Varying decl.
	 * Adds to both shader decls.
	 * @param decl declaration statement; use static varying()
	 * @param vertex vertex statement(s); use static assignment()
	 * @return self.
	 */
	public ShaderBuilder varying(String type, String name) {
		final String decl = String.format("varying %0$s %1$s;\n", type, name);
		if(!checkDecl(vdecl, name)) vdecl.append(decl);
		if(!checkDecl(fdecl, name)) fdecl.append(decl);
		return this;
	}
	/**
	 * Set GLSL fragment position statement based on standard naming conventions.
	 * Checks for dependent decls and adds if missing.
	 * @return self.
	 */
	public ShaderBuilder stdVertexPosition() {
		if(!checkDecl(vdecl, UniformMVP)) mvpMatrix();
		if(!checkDecl(vdecl, AttributePosition)) position();
		vertexPosition("uMVPMatrix * vec4(aPosition, 1)");
		return this;
	}
	/**
	 * Assign GLSL vertex position statement in vertex shader.
	 * Overwrites previous assignment to vertex position.
	 * @param expr VEC4 expression.
	 * @return self.
	 */
	public ShaderBuilder vertexPosition(String expr) { vertexPosition = String.format(FMT_Assignment, GL_Position, expr); return this; }
	/**
	 * Assign GLSL fragment color statement in fragment shader.
	 * Overwrites previous assignment to fragment color.
	 * @param expr RGBA pixel expression.
	 * @return self.
	 */
	public ShaderBuilder fragmentColor(String expr) { fragColor = String.format(FMT_Assignment, GL_FragColor, expr); return this; }
	/**
	 * Add standard decl for vertex uniform MVP(mat4)
	 * @return self.
	 */
	public ShaderBuilder mvpMatrix() { vertexUniform(GL_Mat4, UniformMVP); return this; }
	/**
	 * Add standard decl for vertex uniform MV(mat4)
	 * @return self.
	 */
	public ShaderBuilder mvMatrix() { vertexUniform(GL_Mat4, UniformMV); return this; }
	/**
	 * Add standard decl for vertex attribute position(vec3)
	 * @return self.
	 */
	public ShaderBuilder position() { vertexAttribute(GL_Vec3, AttributePosition); return this; }
	/**
	 * Add standard decl for vertex attribute normal(vec3)
	 * @return self.
	 */
	public ShaderBuilder normal() { vertexAttribute(GL_Vec3, AttributeNormal); return this; }
	/**
	 * Add standard decl for vertex attribute color(vec4)
	 * @return self.
	 */
	public ShaderBuilder color() { vertexAttribute(GL_Vec4, AttributeColor); return this; }
	/**
	 * Add standard decl for vertex attribute texture(vec2)
	 * @return self.
	 */
	public ShaderBuilder texture() { vertexAttribute(GL_Vec2, AttributeTexture); return this; }
	/**
	 * Add standard fragment decl for a texture unit (sampler2D).
	 * @param name
	 * @return self.
	 */
	public ShaderBuilder textureUnit(String name) { fragmentUniform(GL_Texture, name); return this; }
	/**
	 * Add statement text to vertex shader.
	 * @param stmt
	 * @return self.
	 */
	public ShaderBuilder inVertex(String stmt) { this.vertex.append(stmt); return this; }
	/**
	 * Add multiple statement text to vertex shader.
	 * @param list of stmts, treated as "lines" of code.
	 * @return self.
	 */
	public ShaderBuilder inVertex(String... stmts) {
		for(int ix = 0; ix < stmts.length; ix++) {
			this.vertex.append(stmts[ix]);
		}
		return this;
	}
	/**
	 * Add statement text to fragment shader.
	 * @param stmt
	 * @return
	 */
	public ShaderBuilder inFragment(String stmt) { this.frag.append(stmt); return this; }
	/**
	 * Add multiple statement text to fragment shader.
	 * @param list of stmts, treated as "lines" of code.
	 * @return self.
	 */
	public ShaderBuilder inFragment(String... stmts) {
		for(int ix = 0; ix < stmts.length; ix++) {
			this.frag.append(stmts);
		}
		return this;
	}
	/**
	 * Add a uniform to fragment shader.
	 * Checks for existing name.
	 * @param type
	 * @param name
	 * @return self.
	 * @throws IllegalArgumentException name already defined
	 */
	public ShaderBuilder fragmentUniform(String type, String name) {
		if(checkDecl(fdecl, name)) throw new IllegalArgumentException(name + ": already defined");
		fdecl.append(String.format("uniform %0$s %1$s;\n", type, name));
		return this;
	}
	/**
	 * Add an attribute to fragment shader.
	 * Checks for existing name.
	 * @param type
	 * @param name
	 * @return self.
	 * @throws IllegalArgumentException name already defined
	 */
	public ShaderBuilder fragmentAttribute(String type, String name) {
		if(checkDecl(fdecl, name)) throw new IllegalArgumentException(name + ": already defined");
		fdecl.append(String.format("attribute %0$s %1$s;\n", type, name));
		return this;
	}
	/**
	 * Add a uniform to vertex shader.
	 * Checks for existing name.
	 * @param type
	 * @param name
	 * @return self.
	 * @throws IllegalArgumentException name already defined
	 */
	public ShaderBuilder vertexUniform(String type, String name) {
		if(checkDecl(vdecl, name)) throw new IllegalArgumentException(name + ": already defined");
		vdecl.append(String.format("uniform %0$s %1$s;\n", type, name));
		return this;
	}
	/**
	 * Add an attribute to vertex shader.
	 * Checks for existing name.
	 * @param type
	 * @param name
	 * @return self.
	 * @throws IllegalArgumentException name already defined
	 */
	public ShaderBuilder vertexAttribute(String type, String name) {
		if(checkDecl(vdecl, name)) throw new IllegalArgumentException(name + ": already defined");
		vdecl.append(String.format("attribute %0$s %1$s;\n", type, name));
		return this;
	}
	/**
	 * Compose the GLSL source code text for a fragment shader based on current settings.
	 * @return GLSL text.
	 * @throws IllegalArgumentException fragmentColor() was not called
	 */
	public String buildFragmentShader() {
		if(fragColor == null) throw new IllegalArgumentException("fragmentColor() was not called");
		return String.format(FMT_SHADER_FRAGMENT, fdecl.toString(), frag.toString(), fragColor);
	}
	/**
	 * Compose the GLSL source code text for a vertex shader based on current settings.
	 * Automatically adds stdVertexPosition() if nothing set the vertex position.
	 * @return GLSL text.
	 */
	public String buildVertexShader() {
		if(vertexPosition == null) stdVertexPosition();
		return String.format(FMT_SHADER_VERTEX, vdecl.toString(), vertex.toString(), vertexPosition);
	}
}
