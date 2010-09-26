package lw3d.renderer.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lw3d.Lw3dModel.RendererMode;
import lw3d.renderer.ShaderProgram;
import lw3d.renderer.ShaderProgram.Shader;
import lw3d.renderer.ShaderProgram.Shader.Type;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class ShaderManager {
	
	Map<ShaderProgram, Integer> shaderProgramHandles = new HashMap<ShaderProgram, Integer>();
	
	final private RendererMode rendererMode;
	
	public ShaderManager(RendererMode rendererMode) {
		this.rendererMode = rendererMode;
	}
	
	public int getShaderProgramHandle(ShaderProgram shaderProgram) {
		if(tryToUpload(shaderProgram))
			return shaderProgramHandles.get(shaderProgram);
		else
			return 0;
	}
	
	private boolean tryToUpload(ShaderProgram shaderProgram) {
		if(shaderProgramHandles.containsKey(shaderProgram))
			return true;
		
		int shaderProgramHandle = ARBShaderObjects.glCreateProgramObjectARB();
		
		Iterator<ShaderProgram.Shader> it = shaderProgram.getShaders().iterator();
		while(it.hasNext()) {
			Shader shader = it.next();
			if(shader.type == Type.VERTEX && rendererMode != RendererMode.SHADERS)
				continue;
			
			int shaderHandle = ARBShaderObjects.glCreateShaderObjectARB(shader.type.getValue());
			
			if(rendererMode == RendererMode.FIXED_VERTEX && shader.source_ff != null)
				ARBShaderObjects.glShaderSourceARB(shaderHandle, shader.source_ff);
			else
				ARBShaderObjects.glShaderSourceARB(shaderHandle, shader.source);
			ARBShaderObjects.glCompileShaderARB(shaderHandle);
			
			ARBShaderObjects.glAttachObjectARB(shaderProgramHandle, shaderHandle);
			
			System.out.println("logShad: " +
					ARBShaderObjects.glGetInfoLogARB(shaderHandle, 2048));
			
		}
		
		ARBShaderObjects.glLinkProgramARB(shaderProgramHandle);
		
		System.out.println("logProg: " + 
				ARBShaderObjects.glGetInfoLogARB(shaderProgramHandle, 2048));
		
		ARBShaderObjects.glValidateProgramARB(shaderProgramHandle);
		int validStatus = ARBShaderObjects.glGetObjectParameteriARB(shaderProgramHandle,
						ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB);
		System.out.println("validProgStat: " + validStatus);
				
		if(validStatus == GL11.GL_TRUE)
			shaderProgramHandles.put(shaderProgram, shaderProgramHandle);
		else {
			/*System.out.println("Source:");
			Iterator<ShaderProgram.Shader> sit = shaderProgram.getShaders().iterator();
			while(sit.hasNext())
				System.out.println(sit.next().source);
			*/
			if(shaderProgram == ShaderProgram.DEFAULT) {
				System.out.println("Default shader failed validation.");
				return false;
			}
			tryToUpload(ShaderProgram.DEFAULT);
			int defaultHandle = shaderProgramHandles.get(ShaderProgram.DEFAULT);
			shaderProgramHandles.put(shaderProgram, defaultHandle);
		}
		
		return true;
	}

}
